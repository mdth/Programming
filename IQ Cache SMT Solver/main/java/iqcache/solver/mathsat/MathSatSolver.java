package iqcache.solver.mathsat;

import iqcache.common.Preconditions;
import iqcache.expression.Expression;
import iqcache.satisfiability.SatisfiabilityException;
import iqcache.solver.SMTExpressionConverter;
import iqcache.solver.SMTLibExpression;
import iqcache.solver.Solver;
import iqcache.solver.SolverException;

/**
 * An instance of a MathSAT 5 SMT Solver.
 * 
 * @author dinh
 * 
 */
public class MathSatSolver extends Solver {

	private long env;
	private long config;

	static {
		// load the MathSAT library.
		System.loadLibrary("mathsatj");
	}

	/**
	 * Constructor of the Solver.
	 */
	public MathSatSolver() {
		super();
	}

	@Override
	/**
	 * This method resets the used environment, but will not delete the terms, 
	 * that were created until this method was used. If you don't want the 
	 * solver to crash, be sure to use different variable names, when you create
	 * them or use mathsat.api.msat_gc_env. This method will trigger the garbage
	 * collection and destroy all created terms until now.
	 */
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
		Preconditions.checkState(!isOpen, "already open");
		this.config = mathsat.api.msat_create_config();
		this.env = mathsat.api.msat_create_env(config);
		this.isOpen = true;
	}

	@Override
	protected void closeSolver() throws SolverException {
		Preconditions.checkState(isOpen, "not open yet");
		mathsat.api.msat_destroy_env(env);
		mathsat.api.msat_destroy_config(config);
		this.isOpen = false;
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
				try {
					this.closeSolver();
				} catch (SolverException e) {
					throw new SatisfiabilityException("Cannot close solver.", e);
				}
				return false;
			} else if (status == mathsat.api.MSAT_SAT) {
				try {
					this.closeSolver();
				} catch (SolverException e) {
					throw new SatisfiabilityException("Cannot close solver.", e);
				}
				return true;
			} else {
				try {
					this.closeSolver();
				} catch (SolverException e) {
					throw new SatisfiabilityException("Cannot close solver.", e);
				}
				throw new SatisfiabilityException("Cannot validate formula.");
			}
		} else {
			try {
				this.closeSolver();
			} catch (SolverException e) {
				throw new SatisfiabilityException("Cannot close solver.", e);
			}
			throw new SatisfiabilityException("Cannot assert formula.");
		}
	}

	private SMTLibExpression createSMTLibExpression(Expression expression,
			SMTLibExpression smtExpr) throws SatisfiabilityException {
		try {
			smtExpr = expression.accept(new SMTExpressionConverter(this, env));
		} catch (SolverException e) {
			try {
				this.closeSolver();
			} catch (SolverException ex) {
				throw new SatisfiabilityException("Cannot close solver.", ex);
			}
			throw new SatisfiabilityException(
					"Failed to make variable definition.", e);
		}
		return smtExpr;
	}
}
