# Verify Wilfly 10.0.0.Final deployment issues of WAR file

ISSUE - Fixed: the problem was the module configuration having been based on stale wildfly eclipselink documentation.

## MOTIVATION
When building a .war application comprised by multiple dependencies in its **WEB-INF/lib** folder, the root persistence.xml
can be put either in the **WEB-INF/classes/META-INF/persistence.xml** folder of the .war file, or within one of the  **WEB-INF/lib/dependency.jar/META-INF/persistence.xml**.
All the containers will hunt for this persistence.xml, and it will get spotted.


If we assume that in a WAR application, withing our **WEB-INF/lib** we have multipler jar files that contain entities. Such as small software components
containin a subset of the needed entities by an application.
According  JPA 2.1 specifcaiton, it should be trivial to have our jar files scanned, by making sure that the persistence.xml
contains ```<jar-file>relativePathToJar.jar</jar-file>```.
The process is however not trivial depending on:
- Application server chosend (weblogic, wildfly, tomcatee, etc...)
- JPA implementation used (hibernate, eclipselink)
- Eclipse IDE deployment plugin (Oracle OEPE, Wildfly Jboss tools)

According to the specificaiton, the jar-file value should be a relative path of the form:
- If we assume the persistence.xml is put in **WEB-INF/classes/META-INF/persistence.xml** of the WAR file, and from there refers to jar files in **WEB-INF/lib** ,
according to the specification examples our jar-file element should be of the form: ```<jar-file>lib/relativePathToJar.jar</jar-file>```.
- If instead we assume the peristence.xml is in the **META-INF/persistence.xml** of some someJarFile.jar of our **WEB-INF/lib** folder, then the  
path should quite simply be: ```<jar-file>relativePathToJar.jar</jar-file>```.


## PROBLEM - WEBLOGIC
Each container behaves a little differently.
In weblogic, for example, it is perfectly possible to make the jar-file elements be evaluated and point to the required modules.
The weblogic behavior works exactly according to the specification if the persistence.xml is found withing a WEB-INF/lib/someModule.jar file.
But the behavior of weblogic is more problematic if we put the persistence.xml within the WEB-INF/classes/META-INF.
In the latter, scenario we have two different behaviors.
**Scenario 1 - Deploy as exploded war using OEPE plugin**
- file:/C:/dev/eclipse/workspaceWeblogicSmallProjects/.metadata/.plugins/org.eclipse.core.resources/.projects/weblogic-jar-file-createdb/beadep/orcl/weblogic-jar-file-createdb/WEB-INF/classes/../lib/weblogic-jar-file-entities.jar
- the jar-file value has to be set to ```<jar-file>../lib/weblogic-jar-file-entities.jar</jar-file>``` so that we get out of the classes parent folder and go into the WEB-INF root folder.
**Scenario 2 - Deploy the real compiled .war file**
- file:/C:/dev/WLS_12_2_1_2_0/user_projects/domains/orcl/servers/AdminServer/tmp/_WL_user/weblogic-jar-file-createdb/t70b70/war/WEB-INF/lib/../lib/weblogic-jar-file-entities.jar
- The root directory used by weblogic in this case is the WEB-INF/lib. It behaves as if the persistence.xml was witing an inner lib module.
Therefore, we can continue using our development hack  ```<jar-file>../lib/weblogic-jar-file-entities.jar</jar-file>``` to find the jar file.
It is not according to the specifcaiton but it can be made to work.

## PROBLEM - WILDFLY
With wildfly 10.0.0.FINAL, the situation is rather more obscure.
If we are using the default JPA implementation, hibernate, we can make the persistence.xml work according to specification.
But if we try to use eclipselink module instead, e.g. eclipselink 2.6.4, as we debug eclipselink jar-file processing we see two different behaviors
either when we deploy from the JBOSS plugin or the real WAR file, but in neither case does eclipselink seem to support the strange/special jboss 
virtual file system (vfs).

