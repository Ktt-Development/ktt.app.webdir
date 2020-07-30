package com.kttdevelopment.webdir.server.render;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.webdir.api.*;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.LocaleService;
import com.kttdevelopment.webdir.server.Main;
import com.kttdevelopment.webdir.generator.config.ConfigurationSectionImpl;
import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.generator.function.QuinFunction;
import com.kttdevelopment.webdir.generator.pluginLoader.PluginRendererEntry;
import com.kttdevelopment.webdir.generator.render.YamlFrontMatter;
import com.kttdevelopment.webdir.generator.render.YamlFrontMatterReader;
import com.kttdevelopment.webdir.server.ServerVars;
import com.kttdevelopment.webdir.server.permissions.Permissions;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class ExchangePageRenderer implements QuinFunction<SimpleHttpExchange,File,File,ConfigurationSection,byte[],byte[]> {

    @Override
    public byte[] apply(final SimpleHttpExchange exchange, final File source, final File rendered, final ConfigurationSection defaultFrontMatter, final byte[] bytes){
        final LocaleService locale  = Main.getLocaleService();
        final Logger logger         = Main.getLoggerService().getLogger(locale.getString("exchangeRenderer"));

        final String sourceContent;
        try{ sourceContent = Files.readString(source.toPath());
        }catch(final IOException e){
            logger.warning(locale.getString("exchangeRenderer.failedRead", source.getAbsolutePath() + '\n' + Exceptions.getStackTraceAsString(e)));
            return bytes; }
    // create front matter from source
        final YamlFrontMatter frontMatter = new YamlFrontMatterReader(sourceContent).read();

        if(!frontMatter.hasFrontMatter() && defaultFrontMatter == null) return bytes;

        final ConfigurationSection mergedFrontMatter = new ConfigurationSectionImpl();
        if(defaultFrontMatter != null)
            mergedFrontMatter.setDefault(defaultFrontMatter);
        if(frontMatter.hasFrontMatter())
            mergedFrontMatter.setDefault(frontMatter.getFrontMatter());

        final ConfigurationSection finalFrontMatter = YamlFrontMatter.loadImports(source,mergedFrontMatter);
    // render page
        final List<String> renderersStr = finalFrontMatter.getList(ServerVars.Renderer.exchangeRendererKey,String.class);

        // if no renderers then return given bytes
        if(renderersStr == null || renderersStr.isEmpty()) return frontMatter.getContent().getBytes();

        final List<PluginRendererEntry> renderers = YamlFrontMatter.getRenderers(ServerVars.Renderer.exchangeRendererKey, renderersStr);

        final AtomicReference<String> content = new AtomicReference<>(new String(bytes));

        final InetAddress address     = exchange.getPublicAddress().getAddress();
        final Permissions permissions = Main.getPermissions().getPermissions();
        renderers.forEach(renderer -> {
            try{
                final Renderer render = renderer.getRenderer();
                // if is an adapter but not a class (adapter has no permissions) or is class and has permission
                if((render instanceof ExchangeRenderAdapter && !(render instanceof ExchangeRenderer)) || render instanceof ExchangeRenderer && permissions.hasPermission(address,((ExchangeRenderer) render).getPermission())) // todo
                    content.set(((ExchangeRenderAdapter) renderer.getRenderer()).render(exchange, source, finalFrontMatter, content.get()));
            }catch(final Throwable e){
                logger.warning(locale.getString("pageRenderer.pageRenderer.rendererUncaught",renderer.getPluginName(), renderer.getRendererName(), source.getPath()) + '\n' + Exceptions.getStackTraceAsString(e));
            }
        });

        return content.get().getBytes();
    }

}
