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


