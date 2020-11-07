package ca.utoronto.utm.mcs.API;

import ca.utoronto.utm.mcs.Utils;
import com.mongodb.client.MongoClient;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class BlogPostApi implements HttpHandler {

    private final MongoClient db;

    @Inject
    public BlogPostApi(MongoClient db) {
        this.db = db;
    }

    @Override
    public void handle(HttpExchange r) {
        try {
            switch (r.getRequestMethod()) {
                case "PUT":
                    handlePut(r);
                    break;
                case "GET":
                    handleGet(r);
                    break;
                case "DELETE":
                    handleDelete(r);
                    break;
                default:
                    r.sendResponseHeaders(405, -1);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handlePut(HttpExchange r) throws IOException {
        try{
            String title, author, content;
            JSONArray tags;
            Document document = new Document();
            ArrayList tagsArray = new ArrayList();
            JSONObject resJson = new JSONObject();
            String res;

            try{
                String body = Utils.convert(r.getRequestBody());
                JSONObject deserialized = new JSONObject(body);
                title = deserialized.getString("title");
                author = deserialized.getString("author");
                content = deserialized.getString("content");
                tags = deserialized.getJSONArray("tags");
                for (int i = 0; i < tags.length(); i++) {
                    tagsArray.add(tags.getString(i));
                }
            } catch (JSONException e) {
                r.sendResponseHeaders(400, -1);
                return;
            }

            document.put("title", title);
            document.put("author", author);
            document.put("content", content);
            document.put("tags", tagsArray);
            db.getDatabase("csc301a2").getCollection("posts").insertOne(document);

            String id = document.getObjectId("_id").toString();
            resJson.put("_id", id);
            res = resJson.toString();
            r.sendResponseHeaders(200, res.length());
            OutputStream os = r.getResponseBody();
            os.write(res.getBytes());
            os.close();

        } catch (Exception e){
            r.sendResponseHeaders(500, -1);
        }
    }

    public void handleGet(HttpExchange r) throws IOException {
    }

    public void handleDelete(HttpExchange r) throws IOException {
    }
}