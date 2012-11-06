/**
 * CompanyServiceSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.liferay.portal.service.http;

public interface CompanyServiceSoap extends java.rmi.Remote {
    public com.liferay.portal.model.CompanySoap addCompany(java.lang.String webId, java.lang.String virtualHost, java.lang.String mx, java.lang.String shardName, boolean system, int maxUsers, boolean active) throws java.rmi.RemoteException;
    public void deleteLogo(long companyId) throws java.rmi.RemoteException;
    public com.liferay.portal.model.CompanySoap getCompanyById(long companyId) throws java.rmi.RemoteException;
    public com.liferay.portal.model.CompanySoap getCompanyByLogoId(long logoId) throws java.rmi.RemoteException;
    public com.liferay.portal.model.CompanySoap getCompanyByMx(java.lang.String mx) throws java.rmi.RemoteException;
    public com.liferay.portal.model.CompanySoap getCompanyByVirtualHost(java.lang.String virtualHost) throws java.rmi.RemoteException;
    public com.liferay.portal.model.CompanySoap getCompanyByWebId(java.lang.String webId) throws java.rmi.RemoteException;
    public void removePreferences(long companyId, java.lang.String[] keys) throws java.rmi.RemoteException;
    public com.liferay.portal.model.CompanySoap updateCompany(long companyId, java.lang.String virtualHost, java.lang.String mx, int maxUsers, boolean active) throws java.rmi.RemoteException;
    public com.liferay.portal.model.CompanySoap updateCompany(long companyId, java.lang.String virtualHost, java.lang.String mx, java.lang.String homeURL, java.lang.String name, java.lang.String legalName, java.lang.String legalId, java.lang.String legalType, java.lang.String sicCode, java.lang.String tickerSymbol, java.lang.String industry, java.lang.String type, java.lang.String size) throws java.rmi.RemoteException;
    public void updateDisplay(long companyId, java.lang.String languageId, java.lang.String timeZoneId) throws java.rmi.RemoteException;
    public void updateSecurity(long companyId, java.lang.String authType, boolean autoLogin, boolean sendPassword, boolean strangers, boolean strangersWithMx, boolean strangersVerify, boolean siteLogo) throws java.rmi.RemoteException;
}
