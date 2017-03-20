package iqcache.solver.z3;

import com.microsoft.z3.IntExpr;
import iqcache.common.Preconditions;
import iqcache.query.column.Column;
import iqcache.solver.variable.IntVariable;

/**
 * An implementation of a variable of type int. This type does not have a size.
 * 
 * @author dinh
 */
class Z3IntVariable extends IntVariable {

	private IntExpr intExpr;

	/**
	 * Constructor of a variable of the type int.
	 * 
	 * @param column
	 *            the corresponding {@link Column}
	 * @param intExpr
	 *            the to be stored {@link IntExpr} for Z3 ,
	 *            {@code intExpr != null}
	 */
	public Z3IntVariable(Column column, IntExpr intExpr) {
		super(column);
		Preconditions.checkNotNull(intExpr);
		this.intExpr = intExpr;
	}

	/**
	 * This method gets the {@link IntExpr} of this variable.
	 * 
	 * @return The saved {@link IntExpr}.
	 */
	public IntExpr getIntExpr() {
		return this.intExpr;
	}
}
