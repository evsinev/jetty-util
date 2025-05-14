import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payneteasy.apiservlet.GsonJettyContextHandler;
import com.payneteasy.apiservlet.IRequestValidator;
import com.payneteasy.jetty.util.*;
import org.eclipse.jetty.ee8.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import static com.payneteasy.startup.parameters.StartupParametersFactory.getStartupParameters;

public class JettyServerExampleApplication {

    static {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    private static final Logger LOG = LoggerFactory.getLogger( JettyServerExampleApplication.class );

    public static void main(String[] args) {
        IJettyServerExampleConfig config = getStartupParameters(IJettyServerExampleConfig.class);

        JettyServer jetty = new JettyServerBuilder()
                .startupParameters(config)
                .contextOption(JettyContextOption.NO_SESSIONS)

                .filter("/*", new PreventStackTraceFilter())

                .servlet("/health", new HealthServlet())

                .contextListener(JettyServerExampleApplication::configureContext)

                .build();

        jetty.startJetty();


    }

    private static void configureContext(ServletContextHandler aHandler) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        GsonJettyContextHandler gsonHandler = new GsonJettyContextHandler(
                aHandler
                , gson
                , (aException, aContext) -> LOG.error("Error", aException)
                , new IRequestValidator() {
                    @Override
                    public <T> T validateRequest(T aRequest, Class<T> aRequestClass) {
                        return aRequest;
                    }
                }
        );



    }
}
