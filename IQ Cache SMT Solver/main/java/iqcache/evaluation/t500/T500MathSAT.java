package iqcache.evaluation.t500;

import iqcache.evaluation.EvaluationHelper;
import iqcache.evaluation.EvaluationParameters;
import iqcache.expression.Expression;
import iqcache.expression.leaf.typedouble.LessDoubleComparison;
import iqcache.expression.leaf.typeinteger.LessOrEqualIntegerComparison;
import iqcache.expression.leaf.typevarchar.EqualVarcharComparison;
import iqcache.query.Identifier;
import iqcache.query.column.Column;
import iqcache.query.column.DoubleType;
import iqcache.query.column.IntegerType;
import iqcache.query.column.VarcharType;
import iqcache.satisfiability.SatisfiabilityException;
import iqcache.solver.Solver;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * This class executes the test case T500 with the MathSAT 5 solver.
 * 
 * 1. I0 <= 0 OR R0 < 1.0 OR B0 = "a" 
 * 2. I0 <= 0 OR R0 < 1.0 OR B0 = "a" AND I1 <= 0 OR R1 < 1.0 OR B1 = "a"
 * 3. I0 <= 0 OR R0 < 1.0 OR B0 = "a" AND I1 <= 0 OR R1 < 1.0 OR B1 = "a" AND
 *    I2 <= 0 OR R2 < 1.0 OR B2 = "a" 
 * 4. ....
 * 
 * @author dinh
 * 
 */
public class T500MathSAT {

	private static final String TESTCASE = EvaluationParameters.TEST500;
	private static final String SOLVER = EvaluationParameters.MATHSAT;

	private static BigDecimal time = BigDecimal.ZERO;
	private static StringBuilder rawCSVData = new StringBuilder();
	private static StringBuilder csvData = new StringBuilder();

	/**
	 * This method executes a test case T500.
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

		Expression partExpr[] = createExpressions();

		rawCSVData.append(EvaluationParameters.LEVEL_DEF + ";"
				+ EvaluationParameters.TIME);
		rawCSVData.append("\n");

		csvData.append(EvaluationParameters.LEVEL_DEF + ";"
				+ EvaluationParameters.TIME);
		csvData.append("\n");

		for (int i = 1; i <= EvaluationHelper.LEVEL; i++) {
			BigDecimal tempData = BigDecimal.ZERO;
			
			Expression exp = EvaluationHelper.createAndExpression(partExpr,
					i);

			for (int j = 1; j <= repeats; j++) {

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

		EvaluationHelper.createRawCSVFile(rawCSVData, TESTCASE, "",
				SOLVER);
		EvaluationHelper.createCSVFile(csvData, TESTCASE, "", SOLVER);
		EvaluationHelper.createTXTFile(TESTCASE, "", SOLVER, repeats);
	}

	/**
	 * Creates partial {@link Expression}s for this test case.
	 * 
	 * @return Array of partial {@link Expression}s.
	 */
	private static Expression[] createExpressions() {

		// create columns
		Column varI[] = new Column[EvaluationHelper.LEVEL];
		Column varR[] = new Column[EvaluationHelper.LEVEL];
		Column varB[] = new Column[EvaluationHelper.LEVEL];
		
		final String I = "I";
		final String R = "R";
		final String B = "B";
		
		for (int i = 0; i < EvaluationHelper.LEVEL; i++) {
			varI[i] = new Column(new Identifier("t"), new Identifier(I + i), 1,
					IntegerType.instance(), false, false);
			varR[i] = new Column(new Identifier("t"), new Identifier(R + i), 1,
					DoubleType.instance(), false, false);
			varB[i] = new Column(new Identifier("t"), new Identifier(B + i), 1,
					VarcharType.instance(1), false, false);
		}

		// create partial expressions
		Expression[] exp = new Expression[EvaluationHelper.LEVEL];
		
		int tempSize = 3;
		Expression[] temp = new Expression[tempSize];
		for (int i = 0; i < EvaluationHelper.LEVEL; i++) {
			temp[0] = new LessOrEqualIntegerComparison(varI[i], null, 0);
			temp[1] = new LessDoubleComparison(varR[i], null, 1.0);
			temp[2] = new EqualVarcharComparison(varB[i], "a");
			
			exp[i] = EvaluationHelper.createOrExpression(temp, tempSize);
		}
		return exp;
	}
}
