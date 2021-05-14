import java.util.ArrayList;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Solution4  {
    public static int[] solution(int[][] m) {
        try{
            int[][] param_int = m;
            //checking to see if the initial state is a terminal state
            int[] first_line_plus_denom = new int[param_int[0].length+1];
            double sum = 0;
            for(int i = 0;i<param_int[0].length;i++){
                sum+=param_int[0][i];
                first_line_plus_denom[i] = param_int[0][i];
            }
            //after we have added all probabilities of escaping from
            //that state we'll check if is there any chance of doing so
            if(sum == 0){
                first_line_plus_denom[0] = 1;
                first_line_plus_denom[first_line_plus_denom.length-1] = 1;
                return first_line_plus_denom;
            }

            //converting from int to double so we can store the probabilities
            double[][] param = new double[param_int.length][param_int[0].length];
            for (int i = 0; i < param_int.length; i++) {
                sum = 0;
                for (int k = 0; k < param_int[0].length; k++)
                    sum += param_int[i][k];
                sum = sum > 0 ? sum : 1.0;
                for (int j = 0; j < param_int[0].length; j++) {
                    param[i][j] = (1.0 * param_int[i][j]) / sum; //calculating and storing the probability to enter in the next state
                }
            }

            //I've approached this problem using Markov Absorbing Chain method
            //we'll calculate the R, Q, F and FR matrices
            TreeMap<Integer, double[]> R = new TreeMap<>();

            for (int i = 0; i < param.length; i++) {
                sum = 0;
                for (int j = 0; j < param[0].length; j++)
                    sum += param[i][j];
                //if the state is not a terminal or unreachable one,
                //the state number will be mapped with its corresponding probabilities array
                if (sum != 0)
                    R.put(i, param[i]);
               }
            int Q_Size = R.keySet().size();
            //converting treemap's entries to a double matrix which will be later used in calculation of FR
            double[][] R_Matrix = R.values().stream().map(line -> {
                ArrayList<Double> accepted_columns = new ArrayList<>();
                for (int i = 0; i < line.length; i++)
                    if (!R.keySet().contains(i)) {
                        accepted_columns.add(line[i]);
                    }
                return accepted_columns.stream().mapToDouble(i -> i).toArray();
            }).collect(Collectors.toList()).toArray(new double[R.keySet().size()][param[0].length - R.keySet().size()]);

            //keeping track of the states, states[index] => R_Matrix[index]
            Integer[] states = R.keySet().toArray(new Integer[Q_Size]);
            //will construct the Q matrix core (a 2d array which will later be wrapped in a Matrix object)
            double[][] Q = new double[Q_Size][Q_Size];
            for (int i = 0; i < Q_Size; i++) {
                for (int j = 0; j < Q_Size; j++) {
                    Q[i][j] = param[states[i]][states[j]];
                }
            }

            //Will convert the 2d arrays to Matrix Objects so we can perform operations with them
            Matrix mxR = new Matrix(R_Matrix);
            Matrix mxQ = new Matrix(Q);
            //F = (I-Q)^-1
            Matrix F = Matrix.IDENTITY(mxQ.getColumnsSize()).subtract(mxQ).inverse();
            Matrix FR = F.dot(mxR);
            //the first line holds the probabilities we're interested in
            double[] probs = FR.getLine(0);
            //will hold all denominators in an array so we can find the least common multiplier
            int[] denominators = new int[probs.length];
            //the result to be returned
            int[] result = new int[probs.length+1];
            //in mapping we'll store the probabilities as fraction (2d array, first row is the numerator, the second row is the denominator)
            int[][] mapping = new int[probs.length][2];
            for (int i = 0; i < probs.length; i++) {
                //converting probabilities from double to fraction
                int[] res= toFraction(probs[i]);
                mapping[i] = res;
                denominators[i] = res[1];
            }
            //calculating the least common multiplier
            result[result.length-1] = lcm(denominators);
            for(int i = 0;i<probs.length;i++) {
                //amplifying the numerator so the fractions don't change their value
                //we'll amplify with the result of least common multiplier divided by the original denominator
                result[i] = mapping[i][0]*(int)(result[result.length-1]/mapping[i][1]);
            }

            return result;
        }catch(Exception e){return null;}

    }


    public static int[] toFraction(double x) {
        String xStr = String.valueOf(x);
        if(xStr.length() >15)
            xStr = xStr.substring(0,15);
        //truncate the number to 15 decimals if it exceeds this limit
        x = Double.parseDouble(xStr);
        if(x == 0)
            return new int[]{0,1};

        int num = 1;
        int dnum = 1;
        double result = num/dnum;
        int iterations = 0;

        //we'll try the perfect match with no infinitesimal difference
        //if it passes the threshold of 100 iterrations it means it can't find it because of some
        //classic binary architecture problem
        //e.g 0.6 is stored as 0.599999999...
        while (x != result && iterations < 100){
            if(x < 1.0*num/dnum)
                dnum++;
            if(x > 1.0*num/dnum)
                num++;
            result = 1.0*num/dnum;

            String resString = String.valueOf(result);
            //truncate the current attempted number's length if it exceeds this limit
            if(resString.length() != xStr.length() && resString.length() > xStr.length() && xStr.length() > 10)
                resString = resString.substring(0,xStr.length());
            result = Double.parseDouble(resString);
            iterations++;
        }
        //if no direct match has been found
        //we try the same search but this time we'll take a look at the difference between the
        //attempted combination and the actual number
        iterations = 0;
        while(x != result && iterations<500){

            if(x < 1.0*num/dnum)
                dnum++;
            if(x > 1.0*num/dnum)
                num++;
            result = 1.0*num/dnum;
            String resString = String.valueOf(result);
            //truncate the current attempted number's length if it exceeds this limit
            if(resString.length() != xStr.length() && resString.length() > xStr.length())
                resString = resString.substring(0,xStr.length());
            result = Double.parseDouble(resString);

            String diff_str  = String.valueOf(x-result);
            //if the order of the difference is smaller than 10 then that combination must be the one
            if(diff_str.split("E").length>1 && Math.abs(Integer.parseInt(diff_str.split("E")[1]))>10)
                result = x;
            iterations++;
        }

        int g = gcd(num,dnum);
        //return the fraction in it's simple form
        return new int[]{num/g,dnum/g};
    }

    public static int lcm(int[] numbers){
        if(numbers.length == 1)
            return numbers[0];
        if(numbers.length == 2){
            int lcm = numbers[0]>numbers[1]?numbers[0]:numbers[1];
            while(true){
                if(lcm%numbers[0] == 0 && lcm%numbers[1] == 0 )
                    break;
                lcm++;
            }
            return lcm;
        }else{
            int[] new_numbers = new int[numbers.length-1];
            for(int i = 1;i<numbers.length;i++)
                new_numbers[i-1]=numbers[i];
            return lcm(new int[]{numbers[0],lcm(new_numbers)});
        }
    }

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
}


