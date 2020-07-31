package permissions;

import com.kttdevelopment.webdir.server.ServerVars;
import com.kttdevelopment.webdir.server.permissions.*;
import org.junit.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
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

    @Test
    public void testUser() throws UnknownHostException{
        final Permissions[] testPermissions = {
            new Permissions(Map.of(
                ServerVars.Permissions.usersKey,Map.of(
                    "localhost", Collections.emptyMap()
                )
            )),
            new Permissions(Map.of(
                ServerVars.Permissions.usersKey,Map.of(
                    InetAddress.getLocalHost().getHostAddress(), Collections.emptyMap()
                )
            )),
            new Permissions(Map.of(
                ServerVars.Permissions.usersKey,Map.of(
                    InetAddress.getLoopbackAddress().getHostAddress(), Collections.emptyMap()
                )
            ))
        };

        final InetAddress[] tests = {
            InetAddress.getLocalHost(),
            InetAddress.getLoopbackAddress(),
            InetAddress.getByName("localhost")
        };

        for(final InetAddress test : tests)
            for(final Permissions permissions : testPermissions)
                Assert.assertNotNull("Test address " + test.getHostAddress() + " could not find user in " + permissions,permissions.getUser(test));
    }

    @Test
    public void testOptions() throws UnknownHostException{
        final String testDefaultOption = "testOp";
        final String testDefaultOptionOverride = "testOpOver";
        final String testUserOption = "userOp";

        final Map map = Map.of(
            ServerVars.Permissions.groupsKey, Map.of(
                ServerVars.Permissions.defaultKey, Map.of(
                    ServerVars.Permissions.optionsKey, Map.of(
                        ServerVars.Permissions.defaultKey, true,
                        testDefaultOptionOverride, false,
                        testDefaultOption, true
                    )
                )
            ),
            ServerVars.Permissions.usersKey, Map.of(
                "localhost",Map.of(
                    ServerVars.Permissions.optionsKey, Map.of(
                        testDefaultOptionOverride,true,
                        testUserOption,true
                    )
                )
            )
        );

        final Permissions permissions = new Permissions(map);

        Assert.assertEquals("User option from permissions did not match map options",true,permissions.getOption(InetAddress.getLocalHost(),testUserOption));
        Assert.assertEquals("User option did not override default option",true,permissions.getOption(InetAddress.getLocalHost(),testDefaultOptionOverride));
        Assert.assertEquals("User option did not inherit default option",true,permissions.getOption(InetAddress.getLocalHost(),testDefaultOption));
    }

    @Test
    public void testPermissionsScope() throws UnknownHostException{
        final InetAddress local = InetAddress.getLocalHost();

        final Map<String,String> validPerms = Map.of(
            "test.permission","test.permission",
            "asterisk.*","asterisk.any"
        );

        final Map mapValid = Map.of(
            ServerVars.Permissions.usersKey, Map.of(
                "localhost",Map.of(
                    ServerVars.Permissions.permissionsKey, new ArrayList(validPerms.keySet())
                )
            )
        );

        final Permissions permissions = new Permissions(mapValid);

        validPerms.forEach( (k,v) -> Assert.assertTrue("User did not have permission for '" + v + "' but permissions specified '" + k + "' (allowed)", permissions.hasPermission(local, v)));

        //

        final Map<String,String> negativePerms = Map.of(
            "test.permission","test.Permission",
            "!test.negative","test.negative",
            "!negative.*","negative.any"
        );

        final Map negativeMap = Map.of(
            ServerVars.Permissions.usersKey, Map.of(
                "localhost",Map.of(
                    ServerVars.Permissions.permissionsKey, new ArrayList(negativePerms.keySet())
                )
            )
        );

        final Permissions invalidPermissions = new Permissions(negativeMap);

        negativePerms.forEach( (k,v) -> Assert.assertFalse("User had permission for '" + v + "' but permissions specified '" + k + "' (not allowed)", invalidPermissions.hasPermission(local, v)));
    }

    @Test
    public void testInheritedDefault(){
        final String testInherited = "inherited";
        final Map map = Map.of(
            ServerVars.Permissions.groupsKey, Map.of(
                "default",Map.of(
                    ServerVars.Permissions.optionsKey, Map.of(
                        ServerVars.Permissions.defaultKey, true
                    ),
                    ServerVars.Permissions.inheritanceKey,testInherited
                ),
                testInherited,Collections.emptyMap()
            )
        );

        final Permissions permissions = new Permissions(map);
        Assert.assertEquals("Default group did not inherit correct amount of groups from " + map + "(was " + permissions + ')',2, permissions.getDefaultGroupsAndInherited().size());
    }

    @Test
    public void testInheritedLoop(){
        final String testInherited = "inherited", testInherited2 = "inherited2";
        final Map map = Map.of(
            ServerVars.Permissions.groupsKey, Map.of(
                "default",Map.of(
                    ServerVars.Permissions.optionsKey, Map.of(
                        ServerVars.Permissions.defaultKey, true
                    ),
                    ServerVars.Permissions.inheritanceKey,testInherited
                ),
                testInherited,Map.of(
                    ServerVars.Permissions.inheritanceKey,  testInherited2
                ),
                testInherited2, Map.of(
                    ServerVars.Permissions.inheritanceKey, "default"
                )
            )
        );

        try{
            final Permissions permissions = new Permissions(map);
            Assert.assertEquals("Default group did not inherit correct amount of groups from " + map + "(was " + permissions + ')',3, permissions.getDefaultGroupsAndInherited().size());
        }catch(final StackOverflowError ignored){
            Assert.fail("Permissions encountered an infinite loop when trying to load " + map);
        }
    }

    @Test
    public void testInheritedUser() throws UnknownHostException{
        // test that use inherits default op/p and group op/perms
        final String testInheritedDefault = "inheritedDefault";
        final String testOptionDefault = "testOpDef", testPermissionDefault = "default.permission";
        final String testInherited = "inherited";
        final String testOption = "testOp", testPermission = "test.permission";

        final Map map = Map.of(
            ServerVars.Permissions.groupsKey, Map.of(
                "default",Map.of(
                    ServerVars.Permissions.optionsKey, Map.of(
                        ServerVars.Permissions.defaultKey, true
                    ),
                    ServerVars.Permissions.inheritanceKey,testInheritedDefault
                ),
                testInheritedDefault,Map.of(
                    ServerVars.Permissions.optionsKey, Map.of(
                        testOptionDefault, true
                    ),
                    ServerVars.Permissions.permissionsKey, List.of(
                        testPermissionDefault
                    )
                ),
                testInherited,Map.of(
                    ServerVars.Permissions.optionsKey, Map.of(
                        testOption, true
                    ),
                    ServerVars.Permissions.permissionsKey, List.of(
                        testPermission
                    )
                )
            ),
            ServerVars.Permissions.usersKey, Map.of(
                "localhost", Map.of(
                    ServerVars.Permissions.groupsKey, testInherited
                )
            )
        );

        final Permissions permissions = new Permissions(map);

        final InetAddress user = InetAddress.getLocalHost();

        Assert.assertEquals("User did not inherit default option from " + map + "(was " + permissions + ')',true,permissions.getOption(user,testOptionDefault));
        Assert.assertTrue("User did not inherit default permission from " + map + "(was " + permissions + ')', permissions.hasPermission(user, testPermissionDefault));
        Assert.assertEquals("User did not inherit group option from " + map + "(was " + permissions + ')',true,permissions.getOption(user,testOption));
        Assert.assertTrue("User did not inherit group permission from " + map + "(was " + permissions + ')', permissions.hasPermission(user, testPermission));
    }

}
