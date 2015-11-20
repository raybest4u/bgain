package elf.com.bagain.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * User：McCluskey Ray on 2015/11/10 14:14
 * email：lr8734@126.com
 */
public class BliDingItem extends Item implements Parcelable {
    public final int aid;
    public final int typeid;
    public final String subtitle;
    public final String play;
    public final int review;
    public final int video_review;
    public final int favorites;
    public final int mid;
    public final String author;
    public final String description;
    public final String create;
    public final long pubdate;
    public final String pic;
    public final int  credit;
    public final int coins;
    public final String duration;

    public boolean hasFadedIn = false;
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<BliDingItem> CREATOR = new Parcelable.Creator<BliDingItem>() {
        @Override
        public BliDingItem createFromParcel(Parcel in) {
            return new BliDingItem(in);
        }

        @Override
        public BliDingItem[] newArray(int size) {
            return new BliDingItem[size];
        }
    };

    public BliDingItem(  int aid, String title, String pic,int typeid, String subtitle, String play, int review, int video_review, int favorites, int mid, String author, String description, String create, long pubdate,  int credit, int coins, String duration) {
        super(aid, title, pic);
        this.aid = aid;
        this.typeid = typeid;
        this.subtitle = subtitle;
        this.play = play;
        this.review = review;
        this.video_review = video_review;
        this.favorites = favorites;
        this.mid = mid;
        this.author = author;
        this.description = description;
        this.create = create;
        this.pubdate = pubdate;
        this.pic = pic;
        this.credit = credit;
        this.coins = coins;
        this.duration = duration;
    }

    protected BliDingItem(Parcel in) {
        super(in.readLong(), in.readString(), in.readString());
        aid = in.readInt();
        typeid = in.readInt();
        subtitle = in.readString();
        play = in.readString();
        review = in.readInt();
        video_review = in.readInt();
        favorites = in.readInt();
        mid = in.readInt();
        author = in.readString();
        description = in.readString();
        create =in.readString();
        pubdate = in.readLong();
        pic = in.readString();
        credit = in.readInt();
        coins = in.readInt();
        duration = in.readString();
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(url);
        dest.writeInt(aid);
        dest.writeInt(typeid);
        dest.writeString(subtitle);
        dest.writeString(play);
        dest.writeInt(review);
        dest.writeInt(video_review);
        dest.writeInt(favorites);
        dest.writeInt(mid);
        dest.writeString(author);
        dest.writeString(description);
        dest.writeString(create);
        dest.writeLong(pubdate);
        dest.writeString(pic);
        dest.writeInt(credit);
        dest.writeInt(coins);
        dest.writeString(duration);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
