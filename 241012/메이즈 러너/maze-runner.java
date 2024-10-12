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
    	// m명의 모든 참가자들에 대해 이동을 진행합니다.
        for(int i = 1; i <= M; i++) {
            // 이미 출구에 있는 경우 스킵합니다.
            if(players[i].x == exits.x && players[i].y == exits.y)
                continue;
            
            // 행이 다른 경우 행을 이동시켜봅니다.
            if(players[i].x != exits.x) {
                int nx = players[i].x;
                int ny = players[i].y;
    
                if(exits.x > nx) nx++;
                else nx--;
    
                // 벽이 없다면 행을 이동시킬 수 있습니다.
                // 이 경우 행을 이동시키고 바로 다음 참가자로 넘어갑니다.
                if(map[nx][ny] == 0) {
                    players[i].x = nx;
                    players[i].y = ny;
                    ans++;
                    continue;
                }
            }
    
            // 열이 다른 경우 열을 이동시켜봅니다.
            if(players[i].y != exits.y) {
                int nx = players[i].x;
                int ny = players[i].y;
    
                if(exits.y > ny) ny++;
                else ny--;
    
                // 벽이 없다면 행을 이동시킬 수 있습니다.
                // 이 경우 열을 이동시킵니다.
                if(map[nx][ny] == 0) {
                    players[i].x = nx;
                    players[i].y = ny;
                    ans++;
                    continue;
                }
            }
        }
    }
    
    static void findMinimumSquare() {
    	// 가장 작은 정사각형부터 모든 정사각형을 만들어봅니다.
        for(int sz = 2; sz <= N; sz++) {
            // 가장 좌상단 r 좌표가 작은 것부터 하나씩 만들어봅니다.
            for(int x1 = 1; x1 <= N - sz + 1; x1++) {
                // 가장 좌상단 c 좌표가 작은 것부터 하나씩 만들어봅니다.
                for(int y1 = 1; y1 <= N - sz + 1; y1++) {
                    int x2 = x1 + sz - 1;
                    int y2 = y1 + sz - 1;
    
                    // 만약 출구가 해당 정사각형 안에 없다면 스킵합니다.
                    if(!(x1 <= exits.x && exits.x <= x2 && y1 <= exits.y && exits.y <= y2)) {
                        continue;
                    }
    
                    // 한 명 이상의 참가자가 해당 정사각형 안에 있는지 판단합니다.
                    boolean isTravelerIn = false;
                    for(int l = 1; l <= M; l++) {
                        if(x1 <= players[l].x && players[l].x <= x2 && y1 <= players[l].y && players[l].y <= y2) {
                            // 출구에 있는 참가자는 제외합니다.
                            if(!(players[l].x == exits.x && players[l].y == exits.y)) {
                                isTravelerIn = true;
                            }
                        }
                    }
    
                    // 만약 한 명 이상의 참가자가 해당 정사각형 안에 있다면
                    // sx, sy, sqaureSize 정보를 갱신하고 종료합니다.
                    if(isTravelerIn) {
                        sx = x1;
                        sy = y1;
                        squareSize = sz;
    
                        return;
                    }
                }
            }
        }
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
			
			findMinimumSquare();
			
			rotateSquare();
			rotatePlayerAndExit();
			
		}
		
		System.out.println(ans);
		System.out.println(exits.x + " " + exits.y);
	}
}