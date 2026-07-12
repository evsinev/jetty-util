# CLAUDE.md

Guidance for Claude Code working in this repo, and a copy-paste recipe for wiring the
`/app-status` endpoint into any application that depends on jetty-util.

## Build
- JDK 21, Maven wrapper (`./mvnw`). Deps come from the private repo `https://maven.pne.io`.
- Jetty 12 **ee8** environment → the servlet API is `javax.servlet` (NOT jakarta).
- `./mvnw package` / `./mvnw test`.

## Adding `/app-status` to a consuming application

`com.payneteasy.jetty.util.appstatus.AppStatusServlet` is a ready-made, Bearer-token-protected
GET endpoint that reports the running instance's build version, instance name, hostname, port
and uptime as JSON, plus an optional `/match-all/host/<h>/instance/<i>/port/<p>` identity
assertion (412 on mismatch, 200 on match, 401 without a valid token).

Follow these steps in the target app:

### 1. Dependency
The app must depend on `com.payneteasy:jetty-util` (use the latest release). If it already uses
`JettyServerBuilder` (directly or via `mini-core`), jetty-util is on the classpath. For an app on
**raw Jetty**, add it explicitly; if it doesn't use `JettyServerBuilder`/metrics, exclude the
transitive Prometheus/Micrometer to keep the uber-jar lean:

```xml
<dependency>
    <groupId>com.payneteasy</groupId>
    <artifactId>jetty-util</artifactId>
    <exclusions>
        <exclusion><groupId>io.prometheus</groupId><artifactId>*</artifactId></exclusion>
        <exclusion><groupId>io.micrometer</groupId><artifactId>*</artifactId></exclusion>
    </exclusions>
</dependency>
```

### 2. Two startup parameters
Add to the app's startup-config interface:

```java
@AStartupParameter(name = "APP_INSTANCE_NAME", value = "my-app")
String appInstanceName();

@AStartupParameter(name = "APP_STATUS_TOKEN", value = "***", maskVariable = true)
String appStatusToken();
```

### 3. An `IJettyStartupParameters` for the servlet
`AppStatusInfo.jettyConfig` needs an `IJettyStartupParameters`; the servlet only reads
`getJettyPort()` off it (for the reported port and the match check).

- **If the app's config already `extends IJettyStartupParameters`** (the JettyServerBuilder case) —
  pass `config` directly, done.
- **If it does NOT** (raw Jetty with its own port var, e.g. `WEB_SERVER_PORT`) — add a tiny adapter
  that aliases `getJettyPort()` to the app's real port env var (override pattern is supported by
  `startup-parameters`):

```java
public interface IAppStatusConfig extends IJettyStartupParameters {

    @Override
    @AStartupParameter(name = "WEB_SERVER_PORT", value = "8080")   // the app's actual port var
    int getJettyPort();

    @AStartupParameter(name = "APP_INSTANCE_NAME", value = "my-app")
    String appInstanceName();

    @AStartupParameter(name = "APP_STATUS_TOKEN", value = "***", maskVariable = true)
    String appStatusToken();
}
```

### 4. Build the info and register the servlet at `/app-status/*`

```java
AppStatusInfo info = AppStatusInfo.builder()
        .jettyConfig     ( config                    )   // or the IAppStatusConfig adapter
        .instanceName    ( config.appInstanceName()  )
        .applicationClass( MyApplication.class       )   // its package's Implementation-Version is reported
        .bearerToken     ( config.appStatusToken()   )
        .build();
```

- JettyServerBuilder app: `.servlet("/app-status/*", new AppStatusServlet(info))`
- Raw Jetty app: `context.addServlet(new ServletHolder(new AppStatusServlet(info)), "/app-status/*")`

### 5. Make the version resolve (do not skip)
`appVersion` = `applicationClass.getPackage().getImplementationVersion()`, which is `null` unless
the runnable jar's `MANIFEST.MF` carries `Implementation-Version`. Add it to the shade plugin's
manifest transformer (a CI/release build sets `${project.version}` from the tag):

```xml
<transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
    <mainClass>...</mainClass>
    <manifestEntries>
        <Implementation-Version>${project.version}</Implementation-Version>
    </manifestEntries>
</transformer>
```

Without this the endpoint returns `"appVersion":"error-no-impl-version-<epoch>"`.

### 6. Verify

```bash
curl -i localhost:<port>/<context>/app-status/                                       # 401 (no token)
curl -s -H 'Authorization: Bearer <token>' localhost:<port>/<context>/app-status/    # 200 JSON
curl -s -o /dev/null -w '%{http_code}\n' -H 'Authorization: Bearer <token>' \
     localhost:<port>/<context>/app-status/match-all/port/9999                       # 412 mismatch
```
