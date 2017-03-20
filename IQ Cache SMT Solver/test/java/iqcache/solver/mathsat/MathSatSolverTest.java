package iqcache.solver.mathsat;

import static org.junit.Assert.*;
import iqcache.expression.Expression;
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
import iqcache.expression.node.NodeExpression;
import iqcache.expression.node.Not;
import iqcache.expression.node.Or;
import iqcache.query.Identifier;
import iqcache.query.column.BooleanType;
import iqcache.query.column.Column;
import iqcache.query.column.DoubleType;
import iqcache.query.column.IntegerType;
import iqcache.query.column.VarcharType;
import iqcache.satisfiability.SatisfiabilityChecker;
import iqcache.satisfiability.SatisfiabilityException;
import iqcache.solver.mathsat.MathSatSolver;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit Tests for a MathSAT 5 solver.
 * @author dinh
 * 
 */
public class MathSatSolverTest {

	private SatisfiabilityChecker checker;
	private Expression expression;

	// define columns
	private Column ix = new Column(new Identifier("t"), new Identifier("ix"),
			1, IntegerType.instance(), false, false);

	private Column iy = new Column(new Identifier("t"), new Identifier("iy"),
			1, IntegerType.instance(), false, false);

	private Column iz = new Column(new Identifier("t"), new Identifier("iz"),
			1, IntegerType.instance(), false, false);

	private Column dx = new Column(new Identifier("t"), new Identifier("dx"),
			1, DoubleType.instance(), false, false);

	private Column dy = new Column(new Identifier("t"), new Identifier("dy"),
			1, DoubleType.instance(), false, false);

	private Column dz = new Column(new Identifier("t"), new Identifier("dz"),
			1, DoubleType.instance(), false, false);

	private Column sx = new Column(new Identifier("t"), new Identifier("sx"),
			1, VarcharType.instance(20), false, false);

	private Column sy = new Column(new Identifier("t"), new Identifier("sy"),
			1, VarcharType.instance(20), false, false);

	private Column sz = new Column(new Identifier("t"), new Identifier("sz"),
			1, VarcharType.instance(20), false, false);

	private Column bx = new Column(new Identifier("t"), new Identifier("bx"),
			1, BooleanType.instance(), false, false);

	private Column by = new Column(new Identifier("t"), new Identifier("by"),
			1, BooleanType.instance(), false, false);

	private Column bz = new Column(new Identifier("t"), new Identifier("bz"),
			1, BooleanType.instance(), false, false);

	@Before
	public void setUp() throws Exception {
		checker = new MathSatSolver();
	}

	@After
	public void tearDown() throws Exception {
		checker = null;
		expression = null;
	}

	@Test
	public void testIsSatisfiable() throws SatisfiabilityException {
		NodeExpression exp = new And(new BooleanValue(true), new BooleanValue(
				false));

		assertFalse(checker.isSatisfiable(exp));
	}

	@Test
	public void testIsSatisfiable2() throws SatisfiabilityException {
		Expression exp = new Or(new BooleanValue(true), new BooleanValue(false));

		assertTrue(checker.isSatisfiable(exp));
	}

	@Test
	public void testIsSatisfiable3() throws SatisfiabilityException {
		Expression exp = new And(new BooleanValue(true), new Or(
				new BooleanValue(true), new BooleanValue(false)));

		assertTrue(checker.isSatisfiable(exp));
	}

	@Test(expected = NullPointerException.class)
	public void testIsSatisfiableNull() throws SatisfiabilityException {
		checker.isSatisfiable(null);
	}

	@Test
	public void testEqualIntegerComparison1() throws SatisfiabilityException {
		Expression exp = new EqualIntegerComparison(ix, iy, 42);
		assertTrue(checker.isSatisfiable(exp));
	}

	@Test
	public void testEqualIntegerComparison2() throws SatisfiabilityException {
		Expression exp = new EqualIntegerComparison(ix, iy, 0);
		assertTrue(checker.isSatisfiable(exp));
	}

	@Test
	public void testEqualIntegerComparison3() throws SatisfiabilityException {
		Expression exp = new EqualIntegerComparison(ix, null, Integer.MAX_VALUE);
		assertTrue(checker.isSatisfiable(exp));
	}

	@Test
	public void testEqualIntegerComparison4() throws SatisfiabilityException {
		Expression exp = new EqualIntegerComparison(null, iy, Integer.MIN_VALUE);
		assertTrue(checker.isSatisfiable(exp));
	}

