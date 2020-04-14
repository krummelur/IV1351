package fredriksonsound.sql;

import fredriksonsound.sql.DB.DataBase;
import fredriksonsound.sql.DB.IoTProject;

public class main {
    public static void main(String[] args) {
        DataBase db = new DataBase("iot_project_db");
        IoTProject.tables().forEach(db::addTable);
        //db.toXMLSchema();
        System.out.println(db.toSQLCreation());
        //System.out.println(db.toSQLPopulation());
    }
}
