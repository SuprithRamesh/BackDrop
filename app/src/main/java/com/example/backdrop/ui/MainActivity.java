package com.example.backdrop.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.backdrop.model.Category;
import com.example.backdrop.util.NavigationUtil;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.example.backdrop.R;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.list)
    RecyclerView list;
    LinearLayoutManager linearLayoutManager;
    FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupList();
    }

    private void setupList() {
        linearLayoutManager = new LinearLayoutManager(this);

        list.setLayoutManager(linearLayoutManager);
        list.setHasFixedSize(true);

        fetch();
    }

    private void fetch() {
        Query query = FirebaseDatabase.getInstance()
                .getReference().child("categories");

        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(query, snapshot -> new Category(Objects.requireNonNull(snapshot.child("id").getValue()).toString(),
                        Objects.requireNonNull(snapshot.child("thumb").getValue()).toString(),
                        Objects.requireNonNull(snapshot.child("title").getValue()).toString())).build();

        adapter = new FirebaseRecyclerAdapter<Category, ViewHolder>(options) {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_category_layout, parent, false);
                return new ViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull Category model) {
                holder.textMain.setText(model.getCatTitle());

                Picasso.with(getApplicationContext())
                        .load(model.getCatImage())
                        .into(holder.img);

                holder.mainBtn.setOnClickListener(view -> Toast.makeText(MainActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show());
            }
        };

        list.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.add_cat) {
            NavigationUtil.goToAddCat(this);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cat_main_card)
        CardView mainBtn;
        @BindView(R.id.cat_main_text)
        TextView textMain;
        @BindView(R.id.cat_main_bg)
        AppCompatImageView img;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