	@Test
	public void testEqualIntegerComparison5() throws SatisfiabilityException {
		Expression exp = new And(new EqualIntegerComparison(ix, null, 21),
				new EqualIntegerComparison(iy, null, 21),
				new EqualIntegerComparison(ix, iy, 0));
		assertTrue(checker.isSatisfiable(exp));
	}

	@Test
	public void testEqualIntegerComparison6() throws SatisfiabilityException {
		Expression exp = new And(new EqualIntegerComparison(ix, null, 21),
				new EqualIntegerComparison(iy, null, 21),
				new EqualIntegerComparison(ix, iy, -1));
		assertFalse(checker.isSatisfiable(exp));
	}

	@Test
	public void testSatisfiability1() throws SatisfiabilityException {

		// 1.) SAT( true AND false ) = false
		expression = new And(new BooleanValue(true), new BooleanValue(false));
		assertFalse(checker.isSatisfiable(expression));

	}

	@Test
	public void testSatisfiability2() throws SatisfiabilityException {

		// 2.) SAT( x >= 2 OR x <= 2 ) = true
		expression = new Or(new LessOrEqualIntegerComparison(ix, null, 2),
				new LessOrEqualIntegerComparison(null, ix, -2));
		assertTrue(checker.isSatisfiable(expression));
	}

	@Test
	public void testSatisfiability3() throws SatisfiabilityException {

		// 3.)
		expression = new And(new LessOrEqualVarcharComparison(sx, sy),
				new LessOrEqualVarcharComparison(sy, sz),
				new LessOrEqualVarcharComparison(sz, sx),
				new LessOrEqualVarcharComparison(sx, "Hi"),
				new LessOrEqualVarcharComparison("Hi", sy));
		assertTrue(checker.isSatisfiable(expression));
	}

	@Test
	public void testSatisfiability4() throws SatisfiabilityException {

		// 4.)
		expression = new And(new LessOrEqualVarcharComparison(sx, sy),
				new LessOrEqualVarcharComparison(sy, sz),
				new LessOrEqualVarcharComparison(sz, sx),
				new LessOrEqualVarcharComparison(sx, "Hi"),
				new LessOrEqualVarcharComparison("Ho", sy));
		assertFalse(checker.isSatisfiable(expression));
	}

	@Test
	public void testSatisfiability5() throws SatisfiabilityException {

		// 5.)
		expression = new And(new LessOrEqualVarcharComparison(sx, sy),
				new LessOrEqualVarcharComparison(sy, sz),
				new LessOrEqualVarcharComparison(sz, sx),
				new LessOrEqualVarcharComparison(sx, "Ho"),
				new LessOrEqualVarcharComparison("Hi", sy));
		assertTrue(checker.isSatisfiable(expression));

	}

	@Test
	public void testSatisfiability6() throws SatisfiabilityException {

		// 6.)
		expression = new And(new EqualIntegerComparison(ix, iy, 0), new Not(
				new EqualIntegerComparison(ix, iy, 0)));
		assertFalse(checker.isSatisfiable(expression));

	}

	@Test
	public void testSatisfiability7() throws SatisfiabilityException {

		// 7.)
		expression = new LessVarcharComparison(sx, sx);
		assertFalse(checker.isSatisfiable(expression));

	}

	@Test
	public void testSatisfiability8() throws SatisfiabilityException {

		// 8.)
		expression = new And(new LessVarcharComparison(sx, sy),
				new LessOrEqualVarcharComparison(sy, sz),
				new LessOrEqualVarcharComparison(sz, sx));
		assertFalse(checker.isSatisfiable(expression));

	}

	@Test
	public void testSatisfiability9() throws SatisfiabilityException {

		// 9.)
		Column sx3 = new Column(new Identifier("t"), new Identifier("sx3"), 1,
				VarcharType.instance(3), false, false);
		Column sy2 = new Column(new Identifier("t"), new Identifier("sy2"), 1,
				VarcharType.instance(2), false, false);
		Column sz3 = new Column(new Identifier("t"), new Identifier("sz3"), 1,
				VarcharType.instance(3), false, false);
		expression = new And(new EqualVarcharComparison(sx3, "aaa"),
				new EqualVarcharComparison("aab", sz3),
				new LessVarcharComparison(sx3, sy2), new LessVarcharComparison(
						sy2, sz3));
		assertFalse(checker.isSatisfiable(expression));

	}

