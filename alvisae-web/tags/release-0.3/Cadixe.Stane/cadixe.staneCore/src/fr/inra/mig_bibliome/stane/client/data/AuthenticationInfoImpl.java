/*
 *
 *      This software is a result of Quaero project and its use must respect the rules of the Quaero Project Consortium Agreement.
 *
 *      Copyright Institut National de la Recherche Agronomique, 2010-2011.
 *
 */
package fr.inra.mig_bibliome.stane.client.data;

import fr.inra.mig_bibliome.stane.shared.data3.Queries.AuthenticationInfo;

/**
 *
 * @author fpapazian
 */
public class AuthenticationInfoImpl implements AuthenticationInfo {

    private static native String Base64Encode(String input) /*-{ return $wnd.Base64.encode(input);  }-*/;

    public static AuthenticationInfoImpl getForUserPassord(String name, String password) {
        return getForUserToken(name, Base64Encode(name + ":" + password));
    }

    public static AuthenticationInfoImpl getForUserToken(String name, String token) {
        if (name != null && !name.isEmpty() && token != null && !token.isEmpty()) {
            return new AuthenticationInfoImpl(name, token);
        } else {
            return null;
        }
    }
    private Integer id = null;
    private final String name;
    private final String authenticationToken;

    private AuthenticationInfoImpl(String name, String authenticationToken) {
        this.name = name;
        this.authenticationToken = authenticationToken;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getBase64EncodedBasicAuthToken() {
        return authenticationToken;
    }
}