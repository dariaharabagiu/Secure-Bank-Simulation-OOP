# Proiect_Etapa2_POO

## **Banking Application - Enhanced Version**

This project is an advanced banking application designed to manage user accounts, execute transactions, and generate 
comprehensive reports. The system is built with scalability, flexibility, and maintainability in mind, leveraging core 
OOP principles, design patterns, and JSON-based input/output for efficient operations.

---

## **Enhancements in Etapa 2**

In the second phase of the project, several improvements were made to enhance the system's efficiency and maintainability:

- Implementation of new design patterns, including **Singleton**, and **Strategy**, to improve modularity and code organization.
- Improved handling of cashback calculations using the **Strategy Pattern**.
- Introduction of a **Singleton Pattern** to centralize configuration constants.
- Enhanced transaction processing logic with optimized error handling and validations.

---

## **Structure and Design**

### **Package Hierarchy**

- **org.poo.bank**: Core domain classes such as `Account`, `SavingsAccount`, `BusinessAccount`,
`Transaction`, `User`, and `Card`.
- **org.poo.bank.commands**: Encapsulates business logic for operations like `AddAccount`, `PayOnline`, `SplitPayment`, and `Report`.
- **org.poo.bank.commission**: Implements commission strategies using the **Strategy Pattern**, supporting flexible commission calculations.
- **org.poo.utils**: Utility classes like `ExchangeRate` for currency conversions and `Constants` 
(implemented as a **Singleton**) to manage application-wide constants.

---

### **Class Hierarchy and Interactions**

#### **User**

- Represents a user with personal information, accounts, cards, and transaction history.
- Interacts with `Account` and `Transaction` to manage operations efficiently.
- Provides eligibility checks for specific operations (e.g., age verification for withdrawals).

#### **Account**

- Base class with common functionality for all account types.
- `SavingsAccount` extends `Account`, adding specific behaviors such as interest accumulation.
- `BusinessAccount` extends `Account` to handle business-related operations, such as managing associates and business reports.

#### **Transaction**

- Represents various banking operations such as payments, transfers, and errors.
- Maintain a detailed transaction history.

#### **Command Interface**

- Provides a uniform way to execute different operations.
- Each command (e.g., `SplitPayment`, `PayOnline`) follows a standard structure and enhances extensibility.

---

## **Features**

### **Account Management**

- Create and manage different types of accounts (`classic`, `savings`, `business`).
- Link multiple accounts to a user profile.
- Handle balance updates with currency conversion support.

### **Transaction Handling**

- Execute payments (`payOnline`, `sendMoney`) with real-time currency conversion.
- Implement cashback strategies based on user plans and spending patterns.
- Track split payments with error handling for insufficient funds and partial approvals.

### **Reports**

- Generate detailed reports filtered by time range, displaying spending patterns and categorized transactions.
- Provide insights into merchant-based spending through structured analysis.

### **Interest and Commission Handling**

- Apply dynamic interest rates for savings accounts.
- Calculate commissions based on the user's plan using the **Strategy Pattern**, ensuring modular commission policies.

---

## **Design Patterns and Principles**

### **Singleton Pattern**

- Applied to the `Constants` class to ensure a single instance is used throughout the application.
- Centralizes constant values, improving maintainability and reducing redundancy.

### **Strategy Pattern**

- Used for calculating commissions dynamically based on user account plans.
- Enables flexible and extendable commission calculations without modifying existing logic.
- Example strategies: `StudentGoldCommissionStrategy`, `SilverCommissionStrategy`, `StandardCommissionStrategy`.

### **Factory Pattern**

- The `CommandFactory` dynamically creates and returns appropriate commands based on input.
- Centralizes command creation, reducing duplication and increasing flexibility for adding new commands.

### **Command Pattern**

- Encapsulates requests as objects to support dynamic operation execution.
- Allows future extensions without modifying the core structure.

---

**Harabagiu Daria Maria, 325CD**