	@Test
	public void testSatisfiability10() throws SatisfiabilityException {

		// 10.) 'aaa' < sy2 < 'aba'
		Column sx3 = new Column(new Identifier("t"), new Identifier("sx3"), 1,
				VarcharType.instance(3), false, false);
		Column sy2 = new Column(new Identifier("t"), new Identifier("sy2"), 1,
				VarcharType.instance(2), false, false);
		Column sz3 = new Column(new Identifier("t"), new Identifier("sz3"), 1,
				VarcharType.instance(3), false, false);
		expression = new And(new EqualVarcharComparison(sx3, "aaa"),
				new EqualVarcharComparison(sz3, "aba"),
				new LessVarcharComparison(sx3, sy2), new LessVarcharComparison(
						sy2, sz3));
		assertTrue(checker.isSatisfiable(expression));

	}

	@Test
	public void testSatisfiability100() throws SatisfiabilityException {

		// 10.0) 'aaa' < 'aba' < 'ab'
		Column sx3 = new Column(new Identifier("t"), new Identifier("sx3"), 1,
				VarcharType.instance(3), false, false);
		Column sy2 = new Column(new Identifier("t"), new Identifier("sy2"), 1,
				VarcharType.instance(2), false, false);
		Column sz3 = new Column(new Identifier("t"), new Identifier("sz3"), 1,
				VarcharType.instance(3), false, false);
		expression = new And(new EqualVarcharComparison(sx3, "aaa"),
				new EqualVarcharComparison(sz3, "aba"),
				new EqualVarcharComparison(sy2, "ab"),
				new LessVarcharComparison(sx3, sy2), new LessVarcharComparison(
						sy2, sz3));
		assertTrue(checker.isSatisfiable(expression));

	}

	@Test
	public void testSatisfiability1011() throws SatisfiabilityException {

		// 10.11) 'aaa' < 'ab'
		Column sx3 = new Column(new Identifier("t"), new Identifier("sx3"), 1,
				VarcharType.instance(3), false, false);
		Column sy2 = new Column(new Identifier("t"), new Identifier("sy2"), 1,
				VarcharType.instance(2), false, false);
		expression = new And(new EqualVarcharComparison(sx3, "aaa"),
				new EqualVarcharComparison(sy2, "ab"),
				new LessVarcharComparison(sx3, sy2));
		assertTrue(checker.isSatisfiable(expression));

	}

	@Test
	public void testSatisfiability1012() throws SatisfiabilityException {

		// 10.12) 'aaa' < 'ab', with variables size 3
		Column sx3 = new Column(new Identifier("t"), new Identifier("sx3"), 1,
				VarcharType.instance(3), false, false);
		Column sy3 = new Column(new Identifier("t"), new Identifier("sy3"), 1,
				VarcharType.instance(3), false, false);
		expression = new And(new EqualVarcharComparison(sx3, "aaa"),
				new EqualVarcharComparison(sy3, "ab"),
				new LessVarcharComparison(sx3, sy3));
		assertTrue(checker.isSatisfiable(expression));

	}

	@Test
	public void testSatisfiability1013() throws SatisfiabilityException {

		// 10.13) 'aa' < 'b'
		Column sx3 = new Column(new Identifier("t"), new Identifier("sx3"), 1,
				VarcharType.instance(3), false, false);
		Column sy2 = new Column(new Identifier("t"), new Identifier("sy2"), 1,
				VarcharType.instance(2), false, false);
		expression = new And(new EqualVarcharComparison(sx3, "aa"),
				new EqualVarcharComparison(sy2, "b"),
				new LessVarcharComparison(sx3, sy2));
		assertTrue(checker.isSatisfiable(expression));

	}

	@Test
	public void testSatisfiability1014() throws SatisfiabilityException {

		// 10.14) 'ab' < 'aaa'
		Column sx3 = new Column(new Identifier("t"), new Identifier("sx3"), 1,
				VarcharType.instance(3), false, false);
		Column sy2 = new Column(new Identifier("t"), new Identifier("sy2"), 1,
				VarcharType.instance(2), false, false);
		expression = new And(new EqualVarcharComparison(sx3, "aaa"),
				new EqualVarcharComparison(sy2, "ab"),
				new LessVarcharComparison(sy2, sx3));
		assertFalse(checker.isSatisfiable(expression));

	}

