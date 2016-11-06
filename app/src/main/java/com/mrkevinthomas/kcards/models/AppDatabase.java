package com.mrkevinthomas.kcards.models;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION)
public class AppDatabase {

    public static final String NAME = "AppDatabase"; // dbflow will add the .db extension

    public static final int VERSION = 2;

    @Migration(version = 2, database = AppDatabase.class)
    public static class Migration2 extends AlterTableMigration<Deck> {

        public Migration2(Class<Deck> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.INTEGER, "createdTimeMs");
            addColumn(SQLiteType.INTEGER, "updatedTimeMs");
        }
    }

}