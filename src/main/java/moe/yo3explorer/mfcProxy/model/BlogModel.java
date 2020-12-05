package moe.yo3explorer.mfcProxy.model;

import moe.yo3explorer.mfcProxy.model.subtypes.Comment;
import moe.yo3explorer.mfcProxy.model.subtypes.Header;

public class BlogModel {
    public Header header;
    public Integer featuredPartner;
    public String author;
    public long posted;
    public String content;
    public Comment[] comments;
}
