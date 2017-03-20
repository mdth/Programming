package iqcache.solver.z3;

import com.microsoft.z3.RealExpr;

import iqcache.common.Preconditions;
import iqcache.query.column.Column;
import iqcache.solver.variable.RealVariable;

/**
 * An implementation of a variable of type real. This type does not have a
 * size.
 * 
 * @author dinh
 */
class Z3RealVariable extends RealVariable {

	private RealExpr realExpr;

	/**
	 * Construct the variable
	 * 
	 * @param column
	 *            the corresponding {@link Column}
	 * @param realExpr
	 *            the {@link RealExpr}, {@code realExpr != null}
	 */
	public Z3RealVariable(Column column, RealExpr realExpr) {
		super(column);
		Preconditions.checkNotNull(realExpr);
		this.realExpr = realExpr;
	}

	/**
	 * Returns the stored {@link RealExpr}.
	 * 
	 * @return The stored {@link RealExpr}.
	 */
	public RealExpr getRealExpr() {
		return this.realExpr;
	}
}
