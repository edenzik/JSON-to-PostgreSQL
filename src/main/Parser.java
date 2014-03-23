package main;

import java.io.*;
import java.sql.*;
import java.util.*;
import org.json.*;

/**
 * Author: Eden Zik
 * Intended use: VisiTrend
 * Date: 3/22/2014
 * Version: 1.0
 * JSON to Postgres parser.
 * Reads a JSON file and converts it to an SQL table definition.
 * Converts every subsequent JSON into a SQL insertion command.
 * Connects to a remote Postgres database server to initilize the table and the tuples.
 * Bugs: If a Schema JSON file is not found, a blank one is produced. This is intentional, to make the API more flexible.
 * Connection to server sometimes fails, reason unknown.
 * Things to fix: Type inference for a string, int, or double for JSON attributes.
 * Right now all types are registered in SQL as varchar of length 255. This can be changed by passing th JSON schema the orignal JSONObject file
 * and inferring types from that.
 */


public class Parser {
    static Scanner console = new Scanner(System.in);
    static String tableName;
    static String SQLCommand = "";                                                                  //Keeps track of SQL command

    public static void main(String[] args) throws IOException{

        System.out.println("Glue Utility: JSON -> SQL Parser -> PostgreSQL Server");
        System.out.println("Indicate the location of a JSON file to produce a Schema for an SQL table");

        JSONObject tempJSON = getJSONfromFile(console.nextLine());

        System.out.println("JSON Loaded. Name of Table:");

        tableName = console.nextLine();

        JSONSchema schema = new JSONSchema(tableName,tempJSON.keys());                     //Reads Schema from JSON

        System.out.println("Schema produced:\n" + schema.toString());

        SQLCommand = SQLCommand + "--Schema Setup\n" + schema.toString() + "\n";

        System.out.println("To produce SQL Queries from JSON Files, indicate the location of an additional JSON file (files must have matching attributes)");

        JSONObject tuple;
        String line = console.nextLine();
        while (!line.equalsIgnoreCase("Q")){
            String command = "INSERT INTO TABLE " + schema.getTableName() + "\n" + "VALUES(";
            tuple = getJSONfromFile(line);
            for (String x:schema.getAtt()){
                command = command + "\'" + tuple.get(x) + "\'" + ",";
            }
            command = command.substring(0, command.length()-1) + ")" + "\n";
            SQLCommand = SQLCommand + "--QUERY from " + line + "\n" + command + "\n";
            System.out.println(command);
            System.out.println("Log another file, or type Q to quit.");
            line = console.nextLine();
        }

        System.out.println("Insertion Done.");
        writeToFile(tableName);
        System.out.println("Queries saved in file \"" + tableName + ".sql\"");

        System.out.println("Would you like to input this Query to a Postgres Server? (Y/N)");
        if (console.nextLine().equalsIgnoreCase("Y")){
            System.out.println("What is your database address?");
            String dbdir = console.nextLine();
            System.out.println("What is your database name?");
            String dbname = console.nextLine();
            System.out.println("What is your username?");
            String user = console.nextLine();
            System.out.println("What is your password?");
            String pass = console.nextLine();
            try {
                System.out.println("Issueing Query to Database...");
                connectToPGServer(dbdir, dbname, user, pass);
            } catch (Error e){
                System.out.println("Connection Failed");
            }
        }
        System.out.println("Thank you for using JSON-to_PostgreSQL");

    }

    //Writes the SQL queries to a file
    public static void writeToFile(String fileName){                                             //
        try{
            FileWriter fstream = new FileWriter(fileName + ".sql");
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(SQLCommand);
            out.close();
        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }
    }

    //Reads a file and converts it to JSON
    public static JSONObject getJSONfromFile(String fileName) throws IOException{

        BufferedReader br = null;
        String sTotalLine = "";

        try {
            String sCurrentLine;

            br = new BufferedReader(new FileReader(fileName));

            while ((sCurrentLine = br.readLine()) != null) {
                sTotalLine = sTotalLine + sCurrentLine.trim();
            }

        } catch (IOException e) {
            System.out.println("File failed to load.");
            sTotalLine = "{}";

        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return new JSONObject(sTotalLine);
    }

    //Connects to a PGF server
    public static void connectToPGServer(String dbdir, String dbName, String username, String pass) {
        try {
            System.out.println("Loading the driver...");
            Class.forName("org.postgresql.Driver");
            System.out.println("Connecting...");
            Connection db = DriverManager.getConnection("jdbc:postgresql://" + dbdir + dbName, username, pass);
            System.out.println("Creating statement...");
            Statement statement = db.createStatement();
                    ResultSet rs = statement.executeQuery(SQLCommand);
                    printResult(rs);
        } catch (Exception e) {
            System.out.println("SQLException: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Call this to print a result
    public static void printResult(ResultSet rs) {
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int numberOfColumns = rsmd.getColumnCount();

            int rowCount = 1;

            while (rs.next()) {
                System.out.print("Row " + rowCount + ":  ");
                for (int i = 1; i <= numberOfColumns; i++) {
                    System.out.print(rs.getString(i)+" ");
                }
                System.out.println("");
                rowCount++;
            }
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    }

}