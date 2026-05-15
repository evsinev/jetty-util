package example;

import com.payneteasy.jetty.util.SafeHttpServlet;
import com.payneteasy.jetty.util.SafeServletRequest;
import com.payneteasy.jetty.util.SafeServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class WaitServletExample extends SafeHttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger( WaitServletExample.class );

    @Override
    protected void doSafeGet(SafeServletRequest aRequest, SafeServletResponse aResponse) {
        Duration sleepDuration = Duration.ofSeconds(30);
        aResponse.setStatus(200);
        aResponse.writeChunk("Sleeping for " + sleepDuration + "\n");
        try {
            Thread.sleep(sleepDuration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.error( "Interrupted", e);
        }
        aResponse.writeChunk("Done sleeping for " + sleepDuration + "\n");
    }
}
