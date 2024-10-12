import java.util.*;
import java.io.*;

/**
 * 부서지지 않은 포탑이 1개가 된다면 그 즉시 중지
 * 상황에 따라 공격력이 줄어들거나 늘어날 수 있습니다. 
 * 공격력이 0 이하가 된다면, 해당 포탑은 부서지며 더 이상의 공격을 할 수 없습니다.
 * 
 * 공격을 할 때에는 레이저 공격을 먼저 시도하고, 만약 그게 안 된다면 포탄 공격
 * 
 * 레이저
 * 1. 상하좌우 ( 우/하/좌/상의 우선순위 )
 * 2. 부서진 포탑이 있는 위치는 지날 수 없습니다.
 * 3. 가장자리에서 막힌 방향으로 진행하고자 한다면, 반대편으로 나옵니다. (행과 열이 이어질 수 있음)
 * 
 * 공격자의 위치에서 공격 대상 포탑까지의 최단 경로로 공격
 * 그러한 경로가 존재하지 않는다면 (2) 포탄 공격을 진행.
 * 경로의 길이가 똑같은 최단 경로가 2개 이상이라면, 우/하/좌/상의 우선순위대로 먼저 움직인 경로가 선택
 * 공격 대상에는 공격자의 공격력 만큼의 피해를 입히며, 피해를 입은 포탑은 해당 수치만큼 공격력이 줄어듭니다. 
 * 공격 대상을 제외한 레이저 경로에 있는 포탑도 공격을 받게 되는데, 이 포탑은 공격자 공격력의 절반 만큼의 공격
 * 
 * 
 * 
 * (2) 포탄 공격
 * 공격 대상은 공격자 공격력 만큼의 피해
 * 추가적으로 주위 8개의 방향에 있는 포탑도 피해 - 공격자 공격력의 절반 만큼의 피해
 * 공격자는 해당 공격에 영향을 받지 않습니다.
 * 
 * 
 * 공격력이 0 이하가 된 포탑은 부서집니다.
 * 
 * 부서지지 않은 포탑 중 공격과 무관했던 포탑은 공격력이 1씩 올라갑니다.
 */

public class Main {
	
	// N x M 격자
	// K 턴
	static int N, M, K;
	static int[][] map;
	static int[][] lastAttack;
	static boolean[][] isAttacked;
	static int tankNum = 0;
	
	// 우/하/좌/상
	static int[] dx = {0, 1, 0, -1};
	static int[] dy = {1, 0, -1, 0};
	
	static class Pair {
		int x, y;
		
		public Pair(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
	
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st;
		
		st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		K = Integer.parseInt(st.nextToken());
		map = new int[N][M];
		lastAttack = new int[N][M];
		
		for (int i = 0; i < N; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 0; j < M; j++) {
				int power = Integer.parseInt(st.nextToken());
				map[i][j] = power;
				if (power != 0) tankNum++;
			}
		}
		
		for (int t = 1; t <= K; t++) {
			 // 부서지지 않은 포탑이 1개가 된다면 그 즉시 중지
			if (tankNum == 1) break;
			
			isAttacked = new boolean[N][M];
			
			// 공격자 선정
			int[] attacker = findAttacker();
			isAttacked[attacker[0]][attacker[1]] = true;
			lastAttack[attacker[0]][attacker[1]] = t;
			
			// 핸디캡 적용
			map[attacker[0]][attacker[1]] += (N + M);
			
			// 공격대상 선정
			int[] target = findTarget(attacker[0], attacker[1]);
			isAttacked[target[0]][target[1]] = true;
			
			// 공격
			if (!laser(attacker, target)) {
				bomb(attacker, target);
			}
			
			tankNum = 0;
			// 정비
			for (int i = 0; i < N; i++) {
				for (int j = 0; j < M; j++) {
					if (map[i][j] > 0) tankNum++;
					if (map[i][j] <= 0) continue;
					if (isAttacked[i][j]) continue;
					map[i][j]++;
				}
			}
			
			if (tankNum == 1) break;
		}
		
