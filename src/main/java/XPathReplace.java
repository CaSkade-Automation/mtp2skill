import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.xpath.*;
import javax.xml.parsers.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class XPathReplace {
	static String inputFilename = "src/main/resources/StateMachineJavaInput.ttl";
	static String outputFilename = "src/main/resources/Output/MappingOutput.ttl";
	static String pattern = "_Replace_";

	public void executeMapping(String xmlFile) {

		// overwrite existing xml file which should be mapped
		writeFile(xmlFile, "./src/main/resources/Manifest_Mischmodul.xml", false);

		// 1. Execute RML mapping
		RmlTest rmlMapper = new RmlTest();
		rmlMapper.executeRmlMapping(outputFilename);

		// 2. Get state machines with replaced names
		String stateMachines = getNodeNameAndValue();

		// 3. Append statemachines to rml mapping
		writeFile(stateMachines, outputFilename, true);
	}

	public void writeFile(String file, String filePath, boolean append) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, append));
			writer.write(file);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getNodeNameAndValue() {

		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();

		domFactory.setNamespaceAware(true);
		DocumentBuilder builder;
		Document doc = null;

		try {
			builder = domFactory.newDocumentBuilder();

			doc = builder.parse("src/main/resources/Manifest_Mischmodul.xml");
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		XPath xpath = XPathFactory.newInstance().newXPath();

		XPathExpression expr;
		Object result = null;

		try {

			expr = xpath.compile(
					"/CAEXFile/InstanceHierarchy/InternalElement[@RefBaseSystemUnitPath='MTPServiceSUCLib/Service']/child::InternalElement[@RefBaseSystemUnitPath='MTPServiceSUCLib/ServiceProcedure']/@Name");

			result = expr.evaluate(doc, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		NodeList nodes = (NodeList) result;

		String stateMachines = "";
		for (int i = 0; i < nodes.getLength(); i++) {
			String nodename = nodes.item(i).getNodeValue();
			stateMachines += ReplaceStateMachine.replaceString(XPathReplace.pattern, nodename,
					XPathReplace.inputFilename); // call
			// term
		}
		return stateMachines;
	}
}