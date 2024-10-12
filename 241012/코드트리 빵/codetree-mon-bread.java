import java.util.*;
import java.io.*;

public class Main {
	
	// 격자의 크기 n
	// 사람 수 m
	static int n, m;
	static int[][] map;
	static Pair[] people;
	static boolean[] isArrive;
	
	// 상/좌/우/하
	static int[] dx = {-1, 0, 0, 1};
	static int[] dy= {0, -1, 1, 0};
	
	static class Loca {
		int x, y;
		
		Loca(int x, int y) {
			this.x = x; this.y = y;
		}
	}
	
	static class Pair {
		int x, y;
		int tx, ty;
		boolean inMap;
		
		Pair(int tx, int ty) {
			this.tx = tx; this.ty = ty; this.inMap = false;
		}
	}
	
	static boolean inMap(int x, int y) {
		return 0 <= x && x < n && 0 <= y && y < n;
	}
	
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st;
		
		st = new StringTokenizer(br.readLine());
		n = Integer.parseInt(st.nextToken());
		m = Integer.parseInt(st.nextToken());
		map = new int[n][n];
		people = new Pair[m + 1];
		isArrive = new boolean[m + 1];
		
		for (int i = 0; i < n; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 0; j < n; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		
		for (int i = 1; i <= m; i++) {
			st = new StringTokenizer(br.readLine());
			people[i] = new Pair(Integer.parseInt(st.nextToken()) - 1, Integer.parseInt(st.nextToken()) - 1);
		}
		
		int t = 0;
		while(true) {
			t += 1;
			// System.out.println("TURN " + t);
			if (t <= m) {
				int[] baseCamp = findBaseCamp(t);
				people[t].x = baseCamp[0];
				people[t].y = baseCamp[1];
				people[t].inMap = true;
				// 사람들 이동 후 해당 베이스캠프 이동 불가 칸으로 만듬
				movePeople(t);
				map[baseCamp[0]][baseCamp[1]] = -1;
			} else {
                // 사람들 이동
			    movePeople(0);
            }
			
			// 도착한 사람 체크
			for (int i = 1; i <= m; i++) {
				// 도착했다면
				if (people[i].x == people[i].tx && people[i].y == people[i].ty) {
					// 도착 기록
					isArrive[i] = true;
					// 이동 불가 칸으로 만듬.
					map[people[i].tx][people[i].ty] = -1;
				}
			}

            // for (int i = 1; i <= m; i++) {
            //     System.out.println("people[i].x = " + people[i].x + ", people[i].y = " + people[i].y);
            // }
			
			boolean isDone = true;
			for (int i = 1; i <= m; i++) {
                // System.out.print(isArrive[i] + " ");
				if (!isArrive[i]) isDone = false;
			}
            // System.out.println();
			
			if (isDone) break;
		}
		
		System.out.println(t);
 	}
	
	static void movePeople(int t) {
        // System.out.println("movePeople t = " + t);
		for (int i = 1; i <= m; i++) {
			if (i == t) continue;
			// 격자 위에 없는 사람은 움직이지 않음.
			if (!people[i].inMap) continue;
			// 도착한 사람은 움직이지 않음.
			if (isArrive[i]) continue;
			
			int dir = bfs(i);
			people[i].x += dx[dir];
			people[i].y += dy[dir];
			
			
		}
	}
	
	static int bfs(int i) {
		int sx = people[i].x; int sy = people[i].y;
		Queue<int[]> q = new LinkedList<int[]>();
		q.add(new int[] {sx, sy, -1});
		boolean[][] visited = new boolean[n][n];
		visited[sx][sy] = true;
		
		while(!q.isEmpty()) {
			int[] cur = q.poll();
			if (cur[0] == people[i].tx && cur[1] == people[i].ty) return cur[2];
			
			for (int d = 0; d < 4; d++) {
				int nx = cur[0] + dx[d];
				int ny= cur[1] + dy[d];
				
				if (!inMap(nx, ny)) continue;
				if (map[nx][ny] == -1) continue;
				
				visited[nx][ny] = true;
				// 처음 방향만 넘겨주어야 해서 -1 일때 (처음일때)만 방향 넘겨주고 그 이후로는 같은 방향 넘겨줌.
				if (cur[2] == -1) {
					q.add(new int[] {nx, ny, d});
				} else {
					q.add(new int[] {nx, ny, cur[2]});
				}
			}
		}
		return -1;
	}
	
	static int[] findBaseCamp(int t) {
		int tx = people[t].tx; int ty = people[t].ty;
		int bx = 0; int by = 0;
		int dist = Integer.MAX_VALUE;
		
		for (int x = 0; x < n; x++) {
			for (int y = 0; y < n; y++) {
				// 베이스캠프가 아니거나, 갈 수 없는 베이스 캠프일 경우
				if (map[x][y] == 0 || map[x][y] == -1)	continue;
				
				int value = Math.abs(tx - x) + Math.abs(ty - y);
				
				if (value < dist) {
					bx = x; by = y; dist = value;
					continue;
				} else if (value > dist) continue;
				
				if (x < bx) {
					bx = x; by = y;
					continue;
				} else if (x > bx) continue;
				
				if (y < by) {
					bx = x; by = y;
				}
			}
		}
		
		return new int[] {bx, by};
	}
}