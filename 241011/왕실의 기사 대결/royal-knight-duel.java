import java.util.*;
import java.io.*;

public class Main {

	// L: 격자 사이즈
	// N: 기사 사이즈
	// Q: 명령 사이즈
	static int L, N, Q;
	static int[][] map;
	static Knight[] arr;
	static int[][] knightMap;
	static boolean[] isLive;
	static boolean[] isPushed;
	
	// 위 오른쪽 아래 왼쪽
	static int[] dx = {-1, 0, 1, 0};
	static int[] dy = {0, 1, 0, -1};
	
	static class Knight {
		int x1, x2, y1, y2;
		int hp, damage;
		
		public Knight(int r, int c, int h, int w, int k) {
			x1 = r; y1 = c;
			x2 = r + h - 1; y2 = c + w - 1;
			hp = k; damage = 0;
		}
	}
	
	static boolean inMap(int x, int y) {
		return 1 <= x && x <= L && 1 <= y && y <= L;
	}
	
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st;
		
		st = new StringTokenizer(br.readLine());
		L = Integer.parseInt(st.nextToken());
		N = Integer.parseInt(st.nextToken());
		Q = Integer.parseInt(st.nextToken());
		arr = new Knight[N + 1];
		map = new int[L + 1][L + 1];
		knightMap = new int[L + 1][L + 1];
		isLive = new boolean[N + 1];
		
		for (int i = 1; i <= L; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 1; j <= L; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		
		for (int i = 1; i <= N; i++) {
			st = new StringTokenizer(br.readLine());
			int r = Integer.parseInt(st.nextToken());
			int c = Integer.parseInt(st.nextToken());
			int h = Integer.parseInt(st.nextToken());
			int w = Integer.parseInt(st.nextToken());
			int k = Integer.parseInt(st.nextToken());
			arr[i] = new Knight(r, c, h, w, k);
			for (int x = arr[i].x1; x <= arr[i].x2; x++) {
				for (int y = arr[i].y1; y <= arr[i].y2; y++) {
					knightMap[x][y] = i;
				}
			}
			isLive[i] = true; 
		}
		
		for (int t = 1; t <= Q; t++) {
//			System.out.println("START TURN " + t);
			st = new StringTokenizer(br.readLine());
			int i = Integer.parseInt(st.nextToken());
			int d = Integer.parseInt(st.nextToken());
			
			isPushed = new boolean[N + 1];
			
			// 살아있지 않은 기사는 skip
			if (!isLive[i]) continue;
			
			if (movePossible(i, d)) {
//				System.out.println("MOVE !!!");
				moveKnight(i, d);
				isPushed[i] = false;
				checkDamage();
			}
			
//			System.out.println("AFTER TURN " + t);
//			for (int a = 1; a <= N; a++) {
//				Knight knight = arr[a];
//				System.out.println(knight.x1 + " // " + knight.y1);
//			}
		}
		
		int answer = 0;
		for (int i = 1; i <= N; i++) {
			if (!isLive[i]) continue;
			
			answer += arr[i].damage;
		}
		
		System.out.println(answer);
	}
	
	static void checkDamage() {
		for (int i = 1; i <= N; i++) {
			if (!isLive[i]) continue;
			if (!isPushed[i]) continue;
			
			Knight knight = arr[i];
			int damage = 0;
			for (int x = knight.x1; x <= knight.x2; x++) {
				for (int y = knight.y1; y <= knight.y2; y++) {
					if (map[x][y] == 1) {
						damage++;
					}
				}
			}
			
			knight.hp -= damage;
			knight.damage += damage;
			
			if (knight.hp <= 0) {
				isLive[i] = false;
				for (int x = knight.x1; x <= knight.x2; x++) {
					for (int y = knight.y1; y <= knight.y2; y++) {
						knightMap[x][y] = 0;
					}
				}
			}
		}
	}
	
	static void moveKnight(int i, int d) {
        for (int r = arr[i].x1; r <= arr[i].x2; r++) {
            for (int c = arr[i].y1; c <= arr[i].y2; c++) {
                int nr = r + dx[d];
                int nc = c + dy[d];

                int newId = knightMap[nr][nc];

                if (newId == 0 || newId == i) {
                    continue;
                }

                // 이동하려는 위치에 다른 기사가 있다면 그 기사도 함께 연쇄적으로 한 칸 밀려난다.
                moveKnight(newId, d);
            }
        }

        switch (d) {
            case 0:
                moveUp(i, d);
                break;
            case 1:
                moveRight(i, d);
                break;
            case 2:
                moveDown(i, d);
                break;
            case 3:
                moveLeft(i, d);
                break;
        }

        isPushed[i] = true;
        arr[i].x1 += dx[d]; arr[i].x2 += dx[d];
        arr[i].y1 += dy[d]; arr[i].y2 += dy[d];
    }
	
	static void moveUp(int id, int d) {
		Knight knight = arr[id];
		for (int x = knight.x1; x <= knight.x2; x++) {
			for (int y = knight.y1; y <= knight.y2; y++) {
				int nx = x + dx[d];
				int ny = y + dy[d];
				
				knightMap[x][y] = 0;
				knightMap[nx][ny] = id;
			}
		}
	}

	static void moveDown(int id, int d) {
		Knight knight = arr[id];
		for (int x = knight.x2; x >= knight.x1; x--) {
			for (int y = knight.y1; y <= knight.y2; y++) {
				int nx = x + dx[d];
				int ny = y + dy[d];
				
				knightMap[x][y] = 0;
				knightMap[nx][ny] = id;
			}
		}
	}
	
	static void moveRight(int id, int d) {
		Knight knight = arr[id];
		for (int x = knight.x1; x <= knight.x2; x++) {
			for (int y = knight.y2; y >= knight.y1; y--) {
				int nx = x + dx[d];
				int ny = y + dy[d];
				
				knightMap[x][y] = 0;
				knightMap[nx][ny] = id;
			}
		}
	}
	
	static void moveLeft(int id, int d) {
		Knight knight = arr[id];
		for (int x = knight.x1; x <= knight.x2; x++) {
			for (int y = knight.y1; y <= knight.y2; y++) {
				int nx = x + dx[d];
				int ny = y + dy[d];
				
				knightMap[x][y] = 0;
				knightMap[nx][ny] = id;
			}
		}
	}
	
	static boolean movePossible(int i, int d) {
		Queue<Integer> q = new LinkedList<Integer>();
		q.add(i);
		boolean[] moved = new boolean[N + 1];
		
		while (!q.isEmpty()) {
			int id = q.poll();
//			System.out.println("id = " + id);
			moved[id] = true;
			Knight knight = arr[id];
			
			for (int x = knight.x1; x <= knight.x2; x++) {
				for (int y = knight.y1; y <= knight.y2; y++) {
					int nx = x + dx[d];
					int ny = y + dy[d];
					
					// 벽이라면 false
					if (!inMap(nx, ny)) return false;
					if (map[nx][ny] == 2) return false;
					// 이미 움직인 기사라면 continue
					if (moved[knightMap[nx][ny]]) continue;
					// 기사가 있고 해당 기사가 본인이 아닌경우
					if (knightMap[nx][ny] > 0 && knightMap[nx][ny] != id) {
						q.add(knightMap[nx][ny]);
						moved[knightMap[nx][ny]] = true;
					}
				}
			}
		}
		
		return true;
	}
}