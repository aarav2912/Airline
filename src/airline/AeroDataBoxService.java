package airline;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Fetches real flights from AeroDataBox (via RapidAPI).
 *
 * Endpoint used:
 *   GET /airports/iata/{code}/stats/routes/daily
 *   — returns all routes departing from a given airport today,
 *     with airline, flight number, destination, and frequency.
 *
 * To activate: paste your RapidAPI key into RAPIDAPI_KEY below.
 */
public class AeroDataBoxService {

    // ─── PASTE YOUR KEY HERE ───────────────────────────────────────
    private static final String RAPIDAPI_KEY = "cc337e7b43msh229c6d2977e7ae3p10a680jsn3b4a61397205";
    // ──────────────────────────────────────────────────────────────

    private static final String HOST = "aerodatabox.p.rapidapi.com";

    /**
     * Returns true if a real API key has been set.
     */
    public static boolean isConfigured() {
        return !RAPIDAPI_KEY.equals("YOUR_RAPIDAPI_KEY_HERE") && !RAPIDAPI_KEY.isBlank();
    }

    /**
     * Fetches flights from origin → destination for today using
     * the Airport FIDS (departures) endpoint:
     *   GET /airports/iata/{origin}/flights/departures
     *
     * Filters results to only those arriving at the destination.
     * Returns a list of Flight objects with real data + randomised prices.
     */
    public static List<Flight> fetchFlights(String originIata, String destinationIata) {
        List<Flight> flights = new ArrayList<>();
        if (!isConfigured()) return flights;

        try {
            // Use the departures FIDS window: now to now+12h
            String urlStr = "https://" + HOST + "/airports/iata/" + originIata
                    + "/flights/departures?withLeg=true&direction=Departure&withCancelled=false&withCodeshared=false&withCargo=false&withPrivate=false";
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("x-rapidapi-key", RAPIDAPI_KEY);
            conn.setRequestProperty("x-rapidapi-host", HOST);
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);

            int code = conn.getResponseCode();
            if (code != 200) return flights;

            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
            }
            flights = parseFlights(sb.toString(), originIata, destinationIata);
        } catch (Exception e) {
            System.err.println("[AeroDataBox] API call failed: " + e.getMessage());
        }
        return flights;
    }

    /**
     * Minimal JSON parser — no external library needed.
     * Extracts flight entries where arrival.iata == destinationIata.
     */
    private static List<Flight> parseFlights(String json, String origin, String destination) {
        List<Flight> result = new ArrayList<>();
        if (json == null || json.isEmpty()) return result;

        // Split into individual flight blocks by "number" key
        String[] blocks = json.split("\\{\"number\":");
        java.util.Random rand = new java.util.Random();

        for (int i = 1; i < blocks.length; i++) {
            String block = blocks[i];
            try {
                // Check destination
                String arrIata = extractField(block, "iata", 2); // second iata = arrival airport
                if (arrIata == null || !arrIata.equalsIgnoreCase(destination)) continue;

                String flightNum  = extractField(block, "number", 1);
                String airlineName = extractField(block, "name", 1);
                String depTime    = extractTimeField(block, "scheduledTime", "departure");
                String arrTime    = extractTimeField(block, "scheduledTime", "arrival");

                if (flightNum == null || depTime == null || arrTime == null) continue;

                // Format times to HH:mm
                depTime = formatTime(depTime);
                arrTime = formatTime(arrTime);
                if (depTime == null || arrTime == null) continue;

                String airline = airlineName != null ? airlineName : "Unknown Airline";

                // Estimate duration
                String duration = estimateDuration(origin, destination);

                // Randomise realistic prices
                double ecoBase = 2500 + rand.nextInt(5000);
                double bizBase = ecoBase * 2.8 + rand.nextInt(3000);
                ecoBase = Math.round(ecoBase / 100.0) * 100;
                bizBase = Math.round(bizBase / 100.0) * 100;

                Flight f = new Flight(flightNum, airline, origin, destination,
                        depTime, arrTime, duration, ecoBase, bizBase);
                result.add(f);

                if (result.size() >= 8) break; // cap at 8 real flights per search
            } catch (Exception ignored) {}
        }
        return result;
    }

    private static String extractField(String text, String key, int occurrence) {
        String search = "\"" + key + "\":\"";
        int found = 0;
        int idx = 0;
        while (idx < text.length()) {
            int start = text.indexOf(search, idx);
            if (start < 0) return null;
            found++;
            if (found == occurrence) {
                int valueStart = start + search.length();
                int valueEnd = text.indexOf("\"", valueStart);
                if (valueEnd < 0) return null;
                return text.substring(valueStart, valueEnd);
            }
            idx = start + search.length();
        }
        return null;
    }

    private static String extractTimeField(String text, String timeKey, String section) {
        int secIdx = text.indexOf("\"" + section + "\"");
        if (secIdx < 0) return null;
        String sub = text.substring(secIdx);
        return extractField(sub, timeKey, 1);
    }

    private static String formatTime(String iso) {
        // Expects ISO like "2026-03-24T14:30:00+05:30" or "2026-03-24T14:30"
        try {
            int tIdx = iso.indexOf('T');
            if (tIdx < 0) return null;
            String timePart = iso.substring(tIdx + 1);
            // strip timezone
            int plusIdx = timePart.indexOf('+');
            if (plusIdx > 0) timePart = timePart.substring(0, plusIdx);
            String[] parts = timePart.split(":");
            return parts[0] + ":" + parts[1];
        } catch (Exception e) {
            return null;
        }
    }

    /** Rough duration estimate based on known Indian route distances */
    private static String estimateDuration(String from, String to) {
        java.util.Map<String, Integer> dist = new java.util.HashMap<>();
        dist.put("DEL-BOM", 130); dist.put("DEL-BLR", 160); dist.put("DEL-MAA", 175);
        dist.put("DEL-HYD", 150); dist.put("DEL-CCU", 145); dist.put("DEL-GOI", 145);
        dist.put("BOM-BLR", 95);  dist.put("BOM-HYD", 90);  dist.put("BOM-COK", 125);
        dist.put("BLR-HYD", 65);  dist.put("BLR-MAA", 65);  dist.put("BOM-MAA", 120);
        String key1 = from + "-" + to;
        String key2 = to + "-" + from;
        int mins = dist.getOrDefault(key1, dist.getOrDefault(key2, 120));
        return (mins / 60) + "h " + String.format("%02d", mins % 60) + "m";
    }
}
