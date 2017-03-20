package iqcache.evaluation;

import java.io.IOException;

import iqcache.evaluation.t100.T100Z3BVLE;
import iqcache.evaluation.t100.T100Z3IntLE;
import iqcache.evaluation.t100.T100Z3RealLE;
import iqcache.evaluation.t200.T200Z3BVLT_EQUAL;
import iqcache.evaluation.t200.T200Z3IntLE;
import iqcache.evaluation.t200.T200Z3RealLT_EQUAL;
import iqcache.evaluation.t300.T300Z3IntLT;
import iqcache.evaluation.t300.T300Z3RealLT;
import iqcache.evaluation.t400.T400Z3BVLE;
import iqcache.evaluation.t400.T400Z3IntLE;
import iqcache.evaluation.t400.T400Z3RealLE;
import iqcache.evaluation.t500.T500Z3;
import iqcache.evaluation.testbv.TBVZ3;
import iqcache.expression.Expression;
import iqcache.satisfiability.SatisfiabilityException;
import iqcache.solver.Solver;
import iqcache.solver.z3.Z3ResettingSolver;

/**
 * This class represents the abstract evaluation environment for the Solvers Z3
 * and Z3 5.
 * 
 * @author dinh
 * 
 */
public class Z3EvaluationExecuter {

	private final static int REPEATS = 20;

	public static void main(String[] args) throws SatisfiabilityException,
			IOException {

		Solver solver = new Z3ResettingSolver();
		Expression[] partExpr = EvaluationHelper.createBufferExpressions();

		// buffer for JVM -> using solver 1000 times
		for (int a = 0; a < EvaluationHelper.LEVEL * 10; a++) {
			Expression exp = EvaluationHelper.createAndExpression(partExpr,
					EvaluationHelper.LEVEL);
			solver.isSatisfiable(exp);
		}

		T100Z3IntLE.test(solver, REPEATS);
		T100Z3RealLE.test(solver, REPEATS);
		T100Z3BVLE.test(solver, REPEATS);
		System.out.println("T100: ok.");
		T200Z3IntLE.test(solver, REPEATS);
		T200Z3RealLT_EQUAL.test(solver, REPEATS);
		T200Z3BVLT_EQUAL.test(solver, REPEATS);
		System.out.println("T200: ok.");
		T300Z3IntLT.test(solver, REPEATS);
		T300Z3RealLT.test(solver, REPEATS);
		System.out.println("T300: ok.");
		T400Z3BVLE.test(solver, REPEATS);
		T400Z3IntLE.test(solver, REPEATS);
		T400Z3RealLE.test(solver, REPEATS);
		System.out.println("T400: ok.");
		T500Z3.test(solver, REPEATS);
		TBVZ3.test(solver, REPEATS);
	}
}