package com.apprenda.guest.data;

import com.apprenda.guest.tenant.ConnectionConfig;

public class ApprendaMicrosoftConnectionStringProvider extends ApprendaAbstractConnectionStringProvider {
    public String createConnectionUrl(ConnectionConfig config) {
        StringBuilder b = new StringBuilder();
        b.append("jdbc:sqlserver://");
        b.append(config.getServer());
        Integer port = config.getPort();
        if (port!=null) {
            b.append(':');
            b.append(port);
        }
        b.append(';');
        String database = config.getDatabaseName();
        if (!isNullOrWhitespace(database)) {
            b.append("databaseName=");
            b.append(database);
            b.append(";");
        }

        appendProperties(b);

        return b.toString();
    }

    private boolean isNullOrWhitespace(String s) {
        return s==null || s.trim().length()==0;
    }
}
