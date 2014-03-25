package com.apprenda.guest.data;

import com.apprenda.guest.tenant.ConnectionConfig;

public class ApprendaOracleConnectionStringProvider extends ApprendaAbstractConnectionStringProvider {
    public String createConnectionUrl(ConnectionConfig config) {
        //jdbc:oracle:thin:@//localhost:1521/orcl
        StringBuilder b = new StringBuilder();
        b.append("jdbc:oracle:thin:@//");
        b.append(config.getServer());
        Integer port = config.getPort();
        if (port!=null) {
            b.append(':');
            b.append(port);
        }
        b.append('/');
        b.append(config.getServiceName());

        appendProperties(b);
        return b.toString();
    }
}
