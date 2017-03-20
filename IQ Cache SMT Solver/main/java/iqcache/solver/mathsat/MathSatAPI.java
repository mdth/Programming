package iqcache.solver.mathsat;

import iqcache.common.Preconditions;
import iqcache.query.column.Column;
import iqcache.solver.SMTLibExpression;
import iqcache.solver.Solver;
import iqcache.solver.SolverAPI;
import iqcache.solver.variable.BitvectorVariable;
import iqcache.solver.variable.IntVariable;
import iqcache.solver.variable.RealVariable;

/**
 * This class represents the MathSAT 5 API.
 * 
 * @author dinh
 */
public class MathSatAPI extends SolverAPI {

	private long env;

	/**
	 * Constructor for the MathSatAPI.
	 * 
	 * @param env
	 *            the needed environment for the MathSatAPI
	 */
	public MathSatAPI(long env) {
		if (!mathsat.api.MSAT_ERROR_ENV(env)) {
			this.env = env;
		}
	}

	@Override
	protected SMTLibExpression createBoolVariable(Column col) {
		Preconditions.checkNotNull(col);
		long boolType = mathsat.api.msat_get_bool_type(env);
		long boolVarTemp = mathsat.api.msat_declare_function(env,
				Solver.getVariableName(col), boolType);
		checkDeclaration(boolVarTemp);
		long boolVar = mathsat.api.msat_make_constant(env, boolVarTemp);
		return new SMTLibExpression(new MathSatExpression(boolVar));
	}

	private void checkDeclaration(long declaration) {
		assert (!mathsat.api.MSAT_ERROR_DECL(declaration));
	}

	@Override
	protected IntVariable createIntVariable(Column col) {
		Preconditions.checkNotNull(col);
		long intType = mathsat.api.msat_get_integer_type(env);
		long intVarTemp = mathsat.api.msat_declare_function(env,
				Solver.getVariableName(col), intType);
		checkDeclaration(intVarTemp);
		long intVar = mathsat.api.msat_make_constant(env, intVarTemp);
		return new MathSatIntVariable(col, intVar);
	}

	@Override
	protected IntVariable createIntNum(Column col, int value) {
		String intValue = "" + value;
		long intVar = mathsat.api.msat_make_number(env, intValue);
		return new MathSatIntVariable(col, intVar);
	}

	@Override
	protected RealVariable createRealVariable(Column col) {
		Preconditions.checkNotNull(col);
		long realType = mathsat.api.msat_get_integer_type(env);
		long realVarTemp = mathsat.api.msat_declare_function(env,
				Solver.getVariableName(col), realType);
		checkDeclaration(realVarTemp);
		long realVar = mathsat.api.msat_make_constant(env, realVarTemp);
		return new MathSatRealVariable(col, realVar);
	}

	@Override
	protected RealVariable createRealNum(Column col, double value) {
		String realValue = "" + value;
		long realVar = mathsat.api.msat_make_number(env, realValue);
		return new MathSatRealVariable(col, realVar);
	}

	@Override
	protected BitvectorVariable createBitvectorVariable(Column col, int size) {
		Preconditions.checkNotNull(col);
		Preconditions.checkArgument(size > 0);
		long bitVecType = mathsat.api.msat_get_bv_type(env, size);
		long bitVecVarTemp = mathsat.api.msat_declare_function(env,
				Solver.getVariableName(col), bitVecType);
		checkDeclaration(bitVecVarTemp);
		long bitVecVar = mathsat.api.msat_make_constant(env, bitVecVarTemp);
		return new MathSatBitvectorVariable(col, size, bitVecVar);
	}

	@Override
	protected SMTLibExpression createEqual(IntVariable leftVariable, IntVariable rightVariable) {
		MathSatIntVariable mathSatVar1 = (MathSatIntVariable) leftVariable;
		MathSatIntVariable mathSatVar2 = (MathSatIntVariable) rightVariable;
		long newIntVar = mathsat.api.msat_make_equal(env,
				mathSatVar1.getIntVar(), mathSatVar2.getIntVar());
		checkTerm(newIntVar);
		MathSatExpression msatExpr = new MathSatExpression(newIntVar);
		return new SMTLibExpression(msatExpr);
	}

