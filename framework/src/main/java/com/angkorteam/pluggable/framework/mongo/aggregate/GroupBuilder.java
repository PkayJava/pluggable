package com.angkorteam.pluggable.framework.mongo.aggregate;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class GroupBuilder {

    protected DBObject aggregates;

    public GroupBuilder() {
        this.aggregates = new BasicDBObject();
    }

    public GroupBuilder(DBObject aggregates) {
        this.aggregates = aggregates;
    }

    public GroupBuilder groupBy(String fieldName) {
        aggregates.put("_id", fieldName);
        return this;
    }

    public GroupBuilder count(String labelName) {
        return group("sum", labelName, 1);
    }

    private GroupBuilder group(String operation, String labelName,
            Object fieldName) {
        if (fieldName instanceof Number) {
            aggregates.put(labelName, new BasicDBObject("$" + operation,
                    fieldName));
        } else {
            aggregates.put(labelName, new BasicDBObject("$" + operation, "$"
                    + fieldName));
        }
        return this;
    }

    public GroupBuilder addToSet(String labelName, String fieldName) {
        return group("addToSet", labelName, fieldName);
    }

    public Sort first(String labelName, String fieldName) {
        GroupBuilder first = group("first", labelName, fieldName);
        if (first instanceof Sort) {
            Sort sort = (Sort) first;
            sort.labelName = labelName;
            return sort;
        } else {
            return new Sort(first.aggregates, labelName);
        }
    }

    public Sort last(String labelName, String fieldName) {
        GroupBuilder last = group("last", labelName, fieldName);
        if (last instanceof Sort) {
            Sort sort = (Sort) last;
            sort.labelName = labelName;
            return sort;
        } else {
            return new Sort(last.aggregates, labelName);
        }
    }

    public GroupBuilder max(String labelName, String fieldName) {
        return group("max", labelName, fieldName);
    }

    public GroupBuilder min(String labelName, String fieldName) {
        return group("min", labelName, fieldName);
    }

    public GroupBuilder average(String labelName, String fieldName) {
        return group("avg", labelName, fieldName);
    }

    public GroupBuilder push(String labelName, String fieldName) {
        return group("push", labelName, fieldName);
    }

    public GroupBuilder sum(String labelName, String fieldName) {
        return group("sum", labelName, fieldName);
    }

    public DBObject build() {
        DBObject object = new BasicDBObject();
        object.put("$group", aggregates);
        return object;
    }

    public static class Sort extends GroupBuilder {

        private String labelName;

        public Sort(String labelName) {
            this.labelName = labelName;
        }

        public Sort(DBObject aggregates, String labelName) {
            this.aggregates = aggregates;
            this.labelName = labelName;
        }

        public GroupBuilder sort(String orderBy, boolean asc) {
            DBObject sort = (DBObject) this.aggregates.get(labelName);
            if (asc) {
                sort.put("$sort", new BasicDBObject(orderBy, 1));
            } else {
                sort.put("$sort", new BasicDBObject(orderBy, -1));
            }
            return this;
        }
    }
}
