package com.WeatherPackage;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
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
                    String date = scanner.nextLine();
                    double temperature = apiHandler.getTemperature(date);
                    System.out.println("Temperature: " + temperature + " K");
                    break;
                case 2:
                    System.out.print("Enter the date (YYYY-MM-DD HH:MM:SS): ");
                    date = scanner.nextLine();
                    double windSpeed = apiHandler.getWindSpeed(date);
                    System.out.println("Wind Speed: " + windSpeed + " m/s");
                    break;
                case 3:
                    System.out.print("Enter the date (YYYY-MM-DD HH:MM:SS): ");
                    date = scanner.nextLine();
                    double pressure = apiHandler.getPressure(date);
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

    public double getTemperature(String date) {
        return fetchData(date, "temp");
    }

    public double getWindSpeed(String date) {
        return fetchData(date, "wind.speed");
    }

    public double getPressure(String date) {
        return fetchData(date, "main.pressure");
    }

  private double fetchData(String date, String field) {
    try {
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        Scanner scanner = new Scanner(connection.getInputStream());
        StringBuilder response = new StringBuilder();
        while (scanner.hasNextLine()) {
            response.append(scanner.nextLine());
        }
        scanner.close();

        // Parse the JSON response to get the desired data using Gson
        Gson gson = new Gson();
        JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);

        // Extract the relevant data based on the 'field' parameter
        double value = -1.0;
        if (jsonResponse.has("list")) {
            JsonElement listElement = jsonResponse.get("list");
            if (listElement.isJsonArray()) {
                for (JsonElement element : listElement.getAsJsonArray()) {
                    if (element.isJsonObject()) {
                        JsonObject dataObject = element.getAsJsonObject();
                        if (dataObject.has("dt_txt") && dataObject.get("dt_txt").getAsString().startsWith(date)) {
                            if (field.equals("temp")) {
                                value = dataObject.getAsJsonObject("main").get("temp").getAsDouble();
                            } else if (field.equals("wind.speed")) {
                                value = dataObject.getAsJsonObject("wind").get("speed").getAsDouble();
                            } else if (field.equals("main.pressure")) {
                                value = dataObject.getAsJsonObject("main").get("pressure").getAsDouble();
                            }
                            break;
                        }
                    }
                }
            }
        }

        return value;
    } catch (IOException e) {
        System.out.println("Error fetching data from API: " + e.getMessage());
        return -1.0; // Return a sentinel value to indicate an error
    }
}
}

