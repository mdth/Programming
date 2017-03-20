package iqcache.solver;

import iqcache.common.Preconditions;
import iqcache.solver.mathsat.MathSatExpression;
import iqcache.solver.variable.BoolVariable;
import iqcache.solver.z3.Z3Expression;

/**
 * Expression in SMT-Lib format.
 * 
 * @author dinh
 */
public class SMTLibExpression {

	private Z3Expression z3Expr;
	private MathSatExpression mathSatExpr;

	/**
	 * Constructor of SMTLibExpression containing a {@link Z3Expression}.
	 * 
	 * @param expr
	 *            containing {@link Z3Expression}
	 */
	public SMTLibExpression(Z3Expression expr) {
		Preconditions.checkNotNull(expr);
		this.z3Expr = expr;
		this.mathSatExpr = null;
	}

	/**
	 * Constructor of SMTLibExpression containing a {@link MathSatExpression}.
	 * 
	 * @param expr
	 *            containing {@link MathSatExpression}
	 */
	public SMTLibExpression(MathSatExpression expr) {
		Preconditions.checkNotNull(expr);
		this.z3Expr = null;
		this.mathSatExpr = expr;
	}

	/**
	 * Constructor of SMTLibExpression containing a {@link BoolVariable}.
	 * 
	 * @param expr
	 *            containing {@link BoolVariable}
	 */
	public SMTLibExpression(BoolVariable boolVar) {
		Preconditions.checkNotNull(boolVar);
		this.z3Expr = null;
		this.mathSatExpr = null;
	}

	/**
	 * This methods gets the stored {@link Z3Expression}.
	 * 
	 * @return Returns the stored {@link Z3Expression}.
	 */
	public Z3Expression getZ3Expression() {
		return this.z3Expr;
	}

	/**
	 * This methods gets the stored {@link MathSatExpression}.
	 * 
	 * @return Returns the stored {@link MathSatExpression}.
	 */
	public MathSatExpression getMathSatExpression() {
		return this.mathSatExpr;
	}
}
