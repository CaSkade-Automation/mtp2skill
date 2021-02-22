package mapping;

import org.jline.utils.InputStreamReader;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.xpath.*;
import javax.xml.parsers.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class Mapping {
	static String inputStatemachineFilename = "StateMachineJavaInput.ttl";
	static String outputFilename = "MappingOutput.ttl";
	static String pattern = "_Replace_";

	public void executeMapping(String xmlFilePath) {

		// 1. Execute RML mapping
		RmlMapping rmlMapper = new RmlMapping();
		rmlMapper.executeRmlMapping(outputFilename, xmlFilePath);

		// 2. Get state machines with replaced names
		String stateMachines = getProcedureNames(xmlFilePath);

		// 3. Append state machines to rml mapping
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

	private String getProcedureNames(String xmlFile) {

		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();

		domFactory.setNamespaceAware(true);
		DocumentBuilder builder;
		Document doc = null;

		try {
			builder = domFactory.newDocumentBuilder();

			doc = builder.parse(xmlFile);
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

		InputStream inputFile = this.getClass().getClassLoader().getResourceAsStream(inputStatemachineFilename);

		for (int i = 0; i < nodes.getLength(); i++) {
			String nodename = nodes.item(i).getNodeValue();
			stateMachines += replaceString(pattern, nodename, inputFile); // call term
		}
		return stateMachines;
	}

	public String replaceString(String pattern, String replacement, InputStream inputFile) {

		BufferedReader br = new BufferedReader(new InputStreamReader(inputFile));
		String currentline = "";
		String lines = "";
		try {
			while ((currentline = br.readLine()) != null) {
				currentline = currentline.replace(pattern, replacement);
				lines += currentline + "\n";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lines;
	}
}