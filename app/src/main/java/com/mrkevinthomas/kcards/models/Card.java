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
    long createdTimeMs;

    @Column
    long updatedTimeMs;

    @Column
    String frontText;

    @Column
    String backText;

    @Column
    String frontLanguageCode;

    @Column
    String backLanguageCode;

    @Column
    int correctCount;

    @Column
    int incorrectCount;

    public Card() {
    }

    public Card(long deckId, String frontText, String backText) {
        this.deckId = deckId;
        this.frontText = frontText;
        this.backText = backText;
    }

    @Override
    public void save() {
        if (createdTimeMs == 0) {
            createdTimeMs = System.currentTimeMillis();
        }
        updatedTimeMs = System.currentTimeMillis();
        super.save();
    }

    public void setDeckId(long deckId) {
        this.deckId = deckId;
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

    public Language getFrontLanguage() {
        return Language.fromCode(frontLanguageCode);
    }

    public Language getBackLanguage() {
        return Language.fromCode(backLanguageCode);
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public int getIncorrectCount() {
        return incorrectCount;
    }

    public void incrementCorrect() {
        correctCount++;
        save();
    }

    public void incrementIncorrect() {
        incorrectCount++;
        save();
    }

    public void resetProgress() {
        correctCount = 0;
        incorrectCount = 0;
        save();
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
        dest.writeLong(this.createdTimeMs);
        dest.writeLong(this.updatedTimeMs);
        dest.writeString(this.frontText);
        dest.writeString(this.backText);
        dest.writeString(this.frontLanguageCode);
        dest.writeString(this.backLanguageCode);
        dest.writeInt(this.correctCount);
        dest.writeInt(this.incorrectCount);
    }

    protected Card(Parcel in) {
        this.id = in.readLong();
        this.deckId = in.readLong();
        this.createdTimeMs = in.readLong();
        this.updatedTimeMs = in.readLong();
        this.frontText = in.readString();
        this.backText = in.readString();
        this.frontLanguageCode = in.readString();
        this.backLanguageCode = in.readString();
        this.correctCount = in.readInt();
        this.incorrectCount = in.readInt();
    }

    public static final Creator<Card> CREATOR = new Creator<Card>() {
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
