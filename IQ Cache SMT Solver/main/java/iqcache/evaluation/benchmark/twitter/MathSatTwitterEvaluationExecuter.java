package iqcache.evaluation.benchmark.twitter;

import iqcache.evaluation.BenchmarkEvaluationEnvironmentN;
import iqcache.evaluation.EvaluationHelper;
import iqcache.evaluation.EvaluationParameters;
import iqcache.expression.Expression;
import iqcache.solver.Solver;
import iqcache.solver.mathsat.MathSatResettingSolver;

/**
 * This class generates benchmark test results.
 * 
 * @author dinh
 */
public class MathSatTwitterEvaluationExecuter {

	// changeable information
	private static final String BENCHMARK = EvaluationParameters.TWITTER;

	// Twitter benchmark data
	private static final String FILE1 = "C:\\Users\\foo\\workspace\\IQCache SMTSolver Evaluation Scripts\\twitter\\semanticcache-t4000-s2-q2000.txt";
	private static final String FILE2 = "C:\\Users\\foo\\workspace\\IQCache SMTSolver Evaluation Scripts\\twitter\\semanticcache-t8000-s4-q2000.txt";
	private static final String FILE3 = "C:\\Users\\foo\\workspace\\IQCache SMTSolver Evaluation Scripts\\twitter\\semanticcache-t12000-s6-q2000.txt";
	private static final String FILE4 = "C:\\Users\\foo\\workspace\\IQCache SMTSolver Evaluation Scripts\\twitter\\semanticcache-t16000-s8-q2000.txt";
	private static final String FILE5 = "C:\\Users\\foo\\workspace\\IQCache SMTSolver Evaluation Scripts\\twitter\\semanticcache-t20000-s10-q2000.txt";

	// Twitter benchmark data
	private static final String T4000_S2_Q2000 = "T4000_S2_Q2000";
	private static final String T8000_S4_Q2000 = "T8000_S4_Q2000";
	private static final String T12000_S6_Q2000 = "T12000_S6_Q2000";
	private static final String T16000_S8_Q2000 = "T16000_S8_Q2000";
	private static final String T20000_S10_Q2000 = "T20000_S10_Q2000";

	/**
	 * Main method, that creates the benchmark test results.
	 * 
	 * @param args
	 *            unused
	 * @throws Exception
	 *             if there are errors in one of the test cases
	 */
	public static void main(String[] args) throws Exception {

		Solver solver = new MathSatResettingSolver();
		
		// buffer for JVM -> using solver 400 times
		for (int i = 0; i < EvaluationHelper.LEVEL * 4; i++) {
			Expression[] partExpr = EvaluationHelper.createBufferExpressions();
			Expression exp = EvaluationHelper.createAndExpression(partExpr,
					EvaluationHelper.LEVEL);

			solver.isSatisfiable(exp);
		}

		BenchmarkEvaluationEnvironmentN.test(FILE1, T4000_S2_Q2000, solver,
				BENCHMARK);
		BenchmarkEvaluationEnvironmentN.test(FILE2, T8000_S4_Q2000, solver,
				BENCHMARK);
		BenchmarkEvaluationEnvironmentN.test(FILE3, T12000_S6_Q2000, solver,
				BENCHMARK);
		BenchmarkEvaluationEnvironmentN.test(FILE4, T16000_S8_Q2000, solver,
				BENCHMARK);
		BenchmarkEvaluationEnvironmentN.test(FILE5, T20000_S10_Q2000, solver,
				BENCHMARK);
	}
}
