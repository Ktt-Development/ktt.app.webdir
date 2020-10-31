package com.kttdevelopment.webdir.client.server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

public class HTMLNameAdapterTests {

    @Test
    public void test(){
        final HTMLNameAdapter adapter = new HTMLNameAdapter();

        Assertions.assertEquals("", adapter.getName(new File("")));
        Assertions.assertEquals("file", adapter.getName(new File("file")));
        Assertions.assertEquals(".html", adapter.getName(new File(".html")));
        Assertions.assertEquals("file", adapter.getName(new File("file.html")));
        Assertions.assertEquals("file.html", adapter.getName(new File("file.html.html")));
    }

}