class Matrix{
    private double[][] matrix;
    private Double determinant;
    private Matrix inverseMatrix;
    Matrix(double[][] mx){
        matrix = mx;
    }
    public int getRowsSize(){
        return matrix.length;
    }
    public int getColumnsSize(){
        return matrix[0].length;
    }

    public double getElement(int i, int j){
        return this.matrix[i][j];
    }

    public Matrix add(Matrix mx) throws Exception{
        if(mx.getRowsSize() == this.getRowsSize() && mx.getColumnsSize() == this.getColumnsSize()){
            int N = mx.getRowsSize();
            int M = mx.getColumnsSize();
            double[][] new_m = new double[N][M];
            for(int i = 0;i<N;i++){
                for(int j = 0;j<M;j++){
                    new_m[i][j] = this.getElement(i,j) + mx.getElement(i,j);
                }
            }
            return new Matrix(new_m);
        }
        throw new Exception("Incompatible matrix");
    }
    public Matrix subtract(Matrix mx) throws Exception {
        if(mx.getRowsSize() == this.getRowsSize() && mx.getColumnsSize() == this.getColumnsSize()){
            int N = mx.getRowsSize();
            int M = mx.getColumnsSize();
            double[][] new_m = new double[N][M];
            for(int i = 0;i<N;i++){
                for(int j = 0;j<M;j++){
                    new_m[i][j] = this.getElement(i,j) - mx.getElement(i,j);
                }
            }
            return new Matrix(new_m);
        }
        throw new Exception("Incompatible matrix");
    }

