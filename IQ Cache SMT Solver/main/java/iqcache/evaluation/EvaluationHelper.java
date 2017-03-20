package iqcache.evaluation;

import iqcache.evaluation.EvaluationParameters;
import iqcache.expression.Expression;
import iqcache.expression.leaf.typeinteger.LessOrEqualIntegerComparison;
import iqcache.expression.node.And;
import iqcache.expression.node.Or;
import iqcache.query.Identifier;
import iqcache.query.column.Column;
import iqcache.query.column.IntegerType;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigInteger;
import java.util.ArrayList;

/**
 * This class represents the abstract evaluation environment for the Solvers Z3
 * and MathSAT 5.
 * 
 * @author dinh
 * 
 */
public class EvaluationHelper {

	public static final int MS_TRANSFORMATION = 1000000;
	public static final int LEVEL = 100;

	/**
	 * Creates a csv file with raw data for the current test case.
	 * 
	 * @throws IOException
	 *             if there's an IO error.
	 */
	public static void createRawCSVFile(StringBuilder data, String testCase,
			String testCaseExtension, String solverName) throws IOException {
		Writer fwCSV = new FileWriter(testCase + solverName + testCaseExtension
				+ EvaluationParameters.RAW + EvaluationParameters.CSV);

		try {
			fwCSV.write(data.toString());
		} catch (IOException e) {
			System.err.println("Couldn't write file.");
		} finally {
			if (fwCSV != null)
				try {
					fwCSV.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * Creates a csv file with computed data for the current test case.
	 * 
	 * @throws IOException
	 *             if there's an IO error.
	 */
	public static void createCSVFile(StringBuilder data, String testCase,
			String testCaseExtension, String solverName) throws IOException {
		Writer fwCSV = new FileWriter(testCase + solverName + testCaseExtension
				+ EvaluationParameters.CSV);

		try {
			fwCSV.write(data.toString());
		} catch (IOException e) {
			System.err.println("Couldn't write file.");
		} finally {
			if (fwCSV != null)
				try {
					fwCSV.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * Creates a txt information file about the current test case.
	 * 
	 * @throws IOException
	 *             if there's an IO error.
	 */
	public static void createTXTFile(String testCase, String testCaseExtension,
			String solverName, int repeats) throws IOException {
		Writer fwTXT = new FileWriter(testCase + solverName + testCaseExtension
				+ EvaluationParameters.TXT);

		StringBuilder txtData = new StringBuilder();
		txtData.append(EvaluationParameters.TEST_CASE_NUMBER + testCase + "\n");
		txtData.append(EvaluationParameters.SOLVERNAME_DEF + solverName + "\n");
		txtData.append(EvaluationParameters.TYPE_OF_TEST + testCaseExtension
				+ "\n");
		txtData.append(EvaluationParameters.LEVEL_DEF + LEVEL + "\n");
		txtData.append(EvaluationParameters.REPEATS_DEF + repeats + "\n");

		try {
			fwTXT.write(txtData.toString());
		} catch (IOException e) {
			System.err.println("Couldn't write file.");
		} finally {
			if (fwTXT != null)
				try {
					fwTXT.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * Creates a txt information file about the current benchmark test case.
	 * 
	 * @throws IOException
	 *             if there's an I/O error.
	 */
	protected static void createTXTFile(String solverName, String benchmark,
			String test, BigInteger totalTime, int numberOfExpressions)
			throws IOException {
		Writer fwTXT = new FileWriter(benchmark + solverName + test
				+ EvaluationParameters.TXT);

		StringBuilder txtData = new StringBuilder();
		txtData.append(EvaluationParameters.BENCHMARK_DEF + benchmark + "\n");
		txtData.append(EvaluationParameters.SOLVERNAME_DEF + solverName + "\n");
		txtData.append(EvaluationParameters.TOTAL_TIME + totalTime.toString()
				+ "\n");
		txtData.append(EvaluationParameters.NUMBER_OF_CHECKED_EXPRESSIONS
				+ numberOfExpressions);

		try {
			fwTXT.write(txtData.toString());
		} catch (IOException e) {
			System.err.println("Couldn't write file.");
		} finally {
			if (fwTXT != null)
				try {
					fwTXT.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * Creates a concatenation of given {@link Expression}s with the logical 
	 * AND.
	 * 
	 * @param links
	 *            the number of expressions to link
	 * @return The concatenated expression.
	 */
	public static Expression createAndExpression(Expression[] partExpr,
			int links) {
		if (links == 1) {
			return partExpr[links - 1];
		} else {
			Expression[] tempExpr = new Expression[links];
			for (int j = 0; j < links; j++) {
				tempExpr[j] = partExpr[j];
			}

			return new And(tempExpr);
		}
	}
	
	/**
	 * Creates a concatenation of given {@link Expression}s with the logical 
	 * OR.
	 * 
	 * @param links
	 *            the number of expressions to link
	 * @return The concatenated expression.
	 */
	public static Expression createOrExpression(Expression[] partExpr,
			int links) {
		if (links == 1) {
			return partExpr[links - 1];
		} else {
			Expression[] tempExpr = new Expression[links];
			for (int j = 0; j < links; j++) {
				tempExpr[j] = partExpr[j];
			}

			return new Or(tempExpr);
		}
	}
	
	/**
	 * Creates a concatenation of given {@link Expression}s with the logical 
	 * AND.
	 * 
	 * @param links
	 *            the number of expressions to link
	 * @return The concatenated expression.
	 */
	public static Expression createAndExpression(
			ArrayList<Expression> partExpr, int links) {
		if (links == 1) {
			return partExpr.get(links - 1);
		} else {
			
			Expression[] tempExpr = new Expression[links];
			for (int j = 0; j < links; j++) {
				tempExpr[j] = partExpr.get(j);
			}
			return new And(tempExpr);
		}
	}

	/**
	 * Creates partial {@link Expression}s for the buffer rounds.
	 * 
	 * @return Array of partial {@link Expression}s.
	 */
	public static Expression[] createBufferExpressions() {

		// create columns
		Column var[] = new Column[EvaluationHelper.LEVEL];
		final String X = "X";

		for (int i = 0; i < EvaluationHelper.LEVEL; i++) {
			String id = X + i;
			var[i] = new Column(new Identifier("t"), new Identifier(id), 1,
					IntegerType.instance(), false, false);
		}

		// create partial expressions
		Expression exp[] = new Expression[EvaluationHelper.LEVEL];

		for (int i = 0; i < EvaluationHelper.LEVEL; i++) {
			exp[i] = new LessOrEqualIntegerComparison(var[i], null, 0);
		}
		return exp;
	}
}
