import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;

public class Solution8 {

    public static TreeMap<Integer,BigInteger> factorialCache = new TreeMap<>();

    public static String solution(int w, int h, int s){

        /*
         After some trial and error with brute force and some further research
         I've tried to tackle this problem using Polya Enumeration Theorem
         The final formula that gives us the right answer is the following one

         N=1/w!h!* Sum having i in P(W) and j in P(H) of ( ( ( w!/(1^(i_1)i_1! * 2^(i_2)*i_2!...w^(i_w)*i_w!) * ( h!/ (1^(j_1)*j_1! * 2^(j_2)*j_2!...h^(j_h)*j_h! )) ) * s^ Sum having a in i and b in j of (gcd(a,b)))

         where w is the width, h is the height and s are all the possible states

         The formula can be easely read in the link below.

         In order to calculate the answer, we need to:
         *get partitions of w and h
         *calculate the coefficients
         *apply formula

         Inspired from
          https://franklinvp.github.io/2020-06-05-PolyaFooBar/
         *accessed at 7 Oct 2020 23:33 UTC
         */

        Collection<TreeMap<Integer,Integer>> partitions_w = partitionNumber(w);
        Collection<TreeMap<Integer,Integer>> partitions_h = partitionNumber(h);

        HashMap<TreeMap<Integer,Integer>,BigInteger> partitionCoefficientMap_w = new HashMap<>();
        HashMap<TreeMap<Integer,Integer>,BigInteger> partitionCoefficientMap_h = new HashMap<>();

        //mapping the partitions with their coefficients
        partitions_w.forEach(partition->{
            partitionCoefficientMap_w.put(partition,coefficient(partition,w));
        });

        partitions_h.forEach(partition->{
            partitionCoefficientMap_h.put(partition,coefficient(partition,h));
        });

        AtomicReference<BigInteger> answer = new AtomicReference<>(BigInteger.ZERO);

        //applying the formula (multiplying the coefficients, calculating the exponent, and raising the result of multiplication to the computed exponent
        partitions_w.forEach(partition_w->{
            partitions_h.forEach(partition_h->{

                BigInteger m = partitionCoefficientMap_w.get(partition_w).multiply(partitionCoefficientMap_h.get(partition_h));

                List<Integer> elems_p_w = getElements(partition_w);
                List<Integer> elems_p_h = getElements(partition_h);

                final BigInteger[] gcdSum = {BigInteger.ZERO};
                elems_p_h.forEach(element_h->{
                    elems_p_w.forEach(element_w->{
                        gcdSum[0] = gcdSum[0].add(BigInteger.valueOf(gcd(element_w,element_h)));
                    });
                });

                BigInteger exponent = gcdSum[0];
                answer.set(answer.get().add(m.multiply(BigInteger.valueOf(s).pow(exponent.intValue()))));

            });
        });


        //applying the last part of formula which is 1/w!h!
        //it's multiplied with the result of the before calculations
        return answer.get().divide(factorial(w,factorialCache).multiply(factorial(h,factorialCache))).toString();
    }

    //converts a partition that is represented as a treemap to a list
    //for each prime factor (treemap key) we append to the "elems" list the current prime factor TIMES its coefficient (its treemap corresponding value)
    public static List<Integer> getElements(TreeMap<Integer,Integer> partition){

        List<Integer> elems = new ArrayList<>();
        partition.forEach((k,v)->{
            for(int i = 0;i<v;i++)
                elems.add(k);
        });
        return elems;
    }

    /*
    *Computes
    n!/(1^{i_1}i_1!2^{i_2}i_2!...n^{i_n}i_n!)
    where "partition" is a partition of n that has   i_1 1s, i_2 2s, ..., and i_n ns.
    * */
    public static BigInteger coefficient(TreeMap<Integer,Integer> partition,int n){
        AtomicReference<BigInteger> numerator = new AtomicReference<>(factorial(n, factorialCache));
        AtomicReference<BigInteger> denominator = new AtomicReference<>(BigInteger.ONE);

        partition.forEach((prime,coeff)->{;
            BigInteger power_result = BigInteger.valueOf((long) Math.pow(prime,coeff));

            BigInteger old_denominator = denominator.get();
            denominator.set(power_result.multiply(factorial(coeff,factorialCache)));

            denominator.set(old_denominator.multiply(denominator.get()));
        });


        numerator.set(numerator.get().divide(denominator.get()));

        return numerator.get();
    }

