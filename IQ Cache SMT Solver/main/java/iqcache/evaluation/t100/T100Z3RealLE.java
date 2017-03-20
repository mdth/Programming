package iqcache.evaluation.t100;

import iqcache.evaluation.EvaluationHelper;
import iqcache.evaluation.EvaluationParameters;
import iqcache.expression.Expression;
import iqcache.expression.leaf.typedouble.LessOrEqualDoubleComparison;
import iqcache.query.Identifier;
import iqcache.query.column.Column;
import iqcache.query.column.DoubleType;
import iqcache.satisfiability.SatisfiabilityException;
import iqcache.solver.Solver;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * This class executes the test case T100REALLE with the Z3 solver.
 * 
 * 1. X0 <= 0 
 * 2. X0 <= 0 AND X1 <= 0 
 * 3. X0 <= 0 AND X1 <= 0 AND X2 <= 0 
 * 4. .....
 * 
 * @author dinh
 * 
 */
public class T100Z3RealLE {

	private static final String TESTCASE = EvaluationParameters.TEST100;
	private static final String TESTCASE_EXT = EvaluationParameters.REAL
			+ EvaluationParameters.LE;
	private static final String SOLVER = EvaluationParameters.Z3;

	private static BigDecimal time = BigDecimal.ZERO;
	private static StringBuilder rawCSVData = new StringBuilder();
	private static StringBuilder csvData = new StringBuilder();

	/**
	 * This method executes a test case T100REALLE.
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

		Expression partExpr[] = createPartialExpressions();

		rawCSVData.append(EvaluationParameters.LEVEL_DEF + ";"
				+ EvaluationParameters.TIME);
		rawCSVData.append("\n");

		csvData.append(EvaluationParameters.LEVEL_DEF + ";"
				+ EvaluationParameters.TIME);
		csvData.append("\n");

		for (int i = 1; i <= EvaluationHelper.LEVEL; i++) {
			BigDecimal tempData = BigDecimal.ZERO;

			for (int j = 1; j <= repeats; j++) {
				Expression exp = EvaluationHelper.createAndExpression(partExpr,
						i);

				// starting time
				long start = System.nanoTime();

				solver.isSatisfiable(exp);

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
	 * Creates partial {@link Expression}s in the form Xi <= 0, for i =
	 * {0,...,N}. N is max. 100 in this case.
	 * 
	 * @return Array of partial {@link Expression}s.
	 */
	private static Expression[] createPartialExpressions() {

		// create columns
		Column var[] = new Column[EvaluationHelper.LEVEL];
		final String X = "X";

		for (int i = 0; i < EvaluationHelper.LEVEL; i++) {
			String id = X + i;
			var[i] = new Column(new Identifier("t"), new Identifier(id), 1,
					DoubleType.instance(), false, false);
		}

		// create partial expressions
		Expression exp[] = new Expression[EvaluationHelper.LEVEL];

		for (int i = 0; i < EvaluationHelper.LEVEL; i++) {
			exp[i] = new LessOrEqualDoubleComparison(var[i], null, 0);
		}
		return exp;
	}
}