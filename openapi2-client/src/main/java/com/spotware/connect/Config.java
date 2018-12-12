package com.spotware.connect;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {

    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);

    private String host;
    private int port;

    private String clientId;
    private String clientSecret;
    private String accessToken;
    private Long ctid;

    public Config() {
        String defaultFileName = "application.properties";
        load(defaultFileName);
    }

    public Config(String defaultFileName) {
        load(defaultFileName);
    }


    private void load(String fileName) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties prop = new Properties();
        try (InputStream resourceStream = loader.getResourceAsStream(fileName)) {
            prop.load(resourceStream);
            host = prop.getProperty("host");
            port = Integer.parseInt(prop.getProperty("port"));
            clientId = prop.getProperty("CLIENT_PUBLIC_ID");
            clientSecret = prop.getProperty("CLIENT_SECRET");
            accessToken = prop.getProperty("ACCESS_TOKEN");
            ctid = Long.parseLong(prop.getProperty("CTID_TRADER_ACCOUNT_ID"));

        } catch (IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Long getCtid() {
        return ctid;
    }
}
