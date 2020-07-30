package permissions;

import com.kttdevelopment.webdir.server.ServerVars;
import com.kttdevelopment.webdir.server.permissions.*;
import org.junit.*;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class PermissionsTests {

    @Test
    public void testSchema() throws UnknownHostException{
        final String testUser = "localhost";
        final String testGroup = "testGroup", testOption = "testOp", testPermission = "testPermission";

        final Map map = Map.of(
            ServerVars.Permissions.groupsKey, Map.of(
                testGroup, Map.of(
                    ServerVars.Permissions.inheritanceKey, testGroup,
                    ServerVars.Permissions.optionsKey, Map.of(
                        testOption,true
                        ),
                    ServerVars.Permissions.permissionsKey, List.of(testPermission)
                )
            ),
            ServerVars.Permissions.usersKey, Map.of(
                testUser, Map.of(
                    ServerVars.Permissions.groupsKey,List.of(testGroup),
                    ServerVars.Permissions.optionsKey,Map.of(
                        testOption,true
                    ),
                    ServerVars.Permissions.permissionsKey,List.of(testPermission)
                )
            )
        );

        final Permissions permissions = new Permissions(map);

        Assert.assertEquals("Permissions did not map group object", new PermissionsGroup(testGroup, (Map) ((Map) map.get(ServerVars.Permissions.groupsKey)).get(testGroup)), permissions.getGroups().get(0));
        Assert.assertEquals("Permissions did not map user object",new PermissionsUser(testUser, ((Map) ((Map) map.get(ServerVars.Permissions.usersKey)).get(testUser))),permissions.getUsers().get(0));
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
