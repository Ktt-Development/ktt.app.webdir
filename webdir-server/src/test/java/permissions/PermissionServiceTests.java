package permissions;

import com.esotericsoftware.yamlbeans.YamlException;
import com.kttdevelopment.webdir.server.PermissionsService;
import org.junit.*;
import utility.TestFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class PermissionServiceTests {

    @Test
    public void testValid() throws YamlException{
        final String testOp = "testOp", value = "true";
        final String content =
            "groups:\n" +
            "  default:\n" +
            "    options:\n" +
            "      default: true\n" +
            "      " + testOp + ": " + value;
        final File permissionsFile = new File("src/test/resources/permissionsTests/testPermissions.yml");
        TestFile.createTestFile(permissionsFile,content);

        final String defaultResource = "/permissionsTests/defaultPermissions.yml";

        final PermissionsService permissions = new PermissionsService(permissionsFile,defaultResource);

        Assert.assertEquals("Valid permissions file did not return the correct value",value,permissions.getPermissions().getOption(null,testOp));
    }

    @Test
    public void testMissingDef() throws YamlException{
        final String defaultResource = "/permissionsTests/missingDefault.yml";

        try{
            new PermissionsService(new File("null"),defaultResource);
            Assert.fail("Permissions service Service failed to throw null exception for missing default permissions");
        }catch(final NullPointerException ignored){ }
    }

    @Test
    public void testMalformedDef(){
        final String defaultResource = "/permissionsTests/malformedDefault.yml";

        try{
            new PermissionsService(new File("null"),defaultResource);
            Assert.fail("Permissions service Service failed to throw yaml exception for malformed default permissions");
        }catch(final ClassCastException | YamlException ignored){ }
    }

    @Test
    public void testMissingPerm() throws IOException{
        final String defOp = "def", defValue = "true";
        final String defaultResource = "/permissionsTests/defaultPermissions.yml";
        final File missingPermissions = new File("src/test/resources/permissionsTests/missing.yml");
        if(missingPermissions.exists() && !missingPermissions.delete())
            Assert.fail("Failed to delete config file for testing");
        missingPermissions.deleteOnExit();

        final PermissionsService permissions = new PermissionsService(missingPermissions,defaultResource);

        Assert.assertEquals("Permissions service did not return default value when using a missing permissions",defValue,permissions.getPermissions().getOption(null,defOp));
        Assert.assertTrue("Permissions service did not create a new permissions file when using a missing permissions",missingPermissions.exists());
        Assert.assertEquals("New file created by permissions service did not match default permissions", Files.readString(new File("src/test/resources" + defaultResource).toPath()),Files.readString(missingPermissions.toPath()));
    }

    @Test
    public void testMalformedPerm() throws YamlException{
        final String defOp = "def", defValue = "true";
        final String defaultResource = "/permissionsTests/defaultPermissions.yml";
        final File malformedPermissions = new File("src/test/resources/permissionsTests/malformed.yml");
        TestFile.createTestFile(malformedPermissions,String.valueOf(System.currentTimeMillis()));

        final PermissionsService permissions = new PermissionsService(malformedPermissions,defaultResource);

        Assert.assertEquals("Permissions service did not return default value when using a missing permissions",defValue,permissions.getPermissions().getOption(null,defOp));
    }

}
