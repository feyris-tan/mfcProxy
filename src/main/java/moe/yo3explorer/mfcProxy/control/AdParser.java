package moe.yo3explorer.mfcProxy.control;

import moe.yo3explorer.mfcProxy.model.AdModel;
import moe.yo3explorer.mfcProxy.model.subtypes.Sale;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.inject.Singleton;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class AdParser extends BaseParser<AdModel> {
    @Override
    protected AdModel parse(Document document) {
        //Username
        Element wide = document.getElementById("wide");
        Element classifiedObject = wide.getElementsByClass("classified-object").first();
        Element objectMeta = classifiedObject.getElementsByClass("object-meta").first();
        Element img = objectMeta.select("img").first();
        AdModel result = new AdModel();
        result.username = img.attr("alt");

        //Timestamp
        Element time = objectMeta.getElementsByClass("time").first();
        Element span = time.select("span").get(2);
        String timestampString = span.attr("title");
        result.unixtime = DateUtils.mmddyyyyhhmmssToUnixtime(timestampString);

        //Expression
        Element split = classifiedObject.getElementsByClass("split").first();
        Element splitLeft = split.getElementsByClass("split-left").first();
        Element userExpression = splitLeft.getElementsByClass("user-expression").first();
        Element expression = userExpression.getElementsByClass("expression").first();
        Element content = expression.getElementsByClass("content").first();
        result.userExpression = content.wholeText();

        //Properties
        Element splitRight = split.getElementsByClass("split-right").first();
        Elements formFields = splitRight.getElementsByClass("form-field");
        for (Element formField : formFields) {
            Element formLabel = formField.getElementsByClass("form-label").first();
            if (formLabel == null)
                continue;
            Element formInput = formField.getElementsByClass("form-input").first();
            String key = formLabel.wholeText();
            switch (key)
            {
                case "Price":
                    Element classifiedPrice = formField.getElementsByClass("classified-price").first();
                    Element classifiedPriceCurrency = classifiedPrice.getElementsByClass("classified-price-currency").first();
                    result.currency = classifiedPriceCurrency.wholeText();
                    Element classifiedPriceValue = classifiedPrice.getElementsByClass("classified-price-value").first();
                    result.value = Double.parseDouble(classifiedPriceValue.wholeText());
                    Element small = classifiedPrice.select("small").first();
                    String wholeText = small.wholeText();
                    if (wholeText.equals("Free shipping"))
                        result.freeShipping = true;
                    else
                        logger.warn("Nani? Shipping?");
                    break;
                case "Item condition":
                    Element em = formInput.select("em").first();
                    result.condition = em.wholeText();
                    break;
                case "Box condition":
                    Element emB = formInput.select("em").first();
                    result.boxCondition = emB.wholeText();
                    break;
                case "Location":
                    result.location = formInput.wholeText();
                    break;
                default:
                    logger.warnf("What's that? %s", key);
                    break;
            }
        }

        //Featured Partner
        result.featuredPartner = super.getFeaturedPartner(document);

        //Featured Sales
        Element side = document.getElementById("side");
        Element listing = side.getElementsByClass("listing").first();
        Elements elementsByClass = listing.getElementsByClass("stamp-anchor");
        for (Element saleElement : elementsByClass) {
            Element a = saleElement.select("a").first();
            String href = a.attr("href");
            if (!href.contains("/classified/"))
                continue;
            Integer firstInteger = StringUtils.findFirstInteger(href.split("/"));
            if (firstInteger == null)
                continue;
            Sale sale = new Sale();
            sale.classifiedId = firstInteger;
            sale.name = a.attr("title");

            if (result.featuredSales == null)
                result.featuredSales = new LinkedList<>();
            result.featuredSales.add(sale);
        }

        return result;
    }
}
