package iqcache.solver;

import iqcache.query.column.Column;
import iqcache.solver.variable.BitvectorVariable;
import iqcache.solver.variable.IntVariable;
import iqcache.solver.variable.RealVariable;

/**
 * An interface that combines important API functions for SMT solver that use
 * SMT-LIB 2.0.
 * 
 * @author dinh
 */
public abstract class SolverAPI {

	/**
	 * Constructor of an SolverAPI.
	 */
	protected SolverAPI() {
	}

	/**
	 * This method creates a variable of the type bool.
	 * 
	 * @param col
	 *            the {@link Column} of the variable
	 * @return Returns a {@link SMTLibExpression}.
	 */
	protected abstract SMTLibExpression createBoolVariable(Column col);

	/**
	 * This method creates a variable of the type int.
	 * 
	 * @param col
	 *            the {@link Column} of the variable
	 * @return Returns a variable of type int..
	 */
	protected abstract IntVariable createIntVariable(Column col);

	/**
	 * This method creates a variable of the type int.
	 * 
	 * @param col
	 *            the {@link Column} of the variable, is <b>null</b>.
	 * @param value
	 *            the value of the variable
	 * @return Returns a constant variable of type int.
	 */
	protected abstract IntVariable createIntNum(Column col, int value);

	/**
	 * This method creates a variable of the type real.
	 * 
	 * @param col
	 *            the {@link Column} of the variable
	 * @return Returns a variable of type real.
	 */
	protected abstract RealVariable createRealVariable(Column col);

	/**
	 * This method creates a variable of the type real.
	 * 
	 * @param col
	 *            the {@link Column} of the variable, is <b>null</b>
	 * @param value
	 *            the value of the variable
	 * @return Returns a constant variable of type real.
	 */
	protected abstract RealVariable createRealNum(Column col, double value);

	/**
	 * This method creates a {@link SMTLibExpression} in the form leftVariable = rightVariable.
	 * Both variables must be variables of the type int.
	 * 
	 * @param leftVariable
	 *            the first int variable
	 * @param rightVariable
	 *            the second int variable
	 * @return Returns a {@link SMTLibExpression} in the form leftVariable = rightVariable.
	 */
	protected abstract SMTLibExpression createEqual(IntVariable leftVariable,
			IntVariable rightVariable);

	/**
	 * This method creates a {@link SMTLibExpression} in the form leftVariable = rightVariable.
	 * Both variables must be variables of the type real.
	 * 
	 * @param leftVariable
	 *            the first real variable
	 * @param rightVariable
	 *            the second real variable
	 * @return Returns a {@link SMTLibExpression} in the form leftVariable = rightVariable.
	 */
	protected abstract SMTLibExpression createEqual(RealVariable leftVariable,
			RealVariable rightVariable);

	/**
	 * This method creates a {@link SMTLibExpression} in the form leftVariable = rightVariable.
	 * Both variables must be variables of the type bitvector.
	 * 
	 * @param leftVariable
	 *            the first bitvector variable
	 * @param rightVariable
	 *            the second bitvector variable
	 * @return Returns a {@link SMTLibExpression} in the form leftVariable = rightVariable.
	 */
	protected abstract SMTLibExpression createEqual(BitvectorVariable leftVariable,
			BitvectorVariable rightVariable);

	/**
	 * This method creates a {@link SMTLibExpression} in the form leftVariable != rightVariable.
	 * Both variables must be variables of the type int.
	 * 
	 * @param leftVariable
	 *            the first int variable
	 * @param rightVariable
	 *            the second int variable
	 * @return Returns a {@link SMTLibExpression} in the form leftVariable != rightVariable.
	 */
	protected abstract SMTLibExpression createNotEqual(IntVariable leftVariable,
			IntVariable rightVariable);

	/**
	 * This method creates a {@link SMTLibExpression} in the form leftVariable != rightVariable.
	 * Both variables must be variables of the type real.
	 * 
	 * @param leftVariable
	 *            the first real variable
	 * @param rightVariable
	 *            the second real variable
	 * @return Returns a {@link SMTLibExpression} in the form leftVariable != rightVariable.
	 */
	protected abstract SMTLibExpression createNotEqual(RealVariable leftVariable,
			RealVariable rightVariable);

	/**
	 * This method creates a {@link SMTLibExpression} in the form leftVariable != rightVariable.
	 * Both variables must be variables of the type bitvector.
	 * 
	 * @param leftVariable
	 *            the first bitvector variable
	 * @param rightVariable
	 *            the second bitvector variable
	 * @return Returns a {@link SMTLibExpression} in the form leftVariable != rightVariable.
	 */
	protected abstract SMTLibExpression createNotEqual(BitvectorVariable leftVariable,
			BitvectorVariable rightVariable);

