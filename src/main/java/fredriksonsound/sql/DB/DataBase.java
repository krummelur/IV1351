package fredriksonsound.sql.DB;

//import org.dom4j.Document;

import java.util.ArrayList;



public class DataBase {
    String name;
    ArrayList<Table> tables = new ArrayList<>();

    public DataBase(String name) { this.name = name; }

    public void addTable(Table t) { this.tables.add(t); }

    /**
     * Creates an XML version of the database including all data
     * @return a String representation of an XML document for use in e.g. Basex
     */
    /*
    public Document toXMLImpl() {
        return XMLdb.dbToXMLImpl(this);
    }
    */

    /**
     * Creates an XML version of the schema matching the database
     * @return an XML schema matching the db.
     */
    /*
    public Document toXMLSchema() {
        return XMLdb.dbToXMLSchema(this);
    }
    */
    /**
     * Generates the SQL statements necessary to recreate the database.
     * @return the String representation of the SQL for this database
     */
    public String toSQLCreation() {
        StringBuilder sb = new StringBuilder();
        sb.append("DROP DATABASE IF EXISTS `"+ this.name + "`;\n");
        sb.append("CREATE SCHEMA `"+ this.name +"` DEFAULT CHARACTER SET latin1;\n");
        sb.append("USE "+ this.name +";\n");
        for(Table t :  this.tables)
            sb.append(t.getCreationStatement());
        //TODO: implement maybe
        return sb.toString();
    }

    /**
     * Generates the SQL statements necessary to populate the database
     * The data for each table must exist as csv in a file named [TABLE_NAME]_ok.dat
     * @return String representation of database data as sql statements.
     */
    public String toSQLPopulation() {
        StringBuilder sb = new StringBuilder();
        for(Table t : this.tables)
            sb.append(t.getPopulationStatement() + "\n");
        return sb.toString();
    }
}
