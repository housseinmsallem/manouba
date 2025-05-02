package com.example.manouba;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.content.Context.MODE_PRIVATE;

public class TicketFragment extends Fragment {

    private int ticketCount = 0;
    private final int maxTickets = 10;
    private final double pricePerTicket = 1.0;

    private TextView ticketCountText, totalPriceText;
    private Button increaseBtn, decreaseBtn, payBtn;

    public TicketFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket, container, false);

        ticketCountText = view.findViewById(R.id.tv_ticket_count);
        totalPriceText = view.findViewById(R.id.tv_total_price);
        increaseBtn = view.findViewById(R.id.btn_increase);
        decreaseBtn = view.findViewById(R.id.btn_decrease);
        payBtn = view.findViewById(R.id.btn_pay);

        updateUI();

        increaseBtn.setOnClickListener(v -> {
            if (ticketCount < maxTickets) {
                ticketCount++;
                updateUI();
            }
        });

        decreaseBtn.setOnClickListener(v -> {
            if (ticketCount > 0) {
                ticketCount--;
                updateUI();
            }
        });

        payBtn.setOnClickListener(v -> {
            // User needs to log in to proceed with ticket purchase
            Toast.makeText(getContext(), "You need to log in to buy a ticket", Toast.LENGTH_SHORT).show();

            // Save ticket data before login
            SharedPreferences prefs = getActivity().getSharedPreferences("ticketData", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("ticketCount", ticketCount);
            editor.putFloat("totalPrice", (float) (ticketCount * pricePerTicket));
            editor.apply();

            // Redirect to LoginActivity (always)
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void updateUI() {
        ticketCountText.setText(String.valueOf(ticketCount));
        totalPriceText.setText("Total: " + (ticketCount * pricePerTicket) + " TND");
    }
}
