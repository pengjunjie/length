
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.Hashtable;

public class Length {
	
	private static final String SPLIT = " ";
	private static final String EMAIL = "pjj.ccce@gmail.com";
	private static final String OP_ADD = "+";
	private static final String[][] NORMALIZED_TABLE = {
		{"mile", "mile"},
		{"miles", "mile"},
		{"yard", "yard"},
		{"yards", "yard"},
		{"inch", "inch"},
		{"inches", "inch"},
		{"feet", "foot"},
		{"foot", "foot"},
		{"faths", "fath"},
		{"fath", "fath"},
		{"furlong", "furlong"},
	};



	private static void parseFileAndOutput(String inputPath, String outputPath) throws IOException {
		File inputFile = new File(inputPath);
		if (!inputFile.exists()) {
			throw new IllegalArgumentException(inputPath + " does not exists!");
		}
		
		BufferedReader br = new BufferedReader(new FileReader(inputFile));	
		Hashtable<String, Double> transferTable = new Hashtable<String, Double>(20);
		readTransform(br, transferTable);
		
		File outputFile = new File(outputPath);
		outputFile.createNewFile();
		PrintStream ps = new PrintStream(outputFile);		
		parseAndOutputResult(br, ps, transferTable);
		
		ps.close();
		br.close();
	}

	private static void readTransform(BufferedReader br,
					Hashtable<String, Double> transferTable) throws IOException {
		String line = null;
		while(( line = br.readLine()) != null) {
			if (line.length()  == 0) {
				break;
			}
			
			parseSingleFormulation(line, transferTable);
		}
	}

	private static void parseSingleFormulation(String line,
			Hashtable<String, Double> transferTable) {
		String[] segs = line.split(SPLIT);
		String name = segs[1];
		String value = segs[3];
		transferTable.put(name, Double.valueOf(value));
	}

	private static void parseAndOutputResult(BufferedReader br, PrintStream ps,
				Hashtable<String, Double> transferTable) throws IOException {
		ps.println(EMAIL);
		
		String line = null;
		while(( line = br.readLine()) != null) {
			if (line.length()  == 0) {
				break;
			}
			
			double value = parseSingleTransformation(line, transferTable);
			outputSingleTransform(ps, value);
		}
	}

	private static double parseSingleTransformation(String line,
			Hashtable<String, Double> transferTable) {
		String[] segs = line.split(SPLIT);		
		String value = segs[0];
		String name = segs[1];
		double simpleValue = transferSimpleValue(name, value, transferTable);
		double totoalValue = simpleValue;
		String op = null;
		
		for (int i = 2; i < segs.length; i += 3) {
			op = segs[i];
			value = segs[i + 1];
			name = segs[i + 2];
			simpleValue = transferSimpleValue(name, value, transferTable);

			if (op.equals(OP_ADD)) {
				totoalValue += simpleValue;
			} else {
				totoalValue -= simpleValue;				
			}
		}
		
		return totoalValue;		
	}

	private static double transferSimpleValue(String name, String value, Hashtable<String, Double> transferTable) {
		String normalizedName = normalizeName(name);
		Double trans = transferTable.get(normalizedName);
		return Double.valueOf(value).doubleValue() * trans.doubleValue();
	}

	private static String normalizeName(String name) {
		for (int i = 0; i < NORMALIZED_TABLE.length; i++) {
			if (NORMALIZED_TABLE[i][0].equals(name)) {
				return NORMALIZED_TABLE[i][1];
			}
		}
		throw new IllegalStateException("Strange name: " + name);
	}
	
	private static void outputSingleTransform(PrintStream ps, double value) {
		DecimalFormat format  = new DecimalFormat("0.00"); 
		ps.print("\n" + format.format(value) + SPLIT + "m");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String path = "input.txt";
		
		if (args.length > 0) {
			path = args[0];
		}
		
		String outputPath = "output.txt";
		try{
			parseFileAndOutput(path, outputPath);
		} catch(IOException e) {
			e.printStackTrace();
		}
		System.out.println("finish!Please see " + outputPath);
	}
}
