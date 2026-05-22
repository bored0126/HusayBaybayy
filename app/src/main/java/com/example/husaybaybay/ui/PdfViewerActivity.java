package com.example.husaybaybay.ui;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.husaybaybay.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PdfViewerActivity extends AppCompatActivity {

    private RecyclerView rvPdfPages;
    private ImageButton btnBack;
    private PdfRenderer pdfRenderer;
    private ParcelFileDescriptor parcelFileDescriptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        rvPdfPages = findViewById(R.id.rvPdfPages);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        try {
            openRenderer();
            setupRecyclerView();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Hindi mabuksan ang dokumento.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void openRenderer() throws IOException {
        // Copy asset to a temporary file because PdfRenderer needs a FileDescriptor
        File tempFile = new File(getCacheDir(), "temp_manual.pdf");
        if (!tempFile.exists() || tempFile.length() == 0) {
            InputStream is = getAssets().open("Studies - HusayBaybay.pdf");
            FileOutputStream fos = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = is.read(buffer)) != -1) {
                fos.write(buffer, 0, read);
            }
            fos.close();
            is.close();
        }

        parcelFileDescriptor = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY);
        if (parcelFileDescriptor != null) {
            pdfRenderer = new PdfRenderer(parcelFileDescriptor);
        }
    }

    private void setupRecyclerView() {
        rvPdfPages.setLayoutManager(new LinearLayoutManager(this));
        rvPdfPages.setAdapter(new PdfPageAdapter());
    }

    @Override
    protected void onDestroy() {
        try {
            if (pdfRenderer != null) pdfRenderer.close();
            if (parcelFileDescriptor != null) parcelFileDescriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private class PdfPageAdapter extends RecyclerView.Adapter<PdfPageAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pdf_page, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            PdfRenderer.Page page = pdfRenderer.openPage(position);
            
            // Create a bitmap with the page's dimensions
            // Adjust scale if needed for better quality/performance
            Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            
            holder.ivPage.setImageBitmap(bitmap);
            page.close();
        }

        @Override
        public int getItemCount() {
            return pdfRenderer != null ? pdfRenderer.getPageCount() : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivPage;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                ivPage = (ImageView) itemView;
            }
        }
    }
}
