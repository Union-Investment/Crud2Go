package de.uit.eai.portal.mvn.plugin;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class SourceTransformer {

	private static final String CRUD2GO_PORTLET_NAMESPACE = "http://www.unioninvestment.de/eai/portal/crud-portlet";
	private static final String PORTLET_TAG = "portlet";
	private static final String SCRIPT_TAG = "script";

	private static final String SRC_ATTRIBUTE = "src";

	public enum RESULT {
		NOT_PORTLET, PORTLET_NO_PROCESSING, PORTLET_PROCESSING_DONE;
	}

	public SourceTransformer() {
	}

	public RESULT processFile(String inputFilePath, String outputFilePath)
			throws IOException {
		FileReader inputReader = new FileReader(inputFilePath);
		StringWriter outputWriter = new StringWriter();
		
		RESULT result = transformXMLContent(inputFilePath, inputReader, outputWriter);
		switch(result){
		case NOT_PORTLET:
			//FALLTHROUGH: do nothing
			break;
		case PORTLET_NO_PROCESSING:
            //No Skript tag should be expanded
			FileUtils.copyFile(new File(inputFilePath), new File(outputFilePath));
            break;
        case PORTLET_PROCESSING_DONE:
            FileWriter outputFileWriter = new FileWriter(outputFilePath);
            IOUtils.write(outputWriter.toString(), outputFileWriter);
        }
		return result;
	}

	public RESULT transformXMLContent(String basePath, Reader inputReader,
			Writer outputWriter) throws FactoryConfigurationError, IOException {
		RESULT result = RESULT.NOT_PORTLET;
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		inputFactory.setProperty(
				"http://java.sun.com/xml/stream/properties/report-cdata-event",
				Boolean.TRUE);

		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		// get the XMLInputFactory and XMLOutputFactory objects

		XMLEventFactory eventFactory = XMLEventFactory.newInstance();

		XMLEventReader eventReader;
		XMLEventWriter eventWriter;

		// get the XMLEventReader and XMLEventWriter objects
		try {

			eventReader = inputFactory.createXMLEventReader(inputReader);
			eventWriter = outputFactory.createXMLEventWriter(outputWriter);

			boolean skipClosingScriptElenent = false;

			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				if (event.isStartElement()) {
					StartElement element = event.asStartElement();
					QName elementQName = element.getName();
					String nameLocalPart = elementQName.getLocalPart();
					if (PORTLET_TAG.equalsIgnoreCase(nameLocalPart)
							&& CRUD2GO_PORTLET_NAMESPACE.equals(elementQName
									.getNamespaceURI())) {
						result = RESULT.PORTLET_NO_PROCESSING;
					}
					if (result != RESULT.NOT_PORTLET
							&& SCRIPT_TAG.equalsIgnoreCase(nameLocalPart)) {
						String value = getSrcAttribute(element);
						if (value != null) {
							event = null;
							skipClosingScriptElenent = true;
							XMLEvent newScriptTag = eventFactory
									.createStartElement(
											elementQName.getPrefix(),
											elementQName.getNamespaceURI(),
											elementQName.getLocalPart());
							eventWriter.add(newScriptTag);
							XMLEvent cdata = eventFactory
									.createCData(MessageFormat.format(
											"\r\n{0}\r\n",
											getSourceContent(basePath, value)));

							eventWriter.add(cdata);
							XMLEvent newEndScriptTag = eventFactory
									.createEndElement(elementQName.getPrefix(),
											elementQName.getNamespaceURI(),
											elementQName.getLocalPart());
							eventWriter.add(newEndScriptTag);
							result = RESULT.PORTLET_PROCESSING_DONE;
						}
					}
				} else if (event.isEndElement()) {
					EndElement element = event.asEndElement();
					QName elementQName = element.getName();
					String nameLocalPart = elementQName.getLocalPart();
					if (SCRIPT_TAG.equalsIgnoreCase(nameLocalPart)) {
						if (skipClosingScriptElenent) {
							skipClosingScriptElenent = false;
							event = null;
						}
					}
				}
				if (event != null) {
					eventWriter.add(event);
				}
			}
			// clean up
			eventWriter.close();
			eventReader.close();
			return result;
		} catch (javax.xml.stream.XMLStreamException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	static String readFileToString(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
			throw new IOException("Path does not exists");
		}
        return IOUtils.toString(new FileReader(file));
//		File file = new File(path);
//		if (!file.exists()) {
//			throw new IOException("Path does not exists");
//		}
//		String line = null;
//		StringBuilder result = new StringBuilder();
//		LineNumberReader reader = null;
//		try {
//			reader = new LineNumberReader(new FileReader(file));
//			String separator = System.getProperty("line.separator");
//			while ((line = reader.readLine()) != null) {
//				result.append(line);
//				result.append(separator);
//			}
//
//			return result.toString();
//		} finally {
//			if (reader != null) {
//				try {
//					reader.close();
//				} catch (IOException e) {
//					// TODO: Log
//					e.printStackTrace();
//				}
//			}
//		}
	}

	private String getSourceContent(String basePath, String includePath)
			throws IOException {

		String absolutePath = constructAbsolutePath(basePath, includePath);
		return readFileToString(absolutePath);
	}

	public static String constructAbsolutePath(String basePath,
			String includePath) throws IOException {
		String result = null;
		if (includePath.startsWith("/") || includePath.contains(":")) {
			result = includePath;
		} else {
			int lastIndexOf = basePath.lastIndexOf(File.separator);
			int slashLastIndex = basePath.lastIndexOf("/");
			lastIndexOf = lastIndexOf > slashLastIndex ? lastIndexOf
					: slashLastIndex;
			String baseDir = "";
			if (lastIndexOf != -1) {
				baseDir = basePath.substring(0, lastIndexOf + 1);
			}
			result = baseDir + includePath;
		}
		return new File(result).getCanonicalPath();
	}

	private String getSrcAttribute(StartElement element) {
		@SuppressWarnings("unchecked")
		Iterator<Attribute> attributes = element.getAttributes();
		while (attributes.hasNext()) {
			Attribute attribute = attributes.next();
			String attributeName = attribute.getName().getLocalPart();
			if (attributeName.equals(SRC_ATTRIBUTE)) {
				return attribute.getValue();
			}
		}
		return null;
	}

	public static String getRelativePath(File sourceDirectory,
			File fileToProcess) {
		return sourceDirectory.toURI().relativize(fileToProcess.toURI())
				.getPath();
	}

}
