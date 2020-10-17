package moe.yo3explorer.mfcProxy.model;

import moe.yo3explorer.mfcProxy.model.subtypes.Sale;

import java.util.HashMap;
import java.util.List;

public class FrontPageModel {
    public int[] partners;
    public int[] picturesOtd;
    public List<Sale> featuredSales;
    public int[] itemsOnFire;
    public Integer featuredPartner;
    public HashMap<String, List<Integer>> articles;
}
