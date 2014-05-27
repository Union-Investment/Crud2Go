package de.unioninvestment.eai.portal.portlet.crud.domain.util;

import java.io.IOException;
import java.util.Properties;

public class MimetypeRegistry {

	static final Properties props = new Properties();

	static {
		try {
			props.load(MimetypeRegistry.class
					.getResourceAsStream("mimetype.properties"));

		} catch (IOException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public String detectFromFilename(String fileName) {
		int dotPos = fileName.lastIndexOf('.');
		if (dotPos >= 0) {
			String extension = fileName.substring(dotPos + 1).toLowerCase();
			synchronized (props) {
				String mimetype = props.getProperty(extension);
				if (mimetype != null) {
					return mimetype;
				}
			}
		}
		return "application/octet-stream";
	}

}
