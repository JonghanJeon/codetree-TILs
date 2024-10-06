import java.util.*;
import java.io.*;

class Person {
    int x, y;
    int num;
    int team;

    public Person(int x, int y, int num, int team) {
        this.x = x;
        this.y = y;
        this.num = num;
        this.team = team;
    }

    public Person(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

public class Main {

    static int n, m, k;
    static int[][] map;
    static List<Person>[] teams;
    static int[] dx = {-1, 1, 0, 0};
    static int[] dy = {0, 0, -1, 1};
    // 0이면 정방향 1이면 역방향
    // 0이면 0번째 사람이 머리
    // 1이면 마지막 사람이 머리
    static int[] teamDirection;
    static int ballDirection;
    static int answer = 0;

    public static void main(String[] args) throws IOException{
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        k = Integer.parseInt(st.nextToken());
        teams = new List[m];
        teamDirection = new int[m];
        for (int i = 0; i < m; i++) {
            teams[i] = new ArrayList<>();
            teams[i].clear();
        }
        map = new int[n][n];

        for (int i = 0; i < n; i++) {
            st = new StringTokenizer(br.readLine());
            for (int j = 0; j < n; j++) {
                map[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        // 각 팀별 사람 구하기
        int index = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (map[i][j] == 1) {
                    bfs(i, j, index);
                    index += 1;
                }
            }
        }

        game();

        System.out.println(answer);
    }

    public static void game() {
        for (int round = 0; round < k; round++) {
            // 앞으로 한칸씩 전진
            go();

            // 공던지기
            throwBall(round);
        }
    }

    public static void throwBall(int round) {
        ballDirection = (round / n) % 4; // 우(0) 상(1) 좌(2) 하(3)
        if (ballDirection == 0) {
            int ballRow = round % n;
            for (int i = 0; i < m; i++) {
                int[] personInfo = findFirstPerson(ballDirection, i, ballRow, 0);
                int score = 0;
                //맞은 사람이 있다
                if (personInfo[2] != -1) {
                    // 0번째가 머리 사람
                    if (teamDirection[i] == 0) {
                        score = (personInfo[3] + 1) * (personInfo[3] + 1);
                    } 
                    // 마지막이 머리 사람
                    else {
                        score = (teams[i].size() - personInfo[3]) * (teams[i].size() - personInfo[3]);
                    }
                    answer += score;
                    // 머리랑 꼬리 바뀜
                    teamDirection[i] = (teamDirection[i] + 1) % 2;
                    int tmp = teams[i].get(0).num;
                    teams[i].get(0).num = teams[i].get(teams[i].size() - 1).num;
                    teams[i].get(teams[i].size() - 1).num = tmp;
                }
            }
        } else if (ballDirection == 1) {
            int ballCol = round % n;
            for (int i = 0; i < m; i++) {
                int[] personInfo = findFirstPerson(ballDirection, i, 0, ballCol);
                int score = 0;
                //맞은 사람이 있다
                if (personInfo[2] != -1) {
                    // 0번째가 머리 사람
                    if (teamDirection[i] == 0) {
                        score = (personInfo[3] + 1) * (personInfo[3] + 1);
                    } 
                    // 마지막이 머리 사람
                    else {
                        score = (teams[i].size() - personInfo[3]) * (teams[i].size() - personInfo[3]);
                    }
                    answer += score;
                    // 머리랑 꼬리 바뀜
                    teamDirection[i] = (teamDirection[i] + 1) % 2;
                    int tmp = teams[i].get(0).num;
                    teams[i].get(0).num = teams[i].get(teams[i].size() - 1).num;
                    teams[i].get(teams[i].size() - 1).num = tmp;
                }
            }
        } else if (ballDirection == 2) {
            int ballRow = n - 1 - (round % n);
            for (int i = 0; i < m; i++) {
                int[] personInfo = findFirstPerson(ballDirection, i, ballRow, 0);
                int score = 0;
                //맞은 사람이 있다
                if (personInfo[2] != -1) {
                    // 0번째가 머리 사람
                    if (teamDirection[i] == 0) {
                        score = (personInfo[3] + 1) * (personInfo[3] + 1);
                    } 
                    // 마지막이 머리 사람
                    else {
                        score = (teams[i].size() - personInfo[3]) * (teams[i].size() - personInfo[3]);
                    }
                    answer += score;
                    // 머리랑 꼬리 바뀜
                    teamDirection[i] = (teamDirection[i] + 1) % 2;
                    int tmp = teams[i].get(0).num;
                    teams[i].get(0).num = teams[i].get(teams[i].size() - 1).num;
                    teams[i].get(teams[i].size() - 1).num = tmp;
                }
            }
        } else {
            int ballCol = n - 1 - (round % n);
            for (int i = 0; i < m; i++) {
                int[] personInfo = findFirstPerson(ballDirection, i, 0, ballCol);
                int score = 0;
                //맞은 사람이 있다
                if (personInfo[2] != -1) {
                    // 0번째가 머리 사람
                    if (teamDirection[i] == 0) {
                        score = (personInfo[3] + 1) * (personInfo[3] + 1);
                    } 
                    // 마지막이 머리 사람
                    else {
                        score = (teams[i].size() - personInfo[3]) * (teams[i].size() - personInfo[3]);
                    }
                    answer += score;
                    // 머리랑 꼬리 바뀜
                    teamDirection[i] = (teamDirection[i] + 1) % 2;
                    int tmp = teams[i].get(0).num;
                    teams[i].get(0).num = teams[i].get(teams[i].size() - 1).num;
                    teams[i].get(teams[i].size() - 1).num = tmp;
                }
            }
        }
    }



    public static int[] findFirstPerson(int ballDirection, int teamId, int ballRow, int ballCol) {
        int[] info;
        if (ballDirection == 0) { // 오른쪽으로    
            info = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, -1, -1};
            for (int i = 0; i < teams[teamId].size(); i++) {
                Person person = teams[teamId].get(i);
                if (person.x == ballRow && person.y < info[1]) {
                    info[0] = person.x;
                    info[1] = person.y;
                    info[2] = person.num;
                    info[3] = i; // 몇 번째 사람인지
                }
            }
        } else if (ballDirection == 1) { // 위로
            info  = new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE, -1, -1};
            for (int i = 0; i < teams[teamId].size(); i++) {
                Person person = teams[teamId].get(i);
                if (person.y == ballCol && info[0] < person.x) {
                    info[0] = person.x;
                    info[1] = person.y;
                    info[2] = person.num;
                    info[3] = i; // 몇 번째 사람인지
                }
            }
        } else if (ballDirection == 2) { // 왼쪽으로
            info = new int[]{Integer.MIN_VALUE, Integer.MIN_VALUE, -1, -1};
            for (int i = 0; i < teams[teamId].size(); i++) {
                Person person = teams[teamId].get(i);
                if (person.x == ballRow && info[1] < person.y) {
                    info[0] = person.x;
                    info[1] = person.y;
                    info[2] = person.num;
                    info[3] = i; // 몇 번째 사람인지
                }
            }

        } else { // 아래로
            info  = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, -1, -1};
            for (int i = 0; i < teams[teamId].size(); i++) {
                Person person = teams[teamId].get(i);
                if (person.y == ballCol && person.x < info[0]) {
                    info[0] = person.x;
                    info[1] = person.y;
                    info[2] = person.num;
                    info[3] = i; // 몇 번째 사람인지
                }
            }
        }

        return info;
    }

