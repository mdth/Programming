package iqcache.solver.mathsat;

import iqcache.query.column.Column;
import iqcache.solver.variable.IntVariable;

/**
 * An implementation of a variable of type int. This type does not have a size.
 * 
 * @author dinh
 */
public class MathSatIntVariable extends IntVariable {

	private long intVar;

	/**
	 * Construct the variable
	 * 
	 * @param column
	 *            the corresponding {@link Column}
	 * @param intVar
	 *            integer Variable in type long
	 */
	public MathSatIntVariable(Column column, long intVar) {
		super(column);
		this.intVar = intVar;
	}

	/**
	 * This methods gets the saved integer Variable.
	 * 
	 * @return Returns the integer Variable in type long.
	 */
	public long getIntVar() {
		return this.intVar;
	}
}
