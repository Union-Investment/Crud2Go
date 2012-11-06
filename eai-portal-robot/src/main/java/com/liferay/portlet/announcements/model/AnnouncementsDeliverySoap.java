/**
 * AnnouncementsDeliverySoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.liferay.portlet.announcements.model;

public class AnnouncementsDeliverySoap  implements java.io.Serializable {
    private long companyId;

    private long deliveryId;

    private boolean email;

    private long primaryKey;

    private boolean sms;

    private java.lang.String type;

    private long userId;

    private boolean website;

    public AnnouncementsDeliverySoap() {
    }

    public AnnouncementsDeliverySoap(
           long companyId,
           long deliveryId,
           boolean email,
           long primaryKey,
           boolean sms,
           java.lang.String type,
           long userId,
           boolean website) {
           this.companyId = companyId;
           this.deliveryId = deliveryId;
           this.email = email;
           this.primaryKey = primaryKey;
           this.sms = sms;
           this.type = type;
           this.userId = userId;
           this.website = website;
    }


    /**
     * Gets the companyId value for this AnnouncementsDeliverySoap.
     * 
     * @return companyId
     */
    public long getCompanyId() {
        return companyId;
    }


    /**
     * Sets the companyId value for this AnnouncementsDeliverySoap.
     * 
     * @param companyId
     */
    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }


    /**
     * Gets the deliveryId value for this AnnouncementsDeliverySoap.
     * 
     * @return deliveryId
     */
    public long getDeliveryId() {
        return deliveryId;
    }


    /**
     * Sets the deliveryId value for this AnnouncementsDeliverySoap.
     * 
     * @param deliveryId
     */
    public void setDeliveryId(long deliveryId) {
        this.deliveryId = deliveryId;
    }


    /**
     * Gets the email value for this AnnouncementsDeliverySoap.
     * 
     * @return email
     */
    public boolean isEmail() {
        return email;
    }


    /**
     * Sets the email value for this AnnouncementsDeliverySoap.
     * 
     * @param email
     */
    public void setEmail(boolean email) {
        this.email = email;
    }


    /**
     * Gets the primaryKey value for this AnnouncementsDeliverySoap.
     * 
     * @return primaryKey
     */
    public long getPrimaryKey() {
        return primaryKey;
    }


    /**
     * Sets the primaryKey value for this AnnouncementsDeliverySoap.
     * 
     * @param primaryKey
     */
    public void setPrimaryKey(long primaryKey) {
        this.primaryKey = primaryKey;
    }


    /**
     * Gets the sms value for this AnnouncementsDeliverySoap.
     * 
     * @return sms
     */
    public boolean isSms() {
        return sms;
    }


    /**
     * Sets the sms value for this AnnouncementsDeliverySoap.
     * 
     * @param sms
     */
    public void setSms(boolean sms) {
        this.sms = sms;
    }


    /**
     * Gets the type value for this AnnouncementsDeliverySoap.
     * 
     * @return type
     */
    public java.lang.String getType() {
        return type;
    }


    /**
     * Sets the type value for this AnnouncementsDeliverySoap.
     * 
     * @param type
     */
    public void setType(java.lang.String type) {
        this.type = type;
    }


    /**
     * Gets the userId value for this AnnouncementsDeliverySoap.
     * 
     * @return userId
     */
    public long getUserId() {
        return userId;
    }


    /**
     * Sets the userId value for this AnnouncementsDeliverySoap.
     * 
     * @param userId
     */
    public void setUserId(long userId) {
        this.userId = userId;
    }


    /**
     * Gets the website value for this AnnouncementsDeliverySoap.
     * 
     * @return website
     */
    public boolean isWebsite() {
        return website;
    }


    /**
     * Sets the website value for this AnnouncementsDeliverySoap.
     * 
     * @param website
     */
    public void setWebsite(boolean website) {
        this.website = website;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AnnouncementsDeliverySoap)) return false;
        AnnouncementsDeliverySoap other = (AnnouncementsDeliverySoap) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.companyId == other.getCompanyId() &&
            this.deliveryId == other.getDeliveryId() &&
            this.email == other.isEmail() &&
            this.primaryKey == other.getPrimaryKey() &&
            this.sms == other.isSms() &&
            ((this.type==null && other.getType()==null) || 
             (this.type!=null &&
              this.type.equals(other.getType()))) &&
            this.userId == other.getUserId() &&
            this.website == other.isWebsite();
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
        _hashCode += new Long(getCompanyId()).hashCode();
        _hashCode += new Long(getDeliveryId()).hashCode();
        _hashCode += (isEmail() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += new Long(getPrimaryKey()).hashCode();
        _hashCode += (isSms() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        _hashCode += new Long(getUserId()).hashCode();
        _hashCode += (isWebsite() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AnnouncementsDeliverySoap.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://model.announcements.portlet.liferay.com", "AnnouncementsDeliverySoap"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("companyId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "companyId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("deliveryId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "deliveryId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("email");
        elemField.setXmlName(new javax.xml.namespace.QName("", "email"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("primaryKey");
        elemField.setXmlName(new javax.xml.namespace.QName("", "primaryKey"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sms");
        elemField.setXmlName(new javax.xml.namespace.QName("", "sms"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("", "type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "userId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("website");
        elemField.setXmlName(new javax.xml.namespace.QName("", "website"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
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
