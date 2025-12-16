package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Scanner;


public class Criptopepe {
//Delcaramos el


    public static int peticionGlobalMonedas() {

        HttpClient hc = HttpClient.newHttpClient();

        HttpRequest peticionGlobal = HttpRequest.newBuilder()
                .uri(URI.create("https://api.coinlore.net/api/global/"))
                .GET()
                .build();


        try {
            HttpResponse<String> jsonRespuestaGlobal = hc.send(peticionGlobal, HttpResponse.BodyHandlers.ofString());


            Gson gson = new Gson();
            JsonArray objetoConsultaGlobal = gson.fromJson(jsonRespuestaGlobal.body(), JsonArray.class);

            JsonObject datos = objetoConsultaGlobal.get(0).getAsJsonObject();

            return datos.get("coins_count").getAsInt();
//TODO
//                JsonObject objetoConsultaGlobal = gson.fromJson(jsonRespuestaGlobal.body(), JsonObject.class);
//
//                return objetoConsultaGlobal.get("coins_count").getAsInt();


        } catch (IOException | InterruptedException e) {
            System.err.println("ERROR AL REALIZAR LA PETICION" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static JsonObject buscarMoneda(String nombre) {
        try {
            //Declaracion de cliente
            HttpClient cliente = HttpClient.newHttpClient();


            int monedasTotales = peticionGlobalMonedas();
            //

            for (int i = 0; i <= monedasTotales; i += 100) {
                HttpRequest peticionMoneda = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.coinlore.net/api/tickers/?start=&limit=100"))
                        .GET()
                        .build();

                HttpResponse<String> respuestaMoneda = cliente.send(peticionMoneda, HttpResponse.BodyHandlers.ofString());
                Gson gson = new Gson();
                JsonObject objetoJson = gson.fromJson(respuestaMoneda.body(), JsonObject.class);
                JsonArray arrayJson = objetoJson.getAsJsonArray("data");

                for (int j = 0; j < arrayJson.size(); j++) {

                    JsonObject moneda = arrayJson.get(j).getAsJsonObject();
                    String nombreMoneda = moneda.get("name").getAsString().toLowerCase();
                    String simbolo = moneda.get("symbol").getAsString().toLowerCase();

                    if (nombreMoneda.equals(nombre) || simbolo.equals(nombre)) {
                        return moneda;
                    } else {
                        return null;
                    }


                }

            }
        } catch (IOException | InterruptedException e) {

            System.err.println("ERRO AL BUSCAR MONEDA: " + e.getMessage());

        }
        return null;


    }

    public static void printearMoneda(JsonObject moneda) {
        String nombre = moneda.get("name").getAsString();
        String simbolo = moneda.get("symbol").getAsString();
        int ranking = moneda.get("rank").getAsInt();
        String precio = moneda.get("price_usd").getAsString();
        String variacion = moneda.get("percent_change_24h").getAsString();

        System.out.println("Nombre: " + nombre);
        System.out.println("Simbolo: " + simbolo);
        System.out.println("ranking: " + ranking);
        System.out.println("Precio: " + precio);
        System.out.println("Variation: " + variacion);


    }


    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("Introduzca el nombre de la moneda");
        String nombre = sc.nextLine().toLowerCase();
        JsonObject mone = buscarMoneda(nombre);

        if (mone == null) {
            System.err.println("LA MONEDA NO EXISTE");
        } else {
            printearMoneda(mone);

        }


    }
}
