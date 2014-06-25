package de.unioninvestment.eai.portal.portlet.crud.liferay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.portlet.PortletLayoutListener;
import com.liferay.portal.kernel.portlet.PortletLayoutListenerException;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Layout;
import com.liferay.portal.service.LayoutLocalServiceUtil;

import de.unioninvestment.eai.portal.portlet.crud.services.ConfigurationService;

/**
 * Liferay Listener that removes configuration data on removal of a CrudPortlet instance.
 * 
 * @author cmj
 */
public class CrudPortletLayoutListener implements PortletLayoutListener {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(CrudPortletLayoutListener.class);

	private static ConfigurationService configurationService;

	@Override
	public void onAddToLayout(String portletId, long plid)
			throws PortletLayoutListenerException {
		LOGGER.debug("Crud2Go Portlet instance {} added to layout {}",
				portletId, plid);
	}

	@Override
	public void onMoveInLayout(String portletId, long plid)
			throws PortletLayoutListenerException {
		LOGGER.debug("Crud2Go Portlet instance {} moved in layout {}",
				portletId, plid);
	}

	@Override
	public void onRemoveFromLayout(String portletId, long plid)
			throws PortletLayoutListenerException {
		LOGGER.debug("Crud2Go Portlet instance {} removed in layout {}",
				portletId, plid);

		try {
			Layout layout = LayoutLocalServiceUtil.getLayout(plid);
			Group group = layout.getGroup();
			if (group.isRegularSite()) {

				configurationService.removePortletInstanceData(portletId,
						group.getGroupId());
				LOGGER.info(
						"Removed Crud2Go portlet configuration for instance {} on layout {}, community id {}",
						new Object[] { portletId, plid, group.getGroupId() });
			} else {
				LOGGER.warn(
						"Failed to remove Crud2Go portlet configuration for instance {} on layout {}. Unknown community ID!",
						portletId, plid);
			}

		} catch (PortalException e) {
			LOGGER.error("Failed to retrive the community ID", e);
		} catch (SystemException e) {
			LOGGER.error("Failed to retrive the community ID", e);
		}
	}

	public static void setConfigurationService(
			ConfigurationService newConfigurationService) {
		configurationService = newConfigurationService;
	}

}
