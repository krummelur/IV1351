package IV1351;

import IV1351.DB.BostadBast;
import IV1351.DB.DataBase;
import IV1351.DB.iotProject;

public class main {
    public static void main(String[] args) {
        DataBase db = new DataBase("iot_project_db");
        iotProject.tables().forEach(db::addTable);
        //db.toXMLSchema();
        System.out.println(db.toSQLCreation());
        //System.out.println(db.toSQLPopulation());
    }
}
