package iqcache.evaluation.benchmark.yahoo;

import iqcache.evaluation.BenchmarkEvaluationEnvironment;
import iqcache.evaluation.BenchmarkEvaluationEnvironmentN;
import iqcache.evaluation.EvaluationHelper;
import iqcache.evaluation.EvaluationParameters;
import iqcache.expression.Expression;
import iqcache.solver.Solver;
import iqcache.solver.z3.Z3ResettingSolver;

/**
 * This class generates benchmark test results.
 * 
 * @author dinh
 */
public class Z3YahooEvaluationExecuter {

	// changeable information
	private static final String BENCHMARK = EvaluationParameters.YAHOO;

	// Yahoo benchmark data
	private static final String FILE1_1 = "C:\\Users\\foo\\workspace\\IQCache SMTSolver Evaluation Scripts\\ycsb\\semanticcache-t2000-s10-r1.txt";
	private static final String FILE2_1 = "C:\\Users\\foo\\workspace\\IQCache SMTSolver Evaluation Scripts\\ycsb\\semanticcache-t2000-s10-r2.txt";
	private static final String FILE3_1 = "C:\\Users\\foo\\workspace\\IQCache SMTSolver Evaluation Scripts\\ycsb\\semanticcache-t2000-s10-r3.txt";
	private static final String FILE4_1 = "C:\\Users\\foo\\workspace\\IQCache SMTSolver Evaluation Scripts\\ycsb\\semanticcache-t2000-s10-r4.txt";
	private static final String FILE5_1 = "C:\\Users\\foo\\workspace\\IQCache SMTSolver Evaluation Scripts\\ycsb\\semanticcache-t2000-s10-r5.txt";

	private static final String FILE1_2 = "C:\\Users\\foo\\workspace\\IQCache SMTSolver Evaluation Scripts\\ycsb\\semanticcache-t4000-s20-r1.txt";
	private static final String FILE2_2 = "C:\\Users\\foo\\workspace\\IQCache SMTSolver Evaluation Scripts\\ycsb\\semanticcache-t4000-s20-r2.txt";
	private static final String FILE3_2 = "C:\\Users\\foo\\workspace\\IQCache SMTSolver Evaluation Scripts\\ycsb\\semanticcache-t4000-s20-r3.txt";
	private static final String FILE4_2 = "C:\\Users\\foo\\workspace\\IQCache SMTSolver Evaluation Scripts\\ycsb\\semanticcache-t4000-s20-r4.txt";
	private static final String FILE5_2 = "C:\\Users\\foo\\workspace\\IQCache SMTSolver Evaluation Scripts\\ycsb\\semanticcache-t4000-s20-r5.txt";

	private static final String FILE1_3 = "C:\\Users\\foo\\workspace\\IQCache SMTSolver Evaluation Scripts\\ycsb\\semanticcache-t6000-s30-r1.txt";
	private static final String FILE2_3 = "C:\\Users\\foo\\workspace\\IQCache SMTSolver Evaluation Scripts\\ycsb\\semanticcache-t6000-s30-r2.txt";
	private static final String FILE3_3 = "C:\\Users\\foo\\workspace\\IQCache SMTSolver Evaluation Scripts\\ycsb\\semanticcache-t6000-s30-r3.txt";
	private static final String FILE4_3 = "C:\\Users\\foo\\workspace\\IQCache SMTSolver Evaluation Scripts\\ycsb\\semanticcache-t6000-s30-r4.txt";
	private static final String FILE5_3 = "C:\\Users\\foo\\workspace\\IQCache SMTSolver Evaluation Scripts\\ycsb\\semanticcache-t6000-s30-r5.txt";

	private static final String FILE1_4 = "C:\\Users\\foo\\workspace\\IQCache SMTSolver Evaluation Scripts\\ycsb\\semanticcache-t8000-s40-r1.txt";
	private static final String FILE2_4 = "C:\\Users\\foo\\workspace\\IQCache SMTSolver Evaluation Scripts\\ycsb\\semanticcache-t8000-s40-r2.txt";
	private static final String FILE3_4 = "C:\\Users\\foo\\workspace\\IQCache SMTSolver Evaluation Scripts\\ycsb\\semanticcache-t8000-s40-r3.txt";
	private static final String FILE4_4 = "C:\\Users\\foo\\workspace\\IQCache SMTSolver Evaluation Scripts\\ycsb\\semanticcache-t8000-s40-r4.txt";
	private static final String FILE5_4 = "C:\\Users\\foo\\workspace\\IQCache SMTSolver Evaluation Scripts\\ycsb\\semanticcache-t8000-s40-r5.txt";

	private static final String FILE1_5 = "C:\\Users\\foo\\workspace\\IQCache SMTSolver Evaluation Scripts\\ycsb\\semanticcache-t10000-s50-r1.txt";
	private static final String FILE2_5 = "C:\\Users\\foo\\workspace\\IQCache SMTSolver Evaluation Scripts\\ycsb\\semanticcache-t10000-s50-r2.txt";
	private static final String FILE3_5 = "C:\\Users\\foo\\workspace\\IQCache SMTSolver Evaluation Scripts\\ycsb\\semanticcache-t10000-s50-r3.txt";
	private static final String FILE4_5 = "C:\\Users\\foo\\workspace\\IQCache SMTSolver Evaluation Scripts\\ycsb\\semanticcache-t10000-s50-r4.txt";
	private static final String FILE5_5 = "C:\\Users\\foo\\workspace\\IQCache SMTSolver Evaluation Scripts\\ycsb\\semanticcache-t10000-s50-r5.txt";