	@Test
	public void testSatisfiability1015() throws SatisfiabilityException {

		// 10.15) 'aab' < 'b'
		Column sx3 = new Column(new Identifier("t"), new Identifier("sx3"), 1,
				VarcharType.instance(3), false, false);
		Column sy1 = new Column(new Identifier("t"), new Identifier("sy1"), 1,
				VarcharType.instance(1), false, false);
		expression = new And(new EqualVarcharComparison(sx3, "aab"),
				new EqualVarcharComparison(sy1, "b"),
				new LessVarcharComparison(sx3, sy1));
		assertTrue(checker.isSatisfiable(expression));

	}

	@Test
	public void testSatisfiability1016() throws SatisfiabilityException {

		// 10.16) 'aab' < 'ac'
		Column sx3 = new Column(new Identifier("t"), new Identifier("sx3"), 1,
				VarcharType.instance(3), false, false);
		Column sy2 = new Column(new Identifier("t"), new Identifier("sy2"), 1,
				VarcharType.instance(2), false, false);
		expression = new And(new EqualVarcharComparison(sx3, "aab"),
				new EqualVarcharComparison(sy2, "ac"),
				new LessVarcharComparison(sx3, sy2));
		assertTrue(checker.isSatisfiable(expression));

	}

	@Test
	public void testSatisfiability1017() throws SatisfiabilityException {

		// 10.17) 'aab' < 'ac' with variables size 3
		Column sx3 = new Column(new Identifier("t"), new Identifier("sx3"), 1,
				VarcharType.instance(3), false, false);
		Column sy3 = new Column(new Identifier("t"), new Identifier("sy3"), 1,
				VarcharType.instance(3), false, false);
		expression = new And(new EqualVarcharComparison(sx3, "aab"),
				new EqualVarcharComparison(sy3, "ac"),
				new LessVarcharComparison(sx3, sy3));
		assertTrue(checker.isSatisfiable(expression));

	}

	@Test
	public void testSatisfiability1018() throws SatisfiabilityException {

		// 10.18) 'aaa' < 'aa'
		Column sx3 = new Column(new Identifier("t"), new Identifier("sx3"), 1,
				VarcharType.instance(3), false, false);
		Column sy3 = new Column(new Identifier("t"), new Identifier("sy3"), 1,
				VarcharType.instance(3), false, false);
		expression = new And(new EqualVarcharComparison(sx3, "aaa"),
				new EqualVarcharComparison(sy3, "aa"),
				new LessVarcharComparison(sx3, sy3));
		assertFalse(checker.isSatisfiable(expression));

	}

	@Test
	public void testSatisfiability102() throws SatisfiabilityException {

		// 10.2) 'ab' < 'aba'
		Column sy2 = new Column(new Identifier("t"), new Identifier("sy2"), 1,
				VarcharType.instance(2), false, false);
		Column sz3 = new Column(new Identifier("t"), new Identifier("sz3"), 1,
				VarcharType.instance(3), false, false);
		expression = new And(new EqualVarcharComparison(sz3, "aba"),
				new EqualVarcharComparison(sy2, "ab"),
				new LessVarcharComparison(sy2, sz3));
		assertTrue(checker.isSatisfiable(expression));

	}

	@Test
	public void testSatisfiability1025() throws SatisfiabilityException {

		// 10.25) 'ab' < 'aba' with variables size 3
		Column sy3 = new Column(new Identifier("t"), new Identifier("sy3"), 1,
				VarcharType.instance(3), false, false);
		Column sz3 = new Column(new Identifier("t"), new Identifier("sz3"), 1,
				VarcharType.instance(3), false, false);
		expression = new And(new EqualVarcharComparison(sz3, "aba"),
				new EqualVarcharComparison(sy3, "ab"),
				new LessVarcharComparison(sy3, sz3));
		assertTrue(checker.isSatisfiable(expression));

	}

	@Test
	public void testSatisfiability103() throws SatisfiabilityException {

		// 10.3) 'aa' < 'ab'
		Column sy2 = new Column(new Identifier("t"), new Identifier("sy2"), 1,
				VarcharType.instance(2), false, false);
		Column sz3 = new Column(new Identifier("t"), new Identifier("sz3"), 1,
				VarcharType.instance(3), false, false);
		expression = new And(new EqualVarcharComparison(sz3, "ab"),
				new EqualVarcharComparison(sy2, "aa"),
				new LessVarcharComparison(sy2, sz3));
		assertTrue(checker.isSatisfiable(expression));

	}

