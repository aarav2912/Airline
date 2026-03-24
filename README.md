# SkyWings – Airline Booking System
### A JavaFX Desktop Application

---

## Features
- **User Registration & Login** with validation (email, phone, strong password)
- **Flight Search** by origin & destination (Indian routes – DEL, BOM, BLR, MAA, HYD, CCU, COK, AMD, PNQ, GOI, JAI, LKO)
- **Flight Cards** showing airline, times, prices (Economy & Business), seats available
- **Seat Selection UI** – Plane body layout like BookMyShow
  - Business Class: Rows 1–4, seats A B | C D
  - Economy Class: Rows 1–20, seats A B C | D E F
  - Green = Available, Red = Booked, Orange = Selected
- **Payment Screen** – Card / UPI / Net Banking (mock)
- **Boarding Pass / Confirmation** with barcode
- **My Bookings** – View all past bookings
- **AES Encryption** data model (same as reference project)

---

## Prerequisites
- **Java 11 or higher** (JDK)
- **JavaFX SDK 11+** (download from https://gluonhq.com/products/javafx/)

---

## Setup & Run

### Step 1 – Get JavaFX
Download JavaFX SDK from: https://gluonhq.com/products/javafx/

Extract the ZIP. Copy all `.jar` files from the `lib` folder into:
```
AirlineBookingSystem/lib/
```

### Step 2 – Run

**Linux / macOS:**
```bash
chmod +x run.sh
./run.sh
```

**Windows:**
Double-click `run.bat`

**Manual (any OS):**
```bash
# Compile
javac --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml \
      -d out $(find src -name "*.java")

# Run
java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml \
     -cp out airline.Main
```

---

## Demo Credentials
- **Email:** demo@fly.com
- **Password:** Demo@123

---

## Project Structure
```
AirlineBookingSystem/
├── src/
│   └── airline/
│       ├── Main.java              – Entry point
│       ├── LoginScreen.java       – Login UI
│       ├── RegisterScreen.java    – Registration UI
│       ├── HomeScreen.java        – Flight search dashboard
│       ├── SeatSelectionScreen.java – Plane body seat picker
│       ├── PaymentScreen.java     – Payment UI
│       ├── ConfirmationScreen.java – Boarding pass
│       ├── MyBookingsScreen.java  – Booking history
│       ├── User.java              – User model
│       ├── UserManager.java       – User storage
│       ├── Flight.java            – Flight + seat model
│       ├── FlightManager.java     – Flight data & search
│       └── Booking.java           – Booking model
├── lib/                           – Place JavaFX jars here
├── out/                           – Compiled classes (auto-created)
├── run.sh                         – Linux/Mac run script
├── run.bat                        – Windows run script
└── README.md
```

---

## Airlines & Routes
| Code | Airport |
|------|---------|
| DEL  | Delhi (IGI) |
| BOM  | Mumbai (CSMIA) |
| BLR  | Bengaluru (KIA) |
| MAA  | Chennai International |
| HYD  | Hyderabad (RGIA) |
| CCU  | Kolkata (NSCBI) |
| COK  | Kochi (Cochin International) |
| AMD  | Ahmedabad (SVPI) |
| PNQ  | Pune Airport |
| GOI  | Goa (Dabolim) |
| JAI  | Jaipur International |
| LKO  | Lucknow (CCSI) |

Airlines: Air India, IndiGo, SpiceJet, Vistara
