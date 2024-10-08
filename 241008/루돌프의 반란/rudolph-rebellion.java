import java.util.*;
import java.io.*;

public class Main {

    static class Pair {
        int x, y;

        Pair(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static class Tuple implements Comparable<Tuple> {
        int x, y, distance, id;

        Tuple(int x, int y, int distance, int id) {
            this.x = x;
            this.y = y;
            this.distance = distance;
            this.id = id;
        }

        @Override
        public int compareTo(Tuple o) {
            if (this.distance == o.distance) {
                if (this.x == o.x) {
                    return o.y - this.y;
                }
                return o.x - this.x;
            }
            return this.distance - o.distance;
        }
    }

    static int n, m, p, c, d;
    static int[] points;
    static Map<Integer, Pair> pos = new HashMap<>();
    static Pair rudolf;

    static int[][] map;
    static boolean[] is_live;
    static int[] stun;

    // 상 우 하 좌
    static final int[] dx = {-1, 0, 1, 0};
    static final int[] dy = {0, 1, 0, -1};

    static boolean inMap(int x, int y) {
        return 1 <= x && x <= n && 1 <= y && y <= n;
    }

    public static void main(String[] args) throws IOException {
        // 여기에 코드를 작성해주세요.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;

        st = new StringTokenizer(br.readLine());
        n = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken());
        p = Integer.parseInt(st.nextToken());
        c = Integer.parseInt(st.nextToken());
        d = Integer.parseInt(st.nextToken());

        map = new int[n + 1][n + 1];
        points = new int[p + 1];
        stun = new int[p + 1];
        is_live = new boolean[n + 1];

        st = new StringTokenizer(br.readLine());
        rudolf = new Pair(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));

        // 루돌프의 위치를 보드에 표시
        map[rudolf.x][rudolf.y] = -1;

        for (int i = 1; i <= p; i++) {
            st = new StringTokenizer(br.readLine());
            int id = Integer.parseInt(st.nextToken());
            int x = Integer.parseInt(st.nextToken());
            int y = Integer.parseInt(st.nextToken());

            pos.put(id, new Pair(x, y));

            // 각 산타의 위치를 보드에 표시
            map[x][y] = id;

            // 산타가 살아있는지 여부를 표시
            is_live[id] = true;
        }


