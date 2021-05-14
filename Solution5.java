import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Solution5 {
    public static int solution(int[] l){
        AtomicInteger total_combos = new AtomicInteger();
        //for every element in the array we search all the other greater elements which can be divided by the current one
        for (int i = 0; i < l.length; i++) {
            //we'll store in middle_terms the indexes of all the elements divisible with l[i] (current element)
            ArrayList<Integer> middle_terms = new ArrayList<>();
            for (int j = i + 1; j < l.length; j++) {
                if (l[j] % l[i] == 0) {
                    //we add the index of the matching element
                    middle_terms.add(j);
                }
            }

            //for every element found to be divided by the current element
            //we start another search in the numbers which are greater than it
            middle_terms.forEach(index -> {
                for (int k = index + 1; k < l.length; k++) {
                    //when we find an element that is divided by the middle term
                    //it means we have found a lucky triple because l[i] divides l[index] and l[index] divides l[k]
                    //where i<index<k
                    if (l[k] % l[index] == 0) {
                        //we increment the variable responsible for counting the lucky triples
                        total_combos.getAndIncrement();
                    }
                }
            });

        }
        return total_combos.get();
    }
}

/*Find the Access Codes
=====================

In order to destroy Commander Lambda's LAMBCHOP doomsday device, you'll need access to it. But the only door leading to the LAMBCHOP chamber is secured with a unique lock system whose number of passcodes changes daily. Commander Lambda gets a report every day that includes the locks' access codes, but only she knows how to figure out which of several lists contains the access codes. You need to find a way to determine which list contains the access codes once you're ready to go in.

Fortunately, now that you're Commander Lambda's personal assistant, she's confided to you that she made all the access codes "lucky triples" in order to help her better find them in the lists. A "lucky triple" is a tuple (x, y, z) where x divides y and y divides z, such as (1, 2, 4). With that information, you can figure out which list contains the number of access codes that matches the number of locks on the door when you're ready to go in (for example, if there's 5 passcodes, you'd need to find a list with 5 "lucky triple" access codes).

Write a function solution(l) that takes a list of positive integers l and counts the number of "lucky triples" of (li, lj, lk) where the list indices meet the requirement i < j < k.  The length of l is between 2 and 2000 inclusive.  The elements of l are between 1 and 999999 inclusive.  The answer fits within a signed 32-bit integer. Some of the lists are purposely generated without any access codes to throw off spies, so if no triples are found, return 0.

For example, [1, 2, 3, 4, 5, 6] has the triples: [1, 2, 4], [1, 2, 6], [1, 3, 6], making the answer 3 total.

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
Solution.solution([1, 1, 1])
Output:
    1

Input:
Solution.solution([1, 2, 3, 4, 5, 6])
Output:
    3
*/
