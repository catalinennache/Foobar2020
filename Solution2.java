/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package foobar;

/**
 *
 * @author Enache
 */
public class Solution2 {
    /*  Explanation
        Let number of pegs be 28, the final ratio must be 2
        The first cog has the radius A
        The second cog has the radius B
        The last cog has the radius Z
    
        The first ratio, between the first two cogs, is A/B
        The second ratio, between the second cog and the third cog, is B/C
        ...
        The last ratio, between the penultimate cog and the last one, is Y/Z
        The total ratio of the system is (A/B)*(B/C)*(C/D)*(D/E)*...*(X/Y)*(Y/Z)
(*1)    Trough simplifying we get that the A/Z ratio must be 2.0
        S1 = P0-0
        S2 = P1-P0
        ...
        S28 = P27-P26
        ----------
        A+B = S1
        B+C = S2
        ...
        X+Y = S27
        Y+Z = S28
        ----------
        Y = S28 - Z
        X = S27 - S28 + Z
        A = S1-S2+S3-S4+...-S28+Z
        2*Z = S1-S2+S3-S4+...-S28+Z
 (*2)   Z = S1-S2+S3-S4+...-S28
    */

    public static int[] solution(int[] pegs) {
        int[] positions = pegs;
        int[] distances = new int[positions.length - 1];
        double last_cog = 0;
        double first_cog = 0;
        
        for (int i = 0; i < positions.length - 1; i++) {
            distances[i] = positions[i + 1] - positions[i];
        } //Computed the distances between pegs (S1,S2,S3 ... )

        //checking for the case where there's only 2 pegs
        if (distances.length == 1) {
            //checking if there's enough space for a valid solution
            if (distances[0] > 3) {
                first_cog = 2 * (distances[0] * 1.0) / 3;
                return toFraction(first_cog);
            } else {
                return noSolution();
            }
        }
        
        //Computing the last cog radius based on the point presented at (*2)
        for (int i = 0; i < distances.length; i++) {
            last_cog += ((i + 1) % 2 == 1 ? 1 : -1) * distances[i];
        }
        //For an even number of pegs (odd number of distances) I must divide by 3 to find out the real radius
        last_cog = distances.length % 2 == 0 ? last_cog : 1.0 / 3 * last_cog;
        
        //Validating the solution based on the known constraints
        if (last_cog < 1 || last_cog >= distances[distances.length - 1] - 1 || 2 * last_cog > distances[0] - 1) {
            return noSolution();
        }

        first_cog = 2 * last_cog;
        
        //Checking every cog to have its radius bigger than one
        double crad = first_cog;
        for (int i = 0; i < distances.length; i++) {
            crad = distances[i] - crad;
            if (crad < 1) {
                return noSolution();
            }

        }
        return toFraction(first_cog);
    }

    public static int[] toFraction(double num) {
        int dn = 1;
        //3.33333... case
        if (num % 1 != 0 && (num * 3) % 1 == 0) {
            num = num * 3;
            dn *= 3;
        }
        //2.5 / 1 => 25/10
        while (num % 1 != 0) {
            num *= 10;
            dn *= 10;
        }
        
        //finding the greatest common divisor
        int nm = (int) num;
        int g = gcd(nm, dn);
        if (g == 0) {
            g = 1;
        }
        //simplifying the fraction with gcd
        return new int[]{nm / g, dn / g};

    }

    public static int gcd(int a, int b) {
        int gcd = 0;
        for (int i = 1; i <= a && i <= b; i++) {
            if (a % i == 0 && b % i == 0) {
                gcd = i;
            }
        }

        return gcd;
    }

    private static int[] noSolution() {
        return new int[]{-1, -1};
    }

}

/*
 You got the guards to teach you a card game today, it's called Fizzbin. It's kind of pointless, but they seem to like it and it helps you pass the time while you work your way up to Commander Lambda's inner circle.
 New challenge "Gearing Up for Destruction" added to your home folder.
 Time to solve: 72 hours.
 foobar:~/ catalinenache03$ ls
 gearing-up-for-destruction
 journal.txt
 start_here.txt
 foobar:~/ catalinenache03$ cd gearing-up-for-destruction
 foobar:~/gearing-up-for-destruction catalinenache03$ ls
 Solution.java
 constraints.txt
 readme.txt
 solution.py
 foobar:~/gearing-up-for-destruction catalinenache03$ ls readme.txt
 readme.txt
 foobar:~/gearing-up-for-destruction catalinenache03$ cat readme.txt
 Gearing Up for Destruction
 ==========================

 As Commander Lambda's personal assistant, you've been assigned the task of configuring the LAMBCHOP doomsday device's axial orientation gears. It should be pretty simple - just add gears to create the appropriate rotation ratio. But the problem is, due to the layout of the LAMBCHOP and the complicated system of beams and pipes supporting it, the pegs that will support the gears are fixed in place.

 The LAMBCHOP's engineers have given you lists identifying the placement of groups of pegs along various support beams. You need to place a gear on each peg (otherwise the gears will collide with unoccupied pegs). The engineers have plenty of gears in all different sizes stocked up, so you can choose gears of any size, from a radius of 1 on up. Your goal is to build a system where the last gear rotates at twice the rate (in revolutions per minute, or rpm) of the first gear, no matter the direction. Each gear (except the last) touches and turns the gear on the next peg to the right.

 Given a list of distinct positive integers named pegs representing the location of each peg along the support beam, write a function solution(pegs) which, if there is a solution, returns a list of two positive integers a and b representing the numerator and denominator of the first gear's radius in its simplest form in order to achieve the goal above, such that radius = a/b. The ratio a/b should be greater than or equal to 1. Not all support configurations will necessarily be capable of creating the proper rotation ratio, so if the task is impossible, the function solution(pegs) should return the list [-1, -1].

 For example, if the pegs are placed at [4, 30, 50], then the first gear could have a radius of 12, the second gear could have a radius of 14, and the last one a radius of 6. Thus, the last gear would rotate twice as fast as the first one. In this case, pegs would be [4, 30, 50] and solution(pegs) should return [12, 1].

 The list pegs will be given sorted in ascending order and will contain at least 2 and no more than 20 distinct positive integers, all between 1 and 10000 inclusive.

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
 Solution.solution({4, 17, 50})
 Output:
 -1,-1

 Input:
 Solution.solution({4, 30, 50})
 Output:
 12,1

 -- Python cases --
 Input:
 solution.solution([4, 30, 50])
 Output:
 12,1

 Input:
 solution.solution([4, 17, 50])
 Output:
 -1,-1

 */
