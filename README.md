# Jetty utilities

Util classes for jetty


## How to add it into your app

### Maven


```xml
<repositories>
    <repository>
        <id>pne</id>
        <name>payneteasy repo</name>
        <url>https://maven.pne.io</url>
    </repository>
</repositories>
  
<dependency>
    <groupId>com.payneteasy</groupId>
    <artifactId>jetty-util</artifactId>
    <version>1.0-1</version>
</dependency>
```

### Example

```java
public interface IEditorConfig extends IJettyStartupParameters {

    @AStartupParameter(name = "STORE_DIR", value = "/opt/store")
    File storeDir();

}

...

IEditorConfig config = StartupParametersFactory.getStartupParameters(IEditorConfig.class);

new JettyServerBuilder()
        .startupParameters(config)
        .servlet("/health/*" , new HealthServlet())
        .contextOption(NO_SESSIONS)
        .contextListener(EditorApplication::onJettyContext)
        .build()
        .startJetty();

...

```

### App Status

```java
// WebApplication

AppStatusInfo appStatusInfo = AppStatusInfo.builder()
        .jettyConfig     ( config                   )
        .instanceName    ( config.appInstanceName() )
        .applicationClass( WebApplication.class     )
        .bearerToken     ( config.appStatusToken()  )
        .build();

new JettyServerBuilder()
 ...
    .servlet("/app-status/*", new AppStatusServlet(appStatusInfo))
 ...

// IStartupConfig

//region app status
@AStartupParameter(name = "APP_INSTANCE_NAME", value = "web-app")
String appInstanceName();

@AStartupParameter(name = "APP_STATUS_TOKEN", value = "***", maskVariable = true)
String appStatusToken();
//endregion
```

```xml
// shade plugin transformer org.apache.maven.plugins.shade.resource.ManifestResourceTransformer
<manifestEntries>
    <Implementation-Version>${project.version}</Implementation-Version>
</manifestEntries>
```


