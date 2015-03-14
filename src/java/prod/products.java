package prod;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
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
        Boolean Result = false;
        JsonArrayBuilder pList = Json.createArrayBuilder();
        StringBuilder sb = new StringBuilder();
        try (Connection cn = credentials.getConnection()) {
            PreparedStatement pstmt = cn.prepareStatement(query);
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
                Result = true;
            }
            ResultSet rs = pstmt.executeQuery();

            if (Result == false) {
                while (rs.next()) {
                    JsonObjectBuilder productBuilder = Json.createObjectBuilder();
                    productBuilder.add("productId", rs.getInt("product_id"))
                            .add("name", rs.getString("product_name"))
                            .add("description", rs.getString("product_description"))
                            .add("quantity", rs.getInt("quantity"));
                    pList.add(productBuilder);
                }
            } else {
                while (rs.next()) {
                    JsonObject jsonObt = Json.createObjectBuilder()
                            .add("productId", rs.getInt("product_id"))
                            .add("name", rs.getString("product_name"))
                            .add("description", rs.getString("product_description"))
                            .add("quantity", rs.getInt("quantity"))
                            .build();
                    return jsonObt.toString();
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(products.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pList.build().toString();
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