	@Override
	protected SMTLibExpression createEqual(RealVariable leftVariable, RealVariable rightVariable) {
		MathSatRealVariable mathSatVar1 = (MathSatRealVariable) leftVariable;
		MathSatRealVariable mathSatVar2 = (MathSatRealVariable) rightVariable;
		long newRealVar = mathsat.api.msat_make_equal(env,
				mathSatVar1.getRealVar(), mathSatVar2.getRealVar());
		checkTerm(newRealVar);
		MathSatExpression msatExpr = new MathSatExpression(newRealVar);
		return new SMTLibExpression(msatExpr);
	}

	@Override
	protected SMTLibExpression createEqual(BitvectorVariable leftVariable,
			BitvectorVariable rightVariable) {

		int leftVarSize = leftVariable.getSize();
		int rightVarSize = rightVariable.getSize();

		// left variable has more bits -> right variable needs zero
		// extension
		if (leftVarSize > rightVarSize) {
			rightVariable = makeBitvectorZeroExtension(rightVariable, leftVarSize - rightVarSize);

			// right variable has more bits -> left variable needs zero
			// extension
		} else if (leftVarSize < rightVarSize) {
			leftVariable = makeBitvectorZeroExtension(leftVariable, rightVarSize - leftVarSize);
		} else {
			// do nothing
		}

		MathSatBitvectorVariable mathSatVar1 = (MathSatBitvectorVariable) leftVariable;
		MathSatBitvectorVariable mathSatVar2 = (MathSatBitvectorVariable) rightVariable;

		long newBitVecVar = mathsat.api.msat_make_equal(env,
				mathSatVar1.getBitvectorVar(), mathSatVar2.getBitvectorVar());
		checkTerm(newBitVecVar);

		MathSatExpression msatExpr = new MathSatExpression(newBitVecVar);
		return new SMTLibExpression(msatExpr);
	}

	private void checkTerm(long term) {
		assert (!mathsat.api.MSAT_ERROR_TERM(term));
	}

	@Override
	protected SMTLibExpression createNotEqual(IntVariable leftVariable, IntVariable rightVariable) {
		return createNOT(createEqual(leftVariable, rightVariable));
	}

	@Override
	protected SMTLibExpression createNotEqual(RealVariable leftVariable,
			RealVariable rightVariable) {
		return createNOT(createEqual(leftVariable, rightVariable));
	}

	@Override
	protected SMTLibExpression createNotEqual(BitvectorVariable leftVariable,
			BitvectorVariable rightVariable) {
		return createNOT(createEqual(leftVariable, rightVariable));
	}

	@Override
	protected SMTLibExpression createAND(SMTLibExpression... expr) {
		int length = expr.length;
		MathSatExpression[] msatExpr = new MathSatExpression[length];

		for (int i = 0; i < length; i++) {
			msatExpr[i] = expr[i].getMathSatExpression();
		}

		if (length == 2) {
			long msatTerm = mathsat.api.msat_make_and(env,
					msatExpr[0].getMsatExpr(), msatExpr[1].getMsatExpr());
			checkTerm(msatTerm);
			return new SMTLibExpression(new MathSatExpression(msatTerm));

		} else {
			long msatTerm = mathsat.api.msat_make_and(env,
					msatExpr[0].getMsatExpr(), msatExpr[1].getMsatExpr());

			checkTerm(msatTerm);

			for (int i = 2; i < length; i++) {
				msatTerm = mathsat.api.msat_make_and(env, msatTerm,
						msatExpr[i].getMsatExpr());
			}
			checkTerm(msatTerm);
			return new SMTLibExpression(new MathSatExpression(msatTerm));
		}
	}

	@Override
	protected SMTLibExpression createOR(SMTLibExpression... expr) {
		int length = expr.length;

		MathSatExpression[] msatExpr = new MathSatExpression[length];

		for (int i = 0; i < length; i++) {
			msatExpr[i] = expr[i].getMathSatExpression();
		}

		if (length == 2) {
			long msatTerm = mathsat.api.msat_make_or(env,
					msatExpr[0].getMsatExpr(), msatExpr[1].getMsatExpr());
			checkTerm(msatTerm);
			return new SMTLibExpression(new MathSatExpression(msatTerm));

		} else {
			long msatTerm = mathsat.api.msat_make_or(env,
					msatExpr[0].getMsatExpr(), msatExpr[1].getMsatExpr());

			checkTerm(msatTerm);

			for (int i = 2; i < length; i++) {
				msatTerm = mathsat.api.msat_make_or(env, msatTerm,
						msatExpr[i].getMsatExpr());
			}
			checkTerm(msatTerm);
			return new SMTLibExpression(new MathSatExpression(msatTerm));
		}
	}