		int maxPower = Integer.MIN_VALUE;
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < M; j++) {
				maxPower = Math.max(maxPower, map[i][j]);
			}
		}
		
		System.out.println(maxPower);
	}
	
	static int[] findAttacker() {
		/*
		 * 1. 공격력이 가장 낮은 포탑
		 * 2. 가장 최근에 공격한 포탑 (모든 포탑은 시점 0에 모두 공격한 경험이 있다고 가정) (최근이면 마지막 턴이니까 큰턴)
		 * 3. 행과 열의 합이 가장 큰 포탑
		 * 4. 열 값이 가장 큰 포탑
		 */
		// 0, 1 : x, y // 2: power // 3. lastAttack
        int power = Integer.MAX_VALUE;
        int minX = -1;
        int minY = -1;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (map[i][j] <= 0) continue;

                if (map[i][j] < power) {
                    power = map[i][j];
                    minX = i; minY = j;
                    continue;
                } else if (map[i][j] > power) continue;

                if (lastAttack[minX][minY] < lastAttack[i][j]) {
                    minX = i; minY = j;
                    continue;
                } else if (lastAttack[minX][minY] > lastAttack[i][j]) continue;

                if ((minX + minY) < (i + j)) {
                    minX = i; minY = j;
                    continue;
                } else if ((minX + minY) > (i + j)) continue;

                if (minY < j) {
                    minX = i; minY = j;
                }
            }

        }
		// List<int[]> list = new ArrayList<int[]>();
		// for (int i = 0; i < N; i++) {
		// 	for (int j = 0; j < M; j++) {
		// 		if (map[i][j] <= 0) continue;
		// 		list.add(new int[] {i, j, map[i][j], lastAttack[i][j]});
		// 	}
		// }
		// Collections.sort(list, (o1, o2) -> {
		// 	if (o1[2] == o2[2]) {
		// 		if (o1[3] == o2[3]) {
		// 			if ((o1[0] + o1[1]) == (o2[0] + o2[1])) {
		// 				return o2[1] - o1[1]; // 4. 열 값이 큰 포탑
		// 			}
		// 			return (o2[0] + o2[1]) - (o1[0] + o1[1]); // 3. 행과 열의 합이 큰 포탑
		// 		}
		// 		return o2[3] - o1[3]; // 2. 가장 최근에 공격한 포탑
		// 	}
		// 	return o1[2] - o2[2]; //1. 공격력이 낮은 포탑
		// });
		// return new int[] {list.get(0)[0], list.get(0)[1]};
        return new int[] {minX, minY};
	}
	
	static int[] findTarget(int atkX, int atkY) {
		/*
		 * 1. 공격력이 가장 높은 포탑이 가장 강한 포탑입니다.
			2. 공격한지 가장 오래된 포탑이 가장 강한 포탑입니다. (모든 포탑은 시점 0에 모두 공격한 경험이 있다고 가정하겠습니다.)
			3. 행과 열의 합이 가장 작은 포탑이 가장 강한 포탑입니다.
			4. 열 값이 가장 작은 포탑이 가장 강한 포탑입니다.
		 */
		// List<int[]> list = new ArrayList<int[]>();
		// for (int i = 0; i < N; i++) {
		// 	for (int j = 0; j < M; j++) {
		// 		if (map[i][j] <= 0) continue;
		// 		if (i == atkX && j == atkY) continue;
		// 		list.add(new int[] {i, j, map[i][j], lastAttack[i][j]});
		// 	}
		// }
		// Collections.sort(list, (o1, o2) -> {
		// 	if (o1[2] == o2[2]) {
		// 		if (o1[3] == o2[3]) {
		// 			if ((o1[0] + o1[1]) == (o2[0] + o2[1])) {
		// 				return o1[1] - o2[1]; // 4. 열 값이 작은 포탑
		// 			}
		// 			return (o1[0] + o1[1]) - (o2[0] + o2[1]); // 3. 행과 열의 합이 작은 포탑
		// 		}
		// 		return o1[3] - o2[3]; // 2. 가장 오래전에 공격한 포탑
		// 	}
		// 	return o2[2] - o1[2]; //1. 공격력이 높은 포탑
		// });
		// return new int[] {list.get(0)[0], list.get(0)[1]};
        int power = -1;
		int ti = 0, tj = 0;
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < M; j++) {
				if (map[i][j] == 0)	continue;
				if (i == atkX && j == atkY)	continue;
				
				// 1. 공격력 높은 포탑
				if (map[i][j] > power) {
					power = map[i][j];
					ti = i;
					tj = j;
					continue;
				} else if (map[i][j] < power)	continue;
				
				// 2. 공격한지 가장 오래된 포탑
				if (lastAttack[i][j] < lastAttack[ti][tj]) {
					ti = i;
					tj = j;
					continue;
				} else if (lastAttack[i][j] > lastAttack[ti][tj])
					continue;
				
				// 3. 행과 열의 합이 가장 작은 포탑
				if (i + j < ti + tj) {
					ti = i;
					tj = j;
					continue;
				} else if (i + j > ti + tj)	continue;
				
				// 4. 열 값이 가장 작은 포탑
				if (j < tj) {
					ti = i;
					tj = j;
				}
			}
		}
		return new int[] { ti, tj };
	}
	
	static boolean laser(int[] attacker, int[] target) {
		boolean[][] visited = new boolean[N][M];
		Pair[][] come = new Pair[N][M];
		
		Queue<Pair> q = new LinkedList<>();
		q.add(new Pair(attacker[0], attacker[1]));
		visited[attacker[0]][attacker[1]] = true;
		
		while (!q.isEmpty()) {
			Pair cur = q.poll();
			for (int d = 0; d < 4; d++) {
				int nx = (cur.x + dx[d] + N) % N;
				int ny = (cur.y + dy[d] + M) % M;
				// 방문했던 곳 continue
				if (visited[nx][ny]) continue;
				// 부서진 포탑이면 경로 불가
				if (map[nx][ny] <= 0) continue;
				come[nx][ny] = new Pair(cur.x, cur.y);
				visited[nx][ny] = true;
				q.add(new Pair(nx, ny));
			}
		}
		// 타겟까지 도달한 적이 없다 = 타겟까지 도달 불가능할 경우
		if (!visited[target[0]][target[1]]) return false;
		
		int x = target[0]; int y = target[1];
		while (x != attacker[0] || y != attacker[1]) {
			int power = map[attacker[0]][attacker[1]] / 2;
			if (x == target[0] && y == target[1]) {
				power = map[attacker[0]][attacker[1]];
			}
			map[x][y] -= power;
			// 공격 관련 기록
			isAttacked[x][y] = true;
			// 경로 역추적
			Pair pair = come[x][y];
			x = pair.x; y = pair.y;
		}
		return true;
	}
	
	/*
	 * 공격 대상은 공격자 공격력 만큼의 피해
	 * 추가적으로 주위 8개의 방향에 있는 포탑도 피해 - 공격자 공격력의 절반 만큼의 피해
	 * 공격자는 해당 공격에 영향을 받지 않습니다.
	 */
	static void bomb(int[] attacker, int[] target) {
		// 상 우상 우 우하 하 좌하 좌 좌상
		int[] bdx = {-1, -1, 0, 1, 1, 1, 0, -1};
		int[] bdy = {0, 1, 1, 1, 0, -1, -1, -1};
		map[target[0]][target[1]] -= map[attacker[0]][attacker[1]];
		int power = map[attacker[0]][attacker[1]] / 2;
		for (int d = 0; d < 8; d++) {
			int nx = (target[0] + bdx[d] + N) % N;
			int ny = (target[1] + bdy[d] + M) % M;
			
			if (map[nx][ny] <= 0) continue;
			if (nx == attacker[0] && ny == attacker[1]) continue;
			map[nx][ny] -= power;
			isAttacked[nx][ny] = true;
		}
	}
}