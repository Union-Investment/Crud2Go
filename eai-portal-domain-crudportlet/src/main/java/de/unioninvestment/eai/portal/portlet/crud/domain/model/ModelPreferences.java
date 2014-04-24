package de.unioninvestment.eai.portal.portlet.crud.domain.model;

/**
 * Preferences that influence the creation of a portlet model, but override
 * settings of the configuration XML.
 * 
 * @author cmj
 */
public class ModelPreferences {
	private String pageHeight;
	private Integer pageMinimumHeight;

	/**
	 * @return the minimum height of the page or <code>null</code>, if not set
	 */
	public Integer getPageMinimumHeight() {
		return pageMinimumHeight;
	}

	/**
	 * @param pageMinimumHeight
	 *            the minimum height of a page
	 */
	public void setPageMinimumHeight(Integer pageMinimumHeight) {
		this.pageMinimumHeight = pageMinimumHeight;
	}

	/**
	 * @return the height of the page or <code>null</code>, if not set
	 */
	public String getPageHeight() {
		return pageHeight;
	}

	/**
	 * @param pageHeight
	 *            the height of the page
	 */
	public void setPageHeight(String pageHeight) {
		this.pageHeight = pageHeight;
	}
}
