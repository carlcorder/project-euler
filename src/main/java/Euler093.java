import java.util.*;

/**

 By using each of the digits from the set, {1, 2, 3, 4}, exactly once, and making use of the four arithmetic operations
 (+, −, *, /) and brackets/parentheses, it is possible to form different positive integer targets.

 For example,

 8 = (4 * (1 + 3)) / 2
 14 = 4 * (3 + 1 / 2)
 19 = 4 * (2 + 3) − 1
 36 = 3 * 4 * (2 + 1)

 Note that concatenations of the digits, like 12 + 34, are not allowed.

 Using the set, {1, 2, 3, 4}, it is possible to obtain thirty-one different target numbers of which 36 is the maximum,
 and each of the numbers 1 to 28 can be obtained before encountering the first non-expressible number.

 Find the set of four distinct digits, a < b < c < d, for which the longest set of consecutive positive integers, 1 to n,
 can be obtained, giving your answer as a string: abcd.

 */

/*

 NOTES:

 There are 10C4 = 210 possible combinations of strings 'abcd' that we can build with the digits [0-9]
 For each string we can permute it 4! = 24 different ways
 Using the Catalan numbers, we know there are 5 distinct ways of grouping {a,b,c,d}:
 1. ((ab)c)d   2. (a(bc))d    3. (ab)(cd)    4. a((bc)d)    5. a(b(cd))
 and they must each be evaluated because PEMDAS does not remove all ambiguity from the order of operations
 Finally, there are 4^3 = 64 ways to choose the three binary operators that go between the four digits

 Total there is (10C4)(4!)(C_4)(4^3) = (210)(24)(5)(64) = 1,612,800 possibilities that we need to check. However a
 significant percentage will be redundant calculations.

 */

public class Euler093 {

    // http://stackoverflow.com/questions/2960826/most-elegant-way-to-apply-an-operator-found-as-a-string-in-java
    // integer division can give us false results
    public enum Operation {

        ADD("+")       { public double perform(double x, double y) {return x + y;}},
        SUBTRACT("-")  { public double perform(double x, double y) {return x - y;}},
        MULTIPLY("*")  { public double perform(double x, double y) {return x * y;}},
        DIVIDE("/")    { public double perform(double x, double y) {return x / y;}};
        public abstract double perform(double x, double y);

        private String symbol;

        private Operation(String symbol) {
            this.symbol = symbol;
        }

        static public Operation getOperator(String symbol) {
            for(Operation op : values()) {
                if(op.symbol.equals(symbol)) {
                    return op;
                }
            }
            throw new IllegalArgumentException("Operation [ " + symbol + " ] is not valid");
        }
    }

