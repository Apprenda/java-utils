package com.apprenda.guest.data;

import com.apprenda.guest.tenant.ConnectionConfig;

public class ApprendaJdtsConnectionStringProvider extends ApprendaAbstractConnectionStringProvider {
    public String createConnectionUrl(ConnectionConfig config) {
        StringBuilder b = new StringBuilder();
        b.append("jdbc:jtds:sqlserver://");
        String server = config.getServer();
        String host;
        String instance=null;
        Integer backslashPos = server.indexOf("\\");
        if (backslashPos>=0) {
            host = server.substring(0, backslashPos);
            instance = server.substring(backslashPos+1);
        } else {
            host = server;
        }
        b.append(host);

        Integer port = config.getPort();
        if (port!=null) {
            b.append(':');
            b.append(port);
        }

        String database = config.getDatabaseName();
        if (!isNullOrWhitespace(database)) {
            b.append('/');
            b.append(database);
        }
        b.append(';');
        if (!isNullOrWhitespace(instance)) {
            b.append("instance=");
            b.append(instance);
            b.append(';');
        }

        appendProperties(b);

        return b.toString();
    }

    private boolean isNullOrWhitespace(String s) {
        return s==null || s.trim().length()==0;
    }
}
