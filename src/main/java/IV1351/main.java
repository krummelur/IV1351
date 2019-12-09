package IV1351;

import IV1351.DB.BostadBast;
import IV1351.DB.DataBase;

public class main {
    public static void main(String[] args) {
        DataBase db = new DataBase("Bostadbäst");
        BostadBast.tables().forEach(db::addTable);
        //db.toXMLSchema();
        System.out.println(db.toSQL());
    }
}
