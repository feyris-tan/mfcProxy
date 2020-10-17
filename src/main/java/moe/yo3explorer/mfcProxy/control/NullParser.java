package moe.yo3explorer.mfcProxy.control;

import moe.yo3explorer.mfcProxy.model.FrontPageModel;
import org.jsoup.nodes.Document;

import javax.inject.Singleton;

@Singleton
public class NullParser extends BaseParser<FrontPageModel> {
    @Override
    protected FrontPageModel parse(Document document) {
        return null;
    }
}
