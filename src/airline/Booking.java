package airline;

import java.util.List;

public class Booking {

    private static int counter = 1000;

    private String bookingId;
    private Flight flight;
    private List<String> seatIds;
    private String seatClass;
    private double pricePaid;
    private String passengerName;
    private String passengerEmail;

    private boolean cancelled = false;

    public Booking(Flight flight, List<String> seatIds, String seatClass, double pricePaid,
                   String passengerName, String passengerEmail) {

        this.bookingId = "BK" + (++counter);
        this.flight = flight;
        this.seatIds = seatIds;
        this.seatClass = seatClass;
        this.pricePaid = pricePaid;
        this.passengerName = passengerName;
        this.passengerEmail = passengerEmail;
    }

    public void cancel() {

        if (!cancelled) {

            for (String seatId : seatIds) {
                flight.releaseSeat(seatId);
            }

            flight.cancelSeats(seatIds);

            cancelled = true;
        }
    }


    public boolean isCancelled()       { return cancelled; }

    public String getBookingId()       { return bookingId; }

    public Flight getFlight()          { return flight; }

    public List<String> getSeatIds()   { return seatIds; }

    public String getSeatId()          { return String.join(", ", seatIds); }

    public String getSeatClass()       { return seatClass; }

    public double getPricePaid()       { return pricePaid; }

    public String getPassengerName()   { return passengerName; }

    public String getPassengerEmail()  { return passengerEmail; }

    public int getSeatCount()          { return seatIds.size(); }
}
