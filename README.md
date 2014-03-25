Apprenda Java Utilities
==========

# Introduction
This repository contains utilities that would typically be used by JVM based applications running on the Apprenda platform.

# Maven Repository

The Apprenda binaries are not published to Maven Central, thus you'll need to add an additional repository to your Maven project, e.g. : 

```xml

<project ...>
...
   <repositories>
        <repository>
            <id>apprenda-public</id>
            <name>Apprenda Public</name>
            <url>https://raw.github.com/Apprenda/mvn-repo/releases</url>
        </repository>
    </repositories>
...
</project>
```

# Projects

## Apprenda Data Source

* Add the following to your list of project dependencies

```xml
<dependency>
    <groupId>com.apprenda</groupId>
    <artifactId>javautils-datasource</artifactId>
    <version>0.0.1</version>
</dependency>
```

* Usage Example (spring bean configuration)

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd">

   ....
    
    <bean id="apprendaDataSource" class="com.apprenda.guest.data.ApprendaSimpleDataSource">
        <property name="driverClass" value="net.sourceforge.jtds.jdbc.Driver" />
    </bean>
    
    ...
    
</beans>
```

