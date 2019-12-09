package IV1351.DB;

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
    final String attrType;
    final String name;
    final boolean unique;
    final boolean pk;
    final boolean notNull;
    final ForeignKey fk;

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
        throw new RuntimeException("No such column in table");
    }
    String getCreationStatement() {
        StringBuilder pkc = new StringBuilder()
        .append(" PRIMARY KEY(");

        StringBuilder sb = new StringBuilder()
        .append("create table ")
        .append("`" + tableName + "` (");
        for(Column c: this.columns){
        sb.append(c.name + " " + c.attrType + " ");
        if(c.pk)
            pkc.append(" " + c.name + ",");

            if (c.notNull)
                sb.append("NOT NULL ");
            if(c.unique)
                sb.append("UNIQUE ");
            if (c.fk != null)
                sb.append("REFERENCES " + c.fk.fromTable.tableName + "(" + c.fk.fromKey.name + ")");
            sb.append(",");
        }

        //if(sb.lastIndexOf(",") != -1)
        //    sb.replace(sb.length()-1, sb.length(), "");

        if(pkc.lastIndexOf(",") != -1)
            pkc.replace(pkc.length()-1, pkc.length(), "");

        pkc.append(")");
        sb.append(pkc.toString());
        sb.append(");\n");
        return sb.toString();
    }
}