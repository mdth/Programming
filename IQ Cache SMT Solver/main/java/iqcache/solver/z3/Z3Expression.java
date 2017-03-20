/**
 * 
 */
package iqcache.solver.z3;

import com.microsoft.z3.BoolExpr;

/**
 * A MathSatExpression encapsulates an expression the solver MathSAT is using.
 * 
 * @author dinh
 * 
 */
public class Z3Expression {

	private BoolExpr boolExpr;

	/**
	 * Constructor of a Z3Expression.
	 * 
	 * @param boolExpr
	 *            the to be stored {@link BoolExpr}
	 */
	public Z3Expression(BoolExpr boolExpr) {
		this.boolExpr = boolExpr;
	}

	/**
	 * Returns the stored {@link BoolExpr}.
	 * 
	 * @return Returns the stored {@link BoolExpr}.
	 */
	public BoolExpr getBoolExpr() {
		return this.boolExpr;
	}

}
