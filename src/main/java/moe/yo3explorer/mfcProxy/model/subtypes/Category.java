package moe.yo3explorer.mfcProxy.model.subtypes;

public enum Category
{
    PREPAINTED(1);

    private final int categoryId;

    Category(int categoryId) {

        this.categoryId = categoryId;
    }

    public int getCategoryId() {
        return categoryId;
    }
}