    //computing the greatest common divisor
    public static int gcd(int a, int b) {
        int gcd = 0;
        for (int i = 1; i <= a && i <= b; i++) {
            if (a % i == 0 && b % i == 0) {
                gcd = i;
            }
        }
        gcd = (gcd == 0)?1:gcd;

        return gcd;
    }

    //based on the logic of n! = n*(n-1)!
    //we store the result of each factorial function call
    //to improve performance on the next calls
    public static BigInteger factorial(int n,TreeMap<Integer,BigInteger> factorialCache){
        if(n == 1){
            return BigInteger.ONE;
        }
        if(factorialCache.containsKey(n)){
            return factorialCache.get(n);
        }
        BigInteger result = BigInteger.valueOf(n).multiply(factorial(n-1,factorialCache));
        factorialCache.put(n,result);
        return result;
    }

    //the partition of a number will have the following form { prime_factor_1: multiplier, prime_factor_2: multiplier...}, it'll be represented by a TreeMap
    //the function will return a collection of such TreeMaps
    //
    //a partition will have a unique code,
    //e.g 3 has one of its partitions { 1 => 1, 2 => 1 ) that means 1*1+2*1 = 3
    //so the string code of this partition will be 1x1$2x1
    //
    //the convention is that we sort the pairs of prime factors and their multipliers
    //we join every prime factor with its corresponding multiplier using the letter "x"
    //and then we join all the processed pairs with the symbol "$"
    //
    //the encoding function ("encodePartition") is surjective, this means that multiple combinations of elements may give the same output (the same encoded string)
    //to find the partitions of a number the system will generate all the combinations of numbers that if summed up
    //would give us the number we asked to partition
    //
    //in order not to get duplicates, we take the every combination and encode it to the format shown above
    //then check if the string has already been stored, if yes then we discard that combination of elements since we've already discovered that partition before even though the elements are not in the same order
    //if no then we've got a new partition to store in a hashmap with the key being the unique code

    public static Collection<TreeMap<Integer,Integer>> partitionNumber(int n){
        //encodingToPartitionMap maps the partition_code to the partition (stored as a treemap with keys being the prime factors and values being the coefficients)
        HashMap<String,TreeMap<Integer,Integer>> encodingToPartitionMap =  new HashMap<>();

        //generate using backtracking all combinations of  numbers that summed up will result in n
        for(int i = 1; i<=n;i++){
            List<Integer> current_partition = new ArrayList<>();
            current_partition.add(i);
            computePartition(n-i,current_partition,encodingToPartitionMap);
        }
        return encodingToPartitionMap.values();
    }

    private static void computePartition(int n, List<Integer> _partition,HashMap<String,TreeMap<Integer,Integer>> partitions) {
        if(n==0) {
            //when the amount of the number to partition reaches 0
            //means we have a candidate for a partition
            String encoded_partition = encodePartition(_partition);
            //we test the candidate and if it's valid we accept it
            if(!partitions.containsKey(encoded_partition)) {
                TreeMap<Integer,Integer> primeToCoeffPartitionMap = decodePartition(encoded_partition);
                partitions.put(encoded_partition,primeToCoeffPartitionMap);
            }
        }

        for(int i = 1; i<=n;i++){
            List<Integer> current_partition = new ArrayList<>(_partition);
            current_partition.add(i);
            computePartition(n - i, current_partition, partitions);
        }
    }

    private static TreeMap<Integer, Integer> decodePartition(String encoded_partition) {
        TreeMap<Integer,Integer> primeToCoeffPartition = new TreeMap<>();

        String[] elements = encoded_partition.split(java.util.regex.Pattern.quote("$"));
        for(String element:elements){
            String[] raw_infos = element.split("x");
            int coefficient = Integer.parseInt(raw_infos[0]);
            int prime = Integer.parseInt(raw_infos[1]);
            primeToCoeffPartition.put(prime,coefficient);
        }

        return primeToCoeffPartition;
    }

    // 9 => [2, 2, 2, 2, 1]  => 1x1$2x4
    public static String encodePartition(List<Integer> target_partition){
        TreeMap<Integer,Integer> primeFactorsCoefficientMap = new TreeMap<>();

        //detecting the frequency of the prime factors
        target_partition.forEach(element->{
            int old_coefficient = primeFactorsCoefficientMap.getOrDefault(element,0);
            primeFactorsCoefficientMap.put(element,old_coefficient+1);
        });

        StringBuilder sb = new StringBuilder();
        String separator = "$";
        primeFactorsCoefficientMap.forEach((primeFactor,coefficient)->{
            sb.append(coefficient).append("x").append(primeFactor).append(separator);
        });

        String encoded_partition = sb.toString();
        encoded_partition = encoded_partition.substring(0,encoded_partition.length()-1);
        return encoded_partition;
    }




}





