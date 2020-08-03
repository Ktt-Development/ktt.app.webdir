package permissions;

import com.esotericsoftware.yamlbeans.YamlException;
import com.kttdevelopment.webdir.server.PermissionsService;
import org.junit.*;
import utility.TestFile;

import java.io.File;

public class PermissionServiceTests {

    @Test @Ignore
    public void testValid() throws YamlException{
        final String testOp = "testOp", value = "true";
        final String defOp = "def", defValue = "true";
        final String content =
            "groups:\n" +
            "  default:\n" +
            "    options:\n" +
            "      default: true\n" +
            "      " + testOp + ':' + value;
        final File permissionsFile = new File("src/test/resources/permissionsTests/testPermissions.yml");
        TestFile.createTestFile(permissionsFile,content);

        final String defaultResource = "/permissionsTests/defaultPermissions.yml";

        final PermissionsService permissions = new PermissionsService(permissionsFile,defaultResource);

        Assert.assertEquals("Valid permissions file did not return the correct value",value,permissions.getPermissions().getOption(null,testOp));
        Assert.assertEquals("Permissions service did not return default value when using a permission with missing key",defValue,permissions.getPermissions().getOption(null,defOp));

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

    @Test @Ignore
    public void testMissingPerm(){

    }

    @Test @Ignore
    public void testMalformedPerm(){

    }

}
