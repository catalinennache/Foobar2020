package foobar;

/**
 *
 * @author Enache
 */

public class Solution1 {
    public static int solution(String x) {
        x = x.trim();
        String[] mms = x.split("");
        String start_symbol = mms[0];
        String pattern = start_symbol;
        
        if(x.replaceAll(start_symbol, "").length() == 0)
            return x.length();
        
        
        for (int i = 1; i < mms.length; i++) {
            if(i + pattern.length() + 1 < mms.length 
                    && x.substring(i+1).replaceAll(pattern+mms[i], "").length() == 0){
                //does pattern's length make it (the pattern) a valid candidate?
                //is the pettern valid?
                pattern += mms[i];
                break;
            }
            pattern += mms[i];
        }
        
        
        String validation_string = x.replaceAll(pattern, "");
        //will test if pattern is valid
        if (validation_string.length() == 0) {
            int eq_parts = 0;
            int pattern_length = pattern.length();
            while (x.indexOf(pattern) >= 0) {
                eq_parts++;
                x = x.substring(pattern_length);
                //search in string untill you exhaust it by chopping the found patterns
            }
            return eq_parts;
        } else {
            return 1;
        }
    }
}

/*
 The cake is not a lie!
 ======================

 Commander Lambda has had an incredibly successful week: she completed the first test run of her LAMBCHOP doomsday device, she captured six key members of the Bunny Rebellion, and she beat her personal high score in Tetris. To celebrate, she's ordered cake for everyone - even the lowliest of minions! But competition among minions is fierce, and if you don't cut exactly equal slices of cake for everyone, you'll get in big trouble. 

 The cake is round, and decorated with M&Ms in a circle around the edge. But while the rest of the cake is uniform, the M&Ms are not: there are multiple colors, and every minion must get exactly the same sequence of M&Ms. Commander Lambda hates waste and will not tolerate any leftovers, so you also want to make sure you can serve the entire cake.

 To help you best cut the cake, you have turned the sequence of colors of the M&Ms on the cake into a string: each possible letter (between a and z) corresponds to a unique color, and the sequence of M&Ms is given clockwise (the decorations form a circle around the outer edge of the cake).

 Write a function called solution(s) that, given a non-empty string less than 200 characters in length describing the sequence of M&Ms, returns the maximum number of equal parts that can be cut from the cake without leaving any leftovers.

 Languages
 =========

 To provide a Python solution, edit solution.py
 To provide a Java solution, edit Solution.java

 Test cases
 ==========
 Your code should pass the following test cases.
 Note that it may also be run against hidden test cases not shown here.

 -- Python cases --
 Input:
 solution.solution("abcabcabcabc")
 Output:
 4

 Input:
 solution.solution("abccbaabccba")
 Output:
 2

 -- Java cases --
 Input:
 Solution.solution("abcabcabcabc")
 Output:
 4

 Input:
 Solution.solution("abccbaabccba")
 Output:
 2
 */
