package de.unioninvestment.crud2go.testing

import de.unioninvestment.eai.portal.portlet.crud.config.PreferenceConfig
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Portlet
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Preference
import de.unioninvestment.eai.portal.portlet.crud.domain.support.PreferencesRepository

/**
 * Implementation of the PreferencesRepository inside the Testing Framework. Returns Preference instances that itself
 * return preferences set during the config build.
 *
 * Created by cmj on 28.07.14.
 */
class TestPreferencesRepository implements PreferencesRepository{

    private Map<Portlet, Map<String,String>> preferences = [:]

    @Override
    Preference getPreference(Portlet requestor, PreferenceConfig config) {
        return new TestPreference(config, preferences.get(requestor))
    }

    void setPreference(Portlet requestor, String key, String value) {
        def backingMap = preferences.get(requestor)
        if (!preferences.containsKey(requestor)) {
            backingMap = [:]
            preferences.put(requestor, backingMap)
        }
        backingMap[key] = value
    }

    void removePreferences(Portlet requestor) {
        preferences.remove(requestor)
    }
}
