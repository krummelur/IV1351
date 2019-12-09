package IV1351.DB;

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
     * Generates the SQL querys necessary to recreate the database, including all data
     * @return the String representation of the SQL for this database
     */
    public String toSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE SCHEMA `"+ this.name +"` DEFAULT CHARACTER SET big5;\n");
        sb.append("USE "+ this.name +";\n");
        for(Table t :  this.tables)
            sb.append(t.getCreationStatement());
        //TODO: implement maybe
        return sb.toString();
    }
}
