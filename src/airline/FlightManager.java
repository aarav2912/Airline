package airline;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FlightManager {
    private List<Flight> seededFlights = new ArrayList<>();

    public static final String[][] AIRPORTS = {
        {"DEL", "Delhi (Indira Gandhi International)"},
        {"BOM", "Mumbai (Chhatrapati Shivaji Maharaj International)"},
        {"BLR", "Bengaluru (Kempegowda International)"},
        {"MAA", "Chennai (Chennai International)"},
        {"HYD", "Hyderabad (Rajiv Gandhi International)"},
        {"CCU", "Kolkata (Netaji Subhas Chandra Bose International)"},
        {"COK", "Kochi (Cochin International)"},
        {"AMD", "Ahmedabad (Sardar Vallabhbhai Patel International)"},
        {"PNQ", "Pune (Pune Airport)"},
        {"GOI", "Goa (Goa International – Dabolim)"},
        {"JAI", "Jaipur (Jaipur International)"},
        {"LKO", "Lucknow (Chaudhary Charan Singh International)"}
    };

    public FlightManager() { 
        seedFlights(); 
    }

    private void seedFlights() {
        // --- STEP 1: LOAD HANDCRAFTED DATA ---
        addInitialFlights();

        // --- STEP 2: AUTO-GENERATE MISSING ROUTES ---
        // Ensures every pair has at least 2 flights (A -> B and B -> A)
        String[] airlines = {"IndiGo", "Air India", "Vistara", "SpiceJet"};
        
        for (int i = 0; i < AIRPORTS.length; i++) {
            for (int j = 0; j < AIRPORTS.length; j++) {
                if (i == j) continue; // Skip same-city pairs

                String origin = AIRPORTS[i][0];
                String destination = AIRPORTS[j][0];

                // Check how many flights we already have for this specific route
                long count = seededFlights.stream()
                    .filter(f -> f.getOrigin().equalsIgnoreCase(origin) && 
                                 f.getDestination().equalsIgnoreCase(destination))
                    .count();

                // Add flights until we hit the minimum of 2
                for (long k = count; k < 2; k++) {
                    String airline = airlines[(i + j + (int)k) % 4];
                    String flightNo = airline.substring(0, 2).toUpperCase() + (1000 + (i * 50) + (j * 5) + (int)k);
                    
                    // Generate unique times based on airport index
                    String depTime = String.format("%02d:%02d", (6 + i + (int)k) % 24, (j * 4) % 60);
                    String arrTime = String.format("%02d:%02d", (9 + i + (int)k) % 24, (j * 4) % 60);
                    
                    // Pricing based on approximate distance (index difference)
                    int basePrice = 2000 + (Math.abs(i - j) * 500);
                    int businessPrice = basePrice * 3;

                    seededFlights.add(new Flight(
                        flightNo, airline, origin, destination, 
                        depTime, arrTime, "2h 30m", basePrice, businessPrice
                    ));
                }
            }
        }
    }

    private void addInitialFlights() {
        // --- ORIGINAL SET ---
        seededFlights.add(new Flight("AI101","Air India","DEL","BOM","06:00","08:10","2h 10m",4500,14000));
        seededFlights.add(new Flight("6E201","IndiGo","DEL","BOM","08:30","10:45","2h 15m",3800,11500));
        seededFlights.add(new Flight("SG301","SpiceJet","DEL","BOM","14:00","16:20","2h 20m",3500,10500));
        seededFlights.add(new Flight("UK401","Vistara","DEL","BOM","18:00","20:15","2h 15m",5200,16000));
        seededFlights.add(new Flight("AI102","Air India","BOM","DEL","09:00","11:15","2h 15m",4800,14500));
        seededFlights.add(new Flight("6E202","IndiGo","BOM","DEL","15:30","17:45","2h 15m",4000,12000));
        seededFlights.add(new Flight("AI111","Air India","DEL","BLR","07:00","09:30","2h 30m",5000,15500));
        seededFlights.add(new Flight("6E211","IndiGo","DEL","BLR","10:00","12:35","2h 35m",4200,13000));
        seededFlights.add(new Flight("UK411","Vistara","DEL","BLR","16:00","18:40","2h 40m",5800,17500));
        seededFlights.add(new Flight("AI112","Air India","BLR","DEL","11:00","13:40","2h 40m",5100,15800));
        seededFlights.add(new Flight("AI121","Air India","BOM","BLR","07:30","09:00","1h 30m",3200,10000));
        seededFlights.add(new Flight("6E221","IndiGo","BOM","BLR","11:00","12:35","1h 35m",2800,9000));
        seededFlights.add(new Flight("SG321","SpiceJet","BOM","BLR","17:00","18:40","1h 40m",2600,8500));
        seededFlights.add(new Flight("AI122","Air India","BLR","BOM","09:30","11:05","1h 35m",3300,10200));
        seededFlights.add(new Flight("AI131","Air India","DEL","MAA","06:30","09:20","2h 50m",5500,16500));
        seededFlights.add(new Flight("6E231","IndiGo","DEL","MAA","13:00","15:55","2h 55m",4700,14500));
        seededFlights.add(new Flight("AI132","Air India","MAA","DEL","10:00","12:55","2h 55m",5600,16800));
        seededFlights.add(new Flight("AI141","Air India","BOM","HYD","08:00","09:30","1h 30m",3000,9500));
        seededFlights.add(new Flight("6E241","IndiGo","BOM","HYD","14:00","15:35","1h 35m",2700,8800));
        seededFlights.add(new Flight("AI142","Air India","HYD","BOM","10:00","11:35","1h 35m",3100,9700));
        seededFlights.add(new Flight("AI151","Air India","DEL","CCU","07:00","09:20","2h 20m",4800,14500));
        seededFlights.add(new Flight("6E251","IndiGo","DEL","CCU","13:30","15:55","2h 25m",4100,12500));
        seededFlights.add(new Flight("AI152","Air India","CCU","DEL","10:30","12:55","2h 25m",4900,14800));
        seededFlights.add(new Flight("6E261","IndiGo","BLR","HYD","09:00","10:05","1h 05m",2200,7500));
        seededFlights.add(new Flight("SG361","SpiceJet","BLR","HYD","15:00","16:10","1h 10m",2000,7000));
        seededFlights.add(new Flight("6E262","IndiGo","HYD","BLR","11:00","12:10","1h 10m",2300,7800));
        seededFlights.add(new Flight("AI161","Air India","DEL","GOI","08:00","10:20","2h 20m",5200,15800));
        seededFlights.add(new Flight("UK461","Vistara","DEL","GOI","19:00","21:25","2h 25m",6000,18000));
        seededFlights.add(new Flight("AI162","Air India","GOI","DEL","11:00","13:25","2h 25m",5300,16000));
        seededFlights.add(new Flight("AI171","Air India","BOM","COK","07:00","09:00","2h 00m",3800,12000));
        seededFlights.add(new Flight("6E271","IndiGo","BOM","COK","14:00","16:05","2h 05m",3400,10800));
        seededFlights.add(new Flight("AI172","Air India","COK","BOM","09:30","11:35","2h 05m",3900,12200));
        seededFlights.add(new Flight("6E281","IndiGo","DEL","JAI","07:30","08:35","1h 05m",2000,7000));
        seededFlights.add(new Flight("AI181","Air India","DEL","JAI","12:00","13:10","1h 10m",2200,7500));
        seededFlights.add(new Flight("6E282","IndiGo","JAI","DEL","09:00","10:10","1h 10m",2100,7200));
        seededFlights.add(new Flight("6E291","IndiGo","BOM","AMD","08:00","09:05","1h 05m",2100,7200));
        seededFlights.add(new Flight("SG391","SpiceJet","BOM","AMD","14:30","15:40","1h 10m",1900,6800));
        seededFlights.add(new Flight("6E292","IndiGo","AMD","BOM","10:00","11:10","1h 10m",2200,7400));
        
        // --- ADDED EXTRAS FOR PUNE & LUCKNOW ---
        seededFlights.add(new Flight("6E501","IndiGo","PNQ","DEL","07:00","09:10","2h 10m",4200,12500));
        seededFlights.add(new Flight("AI502","Air India","PNQ","DEL","19:00","21:15","2h 15m",4600,13800));
        seededFlights.add(new Flight("6E503","IndiGo","DEL","PNQ","10:30","12:40","2h 10m",4100,12200));
        seededFlights.add(new Flight("UK504","Vistara","DEL","PNQ","22:00","00:10","2h 10m",5000,15000));
        seededFlights.add(new Flight("6E601","IndiGo","LKO","DEL","08:30","09:40","1h 10m",2500,8000));
        seededFlights.add(new Flight("AI602","Air India","LKO","DEL","20:00","21:15","1h 15m",2800,8500));
        seededFlights.add(new Flight("6E603","IndiGo","DEL","LKO","07:00","08:10","1h 10m",2400,7800));
        seededFlights.add(new Flight("UK604","Vistara","DEL","LKO","18:00","19:15","1h 15m",3200,9500));
    }

    public List<Flight> searchFlights(String origin, String destination) {
        List<Flight> results = new ArrayList<>();
        // Note: AeroDataBoxService logic remains as per your original file
        
        List<Flight> seeded = seededFlights.stream()
                .filter(f -> f.getOrigin().equalsIgnoreCase(origin)
                        && f.getDestination().equalsIgnoreCase(destination))
                .collect(Collectors.toList());
        results.addAll(seeded);
        return results;
    }

    public List<String> getAirportCodes() {
        List<String> codes = new ArrayList<>();
        for (String[] ap : AIRPORTS) codes.add(ap[0]);
        return codes;
    }

    public String getAirportName(String code) {
        for (String[] ap : AIRPORTS)
            if (ap[0].equals(code)) return ap[1];
        return code;
    }
}