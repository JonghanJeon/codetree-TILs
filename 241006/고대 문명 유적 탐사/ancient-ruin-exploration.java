import java.util.*;
import java.io.*;

class Node implements Comparable<Node>{
    int x;
    int y;
    int type;
    int score;

    Node(int x, int y, int type, int score) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.score = score;
    }

    @Override
    public int compareTo(Node o) {
        if (this.score == o.score) {

            if (this.type == o.type) {

                if (this.y == o.y) {
                    
                    return this.x - o.x; // 4. 행 작은순 (오름차순)
                }

                return this.y - o.y; // 3. 열 작은순 (오름차순)
            }

            return this.type - o.type; // 2. 각도 작은순 (오름차순)
        }

        return o.score - this.score; // 1. 점수 높은순 (내림차순)
    }
}

public class Main {
    // 상 하 좌 우 벡터
    public static int[] dx = {-1, 1, 0, 0};
    public static int[] dy = {0, 0, -1, 1};

    public static int K, M;
    public static int[][] board;
    public static Queue<Integer> numbers = new LinkedList<>();
    public static List<int[]> remove;

    public static void main(String[] args) throws IOException{
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine());
        K = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        board = new int[5][5];
        int[] answer = new int[K];

        for (int i = 0; i < 5; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < 5; j++) {
                board[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        st = new StringTokenizer(br.readLine());
        for (int i = 0; i < M; i++) {
            numbers.add(Integer.parseInt(st.nextToken()));
        }

        for (int no = 0; no < K; no++) {
            List<Node> candidate = new ArrayList<>();

            // 좌상 좌표를 기준으로 90, 180, 270 회전
            for (int type = 0; type < 3; type++) {
                for (int i = 0; i <= 2; i++) {
                    for (int j = 0; j <= 2; j++) {
                        int[][] newBoard = rotateBoard(i, j, type);
                        int score = bfs(newBoard);
                        if (score > 0) {
                            candidate.add(new Node(i, j, type, score));
                        }
                    }
                }
            }

            // 후보 없으면 즉시 종료
            if (candidate.isEmpty()) {
                break;
            }

            Collections.sort(candidate); // 후보 조건에 따라 정렬

            Node best = candidate.get(0);
            int sx = best.x;
            int sy = best.y;

            // 보드회전
            board = rotateBoard(sx, sy, best.type);
            int score = bfs(board);
            int sum = 0;

            while (score > 0) {
                fillMap();
                sum += score;

                score = bfs(board);
            }

            answer[no] = sum;
        }

        for (int ans : answer) {
            if (ans == 0) {
                break;
            }

            System.out.print(ans + " ");
        }
    }

    public static void fillMap() {
        Collections.sort(remove, (o1, o2) -> {
            if (o1[1] == o2[1]) {
                return o2[0] - o1[0];
            }

            return o1[1] - o2[1];
        });

        for (int[] pair : remove) {
            int row = pair[0];
            int col = pair[1];
            board[row][col] = numbers.poll();
        }
    }

    public static int bfs(int[][] newBoard) {
        boolean[][] visited = new boolean[5][5];
        Queue<int[]> q = new LinkedList<>();

        remove = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (!visited[i][j]) {
                    // 방문하지 않은 경우 bfs 를 통해 붙어있는 유적 개수 조사
                    q.add(new int[]{i, j});
                    visited[i][j] = true;
                    int cnt = 1;

                    List<int[]> list = new ArrayList<>();
                    list.add(new int[]{i, j});

                    while(!q.isEmpty()) {
                        int[] current = q.poll();

                        for (int d = 0; d < 4; d++) {
                            int nx = current[0] + dx[d];
                            int ny = current[1] + dy[d];

                            if (!inBoard(nx, ny))
                                continue;
                            if (visited[nx][ny])
                                continue;
                            if (newBoard[i][j] != newBoard[nx][ny])
                                continue;

                            q.add(new int[]{nx, ny});
                            visited[nx][ny] = true;

                            cnt += 1;
                            list.add(new int[]{nx, ny});
                        }
                    }
                    // 개수가 3개 이상이라면 삭제 대상.
                    if (cnt >= 3) {
                        remove.addAll(list);
                    }

                }
            }
        }

        return remove.size();
    }

    public static int[][] rotateBoard(int sx, int sy, int type) {
        int[][] newBoard = new int[5][5];

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                newBoard[i][j] = board[i][j];
            }
        }

        //부분회전
        int[][] matrix = new int[3][3];
        int[][] rotateMatrix = new int[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                matrix[i][j] = board[sx + i][sy + j];
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (type == 0) { // 90
                    rotateMatrix[j][2 - i] = matrix[i][j];
                } else if (type == 1) { // 180
                    rotateMatrix[2 - i][2 - j] = matrix[i][j];
                } else { // 270
                    rotateMatrix[2 - j][i] = matrix[i][j];
                }
            }
        }

        //회전 내용 반영
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                newBoard[sx + i][sy + j] = rotateMatrix[i][j];
            }
        }

        return newBoard;
    }

    public static boolean inBoard(int x, int y) {
        return 0 <= x && x < 5 && 0 <= y && y < 5;
    }
}