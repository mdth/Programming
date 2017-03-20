package iqcache.evaluation;

import iqcache.common.logging.LogOutputManager;
import iqcache.common.sql.DatabaseConnector;
import iqcache.common.sql.SQLScript;
import iqcache.common.test.TestHelper;
import iqcache.expression.Expression;
import iqcache.query.Query;
import iqcache.query.meta.InterpreterException;
import iqcache.query.meta.MetaDataException;
import iqcache.query.meta.MetaDataWithCache;
import iqcache.query.parser.ParseException;
import iqcache.query.parser.QueryParser;
import iqcache.satisfiability.SatisfiabilityException;
import iqcache.satisfiability.yices.YicesSolver;
import iqcache.solver.Solver;
import iqcache.solver.mathsat.MathSatResettingSolver;
import iqcache.solver.z3.Z3ResettingSolver;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Level;

/**
 * This class generates benchmark test results for the SMT solver MathSAT 5,
 * Yices or Z3.
 * 
 * @author dinh
 */
public class BenchmarkEvaluationEnvironmentN {

	// changeable information
	private final static int NUMBER_OF_EXPRESSIONS = 300000;
	private static BigInteger time = BigInteger.ZERO;
	private static BigInteger totalTime = BigInteger.ZERO;

	private static String solverName;

	/**
	 * 
	 * @param file
	 * @param test
	 * @param solver
	 * @param benchmark
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws MetaDataException
	 * @throws ParseException
	 * @throws InterpreterException
	 * @throws SatisfiabilityException
	 * @throws IOException
	 */
	public static void test(String file, String test, YicesSolver solver,
			String benchmark) throws ClassNotFoundException, SQLException,
			MetaDataException, ParseException, InterpreterException,
			SatisfiabilityException, IOException {

		solverName = EvaluationParameters.YICES;
		ArrayList<Expression> expr = createBenchmarkExpressionList(file);
		for (Expression e : expr) {
			// starting time
			long start = System.nanoTime();

			solver.isSatisfiable(e);

			// end time
			long end = System.nanoTime();

			time = (BigInteger.valueOf(end - start));
			totalTime = totalTime.add(time);
		}
		EvaluationHelper.createTXTFile(solverName, benchmark, test, totalTime,
				NUMBER_OF_EXPRESSIONS);
	}

	/**
	 * 
	 * @param file
	 * @param test
	 * @param solver
	 * @param benchmark
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws MetaDataException
	 * @throws ParseException
	 * @throws InterpreterException
	 * @throws SatisfiabilityException
	 * @throws IOException
	 */
	public static void test(String file, String test, Solver solver,
			String benchmark) throws ClassNotFoundException, SQLException,
			MetaDataException, ParseException, InterpreterException,
			SatisfiabilityException, IOException {

		if (solver instanceof Z3ResettingSolver) {
			solverName = EvaluationParameters.Z3;
		} else if (solver instanceof MathSatResettingSolver) {
			solverName = EvaluationParameters.MATHSAT;
		}

		ArrayList<Expression> expr = createBenchmarkExpressionList(file);
		for (int i = 0; i < NUMBER_OF_EXPRESSIONS; i++) {
			// starting time
			long start = System.nanoTime();

			solver.isSatisfiable(expr.get(i));

			// end time
			long end = System.nanoTime();

			time = (BigInteger.valueOf(end - start));
			totalTime = totalTime.add(time);
		}

		EvaluationHelper.createTXTFile(solverName, benchmark, test, totalTime,
				NUMBER_OF_EXPRESSIONS);
	}

	/**
	 * 
	 * @param file
	 * @return
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws MetaDataException
	 * @throws ParseException
	 * @throws InterpreterException
	 */
	protected static ArrayList<Expression> createBenchmarkExpressionList(
			String file) throws SQLException, ClassNotFoundException,
			MetaDataException, ParseException, InterpreterException {
		LogOutputManager.addSink(System.out, Level.ERROR);
		DatabaseConnector dbc = TestHelper.dbcPostgreSQL();

		dbc.connect();

		QueryParser parser = new QueryParser(dbc, new MetaDataWithCache(dbc));
		SQLScript script = new SQLScript(file);

		ArrayList<Expression> expr = new ArrayList<Expression>();
		int num = 0;

		for (Iterator<String> i = script.iterator(); script.iterator()
				.hasNext();) {
			if (num < NUMBER_OF_EXPRESSIONS) {
				String stmt = i.next();
				Query q = parser.parseQuery(stmt);

				// save expressions into an array
				expr.add(q.getWhere());
				num++;

			} else {
				dbc.disconnect();
				return expr;
			}
		}

		dbc.disconnect();
		return expr;
	}

}