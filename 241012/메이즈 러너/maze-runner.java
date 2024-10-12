import java.util.*;
import java.io.*;

public class Main {
	
	// N: 미로 크기
	// M: 참가자 수
	// K: 게임 시간
	static int N, M, K;
	static int[][] map;
	static int[][] runMap;
	static boolean[] isEscape;
	static Pair[] runners;
	static Pair exit;
	
	// 상 하 좌 우
	static int[] dx = {-1, 1, 0, 0};
	static int[] dy = {0, 0, -1, 1};
	
	static class Pair {
		int x, y, id, score;
		
		public Pair(int x, int y, int id) {
			this.x = x; 
			this.y = y;
			this.id = id;
			this.score = 0;
		}
	}
	
	static class Square implements Comparable<Square>{
		int x, y, leng;
		
		public Square (int x, int y, int leng) {
			this.x = x;
			this.y = y;
			this.leng = leng;
		}

		@Override
		public int compareTo(Main.Square o) {
			if (this.leng == o.leng) {
				if (this.x == o.x) {
					return this.y - o.y;
				}
				return this.x - o.x;
			}
			return this.leng - o.leng;
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
		// 0이면 빈칸, 1이상 9이하이면 벽 (해당 벽의 내구도)
		map = new int[N + 1][N + 1];
		runMap = new int[N + 1][N + 1];
		runners = new Pair[M + 1];
		isEscape = new boolean[M + 1];
		
		for (int i = 1; i <= N; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 1; j <= N; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		
		for (int i = 1; i <= M; i++) {
			st = new StringTokenizer(br.readLine());
			int x = Integer.parseInt(st.nextToken());
			int y = Integer.parseInt(st.nextToken());
			runners[i] = new Pair(x, y, i);
			runMap[x][y] = i;
		}
		
		st = new StringTokenizer(br.readLine());
		exit = new Pair(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), -1);
		runMap[exit.x][exit.y] = -1;
		
		for (int t = 1; t <= K; t++) {
            System.out.println("START TURN " + t);

			System.out.println("EXIT x = " + exit.x + ", y = " + exit.y);
			System.out.println("LOAD MAP");
			for (int i = 1; i <= N; i++) {
				for (int j = 1; j <= N; j++) {
					System.out.print(map[i][j]);
				}
				System.out.println();
			}
			
			int runnerCnt = 0;
			for (int i = 1; i <= M; i++) {
				if (isEscape[i]) runnerCnt++;
				System.out.println("runner.x = " + runners[i].x + ", runner.y = " + runners[i].y);
			}
			if (runnerCnt == M) break;
			
			// 참가자가 한 칸씩 움직임
			moveRunner();

			System.out.println("AFTER RUNNER MOVE");
			for (int i = 1; i <= M; i++) {
				if (isEscape[i]) runnerCnt++;
				System.out.println("runner.x = " + runners[i].x + ", runner.y = " + runners[i].y);
			}
			
			// 미로가 회전함 (회전한 미로는 내구도 1 차감)
			// 한 명 이상의 참가자와 출구를 포함한 가장 작은 정사각형을 찾음
			// 가장 작은 정사각형이 2개 이상이라면, 
			// 좌상단 좌표 기준 r 오름차순, c 오름차순
			Square square = findSquare();
			System.out.println("SQURE INFO");
			System.out.println("square.x = " + square.x + ", square.y = " + square.y + ", square.leng = " + square.leng);
			
			// 해당 정사각형 시계방향으로 90도 회전.
			// 회전된 벽은 내구도 1씩 차감
			// 내구도가 0이되면 사라짐.
			rotateSquare(square);
			rotateRunMap(square);
		}
		
		int answer = 0;
		for (Pair runner : runners) {
			answer += runner.score;
		}
		
		System.out.println(answer);
	}
	
	static void rotateSquare(Square square) {
		int sx = square.x; int sy = square.y; int leng = square.leng;
		
		int[][] tmp = new int[leng][leng];
		int[][] tmp2 = new int[leng][leng];
		
		for (int x = sx; x < sx + leng; x++) {
			for (int y = sy; y < sy + leng; y++) {
                System.out.println("x = " + x);
                System.out.println("y = " + y);
                System.out.println("x - sx = " + (x - sx));
                System.out.println("y - sy = " + (y - sy));
				tmp[x - sx][y - sy] = map[x][y]; 
			}
		}
		
		for (int x = 0; x < leng; x++) {
			for (int y = 0; y < leng; y++) {
				tmp2[x][y] = tmp[leng - 1 - y][x];
			}
		}
		
		for (int x = sx; x < sx + leng; x++) {
			for (int y = sy; y < sy + leng; y++) {
				map[x][y] = tmp2[x - sx][y - sy];
			}
		}
		
		for (int x = sx; x < sx + leng; x++) {
			for (int y = sy; y < sy + leng; y++) {
				if (map[x][y] > 0) map[x][y] -= 1;
			}
		}
	}
	
