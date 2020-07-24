import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Scanner;

class Node {
	public int x;
	public int y;
	public String id;
	public HashMap<Node, Integer> edges;
	
	public Node(int x, int y) {
		this.x = x;
		this.y = y;
		this.id = String.format("%d %d", x, y);
		this.edges = new HashMap<Node, Integer>();
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Node)) {
			return false;
		}
		Node o = (Node) obj;
		return o.id.equals(id);
	}
	
	@Override
	public String toString() {
		return id;
	}
}

class State implements Comparable<State> {
	public ArrayList<Node> paths;
	public int weight;
	
	public State() {
		paths = new ArrayList<Node>();
		weight = 0;
	}
	
	public State(State s) {
		this.paths = new ArrayList<Node>(s.paths);
		this.weight = s.weight;
	}
	
	public boolean contains(Node n) {
		return paths.contains(n);
	}
	
	public Node getLastNode() {
		return paths.get(paths.size() - 1);
	}

	@Override
	public int compareTo(State o) {
		return weight - o.weight;
	}
}

public class WGraph {
	HashMap<String, Node> nodes;

	public WGraph(String FName) {
		nodes = new HashMap<String, Node>();
		try {
			Scanner scanner = new Scanner(new File(FName));
			int numberOfVertices = scanner.nextInt();
			int numberOfEdges = scanner.nextInt();
			for (int i = 0; i < numberOfEdges; i++) {
				int x1 = scanner.nextInt();
				int y1 = scanner.nextInt();
				int x2 = scanner.nextInt();
				int y2 = scanner.nextInt();
				int weight = scanner.nextInt();
				Node n1;
				Node n2;
				String s1 = String.format("%d %d", x1, y1);
				String s2 = String.format("%d %d", x2, y2);
				// node1
				if (nodes.containsKey(s1)) {
					n1 = nodes.get(s1);
				} else {
					n1 = new Node(x1, y1);
					nodes.put(s1, n1);
				}
				// node2
				if (nodes.containsKey(s2)) {
					n2 = nodes.get(s2);
				} else {
					n2 = new Node(x2, y2);
					nodes.put(s2, n2);
				}
				n1.edges.put(n2, weight);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	// pre:  ux, uy, vx, vy are valid coordinates of vertices u and v
	//       in the graph
	// post: return arraylist contains even number of integers,
	//       for any even i,
	//       i-th and i+1-th integers in the array represent
	//       the x-coordinate and y-coordinate of the i/2-th vertex
	//       in the returned path (path is an ordered sequence of vertices)
	public ArrayList<Integer> V2V(int ux, int uy, int vx, int vy) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		String su = String.format("%d %d", ux, uy);
		String sv = String.format("%d %d", vx, vy);
		Node start = nodes.get(su);
		Node end = nodes.get(sv);
		if (start == null || end == null) {
			return result;
		}
		
		State state = new State();
		state.paths.add(start);
		PriorityQueue<State> allState = new PriorityQueue<State>();
		allState.add(state);
		
		ArrayList<Node> ends = new ArrayList<Node>();
		ends.add(end);
		State solution = getSolution(allState, ends);
		
		if (solution != null) {
			for (Node n: solution.paths) {
				result.add(n.x);
				result.add(n.y);
			}
		}
		
		return result;
	}
	
	private State getSolution(PriorityQueue<State> allState, ArrayList<Node> end) {
		State solution = null;
		while (!allState.isEmpty()) {
			State front = allState.poll();
			Node lastNode = front.getLastNode();
			if (end.contains(lastNode)) {
				solution = front;
				break;
			}
			for (Entry<Node, Integer> entry: lastNode.edges.entrySet()) {
				if (!front.contains(entry.getKey())) {
					State childState = new State(front);
					childState.paths.add(entry.getKey());
					childState.weight += entry.getValue();
					allState.add(childState);
				}
			}
		}
		return solution;
	}
	
	// pre:  ux, uy are valid coordinates of vertex u from the graph
	//       S represents a set of vertices.
	//       The S arraylist contains even number of intergers
	//       for any even i,
	//       i-th and i+1-th integers in the array represent
	//       the x-coordinate and y-coordinate of the i/2-th vertex
	//       in the set S.
	// post: same structure as the last method’s post.
	public ArrayList<Integer> V2S(int ux, int uy, ArrayList<Integer> S) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		String su = String.format("%d %d", ux, uy);
		Node start = nodes.get(su);
		if (start == null) {
			return result;
		}
		// find all ends nodes
		ArrayList<Node> end = new ArrayList<Node>();
		for (int i = 0; i < S.size(); i += 2) {
			Node node = new Node(S.get(i), S.get(i + 1));
			end.add(node);
		}
		
		State state = new State();
		state.paths.add(start);
		PriorityQueue<State> allState = new PriorityQueue<State>();
		allState.add(state);
		State solution = getSolution(allState, end);
		if (solution != null) {
			for (Node n: solution.paths) {
				result.add(n.x);
				result.add(n.y);
			}
		}
		
		return result;
	}
	
	// pre:  S1 and S2 represent sets of vertices (see above for
	//       the representation of a set of vertices as arrayList)
	// post: same structure as the last method’s post.
	public ArrayList<Integer> S2S(ArrayList<Integer> S1, ArrayList<Integer> S2) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		// find all start nodes
		ArrayList<Node> start = new ArrayList<Node>();
		for (int i = 0; i < S1.size(); i += 2) {
			String s1 = String.format("%d %d", S1.get(i), S1.get(i + 1));
			Node node = nodes.get(s1);
			if (node != null) {
				start.add(node);
			}
		}
		// find all ends nodes
		ArrayList<Node> end = new ArrayList<Node>();
		for (int i = 0; i < S2.size(); i += 2) {
			Node node = new Node(S2.get(i), S2.get(i + 1));
			end.add(node);
		}
		
		ArrayList<State> solutions = new ArrayList<State>();
		// search for each start node
		for (Node startNode: start) {
		
			State state = new State();
			state.paths.add(startNode);
			PriorityQueue<State> allState = new PriorityQueue<State>();
			allState.add(state);
			State solution = getSolution(allState, end);
			if (solution != null) {
				solutions.add(solution);
			}
		}
		// find mininum solution
		State minSolution = null;
		if (solutions.isEmpty()) {
			result.add(S1.get(0));
			result.add(S1.get(1));
			return result;
		} else {
			for (State solution: solutions) {
				if (minSolution == null) {
					minSolution = solution;
				} else if (minSolution.weight > solution.weight) {
					minSolution = solution;
				}
			}
		}
		if (minSolution != null) {
			for (Node n: minSolution.paths) {
				result.add(n.x);
				result.add(n.y);
			}
		}
		return result;
	}
}
