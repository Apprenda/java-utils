package com.apprenda.guest.data;

import com.apprenda.guest.api.ApprendaGuestApp;
import com.apprenda.guest.api.GuestAppContext;
import com.apprenda.guest.tenant.ConnectionConfig;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.apache.tomcat.jdbc.pool.Validator;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A tenant aware, pooled {@link javax.sql.DataSource} implementation that extends {@link ApprendaAbstractDataSource}.
 * It uses a {@code Map<String, ApprendaPoolDataSource>} to map a tenant to the
 * {@link ApprendaPoolDataSource} the tenant should use.
 * Each tenant has its own pooled {@link javax.sql.DataSource}.
 * <p/>
 * It implements {@link org.apache.tomcat.jdbc.pool.PoolConfiguration} to configure the Connection Pool.
 * These configuration properties can be set via {@code BeanProperties}.
 */
public class ApprendaTenantAwarePoolDataSource extends ApprendaAbstractDataSource implements PoolConfiguration {

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        String user=username;
        String pwd=password;
        String tenantProfileId = "__defaultTenantProfileId__";

        GuestAppContext guestCtx = ApprendaGuestApp.getContext();
        if (guestCtx.isEnabled()) {
            // NOTE : This call will throw a GuestApplicationException (a runtime exception) if the application does not have a proper single or multi-tenant context
            // the driver could catch and record it
            // This actually gets called once and fails on hibernate initialization
            ConnectionConfig config = guestCtx.getTenant().getConnectionConfig();
            jdbcUrl = getConnectionStringProvider().createConnectionUrl(config);
            user = config.getUsername();
            pwd = config.getPassword();
            tenantProfileId = guestCtx.getTenant().getProfile().getId();
        }

