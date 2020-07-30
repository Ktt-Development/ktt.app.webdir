package permissions;

import com.kttdevelopment.webdir.server.ServerVars;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class PermissionsTests {

    @Test @Ignore
    public void testSchema(){
        final String testGroup = "testGroup", testOption = "testOp", testPermission = "testPermission";

        final Map test = Map.of(
                ServerVars.Permissions.groupsKey, Map.of(
                "testGroup", Map.of(
                    ServerVars.Permissions.inheritanceKey, testGroup,
                    ServerVars.Permissions.optionsKey, Map.of(
                    "testOp",true
                    ),
                    ServerVars.Permissions.permissionsKey, List.of("testPermission")
                )
            )
        );

        // test that groups and users map correctly to objects

        // test toMap equals source
    }

    @Test @Ignore
    public void testUser(){
        // test exact address and local address variants
    }

    @Test @Ignore
    public void testOptions(){
        // test options
        // test user options override options
        // test default options
    }

    @Test @Ignore
    public void testPermissionsScope(){
        // test exact perm
        // test case perm
        // test negative perm
        // test * perm
        // test negative * perm
    }

    @Test @Ignore
    public void testPermissions(){
        // test above for user and groups
    }

    @Test @Ignore
    public void testDefaults(){
        // test default list
        // test default inherit non default list
    }

    @Test @Ignore
    public void testInherited(){
        // test inheritance on user and groups
    }

}
