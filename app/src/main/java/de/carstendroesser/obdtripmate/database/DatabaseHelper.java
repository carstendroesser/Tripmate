package de.carstendroesser.obdtripmate.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import de.carstendroesser.obdtripmate.obd.OBDCommands.CommandMapper;
import de.carstendroesser.obdtripmate.obd.OBDCommands.OBDCommand;

/**
 * Created by carstendrosser on 29.06.17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    // CONSTANTS

    public static final String TRIPS_TABLENAME = "trips";
    public static final String TRIP_ID = "trip_id";
    public static final String TRIP_TIME_START = "trip_time_start";
    public static final String TRIP_TIME_END = "trip_time_end";
    public static final String TRIP_START_LAT = "trip_start_lat";
    public static final String TRIP_START_LONG = "trip_start_long";
    public static final String TRIP_END_LAT = "trip_end_lat";
    public static final String TRIP_END_LONG = "trip_end_long";

    public static final String LOOPS_TABLENAME = "loops";
    public static final String LOOP_ID = "loop_id";
    public static final String LOOP_TRIP_ID = "loop_trip_id";
    public static final String LOOP_LATITUDE = "loop_latitude";
    public static final String LOOP_LONGITUDE = "loop_longitude";
    public static final String LOOP_TIMESTAMP = "loop_timestamp";

    public static final String VALUES_TABLENAME = "valuez";
    public static final String VALUE_ID = "value_id";
    public static final String VALUE_LOOP_ID = "value_loop_id";
    public static final String VALUE_COMMAND_CODE = "value_command_code";
    public static final String VALUE_RESPONSE_TIME = "value_response_time";
    public static final String VALUE_RESPONSE = "value_response";

    private static final String TRIPS_CREATE_TABLE
            = "CREATE TABLE "
            + TRIPS_TABLENAME
            + " ("
            + TRIP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TRIP_TIME_START + " VARCHAR(100),"
            + TRIP_TIME_END + " VARCHAR(100),"
            + TRIP_START_LAT + " VARCHAR(100),"
            + TRIP_START_LONG + " VARCHAR(100),"
            + TRIP_END_LAT + " VARCHAR(100),"
            + TRIP_END_LONG + " VARCHAR(100));";

    private static final String LOOPS_CREATE_TABLE
            = "CREATE TABLE "
            + LOOPS_TABLENAME
            + " ("
            + LOOP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + LOOP_TRIP_ID + " INTEGER, "
            + LOOP_LATITUDE + " VARCHAR(100), "
            + LOOP_LONGITUDE + " VARCHAR(100),"
            + LOOP_TIMESTAMP + " VARCHAR(100));";

    private static final String VALUES_CREATE_TABLE
            = "CREATE TABLE "
            + VALUES_TABLENAME
            + " ("
            + VALUE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + VALUE_LOOP_ID + " INTEGER, "
            + VALUE_COMMAND_CODE + " VARCHAR(100), "
            + VALUE_RESPONSE_TIME + " VARCHAR(100), "
            + VALUE_RESPONSE + " VARCHAR(100));";

    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;

    private static DatabaseHelper mInstance;

    // MEMBERS

    private SQLiteDatabase mDatabase;

    // CONSTRUCTORS

    /**
     * Private constructor to prevent initialisations as this is a singleton.
     *
     * @param pContext we need that
     */
    private DatabaseHelper(Context pContext) {
        super(pContext, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Gets the single instance of this class.
     *
     * @param pContext we need that
     * @return the single DataBaseHelper instance
     */
    public static DatabaseHelper getInstance(Context pContext) {
        if (mInstance == null) {
            mInstance = new DatabaseHelper(pContext);
        }
        return mInstance;
    }

    // SQLITEOPENHELPER

    @Override
    public void onCreate(SQLiteDatabase pDatabase) {
        pDatabase.execSQL(TRIPS_CREATE_TABLE);
        pDatabase.execSQL(LOOPS_CREATE_TABLE);
        pDatabase.execSQL(VALUES_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase pDatabase, int pOldVersion, int pNewVersion) {
        // empty
    }

    // PRIVATE API

    /**
     * Makes sure that the database member is correct.
     */
    private void checkDatabase() {
        if (mDatabase == null) {
            mDatabase = getWritableDatabase();
        }
    }

    // PUBLIC API

    /**
     * Inserts a trip into the database.
     *
     * @param pTrip the trip to insert
     * @return the id of the inserted trip or -1 if something went wrong
     */
    public long insertTrip(Trip pTrip) {
        checkDatabase();

        ContentValues values = new ContentValues();
        values.put(TRIP_TIME_START, pTrip.getStartTime());
        values.put(TRIP_TIME_END, pTrip.getEndTime());
        values.put(TRIP_START_LAT, pTrip.getStartPoint().latitude);
        values.put(TRIP_START_LONG, pTrip.getStartPoint().longitude);

        return mDatabase.insert(TRIPS_TABLENAME, null, values);
    }

    /**
     * Inserts a Loop for a given tripid with a specific Location.
     *
     * @param pTripId    the tripid this loop belongs to
     * @param pLocation  the location of this loop
     * @param pTimeStamp the time of this loop
     * @return the id of the inserted loop or -1 if something went wrong
     */
    public long insertLoop(long pTripId, Location pLocation, long pTimeStamp) {

        checkDatabase();

        ContentValues values = new ContentValues();
        values.put(LOOP_TRIP_ID, pTripId);
        values.put(LOOP_LATITUDE, "" + pLocation.getLatitude());
        values.put(LOOP_LONGITUDE, "" + pLocation.getLongitude());
        values.put(LOOP_TIMESTAMP, "" + pTimeStamp);

        return mDatabase.insert(LOOPS_TABLENAME, null, values);
    }

    /**
     * Inserts an OBDCommand for a given Loop.
     *
     * @param pLoopId  the loop's id this value belongs to
     * @param pCommand the command to store it's data for
     * @return the id of the inserted value or -1 if something went wrong
     */
    public long insertValue(long pLoopId, OBDCommand pCommand) {
        checkDatabase();

        ContentValues values = new ContentValues();
        values.put(VALUE_LOOP_ID, pLoopId);
        values.put(VALUE_COMMAND_CODE, pCommand.getCommandCode());
        values.put(VALUE_RESPONSE_TIME, pCommand.getResponseTime());
        values.put(VALUE_RESPONSE, pCommand.getResponse());

        return mDatabase.insert(VALUES_TABLENAME, null, values);
    }

    /**
     * Gets a list of all stored trips.
     *
     * @return a list of trips
     */
    public ArrayList<Trip> getTrips() {
        checkDatabase();

        //query
        Cursor tripscursor = mDatabase.rawQuery("SELECT * FROM " + TRIPS_TABLENAME, null);
        ArrayList<Trip> trips = new ArrayList<Trip>();

        while (tripscursor.moveToNext()) {
            int id = tripscursor.getInt(0);
            long startTime = tripscursor.getLong(1);
            long endTime = tripscursor.getLong(2);
            double startLat = tripscursor.getDouble(3);
            double startLong = tripscursor.getDouble(4);
            double endLat = tripscursor.getDouble(5);
            double endLong = tripscursor.getDouble(6);

            trips.add(new Trip(id, startTime, endTime, new LatLng(startLat, startLong), new LatLng(endLat, endLong)));
        }

        tripscursor.close();

        return trips;
    }

    /**
     * Gets the trip by a given id.
     *
     * @param pId the id to get the trip for
     * @return a trip with the given id or null if not existent
     */
    public Trip getTripById(long pId) {
        checkDatabase();

        //query
        Cursor tripscursor = mDatabase.rawQuery("SELECT * FROM " + TRIPS_TABLENAME + " WHERE " + TRIP_ID + " = " + pId, null);

        if (tripscursor.getCount() > 0) {
            tripscursor.moveToFirst();
            int id = tripscursor.getInt(0);
            long startTime = tripscursor.getLong(1);
            long endTime = tripscursor.getLong(2);
            double startLat = tripscursor.getDouble(3);
            double startLong = tripscursor.getDouble(4);
            double endLat = tripscursor.getDouble(5);
            double endLong = tripscursor.getDouble(6);
            tripscursor.close();
            return new Trip(id, startTime, endTime, new LatLng(startLat, startLong), new LatLng(endLat, endLong));
        }

        return null;
    }

    /**
     * Updates a stored trip.
     *
     * @param pTrip the trip to update
     * @return the number of rows that have been updated
     */
    public boolean updateTrip(Trip pTrip) {
        checkDatabase();

        ContentValues values = new ContentValues();
        values.put(TRIP_ID, pTrip.getId());
        values.put(TRIP_TIME_START, pTrip.getStartTime());
        values.put(TRIP_TIME_END, pTrip.getEndTime());
        values.put(TRIP_START_LAT, pTrip.getStartPoint().latitude);
        values.put(TRIP_START_LONG, pTrip.getStartPoint().longitude);
        values.put(TRIP_END_LAT, pTrip.getEndPoint().latitude);
        values.put(TRIP_END_LONG, pTrip.getEndPoint().longitude);

        return mDatabase.update(TRIPS_TABLENAME, values, TRIP_ID + " = " + pTrip.getId(), null) > 0;
    }

    /**
     * Gets a list of all loops for the given tripid.
     *
     * @param pTripId the trip's id to fetch the loops for
     * @return a list of loops belonging to the given trip
     */
    public ArrayList<Loop> getLoopsForTrip(long pTripId) {
        checkDatabase();

        //query
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + LOOPS_TABLENAME + " WHERE " + LOOP_TRIP_ID + " = " + pTripId, null);
        ArrayList<Loop> loops = new ArrayList<Loop>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            double latitude = cursor.getDouble(2);
            double longitude = cursor.getDouble(3);
            long timestamp = cursor.getLong(4);

            Location location = new Location("database");
            location.setLatitude(latitude);
            location.setLongitude(longitude);

            ArrayList<OBDCommand> commands = getValuesForLoop(id);

            loops.add(new Loop(id, location, timestamp, commands));
        }

        cursor.close();

        return loops;
    }

    /**
     * Deletes a trip including it's loops and their values by a given tripid.
     *
     * @param pId the id of the trip to delete
     */
    public void deleteTripById(long pId) {
        mDatabase.delete(TRIPS_TABLENAME, TRIP_ID + "=" + pId, null);
        for (Loop loop : getLoopsForTrip(pId)) {
            mDatabase.delete(VALUES_TABLENAME, VALUE_LOOP_ID + " = " + loop.getId(), null);
            mDatabase.delete(LOOPS_TABLENAME, LOOP_ID + " = " + loop.getId(), null);
        }
    }

    // PRIVATE API

    /**
     * Gets all values for a given loop.
     *
     * @param pLoopId the loop's id to fetch the values for
     * @return a list of values for this loop
     */
    private ArrayList<OBDCommand> getValuesForLoop(int pLoopId) {
        checkDatabase();

        //query
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + VALUES_TABLENAME + " WHERE " + VALUE_LOOP_ID + " = " + pLoopId, null);
        ArrayList<OBDCommand> commands = new ArrayList<OBDCommand>();

        while (cursor.moveToNext()) {
            String commandcode = cursor.getString(2);
            OBDCommand command = CommandMapper.getCommandByCommandCode(commandcode);
            if (command != null) {
                command.setResponse(cursor.getString(4), cursor.getLong(3));
                commands.add(command);
            }
        }

        cursor.close();
        return commands;
    }

}