    public static void go() {
        for (int i = 0; i < m; i++) {
            // 0번째 사람이 머리라면
            if (teamDirection[i] == 0) {
                for (int idx = teams[i].size() - 1; idx >= 0; idx--) {
                    // 머리사람은 앞으로 한칸
                    if (idx == 0) {
                        int[] next = findNext(teams[i].get(idx).x, teams[i].get(idx).y);
                        teams[i].get(idx).x = next[0];
                        teams[i].get(idx).y = next[1];
                    } 
                    // 뒷사람은 앞사람 따라가기
                    else {
                        teams[i].get(idx).x = teams[i].get(idx - 1).x;
                        teams[i].get(idx).y = teams[i].get(idx - 1).y;
                    }
                }
            } 
            // 마지막 사람이 머리라면
            else {
                for (int idx = 0; idx < teams[i].size(); idx++) {
                    // 마지막사람은 앞으로 한칸
                    if (idx == teams[i].size() - 1) {
                        int[] next = findNext(teams[i].get(idx).x, teams[i].get(idx).y);
                        teams[i].get(idx).x = next[0];
                        teams[i].get(idx).y = next[1];

                    } 
                    // 뒷사람은 앞사람 따라가기
                    else {
                        teams[i].get(idx).x = teams[i].get(idx + 1).x;
                        teams[i].get(idx).y = teams[i].get(idx + 1).y;
                    }
                }
            }
        }
    }

    public static int[] findNext(int sx, int sy) {
        for (int d = 0; d < 4; d++) {
            int nx = sx + dx[d];
            int ny = sy + dy[d];

            if (!inMap(nx, ny))
                continue;

            if (map[nx][ny] == 4 || map[nx][ny] == 3) {
                return new int[]{nx, ny};
            }
        }

        return new int[]{-1, -1};
    }

    public static void bfs(int sx, int sy, int index) {
        boolean[][] visited = new boolean[n][n];
        teams[index].add(new Person(sx, sy, map[sx][sy], index));
        Queue<Person> q = new LinkedList<>();
        q.offer(new Person(sx, sy));
        visited[sx][sy] = true;

        while(!q.isEmpty()) {

            Person cur = q.poll();

            for (int d = 0; d < 4; d++) {
                int nx = cur.x + dx[d];
                int ny = cur.y + dy[d];

                if (!inMap(nx, ny))
                    continue;
                if (visited[nx][ny])
                    continue;
                if (map[nx][ny] == 4 || map[nx][ny] == 0)
                    continue;
            
                teams[index].add(new Person(nx, ny, map[nx][ny], index));
                visited[nx][ny] = true;
                q.offer(new Person(nx, ny));
            }

        }
    }

    public static boolean inMap(int sx, int sy) {
        return 0 <= sx && sx < n && 0 <= sy && sy < n;
    }
}