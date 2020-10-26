package com.kttdevelopment.webdir.client;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.kttdevelopment.webdir.client.permissions.PermissionsGroup;
import com.kttdevelopment.webdir.client.permissions.PermissionsUser;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;

public class PermissionsServiceTests {

    @BeforeEach
    public void before(){
        Assertions.assertDoesNotThrow(() -> Main.logger = new LoggerService(), getClass().getSimpleName() + " depends on LoggerService for tests.");
        Assertions.assertDoesNotThrow(() -> Main.locale = new LocaleService(), getClass().getSimpleName() + " depends on LoggerService for tests.");
    }

    @AfterAll
    public static void cleanup(){
        LoggerServiceTests.clearLogFiles();
    }

    @Test
    public void testGroup() throws IOException{
        // test null
        Assertions.assertDoesNotThrow(() -> new PermissionsGroup("", null));

        // test valid
        {
            final String testGroup = "testGroup";
            final String yml = (
                "inheritance:\n" +
                "  - %s\n" +
                "options:\n" +
                "  %s: %s\n" +
                "permissions:\n" +
                "  - %s"
            ).replace("%s", testGroup);

            final PermissionsGroup group = new PermissionsGroup(testGroup, Yaml.createYamlInput(yml).readYamlMapping());
            Assertions.assertEquals(testGroup, group.getGroup());
            Assertions.assertEquals(testGroup, group.getInheritance().get(0));
            Assertions.assertEquals(testGroup, group.getOptions().get(testGroup));
            Assertions.assertEquals(testGroup, group.getPermissions().get(0));
        }

        // test literal
        {
            final String testGroup = "testGroup";
            final String yml = (
                "inheritance: %s\n" +
                "options: %s\n" +
                "permissions: %s\n"
            ).replace("%s", testGroup);

            final PermissionsGroup group = new PermissionsGroup(testGroup, Yaml.createYamlInput(yml).readYamlMapping());
            Assertions.assertEquals(testGroup, group.getGroup());
            Assertions.assertEquals(testGroup, group.getInheritance().get(0));
            Assertions.assertEquals(0, group.getOptions().size());
            Assertions.assertEquals(0, group.getPermissions().size());
        }

        // test invalid
        {
            final String testGroup = "testGroup";
            final String yml = (
                "inheritance: %s" +
                "options: %s\n" +
                "permissions: %s\n"
            ).replace("%s", testGroup);

            final PermissionsGroup group = new PermissionsGroup(testGroup, Yaml.createYamlInput(yml).readYamlMapping());
            Assertions.assertEquals(testGroup, group.getGroup());
            // for inheritance a map will just be interpreted as a string or throw a line error
            // Assertions.assertEquals(0, group.getInheritance().size());
            Assertions.assertEquals(0, group.getOptions().size());
            Assertions.assertEquals(0, group.getPermissions().size());
        }

        // test none
        {
            final String testGroup = "testGroup";

            final PermissionsGroup group = new PermissionsGroup(testGroup, Yaml.createYamlInput("").readYamlMapping());
            Assertions.assertEquals(testGroup, group.getGroup());
            Assertions.assertEquals(0, group.getInheritance().size());
            Assertions.assertEquals(0, group.getOptions().size());
            Assertions.assertEquals(0, group.getPermissions().size());
        }
    }

    @Test
    public void testUser() throws IOException{
        // test null
        Assertions.assertDoesNotThrow(() -> new PermissionsUser(InetAddress.getLocalHost(), null));

        // test valid + loop-back address
        {
            final String testValue = UUID.randomUUID().toString();
            final String yml = (
                "groups:\n" +
                "  - %s\n" +
                "options:\n" +
                "  %s: %s\n" +
                "permissions:\n" +
                "  - %s"
            ).replace("%s", testValue);

            final PermissionsUser user = new PermissionsUser("127.0.0.1", Yaml.createYamlInput(yml).readYamlMapping());
            Assertions.assertEquals(InetAddress.getLocalHost().getHostAddress(), user.getUser().getHostAddress());
            Assertions.assertEquals(testValue, user.getGroups().get(0));
            Assertions.assertEquals(testValue, user.getOptions().get(testValue));
            Assertions.assertEquals(testValue, user.getPermissions().get(0));
        }

        // test literal + literal address
        {
            final String testValue = UUID.randomUUID().toString();
            final String yml = (
                "groups: %s\n" +
                "options: %s\n" +
                "permissions: %s\n"
            ).replace("%s", testValue);

            final PermissionsUser user = new PermissionsUser("255.255.255.255", Yaml.createYamlInput(yml).readYamlMapping());
            Assertions.assertEquals("255.255.255.255", user.getUser().getHostAddress());
            Assertions.assertEquals(testValue, user.getGroups().get(0));
            Assertions.assertEquals(0, user.getOptions().size());
            Assertions.assertEquals(0, user.getPermissions().size());
        }

        // test invalid
        {
            final String testValue = UUID.randomUUID().toString();
            final String yml = (
                "groups: %s\n" +
                "options: %s\n" +
                "permissions: %s\n"
            ).replace("%s", testValue);

            final PermissionsUser user = new PermissionsUser("255.255.255.255", Yaml.createYamlInput(yml).readYamlMapping());
            // for inheritance a map will just be interpreted as a string or throw a line error
            // Assertions.assertEquals(0, user.getGroups().size());
            Assertions.assertEquals(0, user.getOptions().size());
            Assertions.assertEquals(0, user.getPermissions().size());
        }

        // test none
        {
            final String testValue = UUID.randomUUID().toString();

            final PermissionsUser user = new PermissionsUser("255.255.255.255", Yaml.createYamlInput("").readYamlMapping());
            Assertions.assertEquals(0, user.getGroups().size());
            Assertions.assertEquals(0, user.getOptions().size());
            Assertions.assertEquals(0, user.getPermissions().size());
        }

    }

}
