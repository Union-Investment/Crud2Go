package de.unioninvestment.eai.portal.portlet.crud.scripting.domain.container.rest;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;

public interface PayloadParser {
	List<Object[]> getRows(HttpResponse response) throws IOException;

}
