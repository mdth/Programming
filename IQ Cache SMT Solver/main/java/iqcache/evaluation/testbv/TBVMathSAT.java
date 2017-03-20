package iqcache.evaluation.testbv;

import iqcache.evaluation.EvaluationHelper;
import iqcache.evaluation.EvaluationParameters;
import iqcache.expression.Expression;
import iqcache.expression.leaf.typevarchar.LessVarcharComparison;
import iqcache.query.Identifier;
import iqcache.query.column.Column;
import iqcache.query.column.VarcharType;
import iqcache.satisfiability.SatisfiabilityException;
import iqcache.solver.Solver;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * This class executes the test case T100BVLE with the MathSAT 5 solver.
 * 
 * 1. X1 < "b" 
 * 2. X1 < "b" AND X2 < "b" 
 * 3. X1 < "b" AND X2 < "b" AND X3 < "b" 
 * 4. .....
 * 
 * The number <b>i</b> in the variable Xi indicates the length of the bitvector
 * variable. X1 has the length one, X2 has the length two, ...
 * 
 * @author dinh
 * 
 */
public class TBVMathSAT {

	private static final int LEVEL = EvaluationHelper.LEVEL / 2;
	private static final String TESTCASE = EvaluationParameters.TESTBV;
	private static final String SOLVER = EvaluationParameters.MATHSAT;

	private static BigDecimal time = BigDecimal.ZERO;
	private static StringBuilder rawCSVData = new StringBuilder();
	private static StringBuilder csvData = new StringBuilder();

	/**
	 * This method executes a test case T100BVLE.
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
	public static void test(Solver solver, int repeats) throws IOException,
			SatisfiabilityException {

		Expression partExpr[] = createPartialExpressions();

		rawCSVData.append(EvaluationParameters.LEVEL_DEF + ";"
				+ EvaluationParameters.TIME);
		rawCSVData.append("\n");

		csvData.append(EvaluationParameters.LEVEL_DEF + ";"
				+ EvaluationParameters.TIME);
		csvData.append("\n");

		for (int i = 1; i <= LEVEL; i++) {
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
			System.out.println("Stufe " + i);
			tempData = tempData.divide(BigDecimal.valueOf(repeats));
			tempData = tempData.divide(BigDecimal
					.valueOf(EvaluationHelper.MS_TRANSFORMATION));
			csvData.append(i + ";" + tempData);
			csvData.append("\n");
		}

		EvaluationHelper.createRawCSVFile(rawCSVData, TESTCASE, "", SOLVER);
		EvaluationHelper.createCSVFile(csvData, TESTCASE, "", SOLVER);
		EvaluationHelper.createTXTFile(TESTCASE, "", SOLVER, repeats);
	}

	/**
	 * Creates partial {@link Expression}s in the form Xi < "b", for i =
	 * {0,...,N}. N is max. 100 in this case.
	 * 
	 * @return Array of partial {@link Expression}s.
	 */
	private static Expression[] createPartialExpressions() {

		// create columns
		Column var[] = new Column[LEVEL];
		final String B = "B";

		for (int i = 0; i < LEVEL; i++) {
			String id = B + (i + 1);
			var[i] = new Column(new Identifier("t"), new Identifier(id), 1,
					VarcharType.instance(i + 1), false, false);
		}

		// create partial expressions
		Expression exp[] = new Expression[LEVEL];

		for (int i = 0; i < LEVEL; i++) {
			exp[i] = new LessVarcharComparison(var[i], "b");
		}
		return exp;
	}
}