    public double[] getLine(int i){
        return this.matrix[i].clone();
    }

    public double[] getColumn(int j){
        double[] column = new double[matrix.length];
        for(int i = 0; i < matrix.length;i++){
            column[i] = matrix[i][j];
        }
        return column;
    }

    public Matrix dot(Matrix mx) throws Exception {
        if( this.getColumnsSize() == mx.getRowsSize()){
            int N = this.getRowsSize();
            int M = mx.getColumnsSize();

            double[][] new_matrix = new double[N][M];
            for(int i = 0;i<N;i++){
                double[] line = this.matrix[i];
                for(int j = 0;j<M;j++){
                    double[] cols = mx.getColumn(j);
                    double sum = 0;
                    for(int k = 0;k<cols.length;k++){
                        sum += line[k]*cols[k];
                    }
                    new_matrix[i][j] = sum;
                }
            }

            return new Matrix(new_matrix);
        }

        throw new Exception("Incompatible matrix");
    }
    public Matrix transpose(){
        double[][] new_matrix = new double[this.getColumnsSize()][this.getRowsSize()];
        for(int i = 0; i<new_matrix.length;i++){
            for(int j = 0;j<new_matrix[0].length;j++){
                new_matrix[i][j] = this.getElement(j,i);
            }
        }
        return new Matrix(new_matrix);
    }

    public double determinant() throws Exception {
        if(determinant != null) {
            return determinant;
        }
        if(this.getRowsSize() == this.getColumnsSize()){
            determinant = calc(this);
            return this.determinant;
        }
        throw new Exception("This matrix can't have its determinant computed");
    }

    private double calc(Matrix m){
        double determinant = 0;
        if(m.getRowsSize() == m.getColumnsSize() && m.getRowsSize() == 1)
            return m.getElement(0,0);
        if(m.getRowsSize() == m.getColumnsSize() && m.getRowsSize() == 2){
            determinant = m.getElement(0,0)*m.getElement(1,1) - m.getElement(0,1)*m.getElement(1,0);
        }
        else{
            double[] line = m.getLine(0);
            for(int l = 0; l<line.length;l++){
                double pivot = line[l];
                double[][] child_matrix = new double[m.getRowsSize()-1][m.getColumnsSize()-1];
                //  System.out.println(m);
                for(int x = 0;x<child_matrix.length;x++){
                    int col_index = 0;
                    for(int y = 0;y<child_matrix[0].length;y++){
                        if(col_index == l)
                            col_index++;
                        child_matrix[x][y] = m.getElement(x+1,col_index);
                        col_index++;
                    }
                }
                //      System.out.println(pivot);
                //    System.out.println(new Matrix(child_matrix));
                determinant += Math.pow(-1,2+l)*pivot*calc(new Matrix(child_matrix));
            }
        }

        return  determinant;
    }

    public String toString(){
        StringBuilder output = new StringBuilder();
        for(int i = 0;i<matrix.length;i++){
            for(int j = 0; j<matrix[0].length;j++){
                output.append(" ");
                output.append(matrix[i][j]);
                output.append(" ");
            }
            output.append("\n");
        }

        return output.toString();
    }

    public boolean isInversable(){
        return this.getRowsSize() == this.getColumnsSize();
    }

    public Matrix inverse() throws Exception {
        if(this.inverseMatrix != null){
            return this.inverseMatrix;
        }
        Matrix transposed = this.transpose();
        double determinant = this.determinant();
        double[][] adj = new double[transposed.getRowsSize()][transposed.getColumnsSize()];

        for(int i = 0;i<transposed.getRowsSize();i++)
            for(int j = 0; j<transposed.getColumnsSize();j++) {
                adj[i][j] = (Math.pow(-1, (i+1) + (j+1)) * (transposed.getAdjElement(i, j)));
                adj[i][j] = adj[i][j]/determinant;
            }
        this.inverseMatrix = new Matrix(adj);
        return inverseMatrix;
    }

