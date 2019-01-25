package com.spotware.connect.netty.handler;

import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

public class ClientSslEngineFactory implements SslEngineFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientSslEngineFactory.class);
    private SSLContext sslContext;
    private final SslContext nettySslContext;

    public ClientSslEngineFactory() throws SSLException {
        this(null);
    }

    public ClientSslEngineFactory(String ciphers) throws SSLException {
        nettySslContext = createSslContext(ciphers);
    }

    private static SslContext createSslContext(String ciphers) throws SSLException {
        SslContextBuilder sslContextBuilder = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE);

        sslContextBuilder.sslProvider(SslProvider.JDK);

        applyCipherSuitesFromString(sslContextBuilder, ciphers);
        return sslContextBuilder.build();
    }

    private static void applyCipherSuitesFromString(SslContextBuilder sslContextBuilder, String cipherSuitesString) {
        if (cipherSuitesString == null) {
            return;
        }
        List<String> list = new ArrayList<>();
        String[] cipherSuitesArray = cipherSuitesString.split(",");
        for (String cipherSuite : cipherSuitesArray) {
            String trim = cipherSuite.trim();
            if (!trim.isEmpty()) {
                list.add(trim);
            }
        }
        sslContextBuilder.ciphers(list);
    }

    public void load() throws GeneralSecurityException {
        sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
            }
        }}, null);
    }

    @Override
    public SslHandler newHandler(Channel channel) {
        SslHandler newHandler = nettySslContext.newHandler(channel.alloc());
        LOGGER.trace("Ciphers={}", (Object[]) newHandler.engine().getEnabledCipherSuites());
        return newHandler;
    }

    public SSLContext getSslContext() {
        return sslContext;
    }

    public void applyTrustAll() {
        SSLContext.setDefault(sslContext);
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, sslSession) -> true);
    }

    public static void applyTrustAllDefault() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                }
            }}, null);
            SSLContext.setDefault(sslContext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        HttpsURLConnection.setDefaultHostnameVerifier((hostname, sslSession) -> true);
    }
}
