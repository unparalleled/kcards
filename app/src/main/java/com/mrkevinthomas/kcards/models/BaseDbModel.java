package com.mrkevinthomas.kcards.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.structure.BaseModel;

public class BaseDbModel extends BaseModel {

    @Column
    long createdTimeMs;

    @Column
    long updatedTimeMs;

    @Override
    public void save() {
        if (createdTimeMs == 0) {
            createdTimeMs = System.currentTimeMillis();
        }
        updatedTimeMs = System.currentTimeMillis();
        super.save();
    }

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

}
