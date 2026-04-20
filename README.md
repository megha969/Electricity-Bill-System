# ⚡ Electricity Billing Management System

A full-featured, console-based **Electricity Billing System** built in Java. Manage customers, record meter readings, generate itemized bills using slab-based tariffs, process payments, and view reports — all from the command line.

![Java](https://img.shields.io/badge/Java-17-orange?logo=java)
![Maven](https://img.shields.io/badge/Maven-3.8+-blue?logo=apachemaven)
![License](https://img.shields.io/badge/License-MIT-green)
![CI](https://github.com/YOUR_USERNAME/electricity-billing-system/actions/workflows/ci.yml/badge.svg)

---

## ✨ Features

| Feature | Description |
|---|---|
| 👤 Customer Management | Add, search, view, update customers |
| 🔢 Meter Reading | Record readings, auto-compute units consumed |
| 📄 Bill Generation | Slab-rate tariff billing with full charge breakdown |
| 💳 Payment Processing | Full/partial payment with multiple payment modes |
| 📊 Reports | Dashboard, customer ledger, unpaid bills report |
| 📋 Tariff Chart | View all slab rates (Domestic/Commercial/Industrial) |
| ✅ Unit Tests | 20 JUnit 5 tests covering all layers |
| 🚀 GitHub Actions CI | Auto build & test on every push |

---

## 📁 Project Structure

```
ElectricityBilling/
├── src/
│   ├── main/java/com/ebilling/
│   │   ├── Main.java
│   │   ├── model/
│   │   │   ├── Customer.java        # Customer entity
│   │   │   ├── MeterReading.java    # Meter reading entry
│   │   │   ├── Bill.java            # Bill with full breakdown
│   │   │   ├── Payment.java         # Payment transaction
│   │   │   └── TariffRate.java      # Slab tariff definition
│   │   ├── repository/
│   │   │   ├── CustomerRepository.java
│   │   │   ├── MeterReadingRepository.java
│   │   │   ├── BillRepository.java
│   │   │   └── PaymentRepository.java
│   │   ├── service/
│   │   │   ├── BillingService.java  # Core business logic
│   │   │   └── TariffService.java   # Slab-rate calculations
│   │   ├── ui/
│   │   │   └── ConsoleUI.java       # Full menu-driven UI
│   │   ├── util/
│   │   │   ├── BillPrinter.java     # Formatted bill receipt
│   │   │   └── ConsoleColors.java   # ANSI colors
│   │   └── report/
│   │       └── ReportGenerator.java # Reports & dashboard
│   └── test/java/com/ebilling/
│       └── ElectricityBillingTest.java  # 20 unit tests
├── .github/workflows/ci.yml
├── pom.xml
├── .gitignore
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+

### Run

```bash
git clone https://github.com/YOUR_USERNAME/electricity-billing-system.git
cd electricity-billing-system

# Run directly
mvn compile exec:java

# Or build fat JAR and run
mvn package
java -jar target/ebilling.jar
```

### Run Tests

```bash
mvn test
```

---

## ⚡ Tariff Structure

### Domestic
| Slab | Units | Rate/Unit |
|------|-------|-----------|
| Slab 1 | 0 – 100 | ₹3.50 |
| Slab 2 | 101 – 200 | ₹5.00 |
| Slab 3 | 201 – 300 | ₹6.50 |
| Slab 4 | 301+ | ₹8.00 |

### Commercial
| Slab | Units | Rate/Unit |
|------|-------|-----------|
| Slab 1 | 0 – 200 | ₹6.00 |
| Slab 2 | 201 – 500 | ₹7.50 |
| Slab 3 | 501+ | ₹9.00 |

### Industrial
| Slab | Units | Rate/Unit |
|------|-------|-----------|
| Slab 1 | 0 – 500 | ₹5.50 |
| Slab 2 | 501 – 2000 | ₹7.00 |
| Slab 3 | 2001+ | ₹8.50 |

> Bills also include: Fixed Charge, Fuel Surcharge (6%), Electricity Duty (5%), Meter Rent (₹25), and 18% GST.

---

## 📄 Sample Bill Receipt

```
╔════════════════════════════════════════════════════╗
║       ⚡  ELECTRICITY BILL RECEIPT  ⚡             ║
╚════════════════════════════════════════════════════╝
  Bill No:              BILL00001
  Customer ID:          C1001
  Name:                 Ravi Kumar
  Connection:           Domestic
  Units Consumed:       350.00 kWh
  ────────────────────────────────────────────────────
  Energy Charge:        ₹       1,975.00
  Fixed Charge:         ₹         150.00
  Fuel Surcharge:       ₹         118.50
  Electricity Duty:     ₹          98.75
  Meter Rent:           ₹          25.00
  ────────────────────────────────────────────────────
  Sub Total:            ₹       2,367.25
  Tax (18%):            ₹         426.10
  ════════════════════════════════════════════════════
  TOTAL AMOUNT DUE      ₹       2,793.35
  ════════════════════════════════════════════════════
  Status:               UNPAID
```

---

## 🧪 Test Coverage

20 unit tests covering:
- Tariff slab calculations (domestic, commercial, industrial)
- Progressive billing across multiple slabs
- Customer CRUD and validation
- Meter reading validation (reverse reading detection)
- Bill generation and charge calculations
- Full and partial payment processing
- Exception handling for invalid inputs

---

## 🔧 Extending the System

### Add a New Tariff Slab
Edit `TariffService.java`:
```java
tariffs.put(ConnectionType.DOMESTIC.name(), Arrays.asList(
    new TariffRate("DOMESTIC", 0, 100, 3.50, 50.0),
    // add more slabs...
));
```

### Add a New Report
Extend `ReportGenerator.java` and add a menu option in `ConsoleUI.java`.

---

## 🤝 Contributing

1. Fork the repo
2. Create a branch: `git checkout -b feature/my-feature`
3. Commit: `git commit -m 'Add my feature'`
4. Push: `git push origin feature/my-feature`
5. Open a Pull Request

---

## 📄 License

MIT License

---

## 👤 Author

**Your Name**
GitHub: [@YOUR_USERNAME](https://github.com/YOUR_USERNAME)

---

## 📝 GitHub Description (copy-paste ready)

> ⚡ Console-based Electricity Billing System in Java | Customer management, slab-rate billing, payment processing & reports | Maven + JUnit 5
