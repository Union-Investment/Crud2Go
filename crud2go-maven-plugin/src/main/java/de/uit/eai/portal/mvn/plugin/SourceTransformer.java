package de.uit.eai.portal.mvn.plugin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
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

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;

public class SourceTransformer {

	private static final String CRUD2GO_PORTLET_NAMESPACE = "http://www.unioninvestment.de/eai/portal/crud-portlet";
	private static final String PORTLET_TAG = "portlet";
	private static final String SCRIPT_TAG = "script";

	private static final String SRC_ATTRIBUTE = "src";
	private long newestInputDate;
	private long outputDate;

	public enum RESULT {
		NOT_PORTLET, PORTLET_NO_PROCESSING, PORTLET_PROCESSING_DONE, UP_TO_DATE
	}

	public SourceTransformer() {
	}

	public RESULT processFile(String inputFilePath, String outputFilePath)
			throws IOException {

		InputStreamReader inputReader = createReaderFromFile(inputFilePath);

		StringWriter outputWriter = new StringWriter();

		newestInputDate = new File(inputFilePath).lastModified();
		outputDate = new File(outputFilePath).lastModified();

		RESULT result = transformXMLContent(inputFilePath, inputReader,
				outputWriter);
		switch (result) {
		case NOT_PORTLET:
		case UP_TO_DATE:
			// do nothing
			break;
		case PORTLET_NO_PROCESSING:
			// No Skript tag should be expanded
			if (outputDate < newestInputDate) {
				FileUtils.copyFile(new File(inputFilePath), new File(
						outputFilePath));
			} else {
				return RESULT.UP_TO_DATE;
			}
			break;
		case PORTLET_PROCESSING_DONE:
			if (outputDate < newestInputDate) {
				FileWriter outputFileWriter = new FileWriter(outputFilePath);
				try {
					IOUtils.write(outputWriter.toString(), outputFileWriter);
				} finally {
					outputFileWriter.close();
				}
			} else {
				return RESULT.UP_TO_DATE;
			}

			break;
		}
		return result;
	}

	/**
	 * Respect BOM if existing.
	 * 
	 * @param inputFilePath
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	InputStreamReader createReaderFromFile(String inputFilePath)
			throws FileNotFoundException, IOException,
			UnsupportedEncodingException {
		String defaultEncoding = "UTF-8";
		InputStream inputStream = new FileInputStream(inputFilePath);
		BOMInputStream bOMInputStream = new BOMInputStream(inputStream);
		ByteOrderMark bom = bOMInputStream.getBOM();
		String charsetName = bom == null ? defaultEncoding : bom
				.getCharsetName();
		InputStreamReader inputReader = new InputStreamReader(
				new BufferedInputStream(bOMInputStream), charsetName);
		return inputReader;
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

			boolean insertScriptContent = false;
			String scriptContent = null;

			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				if (event.isStartElement()) {
					StartElement element = event.asStartElement();
					QName elementQName = element.getName();
					String nameLocalPart = elementQName.getLocalPart();
					if (PORTLET_TAG.equals(nameLocalPart)
							&& CRUD2GO_PORTLET_NAMESPACE.equals(elementQName
									.getNamespaceURI())) {
						result = RESULT.PORTLET_NO_PROCESSING;
					}
					if (result != RESULT.NOT_PORTLET
							&& SCRIPT_TAG.equals(nameLocalPart)) {
						String value = getSrcAttribute(element);
						if (value != null) {
							insertScriptContent = true;
							scriptContent = getSourceContent(basePath, value);
							result = RESULT.PORTLET_PROCESSING_DONE;
						}
					}
				} else if (event.isEndElement()) {
					EndElement element = event.asEndElement();
					QName elementQName = element.getName();
					String nameLocalPart = elementQName.getLocalPart();
					if (SCRIPT_TAG.equals(nameLocalPart)) {
						if (insertScriptContent) {
							insertScriptContent = false;

							XMLEvent cdata = eventFactory
									.createCData(MessageFormat.format(
											"{0}\r\n", scriptContent));

							eventWriter.add(cdata);
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
			outputWriter.flush();
			return result;
		} catch (javax.xml.stream.XMLStreamException e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	String readFileToString(String path) throws IOException {
		File file = new File(path);
		if (!file.exists()) {
			throw new IOException("Path does not exists");
		}
		newestInputDate = Math.max(newestInputDate, file.lastModified());

		InputStream inputStream = new FileInputStream(path);
		BOMInputStream bOMInputStream = new BOMInputStream(inputStream);
		return IOUtils.toString(bOMInputStream);
	}

	private String getSourceContent(String basePath, String includePath)
			throws IOException {

		String absolutePath = constructAbsolutePath(basePath, includePath);
		return readFileToString(absolutePath);
	}

	public static String constructAbsolutePath(String basePath,
			String includePath) throws IOException {
		String result;
		if (includePath.startsWith("/") || includePath.contains(":")) {
			result = includePath;
		} else {
			String baseDir = "";
			if (new File(basePath).isDirectory()) {
				baseDir = basePath;
				if (!baseDir.endsWith(File.separator)) {
					baseDir += File.separator;
				}
			} else {

				int lastIndexOf = basePath.lastIndexOf(File.separator);
				int slashLastIndex = basePath.lastIndexOf("/");
				lastIndexOf = lastIndexOf > slashLastIndex ? lastIndexOf
						: slashLastIndex;

				if (lastIndexOf != -1) {
					baseDir = basePath.substring(0, lastIndexOf + 1);
				}
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