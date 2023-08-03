package com.WeatherPackage;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class WeatherApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        WeatherApiHandler apiHandler = new WeatherApiHandler();

        while (true) {
            System.out.println("1. Get weather");
            System.out.println("2. Get Wind Speed");
            System.out.println("3. Get Pressure");
            System.out.println("0. Exit");

            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            switch (choice) {
                case 1:
                    System.out.print("Enter the date (YYYY-MM-DD HH:MM:SS): ");
                    String dateStr = scanner.nextLine();
                    double temperature = apiHandler.getTemperature(dateStr);
                    System.out.println("Temperature: " + temperature + " K");
                    break;
                case 2:
                    System.out.print("Enter the date (YYYY-MM-DD HH:MM:SS): ");
                    dateStr = scanner.nextLine();
                    double windSpeed = apiHandler.getWindSpeed(dateStr);
                    System.out.println("Wind Speed: " + windSpeed + " m/s");
                    break;
                case 3:
                    System.out.print("Enter the date (YYYY-MM-DD HH:MM:SS): ");
                    dateStr = scanner.nextLine();
                    double pressure = apiHandler.getPressure(dateStr);
                    System.out.println("Pressure: " + pressure + " hPa");
                    break;
                case 0:
                    System.out.println("Exiting the program.");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}

class WeatherApiHandler {
    private static final String API_URL = "https://samples.openweathermap.org/data/2.5/forecast/hourly?q=London,us&appid=b6907d289e10d714a6e88b30761fae22";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public double getTemperature(String dateStr) {
        return fetchData(dateStr, "temp");
    }

    public double getWindSpeed(String dateStr) {
        return fetchData(dateStr, "speed");
    }

    public double getPressure(String dateStr) {
        return fetchData(dateStr, "pressure");
    }

    private double fetchData(String dateStr, String field) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            JsonParser parser = new JsonParser();
            JsonObject jsonResponse = parser.parse(new InputStreamReader(connection.getInputStream())).getAsJsonObject();

            JsonArray list = jsonResponse.getAsJsonArray("list");
            for (int i = 0; i < list.size(); i++) {
                JsonObject data = list.get(i).getAsJsonObject();
                String dtTxt = data.get("dt_txt").getAsString();

                if (dtTxt.startsWith(dateStr)) {
                    JsonObject main = data.getAsJsonObject("main");
                    double value = main.get(field).getAsDouble();
                    return value;
                }
            }

            return -1.0; // Return a sentinel value if data not found
        } catch (IOException e) {
            System.out.println("Error fetching data from API: " + e.getMessage());
            return -1.0; // Return a sentinel value to indicate an error
        }
    }
}
