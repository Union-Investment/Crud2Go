/**
 * UserSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.liferay.portal.model;

public class UserSoap  implements java.io.Serializable {
    private boolean agreedToTermsOfUse;

    private java.lang.String comments;

    private long companyId;

    private long contactId;

    private java.util.Calendar createDate;

    private boolean defaultUser;

    private java.lang.String digest;

    private java.lang.String emailAddress;

    private boolean emailAddressVerified;

    private long facebookId;

    private int failedLoginAttempts;

    private java.lang.String firstName;

    private int graceLoginCount;

    private java.lang.String greeting;

    private java.lang.String jobTitle;

    private java.lang.String languageId;

    private java.util.Calendar lastFailedLoginDate;

    private java.util.Calendar lastLoginDate;

    private java.lang.String lastLoginIP;

    private java.lang.String lastName;

    private boolean lockout;

    private java.util.Calendar lockoutDate;

    private java.util.Calendar loginDate;

    private java.lang.String loginIP;

    private java.lang.String middleName;

    private java.util.Calendar modifiedDate;

    private java.lang.String openId;

    private java.lang.String password;

    private boolean passwordEncrypted;

    private java.util.Calendar passwordModifiedDate;

    private boolean passwordReset;

    private long portraitId;

    private long primaryKey;

    private java.lang.String reminderQueryAnswer;

    private java.lang.String reminderQueryQuestion;

    private java.lang.String screenName;

    private int status;

    private java.lang.String timeZoneId;

    private long userId;

    private java.lang.String uuid;

    public UserSoap() {
    }

    public UserSoap(
           boolean agreedToTermsOfUse,
           java.lang.String comments,
           long companyId,
           long contactId,
           java.util.Calendar createDate,
           boolean defaultUser,
           java.lang.String digest,
           java.lang.String emailAddress,
           boolean emailAddressVerified,
           long facebookId,
           int failedLoginAttempts,
           java.lang.String firstName,
           int graceLoginCount,
           java.lang.String greeting,
           java.lang.String jobTitle,
           java.lang.String languageId,
           java.util.Calendar lastFailedLoginDate,
           java.util.Calendar lastLoginDate,
           java.lang.String lastLoginIP,
           java.lang.String lastName,
           boolean lockout,
           java.util.Calendar lockoutDate,
           java.util.Calendar loginDate,
           java.lang.String loginIP,
           java.lang.String middleName,
           java.util.Calendar modifiedDate,
           java.lang.String openId,
           java.lang.String password,
           boolean passwordEncrypted,
           java.util.Calendar passwordModifiedDate,
           boolean passwordReset,
           long portraitId,
           long primaryKey,
           java.lang.String reminderQueryAnswer,
           java.lang.String reminderQueryQuestion,
           java.lang.String screenName,
           int status,
           java.lang.String timeZoneId,
           long userId,
           java.lang.String uuid) {
           this.agreedToTermsOfUse = agreedToTermsOfUse;
           this.comments = comments;
           this.companyId = companyId;
           this.contactId = contactId;
           this.createDate = createDate;
           this.defaultUser = defaultUser;
           this.digest = digest;
           this.emailAddress = emailAddress;
           this.emailAddressVerified = emailAddressVerified;
           this.facebookId = facebookId;
           this.failedLoginAttempts = failedLoginAttempts;
           this.firstName = firstName;
           this.graceLoginCount = graceLoginCount;
           this.greeting = greeting;
           this.jobTitle = jobTitle;
           this.languageId = languageId;
           this.lastFailedLoginDate = lastFailedLoginDate;
           this.lastLoginDate = lastLoginDate;
           this.lastLoginIP = lastLoginIP;
           this.lastName = lastName;
           this.lockout = lockout;
           this.lockoutDate = lockoutDate;
           this.loginDate = loginDate;
           this.loginIP = loginIP;
           this.middleName = middleName;
           this.modifiedDate = modifiedDate;
           this.openId = openId;
           this.password = password;
           this.passwordEncrypted = passwordEncrypted;
           this.passwordModifiedDate = passwordModifiedDate;
           this.passwordReset = passwordReset;
           this.portraitId = portraitId;
           this.primaryKey = primaryKey;
           this.reminderQueryAnswer = reminderQueryAnswer;
           this.reminderQueryQuestion = reminderQueryQuestion;
           this.screenName = screenName;
           this.status = status;
           this.timeZoneId = timeZoneId;
           this.userId = userId;
           this.uuid = uuid;
    }


    /**
     * Gets the agreedToTermsOfUse value for this UserSoap.
     * 
     * @return agreedToTermsOfUse
     */
    public boolean isAgreedToTermsOfUse() {
        return agreedToTermsOfUse;
    }


    /**
     * Sets the agreedToTermsOfUse value for this UserSoap.
     * 
     * @param agreedToTermsOfUse
     */
    public void setAgreedToTermsOfUse(boolean agreedToTermsOfUse) {
        this.agreedToTermsOfUse = agreedToTermsOfUse;
    }


    /**
     * Gets the comments value for this UserSoap.
     * 
     * @return comments
     */
    public java.lang.String getComments() {
        return comments;
    }


    /**
     * Sets the comments value for this UserSoap.
     * 
     * @param comments
     */
    public void setComments(java.lang.String comments) {
        this.comments = comments;
    }


    /**
     * Gets the companyId value for this UserSoap.
     * 
     * @return companyId
     */
    public long getCompanyId() {
        return companyId;
    }


    /**
     * Sets the companyId value for this UserSoap.
     * 
     * @param companyId
     */
    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }


    /**
     * Gets the contactId value for this UserSoap.
     * 
     * @return contactId
     */
    public long getContactId() {
        return contactId;
    }


    /**
     * Sets the contactId value for this UserSoap.
     * 
     * @param contactId
     */
    public void setContactId(long contactId) {
        this.contactId = contactId;
    }


    /**
     * Gets the createDate value for this UserSoap.
     * 
     * @return createDate
     */
    public java.util.Calendar getCreateDate() {
        return createDate;
    }


    /**
     * Sets the createDate value for this UserSoap.
     * 
     * @param createDate
     */
    public void setCreateDate(java.util.Calendar createDate) {
        this.createDate = createDate;
    }


    /**
     * Gets the defaultUser value for this UserSoap.
     * 
     * @return defaultUser
     */
    public boolean isDefaultUser() {
        return defaultUser;
    }


    /**
     * Sets the defaultUser value for this UserSoap.
     * 
     * @param defaultUser
     */
    public void setDefaultUser(boolean defaultUser) {
        this.defaultUser = defaultUser;
    }


    /**
     * Gets the digest value for this UserSoap.
     * 
     * @return digest
     */
    public java.lang.String getDigest() {
        return digest;
    }


    /**
     * Sets the digest value for this UserSoap.
     * 
     * @param digest
     */
    public void setDigest(java.lang.String digest) {
        this.digest = digest;
    }


    /**
     * Gets the emailAddress value for this UserSoap.
     * 
     * @return emailAddress
     */
    public java.lang.String getEmailAddress() {
        return emailAddress;
    }


    /**
     * Sets the emailAddress value for this UserSoap.
     * 
     * @param emailAddress
     */
    public void setEmailAddress(java.lang.String emailAddress) {
        this.emailAddress = emailAddress;
    }


    /**
     * Gets the emailAddressVerified value for this UserSoap.
     * 
     * @return emailAddressVerified
     */
    public boolean isEmailAddressVerified() {
        return emailAddressVerified;
    }


    /**
     * Sets the emailAddressVerified value for this UserSoap.
     * 
     * @param emailAddressVerified
     */
    public void setEmailAddressVerified(boolean emailAddressVerified) {
        this.emailAddressVerified = emailAddressVerified;
    }


    /**
     * Gets the facebookId value for this UserSoap.
     * 
     * @return facebookId
     */
    public long getFacebookId() {
        return facebookId;
    }


    /**
     * Sets the facebookId value for this UserSoap.
     * 
     * @param facebookId
     */
    public void setFacebookId(long facebookId) {
        this.facebookId = facebookId;
    }


    /**
     * Gets the failedLoginAttempts value for this UserSoap.
     * 
     * @return failedLoginAttempts
     */
    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }


    /**
     * Sets the failedLoginAttempts value for this UserSoap.
     * 
     * @param failedLoginAttempts
     */
    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }


    /**
     * Gets the firstName value for this UserSoap.
     * 
     * @return firstName
     */
    public java.lang.String getFirstName() {
        return firstName;
    }


    /**
     * Sets the firstName value for this UserSoap.
     * 
     * @param firstName
     */
    public void setFirstName(java.lang.String firstName) {
        this.firstName = firstName;
    }


    /**
     * Gets the graceLoginCount value for this UserSoap.
     * 
     * @return graceLoginCount
     */
    public int getGraceLoginCount() {
        return graceLoginCount;
    }


    /**
     * Sets the graceLoginCount value for this UserSoap.
     * 
     * @param graceLoginCount
     */
    public void setGraceLoginCount(int graceLoginCount) {
        this.graceLoginCount = graceLoginCount;
    }


    /**
     * Gets the greeting value for this UserSoap.
     * 
     * @return greeting
     */
    public java.lang.String getGreeting() {
        return greeting;
    }


    /**
     * Sets the greeting value for this UserSoap.
     * 
     * @param greeting
     */
    public void setGreeting(java.lang.String greeting) {
        this.greeting = greeting;
    }


    /**
     * Gets the jobTitle value for this UserSoap.
     * 
     * @return jobTitle
     */
    public java.lang.String getJobTitle() {
        return jobTitle;
    }


    /**
     * Sets the jobTitle value for this UserSoap.
     * 
     * @param jobTitle
     */
    public void setJobTitle(java.lang.String jobTitle) {
        this.jobTitle = jobTitle;
    }


    /**
     * Gets the languageId value for this UserSoap.
     * 
     * @return languageId
     */
    public java.lang.String getLanguageId() {
        return languageId;
    }


    /**
     * Sets the languageId value for this UserSoap.
     * 
     * @param languageId
     */
    public void setLanguageId(java.lang.String languageId) {
        this.languageId = languageId;
    }


    /**
     * Gets the lastFailedLoginDate value for this UserSoap.
     * 
     * @return lastFailedLoginDate
     */
    public java.util.Calendar getLastFailedLoginDate() {
        return lastFailedLoginDate;
    }


    /**
     * Sets the lastFailedLoginDate value for this UserSoap.
     * 
     * @param lastFailedLoginDate
     */
    public void setLastFailedLoginDate(java.util.Calendar lastFailedLoginDate) {
        this.lastFailedLoginDate = lastFailedLoginDate;
    }


    /**
     * Gets the lastLoginDate value for this UserSoap.
     * 
     * @return lastLoginDate
     */
    public java.util.Calendar getLastLoginDate() {
        return lastLoginDate;
    }


    /**
     * Sets the lastLoginDate value for this UserSoap.
     * 
     * @param lastLoginDate
     */
    public void setLastLoginDate(java.util.Calendar lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }


    /**
     * Gets the lastLoginIP value for this UserSoap.
     * 
     * @return lastLoginIP
     */
    public java.lang.String getLastLoginIP() {
        return lastLoginIP;
    }


    /**
     * Sets the lastLoginIP value for this UserSoap.
     * 
     * @param lastLoginIP
     */
    public void setLastLoginIP(java.lang.String lastLoginIP) {
        this.lastLoginIP = lastLoginIP;
    }


    /**
     * Gets the lastName value for this UserSoap.
     * 
     * @return lastName
     */
    public java.lang.String getLastName() {
        return lastName;
    }


    /**
     * Sets the lastName value for this UserSoap.
     * 
     * @param lastName
     */
    public void setLastName(java.lang.String lastName) {
        this.lastName = lastName;
    }


    /**
     * Gets the lockout value for this UserSoap.
     * 
     * @return lockout
     */
    public boolean isLockout() {
        return lockout;
    }


    /**
     * Sets the lockout value for this UserSoap.
     * 
     * @param lockout
     */
    public void setLockout(boolean lockout) {
        this.lockout = lockout;
    }


    /**
     * Gets the lockoutDate value for this UserSoap.
     * 
     * @return lockoutDate
     */
    public java.util.Calendar getLockoutDate() {
        return lockoutDate;
    }


    /**
     * Sets the lockoutDate value for this UserSoap.
     * 
     * @param lockoutDate
     */
    public void setLockoutDate(java.util.Calendar lockoutDate) {
        this.lockoutDate = lockoutDate;
    }


    /**
     * Gets the loginDate value for this UserSoap.
     * 
     * @return loginDate
     */
    public java.util.Calendar getLoginDate() {
        return loginDate;
    }


    /**
     * Sets the loginDate value for this UserSoap.
     * 
     * @param loginDate
     */
    public void setLoginDate(java.util.Calendar loginDate) {
        this.loginDate = loginDate;
    }


    /**
     * Gets the loginIP value for this UserSoap.
     * 
     * @return loginIP
     */
    public java.lang.String getLoginIP() {
        return loginIP;
    }


    /**
     * Sets the loginIP value for this UserSoap.
     * 
     * @param loginIP
     */
    public void setLoginIP(java.lang.String loginIP) {
        this.loginIP = loginIP;
    }


    /**
     * Gets the middleName value for this UserSoap.
     * 
     * @return middleName
     */
    public java.lang.String getMiddleName() {
        return middleName;
    }


    /**
     * Sets the middleName value for this UserSoap.
     * 
     * @param middleName
     */
    public void setMiddleName(java.lang.String middleName) {
        this.middleName = middleName;
    }


    /**
     * Gets the modifiedDate value for this UserSoap.
     * 
     * @return modifiedDate
     */
    public java.util.Calendar getModifiedDate() {
        return modifiedDate;
    }


    /**
     * Sets the modifiedDate value for this UserSoap.
     * 
     * @param modifiedDate
     */
    public void setModifiedDate(java.util.Calendar modifiedDate) {
        this.modifiedDate = modifiedDate;
    }


    /**
     * Gets the openId value for this UserSoap.
     * 
     * @return openId
     */
    public java.lang.String getOpenId() {
        return openId;
    }


    /**
     * Sets the openId value for this UserSoap.
     * 
     * @param openId
     */
    public void setOpenId(java.lang.String openId) {
        this.openId = openId;
    }


    /**
     * Gets the password value for this UserSoap.
     * 
     * @return password
     */
    public java.lang.String getPassword() {
        return password;
    }


    /**
     * Sets the password value for this UserSoap.
     * 
     * @param password
     */
    public void setPassword(java.lang.String password) {
        this.password = password;
    }


    /**
     * Gets the passwordEncrypted value for this UserSoap.
     * 
     * @return passwordEncrypted
     */
    public boolean isPasswordEncrypted() {
        return passwordEncrypted;
    }


    /**
     * Sets the passwordEncrypted value for this UserSoap.
     * 
     * @param passwordEncrypted
     */
    public void setPasswordEncrypted(boolean passwordEncrypted) {
        this.passwordEncrypted = passwordEncrypted;
    }


    /**
     * Gets the passwordModifiedDate value for this UserSoap.
     * 
     * @return passwordModifiedDate
     */
    public java.util.Calendar getPasswordModifiedDate() {
        return passwordModifiedDate;
    }


    /**
     * Sets the passwordModifiedDate value for this UserSoap.
     * 
     * @param passwordModifiedDate
     */
    public void setPasswordModifiedDate(java.util.Calendar passwordModifiedDate) {
        this.passwordModifiedDate = passwordModifiedDate;
    }


    /**
     * Gets the passwordReset value for this UserSoap.
     * 
     * @return passwordReset
     */
    public boolean isPasswordReset() {
        return passwordReset;
    }


    /**
     * Sets the passwordReset value for this UserSoap.
     * 
     * @param passwordReset
     */
    public void setPasswordReset(boolean passwordReset) {
        this.passwordReset = passwordReset;
    }


    /**
     * Gets the portraitId value for this UserSoap.
     * 
     * @return portraitId
     */
    public long getPortraitId() {
        return portraitId;
    }


    /**
     * Sets the portraitId value for this UserSoap.
     * 
     * @param portraitId
     */
    public void setPortraitId(long portraitId) {
        this.portraitId = portraitId;
    }


    /**
     * Gets the primaryKey value for this UserSoap.
     * 
     * @return primaryKey
     */
    public long getPrimaryKey() {
        return primaryKey;
    }


    /**
     * Sets the primaryKey value for this UserSoap.
     * 
     * @param primaryKey
     */
    public void setPrimaryKey(long primaryKey) {
        this.primaryKey = primaryKey;
    }


    /**
     * Gets the reminderQueryAnswer value for this UserSoap.
     * 
     * @return reminderQueryAnswer
     */
    public java.lang.String getReminderQueryAnswer() {
        return reminderQueryAnswer;
    }


    /**
     * Sets the reminderQueryAnswer value for this UserSoap.
     * 
     * @param reminderQueryAnswer
     */
    public void setReminderQueryAnswer(java.lang.String reminderQueryAnswer) {
        this.reminderQueryAnswer = reminderQueryAnswer;
    }


    /**
     * Gets the reminderQueryQuestion value for this UserSoap.
     * 
     * @return reminderQueryQuestion
     */
    public java.lang.String getReminderQueryQuestion() {
        return reminderQueryQuestion;
    }


    /**
     * Sets the reminderQueryQuestion value for this UserSoap.
     * 
     * @param reminderQueryQuestion
     */
    public void setReminderQueryQuestion(java.lang.String reminderQueryQuestion) {
        this.reminderQueryQuestion = reminderQueryQuestion;
    }


    /**
     * Gets the screenName value for this UserSoap.
     * 
     * @return screenName
     */
    public java.lang.String getScreenName() {
        return screenName;
    }


    /**
     * Sets the screenName value for this UserSoap.
     * 
     * @param screenName
     */
    public void setScreenName(java.lang.String screenName) {
        this.screenName = screenName;
    }


    /**
     * Gets the status value for this UserSoap.
     * 
     * @return status
     */
    public int getStatus() {
        return status;
    }


    /**
     * Sets the status value for this UserSoap.
     * 
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
    }


    /**
     * Gets the timeZoneId value for this UserSoap.
     * 
     * @return timeZoneId
     */
    public java.lang.String getTimeZoneId() {
        return timeZoneId;
    }


    /**
     * Sets the timeZoneId value for this UserSoap.
     * 
     * @param timeZoneId
     */
    public void setTimeZoneId(java.lang.String timeZoneId) {
        this.timeZoneId = timeZoneId;
    }


    /**
     * Gets the userId value for this UserSoap.
     * 
     * @return userId
     */
    public long getUserId() {
        return userId;
    }


    /**
     * Sets the userId value for this UserSoap.
     * 
     * @param userId
     */
    public void setUserId(long userId) {
        this.userId = userId;
    }


    /**
     * Gets the uuid value for this UserSoap.
     * 
     * @return uuid
     */
    public java.lang.String getUuid() {
        return uuid;
    }


    /**
     * Sets the uuid value for this UserSoap.
     * 
     * @param uuid
     */
    public void setUuid(java.lang.String uuid) {
        this.uuid = uuid;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof UserSoap)) return false;
        UserSoap other = (UserSoap) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.agreedToTermsOfUse == other.isAgreedToTermsOfUse() &&
            ((this.comments==null && other.getComments()==null) || 
             (this.comments!=null &&
              this.comments.equals(other.getComments()))) &&
            this.companyId == other.getCompanyId() &&
            this.contactId == other.getContactId() &&
            ((this.createDate==null && other.getCreateDate()==null) || 
             (this.createDate!=null &&
              this.createDate.equals(other.getCreateDate()))) &&
            this.defaultUser == other.isDefaultUser() &&
            ((this.digest==null && other.getDigest()==null) || 
             (this.digest!=null &&
              this.digest.equals(other.getDigest()))) &&
            ((this.emailAddress==null && other.getEmailAddress()==null) || 
             (this.emailAddress!=null &&
              this.emailAddress.equals(other.getEmailAddress()))) &&
            this.emailAddressVerified == other.isEmailAddressVerified() &&
            this.facebookId == other.getFacebookId() &&
            this.failedLoginAttempts == other.getFailedLoginAttempts() &&
            ((this.firstName==null && other.getFirstName()==null) || 
             (this.firstName!=null &&
              this.firstName.equals(other.getFirstName()))) &&
            this.graceLoginCount == other.getGraceLoginCount() &&
            ((this.greeting==null && other.getGreeting()==null) || 
             (this.greeting!=null &&
              this.greeting.equals(other.getGreeting()))) &&
            ((this.jobTitle==null && other.getJobTitle()==null) || 
             (this.jobTitle!=null &&
              this.jobTitle.equals(other.getJobTitle()))) &&
            ((this.languageId==null && other.getLanguageId()==null) || 
             (this.languageId!=null &&
              this.languageId.equals(other.getLanguageId()))) &&
            ((this.lastFailedLoginDate==null && other.getLastFailedLoginDate()==null) || 
             (this.lastFailedLoginDate!=null &&
              this.lastFailedLoginDate.equals(other.getLastFailedLoginDate()))) &&
            ((this.lastLoginDate==null && other.getLastLoginDate()==null) || 
             (this.lastLoginDate!=null &&
              this.lastLoginDate.equals(other.getLastLoginDate()))) &&
            ((this.lastLoginIP==null && other.getLastLoginIP()==null) || 
             (this.lastLoginIP!=null &&
              this.lastLoginIP.equals(other.getLastLoginIP()))) &&
            ((this.lastName==null && other.getLastName()==null) || 
             (this.lastName!=null &&
              this.lastName.equals(other.getLastName()))) &&
            this.lockout == other.isLockout() &&
            ((this.lockoutDate==null && other.getLockoutDate()==null) || 
             (this.lockoutDate!=null &&
              this.lockoutDate.equals(other.getLockoutDate()))) &&
            ((this.loginDate==null && other.getLoginDate()==null) || 
             (this.loginDate!=null &&
              this.loginDate.equals(other.getLoginDate()))) &&
            ((this.loginIP==null && other.getLoginIP()==null) || 
             (this.loginIP!=null &&
              this.loginIP.equals(other.getLoginIP()))) &&
            ((this.middleName==null && other.getMiddleName()==null) || 
             (this.middleName!=null &&
              this.middleName.equals(other.getMiddleName()))) &&
            ((this.modifiedDate==null && other.getModifiedDate()==null) || 
             (this.modifiedDate!=null &&
              this.modifiedDate.equals(other.getModifiedDate()))) &&
            ((this.openId==null && other.getOpenId()==null) || 
             (this.openId!=null &&
              this.openId.equals(other.getOpenId()))) &&
            ((this.password==null && other.getPassword()==null) || 
             (this.password!=null &&
              this.password.equals(other.getPassword()))) &&
            this.passwordEncrypted == other.isPasswordEncrypted() &&
            ((this.passwordModifiedDate==null && other.getPasswordModifiedDate()==null) || 
             (this.passwordModifiedDate!=null &&
              this.passwordModifiedDate.equals(other.getPasswordModifiedDate()))) &&
            this.passwordReset == other.isPasswordReset() &&
            this.portraitId == other.getPortraitId() &&
            this.primaryKey == other.getPrimaryKey() &&
            ((this.reminderQueryAnswer==null && other.getReminderQueryAnswer()==null) || 
             (this.reminderQueryAnswer!=null &&
              this.reminderQueryAnswer.equals(other.getReminderQueryAnswer()))) &&
            ((this.reminderQueryQuestion==null && other.getReminderQueryQuestion()==null) || 
             (this.reminderQueryQuestion!=null &&
              this.reminderQueryQuestion.equals(other.getReminderQueryQuestion()))) &&
            ((this.screenName==null && other.getScreenName()==null) || 
             (this.screenName!=null &&
              this.screenName.equals(other.getScreenName()))) &&
            this.status == other.getStatus() &&
            ((this.timeZoneId==null && other.getTimeZoneId()==null) || 
             (this.timeZoneId!=null &&
              this.timeZoneId.equals(other.getTimeZoneId()))) &&
            this.userId == other.getUserId() &&
            ((this.uuid==null && other.getUuid()==null) || 
             (this.uuid!=null &&
              this.uuid.equals(other.getUuid())));
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
        _hashCode += (isAgreedToTermsOfUse() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getComments() != null) {
            _hashCode += getComments().hashCode();
        }
        _hashCode += new Long(getCompanyId()).hashCode();
        _hashCode += new Long(getContactId()).hashCode();
        if (getCreateDate() != null) {
            _hashCode += getCreateDate().hashCode();
        }
        _hashCode += (isDefaultUser() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getDigest() != null) {
            _hashCode += getDigest().hashCode();
        }
        if (getEmailAddress() != null) {
            _hashCode += getEmailAddress().hashCode();
        }
        _hashCode += (isEmailAddressVerified() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += new Long(getFacebookId()).hashCode();
        _hashCode += getFailedLoginAttempts();
        if (getFirstName() != null) {
            _hashCode += getFirstName().hashCode();
        }
        _hashCode += getGraceLoginCount();
        if (getGreeting() != null) {
            _hashCode += getGreeting().hashCode();
        }
        if (getJobTitle() != null) {
            _hashCode += getJobTitle().hashCode();
        }
        if (getLanguageId() != null) {
            _hashCode += getLanguageId().hashCode();
        }
        if (getLastFailedLoginDate() != null) {
            _hashCode += getLastFailedLoginDate().hashCode();
        }
        if (getLastLoginDate() != null) {
            _hashCode += getLastLoginDate().hashCode();
        }
        if (getLastLoginIP() != null) {
            _hashCode += getLastLoginIP().hashCode();
        }
        if (getLastName() != null) {
            _hashCode += getLastName().hashCode();
        }
        _hashCode += (isLockout() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getLockoutDate() != null) {
            _hashCode += getLockoutDate().hashCode();
        }
        if (getLoginDate() != null) {
            _hashCode += getLoginDate().hashCode();
        }
        if (getLoginIP() != null) {
            _hashCode += getLoginIP().hashCode();
        }
        if (getMiddleName() != null) {
            _hashCode += getMiddleName().hashCode();
        }
        if (getModifiedDate() != null) {
            _hashCode += getModifiedDate().hashCode();
        }
        if (getOpenId() != null) {
            _hashCode += getOpenId().hashCode();
        }
        if (getPassword() != null) {
            _hashCode += getPassword().hashCode();
        }
        _hashCode += (isPasswordEncrypted() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getPasswordModifiedDate() != null) {
            _hashCode += getPasswordModifiedDate().hashCode();
        }
        _hashCode += (isPasswordReset() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        _hashCode += new Long(getPortraitId()).hashCode();
        _hashCode += new Long(getPrimaryKey()).hashCode();
        if (getReminderQueryAnswer() != null) {
            _hashCode += getReminderQueryAnswer().hashCode();
        }
        if (getReminderQueryQuestion() != null) {
            _hashCode += getReminderQueryQuestion().hashCode();
        }
        if (getScreenName() != null) {
            _hashCode += getScreenName().hashCode();
        }
        _hashCode += getStatus();
        if (getTimeZoneId() != null) {
            _hashCode += getTimeZoneId().hashCode();
        }
        _hashCode += new Long(getUserId()).hashCode();
        if (getUuid() != null) {
            _hashCode += getUuid().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(UserSoap.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://model.portal.liferay.com", "UserSoap"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("agreedToTermsOfUse");
        elemField.setXmlName(new javax.xml.namespace.QName("", "agreedToTermsOfUse"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("comments");
        elemField.setXmlName(new javax.xml.namespace.QName("", "comments"));
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
        elemField.setFieldName("contactId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "contactId"));
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
        elemField.setFieldName("defaultUser");
        elemField.setXmlName(new javax.xml.namespace.QName("", "defaultUser"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("digest");
        elemField.setXmlName(new javax.xml.namespace.QName("", "digest"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("emailAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("", "emailAddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("emailAddressVerified");
        elemField.setXmlName(new javax.xml.namespace.QName("", "emailAddressVerified"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("facebookId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "facebookId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("failedLoginAttempts");
        elemField.setXmlName(new javax.xml.namespace.QName("", "failedLoginAttempts"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("firstName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "firstName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("graceLoginCount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "graceLoginCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("greeting");
        elemField.setXmlName(new javax.xml.namespace.QName("", "greeting"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("jobTitle");
        elemField.setXmlName(new javax.xml.namespace.QName("", "jobTitle"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("languageId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "languageId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastFailedLoginDate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "lastFailedLoginDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastLoginDate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "lastLoginDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastLoginIP");
        elemField.setXmlName(new javax.xml.namespace.QName("", "lastLoginIP"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "lastName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lockout");
        elemField.setXmlName(new javax.xml.namespace.QName("", "lockout"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lockoutDate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "lockoutDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("loginDate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "loginDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("loginIP");
        elemField.setXmlName(new javax.xml.namespace.QName("", "loginIP"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("middleName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "middleName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("modifiedDate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "modifiedDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("openId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "openId"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("password");
        elemField.setXmlName(new javax.xml.namespace.QName("", "password"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("passwordEncrypted");
        elemField.setXmlName(new javax.xml.namespace.QName("", "passwordEncrypted"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("passwordModifiedDate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "passwordModifiedDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("passwordReset");
        elemField.setXmlName(new javax.xml.namespace.QName("", "passwordReset"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("portraitId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "portraitId"));
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
        elemField.setFieldName("reminderQueryAnswer");
        elemField.setXmlName(new javax.xml.namespace.QName("", "reminderQueryAnswer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reminderQueryQuestion");
        elemField.setXmlName(new javax.xml.namespace.QName("", "reminderQueryQuestion"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("screenName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "screenName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://schemas.xmlsoap.org/soap/encoding/", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("timeZoneId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "timeZoneId"));
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
