import java.util.*;
import java.io.*;

/*

선택
1. 유물 1차 획득 가치를 최대화
2. 회전 각도가 가장 작은 방법
3. 회전 중심 좌표 열이 가장 작은 방법
4. 회전 중심 좌표 행이 가장 작은 방법

획득
1. 3개이상 붙어있는 유적 탐사.
2. 열 번호가 작은 순으로 조각 재생성
3. 행 번호가 큰순으로 조각 재생성
4. 부족한 경우는 없음.

연쇄 획득
1. 재성성된 유물을 바탕으로 다시 획득 시도.
2. 유물이 될 수 없을 때까지 반복

반복
턴에 걸쳐서 진행.
*/

class Node implements Comparable<Node>{
    // 회전 기준 좌표
    int x;
    int y;
    // 점수
    int score;
    // 회전각도
    int type;

    Node(int x, int y, int score, int type) {
        this.x = x;
        this.y = y;
        this.score = score;
        this.type = type;
    }

    /*
    1. 유물 1차 획득 가치를 최대화 (내림차순)
    2. 회전 각도가 가장 작은 방법 (오름차순)
    3. 회전 중심 좌표 열이 가장 작은 방법 (오름차순)
    4. 회전 중심 좌표 행이 가장 작은 방법 (오름차순)
    */
    @Override
    public int compareTo(Node o) {
        if (this.score == o.score) {
            if (this.type == o.type) {
                if (this.y == o.y) {
                    return this.x - o.x;
                }
                return this.y - o.y;
            }
            return this.type - o.type;
        }
        return o.score - this.score;
    }

}

public class Main {

    static int K, M;
    static int[][] board;
    static Queue<Integer> bonus = new LinkedList<>();

    // 상 하 좌 우
    static int[] dx = {-1, 1, 0, 0};
    static int[] dy = {0, 0, -1, 1};

    static List<int[]> remove;

    public static void main(String[] args) throws IOException{
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine());
        K = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        board = new int[5][5];

        for (int i = 0; i < 5; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < 5; j++) {
                board[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        st = new StringTokenizer(br.readLine());
        for (int i = 0; i < M; i++) {
            bonus.offer(Integer.parseInt(st.nextToken()));
        }

        for (int no = 0; no < K; no++) {
            
            List<Node> candidate = new ArrayList<>();
            
            for (int type = 0; type < 3; type ++) {
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        //회전시킨 보드 구하기
                        int[][] newBoard = getNewBoard(i, j, type);

                        //회전시킨 보드에서 점수 구하기
                        int score = bfs(newBoard);

                        //점수가 0이 아니라면 후보에 추가
                        if (score > 0)
                            candidate.add(new Node(i, j, score, type));
                    }
                }
            }

            // 탐사 진행 과정에서 어떠한 방법을 사용하더라도 유물을 획득할 수 없었다면 모든 탐사는 그 즉시 종료
            if (candidate.isEmpty()) {
                break;
            }

            Collections.sort(candidate);

            Node best = candidate.get(0);
            board = getNewBoard(best.x, best.y, best.type);
            int score = bfs(board);
            int sum = 0;

            while (score > 0) {
                sum += score;
                fillBoard();

                score = bfs(board);
            }

            System.out.print(sum + " ");
        }
    }

    public static void fillBoard() {
        Collections.sort(remove, (o1, o2) -> {
            if (o1[1] == o2[1]) {
                return o2[0] - o1[0];
            }
            return o1[1] - o2[1];
        });

        for (int[] tmp : remove) {
            int row = tmp[0];
            int col = tmp[1];
            board[row][col] = bonus.poll();
        }
    }

    public static int bfs(int[][] newBoard) {
        Queue<int[]> q = new LinkedList<>();
        boolean[][] visited = new boolean[5][5];

        remove = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (!visited[i][j]) {
                    q.offer(new int[]{i, j});
                    visited[i][j] = true;

                    List<int[]> tmp = new ArrayList<>();
                    tmp.add(new int[]{i, j});

                    while(!q.isEmpty()) {
                        int[] current = q.poll();
                        int cx = current[0];
                        int cy = current[1];

                        for (int d = 0; d < 4; d++) {
                            int nx = current[0] + dx[d];
                            int ny = current[1] + dy[d];

                            // 다음 칸이 보드 밖에 있으면 continue;
                            if (!inBoard(nx, ny))
                                continue;
                            // 다음 칸이 방문했던 칸이면 continue;
                            if (visited[nx][ny])
                                continue;
                            // 다음 칸이 기존 칸과 다른 유적이면 continue;
                            if (newBoard[cx][cy] != newBoard[nx][ny])
                                continue;

                            q.offer(new int[]{nx, ny});
                            tmp.add(new int[]{nx, ny});
                            visited[nx][ny] = true;
                        }
                    }

                    if (tmp.size() >= 3) {
                        remove.addAll(tmp);
                    }
                }
            }
        }

        return remove.size();
    }

    public static boolean inBoard(int sx, int sy) {
        return 0 <= sx && sx < 5 && 0 <= sy && sy < 5;
    }

    public static int[][] getNewBoard(int sx, int sy, int type) {
        int[][] result = new int[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                result[i][j] = board[i][j];
            }
        }

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

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                result[sx +i][sy + j] = rotateMatrix[i][j];
            }
        }

        return result;
    }
}