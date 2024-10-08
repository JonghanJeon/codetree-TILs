import java.util.*;
import java.io.*;

public class Main {

    static class Rule {
        int dir; // 이동 방향
        int amount; // 이동 칸수

        Rule(int dir, int amount) {
            this.dir = dir;
            this.amount = amount;
        }
    }

    // n : 격자의 크기
    // m : 총 년수
    static int n, m;
    static int[][] board;
    static boolean[][] tonicBoard;
    static Rule[] rule;

    // 0, 우, 우상, 상, 좌상, 좌, 좌하, 하, 우하 
    static final int[] dx = {Integer.MIN_VALUE, 0, -1, -1, -1, 0, 1, 1, 1};
    static final int[] dy = {Integer.MIN_VALUE, 1, 1, 0, -1, -1, -1, 0, 1};

    // 좌상, 우상, 우하, 좌하
    static final int[] treeDx = {-1, -1, 1, 1};
    static final int[] treeDy = {-1, 1, 1, -1};

    static boolean inBoard(int x, int y) {
        return 0 <= x && x < n && 0 <= y && y < n;
    }

    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());

        board = new int[n][n];
        tonicBoard = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < n; j++) {
                board[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        for (int i = n - 1; i >= n - 2; i--) {
            for (int j = 0; j <= 1; j++) {
                tonicBoard[i][j] = true;
            }
        }

        rule = new Rule[m + 1];
        for (int i = 1; i <= m; i++) {
            st = new StringTokenizer(br.readLine());
            int d = Integer.parseInt(st.nextToken());
            int p = Integer.parseInt(st.nextToken());
            rule[i] = new Rule(d, p);
        }

        for (int year = 1; year <= m; year++) {
            int dir = rule[year].dir;
            int amount = rule[year].amount;

            // 특수 영양제 이동
            List<int[]> moveTonic = new ArrayList<>();
            for (int x = 0; x < n; x++) {
                for (int y = 0; y < n; y++) {
                    
                    if (tonicBoard[x][y]) {
                        int px = x;
                        int py = y;

                        int nx = px + dx[dir] * amount;
                        if (nx >= 0) nx = nx % n;
                        else nx = n + (nx % n);

                        int ny = py + dy[dir] * amount;
                        if (ny >= 0) ny = ny % n;
                        else ny = n + (ny % n);

                        tonicBoard[px][py] = false;
                        moveTonic.add(new int[]{nx, ny});
                    }
                }
            }

            for (int[] pair : moveTonic) {
                int x = pair[0];
                int y = pair[1];
                tonicBoard[x][y] = true;
            }

            // System.out.println("AFTER MOVE");
            // for (int i = 0 ; i < n ; i++) {
            //     for (int j = 0 ; j < n ; j++) {
            //         if (tonicBoard[i][j]) System.out.print("T ");
            //         else System.out.print("0 ");
            //     }
            //     System.out.println();
            // }

            // 특수 영양제 투입
            for (int x = 0; x < n; x++) {
                for (int y = 0; y < n; y++) {
                    
                    if (tonicBoard[x][y]) {
                        board[x][y] += 1; // 1씩 성장
                    }
                }
            }

            // 추가성장
            int[][] tmpBoard = new int[n][n]; // 투입한 장소 보관할 임시 보드
            for (int x = 0; x < n; x++) {
                for (int y = 0; y < n; y++) {
                    
                    if (tonicBoard[x][y]) {
                        for (int d = 0; d < 4; d++) {
                            int nx = x + treeDx[d];
                            int ny = y + treeDy[d];

                            // 인접한 대각선에 높이가 1이상인 나무가 있다면 count
                            if (inBoard(nx, ny) && board[nx][ny] > 0) {
                                tmpBoard[x][y] += 1;
                            }
                        }
                        // 투입한 특수 영양제는 소멸
                        tonicBoard[x][y] = false;
                    }
                }
            }

            for (int x = 0; x < n; x++) {
                for (int y = 0; y < n; y++) {
                    if (tmpBoard[x][y] != 0) {
                        board[x][y] += tmpBoard[x][y];
                    }
                }
            }

            // 특수영양제 구매
            for (int x = 0; x < n; x++) {
                for (int y = 0; y < n; y++) {
                    // 투입하지 않은 장소에 높이가 2이상인 곳
                    if (tmpBoard[x][y] == 0 && board[x][y] >= 2) {
                        // 높이 2를 베고 특수영양제 구입 후 해당 장소에 특수 영양제 올려둠.
                        board[x][y] -= 2;
                        tonicBoard[x][y] = true;
                    }
                }
            }

            // System.out.println("AFTER BOARD");
            // for (int i = 0 ; i < n ; i++) {
            //     for (int j = 0 ; j < n ; j++) {
            //         if (tonicBoard[i][j]) System.out.print("T ");
            //         else System.out.print("0 ");
            //     }
            //     System.out.println();
            // }
        }

        int answer = 0;
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                answer += board[x][y];
            }
        }

        System.out.println(answer);
    }
}