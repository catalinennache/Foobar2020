import java.util.ArrayList;
import java.util.List;

public class Solution7 {



    public static int solution(int[] entrances, int[] exits, int[][] path) {
        int answer = 0;

        // *** Long Explanation Ahead ***
        //
        // In the following examples:
        // "=" means tunnel
        // "== x ==" means a tunnel that supports x bunnies at a time step
        // | |;   / /; \ \  ;  |
        // |x|;  /x/ ;  \x\ ;  |--> 3 tunnels that support x bunnies at a time step
        // | |; / /  ;   \ \;  |
        //
        // Rz means room z
        // "reverse bottleneck" means that the input is lower than the potential output so the input will limit the debit
        // "bottleneck" means that the input si higher than the potential output so the output will limit the debit

        // Starting from a simple situation we can observe some useful things
        //
        // Case 1
        // R1 (entrance) == 6 == R2 == 6 == R3 (exit)
        // This is the ideal case where there is no bottleneck along the way
        // and the peak debit (exit's input debit) is equal with the source's debit
        //
        // Case 2 and 3
        // R1 (entrance) == 6 == R2 == 2 == R3 == 8 == R4 (exit)
        // In this example the R2 -> R3 tunnel is a bottleneck that forces
        // the debit down to 2 bunnies/time step
        // If it hadn't been for that bottleneck the output would have been
        // 6 and not 8 due to a lower input debit than the output happening at room R3
        // (still some kind of reverse bottleneck)
        //
        // Example
        //
        // | R0 |   | R1|
        // | |\ \  / /| |
        // |4| \ \/ / |2|
        // | |  \ \/  | |
        // | | / \ \  | |
        // | |/5/ \6\ | |
        // |R2/    \_ R3|
        // | |\      /| |
        // | | \    / | |
        // | |\ \  / /| |
        // |4| \ \/ / |6|
        // | |  \ \/  | |
        // | | / \ \  | |
        // | |/6/ \4\ | |
        // |R4/    \_ R5|
        //
        // R0, R1 - entrances
        // R4, R5 - exits
        //
        // The peak debit is a sum of all debits (tunnels) connected to exits
        // Theoretically this would be super simple to compute
        // by adding the declared outputs of the rooms connected to exits
        // given the "path" parameter IF there wasn't for the bottlenecks before those rooms.
        //
        // However, we can still use this idea but with a more carefully approach
        // We're going to compute the input of every room and store it in an array
        // Then when we're summing up the debits we're going to check if a bottleneck or a "reverse bottleneck" is occurring
        // "reverse bottleneck" means that the input is lower than the potential output
        // "bottleneck" means that the input si higher than the potential output
        //
        // With this in mind we will properly compute the real debit for each terminal tunnel



        //we need to compute for each room (apart from exits) the amount of bunnies it receives
        int[] roomsInputBuffer = computeInputBuffer(exits,path);

        //for each exit we'll analyze the rooms that send bunnies to it
        for (int studied_exit : exits) {
            //gettign the rooms to analyze
            List<Integer> rooms = getRoomsConnectedTo(studied_exit, path, exits);
            for (int room : rooms) {
                answer += getActualOutput(path, room, studied_exit, roomsInputBuffer);
            }
        }

        return answer;

    }

    public static List<Integer> getRoomsConnectedTo(int room_id,int[][] path,int[] exits){
        ArrayList<Integer> result = new ArrayList<>();
        for(int i = 0;i<path.length;i++){
            //if the room is not an exit and it outputs something to the room given as a parameter
            //then it's eligible for analysis
            if(path[i][room_id]!=0 && !arrayContains(exits,i))
                result.add(i);
        }

        return result;
    }


    public static int[] computeInputBuffer(int[] exits,int[][] path){
        int[] roomsInputBuffer = new int[path.length];

        //to compute the input for a room we'll add all the output of other rooms that's intended for the studied room
        for(int i = 0;i<path.length;i++){
            roomsInputBuffer[i] = 0;
            for(int j = 0;j<path.length;j++){
                //there's no need to compute the input debit for exit rooms since it doesn't necessarily reflect the truth
                //and will lately be computed with a more trustworthy method
                //also we must not take into account loopbacks
                if(j == i && arrayContains(exits,j))
                    continue;
                roomsInputBuffer[i] += path[j][i];
            }

        }

        return roomsInputBuffer;
    }