	@Override
	protected SMTLibExpression createNOT(SMTLibExpression... expr) {
		MathSatExpression msatExpr = expr[0].getMathSatExpression();

		long newMsatExpr = mathsat.api.msat_make_not(env,
				msatExpr.getMsatExpr());
		checkTerm(newMsatExpr);
		return new SMTLibExpression(new MathSatExpression(newMsatExpr));

	}

	@Override
	protected SMTLibExpression createLE(IntVariable leftVariable, IntVariable rightVariable) {
		MathSatIntVariable msatVar1 = (MathSatIntVariable) leftVariable;
		MathSatIntVariable msatVar2 = (MathSatIntVariable) rightVariable;

		long msatTerm = mathsat.api.msat_make_leq(env, msatVar1.getIntVar(),
				msatVar2.getIntVar());

		checkTerm(msatTerm);
		MathSatExpression msatExpr = new MathSatExpression(msatTerm);
		return new SMTLibExpression(msatExpr);
	}

	@Override
	protected SMTLibExpression createLE(RealVariable leftVariable, RealVariable rightVariable) {
		MathSatRealVariable msatVar1 = (MathSatRealVariable) leftVariable;
		MathSatRealVariable msatVar2 = (MathSatRealVariable) rightVariable;

		long msatTerm = mathsat.api.msat_make_leq(env, msatVar1.getRealVar(),
				msatVar2.getRealVar());

		checkTerm(msatTerm);
		MathSatExpression msatExpr = new MathSatExpression(msatTerm);
		return new SMTLibExpression(msatExpr);
	}

	@Override
	protected SMTLibExpression createLE(BitvectorVariable leftVariable,
			BitvectorVariable rightVariable) {

		int leftVarSize = leftVariable.getSize();
		int rightVarSize = rightVariable.getSize();

		// left variable has more bits -> right variable needs zero
		// extension
		if (leftVarSize > rightVarSize) {
			rightVariable = makeBitvectorZeroExtension(rightVariable, leftVarSize - rightVarSize);

			// right variable has more bits -> left variable needs zero
			// extension
		} else if (leftVarSize < rightVarSize) {
			leftVariable = makeBitvectorZeroExtension(leftVariable, rightVarSize - leftVarSize);
		} else {
			// do nothing
		}

		MathSatBitvectorVariable msatVar1 = (MathSatBitvectorVariable) leftVariable;
		MathSatBitvectorVariable msatVar2 = (MathSatBitvectorVariable) rightVariable;

		long msatTerm = mathsat.api.msat_make_bv_uleq(env,
				msatVar1.getBitvectorVar(), msatVar2.getBitvectorVar());
		checkTerm(msatTerm);
		MathSatExpression msatExpr = new MathSatExpression(msatTerm);
		return new SMTLibExpression(msatExpr);
	}

	@Override
	protected SMTLibExpression createLEc(IntVariable leftVariable, IntVariable rightVariable,
			IntVariable constant) {
		MathSatIntVariable msatVar1 = (MathSatIntVariable) leftVariable;
		MathSatIntVariable msatVar2 = (MathSatIntVariable) rightVariable;
		MathSatIntVariable c = (MathSatIntVariable) constant;

		long msatRightExpr = mathsat.api.msat_make_plus(env,
				msatVar2.getIntVar(), c.getIntVar());
		checkTerm(msatRightExpr);
		long msatTerm = mathsat.api.msat_make_leq(env, msatVar1.getIntVar(),
				msatRightExpr);
		checkTerm(msatTerm);
		MathSatExpression msatExpr = new MathSatExpression(msatTerm);
		return new SMTLibExpression(msatExpr);
	}

