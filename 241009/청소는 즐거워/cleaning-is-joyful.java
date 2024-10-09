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

    // 좌 하 우 상
    public static int[] dx = {0, 1, 0, -1};
    public static int[] dy = {-1, 0, 1, 0};

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
        while(!(currX == 0 && currY == 0)) {
            for (int i = 0; i < moveNum; i++) {
                currX += dx[moveDir];
                currY += dy[moveDir];

                int totalAddDust = 0;
                for (int x = 0; x < 5; x++) {
                    for (int y = 0; y < 5; y++) {
                        if (dustRatio[moveDir][x][y] != 0) {
                            int dust = grid[currX][currY] * dustRatio[moveDir][x][y] / 100;

                            int nx = currX - 2 + x;
                            int ny = currY - 2 + y;

                            if (!inGrid(nx, ny)) ans += dust;
                            else grid[nx][ny] += dust;
                            
                            totalAddDust += dust;
                        }
                    }
                }

                int ax = currX + dx[moveDir];
                int ay = currY + dy[moveDir];
                int tmp = grid[currX][currY] - totalAddDust;
                
                if(!inGrid(ax, ay)) ans += tmp;
                else grid[ax][ay] += tmp;

                if (currX == 0 && currY == 0) break;
            }

            moveDir = (moveDir + 1) % 4;
            if (moveDir == 0 || moveDir == 2)
                moveNum++;
        }

        System.out.print(ans);
    }
}