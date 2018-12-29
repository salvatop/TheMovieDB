package com.salvatop.android.moviedb.utilities;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public final class JsonUtils {

    public static String[] getMovieJson(Context context, String moviesJsonString) throws JSONException {

        /* Weather information. Each day's movies info is an element of the "list" array */
        final String OWM_LIST = "list";


        final String OWM_WEATHER = "weather";
        final String OWM_DESCRIPTION = "main";

        final String OWM_MESSAGE_CODE = "cod";

        /* String array to hold each day's weather String */
        String[] parsedWeatherData = null;

        JSONObject forecastJson = new JSONObject(moviesJsonString);

        /* Is there an error? */
        if (forecastJson.has(OWM_MESSAGE_CODE)) {
            int errorCode = forecastJson.getInt(OWM_MESSAGE_CODE);

            switch (errorCode) {

                case HttpURLConnection.HTTP_OK: break;

                case HttpURLConnection.HTTP_NOT_FOUND: return null;

                default: return null;
            }
        }

        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        parsedWeatherData = new String[weatherArray.length()];

        for (int i = 0; i < weatherArray.length(); i++) {

            String description;

            /* Get the JSON object representing the day */
            JSONObject dayForecast = weatherArray.getJSONObject(i);

            JSONObject weatherObject =
                    dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);

            parsedWeatherData[i] = description + " - ";
        }

        return parsedWeatherData;
    }
}