	@Test
	public void testSatisfiability104() throws SatisfiabilityException {

		// 10.4) 'aaa' < 'aab' < 'ab'
		Column sx3 = new Column(new Identifier("t"), new Identifier("sx3"), 1,
				VarcharType.instance(3), false, false);
		Column sy3 = new Column(new Identifier("t"), new Identifier("sy3"), 1,
				VarcharType.instance(3), false, false);
		Column sz3 = new Column(new Identifier("t"), new Identifier("sz3"), 1,
				VarcharType.instance(3), false, false);
		expression = new And(new EqualVarcharComparison(sx3, "aaa"),
				new EqualVarcharComparison(sz3, "ab"),
				new EqualVarcharComparison(sy3, "aab"),
				new LessVarcharComparison(sx3, sy3), new LessVarcharComparison(
						sy3, sz3));
		assertTrue(checker.isSatisfiable(expression));
	}

	@Test
	public void testSatisfiability1040() throws SatisfiabilityException {

		// 10.40) 'aab' < 'ab'
		Column sy3 = new Column(new Identifier("t"), new Identifier("sy3"), 1,
				VarcharType.instance(3), false, false);
		Column sz3 = new Column(new Identifier("t"), new Identifier("sz3"), 1,
				VarcharType.instance(3), false, false);
		expression = new And(new EqualVarcharComparison(sz3, "ab"),
				new EqualVarcharComparison(sy3, "aab"),
				new LessVarcharComparison(sy3, sz3));
		assertTrue(checker.isSatisfiable(expression));
	}

	@Test
	public void testSatisfiability105() throws SatisfiabilityException {

		// 10.5) 'aa' < sy2 < 'ab'
		Column sx3 = new Column(new Identifier("t"), new Identifier("sx3"), 1,
				VarcharType.instance(3), false, false);
		Column sy2 = new Column(new Identifier("t"), new Identifier("sy2"), 1,
				VarcharType.instance(2), false, false);
		Column sz3 = new Column(new Identifier("t"), new Identifier("sz3"), 1,
				VarcharType.instance(3), false, false);
		expression = new And(new EqualVarcharComparison(sx3, "aa"),
				new EqualVarcharComparison(sz3, "ab"),
				new LessVarcharComparison(sx3, sy2), new LessVarcharComparison(
						sy2, sz3));
		assertFalse(checker.isSatisfiable(expression));
	}

	@Test
	public void testSatisfiability106() throws SatisfiabilityException {

		// 10.6) 'a' < 'b'
		Column sy2 = new Column(new Identifier("t"), new Identifier("sy2"), 1,
				VarcharType.instance(2), false, false);
		Column sz3 = new Column(new Identifier("t"), new Identifier("sz3"), 1,
				VarcharType.instance(3), false, false);
		expression = new And(new EqualVarcharComparison(sz3, "b"),
				new EqualVarcharComparison(sy2, "a"),
				new LessVarcharComparison(sy2, sz3));
		assertTrue(checker.isSatisfiable(expression));

	}

	@Test
	public void testSatisfiability107() throws SatisfiabilityException {

		// 10.7) 'a' < sy1 < 'b'
		Column sx3 = new Column(new Identifier("t"), new Identifier("sx3"), 1,
				VarcharType.instance(3), false, false);
		Column sy1 = new Column(new Identifier("t"), new Identifier("sy1"), 1,
				VarcharType.instance(1), false, false);
		Column sz3 = new Column(new Identifier("t"), new Identifier("sz3"), 1,
				VarcharType.instance(3), false, false);
		expression = new And(new EqualVarcharComparison(sx3, "a"),
				new EqualVarcharComparison(sz3, "b"),
				new LessVarcharComparison(sx3, sy1), new LessVarcharComparison(
						sy1, sz3));
		assertFalse(checker.isSatisfiable(expression));

	}

