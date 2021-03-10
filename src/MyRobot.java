import java.awt.Point;
import world.Robot;
import world.World;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class MyRobot extends Robot{
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
	
	public MyRobot(World w) {
		world = w;
	}

	@Override
	public void travelToDestination() {
		int numRows = world.numRows();
		int numCols = world.numCols();
		Point start = world.getStartPos();
		Point end = world.getEndPos();
		int [][] grid = new int [numRows][numCols];
		QueueComparator c = new QueueComparator();
		PriorityQueue<Element> pq = new PriorityQueue<>(c);
		int startWeight = this.heuristicValue(start, end);
		pq.add(new Element(start, startWeight));
		grid[start.x][start.y] = startWeight;
		
		ArrayList<Point> discovered = new ArrayList<>();
		while(grid[end.x][end.y] == 0) {
			Element e = pq.remove();
			Point current = e.getPoint();
			if(current.equals(start) || current.equals(end) || !super.pingMap(current).equals("X")) {
				grid[current.x][current.y] = e.getInteger();
				int minValue = e.getInteger() + 1;
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
									if(wValue <= minValue+1) {
										pq.add(new Element(newPoint, wValue));
										minValue = wValue;
									}
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
		/*for(int r=0; r<numRows; r++) {
			for(int cl=0; cl<numCols; cl++) {
				if(path.contains(new Point(r, cl))) {
					System.out.print("+ ");
				}else{
					System.out.print("O ");
				}
			}
			System.out.println("");
		}*/
		for(int i=path.size()-2; i>=0; i--) {
			super.move(path.get(i));
		}
		
	}
	
	public int heuristicValue(Point a, Point b) {
		return Math.max(Math.abs(a.x - b.x), Math.abs(a.y -b.y));
	}
	
	public static void main(String [] args) {
		try {
			//World myWorld = new World(args[0], false);
			World myWorld = new World("src/TestCases/myInputFile3.txt", false);
			MyRobot myRobot = new MyRobot(myWorld);
			myRobot.addToWorld(myWorld);
			myRobot.travelToDestination();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
