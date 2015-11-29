package com.enjoyxstudy.proxy;

import java.util.HashMap;


/**
 * config
 */
public class Config {

    /** mappingMap */
    private HashMap mappingMap;

    /** listenPort */
    private int listenPort;

    /** proxyHost */
    private String proxyHost;

    /** proxyPort */
    private int proxyPort;

    /**
     * @return mappingMap
     */
    public HashMap getMappingMap() {
        return mappingMap;
    }

    /**
     * @param mappingMap
     */
    public void setMappingMap(HashMap mappingMap) {
        this.mappingMap = mappingMap;
    }

    /**
     * @return listenPort
     */
    public int getListenPort() {
        return listenPort;
    }

    /**
     * @param listenPort listenPort
     */
    public void setListenPort(int listenPort) {
        this.listenPort = listenPort;
    }

    /**
     * @return proxyHost
     */
    public String getProxyHost() {
        return proxyHost;
    }

    /**
     * @param proxyHost proxyHost
     */
    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    /**
     * @return proxyPort
     */
    public int getProxyPort() {
        return proxyPort;
    }

    /**
     * @param proxyPort proxyPort
     */
    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

}

/**
 * url -> file mapping
 */
class Mapping {

    /**
     * @param url
     * @param file
     */
    public Mapping(String url, String file) {
        super();
        this.url = url;
        this.file = file;
    }

    /** url */
    private String url;

    /** file */
    private String file;

    /**
     * @return file
     */
    public String getFile() {
        return file;
    }

    /**
     * @param file file
     */
    public void setFile(String file) {
        this.file = file;
    }

    /**
     * @return url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url url
     */
    public void setUrl(String url) {
        this.url = url;
    }
}