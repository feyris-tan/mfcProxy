package moe.yo3explorer.mfcProxy.control;

import moe.yo3explorer.mfcProxy.model.PartnerRedirectModel;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.inject.Singleton;
import java.util.HashMap;

@Singleton
public class PartnerRedirectParser extends BaseParser<PartnerRedirectModel> {

    @Override
    protected PartnerRedirectModel parse(Document document) {
        Element head = document.head();
        Element title = head.select("title").first();
        PartnerRedirectModel result = new PartnerRedirectModel();
        result.title = title.html();

        Elements meta = head.select("meta");
        for (Element element : meta) {
            if (result.meta == null)
                result.meta = new HashMap<String, String>();

            String k = null;
            k = element.attr("name");
            if (k.equals(""))
                k = element.attr("property");

            if (result.meta.containsKey(k))
                continue;

            String v = element.attr("content");
            if (v.equals(""))
                continue;
            result.meta.put(k,v);
        }
        return result;
    }
}
