package mapping;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class MtpToSkillMapper {
	static String stateMachineTemplateFile = "StateMachineTemplate.ttl";
	static String pattern = "_Replace_";


	/**
	 * Maps an MTP file with a given file path to the ontological skill model
	 * @param mtpFilePath File path of the MTP AML file
	 * @return Skill ontology in turtle syntax
	 */
	public String executeMapping(String mtpFilePath) {

		// 1. Execute RML mapping
		RmlMapper rmlMapper = new RmlMapper();
		String rmlMappingResult = rmlMapper.executeRmlMapping(mtpFilePath);

		// 2. Create state machines for each procedure
		String stateMachines = createStateMachines(mtpFilePath);

		// 3. Combine rml mapping result with state machines
		String mappingResult = rmlMappingResult + "\n" + stateMachines;
		return mappingResult;
	}

	
	/**
	 * Creates a state machine for every service procedure of the given MTP. Note that in our skill model, every Skill (=MTP procedure) has to have its own state machine
	 * @param mtpFilePath mtpFilePath File path of the MTP AML file
	 * @return A string in turtle syntax containing all state machines for this MTP
	 */
	private String createStateMachines(String mtpFilePath) {

		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true);
		DocumentBuilder builder;
		Document doc = null;

		// Open the MTP file
		try {
			builder = domFactory.newDocumentBuilder();
			doc = builder.parse(mtpFilePath);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}

		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression expr;
		Object result = null;

		// Evaluate an XPath expression to get all ServiceProcedure / ServiceStrategy
		try {
			expr = xpath.compile("/CAEXFile/InstanceHierarchy/InternalElement[@RefBaseSystemUnitPath='MTPServiceSUCLib/Service']"
					+ "/child::InternalElement[@RefBaseSystemUnitPath='MTPServiceSUCLib/ServiceProcedure' or @RefBaseSystemUnitPath='MTPServiceSUCLib/ServiceStrategy']/@Name");

			result = expr.evaluate(doc, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			System.out.println("Error while executing XPath to get all ServiceProcedures");
			e.printStackTrace();
		}
		NodeList procedureNodes = (NodeList) result;

		// Open the state machine template file
		InputStream stateMachineTemplateStream = this.getClass().getClassLoader().getResourceAsStream(stateMachineTemplateFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(stateMachineTemplateStream));
		String stateMachineTemplate = br.lines().collect(Collectors.joining("\n"));

		// For every procedureNode: Create a customized state machine by replacing the placeholder and add it to the total stateMachin string
		String mappedStateMachines = "";
		for (int i = 0; i < procedureNodes.getLength(); i++) {
			String nodeName = procedureNodes.item(i).getNodeValue();
			String stateMachine = stateMachineTemplate.replaceAll(pattern, nodeName);
			mappedStateMachines += stateMachine;
		}
		
		try {
			br.close();
		} catch (IOException e) {
			System.out.println("Error while closing the file");
			e.printStackTrace();
		}
		
		return mappedStateMachines;
	}

}