/**
 * CompanySoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.liferay.portal.model;

public class CompanySoap  implements java.io.Serializable {
    private long accountId;

    private boolean active;

    private long companyId;

    private java.lang.String homeURL;

    private java.lang.String key;

    private long logoId;

    private int maxUsers;

    private java.lang.String mx;

    private long primaryKey;

    private boolean system;

    private java.lang.String webId;

    public CompanySoap() {
    }

    public CompanySoap(
           long accountId,
           boolean active,
           long companyId,
           java.lang.String homeURL,
           java.lang.String key,
           long logoId,
           int maxUsers,
           java.lang.String mx,
           long primaryKey,
           boolean system,
           java.lang.String webId) {
           this.accountId = accountId;
           this.active = active;
           this.companyId = companyId;
           this.homeURL = homeURL;
           this.key = key;
           this.logoId = logoId;
           this.maxUsers = maxUsers;
           this.mx = mx;
           this.primaryKey = primaryKey;
           this.system = system;
           this.webId = webId;
    }


    /**
     * Gets the accountId value for this CompanySoap.
     * 
     * @return accountId
     */
    public long getAccountId() {
        return accountId;
    }


    /**
     * Sets the accountId value for this CompanySoap.
     * 
     * @param accountId
     */
    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }


    /**
     * Gets the active value for this CompanySoap.
     * 
     * @return active
     */
    public boolean isActive() {
        return active;
    }


    /**
     * Sets the active value for this CompanySoap.
     * 
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }


    /**
     * Gets the companyId value for this CompanySoap.
     * 
     * @return companyId
     */
    public long getCompanyId() {
        return companyId;
    }


    /**
     * Sets the companyId value for this CompanySoap.
     * 
     * @param companyId
     */
    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }


    /**
     * Gets the homeURL value for this CompanySoap.
     * 
     * @return homeURL
     */
    public java.lang.String getHomeURL() {
        return homeURL;
    }


    /**
     * Sets the homeURL value for this CompanySoap.
     * 
     * @param homeURL
     */
    public void setHomeURL(java.lang.String homeURL) {
        this.homeURL = homeURL;
    }


    /**
     * Gets the key value for this CompanySoap.
     * 
     * @return key
     */
    public java.lang.String getKey() {
        return key;
    }


    /**
     * Sets the key value for this CompanySoap.
     * 
     * @param key
     */
    public void setKey(java.lang.String key) {
        this.key = key;
    }


    /**
     * Gets the logoId value for this CompanySoap.
     * 
     * @return logoId
     */
    public long getLogoId() {
        return logoId;
    }


    /**
     * Sets the logoId value for this CompanySoap.
     * 
     * @param logoId
     */
    public void setLogoId(long logoId) {
        this.logoId = logoId;
    }


    /**
     * Gets the maxUsers value for this CompanySoap.
     * 
     * @return maxUsers
     */
    public int getMaxUsers() {
        return maxUsers;
    }


    /**
     * Sets the maxUsers value for this CompanySoap.
     * 
     * @param maxUsers
     */
    public void setMaxUsers(int maxUsers) {
        this.maxUsers = maxUsers;
    }


    /**
     * Gets the mx value for this CompanySoap.
     * 
     * @return mx
     */
    public java.lang.String getMx() {
        return mx;
    }


    /**
     * Sets the mx value for this CompanySoap.
     * 
     * @param mx
     */
    public void setMx(java.lang.String mx) {
        this.mx = mx;
    }


    /**
     * Gets the primaryKey value for this CompanySoap.
     * 
     * @return primaryKey
     */
    public long getPrimaryKey() {
        return primaryKey;
    }


    /**
     * Sets the primaryKey value for this CompanySoap.
     * 
     * @param primaryKey
     */
    public void setPrimaryKey(long primaryKey) {
        this.primaryKey = primaryKey;
    }


    /**
     * Gets the system value for this CompanySoap.
     * 
     * @return system
     */
    public boolean isSystem() {
        return system;
    }


    /**
     * Sets the system value for this CompanySoap.
     * 
     * @param system
     */
    public void setSystem(boolean system) {
        this.system = system;
    }


    /**
     * Gets the webId value for this CompanySoap.
     * 
     * @return webId
     */
    public java.lang.String getWebId() {
        return webId;
    }


    /**
     * Sets the webId value for this CompanySoap.
     * 
     * @param webId
     */
    public void setWebId(java.lang.String webId) {
        this.webId = webId;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CompanySoap)) return false;
        CompanySoap other = (CompanySoap) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.accountId == other.getAccountId() &&
            this.active == other.isActive() &&
            this.companyId == other.getCompanyId() &&
            ((this.homeURL==null && other.getHomeURL()==null) || 
             (this.homeURL!=null &&
              this.homeURL.equals(other.getHomeURL()))) &&
            ((this.key==null && other.getKey()==null) || 
             (this.key!=null &&
              this.key.equals(other.getKey()))) &&
            this.logoId == other.getLogoId() &&
            this.maxUsers == other.getMaxUsers() &&
            ((this.mx==null && other.getMx()==null) || 
             (this.mx!=null &&
              this.mx.equals(other.getMx()))) &&
            this.primaryKey == other.getPrimaryKey() &&
            this.system == other.isSystem() &&
            ((this.webId==null && other.getWebId()==null) || 
             (this.webId!=null &&
              this.webId.equals(other.getWebId())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        _hashCode += new Long(getAccountId()).hashCode();
        _hashCode += (isActive() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += new Long(getCompanyId()).hashCode();
        if (getHomeURL() != null) {
            _hashCode += getHomeURL().hashCode();
        }
        if (getKey() != null) {
            _hashCode += getKey().hashCode();
        }
        _hashCode += new Long(getLogoId()).hashCode();
        _hashCode += getMaxUsers();
        if (getMx() != null) {
            _hashCode += getMx().hashCode();
        }
        _hashCode += new Long(getPrimaryKey()).hashCode();
        _hashCode += (isSystem() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getWebId() != null) {
            _hashCode += getWebId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CompanySoap.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://model.portal.liferay.com", "CompanySoap"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("accountId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "accountId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("active");
        elemField.setXmlName(new javax.xml.namespace.QName("", "active"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("companyId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "companyId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("homeURL");
        elemField.setXmlName(new javax.xml.namespace.QName("", "homeURL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("key");
        elemField.setXmlName(new javax.xml.namespace.QName("", "key"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("logoId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "logoId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maxUsers");
        elemField.setXmlName(new javax.xml.namespace.QName("", "maxUsers"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mx");
        elemField.setXmlName(new javax.xml.namespace.QName("", "mx"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("primaryKey");
        elemField.setXmlName(new javax.xml.namespace.QName("", "primaryKey"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("system");
        elemField.setXmlName(new javax.xml.namespace.QName("", "system"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("webId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "webId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
