package com.yferhaoui.pinger;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "servers.db";
    public static final String SERVER_TABLE_NAME = "server";

    public static final Long msPerDay = 86000000L;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS server " +
                "(ip TEXT PRIMARY KEY, " +
                "name TEXT UNIQUE NOT NULL, " +
                "ipMac TEXT," +
                "externalPort INTEGER, " +
                "internalPort INTEGER, " +
                "isPublic INTEGER NOT NULL CHECK (isPublic IN (0,1)), " +
                "expiryDate INTEGER NOT NULL)"
        );
        db.execSQL("INSERT INTO server(ip, name, isPublic, expiryDate) VALUES ('google.fr', 'google', 1, " + (System.currentTimeMillis() + msPerDay*100) + ")");
        db.execSQL("INSERT INTO server(ip, name, isPublic, expiryDate) VALUES ('youtube.fr', 'youtube', 1, " + (System.currentTimeMillis() + msPerDay*100) + ")");
        db.execSQL("INSERT INTO server(ip, name, isPublic, expiryDate) VALUES ('amazon.fr', 'amazon', 1, " + (System.currentTimeMillis() + msPerDay*100) + ")");
        db.execSQL("INSERT INTO server(ip, name, isPublic, expiryDate) VALUES ('mauvaiseIp.fr', 'ChezYani', 0, " + (System.currentTimeMillis() + msPerDay*100) + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS server");
        onCreate(db);
    }

    public boolean insertServer (String ip, String name, String ipMac, String externalPort, String internalPort, Boolean isPublic, Long expiryDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ip", ip);
        contentValues.put("name", name);
        contentValues.put("ipMac", ipMac);
        contentValues.put("externalPort", externalPort);
        contentValues.put("internalPort", internalPort);
        contentValues.put("isPublic", isPublic);
        contentValues.put("expiryDate", expiryDate);
        db.insertWithOnConflict("server", null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from server where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, SERVER_TABLE_NAME);
        return numRows;
    }

    public boolean updateServer (String ip, String name, String ipMac, String externalPort, String internalPort, Boolean isPublic, Long expiryDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("ipMac", ipMac);
        contentValues.put("externalPort", externalPort);
        contentValues.put("internalPort", internalPort);
        contentValues.put("isPublic", isPublic);
        contentValues.put("expiryDate", expiryDate);
        db.update("server", contentValues, "ip = ? ", new String[] { ip } );
        return true;
    }

    public boolean contains(String key, String value) {
        SQLiteDatabase db = this.getReadableDatabase();
        return DatabaseUtils.longForQuery(db, "SELECT COUNT(*) FROM server where " + key + " = '" + value + "'", null) > 0;
    }

    public Integer deleteServer (String ip) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("server",
                "ip = ? ",
                new String[] { ip });
    }

    public ArrayList<Server> getServers() {
        ArrayList<Server> servers = new ArrayList<Server>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from server", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            servers.add(new Server(res));
            res.moveToNext();
        }
        return servers;
    }
}
