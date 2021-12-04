package mapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.stream.Collectors;

import javax.xml.namespace.NamespaceContext;
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
	 * 
	 * @param mtpFilePath File path of the MTP AML file
	 * @return Skill ontology in turtle syntax
	 */
	public String executeMapping(String mtpFilePath) {
		
		System.out.println("Started mapping of " + mtpFilePath);
		long start = System.currentTimeMillis();
		
		// 1. Execute RML mapping
		RmlMapper rmlMapper = new RmlMapper();
		String rmlMappingResult = rmlMapper.executeRmlMapping(mtpFilePath);

		// 2. Create state machines for each procedure
		String stateMachines = createStateMachines(mtpFilePath);

		// 3. Combine rml mapping result with state machines
		String mappingResult = rmlMappingResult + "\n" + stateMachines;
		
		long end = System.currentTimeMillis();
		long mappingDuration = (end - start) / 1000;
		System.out.println("Finished mapping in " + mappingDuration + " seconds.");
		return mappingResult;
	}

	/**
	 * Creates a state machine for every service procedure of the given MTP. Note that in our skill model, every Skill (=MTP procedure) has to have
	 * its own state machine
	 * 
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

		// Check if the default namespace (xmlns) is defined -> If it is, it has to be incorporated into XPath 
		String defaultNs;
		String defaultNsPrefix;
		try {
			// Try to get the default namespace and create a so-called NamspaceContext that will be set to XPath
			// This namespace context consists of prefix (here: x) and the namespace that is retrieved
			defaultNs = doc.getChildNodes().item(0).getAttributes().getNamedItem("xmlns").getNodeValue();
			defaultNsPrefix = "x:";
			
			NamespaceContext ctx = new NamespaceContext() {
				public String getNamespaceURI(String prefix) {
					return prefix.equals("x") ? defaultNs : null;
				}

				public Iterator<Object> getPrefixes(String val) {
					return null;
				}

				public String getPrefix(String uri) {
					return null;
				}
			};
			xpath.setNamespaceContext(ctx);
		} catch (Exception e) {
			// If no namespace can be found, no prefix is needed
			defaultNsPrefix = "";
		}

		XPathExpression expr;
		NodeList skillNodes = null;

		// Create an expression to get all ServiceProcedure / ServiceStrategy (passing in the prefix)
		String xpathExp = String.format("/%1$sCAEXFile/%1$sInstanceHierarchy/%1$sInternalElement[@RefBaseSystemUnitPath='MTPServiceSUCLib/Service']"
				+ "/child::%1$sInternalElement[@RefBaseSystemUnitPath='MTPServiceSUCLib/ServiceProcedure' or @RefBaseSystemUnitPath='MTPServiceSUCLib/ServiceStrategy']/@Name",
				defaultNsPrefix);

		try {
			expr = xpath.compile(xpathExp);
			skillNodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			System.out.println("Error while executing XPath to get all ServiceProcedures");
			e.printStackTrace();
		}
		
		// If no procedure nodes where found, we can stop this because no state machine is needed
		if(skillNodes.getLength() == 0) {
			return "";
		}
		
		// Open the state machine template file
		InputStream stateMachineTemplateStream = this.getClass().getClassLoader().getResourceAsStream(stateMachineTemplateFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(stateMachineTemplateStream));
		String stateMachineTemplate = br.lines().collect(Collectors.joining("\n"));

		// For every procedureNode: Create a customized state machine by replacing the placeholder and add it to the total stateMachin string
		String mappedStateMachines = "";
		for (int i = 0; i < skillNodes.getLength(); i++) {
			String skillName = skillNodes.item(i).getNodeValue();
			String stateMachine = stateMachineTemplate.replaceAll(pattern, skillName);
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