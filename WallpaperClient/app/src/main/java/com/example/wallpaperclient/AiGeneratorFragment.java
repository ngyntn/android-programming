package com.example.wallpaperclient;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.wallpaperclient.api.AIRequest;
import com.example.wallpaperclient.api.ApiClient;
import com.example.wallpaperclient.api.ApiService;
import com.example.wallpaperclient.database.WallpaperEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AiGeneratorFragment extends Fragment {

    private EditText edtPrompt;
    private AppCompatButton btnSubmit;
    private LinearLayout layoutResult;
    private ImageView imgResult;

    // Holds the transient result before saving to DB
    private WallpaperEntity currentGeneratedImage = null;

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

        btnSubmit.setOnClickListener(v -> {
            String prompt = edtPrompt.getText().toString().trim();
            if (prompt.isEmpty()) {
                Toast.makeText(getContext(), "Please enter your idea!", Toast.LENGTH_SHORT).show();
                return;
            }
            performAiGeneration(prompt);
        });

        imgResult.setOnClickListener(v -> {
            if (currentGeneratedImage != null) {
                showDetailDialog(currentGeneratedImage);
            }
        });
    }

    private void performAiGeneration(String prompt) {
        Toast.makeText(getContext(), "Generating art... This may take ~10s", Toast.LENGTH_LONG).show();
        btnSubmit.setEnabled(false);
        btnSubmit.setText("Generating...");
        layoutResult.setVisibility(View.GONE);

        ApiService apiService = ApiClient.getApiService();
        Call<ResponseBody> call = apiService.generateAiImage(new AIRequest(prompt));

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {

                    // Persist raw stream to cache with a unique timestamp
                    File savedFile = saveImageToCache(response.body());

                    if (savedFile != null) {
                        layoutResult.setVisibility(View.VISIBLE);

                        // Force Glide to skip cache to ensure the new file is rendered
                        Glide.with(getContext())
                                .load(savedFile)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(imgResult);

                        currentGeneratedImage = new WallpaperEntity(
                                savedFile.getAbsolutePath(),
                                "AI",
                                System.currentTimeMillis()
                        );
                        currentGeneratedImage.aiPrompt = prompt;
                    } else {
                        Toast.makeText(getContext(), "Error saving image", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Generation failed. Try again!", Toast.LENGTH_SHORT).show();
                }

                btnSubmit.setEnabled(true);
                btnSubmit.setText("Submit");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                btnSubmit.setEnabled(true);
                btnSubmit.setText("Submit");
            }
        });
    }

    /**
     * Saves the ResponseBody stream to a temporary file in the app's cache directory.
     * Handles cache eviction for old AI artifacts.
     */
    private File saveImageToCache(ResponseBody body) {
        try {
            File cacheDir = getContext().getCacheDir();

            // Prune old generation artifacts to prevent cache bloat
            File[] files = cacheDir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.getName().startsWith("ai_gen_")) {
                        f.delete();
                    }
                }
            }

            // Create unique filename to bypass Glide caching issues
            String uniqueName = "ai_gen_" + System.currentTimeMillis() + ".png";
            File file = new File(cacheDir, uniqueName);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] buffer = new byte[4096];
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                int read;
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
                outputStream.flush();
                return file;
            } catch (Exception e) {
                return null;
            } finally {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            }
        } catch (Exception e) {
            return null;
        }
    }

    private void showDetailDialog(WallpaperEntity wallpaper) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_wallpaper_detail);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        ImageView imgDetail = dialog.findViewById(R.id.imgDetail);
        AppCompatButton btnSave = dialog.findViewById(R.id.btnSave);
        AppCompatButton btnSetWallpaper = dialog.findViewById(R.id.btnSetWallpaper);

        // Render preview without caching to reflect immediate changes
        Glide.with(this)
                .load(wallpaper.localPath)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imgDetail);

        btnSave.setOnClickListener(v -> {
            // Persist from Cache -> Storage -> DB
            WallpaperUtils.saveWallpaper(getContext(), wallpaper);
            dialog.dismiss();
        });

        btnSetWallpaper.setOnClickListener(v -> {
            WallpaperUtils.setWallpaper(getContext(), wallpaper);
        });

        dialog.show();
    }
}