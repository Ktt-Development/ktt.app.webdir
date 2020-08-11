package tests;

import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.config.ConfigurationSectionImpl;
import org.junit.*;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ConfigurationSectionTests {

    @Test
    public void testDefault(){
        final String defKey = "default", defVal = "value";
        final Map defMap = new HashMap();
        defMap.put(defKey,defVal);

        final ConfigurationSection def = new ConfigurationSectionImpl(defMap);
        final ConfigurationSection config = new ConfigurationSectionImpl(new HashMap(),def);

        Assert.assertEquals("Configuration without key should use the default value", config.getString(defKey), defVal);
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

        Assert.assertEquals("Third level map did not return second level map as the parent",config.get(iMap2Key).toMap(),config.get(iMap2Key).get(iMap3Key).getParent().toMap());
        Assert.assertEquals("Third level map did not return first level map as the root",config,config.get(iMap2Key).get(iMap3Key).getRoot());

        Assert.assertEquals("Get method should be the same map as getMap",config.get(iMap2Key).toMap(),config.getMap(iMap2Key));
    }

    @Test
    public void testPrimitive(){
        final Map map = Map.of(
            "Boolean",true,
            "Character",'c',
            "String","string",
            "Float",1f,
            "Integer",1,
            "Double",1d,
            "Long",1L
        );

        final ConfigurationSection testConfig = new ConfigurationSectionImpl(map);

        map.forEach((k, v) -> {
            final String key = k.toString();
            try{
                Assert.assertEquals(String.format("Method #get%s did not return the same provided in the initial map",key),v,ConfigurationSection.class.getDeclaredMethod("get" + key,String.class).invoke(testConfig,key));
            }catch(final NoSuchMethodException ignored){
                Assert.fail("Internal failure: method #get" + key + " (not found)");
            }catch(final IllegalAccessException e){
                Assert.fail("Internal failure: method #get" + key + " (bad scope)");
            }catch(final InvocationTargetException e){
                throw new RuntimeException(e.getTargetException());
            }catch(final ClassCastException ignored){
                Assert.fail("Similar test on #get" + key + " did not return the correct type");
            }
        });
    }

    @Test
    public void testPrimitiveSimilar(){
        final Map expected = Map.of(
            "Boolean",true,
            "Character",'c',
            "String","s",
            "Float",1f,
            "Integer",1,
            "Double",1d,
            "Long",1L
        );

        final Map map = Map.of(
            "Boolean","true",
            "Character","cee",
            "String",'s',
            "Float",1,
            "Integer",1.5,
            "Double",1,
            "Long",1
        );

        final ConfigurationSection testConfig = new ConfigurationSectionImpl(map);

        map.forEach((k, v) -> {
            final String key = k.toString();
            try{
                Assert.assertEquals(String.format("Method #get%s did not return the expected value",key),expected.get(k),ConfigurationSection.class.getDeclaredMethod("get" + key,String.class).invoke(testConfig,key));
            }catch(final NoSuchMethodException ignored){
                Assert.fail("Internal failure: method #get" + key + " (not found)");
            }catch(final IllegalAccessException e){
                Assert.fail("Internal failure: method #get" + key + " (bad scope)");
            }catch(final InvocationTargetException e){
                throw new RuntimeException(e.getTargetException());
            }catch(final ClassCastException ignored){
                Assert.fail("Similar test on #get" + key + " did not return the correct type");
            }
        });
    }

    @Test
    public void testPrimitiveOrDef(){
        final Map map = Map.of(
            "Boolean",true,
            "Character",'c',
            "String","string",
            "Float",1f,
            "Integer",1,
            "Double",1d,
            "Long",1L
        );

        final ConfigurationSection testConfig = new ConfigurationSectionImpl();

        final Function<Object,Class> getPrimitiveClass = o -> {
            if(o instanceof Boolean)
                return boolean.class;
            else if(o instanceof Character)
                return char.class;
            else if(o instanceof Float)
                return float.class;
            else if(o instanceof Integer)
                return int.class;
            else if(o instanceof Double)
                return double.class;
            else if(o instanceof Long)
                return long.class;
            else
                return o.getClass();
        };

        map.forEach((k, v) -> {
            final String key = k.toString();
            try{
                Assert.assertEquals(String.format("Method #get%s did not return the default value",key),map.get(k),ConfigurationSection.class.getDeclaredMethod("get" + key,String.class,getPrimitiveClass.apply(v)).invoke(testConfig,key,v));
            }catch(final NoSuchMethodException ignored){
                Assert.fail("Internal failure: method #get" + key + " (not found)");
            }catch(final IllegalAccessException e){
                Assert.fail("Internal failure: method #get" + key + " (bad scope)");
            }catch(final InvocationTargetException e){
                throw new RuntimeException(e.getTargetException());
            }catch(final ClassCastException ignored){
                Assert.fail("Similar test on #get" + key + " did not return the correct type");
            }
        });
    }

    @Test
    public void testList(){
        final String value = "thisValue";

        final Map map = Map.of(
            "ListFromStr",value,
            "StrFromList",List.of(value)
        );

        final ConfigurationSection testConfig = new ConfigurationSectionImpl(map);

        Assert.assertEquals("Getting a string via list should return first value as as a string",value,testConfig.getString("StrFromList"));

        Assert.assertEquals("Getting a list via string should return a singleton list",value,testConfig.getList("ListFromStr").get(0));
    }

    @Test
    public void testInvalid(){
        final Map map = Map.of( // numbers are complex and may be valid values for casting
            "Float","x",
            "Integer","x",
            "Double","x",
            "Long","x",
            "List",new HashMap<>(),
            "Map",new ArrayList<>()
        );

        final ConfigurationSection testConfig = new ConfigurationSectionImpl(map);

        map.forEach((k, v) -> {
            final String key = k.toString();
            boolean ex = false;
            try{
                Assert.assertEquals(String.format("Method #get%s did not return the same provided in the initial map",key),v,ConfigurationSection.class.getDeclaredMethod("get" + key,String.class).invoke(testConfig,key));
            }catch(final NoSuchMethodException ignored){
                Assert.fail("Internal failure: method #get" + key + " (not found)");
            }catch(final IllegalAccessException e){
                Assert.fail("Internal failure: method #get" + key + " (bad scope)");
            }catch(final InvocationTargetException e){
                if(!(e.getTargetException() instanceof ClassCastException || e.getTargetException() instanceof NumberFormatException))
                    throw new RuntimeException(e.getTargetException());
                else
                    ex = true;
            }catch(final ClassCastException ignored){
                Assert.fail("Similar test on #get" + key + " did not return the correct type");
            }
            if(!ex)
                Assert.fail("Invalid value for " + key + " did not throw a class cast exception");
        });
    }

    @Test
    public void testSet(){
        final String key = "testKey", value = "testValue";
        final ConfigurationSection testConfig = new ConfigurationSectionImpl();

        Assert.assertFalse("Empty config should not contain any keys",testConfig.contains(key));
        testConfig.set(key,value);
        Assert.assertEquals("Config with set value should have set value",value,testConfig.getString(key));
    }

}
