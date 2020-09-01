import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.List;


public class ReplaceStateMachine {
    static List<String> lines = new ArrayList<>();
    static String line = null;

    public static void replaceString(String pattern, String replacement, String inputFilename, String outputFilename) {
    	 List<String> lines = new ArrayList<>();
         String line = null;
    	try {
            File f1 = new File(inputFilename);
            FileReader fr = new FileReader(f1);
            BufferedReader br = new BufferedReader(fr);
            while ((line = br.readLine()) != null) {
                if (line.contains("_Replace_"))
                    line = line.replace(pattern, replacement);
                lines.add(line);
            }
            fr.close();
            br.close();

            File outFile = new File(outputFilename);
            FileWriter fw = new FileWriter(outFile);
            BufferedWriter out = new BufferedWriter(fw);
            for(String s : lines)
                out.write(s.concat("\n"));
            out.flush();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}