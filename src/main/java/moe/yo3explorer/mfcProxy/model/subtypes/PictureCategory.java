package moe.yo3explorer.mfcProxy.model.subtypes;

public enum PictureCategory {
    FIGURES(1),
    VARIOUS(2),
    BOOTLEGS(3),
    COLLECTIONS(4),
    OFFICIAL(5),
    SPACES(6),
    CHAN(7),
    LOOTS_AND_BOXES(8),
    BANNERS(10),
    ITEMS(12),
    EXPOSITION(14)
    ;

    private final int categoryId;

    PictureCategory(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getCategoryId() {
        return categoryId;
    }
}
