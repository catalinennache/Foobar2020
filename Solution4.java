import java.util.ArrayList;
public class Solution4 {
    public static int solution(int[][] matrix) {
        //matrix = map
        //cell = the cell of a matrix = a point on map

        //it will check if the absolute shortest path is available
        int wallSymbol = 1;
        int offset = wallSymbol;
        int walls = 0;
        for(int i = 0;i<matrix.length;i++)
            walls += matrix[i][0];
        for(int i = 0;i<matrix[0].length;i++)
            walls += matrix[matrix.length-1][i];

        if(walls<=1)
            return matrix.length+matrix[0].length-1;
        //if the shortest absolute path isn't available it starts flooding the matrix
        //writing in every cell the minimum steps required to get there
        Flood(matrix,0,0,1,1,true);

        //after the first flood it "cleans" the unused cells which clearly aren't
        //used in the shortest path (required steps untill the current cell > required steps until destination)
        if(matrix[matrix.length-1][matrix[0].length-1] > 0){
            cleanMatrixOfUnusedRoads(matrix,matrix[matrix.length-1][matrix[0].length-1]);
        }
        //It starts flooding the matrix again, but this time the starting point is the destination
        //and it "floods" with negative numbers whose module represents required steps to reach the cell in cause
        Flood(matrix,matrix.length-1,matrix[0].length-1,1,-1,true);

        //After the second flood it loops trough the map with a frame of 5x5 cells,
        //the current element stays in the center of the frame.
        //The system will check if in the studied area exists potential of a new path
        //by "binding" two paths (destroying a wall between them).
        //
        //For every new path discovered we can easily calculate its length by examining the two cells we're going to connect.
        //That is by adding the absolute values of the cells + 1 (the connecting cell / the destroyed wall)
        //and correcting the result.
        //
        //The length of every new path discovered is added to the lengthsFound array which is then parsed
        //and the minimum length is extracted.

        ArrayList<Integer> lengthsFound = new ArrayList<>();
        for(int i = 0;i<matrix.length;i++){
            for(int j = 0;j<matrix[0].length;j++){
                if(matrix[i][j]>1) {
                    int x = 0;
                    int y = 0;
                    x = i - 1 - 1;
                    y = j;

                    if (x >= 0 && x < matrix.length && matrix[x][y] < 0) {
                        lengthsFound.add(matrix[i][j] + (-1) * matrix[x][y] - 1);
                    }

                    x = i + 1 + 1;
                    y = j;

                    if (x >= 0 && x < matrix.length && matrix[x][y] < 0) {
                        lengthsFound.add(matrix[i][j] + (-1) * matrix[x][y] - 1);
                    }


                    y = j - 1 - 1;
                    x = i;

                    if (y >= 0 && y < matrix[0].length && matrix[x][y] < 0) {
                        lengthsFound.add(matrix[i][j] + (-1) * matrix[x][y] - 1);
                    }

                    y = j + 2;
                    x = i;

                    if (y >= 0 && y < matrix[0].length && matrix[x][y] < 0) {
                        lengthsFound.add(matrix[i][j] + (-1) * matrix[x][y] - 1);
                    }
                    x = i - 1;
                    y = j - 1;

                    if (x >= 0 && x < matrix.length && y >= 0 && y < matrix[0].length && matrix[x][y] < 0) {
                        lengthsFound.add(matrix[i][j] + (-1) * matrix[x][y] - 1);
                    }

                    x = i + 1;
                    y = j - 1;
                    if (x >= 0 && x < matrix.length && y >= 0 && y < matrix[0].length && matrix[x][y] < 0) {
                        lengthsFound.add(matrix[i][j] + (-1) * matrix[x][y] - 1);
                    }

                    x = i - 1;
                    y = j + 1;
                    if (x >= 0 && x < matrix.length && y >= 0 && y < matrix[0].length && matrix[x][y] < 0) {
                        lengthsFound.add(matrix[i][j] + (-1) * matrix[x][y] - 1);
                    }

                    x = i + 1;
                    y = j + 1;
                    if (x >= 0 && x < matrix.length && y >= 0 && y < matrix[0].length && matrix[x][y] < 0) {
                        lengthsFound.add(matrix[i][j] + (-1) * matrix[x][y] - 1);
                    }
                }
            }
        }

        int min = lengthsFound.get(0);
        for(int i = 1;i<lengthsFound.size();i++)
            if(min>lengthsFound.get(i))
                min = lengthsFound.get(i);

        return min;
    }

