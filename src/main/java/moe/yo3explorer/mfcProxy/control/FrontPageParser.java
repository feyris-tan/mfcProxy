package moe.yo3explorer.mfcProxy.control;

import moe.yo3explorer.mfcProxy.model.FrontPageModel;
import moe.yo3explorer.mfcProxy.model.subtypes.Sale;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class FrontPageParser extends BaseParser<FrontPageModel> {
    @Override
    protected FrontPageModel parse(Document document) {

        Element content = document.getElementById("content");
        Element contentWrapper = content.getElementsByClass("content-wrapper").first();
        Element wide = contentWrapper.getElementById("wide");
        Element wrapper = contentWrapper.getElementsByClass("wrapper").first();

        //Partners
        Element first = wrapper.getElementsByClass("home-partners").first();
        int[] collect = first.select("a").stream()
                .map(x -> x.attr("href"))
                .map(x -> x.split("="))
                .mapToInt(x -> Integer.parseInt(x[x.length - 1]))
                .toArray();
        FrontPageModel result = new FrontPageModel();
        result.partners = collect;

        //Pictures
        first = wrapper.getElementsByClass("home-picture-mixed-content").first();
        result.picturesOtd = first.select("a").stream()
                .map(x -> x.attr("href"))
                .map(x -> x.split("/"))
                .mapToInt(x -> Integer.parseInt(x[x.length - 1]))
                .toArray();

        //Sales
        Element section = wrapper.select("section").get(3);
        Element listing = section.getElementsByClass("listing").first();
        Elements listingItems = listing.getElementsByClass("listing-item");
        for (Element listingItem : listingItems) {
            Element listAnchor = listingItem.getElementsByClass("list-anchor").first();
            Element a = listAnchor.select("a").first();
            String[] href = a.attr("href").split("/");
            Sale sale = new Sale();
            sale.classifiedId = Integer.parseInt(href[href.length - 1]);
            sale.name = a.wholeText();

            if (result.featuredSales == null)
                result.featuredSales = new LinkedList<>();
            result.featuredSales.add(sale);
        }


        //Items on fire
        //TODO: get items using https://myfigurecollection.net/item/{0}
        Element itemIcons = wrapper.getElementsByClass("item-icons").first();
        result.itemsOnFire = itemIcons.select("a").stream()
                .map(x -> x.attr("href"))
                .map(x -> x.split("/"))
                .mapToInt(x -> Integer.parseInt(x[x.length - 1]))
                .toArray();

        //Featured partner
        Element side = contentWrapper.getElementById("side");
        result.featuredPartner = getFeaturedPartner(document);

        //Articles
        //TODO: fetch articles from https://myfigurecollection.net/blog/46546
        section = side.select("section").get(1);
        listing = section.getElementsByClass("listing").first();
        Elements li = listing.select("li");
        result.articles = new HashMap<String,List<Integer>>();
        String currentTitle = null;
        for (int i = 0; i < li.size(); i++)
        {
            Element currentListing = li.get(i);
            String aClass = currentListing.attr("class");
            switch (aClass)
            {
                case "listing-title":
                    currentTitle = currentListing.html();
                    if (!result.articles.containsKey(currentTitle))
                        result.articles.put(currentTitle,new LinkedList<>());
                    break;
                case "listing-item":
                    Element a1 = currentListing.select("a").first();
                    String href = a1.attr("href");
                    result.articles.get(currentTitle).add(StringUtils.findFirstInteger(href.split("/")));
                    break;
                case "listing-more":
                    i = li.size();
                    break;
                default:
                    logger.warn(String.format("Unknown articles class: %s",aClass));
                    break;
            }
        }
        return result;
    }
}
