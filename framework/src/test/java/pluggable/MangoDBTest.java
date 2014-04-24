package pluggable;

import java.net.UnknownHostException;
import java.util.Date;

import org.joda.time.DateTime;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class MangoDBTest {
    public static void main(String[] args) throws UnknownHostException {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        for (String db : mongoClient.getDatabaseNames()) {
            System.out.println(db);
        }
        {
            DB db = mongoClient.getDB("pkayjava");
            for (String collection : db.getCollectionNames()) {
                System.out.println(collection);
            }
            DBCollection users = db.getCollection("tbl_user");
            BasicDBObject doc = new BasicDBObject("name", "MongoDB")
                    .append("type", "database")
                    .append("count", 1)
                    .append("date", new Date())
                    .append("info",
                            new BasicDBObject("x", 203).append("y", 102));
            users.insert(doc);
            db.dropDatabase();
        }
        {
            for (String db : mongoClient.getDatabaseNames()) {
                System.out.println(db);
            }
            DB db = mongoClient.getDB("pkayjava");
            for (String collection : db.getCollectionNames()) {
                System.out.println(collection);
            }
        }
        // {
        // DB db = mongoClient.getDB("pkayjava");
        // DBCollection users = db.getCollection("tbl_user");
        // BasicDBObject basicDBObject = new BasicDBObject("name", 1);
        //
        // DBObject o = new BasicDBObject();
        // o.put("dropDups", true);
        // o.put("unique", true);
        // users.createIndex(basicDBObject, o);
        //
        // DBCursor cursor = users.find();
        // while (cursor.hasNext()) {
        // DBObject object = cursor.next();
        // if (object.containsField("date")) {
        // Date date = (Date) object.get("date");
        // System.out.println(date);
        // }
        // System.out.println(object);
        // }
        // cursor.close();
        //
        // }
    }
}