    // 4^3 = 64 combinations
    private static final Map<Integer, List<String>> OPS = new HashMap<Integer, List<String>>() {
        {
            put(1  , Arrays.asList("+","+","+"));   put(2  , Arrays.asList("+","+","-"));   put(3  , Arrays.asList("+","+","*"));   put(4  , Arrays.asList("+","+","/"));
            put(5  , Arrays.asList("+","-","+"));   put(6  , Arrays.asList("+","-","-"));   put(7  , Arrays.asList("+","-","*"));   put(8  , Arrays.asList("+","-","/"));
            put(9  , Arrays.asList("+","*","+"));   put(10 , Arrays.asList("+","*","-"));   put(11 , Arrays.asList("+","*","*"));   put(12 , Arrays.asList("+","*","/"));
            put(13 , Arrays.asList("+","/","+"));   put(14 , Arrays.asList("+","/","-"));   put(15 , Arrays.asList("+","/","*"));   put(16 , Arrays.asList("+","/","/"));
            put(17 , Arrays.asList("-","+","+"));   put(18 , Arrays.asList("-","+","-"));   put(19 , Arrays.asList("-","+","*"));   put(20 , Arrays.asList("-","+","/"));
            put(21 , Arrays.asList("-","-","+"));   put(22 , Arrays.asList("-","-","-"));   put(23 , Arrays.asList("-","-","*"));   put(24 , Arrays.asList("-","-","/"));
            put(25 , Arrays.asList("-","*","+"));   put(26 , Arrays.asList("-","*","-"));   put(27 , Arrays.asList("-","*","*"));   put(28 , Arrays.asList("-","*","/"));
            put(29 , Arrays.asList("-","/","+"));   put(30 , Arrays.asList("-","/","-"));   put(31 , Arrays.asList("-","/","*"));   put(32 , Arrays.asList("-","/","/"));
            put(33 , Arrays.asList("*","+","+"));   put(34 , Arrays.asList("*","+","-"));   put(35 , Arrays.asList("*","+","*"));   put(36 , Arrays.asList("*","+","/"));
            put(37 , Arrays.asList("*","-","+"));   put(38 , Arrays.asList("*","-","-"));   put(39 , Arrays.asList("*","-","*"));   put(40 , Arrays.asList("*","-","/"));
            put(41 , Arrays.asList("*","*","+"));   put(42 , Arrays.asList("*","*","-"));   put(43 , Arrays.asList("*","*","*"));   put(44 , Arrays.asList("*","*","/"));
            put(45 , Arrays.asList("*","/","+"));   put(46 , Arrays.asList("*","/","-"));   put(47 , Arrays.asList("*","/","*"));   put(48 , Arrays.asList("*","/","/"));
            put(49 , Arrays.asList("/","+","+"));   put(50 , Arrays.asList("/","+","-"));   put(51 , Arrays.asList("/","+","*"));   put(52 , Arrays.asList("/","+","/"));
            put(53 , Arrays.asList("/","-","+"));   put(54 , Arrays.asList("/","-","-"));   put(55 , Arrays.asList("/","-","*"));   put(56 , Arrays.asList("/","-","/"));
            put(57 , Arrays.asList("/","*","+"));   put(58 , Arrays.asList("/","*","-"));   put(59 , Arrays.asList("/","*","*"));   put(60 , Arrays.asList("/","*","/"));
            put(61 , Arrays.asList("/","/","+"));   put(62 , Arrays.asList("/","/","-"));   put(63 , Arrays.asList("/","/","*"));   put(64 , Arrays.asList("/","/","/"));
        }
    };

    /*
        4! = 24 permutations of {a,b,c,d}
        abcd	badc	cabd	dacb
        abdc    bacd    cadb    dabc
        acbd    bcda    cbad    dbca
        acdb    bcad    cbda    dbac
        adbc    bdca    cdab    dcba
        adcb    bdac    cdba    dcab
    */
    private static final Map<Integer, List<Integer>> PERMS = new HashMap<Integer, List<Integer>>() {
        {
            put(1  , Arrays.asList(0,1,2,3));   put(2  , Arrays.asList(0,1,3,2));   put(3  , Arrays.asList(0,2,1,3));   put(4  , Arrays.asList(0,2,3,1));
            put(5  , Arrays.asList(0,3,1,2));   put(6  , Arrays.asList(0,3,2,1));   put(7  , Arrays.asList(1,0,3,2));   put(8  , Arrays.asList(1,0,2,3));
            put(9  , Arrays.asList(1,2,3,0));   put(10 , Arrays.asList(1,2,0,3));   put(11 , Arrays.asList(1,3,2,0));   put(12 , Arrays.asList(1,3,0,2));
            put(13 , Arrays.asList(2,0,1,3));   put(14 , Arrays.asList(2,0,3,1));   put(15 , Arrays.asList(2,1,0,3));   put(16 , Arrays.asList(2,1,3,0));
            put(17 , Arrays.asList(2,3,0,1));   put(18 , Arrays.asList(2,3,1,0));   put(19 , Arrays.asList(3,0,2,1));   put(20 , Arrays.asList(3,0,1,2));
            put(21 , Arrays.asList(3,1,2,0));   put(22 , Arrays.asList(3,1,0,2));   put(23 , Arrays.asList(3,2,1,0));   put(24 , Arrays.asList(3,2,0,1));
        }
    };

