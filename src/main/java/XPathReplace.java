import org.w3c.dom.*;
import javax.xml.xpath.*;
import javax.xml.parsers.*;
import java.io.IOException;
import org.xml.sax.SAXException;

public class XPathReplace {
	static String inputFilename = "src/main/resources/StateMachineJavaInput.ttl";
	static String outputFilenamePattern = "src/main/resources/Output/StateMachineJavaInput_%s.ttl";
	static String pattern = "_Replace_";
	
	public static void main(String[] args){
		 
		 DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		 
		 domFactory.setNamespaceAware(true);
		 DocumentBuilder builder;
		 Document doc = null;
		 
		 try {
			 builder = domFactory.newDocumentBuilder();
			 
			 doc = builder.parse("src/main/resources/Manifest_Mischmodul.xml");
		 }
		 catch (SAXException | IOException | ParserConfigurationException e) {
			 e.printStackTrace();
		 }
		 XPath xpath = XPathFactory.newInstance().newXPath();
		 getNodeNameAndValue(doc, xpath);	 
	 }
	private static void getNodeNameAndValue(Document doc, XPath xpath) {
		
		XPathExpression expr;
		Object result = null;
		
		try {
			
			expr = xpath.compile("/CAEXFile/InstanceHierarchy/InternalElement[@RefBaseSystemUnitPath='MTPServiceSUCLib/Service']/child::InternalElement[@RefBaseSystemUnitPath='MTPServiceSUCLib/ServiceProcedure']/@Name");
			
			 result = expr.evaluate(doc, XPathConstants.NODESET);
		}
		catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		NodeList nodes = (NodeList) result;

		for (int i = 0; i < nodes.getLength(); i++) {
			String nodename = nodes.item(i).getNodeValue();
			String savename = String.format(XPathReplace.outputFilenamePattern, nodename); // Construct output filenames
			ReplaceStateMachine.replaceString(XPathReplace.pattern, nodename, XPathReplace.inputFilename, savename); // call function to replace term
		}
	}
}