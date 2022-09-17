package de.carstendroesser.obdtripmate.activities;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import de.carstendroesser.obdtripmate.database.Loop;
import de.carstendroesser.obdtripmate.database.Trip;
import de.carstendroesser.obdtripmate.obd.OBDCommands.OBDCommand;

/**
 * Created by carstendrosser on 05.07.17.
 */

public class TripSerializer implements JsonSerializer<Trip> {

    public JsonElement serialize(final Trip pTrip, final Type pType, final JsonSerializationContext pContext) {

        JsonObject jsonTrip = new JsonObject();
        jsonTrip.add("id", new JsonPrimitive(pTrip.getId()));
        jsonTrip.add("starttime", new JsonPrimitive(pTrip.getStartTime()));
        jsonTrip.add("endtime", new JsonPrimitive(pTrip.getEndTime()));
        jsonTrip.add("duration", new JsonPrimitive(pTrip.getDuration()));

        JsonObject jsonTripStartLocation = new JsonObject();
        jsonTripStartLocation.add("latitude", new JsonPrimitive(pTrip.getStartPoint().latitude));
        jsonTripStartLocation.add("longitude", new JsonPrimitive(pTrip.getStartPoint().longitude));

        JsonObject jsonTripEndLocation = new JsonObject();
        jsonTripEndLocation.add("latitude", new JsonPrimitive(pTrip.getEndPoint().latitude));
        jsonTripEndLocation.add("longitude", new JsonPrimitive(pTrip.getEndPoint().longitude));

        jsonTrip.add("location_start", jsonTripStartLocation);
        jsonTrip.add("location_end", jsonTripEndLocation);

        JsonArray jsonLoops = new JsonArray();

        for (Loop loop : pTrip.getLoops()) {
            JsonObject jsonLoop = new JsonObject();
            jsonLoop.add("id", new JsonPrimitive(loop.getId()));
            jsonLoop.add("timestamp", new JsonPrimitive(loop.getTimeStamp()));

            JsonObject jsonLocation = new JsonObject();
            jsonLocation.add("latitude", new JsonPrimitive(loop.getLocation().getLatitude()));
            jsonLocation.add("longitude", new JsonPrimitive(loop.getLocation().getLongitude()));

            jsonLoop.add("location", jsonLocation);

            JsonArray jsonCommands = new JsonArray();

            for (OBDCommand command : loop.getCommands()) {
                JsonObject jsonCommand = new JsonObject();
                jsonCommand.add("name", new JsonPrimitive(command.getName()));
                jsonCommand.add("code", new JsonPrimitive(command.getCommandCode()));
                jsonCommand.add("response", new JsonPrimitive(command.getResponse()));
                jsonCommand.add("responsetime", new JsonPrimitive(command.getResponseTime()));

                jsonCommands.add(jsonCommand);
            }

            jsonLoop.add("commands", jsonCommands);

            jsonLoops.add(jsonLoop);
        }

        jsonTrip.add("loops", jsonLoops);

        return jsonTrip;
    }

}