import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import be.ugent.rml.Executor;
import be.ugent.rml.Utils;
import be.ugent.rml.functions.FunctionLoader;
import be.ugent.rml.records.RecordsFactory;
import be.ugent.rml.store.QuadStore;
import be.ugent.rml.store.QuadStoreFactory;
import be.ugent.rml.store.RDF4JStore;

public class RmlTest {

	public void executeRmlMapping(String rmlOutputFilePath) {
		try {
			String mapPath = "./src/main/resources/mapping.ttl"; // path to the mapping file that needs to be executed
			File mappingFile = new File(mapPath);

			// Get the mapping string stream
			InputStream mappingStream = new FileInputStream(mappingFile);

			// Load the mapping in a QuadStore
			QuadStore rmlStore = QuadStoreFactory.read(mappingStream);

			// Set up the basepath for the records factory, i.e., the basepath for the
			// (local file) data sources
			RecordsFactory factory = new RecordsFactory(mappingFile.getParent());

			// Set up the functions used during the mapping
			Map<String, Class> libraryMap = new HashMap<>();

			FunctionLoader functionLoader = new FunctionLoader(null, libraryMap);

			// Set up the outputstore (needed when you want to output something else than
			// nquads
			QuadStore outputStore = new RDF4JStore();

			// Create the Executor
			Executor executor = new Executor(rmlStore, factory, functionLoader, outputStore,
					Utils.getBaseDirectiveTurtle(mappingStream));

			// Execute the mapping
			QuadStore result = executor.execute(null);

			// Output the result
			File outFile = new File(rmlOutputFilePath);
			FileWriter fw = new FileWriter(outFile);
			BufferedWriter out = new BufferedWriter(fw);
			result.write(out, "turtle");

			out.flush();
			out.close();

			out.close();
		} catch (Exception e) {
			System.out.println("Error happend" + e.toString());
		}
	}

}
