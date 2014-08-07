package de.unioninvestment.crud2go.testing.internal

import de.unioninvestment.eai.portal.portlet.crud.config.AuthenticationRealmConfig
import de.unioninvestment.eai.portal.portlet.crud.domain.model.ModelFactory
import de.unioninvestment.eai.portal.portlet.crud.domain.model.authentication.Realm

/**
 * Created by cmj on 07.08.14.
 */
class CrudTestModelFactory extends ModelFactory {

    @Override
    Realm getAuthenticationRealm(AuthenticationRealmConfig config) {
        return new CrudTestAuthenticationRealm(config)
    }
}
