package moe.yo3explorer.mfcProxy.control;

import moe.yo3explorer.mfcProxy.boundary.Item;
import moe.yo3explorer.mfcProxy.model.ItemModel;
import moe.yo3explorer.mfcProxy.model.subtypes.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.inject.Singleton;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class ItemParser extends BaseParser<ItemModel> {
    @Override
    protected ItemModel parse(Document document) {
        ItemModel result = new ItemModel();
        result.header = parseHeader(document);

        //Vertical Featured Partner
        Element contentWrapper = document.getElementsByClass("content-wrapper").first();
        Element xyWideTop = contentWrapper.getElementsByClass("xy-wide-top").first();
        Element xyWrapper = xyWideTop.getElementsByClass("xy-wrapper").first();
        Element a = xyWrapper.select("a").first();
        result.featuredPartner2 = StringUtils.findFirstInteger(a.attr("href").split("/"));

        //Bild
        Element wide = contentWrapper.getElementById("wide");
        Element wrapper = wide.getElementsByClass("wrapper").first();
        Element itemObject = wrapper.getElementsByClass("item-object").first();
        Element split = itemObject.getElementsByClass("split").first();
        Element splitLeft = split.getElementsByClass("split-left").first();
        Element itemPicture = splitLeft.getElementsByClass("item-picture").first();
        Element tbxPswp = itemPicture.getElementsByClass("tbx-pswp").first();
        Element a1 = tbxPswp.select("a").first();
        Element img = a1.select("img").first();
        result.pictureUrl = img.attr("src");

        //Eigenschaften
        Element splitRight = split.getElementsByClass("split-right").first();
        Element form = splitRight.getElementsByClass("form").first();
        Elements formFields = form.getElementsByClass("form-field");
        for (Element formField : formFields) {
            Element formLabel = formField.getElementsByClass("form-label").first();
            Element formInput = formField.getElementsByClass("form-input").first();
            String label = formLabel.wholeText();
            switch (label)
            {
                case "Category":
                    String categoryName = formInput.wholeText();
                    if (categoryName.equals("Prepainted"))
                        result.category = Category.PREPAINTED;
                    else
                        logger.warnf("Unknown category: %s",label);
                    break;
                case "Origin":
                    //TODO: fetch entries from https://myfigurecollection.net/entry/{id}
                    result.origin = formInput.select("a").stream()
                            .map(x -> x.attr("href"))
                            .filter(x -> x.contains("/entry/"))
                            .map(x -> x.split("/"))
                            .mapToInt(x -> Integer.parseInt(x[x.length - 1]))
                            .toArray();
                    break;
                case "Character":
                    result.character = formInput.select("a").stream()
                            .map(x -> x.attr("href"))
                            .filter(x -> x.contains("/entry/"))
                            .map(x -> x.split("/"))
                            .mapToInt(x -> Integer.parseInt(x[x.length - 1]))
                            .toArray();
                    break;
                case "Company":
                    Elements a2 = formInput.select("a");
                    for (Element company : a2) {
                        String href = company.attr("href");
                        if (!href.contains("/entry"))
                            continue;
                        String[] split1 = href.split("/");
                        ItemCompany itemCompany = new ItemCompany();
                        itemCompany.id = Integer.parseInt(split1[split1.length - 1]);
                        Element small = a2.select("small").first();
                        itemCompany.role = small.wholeText();
                        if (itemCompany.role.startsWith("As "))
                            itemCompany.role = itemCompany.role.substring(3);
                        if (result.companies == null)
                            result.companies = new LinkedList<ItemCompany>();
                        result.companies.add(itemCompany);
                    }
                    break;
                case "Artists":
                    Elements a3 = formInput.select("a");
                    for (Element company : a3) {
                        String href = company.attr("href");
                        if (!href.contains("/entry"))
                            continue;
                        String[] split1 = href.split("/");
                        ItemCompany itemCompany = new ItemCompany();
                        itemCompany.id = Integer.parseInt(split1[split1.length - 1]);
                        Element small = company.select("small").first();
                        itemCompany.role = small.wholeText();
                        if (itemCompany.role.startsWith("As "))
                            itemCompany.role = itemCompany.role.substring(3);
                        if (result.artists == null)
                            result.artists = new LinkedList<ItemCompany>();
                        result.artists.add(itemCompany);
                    }
                    break;
                case "Materials":
                    result.materials = formInput.select("a").stream()
                            .map(x -> x.attr("href"))
                            .filter(x -> x.contains("/entry/"))
                            .map(x -> x.split("/"))
                            .mapToInt(x -> Integer.parseInt(x[x.length - 1]))
                            .toArray();
                    break;
                case "Scale & Dimensions":
                    String scaleAndDimensions = formInput.wholeText();
                    scaleAndDimensions = scaleAndDimensions.replace('\u00A0','\u0020');
                    String[] scaleWords = scaleAndDimensions.split(" ");
                    ItemDimension dimension = new ItemDimension();
                    switch (scaleWords[0])
                    {
                        case "1/8":
                            dimension.scale = Scale._1_8;
                            break;
                        default:
                            logger.warnf("What scale is this? %s",scaleWords[0]);
                            break;
                    }
                    if (scaleWords[2].startsWith("H="))
                    {
                        scaleWords[2] = scaleWords[2].substring(2);
                        if (scaleWords[2].endsWith("mm"))
                        {
                            dimension.unit = "mm";
                            scaleWords[2] = scaleWords[2].substring(0, scaleWords.length - 2);
                        }
                        else
                        {
                            logger.warnf("Don't know what unit is %s",scaleWords[2]);
                            break;
                        }
                        dimension.height = Double.parseDouble(scaleWords[2]);
                    }
                    else
                    {
                        logger.warnf("Don't know how to extract height from %s",scaleWords[1]);
                        break;
                    }
                    result.dimension = dimension;
                    break;
                case "Release dates":
                    Elements children = formInput.children();
                    ItemReleaseDate itemReleaseDate = null;
                    for (Element child : children) {
                        if (child.is("a"))
                        {
                            itemReleaseDate = new ItemReleaseDate();
                            String s = child.wholeText();
                            itemReleaseDate.unixtime = DateUtils.mmddyyyyToUnixtime(s);
                        }
                        else if (child.is("small"))
                        {
                            String s = child.wholeText();
                            if (s.startsWith("As "))
                            {
                                s = s.substring(3);
                                itemReleaseDate.edition = s;
                            }
                        }
                        else if (child.is("br"))
                        {
                            if (itemReleaseDate == null)
                                continue;
                            if (itemReleaseDate.unixtime == 0)
                            {
                                logger.warn("Got a release date where the date is missing!");
                                break;
                            }
                            if (itemReleaseDate.edition == null)
                            {
                                logger.warn("Got a release date where the edition is missing!");
                                break;
                            }
                            if (result.releaseDates == null)
                                result.releaseDates = new LinkedList<ItemReleaseDate>();
                            result.releaseDates.add(itemReleaseDate);
                            itemReleaseDate = null;
                        }
                        else
                        {
                            logger.warn("Encountered a weird tag while parsing release dates: " + child.tagName());
                        }
                    }
                    break;
                case "Price":
                    Element itemPrice = formInput.getElementsByClass("item-price").first();
                    result.price = StringUtils.parsePrice(itemPrice.wholeText());
                    break;
                case "JAN":
                    Element first = formInput.getElementsByClass("tbx-window").first();
                    result.jan = first.wholeText();
                    break;
                case "Various":
                    Elements children1 = formInput.children();
                    for (Element element : children1) {
                        if (element.is(".item-has-bootleg"))
                            result.hasBootleg = true;
                        else
                            logger.warnf("Don't know what various \"%s\" means.",element.className());
                    }
                    break;
                default:
                    logger.warnf("What's this? %s",label);
                    break;
            }
        }

        //Hits & Comments & Likes
        //TODO: fetch item comments from https://myfigurecollection.net/item/78589/comments/
        result.objectStats = super.parseObjectStats(document);

        //Purchaseable from
        Element first = wide.getElementsByClass("home-partners").first();
        result.buyFrom = first.select("a").stream()
                .map(x -> x.attr("href"))
                .filter(x -> x.contains("&pid="))
                .map(x -> x.split("pid="))
                .filter(x -> x.length == 2)
                .map(x -> x[1].split("&"))
                .map(x -> x[0])
                .mapToInt(Integer::parseInt)
                .toArray();

        //Featured partner
        result.featuredPartner = super.getFeaturedPartner(document);

        //Pictures
        Element side = contentWrapper.getElementById("side");
        Element sub = side.getElementsByClass("sub").first();
        Element pictureIcons = sub.getElementsByClass("picture-icons").first();
        Elements meta = pictureIcons.select("meta");
        result.pictures = meta.stream()
                .filter(x -> x.attr("name").equals("vars"))
                .filter(x -> x.attr("content").startsWith("2:"))
                .map(x -> x.attr("content"))
                .map(x -> x.substring(2))
                .mapToInt(Integer::parseInt)
                .toArray();

        //Object Categories
        Element objectCategories = sub.getElementsByClass("object-categories").first();
        Elements pictureCategory = objectCategories.getElementsByClass("picture-category");
        for (Element element : pictureCategory) {
            Optional<String> category = element.classNames().stream().filter(x -> x.startsWith("picture-category-")).findAny();
            if (category.isEmpty())
                continue;

            String categoryClass = category.get();
            categoryClass = categoryClass.substring("picture-category-".length());
            int categoryId = Integer.parseInt(categoryClass);
            Optional<PictureCategory> first1 = Arrays.stream(PictureCategory.values()).filter(x -> x.getCategoryId() == categoryId).findFirst();
            if (first1.isEmpty())
            {
                logger.warn("Unknown category id: " + categoryClass);
                continue;
            }

            Element strong = element.select("strong").first();
            int numPics = Integer.parseInt(strong.wholeText());

            if (result.pictureCategories == null)
                result.pictureCategories = new HashMap<PictureCategory,Integer>();
            result.pictureCategories.put(first1.get(),numPics);
        }

        //Related items
        result.relatedItems = parseRelatedItems(document);

        //Community
        Element data_2 = side.getElementsByClass("form").first();
        formFields = data_2.getElementsByClass("form-field");
        for (Element formField : formFields) {
            Element formLabel = formField.getElementsByClass("form-label").first();
            Element formInput = formField.getElementsByClass("form-input").first();
            String label = formLabel.wholeText();
            String s = null;
            switch (label)
            {
                case "Owned by":
                    s = formInput.wholeText();
                    s = s.replace(",","");
                    result.ownedBy = StringUtils.findFirstIntegerOrZero(s.split(" "));
                    break;
                case "Ordered by":
                    s = formInput.wholeText();
                    s = s.replace(",","");
                    result.orderedBy = StringUtils.findFirstIntegerOrZero(s.split(" "));
                    break;
                case "Wished by":
                    s = formInput.wholeText();
                    s = s.replace(",","");
                    result.wishedBy = StringUtils.findFirstIntegerOrZero(s.split(" "));
                    break;
                case "Sold by":
                    s = formInput.wholeText();
                    s = s.replace(",","");
                    result.soldBy = StringUtils.findFirstIntegerOrZero(s.split(" "));
                    break;
                case "Hunted by":
                    s = formInput.wholeText();
                    s = s.replace(",","");
                    result.huntedBy = StringUtils.findFirstIntegerOrZero(s.split(" "));
                    break;
                case "Reviewed by":
                    s = formInput.wholeText();
                    s = s.replace(",","");
                    result.reviewedBy = StringUtils.findFirstIntegerOrZero(s.split(" "));
                    break;
                case "Mentioned in":
                    s = formInput.wholeText();
                    s = s.replace(",","");
                    result.mentionedIn = StringUtils.findFirstIntegerOrZero(s.split(" "));
                    break;
                case "Listed in":
                    s = formInput.wholeText();
                    s = s.replace(",","");
                    result.listedIn = StringUtils.findFirstIntegerOrZero(s.split(" "));
                    break;
                case "Average rating":
                    Element div = formInput.select("div").first();
                    Element a2 = div.select("a").first();
                    Element strong = a2.select("strong").first();
                    String s1 = strong.wholeText();
                    result.averageRating = Double.parseDouble(s1);
                    Optional<Element> first1 = a2.select("meta").stream()
                            .filter(x -> x.attr("itemprop").equals("ratingCount"))
                            .findFirst();
                    if (first1.isEmpty())
                        continue;
                    String content = first1.get().attr("content");
                    result.timesRated = Integer.parseInt(content);
                    break;
                case "Top 100":
                    Elements children = formInput.children();
                    Top100Entry top100Entry = null;
                    for (Element child : children) {
                        if (child.is("strong"))
                        {
                            top100Entry = new Top100Entry();
                            top100Entry.rank = Integer.parseInt(child.wholeText());
                        }
                        else if (child.is("small"))
                        {
                            Element first2 = child.getElementsByClass("item-top").first();
                            if (first2 != null)
                            {
                                String href = first2.attr("href");
                                String[] split1 = href.split("=");
                                top100Entry.listName = split1[split1.length - 1];
                            }
                            else if (child.wholeText().startsWith("/"))
                            {
                                String substring = child.wholeText().substring(1);
                                top100Entry.maxRank = Integer.parseInt(substring);
                            }
                            else
                            {
                                logger.warn("What is this small?");
                            }
                        }
                        else if (child.is("br"))
                        {
                            if (top100Entry == null)
                                break;
                            if (result.top100 == null)
                                result.top100 = new LinkedList<Top100Entry>();
                            result.top100.add(top100Entry);
                            top100Entry = null;
                        }
                    }
                    if (top100Entry != null)
                    {
                        if (result.top100 == null)
                            result.top100 = new LinkedList<Top100Entry>();
                        result.top100.add(top100Entry);
                    }
                    break;
                default:
                    logger.warn("Unknown community property: " + label);
                    break;
            }
        }

        //Tags
        result.tags = super.parseTags(document);

        //Clubs
        result.clubs = super.parseClubs(document);

        //Changelog
        Element iconTerminal = side.getElementsByClass("icon-terminal").first();
        Element h2 = iconTerminal.parent();
        Element section = h2.parent();
        Elements changelogFormFields = section.getElementsByClass("form-field");
        for (Element changelogFormField : changelogFormFields) {
            Element formLabel = changelogFormField.getElementsByClass("form-label").first();
            Element formInput = changelogFormField.getElementsByClass("form-input").first();
            String label = formLabel.wholeText();
            String s = null;
            switch (label) {
                case "Added by":
                    Element a2 = formInput.select("a").first();
                    result.addedBy = a2.wholeText();
                    Element small = formInput.select("small").first();
                    Element span = small.select("span").first();
                    result.addedOn = DateUtils.mmddyyyyhhmmssToUnixtime(span.attr("title"));
                    break;
                case "Last edited by":
                    Element a3 = formInput.select("a").first();
                    result.editedBy = a3.wholeText();
                    Element small2 = formInput.select("small").first();
                    Element span2 = small2.select("span").first();
                    result.editedOn = DateUtils.mmddyyyyhhmmssToUnixtime(span2.attr("title"));
                    break;
                default:
                    logger.info("Don't know about this changelog: " + label);
                    break;
            }
        }

        //Comments
        result.comments = super.parseComments(document);

        return result;
    }
}