    public static void cleanMatrixOfUnusedRoads(int[][] matrix,int treshold){
        for(int i = 0;i<matrix.length;i++)
            for(int j = 0;j<matrix[0].length;j++)
                if(matrix[i][j] > treshold)
                    matrix[i][j] = 0;
    }


    public static void Flood(int[][] matrix, int x, int y, int distance,int sign,boolean initialCall)  {
        distance = Math.abs(distance);

        if(x<0||x>=matrix.length||y<0||y>=matrix[0].length || matrix[x][y] == 1 || (matrix[x][y]>1 && matrix[x][y] <= distance + 1)){
            return;
        }
        int absM =  Math.abs(matrix[x][y]);
        if(absM>1 && absM <= distance + 1 && !initialCall){
            return;
        }
        distance += 1;
        matrix[x][y] = sign*distance;
        Flood(matrix,x+1,y,distance,sign,false);
        Flood(matrix,x-1,y,distance,sign,false);
        Flood(matrix,x,y+1,distance,sign,false);
        Flood(matrix,x,y-1,distance,sign,false);
    }



}
/*Prepare the Bunnies' Escape
===========================

You're awfully close to destroying the LAMBCHOP doomsday device and freeing Commander Lambda's bunny prisoners, but once they're free of the prison blocks, the bunnies are going to need to escape Lambda's space station via the escape pods as quickly as possible. Unfortunately, the halls of the space station are a maze of corridors and dead ends that will be a deathtrap for the escaping bunnies. Fortunately, Commander Lambda has put you in charge of a remodeling project that will give you the opportunity to make things a little easier for the bunnies. Unfortunately (again), you can't just remove all obstacles between the bunnies and the escape pods - at most you can remove one wall per escape pod path, both to maintain structural integrity of the station and to avoid arousing Commander Lambda's suspicions.

You have maps of parts of the space station, each starting at a prison exit and ending at the door to an escape pod. The map is represented as a matrix of 0s and 1s, where 0s are passable space and 1s are impassable walls. The door out of the prison is at the top left (0,0) and the door into an escape pod is at the bottom right (w-1,h-1).

Write a function solution(map) that generates the length of the shortest path from the prison door to the escape pod, where you are allowed to remove one wall as part of your remodeling plans. The path length is the total number of nodes you pass through, counting both the entrance and exit nodes. The starting and ending positions are always passable (0). The map will always be solvable, though you may or may not need to remove a wall. The height and width of the map can be from 2 to 20. Moves can only be made in cardinal directions; no diagonal moves are allowed.

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
solution.solution([[0, 1, 1, 0], [0, 0, 0, 1], [1, 1, 0, 0], [1, 1, 1, 0]])
Output:
    7

Input:
solution.solution([[0, 0, 0, 0, 0, 0], [1, 1, 1, 1, 1, 0], [0, 0, 0, 0, 0, 0], [0, 1, 1, 1, 1, 1], [0, 1, 1, 1, 1, 1], [0, 0, 0, 0, 0, 0]])
Output:
    11

-- Java cases --
Input:
Solution.solution({{0, 1, 1, 0}, {0, 0, 0, 1}, {1, 1, 0, 0}, {1, 1, 1, 0}})
Output:
    7

Input:
Solution.solution({{0, 0, 0, 0, 0, 0}, {1, 1, 1, 1, 1, 0}, {0, 0, 0, 0, 0, 0}, {0, 1, 1, 1, 1, 1}, {0, 1, 1, 1, 1, 1}, {0, 0, 0, 0, 0, 0}})
Output:
    11
    */
