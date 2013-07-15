package de.unioninvestment.eai.portal.portlet.crud.domain.support;

/**
 * Interface to be implemented by the Vaadin Application to fulfill the
 * requirements of the domain.
 * 
 * @author carsten.mjartan
 */
public interface InitializingUI {

	/**
	 * @return <code>true</code>, if the Application is currently initializing a
	 *         new configuration.
	 */
	boolean isInitializing();

}
