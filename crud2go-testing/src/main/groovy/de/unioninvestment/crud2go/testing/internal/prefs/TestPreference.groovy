package de.unioninvestment.crud2go.testing.internal.prefs

import de.unioninvestment.eai.portal.portlet.crud.config.PreferenceConfig
import de.unioninvestment.eai.portal.portlet.crud.domain.model.Preference

/**
 * Preference as provided by the testing framework.
 *
 * Created by cmj on 28.07.14.
 */
class TestPreference extends Preference{
    private Map<String,String> backingMap

    /**
     * Constructor.
     *
     * @param config the XML configuration preference entity
     * @param backingMap the backing map that stores the actual values. Can be null, which means that no values are set
     */
    TestPreference(PreferenceConfig config, Map<String,String> backingMap) {
        super(config)
        this.backingMap = backingMap
    }

    String getValue() {
        (backingMap?.get(key)) ?: config.default
    }
}
