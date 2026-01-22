package com.example.demococaro;


import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    int Winner = -1; // -1: chưa có, 1: Player 1, 2: Player 2, 0: Hòa
    int startGame = 0; // 0: chưa bắt đầu, 1: đang chơi
    Button btPlayAgain, bt1, bt2, bt3, bt4, bt5, bt6, bt7, bt8, bt9;
    TextView txtShowresult;

    // Lưu các ô đã đánh
    ArrayList<Integer> Player1 = new ArrayList<Integer>();
    ArrayList<Integer> Player2 = new ArrayList<Integer>();
    int ActivePlayer = 1; // 1: Player 1 đi trước

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        AnhXa(); // Ánh xạ view

        btPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startGame == 1) {
                    PlayAgain();
                    startGame = 0;
                    btPlayAgain.setText("Bắt đầu");
                } else {
                    PlayAgain(); // Reset bàn cờ trước khi bắt đầu
                    startGame = 1;
                    btPlayAgain.setText("Chơi lại");
                }
            }
        });
    }

    void PlayAgain() {
        Player1.clear();
        Player2.clear();
        Winner = -1;
        ActivePlayer = 1;

        // Reset text và màu sắc về mặc định
        resetButton(bt1); resetButton(bt2); resetButton(bt3);
        resetButton(bt4); resetButton(bt5); resetButton(bt6);
        resetButton(bt7); resetButton(bt8); resetButton(bt9);

        txtShowresult.setVisibility(View.INVISIBLE);
    }

    // Hàm phụ trợ để reset nút gọn hơn
    void resetButton(Button btn) {
        btn.setText("");
        btn.setBackgroundColor(Color.rgb(188, 185, 185)); // Màu xám gốc
        btn.setEnabled(true);
    }

    void AnhXa() {
        btPlayAgain = findViewById(R.id.btPlayAgain);
        txtShowresult = findViewById(R.id.txtShowresult);
        bt1 = findViewById(R.id.bt1);
        bt2 = findViewById(R.id.bt2);
        bt3 = findViewById(R.id.bt3);
        bt4 = findViewById(R.id.bt4);
        bt5 = findViewById(R.id.bt5);
        bt6 = findViewById(R.id.bt6);
        bt7 = findViewById(R.id.bt7);
        bt8 = findViewById(R.id.bt8);
        bt9 = findViewById(R.id.bt9);
    }

    public void btClick(View view) {
        // Nếu chưa bấm Bắt đầu hoặc đã có người thắng thì thoát
        if (startGame == 0 || Winner != -1) return;

        Button btSelected = (Button) view;
        int id = btSelected.getId(); // Lấy ID của nút được bấm
        int CellID = 0;

        // Thay thế switch bằng if-else
        if (id == R.id.bt1) {
            CellID = 1;
        } else if (id == R.id.bt2) {
            CellID = 2;
        } else if (id == R.id.bt3) {
            CellID = 3;
        } else if (id == R.id.bt4) {
            CellID = 4;
        } else if (id == R.id.bt5) {
            CellID = 5;
        } else if (id == R.id.bt6) {
            CellID = 6;
        } else if (id == R.id.bt7) {
            CellID = 7;
        } else if (id == R.id.bt8) {
            CellID = 8;
        } else if (id == R.id.bt9) {
            CellID = 9;
        }

        // Gọi hàm xử lý game
        if (CellID != 0) {
            PlayGame(CellID, btSelected);
        }
    }

    void PlayGame(int CellID, Button btSelected) {
        // Kiểm tra ô đã được đánh chưa
        if (Player1.contains(CellID) || Player2.contains(CellID)) {
            return;
        }

        if (ActivePlayer == 1) {
            btSelected.setText("X");
            btSelected.setBackgroundColor(Color.GREEN);
            btSelected.setTextColor(Color.RED);
            Player1.add(CellID);
            ActivePlayer = 2;
        } else if (ActivePlayer == 2) {
            btSelected.setText("O");
            btSelected.setBackgroundColor(Color.BLUE);
            btSelected.setTextColor(Color.WHITE);
            Player2.add(CellID);
            ActivePlayer = 1;
        }

        CheckWinner();

        // Hiển thị kết quả
        if (Winner == 1) {
            txtShowresult.setVisibility(View.VISIBLE);
            txtShowresult.setText("Player 1 thắng !");
        } else if (Winner == 2) {
            txtShowresult.setVisibility(View.VISIBLE);
            txtShowresult.setText("Player 2 thắng !");
        } else if (Winner == 0) {
            txtShowresult.setVisibility(View.VISIBLE);
            txtShowresult.setText("Hòa!");
        }
    }

    void CheckWinner() {
        // --- Dòng ngang ---
        // Dòng 1
        if (Player1.contains(1) && Player1.contains(2) && Player1.contains(3)) Winner = 1;
        if (Player2.contains(1) && Player2.contains(2) && Player2.contains(3)) Winner = 2;

        // Dòng 2
        if (Player1.contains(4) && Player1.contains(5) && Player1.contains(6)) Winner = 1;
        if (Player2.contains(4) && Player2.contains(5) && Player2.contains(6)) Winner = 2;

        // Dòng 3
        if (Player1.contains(7) && Player1.contains(8) && Player1.contains(9)) Winner = 1;
        if (Player2.contains(7) && Player2.contains(8) && Player2.contains(9)) Winner = 2;

        // --- Cột dọc ---
        // Cột 1
        if (Player1.contains(1) && Player1.contains(4) && Player1.contains(7)) Winner = 1;
        if (Player2.contains(1) && Player2.contains(4) && Player2.contains(7)) Winner = 2;

        // Cột 2
        if (Player1.contains(2) && Player1.contains(5) && Player1.contains(8)) Winner = 1;
        if (Player2.contains(2) && Player2.contains(5) && Player2.contains(8)) Winner = 2;

        // Cột 3 (Đã sửa lỗi logic so với PDF gốc)
        if (Player1.contains(3) && Player1.contains(6) && Player1.contains(9)) Winner = 1;
        if (Player2.contains(3) && Player2.contains(6) && Player2.contains(9)) Winner = 2;

        // --- Đường chéo ---
        // Chéo 1 (1-5-9)
        if (Player1.contains(1) && Player1.contains(5) && Player1.contains(9)) Winner = 1;
        if (Player2.contains(1) && Player2.contains(5) && Player2.contains(9)) Winner = 2;

        // Chéo 2 (3-5-7)
        if (Player1.contains(3) && Player1.contains(5) && Player1.contains(7)) Winner = 1;
        if (Player2.contains(3) && Player2.contains(5) && Player2.contains(7)) Winner = 2;

        // --- Kiểm tra Hòa ---
        // Nếu đã đánh hết 9 ô mà chưa có Winner
        if (Winner == -1 && (Player1.size() + Player2.size() == 9)) {
            Winner = 0;
        }
    }
}