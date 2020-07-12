import com.kttdevelopment.webdir.generator.Main;
import org.junit.Ignore;
import org.junit.Test;

public class ApplicationTest {

    /* to test:
        - plugin safemode skip
        - plugin missing yml
        - plugin missing name
        - plugin malformed yml
        - plugin missing main class
        - plugin main not extends WDP
        - plugin missing dependency
        - todo: plugin circular dependency
        - todo: plugin no dependency
        - plugin timed out
        - plugin internal exception

        - page render order
        - page render skip exception
     */

    @Test @Ignore
    public void testInitialization(){
        Main.main(new String[]{});
    }


}