	// Yahoo benchmark data
	private static final String T2000_S10_R1 = "T2000_S10_R1";
	private static final String T2000_S10_R2 = "T2000_S10_R2";
	private static final String T2000_S10_R3 = "T2000_S10_R3";
	private static final String T2000_S10_R4 = "T2000_S10_R4";
	private static final String T2000_S10_R5 = "T2000_S10_R5";

	private static final String T4000_S20_R1 = "T4000_S20_R1";
	private static final String T4000_S20_R2 = "T4000_S20_R2";
	private static final String T4000_S20_R3 = "T4000_S20_R3";
	private static final String T4000_S20_R4 = "T4000_S20_R4";
	private static final String T4000_S20_R5 = "T4000_S20_R5";

	private static final String T6000_S30_R1 = "T6000_S30_R1";
	private static final String T6000_S30_R2 = "T6000_S30_R2";
	private static final String T6000_S30_R3 = "T6000_S30_R3";
	private static final String T6000_S30_R4 = "T6000_S30_R4";
	private static final String T6000_S30_R5 = "T6000_S30_R5";

	private static final String T8000_S40_R1 = "T8000_S40_R1";
	private static final String T8000_S40_R2 = "T8000_S40_R2";
	private static final String T8000_S40_R3 = "T8000_S40_R3";
	private static final String T8000_S40_R4 = "T8000_S40_R4";
	private static final String T8000_S40_R5 = "T8000_S40_R5";

	private static final String T10000_S50_R1 = "T10000_S50_R1";
	private static final String T10000_S50_R2 = "T10000_S50_R2";
	private static final String T10000_S50_R3 = "T10000_S50_R3";
	private static final String T10000_S50_R4 = "T10000_S50_R4";
	private static final String T10000_S50_R5 = "T10000_S50_R5";

	/**
	 * Main method, that creates the benchmark test results.
	 * 
	 * @param args
	 *            unused
	 * @throws Exception
	 *             if there are errors in one of the test cases
	 */
	public static void main(String[] args) throws Exception {

		Solver solver = new Z3ResettingSolver();

		// buffer for JVM -> using solver 400 times
		for (int i = 0; i < EvaluationHelper.LEVEL * 4; i++) {
			Expression[] partExpr = EvaluationHelper.createBufferExpressions();
			Expression exp = EvaluationHelper.createAndExpression(partExpr,
					EvaluationHelper.LEVEL);

			solver.isSatisfiable(exp);
		}

		// T2000 - S10
		BenchmarkEvaluationEnvironment.test(FILE1_1, T2000_S10_R1, solver,
				BENCHMARK);
		BenchmarkEvaluationEnvironment.test(FILE2_1, T2000_S10_R2, solver,
				BENCHMARK);
		BenchmarkEvaluationEnvironment.test(FILE3_1, T2000_S10_R3, solver,
				BENCHMARK);
		BenchmarkEvaluationEnvironment.test(FILE4_1, T2000_S10_R4, solver,
				BENCHMARK);
		BenchmarkEvaluationEnvironment.test(FILE5_1, T2000_S10_R5, solver,
				BENCHMARK);
		//
		// T4000 - S20
		BenchmarkEvaluationEnvironment.test(FILE1_2, T4000_S20_R1, solver,
				BENCHMARK);
		BenchmarkEvaluationEnvironment.test(FILE2_2, T4000_S20_R2, solver,
				BENCHMARK);
		BenchmarkEvaluationEnvironment.test(FILE3_2, T4000_S20_R3, solver,
				BENCHMARK);
		BenchmarkEvaluationEnvironment.test(FILE4_2, T4000_S20_R4, solver,
				BENCHMARK);
		BenchmarkEvaluationEnvironment.test(FILE5_2, T4000_S20_R5, solver,
				BENCHMARK);

		// T6000 - S30
		BenchmarkEvaluationEnvironment.test(FILE1_3, T6000_S30_R1, solver,
				BENCHMARK);
		BenchmarkEvaluationEnvironment.test(FILE2_3, T6000_S30_R2, solver,
				BENCHMARK);
		BenchmarkEvaluationEnvironment.test(FILE3_3, T6000_S30_R3, solver,
				BENCHMARK);
		BenchmarkEvaluationEnvironment.test(FILE4_3, T6000_S30_R4, solver,
				BENCHMARK);
		BenchmarkEvaluationEnvironment.test(FILE5_3, T6000_S30_R5, solver,
				BENCHMARK);

		// T8000 - S40
		BenchmarkEvaluationEnvironment.test(FILE1_4, T8000_S40_R1, solver,
				BENCHMARK);
		BenchmarkEvaluationEnvironment.test(FILE2_4, T8000_S40_R2, solver,
				BENCHMARK);
		BenchmarkEvaluationEnvironment.test(FILE3_4, T8000_S40_R3, solver,
				BENCHMARK);
		BenchmarkEvaluationEnvironment.test(FILE4_4, T8000_S40_R4, solver,
				BENCHMARK);
		BenchmarkEvaluationEnvironment.test(FILE5_4, T8000_S40_R5, solver,
				BENCHMARK);

		// T10000 - S50
		 BenchmarkEvaluationEnvironment.test(FILE1_5, T10000_S50_R1, solver,
		 BENCHMARK);
		 BenchmarkEvaluationEnvironmentN.test(FILE2_5, T10000_S50_R2, solver,
		 BENCHMARK);
		 BenchmarkEvaluationEnvironmentN.test(FILE3_5, T10000_S50_R3, solver,
		 BENCHMARK);
		 BenchmarkEvaluationEnvironmentN.test(FILE4_5, T10000_S50_R4, solver,
		 BENCHMARK);
		 BenchmarkEvaluationEnvironmentN.test(FILE5_5, T10000_S50_R5, solver,
		 BENCHMARK);
	}
}