    public static void filterSet(Set<Double> set) {
        Iterator<Double> iter = set.iterator();
        while(iter.hasNext()) {
            Double value = iter.next();
            // keep only positive integers (NOTE: -0.0 is not less than 0.0)
            if(value.isNaN() || value.isInfinite() || value.compareTo(1.0) < 0 ||  value.compareTo(Math.floor(value)) > 0) {
                iter.remove();
            }
        }
    }

    // get the longest consecutive streak of positive integers
    public static int measure(Set<Double> set) {
        int n = 0;
        for(int i = 1; i < set.size(); i++) {
            if(set.contains(1.0 * i)) {
                n++;
            } else {
                break;
            }
        }
        return n;
    }

    public static void getResults(int a, int b, int c, int d, int n) {
        String results = String.format("[ %s, %s, %s, %s ] -> %s", a, b, c, d, n);
        System.out.println(results);
    }

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        // ranges for digits a,b,c & d
        int aMin = 0, bMin = 1, cMin = 2, dMin = 3, dMax = 9;
        int maxConsec = 0;
        int[] maxDigits = new int[4];
        double[] digits = new double[4];

        // loop over 10C4 = 210 combinations
        for(int d = dMin; d <= dMax; d++) {
            for(int c = cMin; c < d; c++) {
                for(int b = bMin; b < c; b++) {
                    for(int a = aMin; a < b; a++) {
                        // start with a clean set
                        TreeSet<Double> resultSet = new TreeSet<Double>();
                        // loop over all permutations of a,b,c,d
                        for(Integer permKey : PERMS.keySet()) {
                            digits[PERMS.get(permKey).get(0)] = a; digits[PERMS.get(permKey).get(1)] = b;
                            digits[PERMS.get(permKey).get(2)] = c; digits[PERMS.get(permKey).get(3)] = d;
                            // loop over all operator combinations
                            for(Integer opKey : OPS.keySet()) {

                                // 1. ((ab)c)d
                                resultSet.add(Operation.getOperator(OPS.get(opKey).get(2)).perform(
                                        Operation.getOperator(OPS.get(opKey).get(1)).perform(
                                                Operation.getOperator(OPS.get(opKey).get(0)).perform(digits[0], digits[1]), digits[2]), digits[3]));

                                // 2. (a(bc))d
                                resultSet.add(Operation.getOperator(OPS.get(opKey).get(2)).perform(
                                        Operation.getOperator(OPS.get(opKey).get(0)).perform(
                                                digits[0], Operation.getOperator(OPS.get(opKey).get(1)).perform(digits[1], digits[2])), digits[3]));

                                // 3. (ab)(cd)
                                resultSet.add(Operation.getOperator(OPS.get(opKey).get(1)).perform(
                                        Operation.getOperator(OPS.get(opKey).get(0)).perform(
                                                digits[0], digits[1]), Operation.getOperator(OPS.get(opKey).get(2)).perform(digits[2], digits[3])));

                                // 4. a((bc)d)
                                resultSet.add(Operation.getOperator(OPS.get(opKey).get(0)).perform(
                                        digits[0], Operation.getOperator(OPS.get(opKey).get(2)).perform(
                                                Operation.getOperator(OPS.get(opKey).get(1)).perform(digits[1], digits[2]), digits[3])));

                                // 5. a(b(cd))
                                resultSet.add(Operation.getOperator(OPS.get(opKey).get(0)).perform(
                                        digits[0], Operation.getOperator(OPS.get(opKey).get(1)).perform(
                                                digits[1], Operation.getOperator(OPS.get(opKey).get(2)).perform(digits[2], digits[3]))));

                            }
                        }

                        filterSet(resultSet);

                        if(measure(resultSet) > maxConsec) {
                            maxConsec = measure(resultSet);
                            maxDigits[0] = a; maxDigits[1] = b; maxDigits[2] = c; maxDigits[3] = d;
                        }

                    }
                }
            }
        }

        getResults(maxDigits[0], maxDigits[1], maxDigits[2], maxDigits[3], maxConsec);// 1258

        System.out.println("Took: " + (System.currentTimeMillis() - startTime + "ms"));
    }

}