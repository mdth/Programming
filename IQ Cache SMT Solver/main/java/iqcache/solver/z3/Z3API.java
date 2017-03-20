package iqcache.solver.z3;

import com.microsoft.z3.BitVecExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.IntNum;
import com.microsoft.z3.RatNum;
import com.microsoft.z3.RealExpr;
import com.microsoft.z3.Z3Exception;

import iqcache.common.Preconditions;
import iqcache.query.column.Column;
import iqcache.solver.SMTLibExpression;
import iqcache.solver.Solver;
import iqcache.solver.SolverAPI;
import iqcache.solver.variable.BitvectorVariable;
import iqcache.solver.variable.IntVariable;
import iqcache.solver.variable.RealVariable;
import iqcache.solver.variable.Variable;

/**
 * This class represents the Z3 API.
 * 
 * @author dinh
 */
public class Z3API extends SolverAPI {
	/**
	 * The context of a Z3 SMT Solver.
	 */
	private Context ctx;

	/**
	 * Constructor of an Z3API.
	 * 
	 * @param ctx
	 *            the needed context for a Z3API.
	 */
	public Z3API(Context ctx) {
		this.ctx = ctx;
	}

	@Override
	protected SMTLibExpression createBoolVariable(Column col) {
		Preconditions.checkNotNull(col);
		BoolExpr boolExpr = null;
		try {
			boolExpr = ctx.MkBoolConst(Solver.getVariableName(col));
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
		return new SMTLibExpression(new Z3Expression(boolExpr));
	}

	@Override
	protected IntVariable createIntVariable(Column col) {
		Preconditions.checkNotNull(col);
		IntExpr intExpr = null;
		try {
			intExpr = ctx.MkIntConst(Solver.getVariableName(col));
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
		return new Z3IntVariable(col, intExpr);
	}

	@Override
	protected RealVariable createRealVariable(Column col) {
		Preconditions.checkNotNull(col);
		RealExpr realExpr = null;
		try {
			realExpr = ctx.MkRealConst(Solver.getVariableName(col));
		} catch (Z3Exception e) {
			e.printStackTrace();
		}

		return new Z3RealVariable(col, realExpr);
	}

	@Override
	protected BitvectorVariable createBitvectorVariable(Column col, int size) {
		Preconditions.checkNotNull(col);
		Preconditions.checkArgument(size > 0);
		BitVecExpr bitVecExpr = null;
		try {
			bitVecExpr = ctx.MkBVConst(Solver.getVariableName(col), size);
		} catch (Z3Exception e) {
			e.printStackTrace();
		}

		return new Z3BitvectorVariable(col, bitVecExpr, size);
	}

	@Override
	protected SMTLibExpression createEqual(IntVariable leftVariable,
			IntVariable rightVariable) {
		Z3IntVariable z3Var1 = (Z3IntVariable) leftVariable;
		Z3IntVariable z3Var2 = (Z3IntVariable) rightVariable;
		BoolExpr boolExpr = null;

		try {
			boolExpr = ctx.MkEq(z3Var1.getIntExpr(), z3Var2.getIntExpr());
			disposeExpression(z3Var1, z3Var2);
		} catch (Z3Exception e) {
			e.printStackTrace();
		}

		Z3Expression expr = new Z3Expression(boolExpr);
		return new SMTLibExpression(expr);
	}

	private void disposeExpression(Variable... var) throws Z3Exception {
		for (Variable v : var) {
			if (v instanceof Z3IntVariable) {
				((Z3IntVariable) v).getIntExpr().Dispose();
			} else if (v instanceof Z3BoolVariable) {
				((Z3BoolVariable) v).getBoolExpr().Dispose();
			} else if (v instanceof Z3BitvectorVariable) {
				((Z3BitvectorVariable) v).getBitvectorExpr().Dispose();
			} else {
				((Z3RealVariable) v).getRealExpr().Dispose();
			}
		}
	}

	@Override
	protected SMTLibExpression createEqual(RealVariable leftVariable,
			RealVariable rightVariable) {
		Z3RealVariable z3Var1 = (Z3RealVariable) leftVariable;
		Z3RealVariable z3Var2 = (Z3RealVariable) rightVariable;
		BoolExpr boolExpr = null;

		try {
			boolExpr = ctx.MkEq(z3Var1.getRealExpr(), z3Var2.getRealExpr());
			disposeExpression(z3Var1, z3Var2);
		} catch (Z3Exception e) {
			e.printStackTrace();
		}

		Z3Expression expr = new Z3Expression(boolExpr);
		return new SMTLibExpression(expr);
	}

	@Override
	protected SMTLibExpression createEqual(BitvectorVariable leftVariable,
			BitvectorVariable rightVariable) {

		int leftVarSize = leftVariable.getSize();
		int rightVarSize = rightVariable.getSize();

		// left variable has more bits -> right variable needs zero
		// extension
		if (leftVarSize > rightVarSize) {
			rightVariable = makeBitvectorZeroExtension(rightVariable,
					leftVarSize - rightVarSize);

			// right variable has more bits -> left variable needs zero
			// extension
		} else if (leftVarSize < rightVarSize) {
			leftVariable = makeBitvectorZeroExtension(leftVariable,
					rightVarSize - leftVarSize);
		} else {
			// do nothing
		}

		Z3BitvectorVariable z3Var1 = (Z3BitvectorVariable) leftVariable;
		Z3BitvectorVariable z3Var2 = (Z3BitvectorVariable) rightVariable;
		BoolExpr boolExpr = null;

		try {
			boolExpr = ctx.MkEq(z3Var1.getBitvectorExpr(),
					z3Var2.getBitvectorExpr());
			disposeExpression(z3Var1, z3Var2);
		} catch (Z3Exception e) {
			e.printStackTrace();
		}

		Z3Expression expr = new Z3Expression(boolExpr);
		return new SMTLibExpression(expr);
	}

	@Override
	protected SMTLibExpression createNotEqual(IntVariable leftVariable,
			IntVariable rightVariable) {
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
	protected SMTLibExpression createLE(IntVariable leftVariable,
			IntVariable rightVariable) {
		BoolExpr boolExpr = null;
		Z3IntVariable z3Var1 = (Z3IntVariable) leftVariable;
		Z3IntVariable z3Var2 = (Z3IntVariable) rightVariable;

		try {
			boolExpr = ctx.MkLe(z3Var1.getIntExpr(), z3Var2.getIntExpr());
			disposeExpression(z3Var1, z3Var2);
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
		Z3Expression newExpr = new Z3Expression(boolExpr);
		return new SMTLibExpression(newExpr);
	}

	@Override
	protected SMTLibExpression createLE(RealVariable leftVariable,
			RealVariable rightVariable) {
		BoolExpr boolExpr = null;
		Z3RealVariable z3Var1 = (Z3RealVariable) leftVariable;
		Z3RealVariable z3Var2 = (Z3RealVariable) rightVariable;

		try {
			boolExpr = ctx.MkLe(z3Var1.getRealExpr(), z3Var2.getRealExpr());
			disposeExpression(z3Var1, z3Var2);
		} catch (Z3Exception e) {
			e.printStackTrace();
		}

		Z3Expression newExpr = new Z3Expression(boolExpr);
		return new SMTLibExpression(newExpr);
	}

	@Override
	protected SMTLibExpression createLE(BitvectorVariable leftVariable,
			BitvectorVariable rightVariable) {

		int leftVarSize = leftVariable.getSize();
		int rightVarSize = rightVariable.getSize();

		// left variable has more bits -> right variable needs zero
		// extension
		if (leftVarSize > rightVarSize) {
			rightVariable = makeBitvectorZeroExtension(rightVariable,
					leftVarSize - rightVarSize);

			// right variable has more bits -> left variable needs zero
			// extension
		} else if (leftVarSize < rightVarSize) {
			leftVariable = makeBitvectorZeroExtension(leftVariable,
					rightVarSize - leftVarSize);
		} else {
			// do nothing
		}

		BoolExpr boolExpr = null;
		Z3BitvectorVariable z3Var1 = (Z3BitvectorVariable) leftVariable;
		Z3BitvectorVariable z3Var2 = (Z3BitvectorVariable) rightVariable;

		try {
			boolExpr = ctx.MkBVULE(z3Var1.getBitvectorExpr(),
					z3Var2.getBitvectorExpr());
			disposeExpression(z3Var1, z3Var2);
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
		Z3Expression newExpr = new Z3Expression(boolExpr);
		return new SMTLibExpression(newExpr);
	}

	@Override
	protected SMTLibExpression createLEc(IntVariable leftVariable,
			IntVariable rightVariable, IntVariable c) {

		Z3IntVariable z3Var1 = (Z3IntVariable) leftVariable;
		Z3IntVariable z3Var2 = (Z3IntVariable) rightVariable;
		Z3IntVariable constant = (Z3IntVariable) c;

		// add constant to rightVariable
		IntExpr[] rightVar = new IntExpr[2];
		rightVar[0] = z3Var2.getIntExpr();
		rightVar[1] = constant.getIntExpr();

		IntExpr newIntExpr = null;
		BoolExpr boolExpr = null;

		try {
			newIntExpr = (IntExpr) ctx.MkAdd(rightVar);
		} catch (Z3Exception e1) {
			e1.printStackTrace();
		}

		try {
			boolExpr = ctx.MkLe(z3Var1.getIntExpr(), newIntExpr);
			disposeExpression(z3Var1, z3Var2, constant);
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
		Z3Expression newExpr = new Z3Expression(boolExpr);
		return new SMTLibExpression(newExpr);
	}

	@Override
	protected SMTLibExpression createLT(RealVariable leftVariable,
			RealVariable rightVariable) {
		Z3RealVariable z3Var1 = (Z3RealVariable) leftVariable;
		Z3RealVariable z3Var2 = (Z3RealVariable) rightVariable;
		BoolExpr boolExpr = null;

		try {
			boolExpr = ctx.MkLt(z3Var1.getRealExpr(), z3Var2.getRealExpr());
			disposeExpression(z3Var1, z3Var2);
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
		Z3Expression newExpr = new Z3Expression(boolExpr);
		return new SMTLibExpression(newExpr);
	}

	@Override
	protected SMTLibExpression createLT(BitvectorVariable leftVariable,
			BitvectorVariable rightVariable) {

		int leftVarSize = leftVariable.getSize();
		int rightVarSize = rightVariable.getSize();

		// left variable has more bits -> right variable needs zero
		// extension
		if (leftVarSize > rightVarSize) {
			rightVariable = makeBitvectorZeroExtension(rightVariable,
					leftVarSize - rightVarSize);

			// right variable has more bits -> left variable needs zero
			// extension
		} else if (leftVarSize < rightVarSize) {
			leftVariable = makeBitvectorZeroExtension(leftVariable,
					rightVarSize - leftVarSize);
		} else {
			// do nothing
		}

		BoolExpr boolExpr = null;
		Z3BitvectorVariable z3Var1 = (Z3BitvectorVariable) leftVariable;
		Z3BitvectorVariable z3Var2 = (Z3BitvectorVariable) rightVariable;

		try {
			boolExpr = ctx.MkBVULT(z3Var1.getBitvectorExpr(),
					z3Var2.getBitvectorExpr());
			disposeExpression(z3Var1, z3Var2);
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
		Z3Expression newExpr = new Z3Expression(boolExpr);
		return new SMTLibExpression(newExpr);
	}

	@Override
	protected SMTLibExpression createLEc(RealVariable leftVariable,
			RealVariable rightVariable, RealVariable constant) {
		Z3RealVariable z3Var1 = (Z3RealVariable) leftVariable;
		Z3RealVariable z3Var2 = (Z3RealVariable) rightVariable;
		Z3RealVariable c = (Z3RealVariable) constant;

		// add constant to rightVariable
		RealExpr[] rightVar = new RealExpr[2];
		rightVar[0] = z3Var2.getRealExpr();
		rightVar[1] = c.getRealExpr();

		RealExpr newRealExpr = null;
		BoolExpr boolExpr = null;

		try {
			newRealExpr = (RealExpr) ctx.MkAdd(rightVar);
		} catch (Z3Exception e1) {
			e1.printStackTrace();
		}

		try {
			boolExpr = ctx.MkLe(z3Var1.getRealExpr(), newRealExpr);
			disposeExpression(z3Var1, z3Var2, constant);
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
		Z3Expression newExpr = new Z3Expression(boolExpr);
		return new SMTLibExpression(newExpr);
	}

	@Override
	protected SMTLibExpression createLTc(RealVariable leftVariable,
			RealVariable rightVariable, RealVariable constant) {
		Z3RealVariable z3Var1 = (Z3RealVariable) leftVariable;
		Z3RealVariable z3Var2 = (Z3RealVariable) rightVariable;
		Z3RealVariable c = (Z3RealVariable) constant;

		// add constant to rightVariable
		RealExpr[] rightVar = new RealExpr[2];
		rightVar[0] = z3Var2.getRealExpr();
		rightVar[1] = c.getRealExpr();

		RealExpr newRealExpr = null;
		BoolExpr boolExpr = null;

		try {
			newRealExpr = (RealExpr) ctx.MkAdd(rightVar);
		} catch (Z3Exception e1) {
			e1.printStackTrace();
		}

		try {
			boolExpr = ctx.MkLt(z3Var1.getRealExpr(), newRealExpr);
			disposeExpression(z3Var1, z3Var2, constant);
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
		Z3Expression newExpr = new Z3Expression(boolExpr);
		return new SMTLibExpression(newExpr);
	}

	@Override
	protected SMTLibExpression createAND(SMTLibExpression... expr) {
		BoolExpr[] boolExpr = convertSMTLibExprHelper(expr);

		BoolExpr boolTerm = null;
		try {
			boolTerm = ctx.MkAnd(boolExpr);
			disposeExpression(boolExpr);
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
		
		Z3Expression newZ3Expr = new Z3Expression(boolTerm);
		return new SMTLibExpression(newZ3Expr);
	}

	private void disposeExpression(Expr... boolExpr) throws Z3Exception {
		for (Expr b : boolExpr) {
			b.Dispose();
		}
	}

	@Override
	protected SMTLibExpression createOR(SMTLibExpression... expr) {
		BoolExpr[] boolExpr = convertSMTLibExprHelper(expr);

		BoolExpr boolTerm = null;
		try {
			boolTerm = ctx.MkOr(boolExpr);
			disposeExpression(boolExpr);
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
		Z3Expression newZ3Expr = new Z3Expression(boolTerm);
		return new SMTLibExpression(newZ3Expr);
	}

	private BoolExpr[] convertSMTLibExprHelper(SMTLibExpression... expr) {
		int length = expr.length;
		Z3Expression[] z3Expr = new Z3Expression[length];
		BoolExpr[] boolExpr = new BoolExpr[length];

		for (int i = 0; i < length; i++) {
			z3Expr[i] = expr[i].getZ3Expression();
			if (z3Expr != null) {
				boolExpr[i] = z3Expr[i].getBoolExpr();
			}
		}
		return boolExpr;
	}

	@Override
	protected SMTLibExpression createNOT(SMTLibExpression... expr) {
		BoolExpr boolTerm = expr[0].getZ3Expression().getBoolExpr();
		BoolExpr boolExpr = null;

		try {
			boolExpr = ctx.MkNot(boolTerm);
			disposeExpression(boolTerm);
		} catch (Z3Exception e) {
			e.printStackTrace();
		}

		Z3Expression newZ3Expr = new Z3Expression(boolExpr);
		return new SMTLibExpression(newZ3Expr);
	}

	@Override
	protected SMTLibExpression createBooleanValue(boolean value) {
		BoolExpr boolExpr = null;

		if (value) {
			try {
				boolExpr = ctx.MkTrue();
			} catch (Z3Exception e) {
				e.printStackTrace();
			}
			return new SMTLibExpression(new Z3Expression(boolExpr));

		} else {
			try {
				boolExpr = ctx.MkFalse();
			} catch (Z3Exception e) {
				e.printStackTrace();
			}
			return new SMTLibExpression(new Z3Expression(boolExpr));
		}
	}

	@Override
	protected IntVariable createIntNum(Column col, int value) {
		IntNum intNum = null;
		try {
			intNum = ctx.MkInt(value);
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
		return new Z3IntVariable(col, intNum);
	}

	@Override
	protected RealVariable createRealNum(Column col, double value) {
		RatNum realNum = null;
		try {
			realNum = ctx.MkReal((long) value);
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
		return new Z3RealVariable(col, realNum);
	}

	@Override
	protected SMTLibExpression createNotEqualc(RealVariable leftVariable,
			RealVariable rightVariable, RealVariable constant) {
		SMTLibExpression expr = createEqualc(leftVariable, rightVariable,
				constant);
		return createNOT(expr);
	}

	@Override
	protected SMTLibExpression createEqualc(RealVariable leftVariable,
			RealVariable rightVariable, RealVariable constant) {
		Z3RealVariable z3Var1 = (Z3RealVariable) leftVariable;
		Z3RealVariable z3Var2 = (Z3RealVariable) rightVariable;
		Z3RealVariable c = (Z3RealVariable) constant;

		// add constant to rightVariable
		RealExpr[] rightVar = new RealExpr[2];
		rightVar[0] = z3Var2.getRealExpr();
		rightVar[1] = c.getRealExpr();

		RealExpr newRealExpr = null;
		BoolExpr boolExpr = null;

		try {
			newRealExpr = (RealExpr) ctx.MkAdd(rightVar);
		} catch (Z3Exception e1) {
			e1.printStackTrace();
		}

		try {
			boolExpr = ctx.MkEq(z3Var1.getRealExpr(), newRealExpr);
			disposeExpression(z3Var1, z3Var2, constant);
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
		Z3Expression newExpr = new Z3Expression(boolExpr);
		return new SMTLibExpression(newExpr);
	}

	@Override
	protected SMTLibExpression createEqualc(IntVariable leftVariable,
			IntVariable rightVariable, IntVariable constant) {
		Z3IntVariable z3Var1 = (Z3IntVariable) leftVariable;
		Z3IntVariable z3Var2 = (Z3IntVariable) rightVariable;
		Z3IntVariable c = (Z3IntVariable) constant;

		// add constant to rightVariable
		IntExpr[] rightVar = new IntExpr[2];
		rightVar[0] = z3Var2.getIntExpr();
		rightVar[1] = c.getIntExpr();

		IntExpr newIntExpr = null;
		BoolExpr boolExpr = null;

		try {
			newIntExpr = (IntExpr) ctx.MkAdd(rightVar);
		} catch (Z3Exception e1) {
			e1.printStackTrace();
		}

		try {
			boolExpr = ctx.MkEq(z3Var1.getIntExpr(), newIntExpr);
			disposeExpression(z3Var1, z3Var2, constant);
		} catch (Z3Exception e) {
			e.printStackTrace();
		}
		Z3Expression newExpr = new Z3Expression(boolExpr);
		return new SMTLibExpression(newExpr);
	}

	@Override
	protected SMTLibExpression createNotEqualc(IntVariable leftVariable,
			IntVariable rightVariable, IntVariable constant) {
		return createNOT(createEqualc(leftVariable, rightVariable, constant));
	}

	@Override
	protected BitvectorVariable createBitvectorString(Column col,
			String string, int size) {
		BitVecExpr bitVecExpr = null;
		try {
			/*
			 * Z3 creates a bitvector string with a given decimal number
			 * representation of the binary bitvector
			 */
			bitVecExpr = ctx.MkBV(string, size);
		} catch (Z3Exception e) {
			e.printStackTrace();
		}

		return new Z3BitvectorVariable(col, bitVecExpr, size);
	}

	@Override
	protected String toSMTLib2(SMTLibExpression expr) {
		return expr.getZ3Expression().getBoolExpr().toString();
	}

	@Override
	protected BitvectorVariable makeBitvectorZeroExtension(
			BitvectorVariable bVar, int bitsToExtend) {
		Z3BitvectorVariable bitVecVar = (Z3BitvectorVariable) bVar;

		final String ZERO = "0";

		BitVecExpr zeroExt = null;
		BitVecExpr newVar = null;
		try {
			zeroExt = ctx.MkBV(ZERO, ZERO.length());

			// fill up the missing zero part with zero extension
			zeroExt = ctx.MkZeroExt(bitsToExtend - 1, zeroExt);

			newVar = ctx.MkConcat(bitVecVar.getBitvectorExpr(), zeroExt);
			disposeExpression(zeroExt);
		} catch (Z3Exception e) {
			e.printStackTrace();
		}

		int newSize = bVar.getSize() + bitsToExtend;
		return new Z3BitvectorVariable(bitVecVar.getColumn(), newVar, newSize);
	}
}
