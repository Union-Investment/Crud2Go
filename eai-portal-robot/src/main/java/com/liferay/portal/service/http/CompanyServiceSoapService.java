/**
 * CompanyServiceSoapService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.liferay.portal.service.http;

public interface CompanyServiceSoapService extends javax.xml.rpc.Service {
    public java.lang.String getPortal_CompanyServiceAddress();

    public com.liferay.portal.service.http.CompanyServiceSoap getPortal_CompanyService() throws javax.xml.rpc.ServiceException;

    public com.liferay.portal.service.http.CompanyServiceSoap getPortal_CompanyService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
