/**
 * RoleServiceSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.liferay.portal.service.http;

public interface RoleServiceSoap extends java.rmi.Remote {
    public com.liferay.portal.model.RoleSoap addRole(java.lang.String name, java.lang.String[] titleMapLanguageIds, java.lang.String[] titleMapValues, java.lang.String[] descriptionMapLanguageIds, java.lang.String[] descriptionMapValues, int type) throws java.rmi.RemoteException;
    public void addUserRoles(long userId, long[] roleIds) throws java.rmi.RemoteException;
    public void deleteRole(long roleId) throws java.rmi.RemoteException;
    public com.liferay.portal.model.RoleSoap[] getGroupRoles(long groupId) throws java.rmi.RemoteException;
    public com.liferay.portal.model.RoleSoap getRole(long roleId) throws java.rmi.RemoteException;
    public com.liferay.portal.model.RoleSoap getRole(long companyId, java.lang.String name) throws java.rmi.RemoteException;
    public com.liferay.portal.model.RoleSoap[] getUserGroupGroupRoles(long userId, long groupId) throws java.rmi.RemoteException;
    public com.liferay.portal.model.RoleSoap[] getUserGroupRoles(long userId, long groupId) throws java.rmi.RemoteException;
    public com.liferay.portal.model.RoleSoap[] getUserRelatedRoles(long userId, com.liferay.portal.model.GroupSoap[] groups) throws java.rmi.RemoteException;
    public com.liferay.portal.model.RoleSoap[] getUserRoles(long userId) throws java.rmi.RemoteException;
    public boolean hasUserRole(long userId, long companyId, java.lang.String name, boolean inherited) throws java.rmi.RemoteException;
    public boolean hasUserRoles(long userId, long companyId, java.lang.String[] names, boolean inherited) throws java.rmi.RemoteException;
    public void unsetUserRoles(long userId, long[] roleIds) throws java.rmi.RemoteException;
    public com.liferay.portal.model.RoleSoap updateRole(long roleId, java.lang.String name, java.lang.String[] titleMapLanguageIds, java.lang.String[] titleMapValues, java.lang.String[] descriptionMapLanguageIds, java.lang.String[] descriptionMapValues, java.lang.String subtype) throws java.rmi.RemoteException;
}
