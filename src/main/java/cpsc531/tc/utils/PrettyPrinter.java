package cpsc531.tc.utils;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math.linear.RealMatrix;

public class PrettyPrinter {

	public static void prettyPrintMatrix(String legend, RealMatrix matrix,
			String[] documentNames, String[] words, PrintWriter writer) {
		writer.printf("=== %s ===%n", legend);
		writer.printf("%15s", " ");
		for (int i = 0; i < documentNames.length; i++) {
			writer.printf("%8s", documentNames[i]);
		}
		writer.println();
		for (int i = 0; i < words.length; i++) {
			writer.printf("%15s", words[i]);
			for (int j = 0; j < documentNames.length; j++) {
				writer.printf("%8.4f", matrix.getEntry(i, j));
			}
			writer.println();
		}
		writer.flush();
	}

	public static void prettyPrintPartsOfMatrix(String legend,
			RealMatrix matrix, String[] documentNames, String[] words,
			PrintWriter writer, int startRow, int endRow, int startCol,
			int endCol) {
		writer.printf("=== %s ===%n", legend);
		writer.printf("%15s", " ");
		for (int i = 0; i < endCol && i < documentNames.length; i++) {
			if (i > startCol)
				writer.printf("%8s", documentNames[i]);
		}
		writer.println();
		for (int i = 0; i < words.length && i < endRow; i++) {
			if (i > startRow) {
				writer.printf("%15s", words[i]);
				for (int j = 0; j < endCol && j < documentNames.length; j++) {
					if (j > startCol)
						writer.printf("%8.4f", matrix.getEntry(i, j));
				}
				writer.println();
			}
		}
		writer.flush();
	}

	public static void prettyPrintMap(String legend, Map m,
			PrintWriter writer) {
		writer.printf("=== %s ===%n", legend);
		Set<Map.Entry<String, String>> setView = m.entrySet();
		for (Iterator<Map.Entry<String, String>> it = setView.iterator(); it
				.hasNext();) {
			Map.Entry<String, String> me = it.next();
			writer.printf("Key:%s, Value:%s%n", me.getKey(),
					me.getValue());
		}
		writer.flush();
	}
	
	public static void prettyPrintPortsOfMap(String legend, Map m,
			PrintWriter writer, int start, int end) {
		writer.printf("=== %s ===%n", legend);
		Set<Map.Entry<String, String>> setView = m.entrySet();
		int count = 0;
		for (Iterator<Map.Entry<String, String>> it = setView.iterator(); it
				.hasNext();) {
			Map.Entry<String, String> me = it.next();
			count ++;
			if (count > end) break;
			if(count >= start)
				writer.printf("Key:%s, Value:%s%n", me.getKey(),me.getValue());
		}
		writer.flush();
	}

}
