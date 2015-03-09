package prod;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author C0648301
 */
@Path("products")
public class products {

    @GET
    @Produces("application/json")
    public String doGET(@QueryParam("PRODUCT_ID") String id) {
        if (id == null) {
            return (getResults("SELECT * FROM PRODUCT"));
        } else {
            return (getResults("SELECT * FROM PRODUCT WHERE PRODUCT_ID = ?", id));
        }
    }

    private String getResults(String query, String... params) {
        JSONArray jArray = new JSONArray();
        StringBuilder sb = new StringBuilder();
        try (Connection cn = credentials.getConnection()) {
            PreparedStatement pstmt = cn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                JSONObject json = new JSONObject();
                json.put("PRODUCT_ID", rs.getInt("PRODUCT_ID"));
                json.put("PRODUCT_NAME", rs.getString("PRODUCT_NAME"));
                json.put("PRODUCT_DESCRIPTION", rs.getString("PRODUCT_DESCRIPTION"));
                json.put("QUANTITY", rs.getInt("QUANTITY"));
                jArray.add(json);
            }

        } catch (SQLException ex) {
            Logger.getLogger(products.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jArray.toJSONString();
    }

    private int doUpdate(String query, String... params) {
        int changes = 0;
        try (Connection cn = credentials.getConnection()) {
            PreparedStatement pstmt = cn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            changes = pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(products.class.getName()).log(Level.SEVERE, null, ex);
        }
        return changes;
    }

    @POST
    @Consumes("application/json")
    public void post(String str) {
        JsonObject json = Json.createReader(new StringReader(str)).readObject();
        int newid = json.getInt("PRODUCT_ID");
        String id = String.valueOf(newid);
        String name = json.getString("PRODUCT_NAME");
        String description = json.getString("PRODUCT_DESCRIPTION");
        int newqty = json.getInt("QUANTITY");
        String qty = String.valueOf(newqty);
        System.out.println(id + name + description + qty);
        doUpdate("INSERT INTO PRODUCT (PRODUCT_ID, PRODUCT_NAME, PRODUCT_DESCRIPTION, QUANTITY) VALUES (?, ?, ?, ?)", id, name, description, qty);
    }

    @PUT
    @Consumes("application/json")
    public void put(String str) {
        JsonObject json = Json.createReader(new StringReader(str)).readObject();
        int newid = json.getInt("PRODUCT_ID");
        String id = String.valueOf(newid);
        String name = json.getString("PRODUCT_NAME");
        String description = json.getString("PRODUCT_DESCRIPTION");
        int newqty = json.getInt("QUANTITY");
        String qty = String.valueOf(newqty);
        System.out.println(id + name + description + qty);
        doUpdate("UPDATE PRODUCT SET PRODUCT_ID= ?, PRODUCT_NAME = ?, PRODUCT_DESCRIPTION = ?, QUANTITY = ? WHERE PRODUCT_ID = ?", id, name, description, qty, id);
    }

    @DELETE
    @Consumes("application/json")
    public void doDelete(@PathParam("id") String id) {
        doUpdate("DELETE FROM PRODUCT WHERE PRODUCT_ID=" + id);
    }
}