        ApprendaPoolDataSource dataSource = getTargetDataSource(tenantProfileId, user, pwd);
        return dataSource.getConnection(user, pwd);
    }

    // tenant profile Id to ApprendaPoolDataSource map
    private final Map<String, ApprendaPoolDataSource> tenantDataSourceMap = new ConcurrentHashMap<String, ApprendaPoolDataSource>();

    private synchronized ApprendaPoolDataSource getTargetDataSource(String tenantProfileId, String user, String pwd) {
        ApprendaPoolDataSource dataSource = tenantDataSourceMap.get(tenantProfileId);

        if (dataSource == null) {
            dataSource = createApprendaPoolDataSource(user, pwd);
            tenantDataSourceMap.put(tenantProfileId, dataSource);
        }

        return dataSource;
    }

    private ApprendaPoolDataSource createApprendaPoolDataSource(String user, String pwd) {
        PoolProperties poolProperties = new PoolProperties();
        try {
            poolProperties = (PoolProperties) propertyHolder.clone();
        } catch (CloneNotSupportedException e) {}
        poolProperties.setUrl(jdbcUrl);
        poolProperties.setDriverClassName(driverClass);
        poolProperties.setUsername(user);
        poolProperties.setPassword(pwd);

        DataSource datasource = new DataSource();
        datasource.setPoolProperties(poolProperties);

        // create an ApprendaPoolDataSource and
        // copy our properties
        ApprendaPoolDataSource apprendaPoolDataSource = new ApprendaPoolDataSource();
        apprendaPoolDataSource.setDataSource(datasource);
        apprendaPoolDataSource.setConnectionStringProvider(connectionStringProvider);
        apprendaPoolDataSource.setJdbcUrl(jdbcUrl);
        apprendaPoolDataSource.setUser(user);
        apprendaPoolDataSource.setPassword(pwd);
        return apprendaPoolDataSource;
    }

    class MyPoolProperties extends PoolProperties {
        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }

    private final MyPoolProperties propertyHolder = new MyPoolProperties();

    @Override
    public void setAbandonWhenPercentageFull(int percentage) {
        propertyHolder.setAbandonWhenPercentageFull(percentage);
    }

    @Override
    public int getAbandonWhenPercentageFull() {
        return propertyHolder.getAbandonWhenPercentageFull();
    }

    @Override
    public boolean isFairQueue() {
        return propertyHolder.isFairQueue();
    }

    @Override
    public void setFairQueue(boolean fairQueue) {
        propertyHolder.setFairQueue(fairQueue);
    }

    @Override
    public boolean isAccessToUnderlyingConnectionAllowed() {
        return propertyHolder.isAccessToUnderlyingConnectionAllowed();
    }

    @Override
    public void setAccessToUnderlyingConnectionAllowed(boolean accessToUnderlyingConnectionAllowed) {
        propertyHolder.setAccessToUnderlyingConnectionAllowed(accessToUnderlyingConnectionAllowed);
    }

    @Override
    public String getConnectionProperties() {
        return propertyHolder.getConnectionProperties();
    }

    @Override
    public void setConnectionProperties(String connectionProperties) {
        propertyHolder.setConnectionProperties(connectionProperties);
    }

    @Override
    public Properties getDbProperties() {
        return propertyHolder.getDbProperties();
    }

    @Override
    public void setDbProperties(Properties dbProperties) {
        propertyHolder.setDbProperties(dbProperties);
    }

    @Override
    public Boolean isDefaultAutoCommit() {
        return propertyHolder.isDefaultAutoCommit();
    }

    @Override
    public Boolean getDefaultAutoCommit() {
        return propertyHolder.getDefaultAutoCommit();
    }

    @Override
    public void setDefaultAutoCommit(Boolean defaultAutoCommit) {
        propertyHolder.setDefaultAutoCommit(defaultAutoCommit);
    }

    @Override
    public String getDefaultCatalog() {
        return propertyHolder.getDefaultCatalog();
    }

    @Override
    public void setDefaultCatalog(String defaultCatalog) {
        propertyHolder.setDefaultCatalog(defaultCatalog);
    }

    @Override
    public Boolean isDefaultReadOnly() {
        return propertyHolder.isDefaultReadOnly();
    }

    @Override
    public Boolean getDefaultReadOnly() {
        return propertyHolder.getDefaultReadOnly();
    }

    @Override
    public void setDefaultReadOnly(Boolean defaultReadOnly) {
        propertyHolder.setDefaultReadOnly(defaultReadOnly);
    }

    @Override
    public int getDefaultTransactionIsolation() {
        return propertyHolder.getDefaultTransactionIsolation();
    }

    @Override
    public void setDefaultTransactionIsolation(int defaultTransactionIsolation) {
        propertyHolder.setDefaultTransactionIsolation(defaultTransactionIsolation);
    }

    @Override
    public String getDriverClassName() {
        return propertyHolder.getDriverClassName();
    }

    @Override
    public void setDriverClassName(String driverClassName) {
        propertyHolder.setDriverClassName(driverClassName);
    }

    @Override
    public int getInitialSize() {
        return propertyHolder.getInitialSize();
    }

    @Override
    public void setInitialSize(int initialSize) {
        propertyHolder.setInitialSize(initialSize);
    }

    @Override
    public boolean isLogAbandoned() {
        return propertyHolder.isLogAbandoned();
    }

    @Override
    public void setLogAbandoned(boolean logAbandoned) {
        propertyHolder.setLogAbandoned(logAbandoned);
    }

    @Override
    public int getMaxActive() {
        return propertyHolder.getMaxActive();
    }

    @Override
    public void setMaxActive(int maxActive) {
        propertyHolder.setMaxActive(maxActive);
    }

    @Override
    public int getMaxIdle() {
        return propertyHolder.getMaxIdle();
    }

    @Override
    public void setMaxIdle(int maxIdle) {
        propertyHolder.setMaxIdle(maxIdle);
    }

    @Override
    public int getMaxWait() {
        return propertyHolder.getMaxWait();
    }

    @Override
    public void setMaxWait(int maxWait) {
        propertyHolder.setMaxWait(maxWait);
    }

    @Override
    public int getMinEvictableIdleTimeMillis() {
        return propertyHolder.getMinEvictableIdleTimeMillis();
    }

    @Override
    public void setMinEvictableIdleTimeMillis(int minEvictableIdleTimeMillis) {
        propertyHolder.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
    }

    @Override
    public int getMinIdle() {
        return propertyHolder.getMinIdle();
    }

    @Override
    public void setMinIdle(int minIdle) {
        propertyHolder.setMinIdle(minIdle);
    }

    @Override
    public String getName() {
        return propertyHolder.getName();
    }

    @Override
    public void setName(String name) {
        propertyHolder.setName(name);
    }

    @Override
    public int getNumTestsPerEvictionRun() {
        return propertyHolder.getNumTestsPerEvictionRun();
    }

    @Override
    public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
        propertyHolder.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
    }

    @Override
    public String getPoolName() {
        return propertyHolder.getPoolName();
    }

    @Override
    public String getUsername() {
        return propertyHolder.getUsername();
    }

    @Override
    public void setUsername(String username) {
        propertyHolder.setUsername(username);
    }

    @Override
    public boolean isRemoveAbandoned() {
        return propertyHolder.isRemoveAbandoned();
    }

    @Override
    public void setRemoveAbandoned(boolean removeAbandoned) {
        propertyHolder.setRemoveAbandoned(removeAbandoned);
    }

    @Override
    public void setRemoveAbandonedTimeout(int removeAbandonedTimeout) {
        propertyHolder.setRemoveAbandonedTimeout(removeAbandonedTimeout);
    }

    @Override
    public int getRemoveAbandonedTimeout() {
        return propertyHolder.getRemoveAbandonedTimeout();
    }

    @Override
    public boolean isTestOnBorrow() {
        return propertyHolder.isTestOnBorrow();
    }

    @Override
    public void setTestOnBorrow(boolean testOnBorrow) {
        propertyHolder.setTestOnBorrow(testOnBorrow);
    }

    @Override
    public boolean isTestOnReturn() {
        return propertyHolder.isTestOnReturn();
    }

    @Override
    public void setTestOnReturn(boolean testOnReturn) {
        propertyHolder.setTestOnReturn(testOnReturn);
    }

    @Override
    public boolean isTestWhileIdle() {
        return propertyHolder.isTestWhileIdle();
    }

    @Override
    public void setTestWhileIdle(boolean testWhileIdle) {
        propertyHolder.setTestWhileIdle(testWhileIdle);
    }

    @Override
    public int getTimeBetweenEvictionRunsMillis() {
        return propertyHolder.getTimeBetweenEvictionRunsMillis();
    }

    @Override
    public void setTimeBetweenEvictionRunsMillis(int timeBetweenEvictionRunsMillis) {
        propertyHolder.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
    }

    @Override
    public String getUrl() {
        return propertyHolder.getUrl();
    }

    @Override
    public void setUrl(String url) {
        propertyHolder.setUrl(url);
    }

    @Override
    public String getValidationQuery() {
        return propertyHolder.getValidationQuery();
    }

    @Override
    public void setValidationQuery(String validationQuery) {
        propertyHolder.setValidationQuery(validationQuery);
    }

    @Override
    public String getValidatorClassName() {
        return propertyHolder.getValidatorClassName();
    }

    @Override
    public void setValidatorClassName(String className) {
        propertyHolder.setValidatorClassName(className);
    }

    @Override
    public Validator getValidator() {
        return propertyHolder.getValidator();
    }

    @Override
    public void setValidator(Validator validator) {
        propertyHolder.setValidator(validator);
    }

    @Override
    public long getValidationInterval() {
        return propertyHolder.getValidationInterval();
    }

    @Override
    public void setValidationInterval(long validationInterval) {
        propertyHolder.setValidationInterval(validationInterval);
    }

    @Override
    public String getInitSQL() {
        return propertyHolder.getInitSQL();
    }

    @Override
    public void setInitSQL(String initSQL) {
        propertyHolder.setInitSQL(initSQL);
    }

    @Override
    public boolean isTestOnConnect() {
        return propertyHolder.isTestOnConnect();
    }

    @Override
    public void setTestOnConnect(boolean testOnConnect) {
        propertyHolder.setTestOnConnect(testOnConnect);
    }

    @Override
    public String getJdbcInterceptors() {
        return propertyHolder.getJdbcInterceptors();
    }

    @Override
    public void setJdbcInterceptors(String jdbcInterceptors) {
        propertyHolder.setJdbcInterceptors(jdbcInterceptors);
    }

    @Override
    public PoolProperties.InterceptorDefinition[] getJdbcInterceptorsAsArray() {
        return propertyHolder.getJdbcInterceptorsAsArray();
    }

    @Override
    public boolean isJmxEnabled() {
        return propertyHolder.isJmxEnabled();
    }

    @Override
    public void setJmxEnabled(boolean jmxEnabled) {
        propertyHolder.setJmxEnabled(jmxEnabled);
    }

    @Override
    public boolean isPoolSweeperEnabled() {
        return propertyHolder.isPoolSweeperEnabled();
    }

    @Override
    public boolean isUseEquals() {
        return propertyHolder.isUseEquals();
    }

    @Override
    public void setUseEquals(boolean useEquals) {
        propertyHolder.setUseEquals(useEquals);
    }

    @Override
    public long getMaxAge() {
        return propertyHolder.getMaxAge();
    }

    @Override
    public void setMaxAge(long maxAge) {
        propertyHolder.setMaxAge(maxAge);
    }

    @Override
    public boolean getUseLock() {
        return propertyHolder.getUseLock();
    }

    @Override
    public void setUseLock(boolean useLock) {
        propertyHolder.setUseLock(useLock);
    }

    @Override
    public void setSuspectTimeout(int seconds) {
        propertyHolder.setSuspectTimeout(seconds);
    }

    @Override
    public int getSuspectTimeout() {
        return propertyHolder.getSuspectTimeout();
    }

    @Override
    public void setDataSource(Object ds) {
        propertyHolder.setDataSource(ds);
    }

    @Override
    public Object getDataSource() {
        return propertyHolder.getDataSource();
    }

    @Override
    public void setDataSourceJNDI(String jndiDS) {
        propertyHolder.setDataSourceJNDI(jndiDS);

    }

    @Override
    public String getDataSourceJNDI() {
        return propertyHolder.getDataSourceJNDI();
    }

    @Override
    public boolean isAlternateUsernameAllowed() {
        return propertyHolder.isAlternateUsernameAllowed();
    }

    @Override
    public void setAlternateUsernameAllowed(boolean alternateUsernameAllowed) {
        propertyHolder.setAlternateUsernameAllowed(alternateUsernameAllowed);
    }

    @Override
    public void setCommitOnReturn(boolean commitOnReturn) {
        propertyHolder.setCommitOnReturn(commitOnReturn);
    }

    @Override
    public boolean getCommitOnReturn() {
        return propertyHolder.getCommitOnReturn();
    }

    @Override
    public void setRollbackOnReturn(boolean rollbackOnReturn) {
        propertyHolder.setRollbackOnReturn(rollbackOnReturn);
    }

    @Override
    public boolean getRollbackOnReturn() {
        return propertyHolder.getRollbackOnReturn();
    }

    @Override
    public void setUseDisposableConnectionFacade(boolean useDisposableConnectionFacade) {
        propertyHolder.setUseDisposableConnectionFacade(useDisposableConnectionFacade);
    }

    @Override
    public boolean getUseDisposableConnectionFacade() {
        return propertyHolder.getUseDisposableConnectionFacade();
    }

    @Override
    public void setLogValidationErrors(boolean logValidationErrors) {
        propertyHolder.setLogValidationErrors(logValidationErrors);
    }

    @Override
    public boolean getLogValidationErrors() {
        return propertyHolder.getLogValidationErrors();
    }

    @Override
    public boolean getPropagateInterruptState() {
        return propertyHolder.getPropagateInterruptState();
    }

    @Override
    public void setPropagateInterruptState(boolean propagateInterruptState) {
        propertyHolder.setPropagateInterruptState(propagateInterruptState);
    }
}