	static void rotateRunMap(Square square) {
		int sx = square.x; int sy = square.y; int leng = square.leng;

		int[][] tmp = new int[leng][leng];
		int[][] tmp2 = new int[leng][leng];
		
		for (int x = sx; x < sx + leng; x++) {
			for (int y = sy; y < sy + leng; y++) {
				tmp[x - sx][y - sy] = runMap[x][y]; 
			}
		}
		
		for (int x = 0; x < leng; x++) {
			for (int y = 0; y < leng; y++) {
				tmp2[x][y] = tmp[leng - 1 - y][x];
			}
		}
		
		for (int x = sx; x < sx + leng; x++) {
			for (int y = sy; y < sy + leng; y++) {
				runMap[x][y] = tmp2[x - sx][y - sy];
			}
		}
		
		for (int x = sx; x < sx + leng; x++) {
			for (int y = sy; y < sy + leng; y++) {
				if (runMap[x][y] == 0) continue;
				if (runMap[x][y] > 0) {
					int id = runMap[x][y];
					runners[id].x = x;
					runners[id].y = y;
				}
				if (runMap[x][y] == -1) {
					exit.x = x;
					exit.y = y;
				}
			}
		}
	}
	
	static Square findSquare() {
        System.out.println("findSquare");
		List<Square> list = new ArrayList<Main.Square>();
		for (int i = 1; i <= M; i++) {
			if (isEscape[i]) continue;
			
			Pair runner = runners[i];
			
			System.out.println("exit = " + exit.x + " // " + exit.y);
			System.out.println("runner = " + runner.x + " // " + runner.y);

			int gx = Math.abs(runner.x - exit.x) + 1;
			int gy = Math.abs(runner.y - exit.y) + 1;

			System.out.println("gx = " + gx + ", gy = " + gy);
			
			int leng = Math.max(gx, gy);
			
			int mx = Math.max(runner.x, exit.x);
			int my = Math.max(runner.y, exit.y);
            System.out.println("mx = " + mx + ", my = " + my + ", leng = " + leng);
			
			// 좌상 찾기
			int[] findInfo = findLoca(leng, mx, my);
			
			Square square = new Square(findInfo[0], findInfo[1], leng);
			System.out.println("square.x = " + square.x + ", square.y = " + square.y + ", square.leng = " + square.leng);
            list.add(square);
		}
		Collections.sort(list);
		return list.get(0);
		
	}
	
	static int[] findLoca(int leng, int mx, int my) {
        System.out.println("findLoca");
		for (int i = mx - leng + 1; i <= mx; i++) {
            for (int j = my - leng + 1; j <= my; j++) {
                if (!inMap(i, j)) continue;
                return new int[] {i, j};
            }
		}
		
		return new int[] {-1, -1};
	}
	
	static void moveRunner() {
		for (int i = 1; i <= M; i++) {
			if (isEscape[i]) continue;
			
			Pair runner = runners[i];

			System.out.println("before " + i + " runner.x = " + runner.x + ", runner.y = " + runner.y);
			int minDist = Math.abs(runner.x - exit.x) + Math.abs(runner.y - exit.y);
			List<int[]> list = new ArrayList<int[]>();
			for (int d = 0; d < 4; d++) {
				int nx = runner.x + dx[d];
				int ny = runner.y + dy[d];
				System.out.println("d = " + d + ", nx = " + nx + ", ny = " + ny);
				
                // 맵 밖으로 이동할 수 없습니다.
                if (!inMap(nx, ny)) continue;
				// 벽이 있는 곳으로 이동할 수 없습니다.
				if (map[nx][ny] > 0) continue;
			
				int dist = Math.abs(nx - exit.x) + Math.abs(ny - exit.y);
				System.out.println("d = " + d + ", dist = " + dist + ", minDist = " + minDist);
				if (dist <= minDist) {
					list.add(new int[] {d, dist});
					minDist = dist;
				}
			}
			
			if (list.size() == 0) continue;
			else {
				Collections.sort(list, (o1, o2) -> {
					if (o1[1] == o2[1]) {
						return o1[0] - o2[0]; // 거리가 같다면 상하 우선 이동
					}
					return o1[1] - o2[1]; // 첫 번째 정렬조건. 거리가 가까운순
				});
				
				int[] nextInfo = list.get(0);
				runner.x += dx[nextInfo[0]];
				runner.y += dy[nextInfo[0]];
				runner.score += 1;
				
				System.out.println("after runner.x = " + runner.x + ", runner.y = " + runner.y);
				// 탈출
				if (runner.x == exit.x && runner.y == exit.y) {
					isEscape[runner.id] = true;
					runMap[runner.x][runner.y] = 0;
				}
			}
		}
	}
	
	
}