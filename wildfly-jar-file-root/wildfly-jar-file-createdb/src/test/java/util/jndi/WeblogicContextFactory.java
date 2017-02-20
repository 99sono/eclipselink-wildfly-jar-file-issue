/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.jndi;

import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author b7godin
 */
public final class WeblogicContextFactory {

    public static WeblogicContextFactory SINGLETON = new WeblogicContextFactory();

    private WeblogicContextFactory() {
    }

    public InitialContext create() {
        Properties initialContextProperties = new Properties();
        initialContextProperties.setProperty("java.naming.factory.initial", "weblogic.jndi.WLInitialContextFactory");
        initialContextProperties.setProperty("java.naming.provider.url", "t3://localhost:7001");
        try {
            return new InitialContext(initialContextProperties);
        } catch (NamingException ex) {
            throw new RuntimeException("could not create initial context for weblogic application server", ex);
        }
    }

}
