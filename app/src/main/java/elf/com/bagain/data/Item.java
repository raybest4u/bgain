package elf.com.bagain.data;

/**
 * Base class for all model types
 * User：McCluskey Ray on 2015/11/10 13:54
 * email：lr8734@126.com
 */
public abstract class Item {
    public final long id;
    public final String title;
    public String url; // can't be final as some APIs use different serialized names
    public String dataSource;
    public int page;
    public float weight;
    public float weightBoost;

    public Item(long id,
                     String title,
                     String url) {
        this.id = id;
        this.title = title;
        this.url = url;
    }

    @Override
    public String toString() {
        return title;
    }

    /**
     * Equals check based on the id field
     */
    @Override
    public boolean equals(Object o) {
        return (o.getClass() == getClass() && ((Item) o).id == id);
    }
}
