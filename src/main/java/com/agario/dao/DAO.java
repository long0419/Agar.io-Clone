package com.agario.dao;

import com.mysql.jdbc.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class sets and gets all-time leaderboard scores.
 * @author jaskaran
 */
public class DAO {

//   private static final String MYSQL_HOST = System.getenv("MYSQL_HOST");
//   private static final String MYSQL_PORT = System.getenv("MYSQL_PORT");
//   private static final String MYSQL_USER = System.getenv("MYSQL_USER");
//   private static final String MYSQL_PASS = System.getenv("MYSQL_PASS");
   
   private static final String MYSQL_HOST = "172.29.231.80";
   private static final String MYSQL_PORT = "3306";
   private static final String MYSQL_USER = "root";
   private static final String MYSQL_PASS = "newpwd";

    /**
     * Updates database with player score to form an all-time leaderboard.
     * Requires improvement so it doesn't have to store every player's data.
     * @param alias The player's alias
     * @param score The player's score
     */
    public static void addScore(String alias, int score) {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            // Edit with actual mysql username and password
            Connection con = (Connection) DriverManager.getConnection("jdbc:mysql://" + MYSQL_HOST + ":" + MYSQL_PORT, MYSQL_USER, MYSQL_PASS);
            DatabaseMetaData dbmd = con.getMetaData();

            ResultSet cat = dbmd.getCatalogs();
            boolean dbfound = false;
            while(cat.next())
                if(cat.getString(1).equals("jagario")) {
                    dbfound = true;
                    break;
                }
            if(!dbfound)
                con.createStatement().executeUpdate("CREATE DATABASE jagario");
            con.setCatalog("jagario");

            ResultSet tbls = dbmd.getTables("jagario", null, "scores", new String[] {"TABLE"});
            boolean tblFound = false;
            while(tbls.next())
                if(tbls.getString(3).equals("scores")) {
                    tblFound = true;
                    break;
                }
            if(!tblFound)
                con.createStatement().executeUpdate("CREATE TABLE scores (alias VARCHAR(16), score INT(6))");

            PreparedStatement ps = con.prepareStatement("INSERT INTO scores VALUES (?,?)");
            ps.setString(1, alias);
            ps.setInt(2, score);
            ps.executeUpdate();
            con.close();
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println("Failed connecting to MySQL as: " + MYSQL_HOST + ":" + MYSQL_PORT + ", " + MYSQL_USER + ", " + MYSQL_PASS);
            ex.printStackTrace();
        }
    }

    /**
     * Gets the all time leaderboard for top 10 players and scores.
     * @return String containing player aliases and scores.
     * The player alias and scores are delimited by "^".
     * Different players are delimited by "^^".
     */
    public static String getScores() {
        String output = "";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = (Connection) DriverManager.getConnection("jdbc:mysql://" + MYSQL_HOST + ":" + MYSQL_PORT + "/jagario", MYSQL_USER, MYSQL_PASS);
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM scores ORDER BY score DESC LIMIT 10");

            while(rs.next()) {
                output += rs.getString(1) + "^" + rs.getString(2) + "^^";
            }
        } catch (ClassNotFoundException | SQLException ex) {
            return "";
        }

        return output.substring(0, output.length() - 1);
    }

}
