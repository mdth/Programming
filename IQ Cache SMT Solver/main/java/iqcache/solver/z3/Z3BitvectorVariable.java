package iqcache.solver.z3;

import com.microsoft.z3.BitVecExpr;

import iqcache.common.Preconditions;
import iqcache.query.column.Column;
import iqcache.solver.variable.BitvectorVariable;

/**
 * An implementation of a variable of type bitvector. The size of this type is
 * the number of bits of the bitvector (its length).
 * 
 * @author dinh
 */
class Z3BitvectorVariable extends BitvectorVariable {

	private BitVecExpr bitVecExpr;

	/**
	 * Constructor of the variable.
	 * 
	 * @param column
	 *            the corresponding {@link Column}
	 * @param bitVecExpr
	 *            the to be stored {@link BitVecExpr}, {@code size != null}
	 * @param size
	 *            the size of the bitvector, {@code size > 0}
	 */
	public Z3BitvectorVariable(Column column, BitVecExpr bitVecExpr, int size) {
		super(column, size);
		Preconditions.checkNotNull(bitVecExpr);
		this.bitVecExpr = bitVecExpr;
	}

	/**
	 * Returns the stored bitvector expression.
	 * 
	 * @return Returns the stored bitvector expression.
	 */
	public BitVecExpr getBitvectorExpr() {
		return this.bitVecExpr;
	}
}