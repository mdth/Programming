package iqcache.solver;

/**
 * A {@link SolverException} signals a failure condition within a solver.
 * 
 * @author dinh
 */
public class SolverException extends Exception {

	private static final long serialVersionUID = -2963746297371945495L;

	/**
	 * Constructor
	 * 
	 * @param message
	 *            error message
	 */
	public SolverException(String message) {
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param message
	 *            error message
	 * @param cause
	 *            cause of the exception
	 */
	public SolverException(String message, Throwable cause) {
		super(message, cause);
	}
}