	@Override
	protected SMTLibExpression createLEc(RealVariable leftVariable, RealVariable rightVariable,
			RealVariable constant) {
		MathSatRealVariable msatVar1 = (MathSatRealVariable) leftVariable;
		MathSatRealVariable msatVar2 = (MathSatRealVariable) rightVariable;
		MathSatRealVariable c = (MathSatRealVariable) constant;

		long msatRightExpr = mathsat.api.msat_make_plus(env,
				msatVar2.getRealVar(), c.getRealVar());
		checkTerm(msatRightExpr);
		long msatTerm = mathsat.api.msat_make_leq(env, msatVar1.getRealVar(),
				msatRightExpr);
		checkTerm(msatTerm);
		MathSatExpression msatExpr = new MathSatExpression(msatTerm);
		return new SMTLibExpression(msatExpr);
	}

	@Override
	protected SMTLibExpression createLT(RealVariable leftVariable, RealVariable rightVariable) {
		Preconditions.checkArgument(leftVariable instanceof RealVariable,
				"You cannot use Less with IntVariables!");
		Preconditions.checkArgument(rightVariable instanceof RealVariable,
				"You cannot use Less with IntVariables!");

		MathSatRealVariable msatVar1 = (MathSatRealVariable) leftVariable;
		MathSatRealVariable msatVar2 = (MathSatRealVariable) rightVariable;
		long v1 = msatVar1.getRealVar();
		long v2 = msatVar2.getRealVar();

		// leftVariable <= rightVariable
		long msatTerm1 = mathsat.api.msat_make_leq(env, v1, v2);
		checkTerm(msatTerm1);
		// leftVariable != rightVariable
		long msatTemp = mathsat.api.msat_make_equal(env, v1, v2);
		checkTerm(msatTemp);
		long msatTerm2 = mathsat.api.msat_make_not(env, msatTemp);
		checkTerm(msatTerm2);

		// combine to leftVariable < rightVariable
		long msatTerm = mathsat.api.msat_make_and(env, msatTerm1, msatTerm2);
		checkTerm(msatTerm);

		MathSatExpression msatExpr = new MathSatExpression(msatTerm);
		return new SMTLibExpression(msatExpr);
	}

	@Override
	protected SMTLibExpression createLT(BitvectorVariable leftVariable,
			BitvectorVariable rightVariable) {

		int leftVarSize = leftVariable.getSize();
		int rightVarSize = rightVariable.getSize();

		// left variable has more bits -> right variable needs zero
		// extension
		if (leftVarSize > rightVarSize) {
			rightVariable = makeBitvectorZeroExtension(rightVariable, leftVarSize - rightVarSize);

			// right variable has more bits -> left variable needs zero
			// extension
		} else if (leftVarSize < rightVarSize) {
			leftVariable = makeBitvectorZeroExtension(leftVariable, rightVarSize - leftVarSize);
		} else {
			// do nothing
		}

		MathSatBitvectorVariable msatVar1 = (MathSatBitvectorVariable) leftVariable;
		MathSatBitvectorVariable msatVar2 = (MathSatBitvectorVariable) rightVariable;

		long msatTerm = mathsat.api.msat_make_bv_ult(env,
				msatVar1.getBitvectorVar(), msatVar2.getBitvectorVar());
		checkTerm(msatTerm);
		MathSatExpression msatExpr = new MathSatExpression(msatTerm);
		return new SMTLibExpression(msatExpr);
	}

	@Override
	protected SMTLibExpression createLTc(RealVariable leftVariable, RealVariable rightVariable,
			RealVariable constant) {
		MathSatRealVariable msatVar1 = (MathSatRealVariable) leftVariable;
		MathSatRealVariable msatVar2 = (MathSatRealVariable) rightVariable;
		MathSatRealVariable c = (MathSatRealVariable) constant;
		long v1 = msatVar1.getRealVar();

		long msatRightExpr = mathsat.api.msat_make_plus(env,
				msatVar2.getRealVar(), c.getRealVar());
		checkTerm(msatRightExpr);
		// leftVariable <= rightVariable
		long msatTerm1 = mathsat.api.msat_make_leq(env, v1, msatRightExpr);
		checkTerm(msatTerm1);
		// leftVariable != rightVariable
		long msatTemp = mathsat.api.msat_make_equal(env, v1, msatRightExpr);
		checkTerm(msatTemp);
		long msatTerm2 = mathsat.api.msat_make_not(env, msatTemp);
		checkTerm(msatTerm2);

		// combine to leftVariable < rightVariable
		long msatTerm = mathsat.api.msat_make_and(env, msatTerm1, msatTerm2);
		checkTerm(msatTerm);

		MathSatExpression msatExpr = new MathSatExpression(msatTerm);
		return new SMTLibExpression(msatExpr);
	}