/*

foobar:~/disorderly-escape catalinenache03$ cat readme.txt
Disorderly Escape
=================

Oh no! You've managed to free the bunny prisoners and escape Commander Lambdas exploding space station, but her team of elite starfighters has flanked your ship. If you dont jump to hyperspace, and fast, youll be shot out of the sky!

Problem is, to avoid detection by galactic law enforcement, Commander Lambda planted her space station in the middle of a quasar quantum flux field. In order to make the jump to hyperspace, you need to know the configuration of celestial bodies in the quadrant you plan to jump through. In order to do *that*, you need to figure out how many configurations each quadrant could possibly have, so that you can pick the optimal quadrant through which youll make your jump.

There's something important to note about quasar quantum flux fields' configurations: when drawn on a star grid, configurations are considered equivalent by grouping rather than by order. That is, for a given set of configurations, if you exchange the position of any two columns or any two rows some number of times, youll find that all of those configurations are equivalent in that way - in grouping, rather than order.

Write a function solution(w, h, s) that takes 3 integers and returns the number of unique, non-equivalent configurations that can be found on a star grid w blocks wide and h blocks tall where each celestial body has s possible states. Equivalency is defined as above: any two star grids with each celestial body in the same state where the actual order of the rows and columns do not matter (and can thus be freely swapped around). Star grid standardization means that the width and height of the grid will always be between 1 and 12, inclusive. And while there are a variety of celestial bodies in each grid, the number of states of those bodies is between 2 and 20, inclusive. The solution can be over 20 digits long, so return it as a decimal string.  The intermediate values can also be large, so you will likely need to use at least 64-bit integers.

For example, consider w=2, h=2, s=2. We have a 2x2 grid where each celestial body is either in state 0 (for instance, silent) or state 1 (for instance, noisy).  We can examine which grids are equivalent by swapping rows and columns.

00
00

In the above configuration, all celestial bodies are "silent" - that is, they have a state of 0 - so any swap of row or column would keep it in the same state.

00 00 01 10
01 10 00 00

1 celestial body is emitting noise - that is, has a state of 1 - so swapping rows and columns can put it in any of the 4 positions.  All four of the above configurations are equivalent.

00 11
11 00

2 celestial bodies are emitting noise side-by-side.  Swapping columns leaves them unchanged, and swapping rows simply moves them between the top and bottom.  In both, the *groupings* are the same: one row with two bodies in state 0, one row with two bodies in state 1, and two columns with one of each state.

01 10
01 10

2 noisy celestial bodies adjacent vertically. This is symmetric to the side-by-side case, but it is different because there's no way to transpose the grid.

01 10
10 01

2 noisy celestial bodies diagonally.  Both have 2 rows and 2 columns that have one of each state, so they are equivalent to each other.

01 10 11 11
11 11 01 10

3 noisy celestial bodies, similar to the case where only one of four is noisy.

11
11

4 noisy celestial bodies.

There are 7 distinct, non-equivalent grids in total, so solution(2, 2, 2) would return 7.

Languages
=========

To provide a Java solution, edit Solution.java
To provide a Python solution, edit solution.py

Test cases
==========
Your code should pass the following test cases.
Note that it may also be run against hidden test cases not shown here.

-- Java cases --
Input:
Solution.solution(2, 3, 4)
Output:
    430

Input:
Solution.solution(2, 2, 2)
Output:
    7

-- Python cases --
Input:
solution.solution(2, 3, 4)
Output:
    430

Input:
solution.solution(2, 2, 2)
Output:
    7

Use verify [file] to test your solution and see how it does. When you are finished editing your code, use submit [file] to submit your answer. If your solution passes the test cases, it will be removed from your home folder.



Java
====
Your code will be compiled using standard Java 8. All tests will be run by calling the solution() method inside the Solution class

Execution time is limited.

Wildcard imports and some specific classes are restricted (e.g. java.lang.ClassLoader). You will receive an error when you verify your solution if you have used a blacklisted class.

Third-party libraries, input/output operations, spawning threads or processes and changes to the execution environment are not allowed.

Your solution must be under 32000 characters in length including new lines and and other non-printing characters.

Python
======
Your code will run inside a Python 2.7.13 sandbox. All tests will be run by calling the solution() function.

Standard libraries are supported except for bz2, crypt, fcntl, mmap, pwd, pyexpat, select, signal, termios, thread, time, unicodedata, zipimport, zlib.

Input/output operations are not allowed.

Your solution must be under 32000 characters in length including new lines and and other non-printing characters.

 */
