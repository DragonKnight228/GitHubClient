package com.example.githubclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.kohsuke.github.GHCommitSearchBuilder;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainScreenActivity extends AppCompatActivity {

    String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        token = getIntent().getStringExtra("token");
        AsyncTask asyncTask = new AsyncTask();
        AdapterAsyncTask adapterAsyncTask = new AdapterAsyncTask();
        asyncTask.execute();
        adapterAsyncTask.execute();
        try {
            List<GHRepository> list = asyncTask.get();
            ReposArrayAdapter adapter = adapterAsyncTask.get();
            adapter.setRepositories(list, token);
            LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            RecyclerView recyclerView = findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(adapter);

        } catch (ExecutionException | InterruptedException e) {
            Toast.makeText(MainScreenActivity.this, "Ошибка вывода репозиториев", Toast.LENGTH_LONG).show();
        }
    }

    public class AdapterAsyncTask extends android.os.AsyncTask<Void, Void, ReposArrayAdapter> {

        @Override
        public ReposArrayAdapter doInBackground(Void... voids) {
            ReposArrayAdapter adapter = new ReposArrayAdapter();
            return adapter;
        }

        @Override
        protected void onPostExecute(ReposArrayAdapter adapter) {
            super.onPostExecute(adapter);
        }
    }

     public class AsyncTask extends android.os.AsyncTask<Void, Void, List<GHRepository>> {

        @Override
        public List<GHRepository> doInBackground(Void... voids) {
            try {
                GitHub github = new GitHubBuilder().withOAuthToken(token).build();
                GHUser user = github.getMyself();
                List<GHRepository> list = user.listRepositories().toList();
                return list;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<GHRepository> list){
            super.onPostExecute(list);
    }


}}