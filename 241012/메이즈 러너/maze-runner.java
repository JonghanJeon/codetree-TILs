import java.util.*;
import java.io.*;

public class Main {
	
	// N: 맵 크기
	// M: 러너 수
	// K: 턴 수
	static int N, M, K;
	static int[][] map;
	static boolean[][] exitMap;
	static Pair exit;
	static Pair[] players;
	static int[] dist;
	static boolean[] isEscape;
	static int playerNum;
	
	static int[] dx = {-1, 1, 0, 0};
	static int[] dy = {0, 0, 1, -1};
	
	static class Pair {
		int x, y;
		
		Pair(int a, int b) {
			x = a; y = b;
		}
	}
	
	static boolean inMap(int x, int y) {
		return 1 <= x && x <= N && 1 <= y && y <= N;
	}
	
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st;
		
		st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		
		map = new int[N + 1][N + 1];
		for (int i = 1; i <= N; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 1; j <= N; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		
		players = new Pair[M + 1];
		for (int i = 1; i <= M; i++) {
			st = new StringTokenizer(br.readLine());
			int x = Integer.parseInt(st.nextToken());
			int y = Integer.parseInt(st.nextToken());
			players[i] = new Pair(x, y);
		}
		
		st = new StringTokenizer(br.readLine());
		exit = new Pair(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
		exitMap = new boolean[N + 1][N + 1];
		exitMap[exit.x][exit.y] = true;
		
		dist = new int[M + 1];
		isEscape = new boolean[M + 1];
		playerNum = M;
		
		for (int t = 0; t < K; t++) {
			if(playerNum == 0) break;
			
			movePlayer();
			
			if(playerNum == 0) break;
			
			int[] info = findSquare();
			int squareX = info[0];
			int squareY = info[1];
			int squareLeng = info[2];
			
			rotateMaze(squareX, squareY, squareLeng);

		}
		
		int answer = 0;
		for (int val : dist) {
			answer += val;
		}
		
		System.out.println(answer);
        System.out.println(exit.x + " " + exit.y);
	}
	
	static void rotateMaze(int baseX, int baseY, int leng) {
		rotateWall(baseX, baseY, leng);
		
		rotatePlayer(baseX, baseY, leng);
		
		rotateExit(baseX, baseY, leng);
	}
	
	static void rotateExit(int startX, int startY, int leng) {
		int oldX = exit.x; int oldY = exit.y;
		int newX = (oldY - startY) + startX;
		int newY = (leng - 1) - (oldX - startX) + startY;
		exit.x = newX; exit.y = newY;
	}
	
	static void rotatePlayer(int baseX, int baseY, int leng) {
		Pair[] tmp = new Pair[M + 1];
		
		for (int i = 1; i <= M; i++) {
			if (isEscape[i]) continue;
			
			Pair player = players[i];
			
			int ox = player.x; int oy= player.y;
			
			if (baseX <= ox && ox < baseX + leng && baseY <= oy && oy < baseY + leng) {
				int nx = (oy - baseY) + baseX;
				int ny = (leng - 1) - (ox - baseX) + baseY;
				
				tmp[i] = new Pair(nx, ny);
			} else {
				tmp[i] = player;
			}
		}
		
		for (int i = 1; i <= M; i++) {
			players[i] = tmp[i];
		}
	}
	
	static void rotateWall(int baseX, int baseY, int leng) {
		int[][] tmp = new int[leng][leng];
		int[][] tmp2 = new int[leng][leng];
		
		for (int x = baseX; x < baseX + leng; x++) {
			for (int y = baseY; y < baseY + leng; y++) {
				tmp[x - baseX][y - baseY] = map[x][y]; 
			}
		}
		
		for (int x = 0; x < leng; x++) {
			for (int y = 0; y < leng; y++) {
				tmp2[x][y] = tmp[leng - 1 - y][x];
			}
		}
		
		for (int x = baseX; x < baseX + leng; x++) {
			for (int y = baseY; y < baseY + leng; y++) {
				map[x][y] = tmp2[x - baseX][y - baseY];
			}
		}
		
		for (int x = baseX; x < baseX + leng; x++) {
			for (int y = baseY; y < baseY + leng; y++) {
				if (map[x][y] > 0) map[x][y] -= 1;
			}
		}
	}
	
	static int[] findSquare() {
		// 0 = x
		// 1 = y
		// 2 = leng
		List<int[]> list = new ArrayList<int[]>();
		for (int i = 1; i <= M; i++) {
			if (isEscape[i]) continue;
			
			Pair player = players[i];

			int gx = Math.abs(player.x - exit.x) + 1;
			int gy = Math.abs(player.y - exit.y) + 1;
			
			int leng = Math.max(gx, gy);
			
			int mx = Math.max(player.x, exit.x);
			int my = Math.max(player.y, exit.y);
			
			// 좌상 찾기
			int[] findInfo = findLoca(leng, mx, my);
			
			list.add(findInfo);
		}
		
		Collections.sort(list, (o1, o2) -> {
			if (o1[2] == o2[2]) {
				if (o1[0] == o1[0]) {
					return o1[1] - o2[1];
				}
				return o1[0] - o2[0];
			}
			return o1[2] - o2[2];
		});
		
		return list.get(0);
	}
	
	static int[] findLoca(int leng, int mx, int my) {
		for (int i = mx - leng + 1; i <= mx; i++) {
            for (int j = my - leng + 1; j <= my; j++) {
                if (!inMap(i, j)) continue;
                return new int[] {i, j, leng};
            }
		}
		return null;
	}
	
	
	static void movePlayer() {
		for (int i = 1; i <= M; i++) {
			if (isEscape[i]) continue;
			
			Pair player = players[i];
			int base = getDistance(player.x, player.y);
			
			for (int d = 0; d < 4; d++) {
				int nx = player.x + dx[d];
				int ny = player.y + dy[d];
				
				if (!inMap(nx, ny)) continue;
				
				int value = getDistance(nx, ny);
				// 더 먼거리는 이동할 수 없다.
				if (base < value) continue;
				// 벽이 있다면 이동할 수 없다.
				if (map[nx][ny] > 0) continue;
				
				player.x = nx;
				player.y = ny;
				dist[i]++;
				
				if (player.x == exit.x && player.y == exit.y) {
					isEscape[i] = true;
					playerNum--;
				}
				
				break;
			}
		}
	}
	
	static int getDistance(int x, int y) {
		return Math.abs(exit.x - x) + Math.abs(exit.y - y);
	}
}