package iqcache.solver;

import com.microsoft.z3.Context;

import iqcache.common.Preconditions;
import iqcache.expression.Expression;
import iqcache.expression.ExpressionVisitor;
import iqcache.expression.leaf.DummyLeaf;
import iqcache.expression.leaf.typeboolean.BooleanLiteral;
import iqcache.expression.leaf.typeboolean.BooleanValue;
import iqcache.expression.leaf.typeboolean.NegatedBooleanLiteral;
import iqcache.expression.leaf.typedouble.EqualDoubleComparison;
import iqcache.expression.leaf.typedouble.LessDoubleComparison;
import iqcache.expression.leaf.typedouble.LessOrEqualDoubleComparison;
import iqcache.expression.leaf.typedouble.NotEqualDoubleComparison;
import iqcache.expression.leaf.typeinteger.EqualIntegerComparison;
import iqcache.expression.leaf.typeinteger.LessOrEqualIntegerComparison;
import iqcache.expression.leaf.typeinteger.NotEqualIntegerComparison;
import iqcache.expression.leaf.typevarchar.EqualVarcharComparison;
import iqcache.expression.leaf.typevarchar.LessOrEqualVarcharComparison;
import iqcache.expression.leaf.typevarchar.LessVarcharComparison;
import iqcache.expression.leaf.typevarchar.NotEqualVarcharComparison;
import iqcache.expression.node.And;
import iqcache.expression.node.Not;
import iqcache.expression.node.Or;
import iqcache.query.column.Column;
import iqcache.query.column.ColumnType;
import iqcache.query.column.VarcharType;
import iqcache.solver.SolverAPI;
import iqcache.solver.mathsat.MathSatAPI;
import iqcache.solver.mathsat.MathSatSolver;
import iqcache.solver.variable.BitvectorVariable;
import iqcache.solver.variable.IntVariable;
import iqcache.solver.variable.RealVariable;
import iqcache.solver.z3.Z3API;
import iqcache.solver.z3.Z3Solver;

/**
 * This class uses the VisitorPattern to convert Expressions into
 * SMTLibExpressions.
 * 
 * @author dinh
 */
