package IV1351.DB;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class XMLdb {

    /**
     * Generates a schema matching particular DataBase object
     * @param db the DataBase
     * @return XML document schema
     */
    public static Document dbToXMLSchema(DataBase db) {
        //TODO foreign keys and other constraints
        DocumentHelper.createNamespace("xs", "http://www.w3.org/2001/XMLSchema");
        Document XMLRoot = DocumentHelper.createDocument();
        Element schema = XMLRoot.addElement("schema")
                .addNamespace("xs", "http://www.w3.org/2001/XMLSchema");
        //.addAttribute("_xmlns", "http://www.w3.org/2001/XMLSchema");

        //Add all the tables
        db.tables.forEach(t -> {
            String collectionName = t.tableName+"table";
            String collectionTypeName = collectionName +"type";
            String collectionRowType = t.tableName+"type";

            //Add tag for this table
            schema.addElement("element")
                    .addAttribute("name", collectionName)
                    .addAttribute("type", collectionName+"type");
            schema.addElement("complexType")
                    .addAttribute("name", collectionName)
                    .addElement("sequence")
                    .addElement("element")
                    .addAttribute("name", t.tableName)
                    .addAttribute("type", collectionRowType);

            //Specify the row
            Element rowTypeDefElement = schema.addElement("complexType")
                    .addAttribute("name", collectionRowType);

            //TODO: keys and refs
            for(Column c : t.columns) {
                rowTypeDefElement.addElement("Attribute")
                        .addAttribute("name", c.name)
                        .addAttribute("type", c.attrType);
            }
        });
        System.out.println(XMLRoot.asXML());
        return XMLRoot;
    }

    /**
     * Returns the XML format externalization of a particular database
     * @param db the DataBase with data
     * @return  XML implemention
     */
    public static Document dbToXMLImpl(DataBase db) {
        //TODO: implement
        return null;
    }

    private static void createKey(Element e, String keyName, String xPath,String attributeName) {
        //Create a key
        Element key = e.addElement("key")
                //This is the key name e.g. person_personnummer
                .addAttribute("name", keyName);
        key.addElement("selector")
                .addAttribute("xpath", xPath);
        key.addElement("field")
                .addAttribute("xPath", attributeName);
    }

    private static void createRef(Element e, String refName, String keyName, String keyXPath, String keyAttributeName) {
        Element ref = e.addElement("keyref")
                .addAttribute("name",  refName)
                .addAttribute("refer", keyName);
        ref.addElement("selector")
                .addAttribute("xpath", keyXPath);
        ref.addElement("field")
                .addAttribute("xpath", keyAttributeName);
    }
}
