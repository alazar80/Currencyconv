package com.example.currencyconv;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String API_KEY = "73c4c90ecb987201155a4189";

    AutoCompleteTextView autoFrom, autoTo;
    EditText etAmount;
    Button btnConvert;
    TextView tvResult;
    RequestQueue queue;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = Volley.newRequestQueue(this);

        autoFrom  = findViewById(R.id.autoFrom);
        autoTo    = findViewById(R.id.autoTo);
        etAmount  = findViewById(R.id.etAmount);
        btnConvert= findViewById(R.id.btnConvert);
        tvResult  = findViewById(R.id.tvResult);

        List<String> codes = loadCurrencyCodes();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, codes);
        autoFrom.setAdapter(adapter);
        autoTo.setAdapter(adapter);
        autoFrom.setText("USD", false);
        autoTo.setText("EUR", false);

        btnConvert.setOnClickListener(v -> {
            String from = autoFrom.getText().toString().trim();
            String to   = autoTo  .getText().toString().trim();
            String amt  = etAmount.getText().toString().trim();
            if (from.isEmpty()||to.isEmpty()||amt.isEmpty()) {
                tvResult.setText("Fill all fields");
            } else convert(from, to, amt);
        });
    }

    private List<String> loadCurrencyCodes() {
        Set<Currency> set = Currency.getAvailableCurrencies();
        List<String> list = new ArrayList<>();
        for (Currency c : set) list.add(c.getCurrencyCode());
        Collections.sort(list);
        return list;
    }

    private void convert(String from, String to, String amount) {
        try {
            String url = "https://v6.exchangerate-api.com/v6/"
                    + API_KEY + "/pair/"
                    + URLEncoder.encode(from,"UTF-8") + "/"
                    + URLEncoder.encode(to,  "UTF-8") + "/"
                    + URLEncoder.encode(amount,"UTF-8");

            queue.add(new StringRequest(Request.Method.GET, url,
                    resp -> {
                        try {
                            JSONObject o = new JSONObject(resp);
                            if ("success".equals(o.getString("result"))) {
                                double r = o.getDouble("conversion_result");
                                tvResult.setText(
                                        String.format("%s %s = %.2f %s",
                                                amount, from, r, to)
                                );
                            } else tvResult.setText("Error");
                        } catch(Exception e){
                            tvResult.setText("Parse error");
                        }
                    },
                    err -> tvResult.setText("Request failed")
            ));
        } catch (Exception e) {
            tvResult.setText("Invalid input");
        }
    }
}
