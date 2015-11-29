package com.enjoyxstudy.proxy;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.jetty.servlet.ServletHandler;
import org.mortbay.log.Log;
import org.mortbay.servlet.ProxyServlet;

/**
 * exec server
 */
public class ProxyServer {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        String configFile = "config.txt"; // default
        if (args.length != 0) {
            configFile = args[0];
        }

        Config config = parseConfig(configFile);

        Log.info("listenPort: " + config.getListenPort());

        if (config.getProxyHost() != null) {
            Log.info("use proxy:");
            Log.info("  host: " + config.getProxyHost());
            Log.info("  port: " + config.getProxyPort());
            System.setProperty("http.proxyHost", config.getProxyHost());
            System.setProperty("http.proxyPort", String.valueOf(config
                    .getProxyPort()));
        }

        Log.info("mapping:");

        for (Iterator hostIterator = config.getMappingMap().keySet().iterator(); hostIterator
                .hasNext();) {
            String host = (String) hostIterator.next();
            Log.info("  " + host);
            List mappingList = (List) config.getMappingMap().get(host);
            for (Iterator iterator = mappingList.iterator(); iterator.hasNext();) {
                Mapping mapping = (Mapping) iterator.next();
                Log.info("    " + mapping.getUrl() + " -> "
                        + mapping.getFile());
            }
        }

        // create server
        SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(config.getListenPort());
        Server server = new Server();
        server.setConnectors(new Connector[] { connector });

        // context
        ContextHandler contextHandler = new ContextHandler("/");

        // servlet
        ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(ProxyServlet.class, "/*");

        // filter
        FilterHolder filterHolder = new FilterHolder();
        LoadLocalFileFilter loadLocalFileFilter = new LoadLocalFileFilter();
        loadLocalFileFilter.setConfig(config);
        filterHolder.setFilter(loadLocalFileFilter);
        servletHandler
                .addFilterWithMapping(filterHolder, "/*", Handler.DEFAULT);

        contextHandler.addHandler(servletHandler);
        server.addHandler(contextHandler);

        server.start();
        server.join();

    }

    /**
     * @param configFile
     * @return config
     * @throws IOException
     */
    public static Config parseConfig(String configFile) throws IOException {

        Config config = new Config();
        HashMap mappingMap = new HashMap();
        config.setMappingMap(mappingMap);

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(configFile), "UTF-8"));
        try {
            String line = null;
            ArrayList mappingList = new ArrayList();
            int state = -1; // 0:Server 1:Mapping
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() != 0 && line.indexOf("#") != 0) {

                    if (line.indexOf("[Server]") == 0) {
                        state = 0;
                        continue;
                    }
                    if (line.indexOf("[Mapping]") == 0) {
                        state = 1;
                        continue;
                    }

                    switch (state) {
                    case 0: // Server
                        if (line.indexOf("listenPort=") == 0) {
                            // listenPort
                            config.setListenPort(Integer.parseInt(line
                                    .substring("listenPort=".length())));
                        } else if (line.indexOf("proxyHost=") == 0) {
                            // proxyHost
                            config.setProxyHost(line.substring("proxyHost="
                                    .length()));
                        } else if (line.indexOf("proxyPort=") == 0) {
                            // proxyPort
                            config.setProxyPort(Integer.parseInt(line
                                    .substring("proxyPort=".length())));
                        }
                        break;

                    case 1: // Mapping
                        String[] values = line.split("->");
                        if (values.length == 1) {
                            // hostname
                            mappingList = new ArrayList();
                            mappingMap.put(values[0].trim(), mappingList);
                        } else {
                            // url, file
                            mappingList.add(new Mapping(values[0].trim(),
                                    values[1].trim()));
                        }
                        break;

                    default:
                        break;
                    }
                }
            }
        } finally {
            reader.close();
        }
        return config;
    }
}
