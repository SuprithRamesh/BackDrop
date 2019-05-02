package com.example.backdrop.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.backdrop.R;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddCategoryActivity extends AppCompatActivity {
    @BindView(R.id.add_cat_et)
    EditText editText;
    @BindView(R.id.add_cat_img)
    ImageView imageView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    //a constant to track the file chooser intent
    private static final int PICK_IMAGE_REQUEST = 1;
    //a Uri object to store file path
    private Uri filePath;

    private DatabaseReference fCategoriesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        ButterKnife.bind(this);
        setUpToolbar();
        fCategoriesRef = FirebaseDatabase.getInstance().getReference().child("categories");

        imageView.setOnClickListener(view -> showFileChooser());
    }

    private void setUpToolbar() {
        Objects.requireNonNull(getSupportActionBar()).setTitle("Add category");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.cat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.add_cat_btn:
                add();
                break;
        }
        return true;
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);

                //Setting image to ImageView
                imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void add() {
        final String title = editText.getText().toString().trim();
        if (title.equals("") || filePath == null) return;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("adding category");
        progressDialog.show();

        final DatabaseReference newCat = fCategoriesRef.push();
        final String key = newCat.getKey();

        StorageReference mImgStorage = FirebaseStorage.getInstance().getReference();
        assert key != null;
        StorageReference catPath = mImgStorage.child("categories").child(key).child("thumb.jpg");

        catPath.putFile(filePath).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", key);
                map.put("title", title);
                map.put("thumb", catPath.getDownloadUrl());
                map.put("timestamp", ServerValue.TIMESTAMP);
                newCat.setValue(map).addOnCompleteListener(task1 -> {
                    progressDialog.dismiss();
                    if (task1.isSuccessful()) {
                        Toast.makeText(AddCategoryActivity.this, "Category added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddCategoryActivity.this, Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                progressDialog.dismiss();
                Toast.makeText(AddCategoryActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}









