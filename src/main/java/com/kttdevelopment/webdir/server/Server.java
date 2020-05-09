package com.kttdevelopment.webdir.server;

import org.cef.*;
import org.cef.browser.CefBrowser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Server {

    public static void main(String[] args){
        /* * Gets the cef application singleton. It loads all resources
            * (native libraries etc.), initializes app...
            */
            final CefApp cefApp = CefApp.getInstance();
            /*
            * You can create many browser instances per cef app (e.g.
            * used as browser tabs) Responsible for handling all
            * events from the browser instances.
            */
            final CefClient client = cefApp.createClient();
            /*
            * Browser instances are responsible for rendering of
            * the browser's content.
            */

            final CefBrowser browser = client.createBrowser("http://www.google.com", OS.isLinux(), false);
            /*
            * Returns the browser's ui component used for
            * rendering in a awt application
            */
            final Component browserUI = browser.getUIComponent();
            // Create a new frame for holding the browser ui
            final JFrame mainFrame = new JFrame();
            // Add the browser ui to this newly created frame
            mainFrame.getContentPane().add(browserUI, BorderLayout.CENTER);
            // Show frame
            mainFrame.setSize(800, 600); mainFrame.setVisible(true);
            mainFrame.requestFocus();
            /*
            * Attach a handler to close the jcef application.
            * Dispose the JFrame and after that destroy the cefApp
            */
            mainFrame.addWindowListener(new WindowAdapter() {
              @Override public void windowClosing(WindowEvent e) {
                mainFrame.dispose();
                cefApp.dispose();
                // Alternative: CefApp.getInstance().dispose();
              }
            });
    }

}
