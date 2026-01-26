package com.example.demolistview; // Đổi tên package theo dự án của bạn

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Khai báo biến
    EditText editText;
    Button btnAdd, btnUpdate;
    ListView listView;

    // Biến lưu trữ dữ liệu
    ArrayList<String> mList;
    ArrayAdapter<String> mAdapter;

    // Biến lưu vị trí item đang được chọn để cập nhật (-1 là chưa chọn gì)
    int viTriDangChon = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Ánh xạ View
        editText = findViewById(R.id.editText);
        btnAdd = findViewById(R.id.btnAdd);
        btnUpdate = findViewById(R.id.btnUpdate);
        listView = findViewById(R.id.listView);

        // 2. Khởi tạo dữ liệu mẫu giống hình ảnh
        mList = new ArrayList<>();
        mList.add("Android");
        mList.add("Python hsgahsgdhasdas");
        mList.add("Ajax");
        mList.add("C++");
        mList.add("Ruby");
        mList.add("Rails");

        // 3. Tạo Adapter và gán vào ListView
        // android.R.layout.simple_list_item_1 là giao diện dòng có sẵn của Android
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);
        listView.setAdapter(mAdapter);

        // 4. Bắt sự kiện click vào một dòng trong ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Lấy nội dung dòng vừa bấm đưa lên EditText
                editText.setText(mList.get(position));

                // Lưu lại vị trí để tí nữa nút Cập nhật biết sửa dòng nào
                viTriDangChon = position;
            }
        });

        // 5. Xử lý nút THÊM
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noiDung = editText.getText().toString();
                if (noiDung.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập nội dung", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Thêm vào danh sách
                mList.add(noiDung);
                // Cập nhật giao diện
                mAdapter.notifyDataSetChanged();
                // Xóa trắng ô nhập
                editText.setText("");
                viTriDangChon = -1; // Reset vị trí chọn
            }
        });

        // 6. Xử lý nút CẬP NHẬT
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String noiDungMoi = editText.getText().toString();

                if (viTriDangChon == -1) {
                    Toast.makeText(MainActivity.this, "Chưa chọn dòng nào để cập nhật", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (noiDungMoi.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Nội dung không được để trống", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Sửa dữ liệu tại vị trí đã chọn
                mList.set(viTriDangChon, noiDungMoi);

                // Cập nhật giao diện
                mAdapter.notifyDataSetChanged();

                // Reset sau khi sửa xong
                editText.setText("");
                viTriDangChon = -1;
                Toast.makeText(MainActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            }
        });
    }
}