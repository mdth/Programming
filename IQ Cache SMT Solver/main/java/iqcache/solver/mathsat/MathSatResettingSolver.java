package iqcache.solver.mathsat;

import iqcache.common.Preconditions;
import iqcache.expression.Expression;
import iqcache.satisfiability.SatisfiabilityException;
import iqcache.solver.SMTExpressionConverter;
import iqcache.solver.SMTLibExpression;
import iqcache.solver.SolverException;

/**
 * An instance of a MathSAT 5 Resetting SMT Solver.
 * 
 * @author dinh
 * 
 */
public class MathSatResettingSolver extends MathSatSolver {

	private long env;
	private long config;

	static {
		// load the MathSAT library.
		System.loadLibrary("mathsatj");
	}

	/**
	 * Constructor of the Solver.
	 */
	public MathSatResettingSolver() {
		super();
	}

	@Override
	protected void resetSolver() throws SolverException {
		Preconditions.checkState(isOpen,
				"cannot reset, because solver is not open yet");
		int reset = mathsat.api.msat_reset_env(env);
		if (reset != 0) {
			throw new SolverException("Couldn't reset environment.");
		}
	}

	@Override
	protected void openSolver() throws SolverException {
		if (!isOpen) {
			this.config = mathsat.api.msat_create_config();
			this.env = mathsat.api.msat_create_env(config);
			this.isOpen = true;
		} else {
			this.resetSolver();
		}
	}

	@Override
	protected void closeSolver() throws SolverException {
		// not needed due to resetting
	}

	@Override
	public boolean isSatisfiable(Expression expression)
			throws SatisfiabilityException {
		Preconditions.checkNotNull(expression);
		SMTLibExpression smtExpr = null;

		try {
			this.openSolver();
		} catch (SolverException e1) {
			throw new SatisfiabilityException("Cannot open solver.", e1);
		}

		smtExpr = createSMTLibExpression(expression, smtExpr);

		long formula = smtExpr.getMathSatExpression().getMsatExpr();
		return assertAndCheckFormula(formula);
	}

	private boolean assertAndCheckFormula(long formula)
			throws SatisfiabilityException {
		int assertForumular = mathsat.api.msat_assert_formula(env, formula);

		if (assertForumular == 0) {
			long status = mathsat.api.msat_solve(env);

			if (status == mathsat.api.MSAT_UNSAT) {
				return false;
			} else if (status == mathsat.api.MSAT_SAT) {
				return true;
			} else {
				throw new SatisfiabilityException(
						"UNSAT. Cannot validate formula.");
			}
		} else {
			throw new SatisfiabilityException("Cannot assert formula.");
		}
	}

	private SMTLibExpression createSMTLibExpression(Expression expression,
			SMTLibExpression smtExpr) throws SatisfiabilityException {
		try {
			smtExpr = expression.accept(new SMTExpressionConverter(this, env));
		} catch (SolverException e) {
			throw new SatisfiabilityException(
					"Failed to make variable definition.", e);
		}
		return smtExpr;
	}
}
