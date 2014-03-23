package main;

import java.util.*;

/**
 * Author: Eden Zik
 * Intended use: VisiTrend
 * Date: 3/22/2014
 * Version: 1.0
 * JSON Schema
 * Reads a series of attributes and converts it to an SQL CREATE TABLE command.
 */

public class JSONSchema {
    private ArrayList<String> att;
    private String tableName;


    //Adds all the attributes from an iterator
    public JSONSchema(String tableName, Iterator<String> iter){
        att = new ArrayList<String>();
        this.tableName = tableName;
        while (iter.hasNext()){
            att.add(iter.next());
        }
    }

    //Overloaded constructor ensures you can insert into a table with no name
    public JSONSchema(Iterator<String> iter){
        this("UNNAMED_TABLE", iter);
    }

    public String toString(){
        String schema = "CREATE TABLE " + tableName + "(" + "\n";
        for (Object x : att){
            schema = schema + x.toString() + " varchar(255),\n";
        }
        return schema.substring(0,schema.length()-2) + "\n);";
    }

    public ArrayList<String> getAtt(){
        return this.att;
    }

    public String getTableName(){
        return tableName;
    }

}
