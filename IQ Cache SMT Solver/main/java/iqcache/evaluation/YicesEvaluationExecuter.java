package iqcache.evaluation;

import java.io.IOException;

import iqcache.evaluation.t100.T100YicesBVLE;
import iqcache.evaluation.t100.T100YicesIntLE;
import iqcache.evaluation.t100.T100YicesRealLE;
import iqcache.evaluation.t200.T200YicesBVLT_EQUAL;
import iqcache.evaluation.t200.T200YicesIntLE;
import iqcache.evaluation.t200.T200YicesRealLT_EQUAL;
import iqcache.evaluation.t300.T300YicesIntLT;
import iqcache.evaluation.t300.T300YicesRealLT;
import iqcache.evaluation.t400.T400YicesBVLE;
import iqcache.evaluation.t400.T400YicesIntLE;
import iqcache.evaluation.t400.T400YicesRealLE;
import iqcache.evaluation.t500.T500Yices;
import iqcache.evaluation.testbv.TBVYices;
import iqcache.expression.Expression;
import iqcache.satisfiability.SatisfiabilityException;
import iqcache.satisfiability.yices.ResettingYicesSolver;
import iqcache.satisfiability.yices.YicesSolver;

/**
 * This class represents the evaluation executer
 * 
 * @author dinh
 * 
 */
public class YicesEvaluationExecuter {

	private final static int REPEATS = 20;

	public static void main(String[] args) throws SatisfiabilityException,
			IOException {

		YicesSolver solver = new ResettingYicesSolver();
		Expression[] partExpr = EvaluationHelper.createBufferExpressions();

		// buffer for JVM -> using solver 1000 times
		for (int a = 0; a < EvaluationHelper.LEVEL * 10; a++) {
			Expression exp = EvaluationHelper.createAndExpression(partExpr,
					EvaluationHelper.LEVEL);
			solver.isSatisfiable(exp);
		}

		T100YicesIntLE.test(solver, REPEATS);
		T100YicesRealLE.test(solver, REPEATS);
		T100YicesBVLE.test(solver, REPEATS);
		
		T200YicesIntLE.test(solver, REPEATS);
		T200YicesRealLT_EQUAL.test(solver, REPEATS);
		T200YicesBVLT_EQUAL.test(solver, REPEATS);
		
		T300YicesIntLT.test(solver, REPEATS);
		T300YicesRealLT.test(solver, REPEATS);
		
		T400YicesBVLE.test(solver, REPEATS);
		T400YicesIntLE.test(solver, REPEATS);
		T400YicesRealLE.test(solver, REPEATS);
		
		T500Yices.test(solver, REPEATS);
		TBVYices.test(solver, REPEATS);
	}
}