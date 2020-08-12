package com.kttdevelopment.webdir.server.render;

import com.kttdevelopment.simplehttpserver.SimpleHttpExchange;
import com.kttdevelopment.webdir.api.Renderer;
import com.kttdevelopment.webdir.api.server.*;
import com.kttdevelopment.webdir.api.serviceprovider.ConfigurationSection;
import com.kttdevelopment.webdir.generator.Vars;
import com.kttdevelopment.webdir.generator.function.Exceptions;
import com.kttdevelopment.webdir.generator.function.QuinFunction;
import com.kttdevelopment.webdir.generator.locale.ILocaleService;
import com.kttdevelopment.webdir.generator.pluginLoader.PluginRendererEntry;
import com.kttdevelopment.webdir.generator.render.YamlFrontMatter;
import com.kttdevelopment.webdir.server.Main;
import com.kttdevelopment.webdir.server.ServerVars;
import com.kttdevelopment.webdir.server.permissions.Permissions;

import java.io.File;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public final class FilePageRenderer implements QuinFunction<SimpleHttpExchange,File,File,ConfigurationSection,byte[],byte[]> {

    @Override
    public byte[] apply(final SimpleHttpExchange exchange, final File IN, final File OUT, final ConfigurationSection defaultFrontMatter, final byte[] bytes){
        final ILocaleService locale = Vars.Main.getLocaleService();
        final Logger logger         = Vars.Main.getLoggerService().getLogger(locale.getString("fileRenderer"));
        final String sourceABS      = IN.getAbsolutePath();

        logger.finest(locale.getString("exchangeRenderer.debug.render",exchange,sourceABS,defaultFrontMatter,bytes));

        if(defaultFrontMatter == null)
            return bytes;
    // load renders
        final List<String> renderersStr   = defaultFrontMatter.getList(Vars.Renderer.rendererKey, String.class);
        final List<String> renderersExStr = defaultFrontMatter.getList(ServerVars.Renderer.exchangeRendererKey,String.class);

        if((renderersStr == null || renderersStr.isEmpty()) && (renderersExStr == null || renderersExStr.isEmpty())) return bytes;

        final List<PluginRendererEntry> renders = YamlFrontMatter.getRenderers(Vars.Renderer.rendererKey, Objects.requireNonNullElse(renderersStr,new ArrayList<>()));
        final List<PluginRendererEntry> rendersEx = YamlFrontMatter.getRenderers(ServerVars.Renderer.exchangeRendererKey, Objects.requireNonNullElse(renderersExStr,new ArrayList<>()));
        final List<PluginRendererEntry> allRenderers = new ArrayList<>();
        allRenderers.addAll(renders);
        allRenderers.addAll(rendersEx);
    // render page
        final AtomicReference<byte[]> content = new AtomicReference<>("".getBytes());

        final InetAddress address     = exchange.getPublicAddress().getAddress();
        final Permissions permissions = Main.getPermissions().getPermissions();
        logger.finest(locale.getString("exchangeRenderer.debug.permissions",permissions,address.getHostAddress()));
        allRenderers.forEach(renderer -> {
            final Renderer render = renderer.getRenderer();
            final ExecutorService executor = Executors.newSingleThreadExecutor();
            final Future<byte[]> future = executor.submit(() -> {
                final AtomicReference<byte[]> buffer = new AtomicReference<>(content.get());
                byte[] ct = content.get();

                try{
                    buffer.set(render.render(IN, OUT,defaultFrontMatter,new String(buffer.get())).getBytes());
                    logger.finest(locale.getString("pageRenderer.debug.PageRenderer.apply",renderer.getRendererName(),sourceABS,ct,buffer.get()));
                }catch(final Throwable e){
                    logger.warning(locale.getString("pageRenderer.pageRenderer.rendererUncaught",renderer.getPluginName(), renderer.getRendererName(), IN.getPath()) + '\n' + Exceptions.getStackTraceAsString(e));
                }

                try{
                    ct = buffer.get();
                    if((render instanceof ExchangeRendererAdapter && !(render instanceof ExchangeRenderer)) || render instanceof ExchangeRenderer && permissions.hasPermission(address, ((ExchangeRenderer) render).getPermission())){
                        buffer.set(((ExchangeRendererAdapter) renderer.getRenderer()).render(exchange, IN, OUT,defaultFrontMatter, new String(buffer.get())).getBytes());
                        logger.finest(locale.getString("pageRenderer.debug.PageRenderer.apply",renderer.getRendererName(),sourceABS,ct,buffer.get()));
                    }
                }catch(final Throwable e){
                    logger.warning(locale.getString("pageRenderer.pageRenderer.rendererUncaught",renderer.getPluginName(), renderer.getRendererName(), IN.getPath()) + '\n' + Exceptions.getStackTraceAsString(e));
                }

                try{
                    ct = buffer.get();
                    if((render instanceof FileRendererAdapter && !(render instanceof FileRenderer)) || (render instanceof FileRenderer && permissions.hasPermission(address, ((FileRenderer) render).getPermission()))){
                        buffer.set(((FileRendererAdapter) renderer.getRenderer()).render(exchange, IN, defaultFrontMatter, buffer.get()).getBytes());
                        logger.finest(locale.getString("pageRenderer.debug.PageRenderer.apply",renderer.getRendererName(),sourceABS,ct,buffer.get()));
                    }
                }catch(final Throwable e){
                    logger.warning(locale.getString("pageRenderer.pageRenderer.rendererUncaught",renderer.getPluginName(), renderer.getRendererName(), IN.getPath()) + '\n' + Exceptions.getStackTraceAsString(e));
                }
                return buffer.get();
            });

            try{
                content.set(Objects.requireNonNull(future.get(Vars.Plugin.loadTimeout, Vars.Plugin.loadTimeoutUnit)));
            }catch(final TimeoutException | InterruptedException e){
                logger.severe(
                    locale.getString("pageRenderer.pageRenderer.timedOut", renderer.getPluginName(), renderer.getRendererName(), IN.getPath(), Vars.Plugin.loadTimeout + " " + Vars.Plugin.loadTimeoutUnit.name().toLowerCase())
                );
            }catch(final Throwable e){
                logger.warning(locale.getString("pageRenderer.pageRenderer.rendererUncaught", renderer.getPluginName(), renderer.getRendererName(), IN.getPath()) + '\n' + Exceptions.getStackTraceAsString(e));
            }finally{
                future.cancel(true);
                executor.shutdownNow();
            }
        });
        return content.get();
    }

}
