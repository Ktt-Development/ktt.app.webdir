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
            "groups", List.of("testGroup"),
            "options", Map.of("testOption",true),
            "permissions",List.of("test_permission")
        );

        final InetAddress address = InetAddress.getLocalHost();
        final PermissionsUser user = new PermissionsUser(address,map);

        Assert.assertEquals("User did not match source address",address,user.getUser());
        Assert.assertArrayEquals("User group did not match map group",((List) map.get("groups")).toArray(),user.getGroups());
        Assert.assertEquals("User options did not match map options",map.get("options"),user.getOptions());
        Assert.assertArrayEquals("User permissions did not match map permissions", ((List) map.get("permissions")).toArray(), user.getPermissions());
    }

    @Test
    public void testUserSchemaSingleton() throws UnknownHostException{
        final Map map = Map.of(
            "groups", "testGroup"
        );

        final InetAddress address = InetAddress.getLocalHost();
        final PermissionsUser user = new PermissionsUser(address,map);

        Assert.assertEquals("User group did not match map group",map.get("groups"),user.getGroups()[0]);
    }

    @Test
    public void testUserSchemaPermissionLower() throws UnknownHostException{
        final Map map = Map.of(
            "permissions", List.of("testPermissions")
        );

        final InetAddress address = InetAddress.getLocalHost();
        final PermissionsUser user = new PermissionsUser(address,map);

        Assert.assertEquals("User permissions did not match map permissions", ((List<String>) map.get("permissions")).get(0).toLowerCase(), user.getPermissions()[0]);
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
            "options",Map.of("default",true),
            "permissions",List.of("*")
        );

        final String groupName = "testGroup";
        final PermissionsGroup group = new PermissionsGroup(groupName,map);

        Assert.assertEquals("Group did not match source",groupName,group.getGroup());
        Assert.assertArrayEquals("Group inheritance did not match map inheritance", ((List) map.get("inheritance")).toArray(),group.getInheritance());
        Assert.assertEquals("Group options did not match map options",map.get("options"),group.getOptions());
        Assert.assertArrayEquals("Group permissions did not match map permissions", ((List) map.get("permissions")).toArray(), group.getPermissions());
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
            "permissions", List.of("testPermissions")
        );

        final PermissionsGroup group = new PermissionsGroup("testGroup",map);

        Assert.assertEquals("User permissions did not match map permissions", ((List<String>) map.get("permissions")).get(0).toLowerCase(), group.getPermissions()[0]);
    }

}
