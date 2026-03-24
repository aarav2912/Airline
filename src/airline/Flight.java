package airline;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Flight {
    private String flightNumber;
    private String airline;
    private String origin;
    private String destination;
    private String departureTime;
    private String arrivalTime;
    private String duration;
    private double economyPrice;
    private double businessPrice;

    private Map<String, Boolean> seats = new HashMap<>();
    private static final int BUSINESS_ROWS = 4;
    private static final int ECONOMY_ROWS = 20;

    public Flight(String flightNumber, String airline, String origin, String destination,
                  String departureTime, String arrivalTime, String duration,
                  double economyPrice, double businessPrice) {
        this.flightNumber = flightNumber;
        this.airline = airline;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.duration = duration;
        this.economyPrice = economyPrice;
        this.businessPrice = businessPrice;
        initSeats();
    }

    private void initSeats() {
        for (int row = 1; row <= BUSINESS_ROWS; row++)
            for (char col : new char[]{'A', 'B', 'C', 'D'})
                seats.put("B" + row + col, false);
        for (int row = 1; row <= ECONOMY_ROWS; row++)
            for (char col : new char[]{'A', 'B', 'C', 'D', 'E', 'F'})
                seats.put("E" + row + col, false);
        java.util.Random rand = new java.util.Random();
        for (String key : new java.util.ArrayList<>(seats.keySet()))
            if (rand.nextDouble() < 0.30)
                seats.put(key, true);
    }

    public boolean bookSeat(String seatId) {
        if (seats.containsKey(seatId) && !seats.get(seatId)) {
            seats.put(seatId, true);
            return true;
        }
        return false;
    }

    public boolean bookSeats(List<String> seatIds) {
        for (String s : seatIds)
            if (!seats.containsKey(s) || seats.get(s)) return false;
        for (String s : seatIds)
            seats.put(s, true);
        return true;
    }

    public void cancelSeats(List<String> seatIds) {
        for (String s : seatIds)
            if (seats.containsKey(s))
                seats.put(s, false);
    }

    public boolean isSeatBooked(String seatId) { return seats.getOrDefault(seatId, true); }
    public Map<String, Boolean> getSeats() { return seats; }

    public int getAvailableEconomy() {
        return (int) seats.entrySet().stream().filter(e -> e.getKey().startsWith("E") && !e.getValue()).count();
    }
    public int getAvailableBusiness() {
        return (int) seats.entrySet().stream().filter(e -> e.getKey().startsWith("B") && !e.getValue()).count();
    }

    public String getFlightNumber()  { return flightNumber; }
    public String getAirline()       { return airline; }
    public String getOrigin()        { return origin; }
    public String getDestination()   { return destination; }
    public String getDepartureTime() { return departureTime; }
    public String getArrivalTime()   { return arrivalTime; }
    public String getDuration()      { return duration; }
    public double getEconomyPrice()  { return economyPrice; }
    public double getBusinessPrice() { return businessPrice; }
}
