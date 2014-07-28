package de.unioninvestment.eai.portal.portlet.crud.domain.support;

import de.unioninvestment.eai.portal.portlet.crud.config.PreferenceConfig;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet;
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Preference;

/**
 * Interface for a repository that provides Preference Instances. Required for the testing framework.
 * Created by cmj on 28.07.14.
 */
public interface PreferencesRepository {
    public Preference getPreference(Portlet requestor, PreferenceConfig config);
}
