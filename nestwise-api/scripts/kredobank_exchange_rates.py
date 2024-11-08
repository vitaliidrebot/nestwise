# Extracts exchange rates history for the Kredobank API and transforms it into SQL insert statement.
# Created with AI support.

import datetime
import requests

# Define the start date and end date
start_date = datetime.date(2022, 1, 1)
end_date = datetime.date.today()

# Define the API URL
api_url = "https://online.kredobank.com.ua/ibank/api/v1/individual/light/currencies/bank?currency=EUR,USD,PLN&date="

# Define the currencies to filter
currencies_to_filter = {"EUR": 978, "USD": 840, "PLN": 985}
currency_code_from = 980  # UAH
bank_id = 1  # Kredobank

# Open the file to write the SQL insert statements
with open("kredobank_exchange_rates.sql", "w") as file:
    file.write("INSERT INTO exchange_rates (bank_id, currency_code_from, currency_code_to, date, rate_buy, rate_sell) VALUES\n")

    # Iterate over each day from start_date to end_date
    current_date = start_date
    while current_date <= end_date:
        # Format the date as yyyyMMdd
        formatted_date = current_date.strftime("%Y%m%d")

        # Send the request to the API
        response = requests.get(api_url + formatted_date)
        data = response.json()

        # Check if the response contains exchange rates
        for rate in data:
            currency_code_to = currencies_to_filter.get(rate["currency"])
            if currency_code_to:
                rate_buy = rate.get("buyRate", 0)
                rate_sell = rate.get("sellRate", 0)

                # Write the SQL insert statement to the file
                file.write(f"({bank_id},{currency_code_from}, {currency_code_to}, '{current_date}', {rate_buy}, {rate_sell}),\n")

        print(f"Date: {current_date}")
        # Move to the next day
        current_date += datetime.timedelta(days=1)

    # Remove the last comma and add a semicolon
    file.seek(file.tell() - 3, 0)
    file.write(";\n")