public class SMTExpressionConverter implements
		ExpressionVisitor<SMTLibExpression, SolverException> {

	private SolverAPI solverAPI;

	/**
	 * This method creates an ExpressionVisitor for an instance of a MathSAT
	 * solver.
	 * 
	 * @param solver
	 *            the running instance of a MathSAT solver
	 * @param env
	 *            the environment for the MathSAT solver
	 */
	public SMTExpressionConverter(Solver solver, long env) {
		Preconditions.checkNotNull(solver);
		Preconditions.checkArgument(solver instanceof MathSatSolver);
		this.solverAPI = new MathSatAPI(env);
	}

	/**
	 * This method creates an ExpressionVisitor for an instance of a MathSAT
	 * solver.
	 * 
	 * @param solver
	 *            the running instance of a Z3 solver
	 * @param ctx
	 *            the context for the Z3 solver
	 */
	public SMTExpressionConverter(Solver solver, Context ctx) {
		Preconditions.checkNotNull(solver);
		Preconditions.checkArgument(solver instanceof Z3Solver);
		this.solverAPI = new Z3API(ctx);
	}

	/**
	 * Determine the length of a given Varchar-Column * BITS_PER_CHARACTER.
	 * 
	 * @param col
	 *            a Column of type VARCHAR
	 * @return the length of the varchar-field (see
	 *         {@link VarcharType#getLength()}) or <code>-1</code> if the
	 *         specified {@link Column} was not of type VARCHAR
	 * @throws NullPointerException
	 *             iff <code>col == null</code>
	 */
	private int getVarcharLength(Column col) {
		Preconditions.checkNotNull(col);
		ColumnType type = col.getType();

		if (!(type instanceof VarcharType)) {
			return -1;
		} else {
			VarcharType vtype = (VarcharType) type;
			return vtype.getLength() * Solver.BITS_PER_CHARACTER;
		}
	}

	/**
	 * not implemented
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public SMTLibExpression visit(Expression exp) throws SolverException {
		throw new UnsupportedOperationException();
	}

	@Override
	public SMTLibExpression visit(And and) throws SolverException {
		int arraySize = and.getNumberOfSubexpressions();
		SMTLibExpression[] expr = new SMTLibExpression[arraySize];

		for (int i = 0; i < arraySize; i++) {
			SMTLibExpression temp = and.getSubexpressionAt(i).accept(this);
			expr[i] = temp;
		}
		return solverAPI.createAND(expr);
	}

	@Override
	public SMTLibExpression visit(Or or) throws SolverException {
		int arraySize = or.getNumberOfSubexpressions();
		SMTLibExpression[] expr = new SMTLibExpression[arraySize];

		for (int i = 0; i < arraySize; i++) {
			expr[i] = or.getSubexpressionAt(i).accept(this);
		}
		return solverAPI.createOR(expr);

	}

	@Override
	public SMTLibExpression visit(Not not) throws SolverException {
		int arraySize = not.getNumberOfSubexpressions();
		SMTLibExpression[] expr = new SMTLibExpression[arraySize];

		for (int i = 0; i < arraySize; i++) {
			expr[i] = not.getSubexpressionAt(i).accept(this);
		}
		return solverAPI.createNOT(expr);
	}

	@Override
	public SMTLibExpression visit(BooleanValue booleanValue)
			throws SolverException {
		return solverAPI.createBooleanValue(booleanValue.getValue());
	}

	/**
	 * not implemented
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public SMTLibExpression visit(DummyLeaf dummyLeaf) throws SolverException {
		throw new UnsupportedOperationException();
	}

	@Override
	public SMTLibExpression visit(EqualDoubleComparison exp)
			throws SolverException {
		if (exp.getRight() == null && exp.getLeft() != null) {
			Column leftColumn = exp.getLeft();
			RealVariable leftVariable = solverAPI
					.createRealVariable(leftColumn);
			RealVariable rightVariable = solverAPI.createRealNum(null,
					exp.getConstant());
			return solverAPI.createEqual(leftVariable, rightVariable);

		} else if (exp.getRight() != null && exp.getLeft() == null) {
			Column rightColumn = exp.getRight();
			RealVariable rightVariable = solverAPI
					.createRealVariable(rightColumn);
			RealVariable leftVariable = solverAPI.createRealNum(null,
					exp.getConstant());
			return solverAPI.createEqual(leftVariable, rightVariable);

		} else if (exp.getRight() != null && exp.getLeft() != null) {
			Column leftColumn = exp.getLeft();
			Column rightColumn = exp.getRight();

			RealVariable leftVariable = solverAPI
					.createRealVariable(leftColumn);
			RealVariable rightVariable = solverAPI
					.createRealVariable(rightColumn);

			double constant = exp.getConstant();
			RealVariable c = solverAPI.createRealNum(rightColumn, constant);
			SMTLibExpression expr = null;

			if (constant == 0) {
				expr = solverAPI.createEqual(leftVariable, rightVariable);
			} else {
				expr = solverAPI.createEqualc(leftVariable, rightVariable, c);
			}
			return expr;

		} else {
			// left == null, right == null
			// so: left = right + c == true
			return solverAPI.createBooleanValue(true);
		}
	}

	@Override
	public SMTLibExpression visit(EqualIntegerComparison exp)
			throws SolverException {
		if (exp.getRight() == null && exp.getLeft() != null) {
			Column leftColumn = exp.getLeft();
			IntVariable leftVariable = solverAPI.createIntVariable(leftColumn);
			IntVariable rightVariable = solverAPI.createIntNum(null,
					exp.getConstant());
			return solverAPI.createEqual(leftVariable, rightVariable);

		} else if (exp.getRight() != null && exp.getLeft() == null) {
			Column rightColumn = exp.getRight();
			IntVariable rightVariable = solverAPI
					.createIntVariable(rightColumn);
			IntVariable leftVariable = solverAPI.createIntNum(null,
					exp.getConstant());
			return solverAPI.createEqual(leftVariable, rightVariable);

		} else if (exp.getRight() != null && exp.getLeft() != null) {
			Column leftColumn = exp.getLeft();
			Column rightColumn = exp.getRight();

			IntVariable leftVariable = solverAPI.createIntVariable(leftColumn);
			IntVariable rightVariable = solverAPI
					.createIntVariable(rightColumn);

			int constant = exp.getConstant();
			IntVariable c = solverAPI.createIntNum(null, constant);
			SMTLibExpression expr = null;

			if (constant == 0) {
				expr = solverAPI.createEqual(leftVariable, rightVariable);
			} else {
				expr = solverAPI.createEqualc(leftVariable, rightVariable, c);
			}
			return expr;

		} else {
			// left == null, right == null
			// so: left = right + c == true
			return solverAPI.createBooleanValue(true);
		}
	}

	@Override
	public SMTLibExpression visit(EqualVarcharComparison exp)
			throws SolverException {

		if (exp.getRight() == null && exp.getLeft() != null) {
			Column leftColumn = exp.getLeft();
			String rightBitVector = null;

			if (solverAPI instanceof MathSatAPI) {
				rightBitVector = Solver.toBinaryBitVector(
						exp.getRightVarchar(), Solver.CHARSET);
			} else {
				rightBitVector = Solver.toDecimalBitVector(
						exp.getRightVarchar(), Solver.CHARSET);
			}

			BitvectorVariable leftVariable = solverAPI.createBitvectorVariable(
					leftColumn, getVarcharLength(leftColumn));
			BitvectorVariable rightVariable = solverAPI.createBitvectorString(
					null, rightBitVector, exp.getRightVarchar().length()
							* Solver.BITS_PER_CHARACTER);
			return solverAPI.createEqual(leftVariable, rightVariable);

		} else if (exp.getRight() != null && exp.getLeft() == null) {
			Column rightColumn = exp.getRight();

			String leftBitVector = null;

			if (solverAPI instanceof MathSatAPI) {
				leftBitVector = Solver.toBinaryBitVector(exp.getLeftVarchar(),
						Solver.CHARSET);
			} else {
				leftBitVector = Solver.toDecimalBitVector(exp.getLeftVarchar(),
						Solver.CHARSET);
			}

			BitvectorVariable rightVariable = solverAPI
					.createBitvectorVariable(rightColumn,
							getVarcharLength(rightColumn));
			BitvectorVariable leftVariable = solverAPI.createBitvectorString(
					null, leftBitVector, exp.getLeftVarchar().length()
							* Solver.BITS_PER_CHARACTER);
			return solverAPI.createEqual(leftVariable, rightVariable);

		} else if (exp.getRight() != null && exp.getLeft() != null) {
			Column leftColumn = exp.getLeft();
			Column rightColumn = exp.getRight();

			BitvectorVariable leftVariable = solverAPI.createBitvectorVariable(
					leftColumn, getVarcharLength(leftColumn));
			BitvectorVariable rightVariable = solverAPI
					.createBitvectorVariable(rightColumn,
							getVarcharLength(rightColumn));
			return solverAPI.createEqual(leftVariable, rightVariable);

		} else {
			// left == null, right == null -> true
			return solverAPI.createBooleanValue(true);
		}
	}

	@Override
	public SMTLibExpression visit(LessDoubleComparison exp)
			throws SolverException {

		if (exp.getRight() == null && exp.getLeft() != null) {
			Column leftColumn = exp.getLeft();
			RealVariable leftVariable = solverAPI
					.createRealVariable(leftColumn);
			RealVariable rightVariable = solverAPI.createRealNum(null,
					exp.getConstant());
			return solverAPI.createLT(leftVariable, rightVariable);

		} else if (exp.getRight() != null && exp.getLeft() == null) {
			Column rightColumn = exp.getRight();
			RealVariable rightVariable = solverAPI
					.createRealVariable(rightColumn);
			RealVariable leftVariable = solverAPI.createRealNum(null,
					exp.getConstant());
			return solverAPI.createLT(leftVariable, rightVariable);

		} else if (exp.getRight() != null && exp.getLeft() != null) {
			Column leftColumn = exp.getLeft();
			Column rightColumn = exp.getRight();

			RealVariable leftVariable = solverAPI
					.createRealVariable(leftColumn);
			RealVariable rightVariable = solverAPI
					.createRealVariable(rightColumn);
			double constant = exp.getConstant();
			RealVariable c = solverAPI.createRealNum(rightColumn, constant);
			SMTLibExpression expr = null;

			if (constant == 0) {
				expr = solverAPI.createLT(leftVariable, rightVariable);
			} else {
				expr = solverAPI.createLTc(leftVariable, rightVariable, c);
			}
			return expr;

		} else {
			// left == null, right == null
			// so: left = right + c == true
			return solverAPI.createBooleanValue(true);
		}
	}

	@Override
	public SMTLibExpression visit(LessVarcharComparison exp)
			throws SolverException {

		if (exp.getRight() == null && exp.getLeft() != null) {
			Column leftColumn = exp.getLeft();
			String rightBitVector = null;

			if (solverAPI instanceof MathSatAPI) {
				rightBitVector = Solver.toBinaryBitVector(
						exp.getRightVarchar(), Solver.CHARSET);
			} else {
				rightBitVector = Solver.toDecimalBitVector(
						exp.getRightVarchar(), Solver.CHARSET);
			}
			BitvectorVariable leftVariable = solverAPI.createBitvectorVariable(
					leftColumn, getVarcharLength(leftColumn));

			BitvectorVariable rightVariable = solverAPI.createBitvectorString(
					null, rightBitVector, exp.getRightVarchar().length()
							* Solver.BITS_PER_CHARACTER);

			return solverAPI.createLT(leftVariable, rightVariable);

		} else if (exp.getRight() != null && exp.getLeft() == null) {
			Column rightColumn = exp.getRight();
			String leftBitVector = null;

			if (solverAPI instanceof MathSatAPI) {
				leftBitVector = Solver.toBinaryBitVector(exp.getLeftVarchar(),
						Solver.CHARSET);
			} else {
				leftBitVector = Solver.toDecimalBitVector(exp.getLeftVarchar(),
						Solver.CHARSET);
			}

			BitvectorVariable rightVariable = solverAPI
					.createBitvectorVariable(rightColumn,
							getVarcharLength(rightColumn));

			BitvectorVariable leftVariable = solverAPI.createBitvectorString(
					null, leftBitVector, exp.getLeftVarchar().length()
							* Solver.BITS_PER_CHARACTER);
			return solverAPI.createLT(leftVariable, rightVariable);

		} else if (exp.getRight() != null && exp.getLeft() != null) {
			Column leftColumn = exp.getLeft();
			Column rightColumn = exp.getRight();

			BitvectorVariable leftVariable = solverAPI.createBitvectorVariable(
					leftColumn, getVarcharLength(leftColumn));
			BitvectorVariable rightVariable = solverAPI
					.createBitvectorVariable(rightColumn,
							getVarcharLength(rightColumn));
			return solverAPI.createLT(leftVariable, rightVariable);

		} else {
			// left == null, right == null -> true
			return solverAPI.createBooleanValue(true);
		}
	}

	@Override
	public SMTLibExpression visit(LessOrEqualDoubleComparison exp)
			throws SolverException {

		if (exp.getRight() == null && exp.getLeft() != null) {
			Column leftColumn = exp.getLeft();
			RealVariable leftVariable = solverAPI
					.createRealVariable(leftColumn);
			RealVariable rightVariable = solverAPI.createRealNum(null,
					exp.getConstant());
			return solverAPI.createLE(leftVariable, rightVariable);

		} else if (exp.getRight() != null && exp.getLeft() == null) {
			Column rightColumn = exp.getRight();
			RealVariable rightVariable = solverAPI
					.createRealVariable(rightColumn);
			RealVariable leftVariable = solverAPI.createRealNum(null,
					exp.getConstant());
			return solverAPI.createLE(leftVariable, rightVariable);

		} else if (exp.getRight() != null && exp.getLeft() != null) {
			Column leftColumn = exp.getLeft();
			Column rightColumn = exp.getRight();

			RealVariable leftVariable = solverAPI
					.createRealVariable(leftColumn);
			RealVariable rightVariable = solverAPI
					.createRealVariable(rightColumn);

			double constant = exp.getConstant();
			RealVariable c = solverAPI.createRealNum(rightColumn, constant);
			SMTLibExpression expr = null;

			if (constant == 0) {
				expr = solverAPI.createLE(leftVariable, rightVariable);
			} else {
				expr = solverAPI.createLEc(leftVariable, rightVariable, c);
			}
			return expr;

		} else {
			// left == null, right == null
			// so: left = right + c == true
			return solverAPI.createBooleanValue(true);
		}
	}

	@Override
	public SMTLibExpression visit(LessOrEqualIntegerComparison exp)
			throws SolverException {

		if (exp.getRight() == null && exp.getLeft() != null) {
			Column leftColumn = exp.getLeft();
			IntVariable leftVariable = solverAPI.createIntVariable(leftColumn);
			IntVariable rightVariable = solverAPI.createIntNum(null,
					exp.getConstant());
			return solverAPI.createLE(leftVariable, rightVariable);

		} else if (exp.getRight() != null && exp.getLeft() == null) {
			Column rightColumn = exp.getRight();
			IntVariable rightVariable = solverAPI
					.createIntVariable(rightColumn);
			IntVariable leftVariable = solverAPI.createIntNum(null,
					exp.getConstant());
			return solverAPI.createLE(leftVariable, rightVariable);

		} else if (exp.getRight() != null && exp.getLeft() != null) {
			Column leftColumn = exp.getLeft();
			Column rightColumn = exp.getRight();

			IntVariable leftVariable = solverAPI.createIntVariable(leftColumn);
			IntVariable rightVariable = solverAPI
					.createIntVariable(rightColumn);

			int constant = exp.getConstant();
			IntVariable c = solverAPI.createIntNum(rightColumn, constant);
			SMTLibExpression expr = null;

			if (constant == 0) {
				expr = solverAPI.createLE(leftVariable, rightVariable);
			} else {
				expr = solverAPI.createLEc(leftVariable, rightVariable, c);
			}
			return expr;

		} else {
			// left == null, right == null
			// so: left = right + c == true
			return solverAPI.createBooleanValue(true);
		}
	}

	@Override
	public SMTLibExpression visit(LessOrEqualVarcharComparison exp)
			throws SolverException {

		if (exp.getRight() == null && exp.getLeft() != null) {
			Column leftColumn = exp.getLeft();
			String rightBitVector = null;

			if (solverAPI instanceof MathSatAPI) {
				rightBitVector = Solver.toBinaryBitVector(
						exp.getRightVarchar(), Solver.CHARSET);
			} else {
				rightBitVector = Solver.toDecimalBitVector(
						exp.getRightVarchar(), Solver.CHARSET);
			}

			BitvectorVariable leftVariable = solverAPI.createBitvectorVariable(
					leftColumn, getVarcharLength(leftColumn));
			BitvectorVariable rightVariable = solverAPI.createBitvectorString(
					null, rightBitVector, exp.getRightVarchar().length()
							* Solver.BITS_PER_CHARACTER);
			return solverAPI.createLE(leftVariable, rightVariable);

		} else if (exp.getRight() != null && exp.getLeft() == null) {
			Column rightColumn = exp.getRight();
			String leftBitVector = null;

			if (solverAPI instanceof MathSatAPI) {
				leftBitVector = Solver.toBinaryBitVector(exp.getLeftVarchar(),
						Solver.CHARSET);
			} else {
				leftBitVector = Solver.toDecimalBitVector(exp.getLeftVarchar(),
						Solver.CHARSET);
			}

			BitvectorVariable rightVariable = solverAPI
					.createBitvectorVariable(rightColumn,
							getVarcharLength(rightColumn));
			BitvectorVariable leftVariable = solverAPI.createBitvectorString(
					null, leftBitVector, exp.getLeftVarchar().length()
							* Solver.BITS_PER_CHARACTER);
			return solverAPI.createLE(leftVariable, rightVariable);

		} else if (exp.getRight() != null && exp.getLeft() != null) {
			Column leftColumn = exp.getLeft();
			Column rightColumn = exp.getRight();

			BitvectorVariable leftVariable = solverAPI.createBitvectorVariable(
					leftColumn, getVarcharLength(leftColumn));
			BitvectorVariable rightVariable = solverAPI
					.createBitvectorVariable(rightColumn,
							getVarcharLength(rightColumn));

			return solverAPI.createLE(leftVariable, rightVariable);

		} else {
			// left == null, right == null -> true
			return solverAPI.createBooleanValue(true);
		}
	}

	@Override
	public SMTLibExpression visit(NotEqualDoubleComparison exp)
			throws SolverException {
		if (exp.getRight() == null && exp.getLeft() != null) {
			Column leftColumn = exp.getLeft();
			RealVariable leftVariable = solverAPI
					.createRealVariable(leftColumn);
			RealVariable rightVariable = solverAPI.createRealNum(null,
					exp.getConstant());
			return solverAPI.createNotEqual(leftVariable, rightVariable);

		} else if (exp.getRight() != null && exp.getLeft() == null) {
			Column rightColumn = exp.getRight();
			RealVariable rightVariable = solverAPI
					.createRealVariable(rightColumn);
			RealVariable leftVariable = solverAPI.createRealNum(null,
					exp.getConstant());
			return solverAPI.createNotEqual(leftVariable, rightVariable);

		} else if (exp.getRight() != null && exp.getLeft() != null) {
			Column leftColumn = exp.getLeft();
			Column rightColumn = exp.getRight();

			RealVariable leftVariable = solverAPI
					.createRealVariable(leftColumn);
			RealVariable rightVariable = solverAPI
					.createRealVariable(rightColumn);

			double constant = exp.getConstant();
			RealVariable c = solverAPI.createRealNum(rightColumn, constant);
			SMTLibExpression expr = null;

			if (constant == 0) {
				expr = solverAPI.createNotEqual(leftVariable, rightVariable);
			} else {
				expr = solverAPI
						.createNotEqualc(leftVariable, rightVariable, c);
			}
			return expr;

		} else {
			// left == null, right == null
			// so: left = right + c == true
			return solverAPI.createBooleanValue(true);
		}
	}

	@Override
	public SMTLibExpression visit(NotEqualIntegerComparison exp)
			throws SolverException {
		if (exp.getRight() == null && exp.getLeft() != null) {
			Column leftColumn = exp.getLeft();
			IntVariable leftVariable = solverAPI.createIntVariable(leftColumn);
			IntVariable rightVariable = solverAPI.createIntNum(null,
					exp.getConstant());
			return solverAPI.createNotEqual(leftVariable, rightVariable);

		} else if (exp.getRight() != null && exp.getLeft() == null) {
			Column rightColumn = exp.getRight();
			IntVariable rightVariable = solverAPI
					.createIntVariable(rightColumn);
			IntVariable leftVariable = solverAPI.createIntNum(null,
					exp.getConstant());
			return solverAPI.createNotEqual(leftVariable, rightVariable);

		} else if (exp.getRight() != null && exp.getLeft() != null) {
			Column leftColumn = exp.getLeft();
			Column rightColumn = exp.getRight();

			IntVariable leftVariable = solverAPI.createIntVariable(leftColumn);
			IntVariable rightVariable = solverAPI
					.createIntVariable(rightColumn);

			int constant = exp.getConstant();
			IntVariable c = solverAPI.createIntNum(rightColumn, constant);
			SMTLibExpression expr = null;

			if (constant == 0) {
				expr = solverAPI.createNotEqual(leftVariable, rightVariable);
			} else {
				expr = solverAPI
						.createNotEqualc(leftVariable, rightVariable, c);
			}
			return expr;

		} else {
			// left == null, right == null
			// so: left = right + c == true
			return solverAPI.createBooleanValue(true);
		}
	}

	@Override
	public SMTLibExpression visit(NotEqualVarcharComparison exp)
			throws SolverException {

		if (exp.getRight() == null && exp.getLeft() != null) {
			Column leftColumn = exp.getLeft();
			String rightBitVector = null;

			if (solverAPI instanceof MathSatAPI) {
				rightBitVector = Solver.toBinaryBitVector(
						exp.getRightVarchar(), Solver.CHARSET);
			} else {
				rightBitVector = Solver.toDecimalBitVector(
						exp.getRightVarchar(), Solver.CHARSET);
			}

			BitvectorVariable leftVariable = solverAPI.createBitvectorVariable(
					leftColumn, getVarcharLength(leftColumn));
			BitvectorVariable rightVariable = solverAPI.createBitvectorString(
					null, rightBitVector, exp.getRightVarchar().length()
							* Solver.BITS_PER_CHARACTER);
			return solverAPI.createNotEqual(leftVariable, rightVariable);

		} else if (exp.getRight() != null && exp.getLeft() == null) {
			Column rightColumn = exp.getRight();
			String leftBitVector = null;

			if (solverAPI instanceof MathSatAPI) {
				leftBitVector = Solver.toBinaryBitVector(exp.getLeftVarchar(),
						Solver.CHARSET);
			} else {
				leftBitVector = Solver.toDecimalBitVector(exp.getLeftVarchar(),
						Solver.CHARSET);
			}

			BitvectorVariable rightVariable = solverAPI
					.createBitvectorVariable(rightColumn,
							getVarcharLength(rightColumn));
			BitvectorVariable leftVariable = solverAPI.createBitvectorString(
					null, leftBitVector, exp.getLeftVarchar().length()
							* Solver.BITS_PER_CHARACTER);
			return solverAPI.createNotEqual(leftVariable, rightVariable);

		} else if (exp.getRight() != null && exp.getLeft() != null) {
			Column leftColumn = exp.getLeft();
			Column rightColumn = exp.getRight();

			BitvectorVariable leftVariable = solverAPI.createBitvectorVariable(
					leftColumn, getVarcharLength(leftColumn));
			BitvectorVariable rightVariable = solverAPI
					.createBitvectorVariable(rightColumn,
							getVarcharLength(rightColumn));
			return solverAPI.createNotEqual(leftVariable, rightVariable);

		} else {
			// left == null, right == null -> true
			return solverAPI.createBooleanValue(true);
		}
	}

	@Override
	public SMTLibExpression visit(BooleanLiteral booleanLiteral)
			throws SolverException {
		Column col = booleanLiteral.getColumn();
		SMTLibExpression boolVariable = solverAPI.createBoolVariable(col);
		return boolVariable;
	}

	@Override
	public SMTLibExpression visit(NegatedBooleanLiteral negatedBooleanLiteral)
			throws SolverException {
		Column col = negatedBooleanLiteral.getColumn();
		SMTLibExpression expr = solverAPI.createBoolVariable(col);

		return solverAPI.createNOT(expr);
	}
}
