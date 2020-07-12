import com.kttdevelopment.webdir.generator.Main;
import org.junit.Ignore;
import org.junit.Test;

public class ApplicationTest {

    /* to test:
        Test that no plugins load
        - plugin safe-mode skip
        Test that this can load
        - plugin no dependency
        - plugin with dependency

        Test that none of these load
        - plugin missing yml
        - plugin missing name
        - plugin malformed yml
        - plugin missing main class
        - plugin main not extends WDP
        - plugin missing dependency
        - plugin circular dependency
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