To use eclipse link:
- enable the module: C:\dev\Widlfly10\wildfly-10.0.0.Final\modules\system\layers\base\org\eclipse\persistence\main 
```
<module xmlns="urn:jboss:module:1.1" name="org.eclipse.persistence">
    <resources>
    	<!-- 
		THIS jipijapa-eclipselink-10.0.0.Final.jar is the fix for the jar file problem
		Here wildfly implements an eclipselink specific archieve factory that knows how to do the scanning
		of the virtual file system. This needs to be enabled by system propety.
		This was the issue all along 
	-->
        <resource-root path="jipijapa-eclipselink-10.0.0.Final.jar"/>
        <resource-root path="eclipselink-2.6.4.jar">
           <filter>
              <exclude path="javax/**" />
           </filter>
        </resource-root>
    </resources>
 
    <dependencies>
        <module name="asm.asm"/>
        <module name="javax.api"/>
        <module name="javax.annotation.api"/>
        <module name="javax.enterprise.api"/>
        <module name="javax.persistence.api"/>
        <module name="javax.transaction.api"/>
        <module name="javax.validation.api"/>
        <module name="javax.xml.bind.api"/>
        <module name="javax.ws.rs.api"/>
        <module name="org.antlr"/>
        <module name="org.apache.commons.collections"/>
        <module name="org.dom4j"/>
        <module name="org.jboss.as.jpa.spi"/>
        <module name="org.jboss.logging"/>
        <module name="org.jboss.vfs"/>
    </dependencies>
</module>
```

- In the persistence.xml use:
- - ```<provider>org.eclipse.persistence.jpa.PersistenceProvider</provider>``` 
- - Set the jpa properties:
```
<property name="jboss.as.jpa.providerModule" value="org.eclipse.persistence" />
<property name="eclipselink.target-server" value="JBoss" />
<property name="eclipselink.deploy-on-startup" value="true" /> 
```

What we see are two different behaviors, both equally eneffective.
**Scenario 1 - Deploy as exploded war using JBOSS plugin**
Eclipse link loops over:
```
vfs:/C:/dev/Widlfly10/wildfly-10.0.0.Final/bin/content/wildfly-jar-file-createdb.war/WEB-INF/lib/wildfly-jar-file-entities.jar/
vfs:/C:/dev/Widlfly10/wildfly-10.0.0.Final/bin/content/wildfly-jar-file-createdb.war/WEB-INF/classes/
```

In both cases the metadata processor comes out empty handed while doing:
```java
    // Add all the classes from the <jar> specifications.
    for (URL url : persistenceUnitInfo.getJarFileUrls()) {
        classNames.addAll(PersistenceUnitProcessor.getClassNamesFromURL(url, m_loader, null));
    }
```        

