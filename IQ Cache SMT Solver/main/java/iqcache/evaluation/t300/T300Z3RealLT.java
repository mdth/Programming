package iqcache.evaluation.t300;

import iqcache.evaluation.EvaluationHelper;
import iqcache.evaluation.EvaluationParameters;
import iqcache.expression.Expression;
import iqcache.expression.leaf.typedouble.LessDoubleComparison;
import iqcache.query.Identifier;
import iqcache.query.column.Column;
import iqcache.query.column.DoubleType;
import iqcache.satisfiability.SatisfiabilityException;
import iqcache.solver.Solver;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * This class executes the test case T300RealLT with the Z3 solver.
 * 
 * 1. 0 < X0 AND X0 < 1 
 * 2. 0 < X0 AND X0 < X1 AND X1 < 2 
 * 3. 0 < X0 AND X0 < X1 AND X1 < X2 AND X2 < 3 
 * 4. 0 < X0 AND X0 < X1 AND X1 < X2 AND X2 < X3 AND X3 < 4 
 * 5. .....
 * 
 * @author dinh
 * 
 */
public class T300Z3RealLT {

	private static final String TESTCASE = EvaluationParameters.TEST300;
	private static final String TESTCASE_EXT = EvaluationParameters.REAL
			+ EvaluationParameters.LT;
	private static final String SOLVER = EvaluationParameters.Z3;

	private static BigDecimal time = BigDecimal.ZERO;
	private static StringBuilder rawCSVData = new StringBuilder();
	private static StringBuilder csvData = new StringBuilder();

	/**
	 * This method executes a test case T300REALLT
	 * 
	 * @param solver
	 *            Z3 solver
	 * @param repeats
	 *            the number of repeats of one expression
	 * @throws IOException
	 *             if I/O error occurs
	 * @throws SatisfiabilityException
	 *             if the solver cannot validate the given expression
	 */
	public static void test(Solver solver, int repeats) throws IOException,
			SatisfiabilityException {

		Expression expr[] = createExpressions();

		rawCSVData.append(EvaluationParameters.LEVEL_DEF + ";"
				+ EvaluationParameters.TIME);
		rawCSVData.append("\n");

		csvData.append(EvaluationParameters.LEVEL_DEF + ";"
				+ EvaluationParameters.TIME);
		csvData.append("\n");

		for (int i = 1; i <= EvaluationHelper.LEVEL; i++) {
			BigDecimal tempData = BigDecimal.ZERO;

			for (int j = 1; j <= repeats; j++) {

				// starting time
				long start = System.nanoTime();

				solver.isSatisfiable(expr[i - 1]);

				// end time
				long end = System.nanoTime();

				time = BigDecimal.valueOf(end - start);

				rawCSVData.append(i);
				rawCSVData.append(";" + time);
				rawCSVData.append("\n");

				tempData = tempData.add(time);
			}

			tempData = tempData.divide(BigDecimal.valueOf(repeats));
			tempData = tempData.divide(BigDecimal
					.valueOf(EvaluationHelper.MS_TRANSFORMATION));
			csvData.append(i + ";" + tempData);
			csvData.append("\n");
		}

		EvaluationHelper.createRawCSVFile(rawCSVData, TESTCASE, TESTCASE_EXT,
				SOLVER);
		EvaluationHelper.createCSVFile(csvData, TESTCASE, TESTCASE_EXT, SOLVER);
		EvaluationHelper.createTXTFile(TESTCASE, TESTCASE_EXT, SOLVER, repeats);
	}

	/**
	 * Creates partial {@link Expression}s for this test case.
	 * 
	 * @return Array of partial {@link Expression}s.
	 */
	private static Expression[] createExpressions() {

		// create columns
		Column var[] = new Column[EvaluationHelper.LEVEL];
		final String X = "X";

		for (int i = 0; i < EvaluationHelper.LEVEL; i++) {
			String id = X + i;
			var[i] = new Column(new Identifier("t"), new Identifier(id), 1,
					DoubleType.instance(), false, false);
		}

		// create partial expressions
		Expression[] exp = new Expression[EvaluationHelper.LEVEL];

		for (int i = 0; i < EvaluationHelper.LEVEL; i++) {
			ArrayList<Expression> tempExp = new ArrayList<Expression>();
			tempExp.add(new LessDoubleComparison(null, var[0], 0));
			tempExp.add(new LessDoubleComparison(var[i], null, i + 1));

			if (i >= 1) {
				for (int j = 0; j < i; j++) {
					tempExp.add(new LessDoubleComparison(var[j], var[j + 1], 0));
				}
			}
			exp[i] = EvaluationHelper.createAndExpression(tempExp,
					tempExp.size());
			tempExp.clear();
		}
		return exp;
	}
}
