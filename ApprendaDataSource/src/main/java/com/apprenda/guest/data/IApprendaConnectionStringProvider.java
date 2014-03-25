package com.apprenda.guest.data;

import com.apprenda.guest.tenant.ConnectionConfig;

public interface IApprendaConnectionStringProvider {
    String createConnectionUrl(ConnectionConfig config);
}
