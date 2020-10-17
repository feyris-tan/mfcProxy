package moe.yo3explorer.mfcProxy.control;

import moe.yo3explorer.mfcProxy.SimpleWebClient;
import org.jboss.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.inject.Inject;
import javax.print.Doc;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public abstract class BaseParser<T>
{
    @Inject
    SimpleWebClient webClient;

    @Inject
    Logger logger;

    public T parse(String url)
    {
        byte[] bytes = webClient.webRequest(url);

        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try {
            Document document = Jsoup.parse(bais, "UTF-8", url);
            return parse(document);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract T parse(Document document);

    protected String getTitle(@NotNull Document document)
    {
        Element contentHeadline = document.getElementsByClass("content-headline").first();
        Element headline = contentHeadline.getElementsByClass("h1-headline-value").first();
        int children = headline.childrenSize();
        switch (children)
        {
            case 0:
                break;
            case 1:
                headline = headline.children().first();
                break;
            default:
                logger.warn("Unknown number of headline children!");
                break;
        }
        String title = headline.html();
        return title;
    }
}