	@Test
	public void testSatisfiability11() throws SatisfiabilityException {

		// 11.)
		Column sx3 = new Column(new Identifier("t"), new Identifier("sx3"), 1,
				VarcharType.instance(3), false, false);
		Column sy2 = new Column(new Identifier("t"), new Identifier("sy2"), 1,
				VarcharType.instance(2), false, false);
		Column sz3 = new Column(new Identifier("t"), new Identifier("sz3"), 1,
				VarcharType.instance(3), false, false);
		expression = new And(new EqualVarcharComparison(sx3, "aaa"),
				new EqualVarcharComparison(sz3, "ab"),
				new LessVarcharComparison(sx3, sy2), new LessVarcharComparison(
						sy2, sz3));

		assertFalse(checker.isSatisfiable(expression));

	}

	@Test
	public void testSatisfiability12() throws SatisfiabilityException {

		// 12.)
		Column sx3 = new Column(new Identifier("t"), new Identifier("sx3"), 1,
				VarcharType.instance(3), false, false);
		expression = new EqualVarcharComparison(sx3, "aaaa");

		assertFalse(checker.isSatisfiable(expression));

	}

	@Test
	public void testSatisfiability13() throws SatisfiabilityException {

		// 13.)
		Column sx3 = new Column(new Identifier("t"), new Identifier("sx3"), 1,
				VarcharType.instance(3), false, false);
		expression = new EqualVarcharComparison(sx3, "aaa");

		assertTrue(checker.isSatisfiable(expression));

	}

	@Test
	public void testSatisfiability14() throws SatisfiabilityException {

		// 14.)
		expression = new And(new Or(new LessOrEqualIntegerComparison(ix, null,
				-2), new LessOrEqualIntegerComparison(ix, null, -2),
				new LessOrEqualIntegerComparison(ix, null, -2)), new Or(
				new LessOrEqualIntegerComparison(null, ix, -3),
				new LessOrEqualIntegerComparison(null, iy, -3),
				new LessOrEqualIntegerComparison(null, iz, -3)), new And(
				new LessOrEqualIntegerComparison(iz, null, 1),
				new LessOrEqualIntegerComparison(null, iz, 1)));
		assertTrue(checker.isSatisfiable(expression));

	}

	@Test
	public void testSatisfiability15() throws SatisfiabilityException {

		// 15.)
		expression = new And(new LessDoubleComparison(dz, dy, 0.0),
				new LessDoubleComparison(dy, dx, 0.0),
				new LessDoubleComparison(dx, dz, 0.0));
		assertFalse(checker.isSatisfiable(expression));
	}

	@Test
	public void testSatisfiability17() throws SatisfiabilityException {

		// 17.)
		expression = new LessDoubleComparison(dx, dx, 0.0);
		assertFalse(checker.isSatisfiable(expression));

	}

	@Test
	public void testSatisfiability18() throws SatisfiabilityException {

		// 18.)
		expression = new LessVarcharComparison(sx, sx);
		assertFalse(checker.isSatisfiable(expression));
	}

	@Test
	public void testSatisfiability19() throws SatisfiabilityException {

		expression = new And(new LessOrEqualIntegerComparison(ix, iy, 0),
				new LessOrEqualIntegerComparison(iy, iz, 0),
				new LessOrEqualIntegerComparison(iz, ix, 0));
		assertTrue(checker.isSatisfiable(expression));

	}

	@Test
	public void testSatisfiability20() throws SatisfiabilityException {

		expression = new And(new LessDoubleComparison(dx, dy, 0),
				new LessDoubleComparison(dy, dz, 0), new LessDoubleComparison(
						dz, dx, 0));
		assertFalse(checker.isSatisfiable(expression));

	}

	@Test
	public void testSatisfiability21() throws SatisfiabilityException {

		expression = new And(new EqualDoubleComparison(dx, dy, 1),
				new EqualDoubleComparison(dy, dz, 1),
				new EqualDoubleComparison(dz, dx, 1));
		assertFalse(checker.isSatisfiable(expression));
	}

	@Test
	public void testSatisfiability22() throws SatisfiabilityException {

		expression = new And(new EqualDoubleComparison(dx, dy, 1),
				new EqualDoubleComparison(null, dy, 0),
				new EqualDoubleComparison(dx, null, 1));
		assertTrue(checker.isSatisfiable(expression));
	}

	@Test
	public void testSatisfiability23() throws SatisfiabilityException {

		expression = new EqualDoubleComparison(null, null, 1);
		assertTrue(checker.isSatisfiable(expression));
	}

