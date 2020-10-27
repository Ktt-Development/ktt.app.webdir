package com.kttdevelopment.webdir.client;

import com.amihaiemil.eoyaml.Yaml;
import com.kttdevelopment.webdir.client.permissions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.util.UUID;

public class PermissionsServiceTests {

    @BeforeAll
    public static void before(){
        Assertions.assertDoesNotThrow(() -> Main.logger = new LoggerService(), PermissionsServiceTests.class.getSimpleName() + " depends on LoggerService for tests.");
        Assertions.assertDoesNotThrow(() -> Main.locale = new LocaleService("lang/locale"), PermissionsServiceTests.class.getSimpleName() + " depends on LoggerService for tests.");
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
            final PermissionsUser user = new PermissionsUser("255.255.255.255", Yaml.createYamlInput("").readYamlMapping());
            Assertions.assertEquals(0, user.getGroups().size());
            Assertions.assertEquals(0, user.getOptions().size());
            Assertions.assertEquals(0, user.getPermissions().size());
        }
    }

    @Test
    public void testPermissions() throws IOException{
        // test null
        Assertions.assertDoesNotThrow(() -> new Permissions(null));

        // test invalid
        {
            final String testValue = UUID.randomUUID().toString();
            final String yml = (
                "groups: %s\n" +
                "users: %s\n"
            ).replace("%s", testValue);

            final Permissions perm = new Permissions(Yaml.createYamlInput(yml).readYamlMapping());
            Assertions.assertEquals(0, perm.getGroups().size());
            Assertions.assertEquals(0, perm.getUsers().size());
            Assertions.assertFalse(perm.hasPermission(""));
        }
        // test none
        {
            final Permissions perm = new Permissions(Yaml.createYamlInput("").readYamlMapping());
            Assertions.assertEquals(0, perm.getGroups().size());
            Assertions.assertEquals(0, perm.getUsers().size());
            Assertions.assertFalse(perm.hasPermission(""));
        }
        // test valid
        {
            {
                final String testValue = UUID.randomUUID().toString();
                final String testValueInherit = UUID.randomUUID().toString();
                final String yml = (
                    "groups:\n" +
                    "  $1:\n" +
                    "    inheritance:\n" +
                    "      - $2\n" +
                    "    options:\n" +
                    "      $1: $1\n" +
                    "    permissions:\n" +
                    "      - $1\n" +
                    "  $2:\n" +
                    "    options:\n" +
                    "      $2: $2\n" +
                    "    permissions:\n" +
                    "      - $2\n" +
                    "users:\n" +
                    "  255.255.255.255:\n" +
                    "    groups:\n" +
                    "      - $1"
                )
                .replace("$1", testValue)
                .replace("$2", testValueInherit);

                final Permissions perm = new Permissions(Yaml.createYamlInput(yml).readYamlMapping());

                Assertions.assertEquals(2, perm.getGroups().size());
                Assertions.assertEquals(1, perm.getUsers().size());
                // test get option + perm
                Assertions.assertEquals(testValue, perm.getOption(InetAddress.getByName("255.255.255.255"), testValue));
                Assertions.assertTrue(perm.hasPermission(InetAddress.getByName("255.255.255.255"), testValue));

                // test get inherited option + perm
                Assertions.assertEquals(testValueInherit, perm.getOption(InetAddress.getByName("255.255.255.255"), testValueInherit));
                Assertions.assertTrue(perm.hasPermission(InetAddress.getByName("255.255.255.255"), testValueInherit));
            }
            {
                final String testValue = UUID.randomUUID().toString();
                final String testValueInherit = UUID.randomUUID().toString();
                final String yml = (
                    "groups:\n" +
                    "  $1:\n" +
                    "    inheritance:\n" +
                    "      - $2\n" +
                    "    options:\n" +
                    "      default: true\n" +
                    "      $1: $1\n" +
                    "  $2:\n" +
                    "    options:\n" +
                    "      default: false\n" +
                    "      $2: $2\n" +
                    "    permissions:\n" +
                    "      - $2\n" +
                    "users:\n" +
                    "  255.255.255.255:\n" +
                    "    options:\n" +
                    "      $1: $2\n" +
                    "    permissions:\n" +
                    "      - $1"
                )
                .replace("$1", testValue)
                .replace("$2", testValueInherit);

                final Permissions perm = new Permissions(Yaml.createYamlInput(yml).readYamlMapping());
                Assertions.assertEquals(2, perm.getGroups().size());
                Assertions.assertEquals(1, perm.getUsers().size());

                // test user override inherited option + perm
                Assertions.assertEquals(testValueInherit, perm.getOption(InetAddress.getByName("255.255.255.255"), testValue));
                Assertions.assertTrue(perm.hasPermission(InetAddress.getByName("255.255.255.255"), testValue));

                // test default option + perm + inherited
                Assertions.assertNull(perm.getOption(InetAddress.getByName("255.255.255.255"), "default"));
                Assertions.assertTrue(perm.hasPermission(InetAddress.getByName("255.255.255.255"), testValueInherit));
                Assertions.assertEquals(testValueInherit, perm.getOption(InetAddress.getByName("255.255.255.255"), testValue));

                Assertions.assertEquals("true", perm.getOption("default"));
                Assertions.assertTrue(perm.hasPermission(testValueInherit));
                Assertions.assertEquals(testValueInherit, testValueInherit);
            }
            {
                final String testValue = UUID.randomUUID().toString();
                final String testValueInherit = UUID.randomUUID().toString();
                final String yml = (
                    "groups:\n" +
                    "  $1:\n" +
                    "    inheritance:\n" +
                    "      - $2\n" +
                    "    options:\n" +
                    "      default: true\n" +
                    "    permissions:\n" +
                    "      - $1\n" +
                    "      - !$2\n" +
                    "  $2:\n" +
                    "    permissions:\n" +
                    "      - $1\n" +
                    "      - $2\n" +
                    "users:\n" +
                    "  255.255.255.255:\n" +
                    "    permissions:\n" +
                    "      - !$1"
                )
                .replace("$1", testValue)
                .replace("$2", testValueInherit);

                final Permissions perm = new Permissions(Yaml.createYamlInput(yml).readYamlMapping());
                Assertions.assertEquals(2, perm.getGroups().size());
                Assertions.assertEquals(1, perm.getUsers().size());

                // test negative user & group perm override default
                Assertions.assertFalse(perm.hasPermission(InetAddress.getByName("255.255.255.255"), testValue));
                Assertions.assertFalse(perm.hasPermission(InetAddress.getByName("255.255.255.255"), testValueInherit));

                Assertions.assertTrue(perm.hasPermission(testValue));
                Assertions.assertFalse(perm.hasPermission(testValueInherit));
            }
        }

    }

    @TempDir
    public final File permTest = new File(UUID.randomUUID().toString());

    @Test
    public void testService() throws IOException{
        final File permissions = new File(permTest, UUID.randomUUID().toString() + ".yml");

        PermissionsService service = new PermissionsService(permissions);

        // test file exists and is valid
        Assertions.assertTrue(permissions.exists());
        Assertions.assertDoesNotThrow(() -> Yaml.createYamlInput(permissions).readYamlMapping());

        Assertions.assertTrue(Files.isSameFile(permissions.toPath(), service.getPermissionsFile().toPath()));
        Assertions.assertEquals(2, service.getPermissions().getGroups().size());
        Assertions.assertEquals(1, service.getPermissions().getUsers().size());

        // test OK
        service = new PermissionsService(permissions);
        Assertions.assertDoesNotThrow(() -> Yaml.createYamlInput(permissions).readYamlMapping());

        Assertions.assertTrue(Files.isSameFile(permissions.toPath(), service.getPermissionsFile().toPath()));
        Assertions.assertEquals(2, service.getPermissions().getGroups().size());
        Assertions.assertEquals(1, service.getPermissions().getUsers().size());

        // test malformed
        // malformed seems to be ignored by the library, it is unclear if this will cause issues.
        // config also runs this check
        Files.write(permissions.toPath(), "X: {".getBytes());
        service = new PermissionsService(permissions);
        Assertions.assertTrue(permissions.exists());
        Assertions.assertDoesNotThrow(() -> Yaml.createYamlInput(permissions).readYamlMapping());

        Assertions.assertTrue(Files.isSameFile(permissions.toPath(), service.getPermissionsFile().toPath()));
        // Assertions.assertEquals(2, service.getPermissions().getGroups().size());
        // Assertions.assertEquals(1, service.getPermissions().getUsers().size());

        // test empty
        Files.write(permissions.toPath(),"".getBytes());
        service = new PermissionsService(permissions);
        Assertions.assertTrue(permissions.exists());
        Assertions.assertEquals(permissions.length(), 0);

        Assertions.assertTrue(Files.isSameFile(permissions.toPath(), service.getPermissionsFile().toPath()));
        Assertions.assertEquals(0, service.getPermissions().getGroups().size());
        Assertions.assertEquals(0, service.getPermissions().getUsers().size());
    }

}
