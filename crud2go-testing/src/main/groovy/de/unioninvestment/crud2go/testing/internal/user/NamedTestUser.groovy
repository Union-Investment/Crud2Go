package de.unioninvestment.crud2go.testing.internal.user;

import java.util.Set;

import de.unioninvestment.eai.portal.portlet.crud.domain.model.Role;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.NamedUser;

public class NamedTestUser extends NamedUser {

	private Set<Role> portalRoles;

	NamedTestUser(String username, Set<Role> portletRoles, Set<Role> portalRoles) {
		super(username, portletRoles);
		this.portalRoles = portalRoles;
	}
	
	@Override
	Set<String> getPortalRoles() {
		return portalRoles.collect { it.name };
	}

	@Override
	Set<String> getRoles() {
		return roles
	};
}
