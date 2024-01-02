package com.hamada;

import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {


        Map<String, String> prayerTimings = ApiCaller.getPrayerTimings();

        System.out.println(prayerTimings);

    }


}
