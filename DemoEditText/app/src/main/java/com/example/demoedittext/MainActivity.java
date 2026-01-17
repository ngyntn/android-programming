package com.example.demoedittext;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    EditText edtText;
    Button btnClick;
    TextView txtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Init code.
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // If basic then ignore it.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Map Id Interface.
        edtText = findViewById(R.id.editTextText);
        btnClick = findViewById(R.id.button);
        txtView = findViewById(R.id.textView);

        btnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtText.getText().toString();
                System.out.println("Get name from input text: " + name);

                // Display on TextView.
                String displayText = "Welcome " + name + ", who is a number one handsome man in the world";
                txtView.setText(displayText);
            }
        });

    }
}