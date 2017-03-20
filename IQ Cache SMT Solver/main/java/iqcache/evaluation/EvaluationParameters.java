package iqcache.evaluation;

public class EvaluationParameters {

	// solver
	public static final String MATHSAT = "MathSAT5";
	public static final String Z3 = "Z3";
	public static final String YICES = "Yices";

	// test cases
	public static final String TEST100 = "TEST100";
	public static final String TEST200 = "TEST200";
	public static final String TEST300 = "TEST300";
	public static final String TEST400 = "TEST400";
	public static final String TEST500 = "TEST500";
	public static final String TESTBV = "TESTBV";

	// benchmarks
	public static final String MONARCH = "Monarch3";
	public static final String YAHOO = "Yahoo";
	public static final String TWITTER = "Twitter";
	public static final String RQBENCH = "RQBench";

	// operations
	public static final String EQUAL = "Equal";
	public static final String NOT_EQUAL = "NotEqual";
	public static final String LE = "LE"; // less equal
	public static final String LT = "LT"; // less than

	// data types
	public static final String INT = "Int";
	public static final String REAL = "Real";
	public static final String BV = "Bitvec";

	// file extensions
	public static final String CSV = ".csv";
	public static final String TXT = ".txt";
	public static final String RAW = ".raw";

	// information
	public static final String TEST_CASE_NUMBER = "Testnummer: ";
	public static final String BENCHMARK_DEF = "Benchmark: ";
	public static final String TOTAL_TIME = "Gesamtzeit: ";
	public static final String NUMBER_OF_CHECKED_EXPRESSIONS = "Anzahl der validierten Expressions: ";
	public static final String SOLVERNAME_DEF = "Solver: ";
	public static final String TIME = "Zeit: ";
	public static final String LEVEL_DEF = "Stufen : ";
	public static final String REPEATS_DEF = "Wdh.: ";
	public static final String TYPE_OF_TEST = "Testtyp: ";
	public static final String VALIDATED_FORMULA = "Formel: ";
}