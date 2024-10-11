import java.util.*;
import java.io.*;

public class Main {
	
	static int l, n, q;
	static int r, c, h, w, k;
	static int[][] map;
	static boolean[] isLive;
	static int[][] nightMap;
	static int answer = 0;
	static Knight[] arr;
	static boolean[] isPushed;
	
	// 위 오 아래 왼
	static int[] dx = {-1, 0, 1, 0};
	static int[] dy = {0, 1, 0, -1};
	
	static class Knight {
		int x1, y1, x2, y2, h, w;
		int hp, damage;
		
		public Knight(int x, int y, int h, int w, int k) {
			this.x1 = x;
			this.y1 = y;
			this.h = h;
			this.w = w;
			this.x2 = x + h;
			this.y2 = y + w;
			this.hp = k;
			this.damage = 0;
		}
	}
	
	static boolean inMap(int x, int y) {
		return 1 <= x && x <= n && 0 <= y && y <= n;
	}
	
	public static void main(String[] args) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st;
		
		st = new StringTokenizer(br.readLine());
		l = Integer.parseInt(st.nextToken());
		n = Integer.parseInt(st.nextToken());
		q = Integer.parseInt(st.nextToken());
		arr = new Knight[n + 1];
		map = new int[l + 1][l + 1];
		nightMap = new int[l + 1][l + 1];
		isLive = new boolean[n + 1];
		
		for (int i = 1; i <= l; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 1; j <= l; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		
		for (int i = 1; i <= n; i++) {
			st = new StringTokenizer(br.readLine());
			r = Integer.parseInt(st.nextToken());
			c = Integer.parseInt(st.nextToken());
			h = Integer.parseInt(st.nextToken()) - 1;
			w = Integer.parseInt(st.nextToken()) - 1;
			k = Integer.parseInt(st.nextToken());
			arr[i] = new Knight(r, c, h, w, k);
			isLive[i] = true;
			int x1 = arr[i].x1; int y1 = arr[i].y1;
			int x2 = arr[i].x2; int y2 = arr[i].y2;
			
			for (int r = x1; r <= x2; r++) {
				for (int c = y1; c <= y2; c++) {
					nightMap[r][c] = i;
				}
			}
		}
		
		for (int t = 0; t < q; t++) {
			st = new StringTokenizer(br.readLine());
			int i = Integer.parseInt(st.nextToken());
			int d = Integer.parseInt(st.nextToken());
			
			if (!isLive[i]) continue;
			
			isPushed = new boolean[n + 1];
			
			// 움직일 수 있다면
			if (movePossible(i, d)) {
				
				// 움직이기
				moveKnight(i, d);
				isPushed[i] = false;
				checkDamage();
			}
			
		}
		
		for (int a = 1; a <= n; a++) {
			if (!isLive[a]) continue;
			answer += arr[a].damage;
		}
		System.out.println(answer);
		
	}
	
	static void checkDamage() {
		for (int j = 1; j <= n; j++) {
			if (!isPushed[j]) continue; // 밀리지 않은 기사는 continue
			
			// 밀려난 기사는 데미지 계산
			Knight knight = arr[j];
			int damage = 0;
			for (int x = knight.x1; x <= knight.x2; x++) {
				for (int y = knight.y1; y <= knight.y2; y++) {
					if (map[x][y] == 1) damage++;
				}
			}
			
			knight.hp -= damage;
			knight.damage += damage;
			
			if (knight.hp <= 0) {
				isLive[j] = false;
				for (int x = knight.x1; x <= knight.x2; x++) {
					for (int y = knight.y1; y <= knight.y2; y++) {
						nightMap[x][y] = 0;
					}
				}
			}
		}
	}
	
	static void moveKnight(int i, int d) {
		Knight knight = arr[i];
		for (int x = knight.x1; x <= knight.x2; x++) {
			for (int y = knight.y1; y <= knight.y2; y++) {
				int nx = x + dx[d];
				int ny = y + dy[d];
				
				if (nightMap[nx][ny] == 0 || nightMap[nx][ny] == i) continue;
				
				// 다른 기사 있으면 다른 기사도 움직여주기.
				moveKnight(nightMap[nx][ny], d);
			}
		}
		
		if (d == 0) {
			moveUp(i, d);
		} else if (d == 1) {
			moveRight(i, d);
		} else if (d == 2) {
			moveDown(i, d);
		} else {
			moveLeft(i, d);
		}
		
		knight.x1 += dx[d];
		knight.x2 += dx[d];
		knight.y1 += dy[d];
		knight.y2 += dy[d];
		isPushed[i] = true;
	}
	
	static void moveUp(int i, int d) {
		Knight knight = arr[i];
		
		for (int x = knight.x1; x <= knight.x2; x++) {
			for (int y = knight.y1; y <= knight.y2; y++) {
				int nx = x + dx[d];
				int ny = y + dy[d];
				
				nightMap[x][y] = 0;
				nightMap[nx][ny] = i;
			}
		}
	}
	
	static void moveDown(int i, int d) {
		Knight knight = arr[i];
		for (int x = knight.x2; x >= knight.x1; x--) {
			for (int y = knight.y1; y <= knight.y2; y++) {
				int nx = x + dx[d];
				int ny = y + dy[d];
				
				nightMap[x][y] = 0;
				nightMap[nx][ny] = i;
			}
		}
	}
	
	static void moveRight(int i, int d) {
		Knight knight = arr[i];
		for (int x = knight.x1; x <= knight.x2; x++) {
			for (int y = knight.y2; y >= knight.y1; y--) {
				int nx = x + dx[d];
				int ny = y + dy[d];
				
				nightMap[x][y] = 0;
				nightMap[nx][ny] = i;
			}
		}
	}
	
	static void moveLeft(int i, int d) {
		Knight knight = arr[i];
		for (int x = knight.x1; x <= knight.x2; x++) {
			for (int y = knight.y1; y <= knight.y2; y++) {
				int nx = x + dx[d];
				int ny = y + dy[d];
				
				nightMap[x][y] = 0;
				nightMap[nx][ny] = i;
			}
		}
	}
	
	static boolean movePossible(int i, int d) {
		for (int x = arr[i].x1; x <= arr[i].x2; x++) {
			for (int y = arr[i].y1; y <= arr[i].y2; y++) {
				int nx = x + dx[d];
				int ny = y + dy[d];
				
				// 가려는 방향에 벽이 있다면 false
				if (map[nx][ny] == 2) return false;
				
				// 가려는 방향이 빈 칸이거나 자기 자신인 경우 continue
				if (nightMap[nx][ny] == 0 || nightMap[nx][ny] == i) continue;
				
				// 밀려날 칸에 다른 기사가 있다면 해당 기사 움직일 수 있는지 판단.
				// 해당 기사가 밀려날 수 있으면 밀릴 수 있는거여.
				if (!movePossible(nightMap[nx][ny], d)) return false;
			}
		}
		
		return true;
	}
}