	@Test
	public void testSatisfiability24() throws SatisfiabilityException {

		expression = new EqualIntegerComparison(null, null, 1);
		assertTrue(checker.isSatisfiable(expression));
	}

	@Test
	public void testSatisfiability25() throws SatisfiabilityException {

		expression = new And(new LessDoubleComparison(dx, null, 0), new Not(
				new LessDoubleComparison(dx, null, 0)));
		assertFalse(checker.isSatisfiable(expression));

	}

	@Test
	public void testSatisfiability26() throws SatisfiabilityException {

		expression = new LessDoubleComparison(null, null, 1);
		assertTrue(checker.isSatisfiable(expression));
	}

	@Test
	public void testSatisfiability27() throws SatisfiabilityException {

		expression = new And(new LessOrEqualDoubleComparison(dx, dy, 0),
				new LessOrEqualDoubleComparison(dy, dz, 0),
				new LessOrEqualDoubleComparison(dz, dx, 0));
		assertTrue(checker.isSatisfiable(expression));
	}

	@Test
	public void testSatisfiability28() throws SatisfiabilityException {

		expression = new And(new LessOrEqualDoubleComparison(dx, null, 0),
				new Not(new LessOrEqualDoubleComparison(dx, null, 0)));
		assertFalse(checker.isSatisfiable(expression));
	}

	@Test
	public void testSatisfiability29() throws SatisfiabilityException {

		expression = new LessOrEqualDoubleComparison(null, null, 1);
		assertTrue(checker.isSatisfiable(expression));
	}

	@Test
	public void testSatisfiability30() throws SatisfiabilityException {

		expression = new LessOrEqualIntegerComparison(null, null, 1);
		assertTrue(checker.isSatisfiable(expression));
	}

	@Test
	public void testSatisfiability31() throws SatisfiabilityException {
		expression = new And(new NotEqualDoubleComparison(dx, dy, 0),
				new EqualDoubleComparison(dx, dy, 0));
		assertFalse(checker.isSatisfiable(expression));
	}

	@Test
	public void testSatisfiability32() throws SatisfiabilityException {
		expression = new And(new NotEqualDoubleComparison(dx, null, 0.1d),
				new NotEqualDoubleComparison(null, dy, 0.1d),
				new LessOrEqualDoubleComparison(dx, null, -2.54645645d),
				new LessOrEqualDoubleComparison(dy, null, -3.234d));
		assertTrue(checker.isSatisfiable(expression));
	}

	@Test
	public void testSatisfiability33() throws SatisfiabilityException {

		expression = new NotEqualDoubleComparison(null, null, 1);
		assertTrue(checker.isSatisfiable(expression));
	}

	@Test
	public void testSatisfiability34() throws SatisfiabilityException {
		expression = new And(new NotEqualIntegerComparison(ix, null, 0),
				new NotEqualIntegerComparison(null, iy, 0),
				new LessOrEqualIntegerComparison(ix, null, 0),
				new LessOrEqualIntegerComparison(iy, null, 0));
		assertTrue(checker.isSatisfiable(expression));
	}

	@Test
	public void testSatisfiability35() throws SatisfiabilityException {
		expression = new And(new NotEqualIntegerComparison(ix, iy, 0),
				new EqualIntegerComparison(ix, iy, 0));
		assertFalse(checker.isSatisfiable(expression));
	}

	@Test
	public void testSatisfiability36() throws SatisfiabilityException {

		expression = new NotEqualIntegerComparison(null, null, 1);
		assertTrue(checker.isSatisfiable(expression));
	}

	@Test
	public void testSatisfiability37() throws SatisfiabilityException {
		// (x => y) and (y => z) and x and !z
		Or or1 = new Or(new Not(new BooleanLiteral(bx)), new BooleanLiteral(by));
		Or or2 = new Or(new Not(new BooleanLiteral(by)), new BooleanLiteral(bz));
		expression = new And(or1, or2, new BooleanLiteral(bx),
				new NegatedBooleanLiteral(bz));
		assertFalse(checker.isSatisfiable(expression));
	}

	@Test
	public void testSatisfiability38() throws SatisfiabilityException {
		// (x => y) and (y => z) and x and z
		Or or1 = new Or(new Not(new BooleanLiteral(bx)), new BooleanLiteral(by));
		Or or2 = new Or(new Not(new BooleanLiteral(by)), new BooleanLiteral(bz));
		expression = new And(or1, or2, new BooleanLiteral(bx),
				new BooleanLiteral(bz));
		assertTrue(checker.isSatisfiable(expression));
	}

