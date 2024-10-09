import java.util.*;
import java.io.*;

/*

n x n 격자에서 진행. 술래는 정 중앙에 서있음
m 명의 도망자 - 좌우, 상하
좌우로 움직이는 사람은 항상 오른쪽으로 시작, 상하로 움직이는 사람은 항상 아래쪽으로 시작
h 개의 나무가 있음
나무와 도망자가 초기에 겹쳐질 수 있음
m 명의 도망자가 먼저 동시에 움직이고, 술래가 움직이고, 도망자가 움직이고, 술래가 움직이고 ...
도망자 1턴 술래 1턴 진행하는 것을 k번 반복

도망자가 움직일 때 현재 술래와의 거리가 3 이하인 도망자만 움직임.
도망자와 술래간의 거리는 가로갭 + 세로갭

현재 바라보고 있는 방향으로 1칸 움직인다 했을 때 격자를 벗어나지 않는 경우
    움직이려는 칸에 술래가 있는 경우라면 움직이지 않음.
    움직이려는 칸에 술래가 있지 않다면 해당 칸으로 이동. 나무가 있어도 ok

현재 바라보고 있는 방향으로 1칸 움직인다 했을 때 격자를 벗어나는 경우
    먼저 방향을 반대로 틀어줌.
    이후 바라보고 있는 방향으로 1칸 움직인다 했을 때 술래가 없다면 1칸 앞으로 이동.
    
술래는 처음 위 방향으로 시작하여 달팽이 모양으로 움직임.
만약 끝에 도달하게 되면 다시 거꾸로 중심으로 이동. 다시 중심에 오게 되면 다시 원래대로 돔.

술래는 1번의 턴 동안 정확히 한 칸 이동.
이동방향이 틀어지는 지점이라면 방향을 바로 틀어줌.
만약 이동을 통해 양 끝에 해당하는 위치 (1행 1열)나 정 중앙에 도달하게 되면 역시 방향을 바로 틀어줘야함.

이동 직후 술래는 턴을 넘기기 전에 시야 내에 있는 도망자를 잡게 됨.
    술래의 시야는 현재 바라보고 있는 방향을 기준으로 현재 칸을 포함하여 총 3칸.
    격자 크기에 상관없이 술래의 시야는 항상 3칸.
    만약 나무가 놓여 있는 칸이라면 해당 칸에 있는 도망자는 나무에 가려져 보이지 않게 됨.
    술래는 현재 턴을 t번째 턴이라고 했을 때, t x 현재 턴에서 잡힌 도망자의 수 만큼 점수를 얻게 됨.
    잡힌 도망자는 사라짐.
*/

public class Main {

    public static class Pair {
        int x, y;

