import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

public class ReplaceStateMachine {

	public static String replaceString(String pattern, String replacement, String inputFilename) {
		String lines = "";
		String line = null;
		try {
			File f1 = new File(inputFilename);
			FileReader fr = new FileReader(f1);
			BufferedReader br = new BufferedReader(fr);
			while ((line = br.readLine()) != null) {
				if (line.contains("_Replace_"))
					line = line.replace(pattern, replacement);
				lines += line + "\n";
			}
			fr.close();
			br.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return lines;
	}
}