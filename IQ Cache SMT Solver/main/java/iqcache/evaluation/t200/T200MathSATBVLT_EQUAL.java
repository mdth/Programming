package iqcache.evaluation.t200;

import iqcache.evaluation.EvaluationHelper;
import iqcache.evaluation.EvaluationParameters;
import iqcache.expression.Expression;
import iqcache.expression.leaf.typevarchar.EqualVarcharComparison;
import iqcache.expression.leaf.typevarchar.LessVarcharComparison;
import iqcache.query.Identifier;
import iqcache.query.column.Column;
import iqcache.query.column.VarcharType;
import iqcache.satisfiability.SatisfiabilityException;
import iqcache.solver.Solver;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * This class executes the test case T200BVLT_EQUAL with the MathSAT 5 solver.
 * 
 * 1. "aa" < X0 AND X0 = X1 AND X1 < "ac" 
 * 2. "aa" < X0 AND X0 = X1 AND X1 = X2 AND X2 < "ac" 
 * 3. "aa" < X0 AND X0 = X1 AND X1 = X2 AND X2 = X3 AND X3 < "ac"
 * 4. "aa" < X0 AND X0 = X1 AND X1 = X2 AND X2 = X3 AND X3 = X4 AND X4 < "ac" 
 * 5. .....
 * 
 * @author dinh
 * 
 */
public class T200MathSATBVLT_EQUAL {

	private static final String TESTCASE = EvaluationParameters.TEST200;
	private static final String TESTCASE_EXT = EvaluationParameters.BV
			+ EvaluationParameters.LT + "_" + EvaluationParameters.EQUAL;
	private static final String SOLVER = EvaluationParameters.MATHSAT;

	private static BigDecimal time = BigDecimal.ZERO;
	private static StringBuilder rawCSVData = new StringBuilder();
	private static StringBuilder csvData = new StringBuilder();

	/**
	 * This method executes a test case T200BVLT_EQUAL.
	 * 
	 * @param solver
	 *            MathSAT 5 solver
	 * @param repeats
	 *            the number of repeats of one expression
	 * @throws IOException
	 *             if I/O error occurs
	 * @throws SatisfiabilityException
	 *             if the solver cannot validate the given expression
	 */
	public static void test(Solver solver, int repeats)
			throws IOException, SatisfiabilityException {

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
		Column var[] = new Column[EvaluationHelper.LEVEL + 1];
		final String X = "Z";

		for (int i = 0; i <= EvaluationHelper.LEVEL; i++) {
			String id = X + i;
			var[i] = new Column(new Identifier("t"), new Identifier(id), 1,
					VarcharType.instance(2), false, false);
		}

		// create partial expressions
		Expression[] exp = new Expression[EvaluationHelper.LEVEL];

		for (int i = 1; i <= EvaluationHelper.LEVEL; i++) {
			ArrayList<Expression> tempExp = new ArrayList<Expression>();
			tempExp.add(new LessVarcharComparison("aa", var[0]));
			tempExp.add(new LessVarcharComparison(var[i], "ac"));

			for (int j = 0; j < i; j++) {
				tempExp.add(new EqualVarcharComparison(var[j], var[j + 1]));
			}

			exp[i - 1] = EvaluationHelper.createAndExpression(tempExp,
					tempExp.size());
			tempExp.clear();
		}
		return exp;
	}
}
