import java.util.*;
import java.io.*;

public class Main {

    public static int n;
    public static int currX, currY;
    public static int moveDir, moveNum;

    public static int ans = 0;

    public static int[][] grid;

    public static int[][][] dustRatio = new int[][][]{
        {
            {0,  0, 2, 0, 0},
            {0, 10, 7, 1, 0},
            {5,  0, 0, 0, 0},
            {0, 10, 7, 1, 0},
            {0,  0, 2, 0, 0},
        },
        {
            {0,  0, 0,  0, 0},
            {0,  1, 0,  1, 0},
            {2,  7, 0,  7, 2},
            {0, 10, 0, 10, 0},
            {0,  0, 5,  0, 0},
        },
        {
            {0, 0, 2,  0, 0},
            {0, 1, 7, 10, 0},
            {0, 0, 0,  0, 5},
            {0, 1, 7, 10, 0},
            {0, 0, 2,  0, 0},
        },
        {
            {0,  0, 5,  0, 0},
            {0, 10, 0, 10, 0},
            {2,  7, 0,  7, 2},
            {0,  1, 0,  1, 0},
            {0,  0, 0,  0, 0},
        }
    };

    public static boolean inGrid(int x, int y) {
        return 0 <= x && x < n && 0 <= y && y < n;
    }

    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());

        grid = new int[n][n];
        for (int i = 0 ; i < n; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0 ; j < n ; j++) {
                grid[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        currX = n / 2;
        currY = n / 2;
        moveDir = 0; 
        moveNum = 1;

        // 구현해야함.

        System.out.print(ans);
    }
}