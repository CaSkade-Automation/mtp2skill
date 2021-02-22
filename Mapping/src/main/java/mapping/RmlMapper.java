package mapping;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import be.ugent.rml.Executor;
import be.ugent.rml.Utils;
import be.ugent.rml.functions.FunctionLoader;
import be.ugent.rml.records.RecordsFactory;
import be.ugent.rml.store.Quad;
import be.ugent.rml.store.QuadStore;
import be.ugent.rml.store.QuadStoreFactory;
import be.ugent.rml.store.RDF4JStore;
import be.ugent.rml.term.Literal;
import be.ugent.rml.term.NamedNode;
import be.ugent.rml.term.Term;

public class RmlMapping {

	static String mappingFilePath = "mapping.ttl";

	public void executeRmlMapping(String rmlOutputFilePath, String xmlFilePath) {

		// mapping file that needs to be executed
		String userDirectory = System.getProperty("user.dir");
		File mappingFile = new File(userDirectory + "\\" + mappingFilePath);
		try {

			URL resource = this.getClass().getClassLoader().getResource(mappingFilePath);
			InputStream mappingStream = resource.openStream();

			// Get the mapping string stream
			// InputStream mappingStream = new FileInputStream(mappingFile);

			// Load the mapping in a QuadStore
			QuadStore rmlStore = QuadStoreFactory.read(mappingStream);

			Term predicate = new NamedNode("http://semweb.mmlab.be/ns/rml#source");
			Term object = new Literal("${XMLFileToMap}");

			List<Quad> quads = rmlStore.getQuads(null, predicate, object);
			List<Term> subjects = new ArrayList<Term>();
			for (Quad quad : quads) {
				subjects.add(quad.getSubject());
			}

			rmlStore.removeQuads(null, predicate, object);

			Term newObject = new Literal(xmlFilePath);
			for (Term subject : subjects) {
				rmlStore.addQuad(subject, predicate, newObject);
			}

			// Set up the basepath for the records factory, i.e., the basepath for the
			// (local file) data sources
			RecordsFactory factory = new RecordsFactory(mappingFile.getParent());

			// Set up the functions used during the mapping
			Map<String, Class> libraryMap = new HashMap<>();

			FunctionLoader functionLoader = new FunctionLoader(null, libraryMap);

			// Set up the outputstore (needed when you want to output something else than
			// nquads)
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
		} catch (Exception e) {
			System.out.println("Error happend" + e.toString());
		}
	}
}
