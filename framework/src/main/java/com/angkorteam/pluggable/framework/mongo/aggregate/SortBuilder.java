package com.angkorteam.pluggable.framework.mongo.aggregate;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class SortBuilder {

    protected DBObject aggregates;

    public SortBuilder() {
        this.aggregates = new BasicDBObject();
    }

    public SortBuilder(DBObject aggregates) {
        this.aggregates = aggregates;
    }

    public SortBuilder sort(String orderBy, boolean asc) {
        if (asc) {
            aggregates.put(orderBy, 1);
        } else {
            aggregates.put(orderBy, -1);
        }
        return this;
    }

    public DBObject build() {
        DBObject object = new BasicDBObject();
        object.put("$sort", aggregates);
        return object;
    }
}
