import java.util.*;
import java.io.*;

public class Main {
	
	static class Pair {
		int x, y;
		
		Pair(int a, int b) {
			x = a; y = b;
		}
	}
	
	static int N, M, K;
    // 모든 벽들의 상태 기록
	static int[][] map;
	
	// 회전 구현을 편하기 하기 위해 2차원 배열을 하나 더 정의
    static int[][] nextMap;
    
    // 참가자의 위치 정보 기록
    static Pair[] players;
    
    // 출구의 위치 정보 기록
    static Pair exits;
    
    // 정답을 기록
    static int ans;
    
    // 회전해야 하는 최소 정사각형을 찾아 기록
    static int sx, sy, squareSize;
    
    static int[] dx = {-1, 1, 0, 0};
    static int[] dy = {0, 0, -1, 1};
    
    // 모든 참가자 이동
    static void movePlayer() {
    	for (int i = 1; i <= M; i++) {
    		
    		// 이미 출구에 있는 경우 스킵
    		if (players[i].x == exits.x && players[i].y == exits.y) continue;
    		
    		int base = getDist(players[i].x, players[i].y);
    		
    		for (int d = 0; d < 4; d++) {
    			int nx = players[i].x + dx[d];
    			int ny = players[i].y + dy[d];
    			
    			if (!inMap(nx, ny)) continue;
    			
    			int value = getDist(nx, ny);
    			
    			if (base < value) continue;
    			
    			if (map[nx][ny] > 0) continue;
    			
    			players[i].x = nx;
    			players[i].y = ny;
    			ans++;
    			
    			break;
    		}
    	}
    }
    
    static void findSquare() {
		// 0 = x
		// 1 = y
		// 2 = leng
		List<int[]> list = new ArrayList<int[]>();
		for (int i = 1; i <= M; i++) {
			if (players[i].x == exits.x && players[i].y == exits.y) continue;
			
			Pair player = players[i];

			int gx = Math.abs(player.x - exits.x) + 1;
			int gy = Math.abs(player.y - exits.y) + 1;
			
			int leng = Math.max(gx, gy);
			
			int mx = Math.max(player.x, exits.x);
			int my = Math.max(player.y, exits.y);
			
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
		
		sx = list.get(0)[0];
		sy = list.get(0)[1];
		squareSize = list.get(0)[2];
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
    
    static void rotateSquare() {
    	// 먼저 정사각형 안에 있는 벽들을 1 감소시킴
    	for (int x = sx; x < sx + squareSize; x++) {
    		for (int y = sy; y < sy + squareSize; y++) {
    			if (map[x][y] > 0) map[x][y]--;
    		}
    	}
    	
    	nextMap = new int[N + 1][N + 1];
    	// 정사각형을 90도 회전시켜줍니다.
    	for (int x = sx; x < sx + squareSize; x++) {
    		for (int y = sy; y < sy + squareSize; y++) {
    			int oldX = x - sx; int oldY = y - sy;
    			int newX = oldY + sx;
    			int newY = (squareSize - 1) - oldX + sy;
    			
    			nextMap[newX][newY] = map[x][y];
    		}
    	}
    	
    	for (int x = sx; x < sx + squareSize; x++) {
    		for (int y = sy; y < sy + squareSize; y++) {
    			map[x][y] = nextMap[x][y];
    		}
    	}
    }
    
    static void rotatePlayerAndExit() {
    	for (int i = 1; i <= M; i++) {
    		int x = players[i].x;
    		int y = players[i].y;
    		// 참가자가 정사각형 안에 있는 경우 회전
    		if (sx <= x && x < sx + squareSize && sy <= y && y < sy + squareSize) {
    			int oldX = x - sx;
    			int oldY = y - sy;
    			int newX = oldY + sx;
    			int newY = (squareSize - 1) - oldX + sy;
    			
    			players[i].x = newX;
    			players[i].y = newY;
    		}
    	}
    	
    	int x = exits.x; int y = exits.y;
    	
    	if (sx <= x && x < sx + squareSize && sy <= y && y < sy + squareSize) {
    		// 좌표를 0, 0 으로 옮겨주는 변환
    		int oldX = x - sx; int oldY = y - sy;
    		// 변환된 상태에서 회전.
    		int rx = oldY;
    		int ry = squareSize - 1 - oldX;
    		// 다시 sx, sy로 옮겨주는 변환
    		exits.x = rx + sx;
    		exits.y = ry + sy;
    	}
    }
    
    static int getDist(int x, int y) {
    	return Math.abs(exits.x - x) + Math.abs(exits.y - y);
    }
    
    static boolean inMap(int x, int y) {
    	return 1 <= x && x <= N && 1 <= y && y <= N;
    }
    
	
	public static void main(String[] args) throws IOException {
		Scanner sc = new Scanner(System.in);
		N = sc.nextInt();
		M = sc.nextInt();
		K = sc.nextInt();
		map = new int[N + 1][N + 1];
		players = new Pair[M + 1];
		
		for (int i = 1; i <= N; i++) {
			for (int j = 1; j <= N; j++) {
				map[i][j] = sc.nextInt();
			}
		}
		
		for (int i = 1; i <= M; i++) {
			int x = sc.nextInt();
			int y = sc.nextInt();
			players[i] = new Pair(x, y);
		}
		
		exits = new Pair(sc.nextInt(), sc.nextInt());
		
		for (int t = 0; t < K; t++) {
			
			movePlayer();
			
			boolean isAllEscaped = true;
			for (int i = 1; i <= M; i++) {
				if (players[i].x != exits.x || players[i].y != exits.y) isAllEscaped = false;
			}
			
			if (isAllEscaped) break;
			
			findSquare();
			
			rotateSquare();
			rotatePlayerAndExit();
			
		}
		
		System.out.println(ans);
		System.out.println(exits.x + " " + exits.y);
	}
}