However, the interesting point here is that both Hibernate _org.hibernate.boot.archive.scan.spi.AbstractScannerImpl_ and elcipselink _org.eclipse.persistence.internal.jpa.metadata.MetadataProcessor_
are dealing with the same path to archieve: **vfs:/C:/dev/Widlfly10/wildfly-10.0.0.Final/bin/content/wildfly-jar-file-createdb.war/WEB-INF/lib/wildfly-jar-file-entities.jar/**.
Hibnerate can handle this strange vfs path, but ecliseplink clearly cannot.
In this case, this path points to an exploded folder and not a real jar file.

**Scenario 2 - Deploy the rea war file**
When deploying the real war file, without chaing anything in the persistence.xml, the behavior that the class does not get found continues to exist.
However, in this scenario, the path to the JAR file is very different.
And it corresponds to a file path that does not exist in the file system.
- vfs:/C:/dev/Widlfly10/wildfly-10.0.0.Final/bin/content/wildfly-jar-file-createdb.war/WEB-INF/lib/wildfly-jar-file-entities.jar/
- vfs:/C:/dev/Widlfly10/wildfly-10.0.0.Final/bin/content/wildfly-jar-file-createdb.war/WEB-INF/lib/wildfly-jar-file-entities.jar/

The interesting thing, once again, is that Hibernate deals with exactly the same path, bu in this case hibernate manages to discover the entity class
- vfs:/C:/dev/Widlfly10/wildfly-10.0.0.Final/bin/content/wildfly-jar-file-createdb.war/WEB-INF/lib/wildfly-jar-file-entities.jar/db/model/SomeEntity.class 



## Package
This repository devides in two identical projects. One for weblogic and one for wildfly.
The project for weblogic can deploy both with OEPE plugin and as a war file, but it is using the ../lib/entities.jar file path.
The project for wildfly can only deploy for Hibernate. In this project for wildly we have a persistence.xml, and two backup persistence_eclipselink/hierbante.xml files.
Only the the peristence_hibernate.xml template is effective.
To deploy into eclipselink, we have not alternative so far but to use the ```<class>qualifiedname</class>``` to make the deployment go through.
What the application does, when it gets deployed, is to try create a DB schema, since the persistence.xml is tuned to drop and create tables.
The Data source used in this case for doing this dummy create databse should be a NON_JTA data source.
The configuration.xml for the wildfly domain can be seen in: wildfly-jar-file-root\install


## Purpose
The purpose of this sample application is to provide a ready to use application to file a bug, so that the issue of getting jar-file element to work in eclipselink can be addressed.
Ideally, it should also be used to make sure that the paths used in the jar-file in wildfly are compliant with the JEE specification.
In the case of using hibernate, the paths seem to be JEE compliant.




## CLASSES TO DEBUG - ECLIPSELINK
For debugging the class scanning issues in eclipselink 2.6.4, the following classes are interesting.
The classes are interesting because they bare a relationship to the java metadata objects that get created by the JPA libarary based on the persistence.xml jar-file values. Namely, the are associated to path URLs created during runtime. 
- org.eclipse.persistence.internal.jpa.metadata.MetadataProcessor
- org.eclipse.persistence.jpa.PersistenceProvider.createContainerEntityManagerFactory(javax.persistence.spi.PersistenceUnitInfo, java.util.Map) line: 313	
-  javax.persistence.spi.PersistenceUnitInfo
```
PersistenceUnitMetadataImpl(version=2.1) [
	name: CreateDbPU
	jtaDataSource: null
	nonJtaDataSource: jdbc/WM6_NON_JTA_DS
	transactionType: RESOURCE_LOCAL
	provider: org.eclipse.persistence.jpa.PersistenceProvider
	classes[
	]
	packages[
	]
	mappingFiles[
	]
	jarFiles[
		lib/wildfly-jta-commit-entities.jar
	]
	validation-mode: AUTO
	shared-cache-mode: UNSPECIFIED
	properties[
		javax.persistence.lock.timeout: 2000
		eclipselink.logging.level: INFO
		eclipselink.logging.level.sql: INFO
		eclipselink.target-server: JBoss
		eclipselink.ddl-generation.output-mode: database
		eclipselink.deploy-on-startup: true
		jboss.as.jpa.providerModule: org.eclipse.persistence
		eclipselink.ddl-generation: drop-and-create-tables
		eclipselink.logging.parameters: true
	]]
    
[vfs:/C:/dev/Widlfly10/wildfly-10.0.0.Final/standalone-orcl/deployments/wildfly-jta-commit-createdb.war/WEB-INF/lib/wildfly-jta-commit-entities.jar/]    

```

## CLASSES TO DEBUG - Hibernate
In hibernate, the following class is a good starting point for debugging:
- _org.hibernate.boot.archive.scan.spi.AbstractScannerImpl_


## RELATED
[what is the right path to refer a jar file in jpa persistence.xml in a web app?](http://stackoverflow.com/questions/4433341/what-is-the-right-path-to-refer-a-jar-file-in-jpa-persistence-xml-in-a-web-app)

[Boss forum - Question and Solution](https://developer.jboss.org/thread/274048?et=notification.send#968943)

[Eclipselink - Question](https://www.eclipse.org/forums/index.php/t/1084566/)



