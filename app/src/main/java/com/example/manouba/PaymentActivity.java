package com.example.manouba;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaymentActivity extends AppCompatActivity {

    EditText cardNumberField, expiryField, cvvField, passengerNameField;
    RadioGroup cardTypeGroup;
    Button payNowButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        cardNumberField = findViewById(R.id.cardNumber);
        expiryField = findViewById(R.id.expiryDate);
        cvvField = findViewById(R.id.cvv);
        passengerNameField = findViewById(R.id.passengerName);
        cardTypeGroup = findViewById(R.id.cardTypeGroup);
        payNowButton = findViewById(R.id.payNowButton);

        payNowButton.setOnClickListener(v -> {
            String card = cardNumberField.getText().toString().trim();
            String expiry = expiryField.getText().toString().trim();
            String cvv = cvvField.getText().toString().trim();
            String name = passengerNameField.getText().toString().trim();

            int selectedId = cardTypeGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                // Corrected Toast usage
                Toast.makeText(PaymentActivity.this, "Please select a card type", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedCard = findViewById(selectedId);
            String cardType = selectedCard.getText().toString();

            if (TextUtils.isEmpty(card) || TextUtils.isEmpty(expiry) || TextUtils.isEmpty(cvv) || TextUtils.isEmpty(name)) {
                // Corrected Toast usage
                Toast.makeText(PaymentActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate card number (should be 16 digits)
            if (!isValidCardNumber(card)) {
                // Corrected Toast usage
                Toast.makeText(PaymentActivity.this, "Invalid card number. It should be 16 digits.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate expiry date (MM/YY format, not in the past)
            if (!isValidExpiryDate(expiry)) {
                // Corrected Toast usage
                Toast.makeText(PaymentActivity.this, "Invalid expiry date or card expired.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate CVV (3 or 4 digits)
            if (!isValidCVV(cvv, cardType)) {
                // Corrected Toast usage
                Toast.makeText(PaymentActivity.this, "Invalid CVV. It should be 3 digits for Visa/Mastercard or 4 digits for American Express.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Simulate payment
            Toast.makeText(PaymentActivity.this, "Paid with " + cardType + "! Ticket booked for " + name, Toast.LENGTH_LONG).show();
        });
    }

    // Validate Card Number (16 digits)
    private boolean isValidCardNumber(String cardNumber) {
        return cardNumber.length() == 16 && cardNumber.matches("[0-9]+");
    }

    // Validate Expiry Date (MM/YY format)
    private boolean isValidExpiryDate(String expiryDate) {
        // Regular expression to match MM/YY format
        Pattern pattern = Pattern.compile("(0[1-9]|1[0-2])/[0-9]{2}");
        Matcher matcher = pattern.matcher(expiryDate);
        if (!matcher.matches()) {
            return false;
        }

        // Check if the expiry date is not in the past
        String[] parts = expiryDate.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]);

        Calendar currentDate = Calendar.getInstance();
        int currentYear = currentDate.get(Calendar.YEAR) % 100; // Get last 2 digits of current year
        int currentMonth = currentDate.get(Calendar.MONTH) + 1;

        // If the card is expired
        if (year < currentYear || (year == currentYear && month < currentMonth)) {
            return false;
        }

        return true;
    }

    // Validate CVV (3 digits for Visa/Mastercard, 4 digits for American Express)
    private boolean isValidCVV(String cvv, String cardType) {
        if (cardType.equalsIgnoreCase("American Express")) {
            return cvv.length() == 4 && cvv.matches("[0-9]+");
        } else {
            return cvv.length() == 3 && cvv.matches("[0-9]+");
        }
    }
}
