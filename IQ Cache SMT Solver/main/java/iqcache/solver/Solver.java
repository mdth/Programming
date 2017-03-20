package iqcache.solver;

import java.math.BigInteger;
import java.nio.charset.Charset;

import iqcache.common.Preconditions;
import iqcache.expression.Expression;
import iqcache.query.column.Column;
import iqcache.satisfiability.SatisfiabilityChecker;
import iqcache.satisfiability.SatisfiabilityException;

/**
 * Abstract solver.
 * 
 * @author dinh
 */
public abstract class Solver extends SatisfiabilityChecker {

	public final static int BITS_PER_CHARACTER = 16;

	public final static int BIT_CUT_LENGTH_FOR_UMLAUTS = 24;

	/**
	 * Default Charset for conversions (string to bit vector).
	 */
	public final static Charset CHARSET = Charset.forName("UTF-8");
	/**
	 * Contains the actual context/environment state (i.e. <code>true</code> iff
	 * there is a context/environment available).
	 */
	protected boolean isOpen;

	/**
	 * Constructor of a SMT Solver.
	 */
	protected Solver() {
		this.isOpen = false;
	}

	/**
	 * This method returns the full {@link Column}name for a specific variable.
	 * 
	 * @param col
	 *            {@link Column}, which name should be returned
	 * @return A full {@link Column}name for a specific variable.
	 */
	public static String getVariableName(Column col) {
		Preconditions.checkNotNull(col);
		return col.getTableName().name + "." + col.getName().name;
	}

	/**
	 * This method converts a given String into a bitvector representation for
	 * MathSAT.
	 * 
	 * @param s
	 *            the String to convert
	 * @param charset
	 *            the {@link Charset} used to convert the String
	 * @return The bitvector representation of the given String s.
	 */
	public static String toBinaryBitVector(String s, Charset charset) {
		Preconditions.checkNotNull(s);
		Preconditions.checkNotNull(charset);

		final StringBuffer sb = new StringBuffer();
		final String zero = "0";

		final byte[] bytes = s.getBytes(charset);
		for (final byte b : bytes) {
			String bitrep = Integer.toBinaryString(b);
			if (bitrep.length() < BITS_PER_CHARACTER) {
				// pad the byte
				final int pad = BITS_PER_CHARACTER - bitrep.length();
				for (int i = 0; i < pad; i++) {
					sb.append(zero);
				}
			} else if (bitrep.length() > BITS_PER_CHARACTER) {
				// cut off the bit representation for umlauts at the beginning
				// (because of the two's complement)
				bitrep = String.valueOf(bitrep.toCharArray(),
						BIT_CUT_LENGTH_FOR_UMLAUTS, BITS_PER_CHARACTER / 2);
			}
			sb.append(bitrep);
		}
		return sb.toString();
	}

	/**
	 * This method converts a given String into a bitvector representation for
	 * Z3.
	 * 
	 * @param s
	 *            the String to convert
	 * @param charset
	 *            the {@link Charset} used to convert the String
	 * @return The bitvector representation of the given String s.
	 */
	public static String toDecimalBitVector(String s, Charset charset) {
		final String zero = "0";
		String string = toBinaryBitVector(s, charset);
		BigInteger bInteger = new BigInteger(zero);
		
		for (int i = string.length() - 1; i >= 0; i--) {
			String temp = "" + string.charAt(i);
			BigInteger tempInt = new BigInteger(temp);
			
			// calculate decimal value
			BigInteger pow = BigInteger.valueOf(2).pow(
					(string.length() - i) - 1);
			BigInteger bInt = tempInt.multiply(pow);
			bInteger = bInteger.add(bInt);
		}
		return bInteger.toString();
	}

	@Override
	public abstract boolean isSatisfiable(Expression expression)
			throws SatisfiabilityException;

	/**
	 * This method resets the Solver.
	 * 
	 * @throws SolverException
	 *             if the resetting the solver fails.
	 */
	protected abstract void resetSolver() throws SolverException;

	/**
	 * Open the solver.
	 * 
	 * @throws SolverException
	 *             if opening the solver fails.
	 */
	protected abstract void openSolver() throws SolverException;

	/**
	 * Close the solver.
	 * 
	 * @throws SolverException
	 *             if closing the solver fails.
	 */
	protected abstract void closeSolver() throws SolverException;
}
