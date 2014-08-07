package de.unioninvestment.crud2go.testing.internal

import de.unioninvestment.crud2go.testing.CrudTestConfigBuilder
import de.unioninvestment.eai.portal.portlet.crud.config.AuthenticationRealmConfig
import de.unioninvestment.eai.portal.portlet.crud.domain.model.authentication.Realm
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.impl.client.DefaultHttpClient
/**
 * Created by cmj on 07.08.14.
 */
class CrudTestAuthenticationRealm extends Realm {

    private String username
    private String password

    CrudTestAuthenticationRealm(AuthenticationRealmConfig config) {
        super(config, null)
        def prefs = CrudTestConfigBuilder.currentBuilder.portletPreferences
        username = prefs[config.credentials.username.preferenceKey]
        password = prefs[config.credentials.password.preferenceKey]
    }

    @Override
    void applyBasicAuthentication(DefaultHttpClient httpClient) {
        httpClient.getCredentialsProvider().setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(username, password));
    }
}
