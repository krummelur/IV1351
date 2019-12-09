package IV1351;

import IV1351.DB.BostadBäst;
import IV1351.DB.DataBase;

public class main {
    public static void main(String[] args) {
        DataBase db = new DataBase("Bostadbäst");
        BostadBäst.tables().forEach(db::addTable);
        //db.toXMLSchema();
        System.out.println(db.toSQL());
    }
}
