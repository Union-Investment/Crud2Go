/**
 * GroupSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.liferay.portal.model;

public class GroupSoap  implements java.io.Serializable {
    private boolean active;

    private long classNameId;

    private long classPK;

    private long companyId;

    private long creatorUserId;

    private java.lang.String description;

    private java.lang.String friendlyURL;

    private long groupId;

    private long liveGroupId;

    private java.lang.String name;

    private long parentGroupId;

    private long primaryKey;

    private boolean site;

    private int type;

    private java.lang.String typeSettings;

    public GroupSoap() {
    }

    public GroupSoap(
           boolean active,
           long classNameId,
           long classPK,
           long companyId,
           long creatorUserId,
           java.lang.String description,
           java.lang.String friendlyURL,
           long groupId,
           long liveGroupId,
           java.lang.String name,
           long parentGroupId,
           long primaryKey,
           boolean site,
           int type,
           java.lang.String typeSettings) {
           this.active = active;
           this.classNameId = classNameId;
           this.classPK = classPK;
           this.companyId = companyId;
           this.creatorUserId = creatorUserId;
           this.description = description;
           this.friendlyURL = friendlyURL;
           this.groupId = groupId;
           this.liveGroupId = liveGroupId;
           this.name = name;
           this.parentGroupId = parentGroupId;
           this.primaryKey = primaryKey;
           this.site = site;
           this.type = type;
           this.typeSettings = typeSettings;
    }


    /**
     * Gets the active value for this GroupSoap.
     * 
     * @return active
     */
    public boolean isActive() {
        return active;
    }


    /**
     * Sets the active value for this GroupSoap.
     * 
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }


    /**
     * Gets the classNameId value for this GroupSoap.
     * 
     * @return classNameId
     */
    public long getClassNameId() {
        return classNameId;
    }


    /**
     * Sets the classNameId value for this GroupSoap.
     * 
     * @param classNameId
     */
    public void setClassNameId(long classNameId) {
        this.classNameId = classNameId;
    }


    /**
     * Gets the classPK value for this GroupSoap.
     * 
     * @return classPK
     */
    public long getClassPK() {
        return classPK;
    }


    /**
     * Sets the classPK value for this GroupSoap.
     * 
     * @param classPK
     */
    public void setClassPK(long classPK) {
        this.classPK = classPK;
    }


    /**
     * Gets the companyId value for this GroupSoap.
     * 
     * @return companyId
     */
    public long getCompanyId() {
        return companyId;
    }


    /**
     * Sets the companyId value for this GroupSoap.
     * 
     * @param companyId
     */
    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }


    /**
     * Gets the creatorUserId value for this GroupSoap.
     * 
     * @return creatorUserId
     */
    public long getCreatorUserId() {
        return creatorUserId;
    }


    /**
     * Sets the creatorUserId value for this GroupSoap.
     * 
     * @param creatorUserId
     */
    public void setCreatorUserId(long creatorUserId) {
        this.creatorUserId = creatorUserId;
    }


    /**
     * Gets the description value for this GroupSoap.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this GroupSoap.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the friendlyURL value for this GroupSoap.
     * 
     * @return friendlyURL
     */
    public java.lang.String getFriendlyURL() {
        return friendlyURL;
    }


    /**
     * Sets the friendlyURL value for this GroupSoap.
     * 
     * @param friendlyURL
     */
    public void setFriendlyURL(java.lang.String friendlyURL) {
        this.friendlyURL = friendlyURL;
    }


    /**
     * Gets the groupId value for this GroupSoap.
     * 
     * @return groupId
     */
    public long getGroupId() {
        return groupId;
    }


    /**
     * Sets the groupId value for this GroupSoap.
     * 
     * @param groupId
     */
    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }


    /**
     * Gets the liveGroupId value for this GroupSoap.
     * 
     * @return liveGroupId
     */
    public long getLiveGroupId() {
        return liveGroupId;
    }


    /**
     * Sets the liveGroupId value for this GroupSoap.
     * 
     * @param liveGroupId
     */
    public void setLiveGroupId(long liveGroupId) {
        this.liveGroupId = liveGroupId;
    }


    /**
     * Gets the name value for this GroupSoap.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this GroupSoap.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the parentGroupId value for this GroupSoap.
     * 
     * @return parentGroupId
     */
    public long getParentGroupId() {
        return parentGroupId;
    }


    /**
     * Sets the parentGroupId value for this GroupSoap.
     * 
     * @param parentGroupId
     */
    public void setParentGroupId(long parentGroupId) {
        this.parentGroupId = parentGroupId;
    }


    /**
     * Gets the primaryKey value for this GroupSoap.
     * 
     * @return primaryKey
     */
    public long getPrimaryKey() {
        return primaryKey;
    }


    /**
     * Sets the primaryKey value for this GroupSoap.
     * 
     * @param primaryKey
     */
    public void setPrimaryKey(long primaryKey) {
        this.primaryKey = primaryKey;
    }


    /**
     * Gets the site value for this GroupSoap.
     * 
     * @return site
     */
    public boolean isSite() {
        return site;
    }


    /**
     * Sets the site value for this GroupSoap.
     * 
     * @param site
     */
    public void setSite(boolean site) {
        this.site = site;
    }


    /**
     * Gets the type value for this GroupSoap.
     * 
     * @return type
     */
    public int getType() {
        return type;
    }


    /**
     * Sets the type value for this GroupSoap.
     * 
     * @param type
     */
    public void setType(int type) {
        this.type = type;
    }


    /**
     * Gets the typeSettings value for this GroupSoap.
     * 
     * @return typeSettings
     */
    public java.lang.String getTypeSettings() {
        return typeSettings;
    }


    /**
     * Sets the typeSettings value for this GroupSoap.
     * 
     * @param typeSettings
     */
    public void setTypeSettings(java.lang.String typeSettings) {
        this.typeSettings = typeSettings;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GroupSoap)) return false;
        GroupSoap other = (GroupSoap) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.active == other.isActive() &&
            this.classNameId == other.getClassNameId() &&
            this.classPK == other.getClassPK() &&
            this.companyId == other.getCompanyId() &&
            this.creatorUserId == other.getCreatorUserId() &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.friendlyURL==null && other.getFriendlyURL()==null) || 
             (this.friendlyURL!=null &&
              this.friendlyURL.equals(other.getFriendlyURL()))) &&
            this.groupId == other.getGroupId() &&
            this.liveGroupId == other.getLiveGroupId() &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            this.parentGroupId == other.getParentGroupId() &&
            this.primaryKey == other.getPrimaryKey() &&
            this.site == other.isSite() &&
            this.type == other.getType() &&
            ((this.typeSettings==null && other.getTypeSettings()==null) || 
             (this.typeSettings!=null &&
              this.typeSettings.equals(other.getTypeSettings())));
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
        _hashCode += (isActive() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += new Long(getClassNameId()).hashCode();
        _hashCode += new Long(getClassPK()).hashCode();
        _hashCode += new Long(getCompanyId()).hashCode();
        _hashCode += new Long(getCreatorUserId()).hashCode();
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        if (getFriendlyURL() != null) {
            _hashCode += getFriendlyURL().hashCode();
        }
        _hashCode += new Long(getGroupId()).hashCode();
        _hashCode += new Long(getLiveGroupId()).hashCode();
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        _hashCode += new Long(getParentGroupId()).hashCode();
        _hashCode += new Long(getPrimaryKey()).hashCode();
        _hashCode += (isSite() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += getType();
        if (getTypeSettings() != null) {
            _hashCode += getTypeSettings().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GroupSoap.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://model.portal.liferay.com", "GroupSoap"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("active");
        elemField.setXmlName(new javax.xml.namespace.QName("", "active"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("classNameId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "classNameId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("classPK");
        elemField.setXmlName(new javax.xml.namespace.QName("", "classPK"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("companyId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "companyId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("creatorUserId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "creatorUserId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("", "description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("friendlyURL");
        elemField.setXmlName(new javax.xml.namespace.QName("", "friendlyURL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("groupId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "groupId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("liveGroupId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "liveGroupId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("", "name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("parentGroupId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "parentGroupId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("primaryKey");
        elemField.setXmlName(new javax.xml.namespace.QName("", "primaryKey"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("site");
        elemField.setXmlName(new javax.xml.namespace.QName("", "site"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("", "type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("typeSettings");
        elemField.setXmlName(new javax.xml.namespace.QName("", "typeSettings"));
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
