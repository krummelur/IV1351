package fredriksonsound.sql.DB;

import java.util.ArrayList;
import java.util.Arrays;

public class IoTProject {
    /**
     * All the tables complete with attributes and PK, FK constraints
     * @return list of tables
     */
    public static ArrayList<Table> tables() {
        Table location = new Table("location")
                .addColumn(new Column("name","varchar(128)"))
                .addColumn(new Column("id", true, true, "integer").setAutoNumber());

        Table tracker = new Table("rfid_tracker")
                .addColumn(new Column("id", true, true, "varchar(64)"))
                .addColumn(new Column("location", false, false,
                        new ForeignKey(location, location.getColumn("id")), false, "integer"));

        Table receiver = new Table("rfid_receiver")
                .addColumn(new Column("id", true, true, "varchar(64)"))
                .addColumn(new Column("location", false, false,
                new ForeignKey(location, location.getColumn("id")), "integer"));

        Table interest = new Table("interest")
                .addColumn(new Column("name", true, "varchar(128)"))
                .addColumn(new Column("id", true, true, "integer").setAutoNumber());

        Table trackerInterest = new Table("tracker_interest")
                .addColumn(new Column("interest", false, true,
                        new ForeignKey(interest, interest.getColumn("id")),"integer"))
                .addColumn(new Column("tracker", false, true,
                        new ForeignKey(tracker, tracker.getColumn("id")),"varchar(64)"))
                .addColumn(new Column("weight", "float"));

        Table display = new Table("display")
                .addColumn(new Column("id", true, true, "integer").setAutoNumber())
                .addColumn(new Column("location", false, true,
                        new ForeignKey(location, location.getColumn("id")), "integer"));

        Table advertisementVideo = new Table("advertisement_video")
                .addColumn(new Column("id", true, true, "integer").setAutoNumber())
                .addColumn(new Column("interest", false, false,
                        new ForeignKey(interest, interest.getColumn("id")), "integer"))
                .addColumn(new Column("length_sec", false, false, "integer"))
                .addColumn(new Column("url", false, false, "varchar(255)"));


        Table agency = new Table("agency")
                .addColumn(new Column("orgnr", true, true, "string"))
                .addColumn(new Column("name", true, false, "string"));

        Table users = new Table("users")
                .addColumn(new Column("username", true, true, "string"))
                .addColumn(new Column("email", true, false, "string"))
                .addColumn(new Column("agency", false, false, new ForeignKey(agency, agency.getColumn("orgnr")), "string"))
                .addColumn(new Column("pass_hash", false, false, "string"));

        Table orders = new Table("orders")
                .addColumn(new Column("id", true, true, "varchar(40)"))
                .addColumn(new Column("credits", false, false, "integer"))
                .addColumn(new Column("user", false, false, new ForeignKey(users, users.getColumn("email")), "string"));

        Table playedAdvert = new Table("played_video")
                .addColumn(new Column("id", true, true, "integer").setAutoNumber())
                .addColumn(new Column("video", false, false,
                        new ForeignKey(advertisementVideo, advertisementVideo.getColumn("id")), "integer"))
                .addColumn(new Column("time_epoch", false, false, "integer"))
                .addColumn(new Column("order", false, false,
                        new ForeignKey(orders, orders.getColumn("id")), "varchar(40)"));


        Table advertisementOrder = new Table("advertisement_order")
                .addColumn(new Column("video", false, true,
                        new ForeignKey(advertisementVideo, advertisementVideo.getColumn("id")), "integer"))
                .addColumn(new Column("orders", false, true, new ForeignKey(orders, orders.getColumn("id")), "varchar(40)"))
                .addColumn(new Column("start_time_epoch", false, false, null, "integer"))
                .addColumn(new Column("end_time_epoch", false, false, null, "integer"));

        Table refresh_token = new Table("refresh_token").addColumn(new Column("token", true, true, null, "BLOB"))
                .addColumn( new Column("id", false, false, "varchar(40)"));

        ArrayList<Table> allTables = new ArrayList<>(Arrays.asList(location, tracker, receiver, interest, trackerInterest, display,
                advertisementVideo, agency, users, orders, playedAdvert, advertisementOrder, refresh_token));
        return allTables;
    }
}
