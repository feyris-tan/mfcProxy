package moe.yo3explorer.mfcProxy.model;

import moe.yo3explorer.mfcProxy.model.subtypes.Award;
import moe.yo3explorer.mfcProxy.model.subtypes.ObjectStats;
import moe.yo3explorer.mfcProxy.model.subtypes.RelatedItem;

import java.util.LinkedList;
import java.util.List;

public class PictureModel {
    public Integer featuredPartner;
    public String title;
    public String username;
    public long unixtime;
    public String url;
    public int points;
    public int score;
    public LinkedList<Award> awards;
    public String aboutThisPicture;
    public int[] moreBySameAuthor;
    public List<RelatedItem> relatedItems;
    public int[] tags;
    public int[] clubs;
    public ObjectStats stats;
}
