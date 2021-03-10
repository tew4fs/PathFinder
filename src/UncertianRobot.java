import java.awt.Point;
import world.Robot;
import world.World;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class UncertianRobot extends Robot{
	public class Element {
		private Point p;
		private int i;
		public Element(Point point, int integer) {
			p = point;
			i = integer;
		}
		
		public Point getPoint() {
			return p;
		}
		public int getInteger() {
			return i;
		}
	}
	
	public class QueueComparator implements Comparator<Element>{

		@Override
		public int compare(Element o1, Element o2) {
			return o1.i - o2.i;
		}
		
	}
	
	private World world;
	private ArrayList<Point> nonvalid, valid;
	private ArrayList<ArrayList<Point>> pathsTried;
	
	public UncertianRobot(World w) {
		world = w;
		nonvalid = new ArrayList<>();
		valid = new ArrayList<>();
		pathsTried = new ArrayList<>();
	}

	@Override
	public void travelToDestination() {
		Point start = world.getStartPos();
		Point end = world.getEndPos();
		valid.add(start);
		valid.add(end);
		ArrayList<Point> currentPath = new ArrayList<>();
		ArrayList<Point> path = this.getPath(start, end);
		while(path == null) {
			System.out.println("here");
			path = this.getPath(start, end);
		}
		while(!path.get(path.size() - 2).equals(end)) {
			Point oldPoint = super.getPosition();
			Point newPoint = super.move(path.get(path.size() - 2));
			if (newPoint.equals(oldPoint)){
				for(int k=currentPath.size() - 1; k>=0; k--) {
					super.move(currentPath.get(k));
				}
				pathsTried.add(currentPath);
				currentPath.clear();
				nonvalid.add(path.get(path.size() - 2));
				path = this.getPath(start, end);
				while(path == null) {
					//System.out.println("here");
					path = this.getPath(start, end);
				}
			}else {
				currentPath.add(path.get(path.size() - 2));
				valid.add(path.get(path.size() - 2));
				path = this.getPath(newPoint, end);
				if(path == null) {
					for(int k=currentPath.size() - 1; k>=0; k--) {
						super.move(currentPath.get(k));
					}
					pathsTried.add(currentPath);
					currentPath.clear();
					path = this.getPath(start, end);
					while(path == null) {
						//System.out.println("here");
						path = this.getPath(start, end);
					}
				}
			}
		}
		super.move(path.get(path.size() - 2));
		
	}
	
	public ArrayList<Point> getPath(Point start, Point end){
		int numRows = world.numRows();
		int numCols = world.numCols();
		int [][] grid = new int [numRows][numCols];
		QueueComparator c = new QueueComparator();
		PriorityQueue<Element> pq = new PriorityQueue<>(c);
		int startWeight = this.heuristicValue(start, end);
		pq.add(new Element(start, startWeight));
		grid[start.x][start.y] = startWeight;
		
		ArrayList<Point> discovered = new ArrayList<>();
		while(grid[end.x][end.y] == 0) {
			Element e = pq.remove();
			if(pq.size() == 0) {
				return null;
			}
			Point current = e.getPoint();
			boolean lookAtPoint = !nonvalid.contains(current);
			if(lookAtPoint) {
				if(valid.contains(current)) {
					lookAtPoint = true;
				}else {
					lookAtPoint = false;
					int validCount = 0;
					for(int j=0; j<5; j++) {
						if(super.pingMap(current).equals("O")) {
							validCount++;
						}
					}
					if(validCount >= 3) {
						lookAtPoint = true;
					}
				}
			}
			if(lookAtPoint) {
				grid[current.x][current.y] = e.getInteger();
				for(int i=0; i<9; i++) {
					try {
						int rowOffset = i/3;
						int colOffset = i%3;
						if(!(rowOffset == 1 && colOffset == 1)) {
							Point newPoint = new Point(current.x + rowOffset - 1, current.y + colOffset - 1);
							if(!discovered.contains(newPoint)) {
								discovered.add(newPoint);
								if(grid[newPoint.x][newPoint.y] == 0) {
									int wValue = e.getInteger() - this.heuristicValue(current, end) + 1 + this.heuristicValue(newPoint, end);
									pq.add(new Element(newPoint, wValue));
								}
							}
						}
					}catch(Exception ex) {
					}
				}
			}
		}
		
		Point current = end;
		ArrayList<Point> path = new ArrayList<>();
		path.add(current);
		while(!current.equals(start)) {
			Point minPoint = new Point();
			int min = Integer.MAX_VALUE;
			for(int i=0; i<9; i++) {
				try {
					int rowOffset = i/3;
					int colOffset = i%3;
					
					if(!(rowOffset == 1 && colOffset == 1)) {
						Point check = new Point(current.x + rowOffset - 1, current.y + colOffset - 1);
						int travelLength = grid[check.x][check.y] - this.heuristicValue(check, end);
						if(grid[check.x][check.y] != 0 && travelLength < min) {
							min = travelLength;
							minPoint = check;
						}
					}
				}catch(Exception ex) {
					
				}
			}
			current = minPoint;
			path.add(current);
		}
		return path;
	}
	
	public int heuristicValue(Point a, Point b) {
		return Math.max(Math.abs(a.x - b.x), Math.abs(a.y -b.y));
	}
	
	public static void main(String [] args) {
		try {
			//World myWorld = new World(args[0], false);
			World myWorld = new World("src/TestCases/myInputFile2.txt", true);
			UncertianRobot myRobot = new UncertianRobot(myWorld);
			myRobot.addToWorld(myWorld);
			myRobot.travelToDestination();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