    public double getAdjElement(int i, int j) throws Exception {
        ArrayList<Double> columns = new ArrayList<>();
        ArrayList<double[]> rows = new ArrayList<>();
        for(int k = 0;k<this.matrix.length;k++) {
            for (int l = 0; l < this.matrix[0].length; l++) {
                if (k != i && l != j){
                    columns.add(matrix[k][l]);
                }
            }
            if(columns.size() > 0) {
                rows.add(columns.stream().mapToDouble(val -> val.doubleValue()).toArray());
                columns = new ArrayList<>();
            }
        }
        double[][] result = new double[rows.size()][rows.get(0).length];
        for(int k = 0;k<result.length;k++) {
            for (int l = 0; l < result[0].length; l++) {
                result[k][l] = rows.get(k)[l];
            }
        }
        Matrix mx = new Matrix(result);
        return mx.determinant();
    }


    public static Matrix IDENTITY(int dimension){
        double[][] mx = new double[dimension][dimension];
        for(int i = 0;i<dimension;i++)
            for(int j = 0;j<dimension;j++)
                if(i==j)
                    mx[i][j]=1;
                else
                    mx[i][j]=0;

        return new Matrix(mx);
    }
}

/*Doomsday Fuel
=============

Making fuel for the LAMBCHOP's reactor core is a tricky process because of the exotic matter involved. It starts as raw ore, then during processing,
begins randomly changing between forms, eventually reaching a stable form. There may be multiple stable forms that a sample could ultimately reach,
 not all of which are useful as fuel.


Commander Lambda has tasked you to help the scientists increase fuel creation efficiency by predicting the end state of a given ore sample.
You have carefully studied the different structures that the ore can take and which transitions it undergoes. It appears that, while random,
the probability of each structure transforming is fixed. That is, each time the ore is in 1 state, it has the same probabilities of entering
the next state (which might be the same state).  You have recorded the observed transitions in a matrix. The others in the lab have
hypothesized more exotic forms that the ore can become, but you haven't seen all of them.



Write a function solution(m) that takes an array of array of nonnegative ints representing how many times that state has gone to the next state
and return an array of ints for each terminal state giving the exact probabilities of each terminal state, represented as the numerator for each
 state, then the denominator for all of them at the end and in simplest form. The matrix is at most 10 by 10. It is guaranteed that no matter which
  state the ore is in, there is a path from that state to a terminal state. That is, the processing will always eventually end in a stable state.
  The ore starts in state 0. The denominator will fit within a signed 32-bit integer during the calculation, as long as the fraction is simplified
   regularly.


For example, consider the matrix m:
[
  [0,1,0,0,0,1],  # s0, the initial state, goes to s1 and s5 with equal probability
  [4,0,0,3,2,0],  # s1 can become s0, s3, or s4, but with different probabilities
  [0,0,0,0,0,0],  # s2 is terminal, and unreachable (never observed in practice)
  [0,0,0,0,0,0],  # s3 is terminal
  [0,0,0,0,0,0],  # s4 is terminal
  [0,0,0,0,0,0],  # s5 is terminal
]
So, we can consider different paths to terminal states, such as:
s0 -> s1 -> s3
s0 -> s1 -> s0 -> s1 -> s0 -> s1 -> s4
s0 -> s1 -> s0 -> s5
Tracing the probabilities of each, we find that
s2 has probability 0
s3 has probability 3/14
s4 has probability 1/7
s5 has probability 9/14
So, putting that together, and making a common denominator, gives an answer in the form of
[s2.numerator, s3.numerator, s4.numerator, s5.numerator, denominator] which is
[0, 3, 2, 9, 14].

Languages
=========

To provide a Java solution, edit Solution.java
To provide a Python solution, edit solution.py

-- Java cases --
Input:
Solution.solution({{0, 2, 1, 0, 0}, {0, 0, 0, 3, 4}, {0, 0, 0, 0, 0}, {0, 0, 0, 0,0}, {0, 0, 0, 0, 0}})
Output:
    [7, 6, 8, 21]

Input:
Solution.solution({{0, 1, 0, 0, 0, 1}, {4, 0, 0, 3, 2, 0}, {0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0}})
Output:
    [0, 3, 2, 9, 14]

-- Python cases --
Input:
solution.solution([[0, 2, 1, 0, 0], [0, 0, 0, 3, 4], [0, 0, 0, 0, 0], [0, 0, 0, 0,0], [0, 0, 0, 0, 0]])
Output:
    [7, 6, 8, 21]

Input:
solution.solution([[0, 1, 0, 0, 0, 1], [4, 0, 0, 3, 2, 0], [0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0]])
Output:
    [0, 3, 2, 9, 14]

Use verify [file] to test your solution and see how it does. When you are finished editing your code, use submit [file] to submit your answer.
 If your solution passes the test cases, it will be removed from your home folder.
*/


