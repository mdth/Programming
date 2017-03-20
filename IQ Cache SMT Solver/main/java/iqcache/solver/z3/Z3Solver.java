package iqcache.solver.z3;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import com.microsoft.z3.Z3Exception;

import iqcache.common.Preconditions;
import iqcache.expression.Expression;
import iqcache.satisfiability.SatisfiabilityException;
import iqcache.solver.SMTExpressionConverter;
import iqcache.solver.SMTLibExpression;
import iqcache.solver.SolverException;

/**
 * An instance of a Z3 SMT solver.
 * 
 * @author dinh
 */
public class Z3Solver extends iqcache.solver.Solver {

	private Solver solver;
	private Context ctx;

	static {
		// load the Z3 library.
		System.loadLibrary("Microsoft.Z3");
		System.loadLibrary("libz3");
		System.loadLibrary("libz3java");
	}

	/**
	 * Constructor of a Z3Solver.
	 */
	public Z3Solver() {
		super();
	}

	@Override
	public boolean isSatisfiable(Expression expression)
			throws SatisfiabilityException {
		Preconditions.checkNotNull(expression);
		SMTLibExpression smtExpr = null;

		try {
			openSolver();
		} catch (SolverException e) {
			throw new SatisfiabilityException("Cannot open solver.", e);
		}

		smtExpr = createSMTLibExpression(expression, smtExpr);
		BoolExpr formula = smtExpr.getZ3Expression().getBoolExpr();
		assertFormula(formula);
		try {
			formula.Dispose();
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
		return checkFormula();
	}

	private boolean checkFormula() throws SatisfiabilityException {
		Status status = null;
		try {
			status = this.solver.Check();
		} catch (Z3Exception e) {
			try {
				closeSolver();
			} catch (SolverException ex) {
				throw new SatisfiabilityException(
						"Cannot create variable definitions.", ex);
			}
			throw new SatisfiabilityException("Cannot validate formula.", e);
		}

		boolean b = false;

		if (status == Status.UNSATISFIABLE) {
			b = false;

		} else if (status == Status.SATISFIABLE) {
			b = true;

		} else {
			try {
				this.closeSolver();
			} catch (SolverException e) {
				throw new SatisfiabilityException("Cannot close solver.", e);
			}
			throw new SatisfiabilityException("UNSAT. Cannot validate formula.");
		}

		try {
			this.closeSolver();
		} catch (SolverException e) {
			throw new SatisfiabilityException("Cannot close solver.", e);
		}
		return b;
	}

	private void assertFormula(BoolExpr formula) throws SatisfiabilityException {
		try {
			this.solver.Assert(formula);
		} catch (Z3Exception e) {
			try {
				closeSolver();
			} catch (SolverException ex) {
				throw new SatisfiabilityException(
						"Cannot create variable definitions.", ex);
			}
			throw new SatisfiabilityException("Cannot assert formula", e);
		}
	}

	private SMTLibExpression createSMTLibExpression(Expression expression,
			SMTLibExpression smtExpr) throws SatisfiabilityException {
		try {
			smtExpr = expression.accept(new SMTExpressionConverter(this, ctx));
		} catch (SolverException e1) {
			try {
				closeSolver();
			} catch (SolverException e) {
				throw new SatisfiabilityException(
						"Cannot create variable definitions.", e);
			}
			throw new SatisfiabilityException("Cannot open solver.", e1);
		}
		return smtExpr;
	}

	@Override
	protected void resetSolver() throws SolverException {
		Preconditions.checkState(isOpen,
				"cannot reset, because solver is not open yet");
		try {
			this.solver.Reset();
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void closeSolver() throws SolverException {
		Preconditions.checkState(isOpen, "solver is not open");
		this.ctx.Dispose();
		this.isOpen = false;
	}

	@Override
	protected void openSolver() throws SolverException {
		Preconditions.checkState(!isOpen, "already open");
		try {
			this.ctx = new Context();
			this.solver = ctx.MkSolver();
			this.isOpen = true;
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
	}
}
