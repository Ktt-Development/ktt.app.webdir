import com.kttdevelopment.webdir.server.ServerVars;
import com.kttdevelopment.webdir.server.permissions.PermissionsGroup;
import com.kttdevelopment.webdir.server.permissions.PermissionsUser;
import org.junit.Assert;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public class PermissionsTests {

    @Test
    public void testUserSchema() throws UnknownHostException{
        final Map map = Map.of(
            ServerVars.Permissions.groupsKey, List.of("testGroup"),
            ServerVars.Permissions.optionsKey, Map.of("testOption",true),
            ServerVars.Permissions.permissionsKey, List.of("test_permission")
        );

        final InetAddress address = InetAddress.getLocalHost();
        final PermissionsUser user = new PermissionsUser(address,map);

        Assert.assertEquals("User did not match source address",address,user.getUser());
        Assert.assertArrayEquals("User group did not match map group",((List) map.get(ServerVars.Permissions.groupsKey)).toArray(),user.getGroups());
        Assert.assertEquals("User options did not match map options",map.get(ServerVars.Permissions.optionsKey),user.getOptions());
        Assert.assertArrayEquals("User permissions did not match map permissions", ((List) map.get(ServerVars.Permissions.permissionsKey)).toArray(), user.getPermissions());
    }

    @Test
    public void testUserSchemaSingleton() throws UnknownHostException{
        final Map map = Map.of(
            ServerVars.Permissions.groupsKey, "testGroup"
        );

        final InetAddress address = InetAddress.getLocalHost();
        final PermissionsUser user = new PermissionsUser(address,map);

        Assert.assertEquals("User group did not match map group",map.get(ServerVars.Permissions.groupsKey),user.getGroups()[0]);
    }

    @Test
    public void testUserSchemaPermissionLower() throws UnknownHostException{
        final Map map = Map.of(
            ServerVars.Permissions.permissionsKey, List.of("testPermissions")
        );

        final InetAddress address = InetAddress.getLocalHost();
        final PermissionsUser user = new PermissionsUser(address,map);

        Assert.assertEquals("User permissions did not match map permissions", ((List<String>) map.get(ServerVars.Permissions.permissionsKey)).get(0).toLowerCase(), user.getPermissions()[0]);
    }

    @Test
    public void testUserAddressString() throws UnknownHostException{
        final InetAddress address = InetAddress.getLocalHost();
        final PermissionsUser user = new PermissionsUser("localhost",new HashMap());

        Assert.assertEquals("User did not match source address",address,user.getUser());
    }

    //

    @Test
    public void testGroupSchema(){
        final Map map = Map.of(
            "inheritance",List.of("testGroup"),
            ServerVars.Permissions.optionsKey,Map.of("default",true),
            ServerVars.Permissions.permissionsKey,List.of("*")
        );

        final String groupName = "testGroup";
        final PermissionsGroup group = new PermissionsGroup(groupName,map);

        Assert.assertEquals("Group did not match source",groupName,group.getGroup());
        Assert.assertArrayEquals("Group inheritance did not match map inheritance", ((List) map.get("inheritance")).toArray(),group.getInheritance());
        Assert.assertEquals("Group options did not match map options",map.get(ServerVars.Permissions.optionsKey),group.getOptions());
        Assert.assertArrayEquals("Group permissions did not match map permissions", ((List) map.get(ServerVars.Permissions.permissionsKey)).toArray(), group.getPermissions());
    }

    @Test
    public void testGroupSchemaSingleton(){
        final Map map = Map.of(
            "inheritance", "testGroup"
        );

        final PermissionsGroup group = new PermissionsGroup("testGroup",map);

        Assert.assertEquals("Group inheritance did not match map inheritance",map.get("inheritance"),group.getInheritance()[0]);
    }

    @Test
    public void testGroupSchemaPermissionsLower(){
        final Map map = Map.of(
            ServerVars.Permissions.permissionsKey, List.of("testPermissions")
        );

        final PermissionsGroup group = new PermissionsGroup("testGroup",map);

        Assert.assertEquals("User permissions did not match map permissions", ((List<String>) map.get(ServerVars.Permissions.permissionsKey)).get(0).toLowerCase(), group.getPermissions()[0]);
    }

}