        for (int t = 1; t <= m; t++) {

            List<Tuple> list = new ArrayList<>();            

            // 살아있는 산타 중 루돌프에 가장 가까운 산타를 찾습니다.
            for (int i = 1; i <= p; i++) {
                // System.out.println(i + "번 산타 is_live = " + is_live[i]);
                if(!is_live[i]) continue;

                Pair santa = pos.get(i);
                int xGap = Math.abs(rudolf.x - santa.x);
                int yGap = Math.abs(rudolf.y - santa.y);
                int distance = (xGap * xGap) + (yGap * yGap);

                list.add(new Tuple(santa.x, santa.y, distance, i));
            }

            // 가장 가까운 산타의 방향으로 루돌프가 이동합니다.
            if (list.size() != 0) {
                // System.out.println("턴 " + t);
                Collections.sort(list);
                Tuple closestSanta = list.get(0);

                // System.out.println(closestSanta.id + " santa " + closestSanta.x + " santa " + closestSanta.y);
                Pair prevRudolf = new Pair(rudolf.x, rudolf.y);
                int moveX = 0;
                if (closestSanta.x > rudolf.x) moveX = 1;
                else if (closestSanta.x < rudolf.x) moveX = -1; 

                int moveY = 0;
                if (closestSanta.y > rudolf.y) moveY = 1;
                else if (closestSanta.y < rudolf.y) moveY = -1;


                // System.out.println("moveX = " + moveX + ", moveY = " + moveY);
                rudolf.x += moveX;
                rudolf.y += moveY;
                map[prevRudolf.x][prevRudolf.y] = 0;

                // 루돌프의 이동으로 충돌한 경우, 산타를 이동시키고 처리를 합니다.
                if(rudolf.x == closestSanta.x && rudolf.y == closestSanta.y) {
                    int firstX = closestSanta.x + moveX * c;
                    int firstY = closestSanta.y + moveY * c;
                    int lastX = firstX;
                    int lastY = firstY;

                    stun[closestSanta.id] = t + 1;

                    // 만약 이동한 위치에 산타가 있을 경우, 연쇄적으로 이동이 일어납니다.
                    while(inMap(lastX, lastY) && map[lastX][lastY] > 0) {
                        lastX += moveX;
                        lastY += moveY;
                    }

                    // 연쇄적으로 충돌이 일어난 가장 마지막 위치에서 시작해,
                    // 순차적으로 보드판에 있는 산타를 한칸씩 이동시킵니다.
                    while(!(lastX == firstX && lastY == firstY)) {
                        int beforeX = lastX - moveX;
                        int beforeY = lastY - moveY;

                        if (!inMap(beforeX, beforeY)) break;

                        int idx = map[beforeX][beforeY];

                        if (!inMap(lastX, lastY)) {
                            is_live[idx] = false;
                        } else {
                            map[lastX][lastY] = map[beforeX][beforeY];
                            pos.put(idx, new Pair(lastX, lastY));
                        }

                        lastX = beforeX;
                        lastY = beforeY;
                    }

                    points[closestSanta.id] += c;
                    pos.put(closestSanta.id, new Pair(firstX, firstY));
                    if (inMap(firstX, firstY)) {
                        map[firstX][firstY] = closestSanta.id;
                    } else {
                        is_live[closestSanta.id] = false;
                    }
                }
            }

            map[rudolf.x][rudolf.y] = -1;
            // System.out.println(rudolf.x + " // " + rudolf.y + " // " + map[rudolf.x][rudolf.y]);

            // 각 산타들은 루돌프와 가장 가까운 방향으로 한칸 이동합니다.
            for (int i = 1; i <= p; i++) {
                if (!is_live[i] || stun[i] >= t) continue;

                int minDistance = (pos.get(i).x - rudolf.x) * ((pos.get(i).x - rudolf.x)) + (pos.get(i).y - rudolf.y) * (pos.get(i).y - rudolf.y);
                int moveDir = -1;

                for (int dir = 0; dir < 4; dir++) {
                    int nx = pos.get(i).x + dx[dir];
                    int ny = pos.get(i).y + dy[dir];
                    
                    // 해당 방향이 맵 밖이거나 산타가 있을 경우 이동 불가
                    if (!inMap(nx, ny) || map[nx][ny] > 0) continue;

                    int distance = (nx - rudolf.x) * (nx - rudolf.x) + (ny - rudolf.y) * (ny - rudolf.y);
                    if (distance < minDistance) {
                        minDistance = distance;
                        moveDir = dir;
                    }
                }
                
                //이동 가능할 경우
                if (moveDir != -1) {
                    int nx = pos.get(i).x + dx[moveDir];
                    int ny = pos.get(i).y + dy[moveDir];

                    // 산타의 이동으로 충돌한 경우, 산타를 이동시키고 처리를 합니다.
                    if (nx == rudolf.x && ny == rudolf.y) {
                        stun[i] = t + 1;

                        int moveX = -dx[moveDir];
                        int moveY = -dy[moveDir];

                        int firstX = nx + moveX * d;
                        int firstY = ny + moveY * d;
                        int lastX = firstX;
                        int lastY = firstY;

                        if (d == 1) {
                            points[i] += d;
                        } else {

                            // 만약 이동한 위치에 산타가 있을 경우, 연쇄적으로 이동이 일어납니다.
                            while(inMap(lastX, lastY) && map[lastX][lastY] > 0) {
                                lastX += moveX;
                                lastY += moveY;
                            }

                            // 마지막 위치에서 시작해,
                            // 순차적으로 보드판에 있는 산타를 한칸씩 이동시킵니다.
                            while(!(lastX == firstX && lastY == firstY)) {
                                int beforeX = lastX - moveX;
                                int beforeY = lastY - moveY;

                                if (!inMap(beforeX, beforeY)) break;

                                int idx = map[beforeX][beforeY];

                                if (!inMap(lastX, lastY)) {
                                    is_live[idx] = false;
                                } else {
                                    map[lastX][lastY] = map[beforeX][beforeY];
                                    pos.put(idx, new Pair(lastX, lastY));
                                }

                                lastX = beforeX;
                                lastY = beforeY;
                            }

                            points[i] += d;
                            map[pos.get(i).x][pos.get(i).y] = 0;
                            pos.put(i, new Pair(firstX, firstY));
                            if (inMap(firstX, firstY)) {
                                map[firstX][firstY] = i;
                            } else {
                                is_live[i] = false;
                            }
                        }
                    } else {
                        map[pos.get(i).x][pos.get(i).y] = 0;
                        pos.put(i, new Pair(nx, ny));
                        map[nx][ny] = i;
                    }
                }
            }

            for (int i = 1; i <= p; i++) {
                if (is_live[i]) points[i]++;
            }

            // System.out.println("AFTER TURN " + t);
            // for (int i = 1; i <= n; i++) {
            //     for (int j = 1; j <= n; j++) {
            //         System.out.print(map[i][j] + " ");
            //     }
            //     System.out.println();
            // }
            // System.out.println("SCORE : ");
            // for (int i = 1; i <= p; i++) {
            //     System.out.print(points[i] + " ");
            // }
            // System.out.println();
        }

        for (int i = 1; i <= p; i++) {
            System.out.print(points[i] + " ");
        }
    }
}