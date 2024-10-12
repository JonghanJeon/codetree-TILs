import java.util.*;
import java.io.*;

public class Main {
	
	static class Pair {
		int x, y;
		
		Pair(int x, int y) {
			this.x = x; this.y = y;
		}
	}
	
	static int n, m;
	static int[][] map;
	static Pair[] store;
	static Pair[] people;
	
	static int[] dx = {-1, 0, 0, 1};
	static int[] dy = {0, -1, 1, 0};
	
	static int time;
	
	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringTokenizer st;
		
		st = new StringTokenizer(br.readLine());
		n = Integer.parseInt(st.nextToken());
		m = Integer.parseInt(st.nextToken());
		map = new int[n][n];
		for (int i = 0; i < n; i++) {
			st = new StringTokenizer(br.readLine());
			for (int j = 0; j < n; j++) {
				map[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		
		store = new Pair[m];
		people = new Pair[m];
		for (int i = 0; i < m; i++) {
			st = new StringTokenizer(br.readLine());
			int x = Integer.parseInt(st.nextToken()) - 1;
			int y = Integer.parseInt(st.nextToken()) - 1;
			store[i] = new Pair(x, y);
			people[i] = new Pair(-1, -1);
		}
		
		time = 1;
		
		while (true) {
			moveStore();
			
			if (time <= m) 
				moveBasecamp(store[time - 1]);
			
			if (isFinish())
				break;
			
			time += 1;
		}
		
		System.out.println(time);
		
	}
	
	static void moveStore() {
		for (int i = 0; i < m; i++) {
			Pair start = people[i];
			Pair end = store[i];
			
			if (start.x == -1 && start.y == -1)
				continue;
			
			if (!inMap(start.x, start.y))
				continue;
			
			if (start.x == end.x && start.y == end.y)
				continue;
			
			int dir = findDir(start, end);
			
			start.x += dx[dir];
			start.y += dy[dir];
		}
	}
	
	static int findDir(Pair start, Pair end) {
		int sx = start.x; int sy= start.y;
		Queue<int[]> q = new LinkedList<int[]>();
		q.add(new int[] {sx, sy, -1});
		boolean[][] visited = new boolean[n][n];
		visited[sx][sy] = true;
		
		while(!q.isEmpty()) {
			int[] cur = q.poll();
			if (cur[0] == end.x && cur[1] == end.y)
				return cur[2];
			
			for (int d = 0; d < 4; d++) {
				int nx = cur[0] + dx[d];
				int ny = cur[1] + dy[d];
				
				if (!inMap(nx, ny))
					continue;
				
				if (visited[nx][ny])
					continue;
				
				if (map[nx][ny] == -1)
					continue;
				
				if (cur[2] == -1) 
					q.add(new int[] {nx, ny, d});
				else
					q.add(new int[] {nx, ny, cur[2]});
			}
		}
		
		return -1;
	}
	
	static void moveBasecamp(Pair start) {
		int sx = start.x; int sy = start.y;
		
		Queue<int[]> q = new LinkedList<>();
		boolean[][] visited = new boolean[n][n];
		visited[sx][sy] = true;
		q.add(new int[] {sx, sy, 0});
		
		List<int[]> list = new ArrayList<int[]>();
		
		while(!q.isEmpty()) {
			int[] cur = q.poll();
			
			if (map[cur[0]][cur[1]] == 1) {
				list.add(new int[] {cur[0], cur[1], cur[2]});
			}
			
			for (int d = 0; d < 4; d++) {
				int nx = cur[0] + dx[d];
				int ny = cur[1] + dy[d];
				
				if (!inMap(nx, ny))
					continue;
				
				if (visited[nx][ny])
					continue;
				
				if (map[nx][ny] == -1)
					continue;
				
				q.add(new int[] {nx, ny, cur[2] + 1});
				visited[nx][ny] = true;
			}
		}
		
		if (list.size() > 1) {
			Collections.sort(list, (o1, o2) -> {
				if (o1[2] == o2[2]) {
					if (o1[0] == o2[0]) {
						return o1[1] - o2[1];
					}
					return o1[0] - o2[0];
				}
				return o1[2] - o2[2];
			});
		}
		
		int[] baseCamp = list.get(0);
		map[baseCamp[0]][baseCamp[1]] = -1;
		people[time - 1] = new Pair(baseCamp[0], baseCamp[1]);
	}
	
	static boolean isFinish() {
		for (int i = 0; i < m; i++) {
			if (!(people[i].x == store[i].x && people[i].y == store[i].y))
				return false;
		}
		return true;
	}
	
	static boolean inMap(int x, int y) {
		return 0 <= x && x < n && 0 <= y && y < n;
	}
}