	@Test
	public void testSatisfiability39() throws SatisfiabilityException {
		expression = new And(new EqualVarcharComparison(sx, "test"),
				new EqualVarcharComparison(sx, sy), new EqualVarcharComparison(
						sy, "test"));

		assertTrue(checker.isSatisfiable(expression));
	}

	@Test
	public void testSatisfiability40() throws SatisfiabilityException {
		expression = new And(new EqualVarcharComparison(sx, "test123"),
				new EqualVarcharComparison(sx, sy), new EqualVarcharComparison(
						sy, "test"));

		assertFalse(checker.isSatisfiable(expression));
	}

	@Test
	public void testSatisfiability41() throws SatisfiabilityException {

		// 41.)
		Column sx4 = new Column(new Identifier("t"), new Identifier("sx4"), 1,
				VarcharType.instance(4), false, false);

		expression = new LessVarcharComparison(sx4, "aa");
		assertTrue(checker.isSatisfiable(expression));
	}

	@Test
	public void testSatisfiability42() throws SatisfiabilityException {

		// 42.) 'ab' != 'AB'
		Column sx2 = new Column(new Identifier("t"), new Identifier("sx2"), 1,
				VarcharType.instance(2), false, false);
		Column sy2 = new Column(new Identifier("t"), new Identifier("sy2"), 1,
				VarcharType.instance(2), false, false);

		expression = new And(new NotEqualVarcharComparison(sx2, sy2),
				new EqualVarcharComparison("AB", sx2),
				new EqualVarcharComparison(sy2, "ab"));
		assertTrue(checker.isSatisfiable(expression));
	}

	@Test
	public void testSatisfiability43() throws SatisfiabilityException {

		// 43.) 'a' != 'a'
		Column sx2 = new Column(new Identifier("t"), new Identifier("sx2"), 1,
				VarcharType.instance(2), false, false);
		Column sy2 = new Column(new Identifier("t"), new Identifier("sy2"), 1,
				VarcharType.instance(2), false, false);

		expression = new And(new EqualVarcharComparison("a", sx2),
				new EqualVarcharComparison(sy2, "a"),
				new NotEqualVarcharComparison(sy2, sx2));
		assertFalse(checker.isSatisfiable(expression));

	}

	@Test
	public void testSatisfiability44() throws SatisfiabilityException {

		// 44.) 'aa' != 'aa' with variables size 3
		Column sx3 = new Column(new Identifier("t"), new Identifier("sx3"), 1,
				VarcharType.instance(3), false, false);
		Column sy3 = new Column(new Identifier("t"), new Identifier("sy3"), 1,
				VarcharType.instance(3), false, false);

		expression = new And(new EqualVarcharComparison("a", sx3),
				new EqualVarcharComparison(sy3, "a"),
				new NotEqualVarcharComparison(sy3, sx3));
		assertFalse(checker.isSatisfiable(expression));

	}

	@Test
	public void testSatisfiability45() throws SatisfiabilityException {

		// 45.) 'aa' != 'aa'
		Column sx2 = new Column(new Identifier("t"), new Identifier("sx2"), 1,
				VarcharType.instance(2), false, false);
		Column sy2 = new Column(new Identifier("t"), new Identifier("sy2"), 1,
				VarcharType.instance(2), false, false);

		expression = new And(new NotEqualVarcharComparison(sx2, sy2),
				new EqualVarcharComparison("aa", sx2),
				new EqualVarcharComparison("aa", sy2));
		assertFalse(checker.isSatisfiable(expression));

	}

	@Test
	public void testSatisfiability46() throws SatisfiabilityException {

		// 46.) 'a' = 'a'
		Column sx2 = new Column(new Identifier("t"), new Identifier("sx2"), 1,
				VarcharType.instance(2), false, false);
		Column sy2 = new Column(new Identifier("t"), new Identifier("sy2"), 1,
				VarcharType.instance(2), false, false);

		expression = new And(new EqualVarcharComparison(sx2, sy2),
				new EqualVarcharComparison("a", sx2),
				new EqualVarcharComparison(sy2, "a"));
		assertTrue(checker.isSatisfiable(expression));

	}
}