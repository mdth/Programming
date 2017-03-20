package iqcache.solver.mathsat;

import iqcache.query.column.Column;
import iqcache.solver.variable.BitvectorVariable;

/**
 * An implementation of a variable of type bitvector. The size of this type is
 * the number of bits of the bitvector (its length).
 * 
 * @author dinh
 */
class MathSatBitvectorVariable extends BitvectorVariable {

	private final long bitvectorVar;

	/**
	 * Construct the variable.
	 * 
	 * @param column
	 *            the corresponding {@link Column} or <b>null</b> if there is
	 *            none
	 * @param size
	 *            the size of the bitvector, {@code size > 0}
	 * @throws NullPointerException
	 *             iff {@code column == null}
	 */
	public MathSatBitvectorVariable(Column column, int size, long bitvectorVar) {
		super(column, size);
		this.bitvectorVar = bitvectorVar;
	}

	/**
	 * This method returns the bit vector variable.
	 * 
	 * @return Returns the bit vector in type long.
	 */
	public long getBitvectorVar() {
		return this.bitvectorVar;
	}
}
