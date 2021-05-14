import java.util.Arrays;

public class Solution3 {
    public int solution(int[] l){
        //if there's only one element it tests it and returns the result
        if(l.length == 1)
            return l[0]%3 == 0?l[0]:0;

        //sorting the "l" array descending by using the optimized Arrays.sort method which sorts ascending
        //after sorting that the array is reversed to be in descending order
        Arrays.sort(l);
        int[] descendingL = new int[l.length];
        for(int i = l.length-1;i>=0;i--){
            descendingL[l.length-1-i]=l[i];
        }

        //Testing for the case when the largest possible number is also divisible by 3
        String attemptedNumberString = "";
        int digitSum = 0;
        for(int x:descendingL){
            attemptedNumberString += x;
            digitSum += x;
        }
        int attemptedNumberInt = Integer.parseInt(attemptedNumberString);
        if(digitSum%3 == 0){
            return attemptedNumberInt;
        }
        //Since it isn't divisible by 3 by default
        //it will search for the digit which if removed it will make the new number divisible by 3
        //and it will impact the amount of number the least.
        //So it will traverse the number's digits in ascending order.
        int toRemove = -1;
        for(int x:l){
            if(x%3 == digitSum%3) {
                toRemove = x;
                break;
            }
        }

        if(toRemove > 0){
            String attemptedNumberTrimmed = new StringBuilder(attemptedNumberString).toString();
            for(int i = attemptedNumberString.length()-1; i>=0;i--){
                //search for the found number
                //traversing the initial number in ascending digit places
                //trying to find the te searched number on the closest digit position to units, if not on units
                if(attemptedNumberString.charAt(i)-48 == toRemove){
                    if(i+1 < attemptedNumberString.length())
                        attemptedNumberTrimmed = attemptedNumberTrimmed.substring(0,i) + attemptedNumberTrimmed.substring(i+1);
                    else
                        attemptedNumberTrimmed = attemptedNumberTrimmed.substring(0,i);
                    break;
                }
            }

            attemptedNumberInt = Integer.parseInt(attemptedNumberTrimmed);

            return attemptedNumberInt;
        }else{
            //if it can't form a number by removing one digit, it removes the units digit and it recalls itself
            //to see if after this move it can find the solution

            //converting from String to an array of int
            String[] newL = attemptedNumberString.substring(0,attemptedNumberString.length()-1).split("");
            int[] newL_integer = Arrays.stream(newL).mapToInt(Integer::parseInt).toArray();
            return solution(newL_integer);
        }


    }
}

/*
Please Pass the Coded Messages
==============================

You need to pass a message to the bunny prisoners, but to avoid detection, the code you agreed to use is... obscure, to say the least. The bunnies are given food on standard-issue prison plates that are stamped with the numbers 0-9 for easier sorting, and you need to combine sets of plates to create the numbers in the code. The signal that a number is part of the code is that it is divisible by 3. You can do smaller numbers like 15 and 45 easily, but bigger numbers like 144 and 414 are a little trickier. Write a program to help yourself quickly create large numbers for use in the code, given a limited number of plates to work with.

You have L, a list containing some digits (0 to 9). Write a function solution(L) which finds the largest number that can be made from some or all of these digits and is divisible by 3. If it is not possible to make such a number, return 0 as the solution. L will contain anywhere from 1 to 9 digits.  The same digit may appear multiple times in the list, but each element in the list may only be used once.

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
Solution.solution({3, 1, 4, 1})
Output:
    4311

Input:
Solution.solution({3, 1, 4, 1, 5, 9})
Output:
    94311

5 Tests (3 hidden)
 */
