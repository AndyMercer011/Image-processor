import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

class Pixel {
	int r;
	int g;
	int b;
}

public class ImageProcessor {
	ArrayList<ArrayList<Pixel>> image;
	int H;
	int W;
	public ImageProcessor(String FName) {
		image = new ArrayList<ArrayList<Pixel>>();
		try {
			Scanner scanner = new Scanner(new File(FName));
			H = scanner.nextInt();
			W = scanner.nextInt();
			for (int i = 0; i < H; i++) {
				ArrayList<Pixel> line = new ArrayList<Pixel>();
				for (int j = 0; j < W; j++) {
					Pixel p = new Pixel();
					p.r = scanner.nextInt();
					p.g = scanner.nextInt();
					p.b = scanner.nextInt();
					line.add(p);
				}
				image.add(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	// calculate PDist
	private int getPDist(Pixel p1, Pixel p2) {
		return (p1.r - p2.r) * (p1.r - p2.r) + (p1.g - p2.g) * (p1.g - p2.g) + (p1.b - p2.b) * (p1.b - p2.b);
	}
	
	// calculate Y importance
	private int getYImportance(int i, int j) {
		if (i == 0) {
			Pixel p1 = image.get(H - 1).get(j);
			Pixel p2 = image.get(i + 1).get(j);
			return getPDist(p1, p2);
		} else if (i == H - 1) {
			Pixel p1 = image.get(i - 1).get(j);
			Pixel p2 = image.get(0).get(j);
			return getPDist(p1, p2);
		} else {
			Pixel p1 = image.get(i - 1).get(j);
			Pixel p2 = image.get(i + 1).get(j);
			return getPDist(p1, p2);
		}
	}
	
	// calculate X importance
	private int getXImportance(int i, int j) {
		if (j == 0) {
			Pixel p1 = image.get(i).get(W - 1);
			Pixel p2 = image.get(i).get(j + 1);
			return getPDist(p1, p2);
		} else if (j == W - 1) {
			Pixel p1 = image.get(i).get(j - 1);
			Pixel p2 = image.get(i).get(0);
			return getPDist(p1, p2);
		} else {
			Pixel p1 = image.get(i).get(j - 1);
			Pixel p2 = image.get(i).get(j + 1);
			return getPDist(p1, p2);
		}
	}
	
	// calculate importance
	private int getImportance(int i, int j) {
		return getXImportance(i, j) + getYImportance(i, j);
	}
	
	// get importance
	public ArrayList<ArrayList<Integer>> getImportance() {
		ArrayList<ArrayList<Integer>> I = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < H; i++) {
			ArrayList<Integer> l = new ArrayList<Integer>();
			for (int j = 0; j < W; j++) {
				l.add(getImportance(i, j));
			}
			I.add(l);
		}
		return I;
	}
	
	// compute the reduced image
	public void writeReduced(int k, String FName) {
		ArrayList<ArrayList<Integer>> I = getImportance();
		// make graph file
		try {
			FileWriter writer = new FileWriter(new File("temp.txt"));
			writer.write(String.format("%d\n", H * W));
			writer.write(String.format("%d\n", (H * (W - 1))));
			for (int i = 0; i < H; i++) {
				for (int j = 0; j < W - 1; j++) {
					writer.write(String.format("%d %d %d %d %d\n", i, j, i, j + 1, I.get(i).get(j)));
				}
			}
			writer.write("\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		WGraph w = new WGraph("temp.txt");
		// reduce image
		try {
			FileWriter writer = new FileWriter(new File(FName));
			writer.write(String.format("%d\n", H));
			writer.write(String.format("%d\n", W - k));
			for (int i = 0; i < H; i++) {
				for (int j = 0; j < W - k; j++) {
					ArrayList<Integer> v2v = w.V2V(i, j, i, j + 1);
					if (!v2v.isEmpty()) {
						writer.write(String.format("%d %d %d ", image.get(i).get(j).r, image.get(i).get(j).g, image.get(i).get(j).b));
					}
				}
				writer.write("\n");
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
