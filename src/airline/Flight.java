package airline;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Flight {
    private String flightNumber;
    private String airline;
    private String origin;
    private String destination;
    private String departureTime;
    private String arrivalTime;
    private String duration;
    private double baseEconomyPrice;
    private double baseBusinessPrice;

    private Map<String, Boolean> seats = new HashMap<>();
    private static final int BUSINESS_ROWS = 4;
    private static final int ECONOMY_ROWS = 20;

    private LocalDate flightDate = LocalDate.now();

    // Whether this leg is part of a round trip bundle
    private boolean isRoundTripLeg = false;

    // Round trip discount tiers — applied on top of dynamic pricing
    // The further out you book both legs, the bigger the bundle saving
    private static final double RT_DISCOUNT_LASTMINUTE = 0.03; // only 3% off if very close
    private static final double RT_DISCOUNT_SHORT      = 0.07; // 7% off within 7 days
    private static final double RT_DISCOUNT_MEDIUM     = 0.10; // 10% off within 30 days
    private static final double RT_DISCOUNT_ADVANCE    = 0.15; // 15% off beyond 30 days (best deal)

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
        this.baseEconomyPrice = economyPrice;
        this.baseBusinessPrice = businessPrice;
        initSeats();
    }

    public void setFlightDate(LocalDate date) {
        this.flightDate = date != null ? date : LocalDate.now();
    }

    public LocalDate getFlightDate() { return flightDate; }

    public void setRoundTripLeg(boolean isRoundTrip) {
        this.isRoundTripLeg = isRoundTrip;
    }

    public boolean isRoundTripLeg() { return isRoundTripLeg; }

    // ── Dynamic urgency multiplier (same as before) ──────────────────

    private double getUrgencyMultiplier() {
        long daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), flightDate);
        if      (daysUntil <= 1)  return 2.20;
        else if (daysUntil <= 3)  return 1.80;
        else if (daysUntil <= 7)  return 1.40;
        else if (daysUntil <= 14) return 1.20;
        else if (daysUntil <= 30) return 1.05;
        else                      return 1.00;
    }

    private double getDemandMultiplier() {
        int total = ECONOMY_ROWS * 6 + BUSINESS_ROWS * 4;
        long booked = seats.values().stream().filter(v -> v).count();
        double fill = (double) booked / total;
        if      (fill > 0.85) return 1.25;
        else if (fill > 0.70) return 1.15;
        else                  return 1.00;
    }

    private double getPriceMultiplier() {
        return getUrgencyMultiplier() * getDemandMultiplier();
    }

    /**
     * Round-trip discount: slides based on how far in advance the flight is.
     * The idea: airlines reward advance round-trip planners more than last-minute ones.
     */
    private double getRoundTripDiscount() {
        if (!isRoundTripLeg) return 0.0;
        long daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), flightDate);
        if      (daysUntil <= 3)  return RT_DISCOUNT_LASTMINUTE;
        else if (daysUntil <= 7)  return RT_DISCOUNT_SHORT;
        else if (daysUntil <= 30) return RT_DISCOUNT_MEDIUM;
        else                      return RT_DISCOUNT_ADVANCE;
    }

    private double rounded(double price) {
        return Math.round(price / 50.0) * 50.0;
    }

    public double getEconomyPrice() {
        double dynamic = baseEconomyPrice * getPriceMultiplier();
        double discount = dynamic * getRoundTripDiscount();
        return rounded(dynamic - discount);
    }

    public double getBusinessPrice() {
        double dynamic = baseBusinessPrice * getPriceMultiplier();
        double discount = dynamic * getRoundTripDiscount();
        return rounded(dynamic - discount);
    }

    public double getBaseEconomyPrice()  { return baseEconomyPrice; }
    public double getBaseBusinessPrice() { return baseBusinessPrice; }

    /**
     * How much is saved per seat vs one-way pricing for this leg.
     * Used by HomeScreen to display the "You save ₹X per seat" badge.
     */
    public double getEconomyRoundTripSaving() {
        if (!isRoundTripLeg) return 0;
        double oneWayPrice = rounded(baseEconomyPrice * getPriceMultiplier());
        return oneWayPrice - getEconomyPrice();
    }

    public double getBusinessRoundTripSaving() {
        if (!isRoundTripLeg) return 0;
        double oneWayPrice = rounded(baseBusinessPrice * getPriceMultiplier());
        return oneWayPrice - getBusinessPrice();
    }

    /** Round trip discount % as a readable string e.g. "15%" */
    public String getRoundTripDiscountLabel() {
        double pct = getRoundTripDiscount() * 100;
        return (int) pct + "%";
    }

    public String getPricingLabel() {
        long daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), flightDate);
        long booked = seats.values().stream().filter(v -> v).count();
        int total = ECONOMY_ROWS * 6 + BUSINESS_ROWS * 4;
        double fill = (double) booked / total;

        String tag;
        if      (daysUntil <= 1)  tag = "🔥 Last Minute";
        else if (daysUntil <= 3)  tag = "⚡ Filling Fast";
        else if (daysUntil <= 7)  tag = "📅 1 Week Away";
        else if (daysUntil <= 14) tag = "📅 2 Weeks Away";
        else if (daysUntil <= 30) tag = "🗓 Within Month";
        else                      tag = "✅ Best Price";

        if      (fill > 0.85) tag += " · 🔴 Almost Full";
        else if (fill > 0.70) tag += " · 🟠 Filling Up";

        if (isRoundTripLeg) tag += " · 🎫 RT -" + getRoundTripDiscountLabel();

        return tag;
    }

    // ── Seat operations ──────────────────────────────────────────────

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
    public Map<String, Boolean> getSeats()      { return seats; }

    public int getAvailableEconomy() {
        return (int) seats.entrySet().stream()
                .filter(e -> e.getKey().startsWith("E") && !e.getValue()).count();
    }
    public int getAvailableBusiness() {
        return (int) seats.entrySet().stream()
                .filter(e -> e.getKey().startsWith("B") && !e.getValue()).count();
    }

    // ── Getters ──────────────────────────────────────────────────────

    public String getFlightNumber()  { return flightNumber; }
    public String getAirline()       { return airline; }
    public String getOrigin()        { return origin; }
    public String getDestination()   { return destination; }
    public String getDepartureTime() { return departureTime; }
    public String getArrivalTime()   { return arrivalTime; }
    public String getDuration()      { return duration; }
}