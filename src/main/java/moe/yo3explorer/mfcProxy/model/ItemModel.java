package moe.yo3explorer.mfcProxy.model;

import moe.yo3explorer.mfcProxy.model.subtypes.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ItemModel {
    public String name;
    public Integer previous;
    public Integer next;
    public Integer featuredPartner2;
    public String pictureUrl;
    public Category category;
    public int[] origin;
    public int[] character;
    public LinkedList<ItemCompany> companies;
    public LinkedList<ItemCompany> artists;
    public int[] materials;
    public ItemDimension dimension;
    public LinkedList<ItemReleaseDate> releaseDates;
    public Price price;
    public String jan;
    public boolean hasBootleg;
    public ObjectStats objectStats;
    public int[] buyFrom;
    public Integer featuredPartner;
    public int[] pictures;
    public HashMap<PictureCategory, Integer> pictureCategories;
    public List<RelatedItem> relatedItems;
    public int ownedBy;
    public int orderedBy;
    public int wishedBy;
    public int soldBy;
    public int huntedBy;
    public int reviewedBy;
    public int mentionedIn;
    public int listedIn;
    public double averageRating;
    public int timesRated;
    public LinkedList<Top100Entry> top100;
    public int[] tags;
    public int[] clubs;
    public String addedBy;
    public long addedOn;
    public String editedBy;
    public long editedOn;
}
