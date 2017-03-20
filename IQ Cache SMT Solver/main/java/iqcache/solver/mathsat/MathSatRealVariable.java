package iqcache.solver.mathsat;

import iqcache.query.column.Column;
import iqcache.solver.variable.RealVariable;

/**
 * An implementation of a variable of type real. This type does not have a size.
 * 
 * @author dinh
 */
class MathSatRealVariable extends RealVariable {

	private final long realVar;

	/**
	 * Construct the variable
	 * 
	 * @param column
	 *            the corresponding {@link Column}
	 */
	public MathSatRealVariable(Column column, long realVar) {
		super(column);
		this.realVar = realVar;
	}

	/**
	 * This methods gets the saved real Variable.
	 * 
	 * @return Returns the real Variable in type long.
	 */
	public long getRealVar() {
		return this.realVar;
	}
}
