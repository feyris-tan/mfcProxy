package moe.yo3explorer.mfcProxy.control;

import moe.yo3explorer.mfcProxy.model.PictureModel;
import moe.yo3explorer.mfcProxy.model.subtypes.Award;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import javax.inject.Singleton;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class PictureParser extends BaseParser<PictureModel> {
    @Override
    protected PictureModel parse(Document document) {
        PictureModel result = new PictureModel();
        result.title = getTitle(document);
        result.featuredPartner = getFeaturedPartner(document);

        //Username
        Element contentWrapper = document.getElementsByClass("content-wrapper").first();
        Element wide = contentWrapper.getElementById("wide");
        Element pictureObject = wide.getElementsByClass("picture-object").first();
        Element objectMeta = pictureObject.getElementsByClass("object-meta").first();
        Element img = objectMeta.select("img").first();
        result.username = img.attr("alt");

        //Timestamp
        Element time = objectMeta.getElementsByClass("time").first();
        Element span = time.select("span").get(2);
        String timestampString = span.attr("title");
        result.unixtime = DateUtils.mmddyyyyhhmmssToUnixtime(timestampString);

        //Picture URL
        Element thePicture = pictureObject.getElementsByClass("the-picture").first();
        Element img1 = thePicture.select("img").first();
        result.url = img1.attr("src");

        //Hits & Comments
        Element objectStats = pictureObject.getElementsByClass("object-stats").first();
        Elements meta = objectStats.select("meta");
        for (Element element : meta) {
            String itemprop = element.attr("itemprop");
            if (!itemprop.equals("interactionCount"))
                continue;

            //TODO: Fetch comments from https://myfigurecollection.net/picture/{id}/comments/
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

        //Likes
        List<TextNode> a = objectStats.select("a").stream()
                .flatMap(x -> x.textNodes().stream())
                .collect(Collectors.toList());
        result.likes = a.stream()
                .map(x -> x.getWholeText())
                .filter(x -> x.endsWith(" likes"))
                .map(x -> x.split(" "))
                .mapToInt(x -> Integer.parseInt(x[0]))
                .findFirst().orElse(0);

        //Points
        result.points = a.stream()
                .map(x -> x.getWholeText())
                .filter(x -> x.endsWith(" points"))
                .map(x -> x.replace(",",""))
                .map(x -> x.split(" "))
                .mapToInt(x -> Integer.parseInt(x[0]))
                .findFirst().orElse(0);

        //Score
        Element pictureScore = pictureObject.getElementsByClass("picture-score").first();
        Element score = pictureScore.getElementsByClass("score").first();
        result.score = score.getElementsByClass("on").size();

        //Awards
        Element pictureAwards = wide.getElementsByClass("picture-awards").first();
        if (pictureAwards != null) {
            Elements li = pictureAwards.select("li");
            for (Element element : li) {
                if (result.awards == null)
                    result.awards = new LinkedList<Award>();
                Elements div = element.select("div");
                Award award = new Award();
                if (div.size() > 0)
                    award.headline = div.get(0).html();
                if (div.size() > 1)
                    award.subheader = div.get(1).html();
                if (div.size() > 2)
                    logger.warn("Encountered weird award!");
                result.awards.add(award);
            }
        }

        //About this picture
        Element aboutHelper = wide.getElementsByClass("icon-info-circle").first();
        if (aboutHelper != null) {
            aboutHelper = aboutHelper.parent();
            aboutHelper = aboutHelper.parent();
            aboutHelper = aboutHelper.getElementsByClass("bbcode").first();
            result.aboutThisPicture = aboutHelper.wholeText();
        }

        //More by same author
        Element side = contentWrapper.getElementById("side");
        Element pictureItems = side.getElementsByClass("picture-icons").first();
        result.moreBySameAuthor = pictureItems.select("a").stream()
                .map(x -> x.attr("href"))
                .filter(x -> x.contains("picture"))
                .map(x -> x.split("/"))
                .mapToInt(x -> Integer.parseInt(x[x.length - 1]))
                .toArray();

        //Related items
        Element tbxTargetItems = side.getElementsByClass("tbx-target-ITEMS").first();
        result.relatedItems =  tbxTargetItems.getElementsByClass("stamp-anchor").stream()
                .map(x -> x.select("a"))
                .filter(x -> x.size() > 0)
                .map(x -> x.first())
                .map(x -> x.attr("href"))
                .filter(x -> x.contains("item"))
                .map(x -> x.split("/"))
                .mapToInt(x -> Integer.parseInt(x[x.length - 1]))
                .toArray();

        //Tags
        //TODO: fetch tag contents from https://myfigurecollection.net/tag/{id}
        Element tbxTargetTags = side.getElementsByClass("tbx-target-TAGS").first();
        result.tags = tbxTargetTags.select("a").stream()
                .map(x -> x.attr("href"))
                .filter(x -> x.contains("/tag/"))
                .map(x -> x.split("/"))
                .mapToInt(x -> Integer.parseInt(x[x.length - 1]))
                .toArray();

        //Clubs
        //TODO: Fetch Club contents from https://myfigurecollection.net/club/{id}
        Element tbxTargetClubs = side.getElementsByClass("tbx-target-CLUBS").first();
        Elements stampAnchor = tbxTargetClubs.getElementsByClass("stamp-anchor");
        result.clubs = stampAnchor.stream()
                .map(x -> x.select("a").first())
                .map(x -> x.attr("href"))
                .filter(x -> x.contains("/club/"))
                .map(x -> x.split("/"))
                .mapToInt(x -> Integer.parseInt(x[x.length - 1]))
                .toArray();

        return result;
    }


}
