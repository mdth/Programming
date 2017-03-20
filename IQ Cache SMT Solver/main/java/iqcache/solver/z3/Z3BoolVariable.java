package iqcache.solver.z3;

import com.microsoft.z3.BoolExpr;

import iqcache.common.Preconditions;
import iqcache.query.column.Column;
import iqcache.solver.variable.BoolVariable;

/**
 * An implementation of a variable of type bool. This type does not have a size.
 * 
 * @author dinh
 */
class Z3BoolVariable extends BoolVariable {

	private BoolExpr boolExpr;

	/**
	 * Construct the variable
	 * 
	 * @param column
	 *            the corresponding {@link Column}
	 * @param boolExpr
	 *            the to be stored {@link BoolExpr}, {@code boolExpr != null}
	 */
	public Z3BoolVariable(Column column, BoolExpr boolExpr) {
		super(column);
		Preconditions.checkNotNull(boolExpr);
		this.boolExpr = boolExpr;
	}

	/**
	 * This methods gets the saved boolean Expression.
	 * 
	 * @return Returns the boolean Expression in type {@link BoolExpr}.
	 */
	public BoolExpr getBoolExpr() {
		return this.boolExpr;
	}
}
