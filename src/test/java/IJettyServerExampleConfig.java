import com.payneteasy.jetty.util.IJettyStartupParameters;
import com.payneteasy.startup.parameters.AStartupParameter;

public interface IJettyServerExampleConfig extends IJettyStartupParameters {

    @AStartupParameter(name = "JETTY_METRICS_ENABLED", value = "true")
    boolean isJettyMetricsEnabled();

}
