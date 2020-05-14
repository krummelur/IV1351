package fredriksonsound.sql.DB;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

class ForeignKey {
    final Table fromTable;
    final Column fromKey;
    public ForeignKey(Table table, Column fromKey) {
        this.fromTable = table;
        this.fromKey = fromKey;
    }
}

class Column {
    boolean isAutoNumber = false;
    int autonumberIndex = 1;
    final String attrType;
    final String name;
    final boolean unique;
    final boolean pk;
    final boolean notNull;
    final ForeignKey fk;

    public Column setAutoNumber() {
        this.isAutoNumber = true;
        return this;
    }

    public Column(String name, String attrType) {
        this(name, false, false, null, attrType);
    }
    public Column(String name, boolean unique, String attrType) {
        this(name, unique, false, null, attrType);
    }
    public Column(String name,boolean unique, boolean pk, String attrType) {
        this(name, unique, pk, null, attrType);
    }
    public Column(String name, boolean unique, boolean pk, ForeignKey fk, String attrType) {
        this(name, unique, pk, fk, true, attrType);
    }
    public Column(String name, boolean unique, boolean pk, ForeignKey fk, boolean notNull, String attrType) {
        if(attrType.equals("string"))
            attrType = "varchar(128)";
        if(fk != null && !fk.fromKey.attrType.toLowerCase().equals(attrType.toLowerCase()))
            throw new RuntimeException("Foreign key type must match");

        this.name = name;
        this.unique = unique;
        this.pk = pk;
        this.fk = fk;
        this.notNull = notNull;
        this.attrType = attrType;
    }
}

public class Table {
    ArrayList<Column> primaryKey = new ArrayList<>();
    String tableName;
    ArrayList<Column> columns = new ArrayList<>();
    ArrayList<String[]> rows = new ArrayList<>();

    public Table(String t) { this.tableName = t; }

    public Table(String t, ArrayList<Column> c) { this(t); this.columns = c; }

    public void populateFromCSV() {
        this.populateFromCSV(this.tableName.toLowerCase() + "_data_ok.dat");
    }

