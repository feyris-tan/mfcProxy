package moe.yo3explorer.mfcProxy;

import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@Singleton
public class SimpleWebClient {
    @Inject
    Logger logger;

    @Inject
    @RestClient
    KotoriClient kotoriClient;

    @ConfigProperty(name = "kotori.enable", defaultValue = "false")
    boolean useKotori;

    public byte[] webRequest(String url)
    {
        sayHello();

        if (useKotori)
        {
            return kotoriClient.performFetch(url);
        }
        else
        {
            return webRequestWithoutKotori(url);
        }
    }

    private boolean saidHello;
    private void sayHello()
    {
        if (!saidHello)
        {
            logger.info(useKotori ? "Using Kotori Proxy" : "Using direct HttpUrlConnection");
            saidHello = true;
        }
    }

    private byte[] webRequestWithoutKotori(String url)
    {
        try {
            URL theUrl = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) theUrl.openConnection();
            urlConnection.addRequestProperty("User-Agent","mfc_proxy");
            urlConnection.getResponseCode();
            InputStream inputStream = urlConnection.getInputStream();
            return IOUtils.toByteArray(inputStream);
        }
        catch (IOException ioe)
        {
            throw new RuntimeException(ioe);
        }
    }
}