        public Pair(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    public static class Runner{
        int x, y, id, type, dir;

        public Runner (int x, int y, int id, int type, int dir) {
            this.x = x;
            this.y = y;
            this.id = id;
            this.type = type;
            this.dir = dir;
        }
    }

    // 격자 크기 n
    // 도망자의 수 m
    //      x, y (좌표), d(1인 경우 좌우 움직임, 2인 경우 상하 움직임)
    // 나무의 개수 h
    public static int n, m, h, k;
    public static int[][] runGrid;
    public static boolean[][] treeGrid;
    public static Pair cur; // 술래
    public static Runner[] runnerArr;
    public static Pair[] treeArr;
    public static boolean[] isLive;
    public static int ans = 0;
    public static int moveDir;
    public static int moveNum;
    public static int curNum;
    public static int delta;

    // 위 오른쪽 아래 왼쪽
    public static int[][] curDir = new int[][]{{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
    
    // 오른쪽 왼쪽 (d = 1)
    public static int[][] rlDir = new int[][]{{0, 1}, {0, -1}};
    // 아래 위 (d = 2)
    public static int[][] udDir = new int[][]{{1, 0}, {-1 ,0}};

    public static boolean inGrid(int x, int y) {
        return 0 <= x && x < n && 0 <= y && y < n;
    }

    public static int getDistance(Runner runner) {
        return Math.abs(runner.x - cur.x) + Math.abs(runner.y - cur.y);
    }
    
    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        h = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());
        runGrid = new int[n][n];
        treeGrid = new boolean[n][n];
        cur = new Pair(n / 2, n / 2);
        runnerArr = new Runner[m + 1];
        treeArr = new Pair[h];
        isLive = new boolean[m + 1];

        for (int i = 1; i <= m; i++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;
            int type = Integer.parseInt(st.nextToken());
            Runner runner = new Runner(x, y, i, type, 0);
            runnerArr[i] = runner;
            runGrid[x][y] = i;
            isLive[i] = true;
        }

        for (int i = 0; i < h; i++) {
            st = new StringTokenizer(br.readLine());
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;
            Pair tree = new Pair(x, y);
            treeArr[i] = tree;
            treeGrid[x][y] = true;
        }

        moveDir = 0;
        moveNum = 1;
        curNum = 0;
        delta = 1;

        // for (int i = 0 ; i < n; i ++) {
        //     for (int j = 0 ; j < n ; j ++) {
        //         System.out.print(runGrid[i][j] + " ");
        //     }
        //     System.out.println();
        // }

        for (int t = 1; t <= k; t++) {

            //도망자 움직임
            for (int i = 1; i <= m; i++) {
                Runner runner = runnerArr[i];
                // 탈락한 도망자는 넘어감
                if (!isLive[i]) continue;
                
                // 거리 3 초과시 넘어감
                int dist = getDistance(runner);
                if (dist > 3) continue;

                runnerMove(runner);
            }

            // System.out.println("RUNNER GRID TURN " + t);
            // for (int i = 0 ; i < n; i ++) {
            //     for (int j = 0 ; j < n ; j ++) {
            //         System.out.print(runGrid[i][j] + " ");
            //     }
            //     System.out.println();
            // }

            //술래 움직임
            // if (cur.x == (n / 2) && cur.y == (n / 2)) {
            //     moveDir = 0; moveNum = 1; curNum = 0; delta = 1;
            // } else if (cur.x == 0 && cur.y == 0) {
            //     moveDir = 2; moveNum = n - 1; curNum = 0; delta = -1;
            // }
            int[] dir = curDir[moveDir];

            cur.x += dir[0];
            cur.y += dir[1];
            curNum += 1;

            // 이동 방향이 틀어지는 지점이라면 즉시 방향 전환.
            if (curNum == moveNum) {
                if (cur.x == (n / 2) && cur.y == (n / 2)) {
                    moveDir = 0; moveNum = 1; curNum = 0; delta = 1;
                } else if (cur.x == 0 && cur.y == 0) {
                    moveDir = 2; moveNum = n - 1; curNum = 0; delta = -1;
                } else {
                    moveDir = (moveDir + delta) % 4;
                    if (moveDir < 0) moveDir += 4;
                    curNum = 0;
                }
                
                dir = curDir[moveDir];
            }

            int cx = cur.x;
            int cy = cur.y;
            while(true) {
                // System.out.println("cx = " + cx + ", cy = " + cy);
                if (cx == n || cy == n) break;

                // 나무가 없고, 도망자가 있는 경우 잡히고 술래 점수 추가
                if (!treeGrid[cx][cy] && runGrid[cx][cy] > 0) {
                    int runnerId = runGrid[cx][cy];
                    runGrid[cx][cy] = 0;
                    isLive[runnerId] = false;
                    ans += t;
                }

                cx += dir[0];
                cy += dir[1];
            }
        }

        System.out.println(ans);
    }

    public static void runnerMove(Runner runner) {
        int[][] runnerDir;
        if (runner.type == 1) runnerDir = rlDir;
        else runnerDir = udDir;

        int nx = runner.x + runnerDir[runner.dir][0];
        int ny = runner.y + runnerDir[runner.dir][1];

        // 격자 안일때
        // 해당 칸에 술래가 없다면 이동
        if (inGrid(nx, ny) && !(nx == cur.x && ny == cur.y)) {
            runGrid[runner.x][runner.y] = 0;
            runner.x = nx; runner.y = ny;
            runGrid[runner.x][runner.y] = runner.id;
        } 
        // 격자 밖일 때
        else {
            runner.dir = (runner.dir + 1) % 2;
            nx = runner.x + runnerDir[runner.dir][0];
            ny = runner.y + runnerDir[runner.dir][1];
            // 격자 안이고 술래가 없다면 이동
            if (inGrid(nx, ny) && !(nx == cur.x && ny == cur.y)) {
                runGrid[runner.x][runner.y] = 0;
                runner.x = nx; runner.y = ny;
                runGrid[runner.x][runner.y] = runner.id;
            }
        }
    }
}