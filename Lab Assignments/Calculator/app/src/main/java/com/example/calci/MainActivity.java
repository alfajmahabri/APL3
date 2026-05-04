package com.example.calci;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvDisplay;
    private String currentInput = "";
    private double firstOperand = Double.NaN;
    private String operator = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvDisplay = findViewById(R.id.tvDisplay);
        setupListeners();
    }

    private void setupListeners() {
        int[] numberIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot
        };

        View.OnClickListener numberListener = v -> {
            Button b = (Button) v;
            String text = b.getText().toString();
            if (text.equals(".") && currentInput.contains(".")) return;
            currentInput += text;
            tvDisplay.setText(currentInput);
        };

        for (int id : numberIds) {
            findViewById(id).setOnClickListener(numberListener);
        }

        int[] operatorIds = {
                R.id.btnPlus, R.id.btnMinus, R.id.btnMultiply, R.id.btnDivide
        };

        View.OnClickListener operatorListener = v -> {
            if (!currentInput.isEmpty()) {
                firstOperand = Double.parseDouble(currentInput);
                operator = ((Button) v).getText().toString();
                currentInput = "";
            }
        };

        for (int id : operatorIds) {
            findViewById(id).setOnClickListener(operatorListener);
        }

        findViewById(R.id.btnEquals).setOnClickListener(v -> calculate());
        findViewById(R.id.btnClear).setOnClickListener(v -> clear());
    }

    private void calculate() {
        if (!Double.isNaN(firstOperand) && !currentInput.isEmpty()) {
            double secondOperand = Double.parseDouble(currentInput);
            double result = 0;

            switch (operator) {
                case "+": result = firstOperand + secondOperand; break;
                case "-": result = firstOperand - secondOperand; break;
                case "*": result = firstOperand * secondOperand; break;
                case "/":
                    if (secondOperand != 0) {
                        result = firstOperand / secondOperand;
                    } else {
                        tvDisplay.setText("Error");
                        resetState();
                        return;
                    }
                    break;
            }

            String resultStr;
            if (result == (long) result) {
                resultStr = String.format(Locale.getDefault(), "%d", (long) result);
            } else {
                resultStr = String.format(Locale.getDefault(), "%s", result);
            }

            tvDisplay.setText(resultStr);
            currentInput = resultStr;
            firstOperand = Double.NaN;
            operator = "";
        }
    }

    private void clear() {
        resetState();
        tvDisplay.setText("0");
    }

    private void resetState() {
        currentInput = "";
        firstOperand = Double.NaN;
        operator = "";
    }
}