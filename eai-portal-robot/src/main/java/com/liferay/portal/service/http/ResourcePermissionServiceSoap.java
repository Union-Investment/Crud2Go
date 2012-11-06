/**
 * ResourcePermissionServiceSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.liferay.portal.service.http;

public interface ResourcePermissionServiceSoap extends java.rmi.Remote {
    public void addResourcePermission(long groupId, long companyId, java.lang.String name, int scope, java.lang.String primKey, long roleId, java.lang.String actionId) throws java.rmi.RemoteException;
    public void removeResourcePermission(long groupId, long companyId, java.lang.String name, int scope, java.lang.String primKey, long roleId, java.lang.String actionId) throws java.rmi.RemoteException;
    public void removeResourcePermissions(long groupId, long companyId, java.lang.String name, int scope, long roleId, java.lang.String actionId) throws java.rmi.RemoteException;
    public void setIndividualResourcePermissions(long groupId, long companyId, java.lang.String name, java.lang.String primKey, long roleId, java.lang.String[] actionIds) throws java.rmi.RemoteException;
}
