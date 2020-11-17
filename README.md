<p align="center">
    <a href="https://github.com/Ktt-Development/webdir">
        <img src="https://raw.githubusercontent.com/Ktt-Development/webdir/main/icon.png" alt="Logo" width="100" height="100">
    </a>
    <h3 align="center">WebDir</h3>
    <p align="center">
        Extensible site generator and file explorer.
        <br />
        <a href="https://docs.kttdevelopment.com/webdir/">Docs</a>
        •
        <a href="https://wiki.kttdevelopment.com/webdir/">Wiki</a>
        •
        <a href="https://github.com/Ktt-Development/webdir/issues">Issues</a>
    </p>
</p>

[![Deploy](https://github.com/Ktt-Development/webdir/workflows/Deploy/badge.svg)](https://github.com/Ktt-Development/webdir/actions?query=workflow%3ADeploy)
![Java CI](https://github.com/Ktt-Development/webdir/workflows/Java%20CI/badge.svg)
[![Maven Central](https://img.shields.io/maven-central/v/com.kttdevelopment.webdir/webdir-api)](https://mvnrepository.com/artifact/com.kttdevelopment.webdir/webdir-api)
[![version](https://img.shields.io/github/v/release/Ktt-Development/webdir)](https://github.com/Ktt-Development/webdir/releases)
[![license](https://img.shields.io/github/license/Ktt-Development/webdir)](https://github.com/Ktt-Development/webdir/blob/main/LICENSE)
---

# Setup

For people using WebDir, executables are available in releases. [![version](https://img.shields.io/github/v/release/Ktt-Development/webdir)](https://github.com/Ktt-Development/webdir/releases)

For plugin developers, compiled binaries can be found on Maven. [![Maven Central](https://img.shields.io/maven-central/v/com.kttdevelopment.webdir/webdir-api)](https://mvnrepository.com/artifact/com.kttdevelopment.webdir/webdir-api) 

For projects built locally, compiled binaries can be found in releases. [![version](https://img.shields.io/github/v/release/Ktt-Development/webdir)](https://github.com/Ktt-Development/webdir/releases) 
Note that [simplehttpserver](https://github.com/Ktt-Development/simplehttpserver) is a required dependency.


# Features

## ✔ Simple

Easily customize file renders.

- Customize files with yaml front matter
- Add bulk configurations with default configs

```yml
---
renderers:
  - renderer
---
```
```yml
default:
  scope:
    - '*.html'
    - '!index.html'
renderers: 
  - renderer
  - plugin: MyPlugin
    renderer: renderer
```

## ⚙ Flexible

- Customize site access with permissions
- Set input and output folders in configuration

```yml
groups:
  default:
    options:
      default: true
    permissions:
      - $1
  admin:
    inheritance:
      - default
    options:
      connection-limit: -1
    permissions:
      - '*'
users:
  127.0.0.1:
    groups:
      - admin
    permissions:
      - '!permission'
```

## 🔌 Extensible

Add additional features with WebDir Plugins.
- File renderers
- Built-in locale support
- Access to server features with [simplehttpserver](https://github.com/Ktt-Development/simplehttpserver)

```java
public class Plugin extends WebDirPlugin{

    public Plugin(final PluginService service){
        super(service);
    }

    @Override
    public void onEnable(){

        addRenderer("renderer", new Renderer(){

            @Override
            public byte[] render(final FileRender render){
                SimpleHttpServer server = render.getServer();
                SimpleHttpExchange exchange = render.getHttpExchange();

                return super.render(render);
            }

        });

    }

}
```
