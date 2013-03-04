package de.unioninvestment.eai.portal.portlet.crud.scripting.domain.container.rest;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;

/**
 * Parser interface. Implmementations extract tabular data from the HTTP
 * response of a ReST request.
 * 
 * @author carsten.mjartan
 */
public interface PayloadParser {
	/**
	 * @param response
	 *            the ReST response
	 * @return data that is needed by GenericContainer.
	 * @throws IOException
	 *             propagated if thrown by a subroutine
	 */
	List<Object[]> getRows(HttpResponse response) throws IOException;

}