/*
"assert (
    answer({
        {0, 2, 1, 0, 0},
        {0, 0, 0, 3, 4},
        {0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0}
    }) == {7, 6, 8, 21}
)

assert (
    answer({
        {0, 1, 0, 0, 0, 1},
        {4, 0, 0, 3, 2, 0},
        {0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0}
    }) == {0, 3, 2, 9, 14}
)

assert (
    answer({
        {1, 2, 3, 0, 0, 0},
        {4, 5, 6, 0, 0, 0},
        {7, 8, 9, 1, 0, 0},
        {0, 0, 0, 0, 1, 2},
        {0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0}
    }) == {1, 2, 3}
)
assert (
    answer({
        {0}
    }) == {1, 1}
)

assert (
    answer({
        {0, 0, 12, 0, 15, 0, 0, 0, 1, 8},
        {0, 0, 60, 0, 0, 7, 13, 0, 0, 0},
        {0, 15, 0, 8, 7, 0, 0, 1, 9, 0},
        {23, 0, 0, 0, 0, 1, 0, 0, 0, 0},
        {37, 35, 0, 0, 0, 0, 3, 21, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    }) == {1, 2, 3, 4, 5, 15}
)

assert (
    answer({
        {0, 7, 0, 17, 0, 1, 0, 5, 0, 2},
        {0, 0, 29, 0, 28, 0, 3, 0, 16, 0},
        {0, 3, 0, 0, 0, 1, 0, 0, 0, 0},
        {48, 0, 3, 0, 0, 0, 17, 0, 0, 0},
        {0, 6, 0, 0, 0, 1, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    }) == {4, 5, 5, 4, 2, 20}
)

assert (
    answer({
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    }) == {1, 1, 1, 1, 1, 5}
)

assert (
    answer({
        {1, 1, 1, 0, 1, 0, 1, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {1, 0, 1, 1, 1, 0, 1, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {1, 0, 1, 0, 1, 1, 1, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {1, 0, 1, 0, 1, 0, 1, 1, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {1, 0, 1, 0, 1, 0, 1, 0, 1, 1},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    }) == {2, 1, 1, 1, 1, 6}
)

assert (
    answer({
        {0, 86, 61, 189, 0, 18, 12, 33, 66, 39},
        {0, 0, 2, 0, 0, 1, 0, 0, 0, 0},
        {15, 187, 0, 0, 18, 23, 0, 0, 0, 0},
        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    }) == {6, 44, 4, 11, 22, 13, 100}
)

assert (
    answer({
        {0, 0, 0, 0, 3, 5, 0, 0, 0, 2},
        {0, 0, 4, 0, 0, 0, 1, 0, 0, 0},
        {0, 0, 0, 4, 4, 0, 0, 0, 1, 1},
        {13, 0, 0, 0, 0, 0, 2, 0, 0, 0},
        {0, 1, 8, 7, 0, 0, 0, 1, 3, 0},
        {1, 7, 0, 0, 0, 0, 0, 2, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    }) == {1, 1, 1, 2, 5}
)"*/
