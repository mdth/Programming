package iqcache.evaluation.benchmark.monarch;

import iqcache.evaluation.BenchmarkEvaluationEnvironment;
import iqcache.evaluation.EvaluationHelper;
import iqcache.evaluation.EvaluationParameters;
import iqcache.expression.Expression;
import iqcache.satisfiability.yices.ResettingYicesSolver;
import iqcache.satisfiability.yices.YicesSolver;

/**
 * This class generates benchmark test results.
 * 
 * @author dinh
 */
public class YicesMonarchEvaluationExecuter {

	// changeable information
	private static final String BENCHMARK = EvaluationParameters.MONARCH;

	// Monarch benchmark data
	private static final String FILE1_1 = "C:\\Users\\Mailan\\workspace\\IQCache SMTSolver Evaluation Scripts\\monarch3\\semanticcache-t1500-s1.txt";
	private static final String FILE1_2 = "C:\\Users\\Mailan\\workspace\\IQCache SMTSolver Evaluation Scripts\\monarch3\\semanticcache-t2500-s2.txt";

	// Monarch benchmark data
	private static final String T1500_S1 = "T1500_S1_R1";
	private static final String T2500_S2 = "T2500_S2_R2";

	/**
	 * Main method, that creates the benchmark test results.
	 * 
	 * @param args
	 *            unused
	 * @throws Exception
	 *             if there are errors in one of the test cases
	 */
	public static void main(String[] args) throws Exception {

		YicesSolver solver = new ResettingYicesSolver();
		
		// buffer for JVM -> using solver 400 times
		for (int i = 0; i < EvaluationHelper.LEVEL * 4; i++) {
			Expression[] partExpr = EvaluationHelper.createBufferExpressions();
			Expression exp = EvaluationHelper.createAndExpression(partExpr,
					EvaluationHelper.LEVEL);

			solver.isSatisfiable(exp);
		}

		BenchmarkEvaluationEnvironment.test(FILE1_1, T1500_S1, solver,
				BENCHMARK);
		BenchmarkEvaluationEnvironment.test(FILE1_2, T2500_S2, solver,
				BENCHMARK);

	}
}
