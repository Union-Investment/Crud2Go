/**
 * PortletPreferencesIds.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.liferay.portal.model;

public class PortletPreferencesIds  implements java.io.Serializable {
    private long companyId;

    private long ownerId;

    private int ownerType;

    private long plid;

    private java.lang.String portletId;

    public PortletPreferencesIds() {
    }

    public PortletPreferencesIds(
           long companyId,
           long ownerId,
           int ownerType,
           long plid,
           java.lang.String portletId) {
           this.companyId = companyId;
           this.ownerId = ownerId;
           this.ownerType = ownerType;
           this.plid = plid;
           this.portletId = portletId;
    }


    /**
     * Gets the companyId value for this PortletPreferencesIds.
     * 
     * @return companyId
     */
    public long getCompanyId() {
        return companyId;
    }


    /**
     * Sets the companyId value for this PortletPreferencesIds.
     * 
     * @param companyId
     */
    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }


    /**
     * Gets the ownerId value for this PortletPreferencesIds.
     * 
     * @return ownerId
     */
    public long getOwnerId() {
        return ownerId;
    }


    /**
     * Sets the ownerId value for this PortletPreferencesIds.
     * 
     * @param ownerId
     */
    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }


    /**
     * Gets the ownerType value for this PortletPreferencesIds.
     * 
     * @return ownerType
     */
    public int getOwnerType() {
        return ownerType;
    }


    /**
     * Sets the ownerType value for this PortletPreferencesIds.
     * 
     * @param ownerType
     */
    public void setOwnerType(int ownerType) {
        this.ownerType = ownerType;
    }


    /**
     * Gets the plid value for this PortletPreferencesIds.
     * 
     * @return plid
     */
    public long getPlid() {
        return plid;
    }


    /**
     * Sets the plid value for this PortletPreferencesIds.
     * 
     * @param plid
     */
    public void setPlid(long plid) {
        this.plid = plid;
    }


    /**
     * Gets the portletId value for this PortletPreferencesIds.
     * 
     * @return portletId
     */
    public java.lang.String getPortletId() {
        return portletId;
    }


    /**
     * Sets the portletId value for this PortletPreferencesIds.
     * 
     * @param portletId
     */
    public void setPortletId(java.lang.String portletId) {
        this.portletId = portletId;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PortletPreferencesIds)) return false;
        PortletPreferencesIds other = (PortletPreferencesIds) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.companyId == other.getCompanyId() &&
            this.ownerId == other.getOwnerId() &&
            this.ownerType == other.getOwnerType() &&
            this.plid == other.getPlid() &&
            ((this.portletId==null && other.getPortletId()==null) || 
             (this.portletId!=null &&
              this.portletId.equals(other.getPortletId())));
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
        _hashCode += new Long(getOwnerId()).hashCode();
        _hashCode += getOwnerType();
        _hashCode += new Long(getPlid()).hashCode();
        if (getPortletId() != null) {
            _hashCode += getPortletId().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PortletPreferencesIds.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://model.portal.liferay.com", "PortletPreferencesIds"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("companyId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "companyId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ownerId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ownerId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ownerType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ownerType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("plid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "plid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("portletId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "portletId"));
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
