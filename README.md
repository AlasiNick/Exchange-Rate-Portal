# Exchange-Rate-Portal
Homework for software developer position in SEB bank
Task: exchange rate portal.
 
Write a web application with such functionality:
1. Central bank exchange rates page. Exchange rates from the Bank of Lithuania are displayed here: https://www.lb.lt/webservices/FxRates/en/.
2. After selecting a specific currency, its exchange rate history is displayed (chart or table).
3. Currency calculator. The amount is entered, the currency is selected, the program displays the amount in foreign currency and the rate at which it was calculated.
 
-- Exchange rates should be automatically obtained every day (eg using job scheduling library like Quartz).
-- Initially, if no rates yet loaded - populate rates for last 90 days.
 
Tech. stack guidelines
 
Backend: any. Preferred Java/Kotlin.
Frontend: any. Preferred Angular.
Database: any open-source lightweight database like H2 for data storage.