	@Override
	protected SMTLibExpression createBooleanValue(boolean value) {
		long msatTerm;

		if (value) {
			msatTerm = mathsat.api.msat_make_true(env);
		} else {
			msatTerm = mathsat.api.msat_make_false(env);
		}
		checkTerm(msatTerm);
		MathSatExpression msatExpr = new MathSatExpression(msatTerm);
		return new SMTLibExpression(msatExpr);
	}

	@Override
	protected SMTLibExpression createEqualc(RealVariable leftVariable,
			RealVariable rightVariable, RealVariable constant) {
		MathSatRealVariable mathSatVar1 = (MathSatRealVariable) leftVariable;
		MathSatRealVariable mathSatVar2 = (MathSatRealVariable) rightVariable;
		MathSatRealVariable c = (MathSatRealVariable) constant;

		long msatRightExpr = mathsat.api.msat_make_plus(env,
				mathSatVar2.getRealVar(), c.getRealVar());
		checkTerm(msatRightExpr);
		long newRealTerm = mathsat.api.msat_make_equal(env,
				mathSatVar1.getRealVar(), msatRightExpr);
		checkTerm(newRealTerm);

		MathSatExpression msatExpr = new MathSatExpression(newRealTerm);
		return new SMTLibExpression(msatExpr);
	}

	@Override
	protected SMTLibExpression createEqualc(IntVariable leftVariable,
			IntVariable rightVariable, IntVariable constant) {
		MathSatIntVariable mathSatVar1 = (MathSatIntVariable) leftVariable;
		MathSatIntVariable mathSatVar2 = (MathSatIntVariable) rightVariable;
		MathSatIntVariable c = (MathSatIntVariable) constant;

		long msatRightExpr = mathsat.api.msat_make_plus(env,
				mathSatVar2.getIntVar(), c.getIntVar());
		checkTerm(msatRightExpr);
		long newIntTerm = mathsat.api.msat_make_equal(env,
				mathSatVar1.getIntVar(), msatRightExpr);
		checkTerm(newIntTerm);

		MathSatExpression msatExpr = new MathSatExpression(newIntTerm);
		return new SMTLibExpression(msatExpr);
	}

	@Override
	protected SMTLibExpression createNotEqualc(IntVariable leftVariable,
			IntVariable rightVariable, IntVariable c) {
		return createNOT(createEqualc(leftVariable, leftVariable, c));
	}

	@Override
	protected BitvectorVariable createBitvectorString(Column col,
			String string, int size) {
		long msatBitVec = mathsat.api.msat_make_bv_number(env, string, size, 2);
		checkTerm(msatBitVec);
		return new MathSatBitvectorVariable(col, size, msatBitVec);
	}

	@Override
	protected SMTLibExpression createNotEqualc(RealVariable leftVariable,
			RealVariable rightVariable, RealVariable c) {
		return createNOT(createEqualc(leftVariable, rightVariable, c));
	}

	@Override
	protected String toSMTLib2(SMTLibExpression expr) {
		long msatexpr1 = expr.getMathSatExpression().getMsatExpr();
		return mathsat.api.msat_to_smtlib2_term(env, msatexpr1);
	}

	@Override
	protected MathSatBitvectorVariable makeBitvectorZeroExtension(
			BitvectorVariable bVar, int bitsToExtend) {
		MathSatBitvectorVariable bitVecVar = (MathSatBitvectorVariable) bVar;

		String str = "";
		final String ZERO = "0";

		// create string with missing zeros
		for (int i = 0; i < bitsToExtend; i++) {
			str += ZERO;
		}

		long zeroExt = mathsat.api.msat_make_bv_number(env, str, str.length(),
				2);
		checkTerm(zeroExt);

		// concat the former bitvector with the zero bitvector
		long newVar = mathsat.api.msat_make_bv_concat(env,
				bitVecVar.getBitvectorVar(), zeroExt);
		checkTerm(newVar);

		int newSize = bVar.getSize() + bitsToExtend;
		return new MathSatBitvectorVariable(bitVecVar.getColumn(), newSize,
				newVar);
	}
}
