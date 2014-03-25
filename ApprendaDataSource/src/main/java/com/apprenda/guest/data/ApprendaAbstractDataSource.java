package com.apprenda.guest.data;

import com.apprenda.guest.api.GuestAppException;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * An abstract {@link javax.sql.DataSource} implementation. All subclasses must implement
 * the method of
 * <PRE>{@code public Connection getConnection(String username, String password) throws SQLException}</<PRE>
 * <p/>
 */
abstract public class ApprendaAbstractDataSource implements DataSource {
    IApprendaConnectionStringProvider connectionStringProvider;

    public IApprendaConnectionStringProvider getConnectionStringProvider() {
        if (connectionStringProvider==null && driverClass!=null) {
            if (driverClass.equals("net.sourceforge.jtds.jdbc.Driver")) {
                connectionStringProvider= new ApprendaJdtsConnectionStringProvider();
            } else if (driverClass.equals("com.microsoft.sqlserver.jdbc.SQLServerDriver")){
                connectionStringProvider= new ApprendaMicrosoftConnectionStringProvider();
            } else if (driverClass.equals("oracle.jdbc.OracleDriver")){
                connectionStringProvider= new ApprendaOracleConnectionStringProvider();
            } else {
                throw new RuntimeException("ApprendaDataSource does not support the selected jdbc driver");
            }
        }
        return connectionStringProvider;
    }

    public void setConnectionStringProvider(IApprendaConnectionStringProvider provider) {
        connectionStringProvider=provider;
    }

    String driverClass;

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driver) throws ClassNotFoundException {
        this.driverClass=driver;
        Class.forName(driver);
    }

    String jdbcUrl;

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl=jdbcUrl;
    }

    String user;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user=user;
    }

    String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password=password;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Connection getConnection() throws SQLException {
        try {
            return getConnection(user, password); //Use default u/pw
        } catch(GuestAppException e) {
            throw new SQLException(e.getMessage());
        }
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    //    @Override - intentionally skipping the annotation so that it compiles on jdk6
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("Not supported yet.");
    }
}