    private static int getActualOutput( int[][] path, int begin_point,int end_point,int[] inputBuffer) {
        int theoretic_output = path[begin_point][end_point];
        int actual_output = 0;
        int input = inputBuffer[begin_point];

        //the case of reverse bottleneck
        if(theoretic_output>=input){
            //we need to update the variable that stores the available input
            //so the next time the room is queried it will provide
            //a true description of the input
            inputBuffer[begin_point] = 0;
            actual_output = input;
        }else{
            //the other two cases
            //the bottleneck and the ideal case
            //
            //we need to update the variable that stores the available input
            //so the next time the room is queried it will provide
            //a true description of the input
            inputBuffer[begin_point] = input - theoretic_output;
            actual_output = theoretic_output;
        }

        return actual_output;
    }

    private static boolean arrayContains(int[] haystack, int needle){
        boolean found = false;
        for(int x:haystack)
            if (x == needle) {
                found = true;
                break;
            }

        return found;
    }

}

/*


Escape Pods
===========

You've blown up the LAMBCHOP doomsday device and broken the bunnies out of Lambda's prison - and now you need to escape from the space station as quickly and as orderly as possible! The bunnies have all gathered in various locations throughout the station, and need to make their way towards the seemingly endless amount of escape pods positioned in other parts of the station. You need to get the numerous bunnies through the various rooms to the escape pods. Unfortunately, the corridors between the rooms can only fit so many bunnies at a time. What's more, many of the corridors were resized to accommodate the LAMBCHOP, so they vary in how many bunnies can move through them at a time.

Given the starting room numbers of the groups of bunnies, the room numbers of the escape pods, and how many bunnies can fit through at a time in each direction of every corridor in between, figure out how many bunnies can safely make it to the escape pods at a time at peak.

Write a function solution(entrances, exits, path) that takes an array of integers denoting where the groups of gathered bunnies are, an array of integers denoting where the escape pods are located, and an array of an array of integers of the corridors, returning the total number of bunnies that can get through at each time step as an int. The entrances and exits are disjoint and thus will never overlap. The path element path[A][B] = C describes that the corridor going from A to B can fit C bunnies at each time step.  There are at most 50 rooms connected by the corridors and at most 2000000 bunnies that will fit at a time.

For example, if you have:
entrances = [0, 1]
exits = [4, 5]
path = [
  [0, 0, 4, 6, 0, 0],  # Room 0: Bunnies
  [0, 0, 5, 2, 0, 0],  # Room 1: Bunnies
  [0, 0, 0, 0, 4, 4],  # Room 2: Intermediate room
  [0, 0, 0, 0, 6, 6],  # Room 3: Intermediate room
  [0, 0, 0, 0, 0, 0],  # Room 4: Escape pods
  [0, 0, 0, 0, 0, 0],  # Room 5: Escape pods
]

Then in each time step, the following might happen:
0 sends 4/4 bunnies to 2 and 6/6 bunnies to 3
1 sends 4/5 bunnies to 2 and 2/2 bunnies to 3
2 sends 4/4 bunnies to 4 and 4/4 bunnies to 5
3 sends 4/6 bunnies to 4 and 4/6 bunnies to 5

So, in total, 16 bunnies could make it to the escape pods at 4 and 5 at each time step.  (Note that in this example, room 3 could have sent any variation of 8 bunnies to 4 and 5, such as 2/6 and 6/6, but the final solution remains the same.)

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
Solution.solution({0, 1}, {4, 5}, {{0, 0, 4, 6, 0, 0}, {0, 0, 5, 2, 0, 0}, {0, 0, 0, 0, 4, 4}, {0, 0, 0, 0, 6, 6}, {0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0}})
Output:
    16

Input:
Solution.solution({0}, {3}, {{0, 7, 0, 0}, {0, 0, 6, 0}, {0, 0, 0, 8}, {9, 0, 0, 0}})
Output:
    6

-- Python cases --
Input:
solution.solution([0], [3], [[0, 7, 0, 0], [0, 0, 6, 0], [0, 0, 0, 8], [9, 0, 0, 0]])
Output:
    6

Input:
solution.solution([0, 1], [4, 5], [[0, 0, 4, 6, 0, 0], [0, 0, 5, 2, 0, 0], [0, 0, 0, 0, 4, 4], [0, 0, 0, 0, 6, 6], [0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0]])
Output:
    16

 */
