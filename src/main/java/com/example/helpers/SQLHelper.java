package com.example.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class SQLHelper {

    /***
     *
     * @param select new String[]{"a","b"}
     * @param table "myTable"
     * @return SELECT a,b FROM myTable
     */
    public static String select(String[] select, String table) {
        return "SELECT " + Arrays.stream(select).collect(Collectors.joining(",")) +
                " FROM " + table;
    }

    /***
     *
     * @param select new String[]{"a","b"}
     * @param table "myTable"
     * @param where "a=a1 AND b=b1"
     * @return SELECT a,b FROM myTable WHERE a=a1 AND b=b1
     */
    public static String select(String[] select, String table, String where) {
        return "SELECT " + Arrays.stream(select).collect(Collectors.joining(",")) +
                " FROM " + table +
                " WHERE " + where;
    }

    /***
     *
     * @param selectMax "ID"
     * @param as "maxID"
     * @param table myTable
     * @return SELECT MAX(ID) AS maxID FROM myTable
     */
    public static String selectMax(String selectMax, String as, String table) {
        return "SELECT MAX(" + selectMax + ") AS " + as + " FROM " + table;
    }

    /***
     *
     * @param selectMax "ID"
     * @param as "maxID"
     * @param table myTable
     * @param where "a=b"
     * @return SELECT MAX(ID) AS maxID FROM myTable WHERE a=b
     */
    public static String selectMax(String selectMax, String as, String table, String where) {
        return "SELECT MAX(" + selectMax + ") AS " + as + " FROM " + table + " WHERE " + where;
    }

    /***
     *
     * @param table "myTable"
     * @param names new String[]{"a","b"}
     * @param values new String[]{"a1","b1"}
     * @return INSERT INTO myTable (a,b) VALUES ('a1','b1')
     */
    public static String insert(String table, String[] names, String[] values) {
        return "INSERT INTO " + table +
                Arrays.stream(names).collect(Collectors.joining(",", " (", ")")) +
                " VALUES " + Arrays.stream(values).collect(Collectors.joining("','", "('", "')"));
    }

    /***
     *
     * @param table "myTable"
     * @param set new String[]{"a=b","c=d"}
     * @param where "e=f"
     * @return UPDATE myTable SET a=b,c=d WHERE e=f
     */
    public static String update(String table, String[] set, String where) {
        return "UPDATE " + table +
                " SET " + Arrays.stream(set).collect(Collectors.joining(",")) +
                " WHERE " + where;
    }

    /***
     *
     * @param table "myTable"
     * @param where "a=b"
     * @return DELETE FROM myTable WHERE a=b
     */
    public static String delete(String table, String where) {
        return "DELETE FROM " + table +
                " WHERE " + where;
    }
}

