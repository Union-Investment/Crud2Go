package de.unioninvestment.crud2go.testing.internal.user

import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.CurrentUser
import de.unioninvestment.eai.portal.portlet.crud.domain.model.user.User;

class CurrentTestUser extends CurrentUser {

	private User delegate
	
	CurrentTestUser(User delegate) {
		super(null)
		this.delegate = delegate
	}
	
	@Override
	protected User currentUser() {
		return delegate;
	}
}
