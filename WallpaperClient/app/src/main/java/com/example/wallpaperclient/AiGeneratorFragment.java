package com.example.wallpaperclient;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

public class AiGeneratorFragment extends Fragment {

    private EditText edtPrompt;
    private AppCompatButton btnSubmit;
    private LinearLayout layoutResult;
    private ImageView imgResult;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ai_generator, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edtPrompt = view.findViewById(R.id.edtPrompt);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        layoutResult = view.findViewById(R.id.layoutResult);
        imgResult = view.findViewById(R.id.imgResult);

        // Sự kiện nút Submit
        btnSubmit.setOnClickListener(v -> {
            String prompt = edtPrompt.getText().toString().trim();
            if (prompt.isEmpty()) {
                Toast.makeText(getContext(), "Please enter your idea!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Giả lập Loading (Sau này sẽ gọi API Python ở đây)
            Toast.makeText(getContext(), "Generating art...", Toast.LENGTH_SHORT).show();
            btnSubmit.setEnabled(false); // Khóa nút bấm
            btnSubmit.setText("Generating...");

            new Handler().postDelayed(() -> {
                // Sau 2 giây thì hiện kết quả
                layoutResult.setVisibility(View.VISIBLE);

                // Ở đây giả lập set 1 ảnh khác làm kết quả (dùng lại img_demo hoặc ảnh khác)
                imgResult.setImageResource(R.drawable.img_demo);

                btnSubmit.setEnabled(true);
                btnSubmit.setText("Submit");

                // Cuộn xuống xem kết quả (Optional)
            }, 2000);
        });

        // Sự kiện click vào ảnh kết quả để xem chi tiết
        imgResult.setOnClickListener(v -> {
            // Dùng lại resource ID của ảnh demo, sau này là URL thật
            showDetailDialog(R.drawable.img_demo);
        });
    }

    // Hàm hiển thị Dialog chi tiết (Copy y hệt từ HomeFragment)
    private void showDetailDialog(int resourceId) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_wallpaper_detail);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        ImageView imgDetail = dialog.findViewById(R.id.imgDetail);
        imgDetail.setImageResource(resourceId);

        // Xử lý nút Save / Set Wallpaper nếu cần
        // ...

        dialog.show();
    }
}