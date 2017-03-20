package iqcache.solver.mathsat;

/**
 * A MathSatExpression encapsulates an expression the solver MathSAT is using.
 * 
 * @author dinh
 * 
 */
public class MathSatExpression {

	private long msatExpr;

	/**
	 * Constructor of a MathSatExpression.
	 * 
	 * @param msatExpr
	 *            the MathSAT expression to be encapsulated.
	 */
	public MathSatExpression(long msatExpr) {
		this.msatExpr = msatExpr;
	}

	/**
	 * Returns the stored MathSAT expression.
	 * 
	 * @return Returns the stored MathSAT expression.
	 */
	public long getMsatExpr() {
		return this.msatExpr;
	}

}