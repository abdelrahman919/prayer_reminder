package com.hamada;

import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.stream.Collectors;

public class ApiCaller {

    //TLDR: calls the api and returns a map of the 5 prayers and their timings
    public static Map<String,String> getPrayerTimings() {
        try {
            //TODO: DYNAMIC CITY AND COUNTRY, METHOD CAN BE OPTIONAL

            // Specify the API endpoint
            String apiUrl = "http://api.aladhan.com/v1/timingsByCity?city=Alexandria&country=Egypt";

            // Create a URI object
            URI uri = URI.create(apiUrl);

            // Create an HttpClient with automatic redirect following
            HttpClient httpClient = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .build();

            // Create a HttpRequest
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .build();

            // Send the request and get the response
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            // Check the response status code
            int statusCode = httpResponse.statusCode();
            System.out.println("Response Code: " + statusCode);

            // If the response is a redirect (status code 3xx), get the new location
            if (statusCode >= 300 && statusCode < 400) {
                String newLocation = httpResponse.headers().firstValue("Location").orElse("");
                System.out.println("Redirected to: " + newLocation);
            } else {
                // Parse the JSON response
                JSONObject jsonResponse = new JSONObject(httpResponse.body());
                // Extracting the 5 prayers from json body into a map then return it
                Map<String, String> timings = jsonResponse.getJSONObject("data")
                        .getJSONObject("timings")
                        .keySet().stream()
                        .filter(key -> key.equals("Fajr") || key.equals("Dhuhr") || key.equals("Asr") ||
                                key.equals("Maghrib") || key.equals("Isha"))
                        .collect(Collectors.toMap(key -> key, key -> jsonResponse.getJSONObject("data")
                                .getJSONObject("timings").getString(key)));
                return timings;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
