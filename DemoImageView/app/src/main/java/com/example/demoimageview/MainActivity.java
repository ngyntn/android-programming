package com.example.demoimageview;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView txtView;
    ImageView imgView;

    // Danh sách ID của 7 nút bấm
    private final int[] listButtonIds = {
            R.id.btnCenter,
            R.id.btnCenterCrop,
            R.id.btnCenterInside,
            R.id.btnFitCenter,
            R.id.btnFitEnd,
            R.id.btnFitStart,
            R.id.btnFitXY
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Gọi hàm khởi tạo
        init();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void init() {
        txtView = findViewById(R.id.textView);
        imgView = findViewById(R.id.imageView);

        // Đặt ảnh mặc định (đảm bảo bạn có file donaltrump trong res/drawable)
        // Nếu lỗi ảnh, hãy thử đổi thành: R.drawable.ic_launcher_background
        imgView.setImageResource(R.drawable.donaltrump);

        // Gán sự kiện click cho toàn bộ nút trong danh sách
        for (int id : listButtonIds) {
            Button btnTemp = findViewById(id);
            if (btnTemp != null) { // Kiểm tra null để tránh crash nếu quên khai báo bên XML
                btnTemp.setOnClickListener(this);
            }
        }
    }

    @Override
    public void onClick(View v) {
        // Lấy ID của nút vừa bấm
        int id = v.getId();

        // Sử dụng if-else thay vì switch-case để tương thích tốt nhất với các bản Android Studio mới
        if (id == R.id.btnCenter) {
            imgView.setScaleType(ImageView.ScaleType.CENTER);
            txtView.setText("Kiểu: Center");

        } else if (id == R.id.btnCenterCrop) {
            imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            txtView.setText("Kiểu: Center Crop");

        } else if (id == R.id.btnCenterInside) {
            imgView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            txtView.setText("Kiểu: Center Inside");

        } else if (id == R.id.btnFitCenter) {
            imgView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            txtView.setText("Kiểu: Fit Center");

        } else if (id == R.id.btnFitEnd) {
            imgView.setScaleType(ImageView.ScaleType.FIT_END);
            txtView.setText("Kiểu: Fit End");

        } else if (id == R.id.btnFitStart) {
            // Mới thêm: Co dãn về góc trái trên
            imgView.setScaleType(ImageView.ScaleType.FIT_START);
            txtView.setText("Kiểu: Fit Start");

        } else if (id == R.id.btnFitXY) {
            // Mới thêm: Kéo dãn méo hình để lấp đầy
            imgView.setScaleType(ImageView.ScaleType.FIT_XY);
            txtView.setText("Kiểu: Fit XY");
        }
    }
}