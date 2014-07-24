package de.unioninvestment.crud2go.testing

import groovy.transform.CompileStatic;
import org.mockito.ArgumentMatcher;

import com.google.common.base.Objects;
import com.vaadin.ui.Notification;

@CompileStatic
public class NotificationMatcher extends ArgumentMatcher<Notification> {

	private String caption;
	private String description;

	public NotificationMatcher(String caption, String description) {
		this.caption = caption;
		this.description = description;
	}

	@Override
	public boolean matches(Object argument) {
		if (argument instanceof Notification) {
			Notification notification = (Notification) argument;
			if (Objects.equal(notification.getCaption(), caption)
					&& Objects
							.equal(notification.getDescription(), description)) {
				return true;
			}
		}
		return false;
	}

}
