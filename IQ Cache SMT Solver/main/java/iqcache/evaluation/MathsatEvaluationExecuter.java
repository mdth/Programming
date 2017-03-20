package iqcache.evaluation;

import java.io.IOException;

import iqcache.evaluation.t100.T100MathSATBVLE;
import iqcache.evaluation.t100.T100MathSATIntLE;
import iqcache.evaluation.t100.T100MathSATRealLE;
import iqcache.evaluation.t200.T200MathSATBVLT_EQUAL;
import iqcache.evaluation.t200.T200MathSATIntLE;
import iqcache.evaluation.t200.T200MathSATRealLT_EQUAL;
import iqcache.evaluation.t300.T300MathSATIntLT;
import iqcache.evaluation.t300.T300MathSATRealLT;
import iqcache.evaluation.t400.T400MathSATBVLE;
import iqcache.evaluation.t400.T400MathSATIntLE;
import iqcache.evaluation.t400.T400MathSATRealLE;
import iqcache.evaluation.t500.T500MathSAT;
import iqcache.evaluation.testbv.TBVMathSAT;
import iqcache.expression.Expression;
import iqcache.satisfiability.SatisfiabilityException;
import iqcache.solver.Solver;
import iqcache.solver.mathsat.MathSatResettingSolver;

/**
 * This class represents the abstract evaluation environment for the Solvers Z3
 * and MathSAT 5.
 * 
 * @author dinh
 * 
 */
public class MathsatEvaluationExecuter {

	private final static int REPEATS = 20;

	public static void main(String[] args) throws SatisfiabilityException,
			IOException {

		Solver solver = new MathSatResettingSolver();
		Expression[] partExpr = EvaluationHelper.createBufferExpressions();

		// buffer for JVM -> using solver 1000 times
		for (int a = 0; a < EvaluationHelper.LEVEL * 10; a++) {
			Expression exp = EvaluationHelper.createAndExpression(partExpr,
					EvaluationHelper.LEVEL);
			solver.isSatisfiable(exp);
		}

		T100MathSATIntLE.test(solver, REPEATS);
		T100MathSATRealLE.test(solver, REPEATS);
		T100MathSATBVLE.test(solver, REPEATS);
		System.out.println("T100: ok.");

		T200MathSATIntLE.test(solver, REPEATS);
		T200MathSATRealLT_EQUAL.test(solver, REPEATS);
		T200MathSATBVLT_EQUAL.test(solver, REPEATS);
		System.out.println("T200: ok.");
		T300MathSATIntLT.test(solver, REPEATS);
		T300MathSATRealLT.test(solver, REPEATS);
		System.out.println("T300: ok.");
		T400MathSATBVLE.test(solver, REPEATS);
		T400MathSATIntLE.test(solver, REPEATS);
		T400MathSATRealLE.test(solver, REPEATS);
		System.out.println("T400: ok.");
		T500MathSAT.test(solver, REPEATS);
		TBVMathSAT.test(solver, REPEATS);
	}
}