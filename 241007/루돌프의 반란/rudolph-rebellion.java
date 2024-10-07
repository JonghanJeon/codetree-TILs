import java.util.*;
import java.io.*;

class Rudolf {
    int x, y;

    public Rudolf(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

class Santa {
    int x, y;
    int score;
    int num;

    public Santa(int x, int y, int score, int num) {
        this.x = x;
        this.y = y;
        this.score = score;
        this.num = num;
    }
}

class SantaNext implements Comparable<SantaNext> {
    int x, y;
    int distance;
    int type; // 상(0) 우(1) 하(2) 좌(3)

    public SantaNext(int x, int y, int distance, int type) {
        this.x = x;
        this.y = y;
        this.distance = distance;
        this.type = type;
    }

    @Override
    public int compareTo(SantaNext o) {
        if (this.distance == o.distance) {
            return this.type - o.type;
        }
        return this.distance - o.distance;
    }
}

public class Main {

    static int N, M, P, C, D;
    static Rudolf rudolf;
    static List<Santa> santaList;
    static int[][] map; // 루돌프 -1, 빈칸 0, 산타는 산타 번호
    static int[] santaStatus; // 0 정상, 1 기절, 2 탈락

    // 좌상, 상, 우상, 좌, 우, 좌하, 하, 우하
    static int[] rdx = {-1, -1, -1, 0, 0, 1, 1, 1};
    static int[] rdy = {-1, 0, 1, -1, 1, -1, 0, 1};

    // 상 우 하 좌
    static int[] sdx = {-1, 0, 1, 0};
    static int[] sdy = {0, 1, 0, -1};

    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        // 초기화
        st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        P = Integer.parseInt(st.nextToken());
        C = Integer.parseInt(st.nextToken());
        D = Integer.parseInt(st.nextToken());

        st = new StringTokenizer(br.readLine());
        rudolf = new Rudolf(Integer.parseInt(st.nextToken()) - 1, Integer.parseInt(st.nextToken()) - 1);

        santaList = new ArrayList<>();
        santaStatus = new int[P];
        for (int i = 0; i < P; i++) {
            st = new StringTokenizer(br.readLine());
            int num = Integer.parseInt(st.nextToken());
            int x = Integer.parseInt(st.nextToken()) - 1;
            int y = Integer.parseInt(st.nextToken()) - 1;
            santaList.add(new Santa(x, y, 0, num));
        }

        Collections.sort(santaList, (o1, o2) -> {
            return o1.num - o2.num;
        });

        map = new int[N][N];

        setMap();

        for (int[] row : map) {
            for (int num : row) {
                System.out.print(num + " ");
            }
            System.out.println();
        }

        for (int no = 1; no <= M; no++) {
            System.out.println("START TURN " + no);
            game(no);
            System.out.println("AFTER TURN" + no);
            for (int[] row : map) {
                for (int num : row) {
                    System.out.print(num + " ");
                }
                System.out.println();
            }
            System.out.print("SantaStatus = ");
            for (int status : santaStatus) {
                System.out.print(status + " ");
            }
            System.out.print("score : ");
            for (Santa santa : santaList) {
                System.out.print(santa.score + " ");
            }
            System.out.println();
        }

        for (Santa santa : santaList) {
            System.out.print(santa.score + " ");
        }
    }

    public static void game(int no) {
        int gameOverCount = 0;
        for (int i = 0; i < P; i++) {
            if (santaStatus[i] == 2) {
                gameOverCount += 1;
            }
        }

        // 모든 산타 탈락시 게임 종료
        if (gameOverCount == P) {
            return;
        }

        // 루돌프 움직임
        goRudolf();

        // 산타 순서대로 움직임
        for (int i = 0; i < P; i++) {

            // 탈락한 산타 넘기기
            if (santaStatus[i] == 2){
                continue;
            } else if (santaStatus[i] == 3) {
                santaStatus[i] = 0;
                continue;
            } else if (santaStatus[i] == 1) {
                santaStatus[i] = 3;
                continue;
            } else {
                // 산타 움직임
                goSanta(santaList.get(i));
            }
        }

        // 턴 종료 - 탈락하지 않은 산타 1점씩 부여
        for (int i = 0; i < P; i++) {
            if (santaStatus[i] == 2)
                continue;
            
            santaList.get(i).score += 1;
        }

    }

    public static void goSanta(Santa santa) {
        // 이동 위치 구하기
        int[] nextInfo = getSantaNext(santa);

        // 이동 불가시 이동 안함.
        if (nextInfo[0] == Integer.MAX_VALUE) {
            return;
        } 
        // 이동 가능시 산타 이동
        else {
            moveSanta(santa, nextInfo[0], nextInfo[1]);
        }
    }

    public static void moveSanta(Santa santa, int nx, int ny) {
        // 충돌 발생 안했을 때
        if (map[nx][ny] != -1) {
            // 산타 이동 및 맵 변경
            map[santa.x][santa.y] = 0;
            setSanta(santa, nx, ny);
        } 
        // 충돌 발생시
        else {
            System.out.println("산타 이동으로 충돌 발생!!");
            System.out.println(santa.num + "번 산타 " + "[ " + santa.x + ", " + santa.y + "] 에서 충돌!!");
            // 산타 점수 추가
            santa.score += D;
            
            // 날아갈 방향 구하기
            int xDirection = santa.x - rudolf.x;
            int yDirection = santa.y - rudolf.y;

            // System.out.println("xDirection = " + xDirection);
            // System.out.println("yDirection = " + yDirection);

            // 착지할 위치 구하기
            int newSantaX = rudolf.x + (D * xDirection);
            int newSantaY = rudolf.y + (D * yDirection);

            // System.out.println("newSantaX = " + newSantaX);
            // System.out.println("newSantaY = " + newSantaY);

            // 맵 밖이라면 산타 탈락 및 맵 변경
            if (!inMap(newSantaX, newSantaY)) {
                santaStatus[santa.num - 1] = 2;
                map[santa.x][santa.y] = 0;
            } 
            // 착지한 위치가 맵 안이라면
            else {
                // 아무도 없으면
                if (map[newSantaX][newSantaY] == 0 || map[newSantaX][newSantaY] == santa.num) {
                    map[santa.x][santa.y] = 0;
                    setSanta(santa, newSantaX, newSantaY);
                    santaStatus[santa.num - 1] = 3;
                } 
                // 다른 산타가 있다면
                else {
                    map[santa.x][santa.y] = 0;
                    Santa crushSanta = santa;
                    while (true) {
                        // 다음 위치 산타 구하기
                        Santa newSanta = findNextSanta(newSantaX, newSantaY);

                        // 산타 착지 및 맵 변경
                        setSanta(crushSanta, newSantaX, newSantaY);
                        // 산타 기절
                        santaStatus[crushSanta.num - 1] = 3;

                        // 밀려난 산타 다음 위치
                        newSantaX = newSanta.x + xDirection;
                        newSantaY = newSanta.y + yDirection;

                        // 밀려난 위치가 맵 밖이면
                        if (!inMap(newSantaX, newSantaY)) {
                            // 밀려난 산타 탈락 및 맵 변경
                            santaStatus[newSanta.num - 1] = 2;
                            map[newSanta.x][newSanta.y] = 0;
                            break;
                        } 
                        // 밀려난 위치가 맵 안이라면
                        else {
                            // 밀려난 위치에 해당 산타가 있다면?
                            if (map[newSantaX][newSantaY] != 0) {
                                // 충돌산타 바꾸고 반복
                                crushSanta = newSanta;
                                continue;
                            } 
                            // 산타가 없다면?
                            else {
                                // 밀려난 산타 착지 및 맵 변경
                                setSanta(newSanta, newSantaX, newSantaY);
                                break;
                            }
                        }
                    }
                }
            }
        }

    }

    public static int[] getSantaNext(Santa santa) {
        // SantaNext - x, y, distance, type(상 우 좌 하)

        List<SantaNext> list = new ArrayList<>();

        for (int d = 0; d < 4; d++) {
            int nx = santa.x + sdx[d];
            int ny = santa.y + sdy[d];

            // 맵 밖인 경우 이동 불가
            if (!inMap(nx, ny))
                continue;
            // 다른 산타가 있는 경우 이동 불가
            if (map[nx][ny] > 0)
                continue;
            
            // 이동했을 때 멀어지면 이동 불가
            int curDis = getDistance(santa);
            int newDis = getDistance(nx, ny);
            if (curDis < newDis)
                continue;
            
            list.add(new SantaNext(nx, ny, newDis, d));
        }

        if (list.size() == 0) {
            return new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE};
        } else {
            Collections.sort(list);
            SantaNext next = list.get(0);
            return new int[]{next.x, next.y};
        }
    }

