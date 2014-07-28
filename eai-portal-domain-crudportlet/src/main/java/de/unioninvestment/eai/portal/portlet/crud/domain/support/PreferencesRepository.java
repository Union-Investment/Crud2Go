package de.unioninvestment.eai.portal.portlet.crud.domain.support;

import de.unioninvestment.eai.portal.portlet.crud.config.PreferenceConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Preference;

/**
 * Created by cmj on 28.07.14.
 */
public interface PreferencesRepository {
    public Preference getPreference(Portlet requestor, PreferenceConfig config);
}
