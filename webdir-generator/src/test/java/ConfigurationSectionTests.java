import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.config.ConfigurationSectionImpl;
import org.junit.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiConsumer;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ConfigurationSectionTests {

    @Test
    public void testDefault(){
        final String defKey = "default", defVal = "value";
        final Map defMap = new HashMap();
        defMap.put(defKey,defVal);

        final ConfigurationSection def = new ConfigurationSectionImpl(defMap);
        final ConfigurationSection config = new ConfigurationSectionImpl(new HashMap(),def);

        Assert.assertNotNull("Configuration without key should use the default value",config.getString(defKey));
    }

    @Test
    public void testContains(){
        final String key = "key", value = "value";
        final Map map = new HashMap();
        map.put(key,value);

        final ConfigurationSection config = new ConfigurationSectionImpl(map);

        Assert.assertTrue("Configuration from map that contains key should also contain that key",config.contains(key));
    }

    @Test
    public void testLevels(){
        String iMap2Key = "iMap2", iMap3Key = "iMap3";

        final Map iMap1;

        iMap1 = Map.of(
            iMap2Key,Map.of(
                iMap3Key,new HashMap()
            )
        );

        final ConfigurationSection config = new ConfigurationSectionImpl(iMap1);

        Assert.assertEquals("Top level should have root be itself",config,config.getRoot());
        Assert.assertNull("Top level map did not return a null parent",config.getParent());

        Assert.assertEquals("Third level map did not return second level map as the parent",config.get(iMap2Key),config.get(iMap2Key).get(iMap3Key).getParent());
        Assert.assertEquals("Third level map did not return first level map as the root",config,config.get(iMap2Key).get(iMap3Key).getRoot());
    }

    @SuppressWarnings("JavaReflectionInvocation")
    @Test @Ignore
    public void testPrimitive(){
        final Map testValidExact = Map.of(
            "Boolean",true,
            "Character",'c',
            "String","string",
            "Float",1f,
            "Integer",1,
            "Double",1d,
            "Long",1L
        );
        final ConfigurationSection testConfig = new ConfigurationSectionImpl(testValidExact);

        testValidExact.forEach((k, v) -> {
            final String key = k.toString();
            try{
                ConfigurationSection.class.getDeclaredMethod("get" + key).invoke(testConfig,key);
            }catch(final NoSuchMethodException ignored){
                Assert.fail("Internal failure: method #get" + key + " (not found)");
            }catch(IllegalAccessException | InvocationTargetException ignored){
                Assert.fail("Internal failure: method on #get" + key);
            }catch(final ClassCastException ignored){
                Assert.fail("Exact test on #get" + key + " did not return the correct type");
            }
        });


    }

}