	/**
	 * This method creates a {@link SMTLibExpression} that combines an array of
	 * {@link SMTLibExpression}s with logical ANDs.
	 * 
	 * @param expr
	 *            array of {@link SMTLibExpression}s
	 * @return Returns a SMTLibExpression in the form expr1 AND expr2 AND ... .
	 */
	protected abstract SMTLibExpression createAND(SMTLibExpression... expr);

	/**
	 * This method creates a {@link SMTLibExpression} that combines an array of
	 * {@link SMTLibExpression}s with logical ORs.
	 * 
	 * @param expr
	 *            array of {@link SMTLibExpression}s
	 * @return Returns a {@link SMTLibExpression} in the form expr1 OR expr2 OR
	 *         ... .
	 */
	protected abstract SMTLibExpression createOR(SMTLibExpression... expr);

	/**
	 * This method negates a {@link SMTLibExpression}.
	 * 
	 * @param expr
	 *            the {@link SMTLibExpression} that should be negated
	 * @return Returns a negated {@link SMTLibExpression}.
	 */
	protected abstract SMTLibExpression createNOT(SMTLibExpression... expr);

	/**
	 * This method creates a {@link SMTLibExpression} in the form leftVariable <= rightVariable.
	 * Both variables must be variables of the type int. In this case the
	 * constant c is 0.
	 * 
	 * @param leftVariable
	 *            the first int variable
	 * @param rightVariable
	 *            the second int variable
	 * @return Returns a {@link SMTLibExpression} in the form leftVariable <= rightVariable.
	 */
	protected abstract SMTLibExpression createLE(IntVariable leftVariable,
			IntVariable rightVariable);

	/**
	 * This method creates a {@link SMTLibExpression} in the form leftVariable <= rightVariable.
	 * Both variables must be variables of the type real. In this case the
	 * constant c is 0.
	 * 
	 * @param leftVariable
	 *            the first real variable
	 * @param rightVariable
	 *            the second real variable
	 * @return Returns a {@link SMTLibExpression} in the form leftVariable <= rightVariable.
	 */
	protected abstract SMTLibExpression createLE(RealVariable leftVariable,
			RealVariable rightVariable);

	/**
	 * This method creates a {@link SMTLibExpression} in the form leftVariable <= rightVariable.
	 * Both variables must be variables of the type bitvector.
	 * 
	 * @param leftVariable
	 *            the first bitvector variable
	 * @param rightVariable
	 *            the second bitvector variable
	 * @return Returns a {@link SMTLibExpression} in the form leftVariable <= rightVariable.
	 */
	protected abstract SMTLibExpression createLE(BitvectorVariable leftVariable,
			BitvectorVariable rightVariable);

	/**
	 * This method creates a {@link SMTLibExpression} in the form leftVariable <= rightVariable +
	 * c. All variables must be variables of the type int. In this case the
	 * constant c != 0.
	 * 
	 * @param leftVariable
	 *            the first int variable
	 * @param rightVariable
	 *            the second int variable
	 * @param constant
	 *            the constant int variable on the right side of the term
	 * @return Returns a {@link SMTLibExpression} in the form leftVariable <= rightVariable + c.
	 */
	protected abstract SMTLibExpression createLEc(IntVariable leftVariable,
			IntVariable rightVariable, IntVariable c);

	/**
	 * This method creates a {@link SMTLibExpression} in the form leftVariable <= rightVariable +
	 * c. Both variables must be variables of the type real. In this case the
	 * constant c != 0.
	 * 
	 * @param leftVariable
	 *            the first real variable
	 * @param rightVariable
	 *            the second real variable
	 * @param constant
	 *            the constant real variable on the right side of the term
	 * @return Returns a {@link SMTLibExpression} in the form leftVariable <= rightVariable + c.
	 */
	protected abstract SMTLibExpression createLEc(RealVariable leftVariable,
			RealVariable rightVariable, RealVariable constant);

	/**
	 * This method creates a {@link SMTLibExpression} in the form leftVariable < rightVariable.
	 * All variables must be variables of the type real. In this case the
	 * constant c = 0.
	 * 
	 * @param leftVariable
	 *            the first real variable
	 * @param rightVariable
	 *            the second real variable
	 * @return Returns a {@link SMTLibExpression} in the form leftVariable < rightVariable.
	 */
	protected abstract SMTLibExpression createLT(RealVariable leftVariable,
			RealVariable rightVariable);

	/**
	 * This method creates a {@link SMTLibExpression} in the form leftVariable < rightVariable.
	 * Both variables must be variables of the type bitvector.
	 * 
	 * @param leftVariable
	 *            the first bitvector variable
	 * @param rightVariable
	 *            the second bitvector variable
	 * @return Returns a {@link SMTLibExpression} in the form leftVariable < rightVariable.
	 */
	protected abstract SMTLibExpression createLT(
			BitvectorVariable leftVariable, BitvectorVariable rightVariable);

