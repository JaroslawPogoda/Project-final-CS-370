import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Merge {
	public static void mergeXML(String fName1, String fName2) {

		String path = ".." + File.separator + "src" + File.separator;
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		Document doc = null;
		Document doc2 = null;
		
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy-h-mm-ssa");
		String formattedDate = sdf.format(date);
		
		File file1 = null;
		File file2 = null;
		
		try {
			file1 = new File(path + fName1);
			file2 = new File(path + fName2);

			db = dbf.newDocumentBuilder();
			doc = db.parse(file1);
			doc2 = db.parse(file2);

			NodeList ndListFirstFile = doc.getElementsByTagName("tbody");

			for (int i = 0; i < doc2.getElementsByTagName("title").getLength(); i++) {
				Node nodeArea = doc2.getElementsByTagName("title").item(i);

				if (nodeArea.getFirstChild().getTextContent().equals("Medications")) {
					Node tbody = nodeArea.getNextSibling().getNextSibling().getFirstChild().getNextSibling()
							.getFirstChild().getNextSibling().getNextSibling().getNextSibling();

					Node node = doc.importNode(tbody, true);

					/*
					 * Hardcoded 'ndListFirstFile.item(1)' - can't seem to grab
					 * it otherwise.
					 * System.out.println(ndListFirstFile.item(1).getParentNode(
					 * ).getParentNode().getPreviousSibling()
					 * .getPreviousSibling().getFirstChild().getTextContent());
					 * 
					 * Code above verifies where the merged XML should go...
					 * gives null if ran in if statement.
					 */

					ndListFirstFile.item(1).appendChild(node);

					String xml = (tbody.getTextContent().replaceAll("\\t", "") + ndListFirstFile.item(1)
							.getTextContent().substring(ndListFirstFile.item(1).getTextContent().indexOf('\n') + 1)
							.replaceAll("\\t", ""));

					xml = xml.replaceAll("(?m)^[ \t]*\r?\n", "");

					ArrayList<String> lines = new ArrayList<String>(Arrays.asList(xml.split("\\r?\\n")));

					// new ToJSON(lines);
				}
			}

			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new StringWriter());
			transformer.transform(source, result);

			FileWriter file3 = new FileWriter(path + fName1.substring(0, fName1.lastIndexOf(".")) + "&"
					+ fName2.substring(0, fName2.lastIndexOf(".")) + "-MERGED-" + formattedDate + ".xml");
			
			Writer output = new BufferedWriter(file3);

			String xmlOutput = result.getWriter().toString();

			output.write(format(xmlOutput));
			output.close();
			
			System.out.println("FILE WRITTEN TO: " + path + fName1.substring(0, fName1.lastIndexOf(".")) + "&"
					+ fName2.substring(0, fName2.lastIndexOf(".")) + "-MERGED-" + formattedDate + ".xml");

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	public static String innerXml(Node node) {
		DOMImplementationLS lsImpl = (DOMImplementationLS) node.getOwnerDocument().getImplementation().getFeature("LS",
				"3.0");
		LSSerializer lsSerializer = lsImpl.createLSSerializer();
		lsSerializer.getDomConfig().setParameter("xml-declaration", false);
		NodeList childNodes = node.getChildNodes();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < childNodes.getLength(); i++) {
			sb.append(lsSerializer.writeToString(childNodes.item(i)));
		}
		return sb.toString();
	}

	public static String format(String xml) {
		try {
			final InputSource src = new InputSource(new StringReader(xml));
			final Node document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src)
					.getDocumentElement();
			final Boolean keepDeclaration = Boolean.valueOf(xml.startsWith("<?xml"));

			final DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
			final DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
			final LSSerializer writer = impl.createLSSerializer();

			writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
			writer.getDomConfig().setParameter("xml-declaration", keepDeclaration);

			return writer.writeToString(document);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