    public void populateFromCSV(String filePath) {
        String fileContents = null;
        try {
            Path path = Paths.get(new String(filePath.getBytes("UTF-8")));
            fileContents = new String (Files.readAllBytes(path));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        String[] lines = fileContents.split("\n");
        String[] mapper = lines[0].split(",");
        for(int i = 1; i < lines.length; i++) {
            HashMap<String, String> insertMap = new HashMap<>();
            String[] currentVals = lines[i].split(",");
            for(int j = 0; j < currentVals.length; j++) {
                String insertVal =  currentVals[j];
                if(insertVal.trim().equals(""))
                    insertVal = "null";
                insertMap.put(mapper[j].trim(), insertVal.trim());
            }
            this.insert(insertMap);
        }
    }

    public void insert(String ... values) {
        String[] newRow = new String[columns.size()];
        for(int i = 0; i < columns.size(); i++) {
            String v = values[i];
            if(v == null && (columns.get(i).notNull || columns.get(i).pk))
                throw new RuntimeException("PK column may never be null, NOT NULL may never be null");
            newRow[i] = v;
        }
        rows.add(newRow);
    }

    public void insert(HashMap<String,String> values) {
        String[] newRow = new String[columns.size()];
        values.forEach((col,val) -> {
            if(val == null && (getColumn(col).pk || getColumn(col).notNull))
                throw new RuntimeException("PK column may never be null, NOT NULL may never be null");
            if(getColumn(col).isAutoNumber)
                val = "" + getColumn(col).autonumberIndex++;

            if(val == null)
                System.out.println("hmm");
            newRow[columns.indexOf(getColumn(col))] = val;
        });
        rows.add(newRow);
        //TODO check table (duplicate rows etc.)
    }

    public void update(HashMap<String, String> newVals, HashMap<String, String> pattern) {
        ArrayList<String[]> ar = matchRows(pattern);
        //Do stuff to the rows
    }

    public void delete(HashMap<String, String> pattern) {
        matchRows(pattern).forEach(r -> rows.remove(r));
    }

    public ArrayList<String[]> select(HashMap<String, String> pattern){
        return matchRows(pattern);
    }

    public Table addColumn(Column c) {
        columns.forEach(col -> {
            if (col.name.toLowerCase().equals(c.name.toLowerCase()))
                throw new RuntimeException("Can not add duplicate col name");
        });
        if(c.pk)
            this.primaryKey.add(c);
        this.columns.add(c);
        return this;
    }

    private ArrayList<String[]> matchRows(HashMap<String, String> pattern) {
        ArrayList<String[]> resultRows = new ArrayList<>();
        rows.forEach(r -> {
            //Nicer with stream reduce but java doesnt give index
            AtomicBoolean b = new AtomicBoolean(true);
            pattern.forEach((col, val) -> {
                if(r[columns.indexOf(getColumn(col))] != val)
                    b.set(false);
            });
            if(b.get())
                resultRows.add(r);
        });
        return resultRows;
    }

    Column getColumn(String colName) {
        for(Column c : columns) {
            if(c.name.toLowerCase().equals(colName.toLowerCase()))
                return c;
        }
        throw new RuntimeException("No such column in table (" + colName +")");
    }

    private void removeTrailingChars(String match, StringBuilder sb) {
        if(sb.lastIndexOf(match) != -1)
            sb.replace(sb.length()-match.length(), sb.length(), "");
    }

    String getPopulationStatement() {
        StringBuilder psb = new StringBuilder()
                .append("INSERT INTO `" + this.tableName +"` (");
        for(Column c : this.columns) {
            if(!c.isAutoNumber)
                psb.append(" `" + c.name + "`,");
        }
        this.removeTrailingChars(",", psb);
        psb.append(")\nVALUES ");

        for(String[] r : this.rows) {
            psb.append("(");
            for(int i = 0; i < r.length; i++) {
                Column insertColumn = this.columns.get(i);
                if(!insertColumn.isAutoNumber) {
                    String value =  r[i].trim();
                    if(value.equals("") || value.equals("null")) {
                        value = "null";
                    }
                    else if(insertColumn.attrType.toLowerCase().contains("varchar") ||
                            insertColumn.attrType.toLowerCase().contains("date"))
                        value = "\'"+value+"\'";
                    psb.append(value+",");
                }
            }
            this.removeTrailingChars(",",psb);
            psb.append("),\n");
        }
        this.removeTrailingChars(",\n",psb);
        psb.append(";");
        return psb.toString();
    }

    String getCreationStatement() {
        StringBuilder fkc = new StringBuilder();
        StringBuilder pkc = new StringBuilder()
        .append(" PRIMARY KEY(");

        StringBuilder sb = new StringBuilder()
        .append("create table ")
        .append("`" + tableName + "` (");
        for(Column c: this.columns){
        sb.append("`"+c.name + "` " + c.attrType + " ");
        if(c.pk)
            pkc.append(" `" + c.name + "`,");

            if (c.notNull)
                sb.append("NOT NULL ");
            if(c.unique)
                sb.append("UNIQUE ");
            if(c.isAutoNumber)
                sb.append("AUTO_INCREMENT ");

            if (c.fk != null) {
                fkc.append("FOREIGN KEY (`" + c.name + "`)")
                        .append("\nREFERENCES " + c.fk.fromTable.tableName + "(`" + c.fk.fromKey.name + "`),\n");
            }
            sb.append(",\n");
        }

        //if(sb.lastIndexOf(",") != -1)
        //    sb.replace(sb.length()-1, sb.length(), "");
        sb.append(fkc.toString());

        if(pkc.lastIndexOf(",") != -1)
            pkc.replace(pkc.length()-1, pkc.length(), "");

        pkc.append(")");
        sb.append(pkc.toString());
        sb.append(");\n");
        return sb.toString();
    }
}