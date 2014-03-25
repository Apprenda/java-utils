package com.apprenda.guest.data;

import com.apprenda.guest.api.ApprendaGuestApp;
import com.apprenda.guest.api.GuestAppContext;
import com.apprenda.guest.tenant.ConnectionConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * A simple {@link javax.sql.DataSource} implementation that extends {@link ApprendaAbstractDataSource}.
 * <p/>
 */
public class ApprendaSimpleDataSource  extends ApprendaAbstractDataSource {

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        String user=username;
        String pwd=password;

        GuestAppContext guestCtx = ApprendaGuestApp.getContext();
        if (guestCtx.isEnabled()) {
            // NOTE : This call will throw a GuestApplicationException (a runtime exception) if the application does not have a proper single or multi-tenant context
            // the driver could catch and record it
            // This actually gets called once and fails on hibernate initialization
            ConnectionConfig config = guestCtx.getTenant().getConnectionConfig();
            jdbcUrl = getConnectionStringProvider().createConnectionUrl(config);
            user = config.getUsername();
            pwd = config.getPassword();
        }

        return DriverManager.getConnection(jdbcUrl, user, pwd);
    }

}
