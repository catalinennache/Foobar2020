import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Solution6 {



public static int[][] solution (int num_buns, int num_required) {
        //in order for this to work we need to give a key to a unique combination of num_buns - num_required +1 bunnies
        //this +1 reassures that picking any tuple of num_required bunnies the key will be there
        //this means that by picking any tuple of num_required bunnies, the union of their keys will give us the original, not-duplicated set of keys
        int comb_size = num_buns - num_required +1;

        //we generate all unique combinations of bunnies which will then receive their keys
        List<int[]> combos = generateCombinations(num_buns,comb_size);
        //we map the bunny with its keys array
        HashMap<Integer,List<Integer>> bunny_keys_map = new HashMap<>();

        for(int key = 0;key<combos.size();key++){
            int[] the_bunnies_to_receive_the_current_key = combos.get(key);
            //foreach bunny we append the current key to its keys list
            for(int i = 0;i<the_bunnies_to_receive_the_current_key.length;i++){
               // getting the list of keys that the current bunny has, or getting an empty list if the bunny has no keys assigned yet
               List<Integer> existing_keys =  bunny_keys_map.getOrDefault(the_bunnies_to_receive_the_current_key[i],new ArrayList<>());
                //adding the key to its keychain
               existing_keys.add(key);
               bunny_keys_map.put(the_bunnies_to_receive_the_current_key[i],existing_keys);
            }
        }

        //converting the map to matrix, every map's key will become a row in the matrix
        //hence the number of rows will be equal with the number of keys in the bunny_keys_map


        //combos.get(0) represents all the bunnies to receive the first key
        //combos.get(0)[0] represents the first bunny to receive the first key
        //since all the bunnies have the same amount of keys we can use combos.get(0)[0].length as a parameter in matrix creation
        List<Integer> keys_of_a_bunny = bunny_keys_map.get(combos.get(0)[0]);
        int[][] results = new int[bunny_keys_map.keySet().size()][keys_of_a_bunny.size()];

        //foreach bunny
        bunny_keys_map.forEach((k,v)->{
              //for each of its keys in the V list
              for(int j = 0;j<v.size();j++){
                  results[k][j]=v.get(j);
                  //we place the key to the row responsible for the given bunny at the correct column
              }
        });
        return results;
    }
    //inspired from https://www.baeldung.com/java-combinations-algorithm
    //accessed at 20 September 2020 03:46 PM UTC TIME

    public static List<int[]> generateCombinations(int n, int r) {
        List<int[]> combinations = new ArrayList<>();
        helper(combinations, new int[r], 0, n-1, 0);
        return combinations;
    }

    private static void helper(List<int[]> combinations, int data[], int start, int end, int index) {
        if (index == data.length) {
            int[] combination = data.clone();
            combinations.add(combination);
        } else if (start <= end) {
            data[index] = start;
            helper(combinations, data, start + 1, end, index + 1);
            helper(combinations, data, start + 1, end, index);
        }
    }

}
/*Free the Bunny Prisoners
========================

You need to free the bunny prisoners before Commander Lambda's space station explodes! Unfortunately, the commander was very careful with her highest-value prisoners - they're all held in separate, maximum-security cells.
The cells are opened by putting keys into each console, then pressing the open button on each console simultaneously. When the open button is pressed, each key opens its corresponding lock on the cell.
So, the union of the keys in all of the consoles must be all of the keys. The scheme may require multiple copies of one key given to different minions.

The consoles are far enough apart that a separate minion is needed for each one. Fortunately, you have already freed some bunnies to aid you - and even better, you were able to steal the keys while you were working
as Commander Lambda's assistant. The problem is, you don't know which keys to use at which consoles. The consoles are programmed to know which keys each minion had, to prevent someone from just stealing all of the keys
and using them blindly. There are signs by the consoles saying how many minions had some keys for the set of consoles. You suspect that Commander Lambda has a systematic way to decide which keys to give to each minion
such that they could use the consoles.

You need to figure out the scheme that Commander Lambda used to distribute the keys. You know how many minions had keys, and how many consoles are by each cell.  You know that Command Lambda wouldn't issue more keys than
necessary (beyond what the key distribution scheme requires), and that you need as many bunnies with keys as there are consoles to open the cell.

Given the number of bunnies available and the number of locks required to open a cell, write a function solution(num_buns, num_required) which returns a specification of how to distribute the keys such that any num_required
bunnies can open the locks, but no group of (num_required - 1) bunnies can.

Each lock is numbered starting from 0. The keys are numbered the same as the lock they open (so for a duplicate key, the number will repeat, since it opens the same lock). For a given bunny, the keys they get is represented
as a sorted list of the numbers for the keys. To cover all of the bunnies, the final answer is represented by a sorted list of each individual bunny's list of keys.  Find the lexicographically least such key distribution -
that is, the first bunny should have keys sequentially starting from 0.

num_buns will always be between 1 and 9, and num_required will always be between 0 and 9 (both inclusive).  For example, if you had 3 bunnies and required only 1 of them to open the cell, you would give each bunny the same
key such that any of the 3 of them would be able to open it, like so:
[
  [0],
  [0],
  [0],
]
If you had 2 bunnies and required both of them to open the cell, they would receive different keys (otherwise they wouldn't both actually be required), and your answer would be as follows:
[
  [0],
  [1],
]
Finally, if you had 3 bunnies and required 2 of them to open the cell, then any 2 of the 3 bunnies should have all of the keys necessary to open the cell, but no single bunny would be able to do it.  Thus, the answer would be:
[
  [0, 1],
  [0, 2],
  [1, 2],
]

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
solution.solution(2, 1)
Output:
    [[0], [0]]

Input:
solution.solution(4, 4)
Output:
    [[0], [1], [2], [3]]

Input:
solution.solution(5, 3)
Output:
    [[0, 1, 2, 3, 4, 5], [0, 1, 2, 6, 7, 8], [0, 3, 4, 6, 7, 9], [1, 3, 5, 6, 8, 9], [2, 4, 5, 7, 8, 9]]

-- Java cases --
Input:
Solution.solution(2, 1)
Output:
    [[0], [0]]

Input:
Solution.solution(5, 3)
Output:
    [[0, 1, 2, 3, 4, 5], [0, 1, 2, 6, 7, 8], [0, 3, 4, 6, 7, 9], [1, 3, 5, 6, 8, 9], [2, 4, 5, 7, 8, 9]]

Input:
Solution.solution(4, 4)
Output:
    [[0], [1], [2], [3]]*/