	/**
	 * This method creates a {@link SMTLibExpression} in the form leftVariable < rightVariable +
	 * c. All variables must be variables of the type real. In this case the
	 * constant c != 0.
	 * 
	 * @param leftVariable
	 *            the first real variable
	 * @param rightVariable
	 *            the second real variable
	 * @param constant
	 *            the constant real variable on the right side of the term
	 * @return Returns a {@link SMTLibExpression} in the form leftVariable < rightVariable + c.
	 */
	protected abstract SMTLibExpression createLTc(RealVariable leftVariable,
			RealVariable rightVariable, RealVariable constant);

	/**
	 * This method creates a {@link SMTLibExpression} in the form of a logical
	 * <b>true</b> or <b>false</b>.
	 * 
	 * @param value
	 *            <b>true</b> or <b>false</b>
	 * @return Returns a {@link SMTLibExpression} in the form of <b>true</b> or
	 *         <b>false</b>.
	 */
	protected abstract SMTLibExpression createBooleanValue(boolean value);

	/**
	 * This method creates a {@link SMTLibExpression} in the form leftVariable != rightVariable +
	 * c. All variables must be variables of the type real. In this case the
	 * constant c != 0.
	 * 
	 * @param leftVariable
	 *            the first real variable
	 * @param rightVariable
	 *            the second real variable
	 * @param constant
	 *            the constant on the right side of the term
	 * @return Returns a {@link SMTLibExpression} in the form leftVariable != rightVariable + c.
	 */
	protected abstract SMTLibExpression createNotEqualc(RealVariable leftVariable,
			RealVariable rightVariable, RealVariable constant);

	/**
	 * This method creates a {@link SMTLibExpression} in the form leftVariable = rightVariable +
	 * c. All variables must be variables of the type real. In this case the
	 * constant c != 0.
	 * 
	 * @param leftVariable
	 *            the first real variable
	 * @param rightVariable
	 *            the second real variable
	 * @param constant
	 *            the constant on the right side of the term
	 * @return Returns a {@link SMTLibExpression} in the form leftVariable = rightVariable + c.
	 */
	protected abstract SMTLibExpression createEqualc(RealVariable leftVariable,
			RealVariable rightVariable, RealVariable constant);

	/**
	 * This method creates a {@link SMTLibExpression} in the form leftVariable = rightVariable +
	 * c. All variables must be variables of the type int. In this case the
	 * constant c != 0.
	 * 
	 * @param leftVariable
	 *            the first int variable
	 * @param rightVariable
	 *            the second int variable
	 * @param constant
	 *            the constant on the right side of the term
	 * @return Returns a {@link SMTLibExpression} in the form leftVariable = rightVariable + c.
	 */
	protected abstract SMTLibExpression createEqualc(IntVariable leftVariable,
			IntVariable rightVariable, IntVariable constant);

	/**
	 * This method creates a {@link SMTLibExpression} in the form leftVariable != rightVariable +
	 * c. All variables must be variables of the type int. In this case the
	 * constant c != 0.
	 * 
	 * @param leftVariable
	 *            the first int variable
	 * @param rightVariable
	 *            the second int variable
	 * @param constant
	 *            the constant on the right side of the term
	 * @return Returns a {@link SMTLibExpression} in the form leftVariable != rightVariable + c.
	 */
	protected abstract SMTLibExpression createNotEqualc(IntVariable leftVariable,
			IntVariable rightVariable, IntVariable constant);

	/**
	 * This method creates a bitvector variable with a numeral interpretation.
	 * 
	 * @param col
	 *            {@link Column} of the variable, is <b>null</b>
	 * @param string
	 *            the numeral interpretation of the bitvector
	 * @param size
	 *            the size in bits of the bitvector
	 * @return A bitvector variable.
	 */
	protected abstract BitvectorVariable createBitvectorString(Column col,
			String string, int size);

	/**
	 * This method creates a bitvector variable with a specific size (in bits).
	 * 
	 * @param col
	 *            {@link Column} of the variable
	 * @param size
	 *            the size in bits of the bitvector
	 * @return A bitvector variable.
	 */
	protected abstract BitvectorVariable createBitvectorVariable(Column col,
			int size);

	/**
	 * This method converts a {@link SMTLibExpression} into a String in SMTLib
	 * 2.0 format.
	 * 
	 * @param expr
	 *            the to converted {@link SMTLibExpression}
	 * @return A string in SMTLib 2.0 format.
	 */
	protected abstract String toSMTLib2(SMTLibExpression expr);

	/**
	 * This method extends a bitvector with a zero bitvector (on the right side)
	 * with a given size.
	 * 
	 * @param bVar
	 *            bitvector to extend
	 * @param bitsToExtend
	 *            size of bits the bitvector should be extended with
	 * @return A bitvector with zero extension on the right side.
	 */
	protected abstract BitvectorVariable makeBitvectorZeroExtension(
			BitvectorVariable bVar, int bitsToExtend);
}