/**
 * ResourcePermissionServiceSoapServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.liferay.portal.service.http;

public class ResourcePermissionServiceSoapServiceLocator extends org.apache.axis.client.Service implements com.liferay.portal.service.http.ResourcePermissionServiceSoapService {

    public ResourcePermissionServiceSoapServiceLocator() {
    }


    public ResourcePermissionServiceSoapServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ResourcePermissionServiceSoapServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for Portal_ResourcePermissionService
    private java.lang.String Portal_ResourcePermissionService_address = "http://localhost:8080/api/axis/Portal_ResourcePermissionService";

    public java.lang.String getPortal_ResourcePermissionServiceAddress() {
        return Portal_ResourcePermissionService_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String Portal_ResourcePermissionServiceWSDDServiceName = "Portal_ResourcePermissionService";

    public java.lang.String getPortal_ResourcePermissionServiceWSDDServiceName() {
        return Portal_ResourcePermissionServiceWSDDServiceName;
    }

    public void setPortal_ResourcePermissionServiceWSDDServiceName(java.lang.String name) {
        Portal_ResourcePermissionServiceWSDDServiceName = name;
    }

    public com.liferay.portal.service.http.ResourcePermissionServiceSoap getPortal_ResourcePermissionService() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(Portal_ResourcePermissionService_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getPortal_ResourcePermissionService(endpoint);
    }

    public com.liferay.portal.service.http.ResourcePermissionServiceSoap getPortal_ResourcePermissionService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.liferay.portal.service.http.Portal_ResourcePermissionServiceSoapBindingStub _stub = new com.liferay.portal.service.http.Portal_ResourcePermissionServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getPortal_ResourcePermissionServiceWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setPortal_ResourcePermissionServiceEndpointAddress(java.lang.String address) {
        Portal_ResourcePermissionService_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.liferay.portal.service.http.ResourcePermissionServiceSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                com.liferay.portal.service.http.Portal_ResourcePermissionServiceSoapBindingStub _stub = new com.liferay.portal.service.http.Portal_ResourcePermissionServiceSoapBindingStub(new java.net.URL(Portal_ResourcePermissionService_address), this);
                _stub.setPortName(getPortal_ResourcePermissionServiceWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("Portal_ResourcePermissionService".equals(inputPortName)) {
            return getPortal_ResourcePermissionService();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("urn:http.service.portal.liferay.com", "ResourcePermissionServiceSoapService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("urn:http.service.portal.liferay.com", "Portal_ResourcePermissionService"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("Portal_ResourcePermissionService".equals(portName)) {
            setPortal_ResourcePermissionServiceEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
