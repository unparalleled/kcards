package com.mrkevinthomas.kcards.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = AppDatabase.class)
public class Card extends BaseModel implements Parcelable {

    @Column
    @PrimaryKey(autoincrement = true)
    long id;

    @ForeignKey(tableClass = Deck.class)
    long deckId;

    @Column
    String frontText;

    @Column
    String backText;

    public Card() {
    }

    public Card(long deckId, String frontText, String backText) {
        this.deckId = deckId;
        this.frontText = frontText;
        this.backText = backText;
    }

    public String getFrontText() {
        return frontText;
    }

    public String getBackText() {
        return backText;
    }

    public void setFrontText(String frontText) {
        this.frontText = frontText;
    }

    public void setBackText(String backText) {
        this.backText = backText;
    }

    public void setDeckId(long deckId) {
        this.deckId = deckId;
    }

    // equals and hashcode

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Card card = (Card) o;

        return id == card.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }


    // parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.deckId);
        dest.writeString(this.frontText);
        dest.writeString(this.backText);
    }

    protected Card(Parcel in) {
        this.id = in.readLong();
        this.deckId = in.readLong();
        this.frontText = in.readString();
        this.backText = in.readString();
    }

    public static final Parcelable.Creator<Card> CREATOR = new Parcelable.Creator<Card>() {
        @Override
        public Card createFromParcel(Parcel source) {
            return new Card(source);
        }

        @Override
        public Card[] newArray(int size) {
            return new Card[size];
        }
    };

}
