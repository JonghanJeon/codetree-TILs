import java.util.*;
import java.io.*;

// d -> 0, 1, 2, 3 = 상 우 하 좌

class Soul{
    int r;
    int c;
    int exit;
    
    Soul(int r, int c, int exit) {
        this.r = r;
        this.c = c;
        this.exit = exit;
    }
}

public class Main {

    // 북, 동, 남, 서
    public static int[] dx = {-1, 0, 1, 0};
    public static int[] dy = {0, 1, 0, -1};
    public static int R, C, K;
    public static int[][] board;
    public static int answer;

    public static void main(String[] args) throws IOException{
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine());
        R = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        board = new int[R][C];
        answer = 0;

        initBoard();

        for (int no = 1; no <= K; no++) {
            st = new StringTokenizer(br.readLine());
            int c = Integer.parseInt(st.nextToken()) - 1; // 출발하는 열
            int d = Integer.parseInt(st.nextToken()); // 출구 방향

            int[] result = move(c, d, no); // 보드 밖에 있으면 {0, x, y} 보드 안에 있으면 {1, x, y}
            // 골렘이 제대로 배치되지 않았을 경우
            if (result[0] == 0) {
                initBoard();
            } 
            // 골렘이 제대로 배치된 경우
            else {
                answer += bfs(result[1], result[2]);
            }
        }

        System.out.println(answer);
    }

    // 보드 초기화
    public static void initBoard() {
        for (int i = 0; i < R; i++) {
            for (int j = 0; j < C; j++) {
                board[i][j] = 0;
            }
        }
    }

    public static int[] move(int c, int d, int no) {
        int x = -2; 
        int y = c;

        while(true) {
            // 아래로 이동 가능하면
            if (check(x + 2, y) && check(x + 1, y - 1) && check(x + 1, y + 1)) {
                x += 1;
            } 
            // 왼쪽으로 회전 가능하면
            else if (check(x + 1, y - 1) && check(x - 1, y - 1) && check(x, y - 2) && check(x + 1, y - 2) && check(x + 2, y - 1)) {
                x += 1;
                y -= 1;
                d = (d + 3) % 4;
            } 
            // 오른쪽으로 회전 가능하면
            else if (check(x + 1, y + 1) && check(x - 1, y + 1) && check(x, y + 2) && check(x + 1, y + 2) && check(x + 2, y + 1)) {
                x += 1;
                y += 1;
                d = (d + 1) % 4;
            } 
            // 이동이 불가능하다면
            else {
                break;
            }
        }

        if (!inBoard(x, y) || !inBoard(x + 1, y) || !inBoard(x - 1, y) || !inBoard(x, y + 1) || !inBoard(x, y - 1)) {
            return new int[]{0,-1, -1};
        } else {
            // 골렘 번호를 각 맵 위치에 기록
            board[x][y] = board[x + 1][y] = board[x - 1][y] = board[x][y + 1] = board[x][y - 1] = no;
            int[] exit = getExit(x, y, d);
            int exitX = exit[0];
            int exitY = exit[1];
            board[exitX][exitY] = -no;
            return new int[]{1, x, y};
        }
    }

    // 보드 안에 위치해 있는지 확인
    public static boolean inBoard(int x, int y) {
        if (0 <= x && x < R && 0 <= y && y < C) {
            return true;
        }

        return false;
    }

    // 이동 가능한지 확인
    public static boolean check(int x, int y) {
        if (!inBoard(x, y)) {
            if (x < R && y >= 0 && y < C) {
                return true;
            }
        } else {
            if (board[x][y] == 0) {
                return true;
            }
        }

        return false;
    }

    // 출구 좌표 구하기
    public static int[] getExit(int x, int y, int d) {
        if (d == 0) { // 북
            return new int[]{x - 1, y};
        } else if (d == 1) { // 동
            return new int[]{x, y + 1};
        } else if (d == 2) { // 남
            return new int[]{x + 1, y};
        } else { // 서
            return new int[]{x, y - 1};
        }
    }

    public static int bfs(int row, int col) {
        List<Integer> list = new ArrayList<>();
        Queue<int[]> que = new LinkedList<>();
        que.add(new int[]{row, col});
        boolean[][] visited = new boolean[R][C];
        visited[row][col] = true;

        while(!que.isEmpty()) {
            int[] cur = que.poll();
            // x, y => 현재 위치
            // nx, ny => 다음 위치
            int x = cur[0];
            int y = cur[1];

            for (int i = 0; i < 4; i++) {
                int nx = x + dx[i];
                int ny = y + dy[i];
                // 다음 위치가 보드 밖으로 벗어났을때
                if (!inBoard(nx, ny)){
                    continue;
                }
                // 다음 위치가 방문했던 위치라면
                if (visited[nx][ny]) {
                    continue;
                }
                // 다음 위치가 빈칸이라면
                if (board[nx][ny] == 0) {
                    continue;
                }

                // 같은 골렘의 부분이거나 출구인 경우
                // 현재 위치가 출구이고 다음 위치가 출구가 아닌 경우
                if (Math.abs(board[x][y]) == Math.abs(board[nx][ny]) || 
                    (board[x][y] < 0 && Math.abs(board[x][y]) != Math.abs(board[nx][ny]))) {
                        que.add(new int[]{nx, ny});
                        visited[nx][ny] = true;
                        list.add(nx);
                    }
            }
        }

        Collections.sort(list, Collections.reverseOrder());
        return list.get(0) + 1;
    }
}