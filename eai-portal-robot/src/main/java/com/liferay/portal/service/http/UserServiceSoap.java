/**
 * UserServiceSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.liferay.portal.service.http;

public interface UserServiceSoap extends java.rmi.Remote {
    public void addGroupUsers(long groupId, long[] userIds, com.liferay.portal.service.ServiceContext serviceContext) throws java.rmi.RemoteException;
    public void addOrganizationUsers(long organizationId, long[] userIds) throws java.rmi.RemoteException;
    public void addPasswordPolicyUsers(long passwordPolicyId, long[] userIds) throws java.rmi.RemoteException;
    public void addRoleUsers(long roleId, long[] userIds) throws java.rmi.RemoteException;
    public void addTeamUsers(long teamId, long[] userIds) throws java.rmi.RemoteException;
    public void addUserGroupUsers(long userGroupId, long[] userIds) throws java.rmi.RemoteException;
    public com.liferay.portal.model.UserSoap addUserWithWorkflow(long companyId, boolean autoPassword, java.lang.String password1, java.lang.String password2, boolean autoScreenName, java.lang.String screenName, java.lang.String emailAddress, long facebookId, java.lang.String openId, java.lang.String locale, java.lang.String firstName, java.lang.String middleName, java.lang.String lastName, int prefixId, int suffixId, boolean male, int birthdayMonth, int birthdayDay, int birthdayYear, java.lang.String jobTitle, long[] groupIds, long[] organizationIds, long[] roleIds, long[] userGroupIds, com.liferay.portal.model.AddressSoap[] addresses, com.liferay.portal.model.EmailAddressSoap[] emailAddresses, com.liferay.portal.model.PhoneSoap[] phones, com.liferay.portal.model.WebsiteSoap[] websites, com.liferay.portlet.announcements.model.AnnouncementsDeliverySoap[] announcementsDelivers, boolean sendEmail, com.liferay.portal.service.ServiceContext serviceContext) throws java.rmi.RemoteException;
    public com.liferay.portal.model.UserSoap addUserWithWorkflow(long companyId, boolean autoPassword, java.lang.String password1, java.lang.String password2, boolean autoScreenName, java.lang.String screenName, java.lang.String emailAddress, long facebookId, java.lang.String openId, java.lang.String locale, java.lang.String firstName, java.lang.String middleName, java.lang.String lastName, int prefixId, int suffixId, boolean male, int birthdayMonth, int birthdayDay, int birthdayYear, java.lang.String jobTitle, long[] groupIds, long[] organizationIds, long[] roleIds, long[] userGroupIds, boolean sendEmail, com.liferay.portal.service.ServiceContext serviceContext) throws java.rmi.RemoteException;
    public com.liferay.portal.model.UserSoap addUser(long companyId, boolean autoPassword, java.lang.String password1, java.lang.String password2, boolean autoScreenName, java.lang.String screenName, java.lang.String emailAddress, long facebookId, java.lang.String openId, java.lang.String locale, java.lang.String firstName, java.lang.String middleName, java.lang.String lastName, int prefixId, int suffixId, boolean male, int birthdayMonth, int birthdayDay, int birthdayYear, java.lang.String jobTitle, long[] groupIds, long[] organizationIds, long[] roleIds, long[] userGroupIds, com.liferay.portal.model.AddressSoap[] addresses, com.liferay.portal.model.EmailAddressSoap[] emailAddresses, com.liferay.portal.model.PhoneSoap[] phones, com.liferay.portal.model.WebsiteSoap[] websites, com.liferay.portlet.announcements.model.AnnouncementsDeliverySoap[] announcementsDelivers, boolean sendEmail, com.liferay.portal.service.ServiceContext serviceContext) throws java.rmi.RemoteException;
    public com.liferay.portal.model.UserSoap addUser(long companyId, boolean autoPassword, java.lang.String password1, java.lang.String password2, boolean autoScreenName, java.lang.String screenName, java.lang.String emailAddress, long facebookId, java.lang.String openId, java.lang.String locale, java.lang.String firstName, java.lang.String middleName, java.lang.String lastName, int prefixId, int suffixId, boolean male, int birthdayMonth, int birthdayDay, int birthdayYear, java.lang.String jobTitle, long[] groupIds, long[] organizationIds, long[] roleIds, long[] userGroupIds, boolean sendEmail, com.liferay.portal.service.ServiceContext serviceContext) throws java.rmi.RemoteException;
    public void deletePortrait(long userId) throws java.rmi.RemoteException;
    public void deleteRoleUser(long roleId, long userId) throws java.rmi.RemoteException;
    public void deleteUser(long userId) throws java.rmi.RemoteException;
    public int getCompanyUsersCount(long companyId) throws java.rmi.RemoteException;
    public com.liferay.portal.model.UserSoap[] getCompanyUsers(long companyId, int start, int end) throws java.rmi.RemoteException;
    public long getDefaultUserId(long companyId) throws java.rmi.RemoteException;
    public long[] getGroupUserIds(long groupId) throws java.rmi.RemoteException;
    public com.liferay.portal.model.UserSoap[] getGroupUsers(long groupId) throws java.rmi.RemoteException;
    public long[] getOrganizationUserIds(long organizationId) throws java.rmi.RemoteException;
    public com.liferay.portal.model.UserSoap[] getOrganizationUsers(long organizationId) throws java.rmi.RemoteException;
    public long[] getRoleUserIds(long roleId) throws java.rmi.RemoteException;
    public com.liferay.portal.model.UserSoap getUserByEmailAddress(long companyId, java.lang.String emailAddress) throws java.rmi.RemoteException;
    public com.liferay.portal.model.UserSoap getUserById(long userId) throws java.rmi.RemoteException;
    public com.liferay.portal.model.UserSoap getUserByScreenName(long companyId, java.lang.String screenName) throws java.rmi.RemoteException;
    public com.liferay.portal.model.UserSoap[] getUserGroupUsers(long userGroupId) throws java.rmi.RemoteException;
    public long getUserIdByEmailAddress(long companyId, java.lang.String emailAddress) throws java.rmi.RemoteException;
    public long getUserIdByScreenName(long companyId, java.lang.String screenName) throws java.rmi.RemoteException;
    public boolean hasGroupUser(long groupId, long userId) throws java.rmi.RemoteException;
    public boolean hasRoleUser(long roleId, long userId) throws java.rmi.RemoteException;
    public boolean hasRoleUser(long companyId, java.lang.String name, long userId, boolean inherited) throws java.rmi.RemoteException;
    public void setRoleUsers(long roleId, long[] userIds) throws java.rmi.RemoteException;
    public void setUserGroupUsers(long userGroupId, long[] userIds) throws java.rmi.RemoteException;
    public void unsetGroupTeamsUsers(long groupId, long[] userIds) throws java.rmi.RemoteException;
    public void unsetGroupUsers(long groupId, long[] userIds, com.liferay.portal.service.ServiceContext serviceContext) throws java.rmi.RemoteException;
    public void unsetOrganizationUsers(long organizationId, long[] userIds) throws java.rmi.RemoteException;
    public void unsetPasswordPolicyUsers(long passwordPolicyId, long[] userIds) throws java.rmi.RemoteException;
    public void unsetRoleUsers(long roleId, long[] userIds) throws java.rmi.RemoteException;
    public void unsetTeamUsers(long teamId, long[] userIds) throws java.rmi.RemoteException;
    public void unsetUserGroupUsers(long userGroupId, long[] userIds) throws java.rmi.RemoteException;
    public com.liferay.portal.model.UserSoap updateAgreedToTermsOfUse(long userId, boolean agreedToTermsOfUse) throws java.rmi.RemoteException;
    public com.liferay.portal.model.UserSoap updateEmailAddress(long userId, java.lang.String password, java.lang.String emailAddress1, java.lang.String emailAddress2, com.liferay.portal.service.ServiceContext serviceContext) throws java.rmi.RemoteException;
    public com.liferay.portal.model.UserSoap updateIncompleteUser(long companyId, boolean autoPassword, java.lang.String password1, java.lang.String password2, boolean autoScreenName, java.lang.String screenName, java.lang.String emailAddress, long facebookId, java.lang.String openId, java.lang.String locale, java.lang.String firstName, java.lang.String middleName, java.lang.String lastName, int prefixId, int suffixId, boolean male, int birthdayMonth, int birthdayDay, int birthdayYear, java.lang.String jobTitle, boolean updateUserInformation, boolean sendEmail, com.liferay.portal.service.ServiceContext serviceContext) throws java.rmi.RemoteException;
    public com.liferay.portal.model.UserSoap updateLockoutById(long userId, boolean lockout) throws java.rmi.RemoteException;
    public com.liferay.portal.model.UserSoap updateOpenId(long userId, java.lang.String openId) throws java.rmi.RemoteException;
    public void updateOrganizations(long userId, long[] organizationIds, com.liferay.portal.service.ServiceContext serviceContext) throws java.rmi.RemoteException;
    public com.liferay.portal.model.UserSoap updatePassword(long userId, java.lang.String password1, java.lang.String password2, boolean passwordReset) throws java.rmi.RemoteException;
    public com.liferay.portal.model.UserSoap updatePortrait(long userId, byte[] bytes) throws java.rmi.RemoteException;
    public com.liferay.portal.model.UserSoap updateReminderQuery(long userId, java.lang.String question, java.lang.String answer) throws java.rmi.RemoteException;
    public com.liferay.portal.model.UserSoap updateScreenName(long userId, java.lang.String screenName) throws java.rmi.RemoteException;
    public com.liferay.portal.model.UserSoap updateStatus(long userId, int status) throws java.rmi.RemoteException;
    public com.liferay.portal.model.UserSoap updateUser(long userId, java.lang.String oldPassword, java.lang.String newPassword1, java.lang.String newPassword2, boolean passwordReset, java.lang.String reminderQueryQuestion, java.lang.String reminderQueryAnswer, java.lang.String screenName, java.lang.String emailAddress, long facebookId, java.lang.String openId, java.lang.String languageId, java.lang.String timeZoneId, java.lang.String greeting, java.lang.String comments, java.lang.String firstName, java.lang.String middleName, java.lang.String lastName, int prefixId, int suffixId, boolean male, int birthdayMonth, int birthdayDay, int birthdayYear, java.lang.String smsSn, java.lang.String aimSn, java.lang.String facebookSn, java.lang.String icqSn, java.lang.String jabberSn, java.lang.String msnSn, java.lang.String mySpaceSn, java.lang.String skypeSn, java.lang.String twitterSn, java.lang.String ymSn, java.lang.String jobTitle, long[] groupIds, long[] organizationIds, long[] roleIds, com.liferay.portal.model.UserGroupRoleSoap[] userGroupRoles, long[] userGroupIds, com.liferay.portal.model.AddressSoap[] addresses, com.liferay.portal.model.EmailAddressSoap[] emailAddresses, com.liferay.portal.model.PhoneSoap[] phones, com.liferay.portal.model.WebsiteSoap[] websites, com.liferay.portlet.announcements.model.AnnouncementsDeliverySoap[] announcementsDelivers, com.liferay.portal.service.ServiceContext serviceContext) throws java.rmi.RemoteException;
    public com.liferay.portal.model.UserSoap updateUser(long userId, java.lang.String oldPassword, java.lang.String newPassword1, java.lang.String newPassword2, boolean passwordReset, java.lang.String reminderQueryQuestion, java.lang.String reminderQueryAnswer, java.lang.String screenName, java.lang.String emailAddress, long facebookId, java.lang.String openId, java.lang.String languageId, java.lang.String timeZoneId, java.lang.String greeting, java.lang.String comments, java.lang.String firstName, java.lang.String middleName, java.lang.String lastName, int prefixId, int suffixId, boolean male, int birthdayMonth, int birthdayDay, int birthdayYear, java.lang.String smsSn, java.lang.String aimSn, java.lang.String facebookSn, java.lang.String icqSn, java.lang.String jabberSn, java.lang.String msnSn, java.lang.String mySpaceSn, java.lang.String skypeSn, java.lang.String twitterSn, java.lang.String ymSn, java.lang.String jobTitle, long[] groupIds, long[] organizationIds, long[] roleIds, com.liferay.portal.model.UserGroupRoleSoap[] userGroupRoles, long[] userGroupIds, com.liferay.portal.service.ServiceContext serviceContext) throws java.rmi.RemoteException;
}
