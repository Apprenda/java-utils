package com.apprenda.guest.data;

import java.util.Properties;

public abstract class ApprendaAbstractConnectionStringProvider implements IApprendaConnectionStringProvider {
    private Properties properties;

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties props) {
        properties = props;
    }

    public void appendProperties(StringBuilder b) {
        if (properties!=null && !properties.isEmpty()) {
            if (b.charAt(b.length()-1)!=';') {
                b.append(';');
            }
            for (String key : properties.stringPropertyNames()) {
                b.append(properties.get(key));
                b.append(';');
            }
        }
    }
}
