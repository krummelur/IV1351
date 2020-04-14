package fredriksonsound.sql.DB;

import java.util.ArrayList;

public class BostadBast {
    /**
     * All the tables complete with attributes and PK, FK constraints
     * @return list of tables
     */
    public static ArrayList<Table> tables() {
        //Commented out because wouldn't compile with å,ä,ö...
        /*
        Table brf = new Table("Bostadsrättsförening")
        .addColumn(new Column("organisationsnummer", true, true, "integer"))
        .addColumn(new Column("namn", true, false, "varchar(128)"))
        .addColumn(new Column("multiplapplatser", false, false, "boolean"));

        Table bostadsrättsgupp = new Table("Bostadsrättsgrupp")
        .addColumn(new Column("bostadsrättsgruppsid", true, true, "integer").setAutoNumber())
        .addColumn(new Column("namn", "varchar(128)"))
        .addColumn(new Column("bostadsrättsförening", false, false, new ForeignKey(brf, brf.getColumn("organisationsnummer")), "integer"));

        Table hus = new Table("Hus")
        .addColumn(new Column("husid", true, true, null, "integer").setAutoNumber())
        .addColumn(new Column("bostadsrättsförening", false, false,
                new ForeignKey(brf, brf.getColumn("organisationsnummer")), "integer"))
        .addColumn(new Column("gatuadress", "varchar(128)"))
        .addColumn(new Column("postadress", "varchar(128)"))
        .addColumn(new Column("byggår", false, false, null, "integer"));

        Table cykelRum = new Table("Cykelrum")
        .addColumn(new Column("cykelrumsid", true, true, null, "integer").setAutoNumber())
        .addColumn(new Column("husid", false, false,
                new ForeignKey(hus, hus.getColumn("husid")), "integer"))
        .addColumn(new Column("beteckning", false, false ,null,  false,"varchar(60)"))
        .addColumn(new Column("yta", "integer"));

        Table tvättstuga = new Table("Tvättstuga")
                .addColumn(new Column("tvättstugeid", true, true, null, "integer").setAutoNumber())
                .addColumn(new Column("husid", false, false,
        new ForeignKey(hus, hus.getColumn("husid")), "integer"))
        .addColumn(new Column("beteckning", false, false ,null,  false,"varchar(60)"))
        .addColumn(new Column("yta", "integer"));

        Table maskin = new Table("Maskin")
        .addColumn(new Column("maskintyp", false, true, "varchar(128)"))
        .addColumn(new Column("tvättstugeid", false, true,
                new ForeignKey(tvättstuga, tvättstuga.getColumn("tvättstugeid")), "integer"))
        .addColumn(new Column("antal", false, false, null, "integer"));

        Table hiss = new Table("Hiss")
        .addColumn(new Column("hissid", true, true, null, "integer").setAutoNumber())
        .addColumn(new Column("beteckning", false, false, null, false, "varchar(25)"))
        .addColumn(new Column("maxvikt", "integer"))
        .addColumn(new Column("maxantalpersoner", false, false, null, "integer"))
        .addColumn(new Column("husid", false, false,
                new ForeignKey(hus, hus.getColumn("husid")), "integer"));

        Table hissBestiktningsFöretag = new Table("hissbesiktningsföretag")
        .addColumn(new Column("organisationsnummer", true, true, null, "integer"))
        .addColumn(new Column("namn", true, false, null, "varchar(128)"));

        Table besiktning = new Table("Besiktning")
        .addColumn(new Column("hissid", false, true, new ForeignKey(hiss, hiss.getColumn("hissid")), "integer"))
        .addColumn(new Column("datum", false, true, "date"))
        .addColumn(new Column("hissbesiktningsföretag", false, false,
                new ForeignKey(hissBestiktningsFöretag, hissBestiktningsFöretag.getColumn("organisationsnummer")), "integer"));

        Table bostadsrätt = new Table("Bostadsrätt")
        .addColumn(new Column("bostadsrättsid", true, true, null, "integer").setAutoNumber())
        .addColumn(new Column("husid", false, false,
                new ForeignKey(hus, hus.getColumn("husid")), "integer"))
        .addColumn(new Column("lägenhetsnummer", false, false, null, "integer"))
        .addColumn(new Column("yta", "integer"))
        .addColumn(new Column("typ", "varchar(20)"))
        .addColumn(new Column("våning","integer"))
        .addColumn(new Column("bostadsrättsgurppsid", false, false,
                new ForeignKey(bostadsrättsgupp, bostadsrättsgupp.getColumn("bostadsrättsgruppsid")), "integer"));

        Table årsavgift = new Table("Årsavgift")
                .addColumn(new Column("verksamhetsår", false, true, null, "integer"))
                .addColumn(new Column("bostadsrättsgruppsid", false, true, new
                        ForeignKey(bostadsrättsgupp, bostadsrättsgupp.getColumn("bostadsrättsgruppsid")), "integer"))
                .addColumn(new Column("belopp", false, false, null, "integer"));

        Table person = new Table("Person")
                .addColumn(new Column("namn", false, false, null, "varchar(128)"))
                .addColumn(new Column("personnummer", true, true, null, "BIGINT"));

        Table ägandeskap = new Table("Ägandeskap")
                .addColumn(new Column("köpdatum", false, true, null, "date"))
                .addColumn(new Column("säljdatum", false, false, null,false,"date"))
                .addColumn(new Column("procentandel", false, false, null, "float"))
                .addColumn(new Column("bostadsrättsid", false, true,
                        new ForeignKey(bostadsrätt, bostadsrätt.getColumn("bostadsrättsid")), "integer"))
                .addColumn(new Column("person", false, true,
                        new ForeignKey(person, person.getColumn("personnummer")), "BIGINT"));

        Table parkeringsköplats = new Table("Parkeringsköplats")
                .addColumn(new Column("bostadsrättsid", false, true,
                        new ForeignKey(bostadsrätt, bostadsrätt.getColumn("bostadsrättsid")), "integer"))
                .addColumn(new Column("tidsstämpel", false, false, null, "integer"));

        Table parkeringsplatstyp = new Table("Parkeringsplatstyp")
                .addColumn(new Column("bostadsrättsförening", false, false,
                        new ForeignKey(brf, brf.getColumn("organisationsnummer")), "integer"))
                .addColumn(new Column("typid", true, true, null, "integer").setAutoNumber())
                .addColumn(new Column("beskrivning", false, false, null, "varchar(50)"));

        Table köplatsrad = new Table("Köplatsrad")
                .addColumn(new Column("typid", false, true,
                        new ForeignKey(parkeringsplatstyp, parkeringsplatstyp.getColumn("typid")), "integer"))
                .addColumn(new Column("bostadsrättsid", false, true,
                        new ForeignKey(bostadsrätt, bostadsrätt.getColumn("bostadsrättsid")), "integer"));

        Table parkeringsårsavgift = new Table("Parkeringsårsavgift")
                .addColumn(new Column("startår", false, true, "integer"))
                .addColumn(new Column("typid", false, true,
                        new ForeignKey(parkeringsplatstyp, parkeringsplatstyp.getColumn("typid")), "integer"))
                .addColumn(new Column("pris", false, false, "integer"));

        Table parkeringsplats = new Table("Parkeringsplats")
                .addColumn(new Column("nummer", false, true, "integer"))
                .addColumn(new Column("typid", false, true,
                        new ForeignKey(parkeringsplatstyp, parkeringsplatstyp.getColumn("typid")), "integer"))
                .addColumn(new Column("gpskoordinat", true, false, "integer"))
                .addColumn(new Column("bostadsrättsid", true, false,
                        new ForeignKey(bostadsrätt, bostadsrätt.getColumn("bostadsrättsid")), false,"integer"));

        ArrayList<Table> allTables = new ArrayList<>(Arrays.asList(brf, bostadsrättsgupp, hus, cykelRum, tvättstuga, maskin, hiss, hissBestiktningsFöretag,
                besiktning, bostadsrätt, årsavgift, person, ägandeskap, parkeringsköplats, parkeringsplatstyp, köplatsrad,
                parkeringsårsavgift, parkeringsplats));
        allTables.forEach(t -> t.populateFromCSV());
        return allTables;
        */
        return null;
    }
}
