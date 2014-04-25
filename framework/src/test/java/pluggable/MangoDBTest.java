//package pluggable;
//
//import java.net.UnknownHostException;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.jongo.Jongo;
//import org.jongo.MongoCollection;
//import org.jongo.ResultHandler;
//
//import com.angkorteam.pluggable.framework.mongo.aggregate.GroupBuilder;
//import com.angkorteam.pluggable.framework.mongo.aggregate.SortBuilder;
//import com.mongodb.BasicDBObject;
//import com.mongodb.DB;
//import com.mongodb.DBCollection;
//import com.mongodb.DBObject;
//import com.mongodb.DBRef;
//import com.mongodb.LazyDBObject;
//import com.mongodb.MongoClient;
//
//public class MangoDBTest {
//    public static void main(String[] args) throws UnknownHostException {
//        MongoClient mongoClient = new MongoClient("localhost", 27017);
//        DB db = mongoClient.getDB("dating");
//
//        DBCollection role_group_collection = db.getCollection("tbl_role_group");
//        DBCollection group_collection = db.getCollection("tbl_group");
//        DBCollection role_collection = db.getCollection("tbl_role");
//
//        // System.out.println(db.doEval("db.tbl_group.find()").toString());
//        Jongo jongo = new Jongo(db);
//
//        // for (String ite : jongo.getCollection("tbl_role").distinct("name")
//        // .as(String.class)) {
//        // System.out.println(ite.toString());
//        // }
//        //
//        // for (Map ite : jongo.getCollection("tbl_role").find()
//        // .projection("{'name':1,'_id':1}").as(Map.class)) {
//        //
//        // }
//
//        DBObject group = new SortBuilder().sort("ref_role_id", true).build();
////        System.out.println(group.toString());
//        for (DBObject object : role_group_collection.aggregate(
//                Arrays.<DBObject> asList(group)).results()) {
//             System.out.println(object.toString());
//        }
//
//        // MongoCollection s = jongo.getCollection("tbl_role_group");
//        // for (DBObject ite : s.find()
//        // .projection("{'_id':0,'ref_role_id':1,'ref_group_id':1}")
//        // .as(BasicDBObject.class)) {
//        // // LazyDBObject ob = (LazyDBObject) (ite.get("ref_group_id"));
//        // // DBRef dbRef = new DBRef(db, ob);
//        // // System.out.println(dbRef.fetch().toString());
//        // // System.out.println(ob.toString());
//        // System.out.println(ite.get("ref_group_id").toString());
//        // }
//        //
//        // for (DBObject ite : s.aggregate(
//        // "{ $group: {_id : '$ref_group_id', count: {$sum :1}} }").as(
//        // BasicDBObject.class)) {
//        // System.out.println(ite.toString());
//        // }
//        // jongo.runCommand("db.tbl_group.find()");
//    }
//}
