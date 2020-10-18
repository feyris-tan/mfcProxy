package moe.yo3explorer.mfcProxy.control;

import moe.yo3explorer.mfcProxy.SimpleWebClient;
import moe.yo3explorer.mfcProxy.model.subtypes.ObjectStats;
import moe.yo3explorer.mfcProxy.model.subtypes.RelatedItem;
import org.jboss.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import javax.inject.Inject;
import javax.print.Doc;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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

    protected Integer getFeaturedPartner(Document document)
    {
        Element content = document.getElementById("content");
        Element contentWrapper = content.getElementsByClass("content-wrapper").first();
        Element side = contentWrapper.getElementById("side");
        Element partner = side.getElementsByClass("partner").first();
        Element a = partner.select("a").first();
        String[] hrefs = a.attr("href").split("/");
        return StringUtils.findFirstInteger(hrefs);
    }

    protected ObjectStats parseObjectStats(Document document)
    {
        ObjectStats result = new ObjectStats();
        Element contentWrapper = document.getElementsByClass("content-wrapper").first();
        Element wide = contentWrapper.getElementById("wide");
        Element objectStats = wide.getElementsByClass("object-stats").first();
        Elements meta = objectStats.select("meta");
        for (Element element : meta) {
            String itemprop = element.attr("itemprop");
            if (!itemprop.equals("interactionCount"))
                continue;


            String content = element.attr("content");
            String[] split = content.split(":");
            switch(split[0])
            {
                case "UserComments":
                    result.comments = StringUtils.findFirstInteger(split);
                    break;
                case "UserPageVisits":
                    result.hits = StringUtils.findFirstInteger(split);
                    break;
                default:
                    logger.warnf("Unknown interactioncount %s",split[0]);
                    break;
            }
        }

        List<TextNode> a = objectStats.select("a").stream()
                .flatMap(x -> x.textNodes().stream())
                .collect(Collectors.toList());
        result.likes = a.stream()
                .map(x -> x.getWholeText())
                .filter(x -> x.endsWith(" likes"))
                .map(x -> x.split(" "))
                .mapToInt(x -> Integer.parseInt(x[0]))
                .findFirst().orElse(0);

        return result;
    }

    protected List<RelatedItem> parseRelatedItems(Document document)
    {
        LinkedList<RelatedItem> result = new LinkedList<>();

        Element contentWrapper = document.getElementsByClass("content-wrapper").first();
        Element side = contentWrapper.getElementById("side");
        Element tbxTargetItems = side.getElementsByClass("tbx-target-ITEMS").first();

        Elements listingItems = tbxTargetItems.getElementsByClass("listing-item");
        for (Element listingItem : listingItems) {
            RelatedItem child = new RelatedItem();

            Element first1 = listingItem.getElementsByClass("listing-category").first();
            if (first1 != null)
            {
                Element strong = listingItem.select("em").first();
                child.relationType = strong.wholeText();
            }
            Element stampAnchor = listingItem.getElementsByClass("stamp-anchor").first();
            Elements a = stampAnchor.select("a");
            if (a.size() == 0)
                continue;
            Element first = a.first();
            String href = first.attr("href");
            if (!href.contains("item"))
                continue;
            String[] split = href.split("/");
            child.itemId = Integer.parseInt(split[split.length - 1]);
            result.add(child);
        }
        return result;
    }

    protected int[] parseTags(Document document)
    {
        Element contentWrapper = document.getElementsByClass("content-wrapper").first();
        Element side = contentWrapper.getElementById("side");
        Element tbxTargetTags = side.getElementsByClass("tbx-target-TAGS").first();
        return tbxTargetTags.select("a").stream()
                .map(x -> x.attr("href"))
                .filter(x -> x.contains("/tag/"))
                .map(x -> x.split("/"))
                .mapToInt(x -> Integer.parseInt(x[x.length - 1]))
                .toArray();
    }

    protected int[] parseClubs(Document document)
    {
        Element contentWrapper = document.getElementsByClass("content-wrapper").first();
        Element side = contentWrapper.getElementById("side");
        Element tbxTargetClubs = side.getElementsByClass("tbx-target-CLUBS").first();
        Elements stampAnchor = tbxTargetClubs.getElementsByClass("stamp-anchor");
        return stampAnchor.stream()
                .map(x -> x.select("a").first())
                .map(x -> x.attr("href"))
                .filter(x -> x.contains("/club/"))
                .map(x -> x.split("/"))
                .mapToInt(x -> Integer.parseInt(x[x.length - 1]))
                .toArray();
    }
}
