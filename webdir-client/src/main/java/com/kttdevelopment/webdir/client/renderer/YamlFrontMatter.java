package com.kttdevelopment.webdir.client.renderer;

import com.amihaiemil.eoyaml.Yaml;
import com.kttdevelopment.simplehttpserver.ContextUtil;
import com.kttdevelopment.webdir.client.*;
import com.kttdevelopment.webdir.client.plugin.PluginRendererEntry;
import com.kttdevelopment.webdir.client.utility.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class YamlFrontMatter {

     private static final Pattern pattern = Pattern.compile("^(---)$\\n\\r?(.*)\\n\\r?^(---)\\n?\\r?(.*)", Pattern.MULTILINE | Pattern.DOTALL);

     private final Map<String,? super Object> frontMatter;
     private final String content;

     public YamlFrontMatter(final String raw){
          Map<String,? super Object> map = new HashMap<>();
          final Matcher matcher = pattern.matcher(raw);
          if(matcher.find()){
               this.content = matcher.group(4);
               try{
                    map = YamlUtility.asMap(Yaml.createYamlInput(matcher.group(2)).readYamlMapping());
               }catch(final IOException ignored){ } // malformed
          }else
               this.content = raw;
          frontMatter = map.isEmpty() ? null : map;
     }

     public final Map<String,? super Object> getFrontMatter(){
          return frontMatter;
     }

     public final String getContent(){
          return content;
     }

     @Override
     public String toString(){
          return new ToStringBuilder(getClass().getSimpleName())
             .addObject("frontMatter", frontMatter)
             .addObject("content", content)
             .toString();
     }

     //

     public static Map<String,? super Object> loadImports(final File source, final List<Path> checked){
          try{
               final Map<String,? super Object> config = YamlUtility.asMap(Yaml.createYamlInput(source).readYamlMapping());
               return loadImports(source, config, checked);
          }catch(final IOException e){
               Main.getLogger(Main.getLocale().getString("page-renderer.name")).severe(Main.getLocale().getString("page-renderer.front-matter.fail", source.getPath()) + LoggerService.getStackTraceAsString(e));
          }
          return new HashMap<>();
     }

     public static Map<String,? super Object> loadImports(final File source, final Map<String,? super Object> config){
          return loadImports(source, config, new ArrayList<>());
     }

     private static final Pattern hasExtension = Pattern.compile("^(.*)\\.(.*)$");

     @SuppressWarnings("unchecked")
     private static Map<String,? super Object> loadImports(final File source, final Map<String,? super Object> config, final List<Path> checked){
          // reverse so top imports will override lower
          final List<String> imports = new ArrayList<>();
          {
               final Object obj = config.get(PageRenderer.IMPORT);
               if(obj instanceof List)
                    ExceptionUtility.runIgnoreException(() -> imports.addAll(((List<String>) obj)));
               else if(obj instanceof String)
                    imports.add((String) obj);
               Collections.reverse(imports);
          }

          final List<String> relativeImports = new ArrayList<>();

          {
               final Object obj = config.get(PageRenderer.IMPORT_RELATIVE);
               if(obj instanceof List)
                    ExceptionUtility.runIgnoreException(() -> relativeImports.addAll(((List<String>) obj)));
               else if(obj instanceof String)
                    relativeImports.add((String) obj);
               Collections.reverse(relativeImports);
          }

          if(imports.isEmpty() && relativeImports.isEmpty())
               return config;

          // populate
          final Map<String,? super Object> out = new HashMap<>();
          final List<List<String>> both = List.of(imports, relativeImports);
          both.forEach(list -> list.forEach(s -> {
               // if no extension assume yml
               final String fileName = ContextUtil.getContext(s + (hasExtension.matcher(s).matches() ? "" : ".yml"), true, false);
               final Path IN = new File(list == relativeImports ? source.getAbsoluteFile().getParentFile() : new File(".").getAbsoluteFile().getParentFile(), fileName).toPath();

               boolean contains = false;
               for(final Path path : checked) // check if this file has already been read
                    if(ExceptionUtility.requireNonExceptionElse(() -> Files.isSameFile(IN, path), false)){
                         contains = true;
                         break;
                    }

               if(!contains){ // only apply if not already added
                    checked.add(IN);
                    final Map<String, ? super Object> imported = loadImports(IN.toFile(), checked);
                    out.putAll(imported);
               }
          }));
          out.putAll(config);
          return out;
     }

     // renderer

     public static List<PluginRendererEntry> getRenderers(final List<?> renderers){
          final List<PluginRendererEntry> installed = Main.getPluginLoader().getRenderers();
          final List<PluginRendererEntry> out = new ArrayList<>();

          for(final Object obj : renderers){
               PluginRendererEntry renderer = null;
               if(obj instanceof String)
                    renderer = new PluginRendererEntry(null, obj.toString(), null);
               else if(obj instanceof Map){
                    final Map<?,?> map = (Map<?,?>) obj;
                    try{
                         renderer = new PluginRendererEntry(
                              Objects.requireNonNull(map.get(PageRenderer.PLUGIN)).toString(),
                              Objects.requireNonNull(map.get(PageRenderer.RENDERER)).toString(),
                              null
                         );
                    }catch(final NullPointerException e){
                         Main.getLogger(Main.getLocale().getString("page-renderer.name")).severe(Main.getLocale().getString("page-renderer.front-matter.missing") + LoggerService.getStackTraceAsString(e));
                    }
               }

               if(renderer == null) continue;

               for(final PluginRendererEntry entry : installed){
                    if(
                         (renderer.getPluginName() == null &&
                         renderer.getRendererName().equals(entry.getRendererName())) ||
                         (renderer.getPluginName() != null &&
                         renderer.getPluginName().equals(entry.getPluginName()) &&
                         renderer.getRendererName().equals(entry.getRendererName()))
                    ){
                         out.add(entry);
                         break;
                    }
               }
          }
          return out;
     }

}
