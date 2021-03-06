  
package service;

import Entity.Member;
import dbaccess.CommerceDB;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("commerce")
public class ECommerceFacadeREST {

    @Context
    private UriInfo context;

    public ECommerceFacadeREST() {
    }

    @GET
    @Produces("application/json")
    public String getJson() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of ECommerce
     *
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }

    @POST
    @Path("createEcommerceTransactionRecord")
    @Consumes({"application/json"})
    public Response createEcommerceTransactionRecord(@QueryParam("memberID") Long memberID, @QueryParam("amountPaid") double amountPaid,
            @QueryParam("currency") String currency) {

        System.out.println("createEcommerceTransactionRecord called");
        //Prepare data to insert
        //For create date
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String currentDate = dateFormat.format(date);

        //Insert the record to db
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/islandfurniture-it07?user=root&password=12345");
            //syntax tried out in mysql, which successful
            //INSERT INTO salesrecordentity (AMOUNTDUE, AMOUNTPAID, AMOUNTPAIDUSINGPOINTS,CREATEDDATE,CURRENCY,LOYALTYPOINTSDEDUCTED,POSNAME,SERVEDBYSTAFF,MEMBER_ID,STORE_ID) VALUES (12,12, 0,'2020-01-11 12:08:01','SGD',0,' Counter 1','Cashier 1',23,59)
            String stmt = "INSERT INTO salesrecordentity (AMOUNTDUE, AMOUNTPAID, AMOUNTPAIDUSINGPOINTS,CREATEDDATE,CURRENCY,LOYALTYPOINTSDEDUCTED,POSNAME,SERVEDBYSTAFF,MEMBER_ID,STORE_ID) VALUES (?,?,0,?,?,0,' Counter 1','Cashier 1',?,59)";

            System.out.println(amountPaid + "," + currentDate + "," + currency + "," + memberID);
            PreparedStatement ps = conn.prepareStatement(stmt, Statement.RETURN_GENERATED_KEYS);
            ps.setDouble(1, amountPaid);
            ps.setDouble(2, amountPaid);
            ps.setString(3, currentDate);
            ps.setString(4, currency);
            ps.setLong(5, memberID);

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                System.out.println("Result Set: " + rs.toString());
                int generatedKey = rs.getInt(1);
                String id = String.valueOf(generatedKey);
                System.out.println("inserted record id: " + id);
                conn.close();
                return Response.status(201).header("getSOnumber", id).build();
            } else {
                conn.close();
                return Response.status(404).build();
            }

        } catch (Exception ex) {
            System.out.println("ERROR:" + ex.getMessage());
            return Response.status(404).build();
        }
    }

    @POST
    @Path("createEcommerceLineItemRecord")
    @Consumes({"application/json"})
    public Response createEcommerceLineItemRecord(@QueryParam("itemEntityID") int itemID, @QueryParam("quantity") int quantity, @QueryParam("salesRecordID") Long salesId) {
        boolean result = CommerceDB.createECommerceLineItemRecord(itemID,quantity,salesId);
        if(result == false){
            System.out.println("Im hereeee");
            return Response.status(Response.Status.NOT_FOUND).build();
        }else{
            return Response.status(201).build();
        }
    }     
}