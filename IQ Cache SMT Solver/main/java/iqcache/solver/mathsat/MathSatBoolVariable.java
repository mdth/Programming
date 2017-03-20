package iqcache.solver.mathsat;

import iqcache.query.column.Column;
import iqcache.solver.variable.BoolVariable;

/**
 * An implementation of a variable of type bool. This type does not have a size.
 * 
 * @author dinh
 */
class MathSatBoolVariable extends BoolVariable {

	private long boolVar;

	/**
	 * Construct the variable
	 * 
	 * @param column
	 *            the corresponding {@link Column}
	 * @param boolVar
	 *            the bool variable in type long
	 */
	public MathSatBoolVariable(Column column, long boolVar) {
		super(column);
		this.boolVar = boolVar;
	}

	/**
	 * Returns the stored bool variable in type long.
	 * 
	 * @return Returns the bool variable.
	 */
	public long getBoolVariable() {
		return this.boolVar;
	}
}