    public static void goRudolf() {
        // 가장 가까운 산타 찾기
        Santa nearSanta = getNearSantaList();

        // 가장 가까운 방향 찾기
        int[] nextInfo = getRudolfNext(nearSanta);

        // 이동
        moveRudolf(nextInfo[0], nextInfo[1]);

    }

    public static void moveRudolf(int rnx, int rny) {
        // 충돌 발생 안했을 때
        if (map[rnx][rny] == 0) {
            map[rudolf.x][rudolf.y] = 0;
            map[rnx][rny] = -1;
            rudolf.x = rnx;
            rudolf.y = rny;
        } 
        // 충돌이 발생했을 때
        else {
            int santaNum = map[rnx][rny];
            Santa crushSanta = santaList.get(santaNum - 1);
            System.out.println("루돌프 이동으로 충돌 발생!!");
            System.out.println(crushSanta.num + "번 산타 " + "[ " + crushSanta.x + ", " + crushSanta.y + "] 에서 충돌!!");

            // 산타 점수 추가
            crushSanta.score += C;

            // 날아갈 방향 구하기
            int xDirection = crushSanta.x - rudolf.x;
            int yDirection = crushSanta.y - rudolf.y;

            // System.out.println("xDirection = " + xDirection);
            // System.out.println("yDirection = " + yDirection);

            // 착지할 위치 구하기
            int newSantaX = crushSanta.x + (C * xDirection);
            int newSantaY = crushSanta.y + (C * yDirection);
            
            // System.out.println("newSantaX = " + newSantaX);
            // System.out.println("newSantaY = " + newSantaY);

            // 루돌프 이동
            map[rudolf.x][rudolf.y] = 0;
            // System.out.println("rnx = " + rnx + ", rny = " + rny);
            map[rnx][rny] = -1;
            rudolf.x = rnx;
            rudolf.y = rny;

            // 날아간 위치가 맵 밖이라면
            if (!inMap(newSantaX, newSantaY)) {
                // 해당 산타 탈락 및 맵 변경
                santaStatus[santaNum - 1] = 2;      
            } 
            // 맵 안이라면
            else {
                // 해당 위치에 산타가 없다면
                if (map[newSantaX][newSantaY] == 0 || map[newSantaX][newSantaY] == crushSanta.num) {
                    // 산타 착지 및 맵 변경
                    setSanta(crushSanta, newSantaX, newSantaY);
                    // System.out.println("crushSanta.num = " + crushSanta.num);
                    // 산타 기절
                    santaStatus[crushSanta.num - 1] = 1;
                } 
                // 해당 위치에 산타가 있다면
                else {
                    // System.out.println("연쇄");
                    while (true) {
                        // 다음 위치 산타 구하기
                        Santa newSanta = findNextSanta(newSantaX, newSantaY);

                        // System.out.println("### " + crushSanta.num + " // " + newSantaX + " // " + newSantaY);
                        // 산타 착지 및 맵 변경
                        setSanta(crushSanta, newSantaX, newSantaY);
                        
                        // 산타 기절
                        santaStatus[crushSanta.num - 1] = 1;

                        // 밀려난 산타 다음 위치
                        newSantaX = newSanta.x + xDirection;
                        newSantaY = newSanta.y + yDirection;

                        // 밀려난 위치가 맵 밖이면
                        if (!inMap(newSantaX, newSantaY)) {
                            // 밀려난 산타 탈락 및 맵 변경
                            santaStatus[newSanta.num - 1] = 2;
                            break;
                        } 
                        // 밀려난 위치가 맵 안이라면
                        else {
                            // 밀려난 위치에 해당 산타가 있다면?
                            if (map[newSantaX][newSantaY] != 0) {
                                // 충돌산타 바꾸고 반복
                                crushSanta = newSanta;
                                continue;
                            } 
                            // 산타가 없다면?
                            else {
                                // 밀려난 산타 착지 및 맵 변경
                                setSanta(newSanta, newSantaX, newSantaY);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public static void setSanta(Santa santa, int newSantaX, int newSantaY) {
        santa.x = newSantaX;
        santa.y = newSantaY;
        map[santa.x][santa.y] = santa.num;
    }

    public static Santa findNextSanta(int newSantaX, int newSantaY) {
        int newSantaNum = map[newSantaX][newSantaY];
        Santa newSanta = santaList.get(newSantaNum - 1);
        return newSanta;
    }

    public static int[] getRudolfNext(Santa santa) {
        int[] nextInfo = new int[2];
        int distance = Integer.MAX_VALUE;
        for (int d = 0; d < 8; d++) {
            int nx = rudolf.x + rdx[d];
            int ny= rudolf.y + rdy[d];

            // 맵 밖으로 이동 불가
            if (!inMap(nx, ny))
                continue;

            int distanceFromSanta = getDistance(santa, nx, ny);
            if (distanceFromSanta <= distance) {
                distance = distanceFromSanta;
                nextInfo[0] = nx;
                nextInfo[1] = ny;
            }
        }

        return nextInfo;
    }

    public static Santa getNearSantaList() {
        int distance = Integer.MAX_VALUE;
        int[] distances = new int[P];
        List<Santa> nearSantas = new ArrayList<>();

        // 산타별 거리 저장
        for (int i = 0; i < P; i++) {
            distances[i] = Integer.MAX_VALUE;
        }

        // 거리 최솟값 구하면서 산타별 거리 대입
        for (int i = 0; i < P; i++) {
            // 탈락한 산타는 제외
            if (santaStatus[i] == 2)
                continue;

            // 거리구하기
            int distanceFromSanta = getDistance(santaList.get(i));
            if (distanceFromSanta <= distance) {
                distance = distanceFromSanta;
                distances[i] = distanceFromSanta;
            }
        }

        // 거리가 최소인 산타들 리스트에 추가
        for (int i = 0; i < P; i++) {
            if (distances[i] == distance) {
                nearSantas.add(santaList.get(i));
            }
        }

        Collections.sort(nearSantas, (o1, o2) -> {
            if (o1.x == o2.x) {
                return o2.y - o1.y;
            }
            return o2.x - o1.x;
        });

        // 반환
        return nearSantas.get(0);
    }

    /**
    산타와 루돌프의 거리 구하기
    */
    public static int getDistance(Santa santa) {
        int xGap = Math.abs(rudolf.x - santa.x);
        int yGap = Math.abs(rudolf.y - santa.y);

        return (xGap * xGap) + (yGap * yGap);
    }

    public static int getDistance(Santa santa, int nx, int ny) {
        int xGap = Math.abs(nx - santa.x);
        int yGap = Math.abs(ny - santa.y);

        return (xGap * xGap) + (yGap * yGap);
    }

    // 산타 이동시 루돌프와의 거리 구하기
    public static int getDistance(int nx, int ny) {
        int xGap = Math.abs(rudolf.x - nx);
        int yGap = Math.abs(rudolf.y - ny);

        return (xGap * xGap) + (yGap * yGap);
    }

    public static void setMap() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                map[i][j] = 0;
            }
        }

        for (Santa santa : santaList) {
            map[santa.x][santa.y] = santa.num;
        }

        map[rudolf.x][rudolf.y] = -1;
    }

    public static boolean inMap(int x, int y) {
        return 0 <= x && x < N && 0 <= y && y < N;
    }
}