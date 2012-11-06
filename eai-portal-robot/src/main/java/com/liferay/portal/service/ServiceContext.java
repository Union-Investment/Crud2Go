/**
 * ServiceContext.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.liferay.portal.service;

public class ServiceContext  implements java.io.Serializable {
    private boolean addCommunityPermissions;

    private boolean addGroupPermissions;

    private boolean addGuestPermissions;

    private long[] assetCategoryIds;

    private boolean assetEntryVisible;

    private long[] assetLinkEntryIds;

    private java.lang.String[] assetTagNames;

    private java.util.HashMap attributes;

    private java.lang.String command;

    private boolean commandAdd;

    private boolean commandUpdate;

    private java.lang.String[] communityPermissions;

    private long companyId;

    private java.util.Calendar createDate;

    private java.lang.String currentURL;

    private boolean deriveDefaultPermissions;

    private java.util.HashMap expandoBridgeAttributes;

    private java.util.Calendar formDate;

    private java.lang.String[] groupPermissions;

    private long guestOrUserId;

    private java.lang.String[] guestPermissions;

    private java.util.HashMap headers;

    private boolean indexingEnabled;

    private java.lang.String languageId;

    private java.lang.String layoutFullURL;

    private java.lang.String layoutURL;

    private java.lang.Object locale;

    private java.util.Calendar modifiedDate;

    private java.lang.String pathMain;

    private long plid;

    private java.lang.String portalURL;

    private java.lang.String portletId;

    private com.liferay.portal.model.PortletPreferencesIds portletPreferencesIds;

    private java.lang.String remoteAddr;

    private java.lang.String remoteHost;

    private java.lang.Object request;

    private java.lang.String rootPortletId;

    private long scopeGroupId;

    private boolean signedIn;

    private java.lang.String userAgent;

    private java.lang.String userDisplayURL;

    private long userId;

    private java.lang.String uuid;

    private int workflowAction;

    public ServiceContext() {
    }

    public ServiceContext(
           boolean addCommunityPermissions,
           boolean addGroupPermissions,
           boolean addGuestPermissions,
           long[] assetCategoryIds,
           boolean assetEntryVisible,
           long[] assetLinkEntryIds,
           java.lang.String[] assetTagNames,
           java.util.HashMap attributes,
           java.lang.String command,
           boolean commandAdd,
           boolean commandUpdate,
           java.lang.String[] communityPermissions,
           long companyId,
           java.util.Calendar createDate,
           java.lang.String currentURL,
           boolean deriveDefaultPermissions,
           java.util.HashMap expandoBridgeAttributes,
           java.util.Calendar formDate,
           java.lang.String[] groupPermissions,
           long guestOrUserId,
           java.lang.String[] guestPermissions,
           java.util.HashMap headers,
           boolean indexingEnabled,
           java.lang.String languageId,
           java.lang.String layoutFullURL,
           java.lang.String layoutURL,
           java.lang.Object locale,
           java.util.Calendar modifiedDate,
           java.lang.String pathMain,
           long plid,
           java.lang.String portalURL,
           java.lang.String portletId,
           com.liferay.portal.model.PortletPreferencesIds portletPreferencesIds,
           java.lang.String remoteAddr,
           java.lang.String remoteHost,
           java.lang.Object request,
           java.lang.String rootPortletId,
           long scopeGroupId,
           boolean signedIn,
           java.lang.String userAgent,
           java.lang.String userDisplayURL,
           long userId,
           java.lang.String uuid,
           int workflowAction) {
           this.addCommunityPermissions = addCommunityPermissions;
           this.addGroupPermissions = addGroupPermissions;
           this.addGuestPermissions = addGuestPermissions;
           this.assetCategoryIds = assetCategoryIds;
           this.assetEntryVisible = assetEntryVisible;
           this.assetLinkEntryIds = assetLinkEntryIds;
           this.assetTagNames = assetTagNames;
           this.attributes = attributes;
           this.command = command;
           this.commandAdd = commandAdd;
           this.commandUpdate = commandUpdate;
           this.communityPermissions = communityPermissions;
           this.companyId = companyId;
           this.createDate = createDate;
           this.currentURL = currentURL;
           this.deriveDefaultPermissions = deriveDefaultPermissions;
           this.expandoBridgeAttributes = expandoBridgeAttributes;
           this.formDate = formDate;
           this.groupPermissions = groupPermissions;
           this.guestOrUserId = guestOrUserId;
           this.guestPermissions = guestPermissions;
           this.headers = headers;
           this.indexingEnabled = indexingEnabled;
           this.languageId = languageId;
           this.layoutFullURL = layoutFullURL;
           this.layoutURL = layoutURL;
           this.locale = locale;
           this.modifiedDate = modifiedDate;
           this.pathMain = pathMain;
           this.plid = plid;
           this.portalURL = portalURL;
           this.portletId = portletId;
           this.portletPreferencesIds = portletPreferencesIds;
           this.remoteAddr = remoteAddr;
           this.remoteHost = remoteHost;
           this.request = request;
           this.rootPortletId = rootPortletId;
           this.scopeGroupId = scopeGroupId;
           this.signedIn = signedIn;
           this.userAgent = userAgent;
           this.userDisplayURL = userDisplayURL;
           this.userId = userId;
           this.uuid = uuid;
           this.workflowAction = workflowAction;
    }


    /**
     * Gets the addCommunityPermissions value for this ServiceContext.
     * 
     * @return addCommunityPermissions
     */
    public boolean isAddCommunityPermissions() {
        return addCommunityPermissions;
    }


    /**
     * Sets the addCommunityPermissions value for this ServiceContext.
     * 
     * @param addCommunityPermissions
     */
    public void setAddCommunityPermissions(boolean addCommunityPermissions) {
        this.addCommunityPermissions = addCommunityPermissions;
    }


    /**
     * Gets the addGroupPermissions value for this ServiceContext.
     * 
     * @return addGroupPermissions
     */
    public boolean isAddGroupPermissions() {
        return addGroupPermissions;
    }


    /**
     * Sets the addGroupPermissions value for this ServiceContext.
     * 
     * @param addGroupPermissions
     */
    public void setAddGroupPermissions(boolean addGroupPermissions) {
        this.addGroupPermissions = addGroupPermissions;
    }


    /**
     * Gets the addGuestPermissions value for this ServiceContext.
     * 
     * @return addGuestPermissions
     */
    public boolean isAddGuestPermissions() {
        return addGuestPermissions;
    }


    /**
     * Sets the addGuestPermissions value for this ServiceContext.
     * 
     * @param addGuestPermissions
     */
    public void setAddGuestPermissions(boolean addGuestPermissions) {
        this.addGuestPermissions = addGuestPermissions;
    }


    /**
     * Gets the assetCategoryIds value for this ServiceContext.
     * 
     * @return assetCategoryIds
     */
    public long[] getAssetCategoryIds() {
        return assetCategoryIds;
    }


    /**
     * Sets the assetCategoryIds value for this ServiceContext.
     * 
     * @param assetCategoryIds
     */
    public void setAssetCategoryIds(long[] assetCategoryIds) {
        this.assetCategoryIds = assetCategoryIds;
    }


    /**
     * Gets the assetEntryVisible value for this ServiceContext.
     * 
     * @return assetEntryVisible
     */
    public boolean isAssetEntryVisible() {
        return assetEntryVisible;
    }


    /**
     * Sets the assetEntryVisible value for this ServiceContext.
     * 
     * @param assetEntryVisible
     */
    public void setAssetEntryVisible(boolean assetEntryVisible) {
        this.assetEntryVisible = assetEntryVisible;
    }


    /**
     * Gets the assetLinkEntryIds value for this ServiceContext.
     * 
     * @return assetLinkEntryIds
     */
    public long[] getAssetLinkEntryIds() {
        return assetLinkEntryIds;
    }


    /**
     * Sets the assetLinkEntryIds value for this ServiceContext.
     * 
     * @param assetLinkEntryIds
     */
    public void setAssetLinkEntryIds(long[] assetLinkEntryIds) {
        this.assetLinkEntryIds = assetLinkEntryIds;
    }


    /**
     * Gets the assetTagNames value for this ServiceContext.
     * 
     * @return assetTagNames
     */
    public java.lang.String[] getAssetTagNames() {
        return assetTagNames;
    }


    /**
     * Sets the assetTagNames value for this ServiceContext.
     * 
     * @param assetTagNames
     */
    public void setAssetTagNames(java.lang.String[] assetTagNames) {
        this.assetTagNames = assetTagNames;
    }


    /**
     * Gets the attributes value for this ServiceContext.
     * 
     * @return attributes
     */
    public java.util.HashMap getAttributes() {
        return attributes;
    }


    /**
     * Sets the attributes value for this ServiceContext.
     * 
     * @param attributes
     */
    public void setAttributes(java.util.HashMap attributes) {
        this.attributes = attributes;
    }


    /**
     * Gets the command value for this ServiceContext.
     * 
     * @return command
     */
    public java.lang.String getCommand() {
        return command;
    }


    /**
     * Sets the command value for this ServiceContext.
     * 
     * @param command
     */
    public void setCommand(java.lang.String command) {
        this.command = command;
    }


    /**
     * Gets the commandAdd value for this ServiceContext.
     * 
     * @return commandAdd
     */
    public boolean isCommandAdd() {
        return commandAdd;
    }


    /**
     * Sets the commandAdd value for this ServiceContext.
     * 
     * @param commandAdd
     */
    public void setCommandAdd(boolean commandAdd) {
        this.commandAdd = commandAdd;
    }


    /**
     * Gets the commandUpdate value for this ServiceContext.
     * 
     * @return commandUpdate
     */
    public boolean isCommandUpdate() {
        return commandUpdate;
    }


    /**
     * Sets the commandUpdate value for this ServiceContext.
     * 
     * @param commandUpdate
     */
    public void setCommandUpdate(boolean commandUpdate) {
        this.commandUpdate = commandUpdate;
    }


    /**
     * Gets the communityPermissions value for this ServiceContext.
     * 
     * @return communityPermissions
     */
    public java.lang.String[] getCommunityPermissions() {
        return communityPermissions;
    }


    /**
     * Sets the communityPermissions value for this ServiceContext.
     * 
     * @param communityPermissions
     */
    public void setCommunityPermissions(java.lang.String[] communityPermissions) {
        this.communityPermissions = communityPermissions;
    }


    /**
     * Gets the companyId value for this ServiceContext.
     * 
     * @return companyId
     */
    public long getCompanyId() {
        return companyId;
    }


    /**
     * Sets the companyId value for this ServiceContext.
     * 
     * @param companyId
     */
    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }


    /**
     * Gets the createDate value for this ServiceContext.
     * 
     * @return createDate
     */
    public java.util.Calendar getCreateDate() {
        return createDate;
    }


    /**
     * Sets the createDate value for this ServiceContext.
     * 
     * @param createDate
     */
    public void setCreateDate(java.util.Calendar createDate) {
        this.createDate = createDate;
    }


    /**
     * Gets the currentURL value for this ServiceContext.
     * 
     * @return currentURL
     */
    public java.lang.String getCurrentURL() {
        return currentURL;
    }


    /**
     * Sets the currentURL value for this ServiceContext.
     * 
     * @param currentURL
     */
    public void setCurrentURL(java.lang.String currentURL) {
        this.currentURL = currentURL;
    }


    /**
     * Gets the deriveDefaultPermissions value for this ServiceContext.
     * 
     * @return deriveDefaultPermissions
     */
    public boolean isDeriveDefaultPermissions() {
        return deriveDefaultPermissions;
    }


    /**
     * Sets the deriveDefaultPermissions value for this ServiceContext.
     * 
     * @param deriveDefaultPermissions
     */
    public void setDeriveDefaultPermissions(boolean deriveDefaultPermissions) {
        this.deriveDefaultPermissions = deriveDefaultPermissions;
    }


    /**
     * Gets the expandoBridgeAttributes value for this ServiceContext.
     * 
     * @return expandoBridgeAttributes
     */
    public java.util.HashMap getExpandoBridgeAttributes() {
        return expandoBridgeAttributes;
    }


    /**
     * Sets the expandoBridgeAttributes value for this ServiceContext.
     * 
     * @param expandoBridgeAttributes
     */
    public void setExpandoBridgeAttributes(java.util.HashMap expandoBridgeAttributes) {
        this.expandoBridgeAttributes = expandoBridgeAttributes;
    }


    /**
     * Gets the formDate value for this ServiceContext.
     * 
     * @return formDate
     */
    public java.util.Calendar getFormDate() {
        return formDate;
    }


    /**
     * Sets the formDate value for this ServiceContext.
     * 
     * @param formDate
     */
    public void setFormDate(java.util.Calendar formDate) {
        this.formDate = formDate;
    }


    /**
     * Gets the groupPermissions value for this ServiceContext.
     * 
     * @return groupPermissions
     */
    public java.lang.String[] getGroupPermissions() {
        return groupPermissions;
    }


    /**
     * Sets the groupPermissions value for this ServiceContext.
     * 
     * @param groupPermissions
     */
    public void setGroupPermissions(java.lang.String[] groupPermissions) {
        this.groupPermissions = groupPermissions;
    }


    /**
     * Gets the guestOrUserId value for this ServiceContext.
     * 
     * @return guestOrUserId
     */
    public long getGuestOrUserId() {
        return guestOrUserId;
    }


    /**
     * Sets the guestOrUserId value for this ServiceContext.
     * 
     * @param guestOrUserId
     */
    public void setGuestOrUserId(long guestOrUserId) {
        this.guestOrUserId = guestOrUserId;
    }


    /**
     * Gets the guestPermissions value for this ServiceContext.
     * 
     * @return guestPermissions
     */
    public java.lang.String[] getGuestPermissions() {
        return guestPermissions;
    }


    /**
     * Sets the guestPermissions value for this ServiceContext.
     * 
     * @param guestPermissions
     */
    public void setGuestPermissions(java.lang.String[] guestPermissions) {
        this.guestPermissions = guestPermissions;
    }


    /**
     * Gets the headers value for this ServiceContext.
     * 
     * @return headers
     */
    public java.util.HashMap getHeaders() {
        return headers;
    }


    /**
     * Sets the headers value for this ServiceContext.
     * 
     * @param headers
     */
    public void setHeaders(java.util.HashMap headers) {
        this.headers = headers;
    }


    /**
     * Gets the indexingEnabled value for this ServiceContext.
     * 
     * @return indexingEnabled
     */
    public boolean isIndexingEnabled() {
        return indexingEnabled;
    }


    /**
     * Sets the indexingEnabled value for this ServiceContext.
     * 
     * @param indexingEnabled
     */
    public void setIndexingEnabled(boolean indexingEnabled) {
        this.indexingEnabled = indexingEnabled;
    }


    /**
     * Gets the languageId value for this ServiceContext.
     * 
     * @return languageId
     */
    public java.lang.String getLanguageId() {
        return languageId;
    }


    /**
     * Sets the languageId value for this ServiceContext.
     * 
     * @param languageId
     */
    public void setLanguageId(java.lang.String languageId) {
        this.languageId = languageId;
    }


    /**
     * Gets the layoutFullURL value for this ServiceContext.
     * 
     * @return layoutFullURL
     */
    public java.lang.String getLayoutFullURL() {
        return layoutFullURL;
    }


    /**
     * Sets the layoutFullURL value for this ServiceContext.
     * 
     * @param layoutFullURL
     */
    public void setLayoutFullURL(java.lang.String layoutFullURL) {
        this.layoutFullURL = layoutFullURL;
    }


    /**
     * Gets the layoutURL value for this ServiceContext.
     * 
     * @return layoutURL
     */
    public java.lang.String getLayoutURL() {
        return layoutURL;
    }


    /**
     * Sets the layoutURL value for this ServiceContext.
     * 
     * @param layoutURL
     */
    public void setLayoutURL(java.lang.String layoutURL) {
        this.layoutURL = layoutURL;
    }


    /**
     * Gets the locale value for this ServiceContext.
     * 
     * @return locale
     */
    public java.lang.Object getLocale() {
        return locale;
    }


    /**
     * Sets the locale value for this ServiceContext.
     * 
     * @param locale
     */
    public void setLocale(java.lang.Object locale) {
        this.locale = locale;
    }


    /**
     * Gets the modifiedDate value for this ServiceContext.
     * 
     * @return modifiedDate
     */
    public java.util.Calendar getModifiedDate() {
        return modifiedDate;
    }


    /**
     * Sets the modifiedDate value for this ServiceContext.
     * 
     * @param modifiedDate
     */
    public void setModifiedDate(java.util.Calendar modifiedDate) {
        this.modifiedDate = modifiedDate;
    }


    /**
     * Gets the pathMain value for this ServiceContext.
     * 
     * @return pathMain
     */
    public java.lang.String getPathMain() {
        return pathMain;
    }


    /**
     * Sets the pathMain value for this ServiceContext.
     * 
     * @param pathMain
     */
    public void setPathMain(java.lang.String pathMain) {
        this.pathMain = pathMain;
    }


    /**
     * Gets the plid value for this ServiceContext.
     * 
     * @return plid
     */
    public long getPlid() {
        return plid;
    }


    /**
     * Sets the plid value for this ServiceContext.
     * 
     * @param plid
     */
    public void setPlid(long plid) {
        this.plid = plid;
    }


    /**
     * Gets the portalURL value for this ServiceContext.
     * 
     * @return portalURL
     */
    public java.lang.String getPortalURL() {
        return portalURL;
    }


    /**
     * Sets the portalURL value for this ServiceContext.
     * 
     * @param portalURL
     */
    public void setPortalURL(java.lang.String portalURL) {
        this.portalURL = portalURL;
    }


    /**
     * Gets the portletId value for this ServiceContext.
     * 
     * @return portletId
     */
    public java.lang.String getPortletId() {
        return portletId;
    }


    /**
     * Sets the portletId value for this ServiceContext.
     * 
     * @param portletId
     */
    public void setPortletId(java.lang.String portletId) {
        this.portletId = portletId;
    }


    /**
     * Gets the portletPreferencesIds value for this ServiceContext.
     * 
     * @return portletPreferencesIds
     */
    public com.liferay.portal.model.PortletPreferencesIds getPortletPreferencesIds() {
        return portletPreferencesIds;
    }


    /**
     * Sets the portletPreferencesIds value for this ServiceContext.
     * 
     * @param portletPreferencesIds
     */
    public void setPortletPreferencesIds(com.liferay.portal.model.PortletPreferencesIds portletPreferencesIds) {
        this.portletPreferencesIds = portletPreferencesIds;
    }


    /**
     * Gets the remoteAddr value for this ServiceContext.
     * 
     * @return remoteAddr
     */
    public java.lang.String getRemoteAddr() {
        return remoteAddr;
    }


    /**
     * Sets the remoteAddr value for this ServiceContext.
     * 
     * @param remoteAddr
     */
    public void setRemoteAddr(java.lang.String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }


    /**
     * Gets the remoteHost value for this ServiceContext.
     * 
     * @return remoteHost
     */
    public java.lang.String getRemoteHost() {
        return remoteHost;
    }


    /**
     * Sets the remoteHost value for this ServiceContext.
     * 
     * @param remoteHost
     */
    public void setRemoteHost(java.lang.String remoteHost) {
        this.remoteHost = remoteHost;
    }


    /**
     * Gets the request value for this ServiceContext.
     * 
     * @return request
     */
    public java.lang.Object getRequest() {
        return request;
    }


    /**
     * Sets the request value for this ServiceContext.
     * 
     * @param request
     */
    public void setRequest(java.lang.Object request) {
        this.request = request;
    }


    /**
     * Gets the rootPortletId value for this ServiceContext.
     * 
     * @return rootPortletId
     */
    public java.lang.String getRootPortletId() {
        return rootPortletId;
    }


    /**
     * Sets the rootPortletId value for this ServiceContext.
     * 
     * @param rootPortletId
     */
    public void setRootPortletId(java.lang.String rootPortletId) {
        this.rootPortletId = rootPortletId;
    }


    /**
     * Gets the scopeGroupId value for this ServiceContext.
     * 
     * @return scopeGroupId
     */
    public long getScopeGroupId() {
        return scopeGroupId;
    }


    /**
     * Sets the scopeGroupId value for this ServiceContext.
     * 
     * @param scopeGroupId
     */
    public void setScopeGroupId(long scopeGroupId) {
        this.scopeGroupId = scopeGroupId;
    }


    /**
     * Gets the signedIn value for this ServiceContext.
     * 
     * @return signedIn
     */
    public boolean isSignedIn() {
        return signedIn;
    }


    /**
     * Sets the signedIn value for this ServiceContext.
     * 
     * @param signedIn
     */
    public void setSignedIn(boolean signedIn) {
        this.signedIn = signedIn;
    }


    /**
     * Gets the userAgent value for this ServiceContext.
     * 
     * @return userAgent
     */
    public java.lang.String getUserAgent() {
        return userAgent;
    }


    /**
     * Sets the userAgent value for this ServiceContext.
     * 
     * @param userAgent
     */
    public void setUserAgent(java.lang.String userAgent) {
        this.userAgent = userAgent;
    }


    /**
     * Gets the userDisplayURL value for this ServiceContext.
     * 
     * @return userDisplayURL
     */
    public java.lang.String getUserDisplayURL() {
        return userDisplayURL;
    }


    /**
     * Sets the userDisplayURL value for this ServiceContext.
     * 
     * @param userDisplayURL
     */
    public void setUserDisplayURL(java.lang.String userDisplayURL) {
        this.userDisplayURL = userDisplayURL;
    }


    /**
     * Gets the userId value for this ServiceContext.
     * 
     * @return userId
     */
    public long getUserId() {
        return userId;
    }


    /**
     * Sets the userId value for this ServiceContext.
     * 
     * @param userId
     */
    public void setUserId(long userId) {
        this.userId = userId;
    }


    /**
     * Gets the uuid value for this ServiceContext.
     * 
     * @return uuid
     */
    public java.lang.String getUuid() {
        return uuid;
    }


    /**
     * Sets the uuid value for this ServiceContext.
     * 
     * @param uuid
     */
    public void setUuid(java.lang.String uuid) {
        this.uuid = uuid;
    }


    /**
     * Gets the workflowAction value for this ServiceContext.
     * 
     * @return workflowAction
     */
    public int getWorkflowAction() {
        return workflowAction;
    }


    /**
     * Sets the workflowAction value for this ServiceContext.
     * 
     * @param workflowAction
     */
    public void setWorkflowAction(int workflowAction) {
        this.workflowAction = workflowAction;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ServiceContext)) return false;
        ServiceContext other = (ServiceContext) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.addCommunityPermissions == other.isAddCommunityPermissions() &&
            this.addGroupPermissions == other.isAddGroupPermissions() &&
            this.addGuestPermissions == other.isAddGuestPermissions() &&
            ((this.assetCategoryIds==null && other.getAssetCategoryIds()==null) || 
             (this.assetCategoryIds!=null &&
              java.util.Arrays.equals(this.assetCategoryIds, other.getAssetCategoryIds()))) &&
            this.assetEntryVisible == other.isAssetEntryVisible() &&
            ((this.assetLinkEntryIds==null && other.getAssetLinkEntryIds()==null) || 
             (this.assetLinkEntryIds!=null &&
              java.util.Arrays.equals(this.assetLinkEntryIds, other.getAssetLinkEntryIds()))) &&
            ((this.assetTagNames==null && other.getAssetTagNames()==null) || 
             (this.assetTagNames!=null &&
              java.util.Arrays.equals(this.assetTagNames, other.getAssetTagNames()))) &&
            ((this.attributes==null && other.getAttributes()==null) || 
             (this.attributes!=null &&
              this.attributes.equals(other.getAttributes()))) &&
            ((this.command==null && other.getCommand()==null) || 
             (this.command!=null &&
              this.command.equals(other.getCommand()))) &&
            this.commandAdd == other.isCommandAdd() &&
            this.commandUpdate == other.isCommandUpdate() &&
            ((this.communityPermissions==null && other.getCommunityPermissions()==null) || 
             (this.communityPermissions!=null &&
              java.util.Arrays.equals(this.communityPermissions, other.getCommunityPermissions()))) &&
            this.companyId == other.getCompanyId() &&
            ((this.createDate==null && other.getCreateDate()==null) || 
             (this.createDate!=null &&
              this.createDate.equals(other.getCreateDate()))) &&
            ((this.currentURL==null && other.getCurrentURL()==null) || 
             (this.currentURL!=null &&
              this.currentURL.equals(other.getCurrentURL()))) &&
            this.deriveDefaultPermissions == other.isDeriveDefaultPermissions() &&
            ((this.expandoBridgeAttributes==null && other.getExpandoBridgeAttributes()==null) || 
             (this.expandoBridgeAttributes!=null &&
              this.expandoBridgeAttributes.equals(other.getExpandoBridgeAttributes()))) &&
            ((this.formDate==null && other.getFormDate()==null) || 
             (this.formDate!=null &&
              this.formDate.equals(other.getFormDate()))) &&
            ((this.groupPermissions==null && other.getGroupPermissions()==null) || 
             (this.groupPermissions!=null &&
              java.util.Arrays.equals(this.groupPermissions, other.getGroupPermissions()))) &&
            this.guestOrUserId == other.getGuestOrUserId() &&
            ((this.guestPermissions==null && other.getGuestPermissions()==null) || 
             (this.guestPermissions!=null &&
              java.util.Arrays.equals(this.guestPermissions, other.getGuestPermissions()))) &&
            ((this.headers==null && other.getHeaders()==null) || 
             (this.headers!=null &&
              this.headers.equals(other.getHeaders()))) &&
            this.indexingEnabled == other.isIndexingEnabled() &&
            ((this.languageId==null && other.getLanguageId()==null) || 
             (this.languageId!=null &&
              this.languageId.equals(other.getLanguageId()))) &&
            ((this.layoutFullURL==null && other.getLayoutFullURL()==null) || 
             (this.layoutFullURL!=null &&
              this.layoutFullURL.equals(other.getLayoutFullURL()))) &&
            ((this.layoutURL==null && other.getLayoutURL()==null) || 
             (this.layoutURL!=null &&
              this.layoutURL.equals(other.getLayoutURL()))) &&
            ((this.locale==null && other.getLocale()==null) || 
             (this.locale!=null &&
              this.locale.equals(other.getLocale()))) &&
            ((this.modifiedDate==null && other.getModifiedDate()==null) || 
             (this.modifiedDate!=null &&
              this.modifiedDate.equals(other.getModifiedDate()))) &&
            ((this.pathMain==null && other.getPathMain()==null) || 
             (this.pathMain!=null &&
              this.pathMain.equals(other.getPathMain()))) &&
            this.plid == other.getPlid() &&
            ((this.portalURL==null && other.getPortalURL()==null) || 
             (this.portalURL!=null &&
              this.portalURL.equals(other.getPortalURL()))) &&
            ((this.portletId==null && other.getPortletId()==null) || 
             (this.portletId!=null &&
              this.portletId.equals(other.getPortletId()))) &&
            ((this.portletPreferencesIds==null && other.getPortletPreferencesIds()==null) || 
             (this.portletPreferencesIds!=null &&
              this.portletPreferencesIds.equals(other.getPortletPreferencesIds()))) &&
            ((this.remoteAddr==null && other.getRemoteAddr()==null) || 
             (this.remoteAddr!=null &&
              this.remoteAddr.equals(other.getRemoteAddr()))) &&
            ((this.remoteHost==null && other.getRemoteHost()==null) || 
             (this.remoteHost!=null &&
              this.remoteHost.equals(other.getRemoteHost()))) &&
            ((this.request==null && other.getRequest()==null) || 
             (this.request!=null &&
              this.request.equals(other.getRequest()))) &&
            ((this.rootPortletId==null && other.getRootPortletId()==null) || 
             (this.rootPortletId!=null &&
              this.rootPortletId.equals(other.getRootPortletId()))) &&
            this.scopeGroupId == other.getScopeGroupId() &&
            this.signedIn == other.isSignedIn() &&
            ((this.userAgent==null && other.getUserAgent()==null) || 
             (this.userAgent!=null &&
              this.userAgent.equals(other.getUserAgent()))) &&
            ((this.userDisplayURL==null && other.getUserDisplayURL()==null) || 
             (this.userDisplayURL!=null &&
              this.userDisplayURL.equals(other.getUserDisplayURL()))) &&
            this.userId == other.getUserId() &&
            ((this.uuid==null && other.getUuid()==null) || 
             (this.uuid!=null &&
              this.uuid.equals(other.getUuid()))) &&
            this.workflowAction == other.getWorkflowAction();
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
        _hashCode += (isAddCommunityPermissions() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isAddGroupPermissions() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isAddGuestPermissions() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getAssetCategoryIds() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAssetCategoryIds());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAssetCategoryIds(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += (isAssetEntryVisible() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getAssetLinkEntryIds() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAssetLinkEntryIds());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAssetLinkEntryIds(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getAssetTagNames() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAssetTagNames());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAssetTagNames(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getAttributes() != null) {
            _hashCode += getAttributes().hashCode();
        }
        if (getCommand() != null) {
            _hashCode += getCommand().hashCode();
        }
        _hashCode += (isCommandAdd() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += (isCommandUpdate() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getCommunityPermissions() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getCommunityPermissions());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getCommunityPermissions(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += new Long(getCompanyId()).hashCode();
        if (getCreateDate() != null) {
            _hashCode += getCreateDate().hashCode();
        }
        if (getCurrentURL() != null) {
            _hashCode += getCurrentURL().hashCode();
        }
        _hashCode += (isDeriveDefaultPermissions() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getExpandoBridgeAttributes() != null) {
            _hashCode += getExpandoBridgeAttributes().hashCode();
        }
        if (getFormDate() != null) {
            _hashCode += getFormDate().hashCode();
        }
        if (getGroupPermissions() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGroupPermissions());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGroupPermissions(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += new Long(getGuestOrUserId()).hashCode();
        if (getGuestPermissions() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGuestPermissions());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGuestPermissions(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getHeaders() != null) {
            _hashCode += getHeaders().hashCode();
        }
        _hashCode += (isIndexingEnabled() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getLanguageId() != null) {
            _hashCode += getLanguageId().hashCode();
        }
        if (getLayoutFullURL() != null) {
            _hashCode += getLayoutFullURL().hashCode();
        }
        if (getLayoutURL() != null) {
            _hashCode += getLayoutURL().hashCode();
        }
        if (getLocale() != null) {
            _hashCode += getLocale().hashCode();
        }
        if (getModifiedDate() != null) {
            _hashCode += getModifiedDate().hashCode();
        }
        if (getPathMain() != null) {
            _hashCode += getPathMain().hashCode();
        }
        _hashCode += new Long(getPlid()).hashCode();
        if (getPortalURL() != null) {
            _hashCode += getPortalURL().hashCode();
        }
        if (getPortletId() != null) {
            _hashCode += getPortletId().hashCode();
        }
        if (getPortletPreferencesIds() != null) {
            _hashCode += getPortletPreferencesIds().hashCode();
        }
        if (getRemoteAddr() != null) {
            _hashCode += getRemoteAddr().hashCode();
        }
        if (getRemoteHost() != null) {
            _hashCode += getRemoteHost().hashCode();
        }
        if (getRequest() != null) {
            _hashCode += getRequest().hashCode();
        }
        if (getRootPortletId() != null) {
            _hashCode += getRootPortletId().hashCode();
        }
        _hashCode += new Long(getScopeGroupId()).hashCode();
        _hashCode += (isSignedIn() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getUserAgent() != null) {
            _hashCode += getUserAgent().hashCode();
        }
        if (getUserDisplayURL() != null) {
            _hashCode += getUserDisplayURL().hashCode();
        }
        _hashCode += new Long(getUserId()).hashCode();
        if (getUuid() != null) {
            _hashCode += getUuid().hashCode();
        }
        _hashCode += getWorkflowAction();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ServiceContext.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://service.portal.liferay.com", "ServiceContext"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("addCommunityPermissions");
        elemField.setXmlName(new javax.xml.namespace.QName("", "addCommunityPermissions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("addGroupPermissions");
        elemField.setXmlName(new javax.xml.namespace.QName("", "addGroupPermissions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("addGuestPermissions");
        elemField.setXmlName(new javax.xml.namespace.QName("", "addGuestPermissions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("assetCategoryIds");
        elemField.setXmlName(new javax.xml.namespace.QName("", "assetCategoryIds"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("assetEntryVisible");
        elemField.setXmlName(new javax.xml.namespace.QName("", "assetEntryVisible"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("assetLinkEntryIds");
        elemField.setXmlName(new javax.xml.namespace.QName("", "assetLinkEntryIds"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("assetTagNames");
        elemField.setXmlName(new javax.xml.namespace.QName("", "assetTagNames"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("attributes");
        elemField.setXmlName(new javax.xml.namespace.QName("", "attributes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://xml.apache.org/xml-soap", "Map"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("command");
        elemField.setXmlName(new javax.xml.namespace.QName("", "command"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("commandAdd");
        elemField.setXmlName(new javax.xml.namespace.QName("", "commandAdd"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("commandUpdate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "commandUpdate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("communityPermissions");
        elemField.setXmlName(new javax.xml.namespace.QName("", "communityPermissions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("companyId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "companyId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("createDate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "createDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currentURL");
        elemField.setXmlName(new javax.xml.namespace.QName("", "currentURL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("deriveDefaultPermissions");
        elemField.setXmlName(new javax.xml.namespace.QName("", "deriveDefaultPermissions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("expandoBridgeAttributes");
        elemField.setXmlName(new javax.xml.namespace.QName("", "expandoBridgeAttributes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://xml.apache.org/xml-soap", "Map"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("formDate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "formDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("groupPermissions");
        elemField.setXmlName(new javax.xml.namespace.QName("", "groupPermissions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("guestOrUserId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "guestOrUserId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("guestPermissions");
        elemField.setXmlName(new javax.xml.namespace.QName("", "guestPermissions"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("headers");
        elemField.setXmlName(new javax.xml.namespace.QName("", "headers"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://xml.apache.org/xml-soap", "Map"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("indexingEnabled");
        elemField.setXmlName(new javax.xml.namespace.QName("", "indexingEnabled"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("languageId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "languageId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("layoutFullURL");
        elemField.setXmlName(new javax.xml.namespace.QName("", "layoutFullURL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("layoutURL");
        elemField.setXmlName(new javax.xml.namespace.QName("", "layoutURL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("locale");
        elemField.setXmlName(new javax.xml.namespace.QName("", "locale"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("modifiedDate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "modifiedDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pathMain");
        elemField.setXmlName(new javax.xml.namespace.QName("", "pathMain"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("plid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "plid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("portalURL");
        elemField.setXmlName(new javax.xml.namespace.QName("", "portalURL"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("portletId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "portletId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("portletPreferencesIds");
        elemField.setXmlName(new javax.xml.namespace.QName("", "portletPreferencesIds"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://model.portal.liferay.com", "PortletPreferencesIds"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("remoteAddr");
        elemField.setXmlName(new javax.xml.namespace.QName("", "remoteAddr"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("remoteHost");
        elemField.setXmlName(new javax.xml.namespace.QName("", "remoteHost"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("request");
        elemField.setXmlName(new javax.xml.namespace.QName("", "request"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rootPortletId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rootPortletId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("scopeGroupId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "scopeGroupId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("signedIn");
        elemField.setXmlName(new javax.xml.namespace.QName("", "signedIn"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userAgent");
        elemField.setXmlName(new javax.xml.namespace.QName("", "userAgent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userDisplayURL");
        elemField.setXmlName(new javax.xml.namespace.QName("", "userDisplayURL"));
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
        elemField.setFieldName("uuid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "uuid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("workflowAction");
        elemField.setXmlName(new javax.xml.namespace.QName("", "workflowAction"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
