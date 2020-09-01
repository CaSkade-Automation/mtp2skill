import org.w3c.dom.*;
import javax.xml.xpath.*;
import javax.xml.parsers.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import org.xml.sax.SAXException;

public class XPathReplace {
	static String inputFilename = "src/main/resources/StateMachineJavaInput.ttl";
	static String outputFilename = "src/main/resources/Output/MappingOutput.ttl";
	static String pattern = "_Replace_";

	public static void main(String[] args) {

		// 1. Execute RML mapping
		RmlTest rmlMapper = new RmlTest();
		rmlMapper.executeRmlMapping(outputFilename);
		
		// 2. Get state machines with replaced names
		String stateMachines = getNodeNameAndValue();
		
		// 3. Append statemachines to rml mapping
		 try { 
			  
	            // Open given file in append mode. 
	            BufferedWriter out = new BufferedWriter( 
	                   new FileWriter(outputFilename, true)); 
	            out.write(stateMachines); 
	            out.close(); 
	        } 
	        catch (IOException e) { 
	            System.out.println("exception occoured" + e); 
	        } 
	}

	private static String getNodeNameAndValue() {

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
			stateMachines += ReplaceStateMachine.replaceString(XPathReplace.pattern, nodename, XPathReplace.inputFilename); // call
																											// term
		}
		return stateMachines;
	}
}