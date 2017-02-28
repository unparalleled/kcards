package com.mrkevinthomas.kcards.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.mrkevinthomas.kcards.FirebaseDb;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.OneToMany;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.Comparator;
import java.util.List;

@Table(database = AppDatabase.class)
public class Deck extends BaseDbModel implements Parcelable {

    public static final Comparator<Deck> UPDATED_COMPARATOR = new Comparator<Deck>() {
        @Override
        public int compare(Deck deck1, Deck deck2) {
            return Long.valueOf(deck2.getUpdatedTimeMs()).compareTo(deck1.getUpdatedTimeMs());
        }
    };

    public static final Comparator<Deck> CREATED_COMPARATOR = new Comparator<Deck>() {
        @Override
        public int compare(Deck deck1, Deck deck2) {
            return Long.valueOf(deck2.getCreatedTimeMs()).compareTo(deck1.getCreatedTimeMs());
        }
    };

    public static final Comparator<Deck> NAME_COMPARATOR = new Comparator<Deck>() {
        @Override
        public int compare(Deck deck1, Deck deck2) {
            return ("" + deck1.getName()).compareToIgnoreCase(deck2.getName());
        }
    };

    public static final Comparator<Deck> DESCRIPTION_COMPARATOR = new Comparator<Deck>() {
        @Override
        public int compare(Deck deck1, Deck deck2) {
            return ("" + deck1.getDescription()).compareToIgnoreCase(deck2.getDescription());
        }
    };

    @Column
    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    String firebaseKey;

    @Column
    String name;

    @Column
    String description;

    List<Card> cards;

    /**
     * Gets the cards in the deck.
     * Cards are loaded when the deck is loaded from the db.
     * Cards are deleted when the deck is deleted from the db.
     * Cards are NOT saved when the deck is saved, and must be saved independently for performance reasons.
     *
     * @return All the cards in the deck
     */
    @OneToMany(methods = {OneToMany.Method.LOAD, OneToMany.Method.DELETE}, variableName = "cards")
    public List<Card> getCards() {
        if (cards == null || cards.isEmpty()) {
            cards = SQLite.select()
                    .from(Card.class)
                    .where(Card_Table.deckId_id.eq(id))
                    .queryList();
        }
        return cards;
    }

    public Deck() {
    }

    public Deck(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Exclude
    public long getId() {
        return id;
    }

    @Exclude
    public String getFirebaseKey() {
        return firebaseKey;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setFirebaseKey(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Exclude
    public boolean isSyncedWithFirebase() {
        return firebaseKey != null && !firebaseKey.isEmpty();
    }

    public int size() {
        return cards != null ? cards.size() : 0;
    }

    @Override
    public void save() {
        super.save();
        FirebaseDb.updateDeck(this);
    }

    @Override
    public void delete() {
        super.delete();
        FirebaseDb.removeDeck(this);
    }

    // force firebase to serialize/deserialize these fields
    public long getCreatedTimeMs() {
        return createdTimeMs;
    }

    public long getUpdatedTimeMs() {
        return updatedTimeMs;
    }

    public void setUpdatedTimeMs(long updatedTimeMs) {
        this.updatedTimeMs = updatedTimeMs;
    }

    public void setCreatedTimeMs(long createdTimeMs) {
        this.createdTimeMs = createdTimeMs;
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

        Deck deck = (Deck) o;

        return id == deck.id;
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
        dest.writeLong(this.createdTimeMs);
        dest.writeLong(this.updatedTimeMs);
        dest.writeString(this.firebaseKey);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeTypedList(this.cards);
    }

    protected Deck(Parcel in) {
        this.id = in.readLong();
        this.createdTimeMs = in.readLong();
        this.updatedTimeMs = in.readLong();
        this.firebaseKey = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.cards = in.createTypedArrayList(Card.CREATOR);
    }

    public static final Creator<Deck> CREATOR = new Creator<Deck>() {
        @Override
        public Deck createFromParcel(Parcel source) {
            return new Deck(source);
        }

        @Override
        public Deck[] newArray(int size) {
            return new Deck[size];
        }
    };

}
