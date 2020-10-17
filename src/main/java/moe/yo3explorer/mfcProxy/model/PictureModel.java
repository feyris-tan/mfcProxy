package moe.yo3explorer.mfcProxy.model;

import moe.yo3explorer.mfcProxy.model.subtypes.Award;

import java.util.LinkedList;

public class PictureModel {
    public Integer featuredPartner;
    public String title;
    public String username;
    public long unixtime;
    public String url;
    public Integer comments;
    public Integer hits;
    public int likes;
    public int points;
    public int score;
    public LinkedList<Award> awards;
    public String aboutThisPicture;
    public int[] moreBySameAuthor;
    public int[] relatedItems;
    public int[] tags;
    public int[] clubs;
}
