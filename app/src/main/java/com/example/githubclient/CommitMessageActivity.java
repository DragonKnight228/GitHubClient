package com.example.githubclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHCommitQueryBuilder;
import org.kohsuke.github.GHCommitSearchBuilder;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.PagedIterable;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CommitMessageActivity extends AppCompatActivity {

    int repositoryPosition;
    String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commit_message);
        Intent intent = getIntent();
        repositoryPosition = intent.getIntExtra("repository_position", 0);
        token = intent.getStringExtra("token");
        AsyncTask asyncTask = new AsyncTask();
        asyncTask.execute();
        try {
            List<GHCommit> commits = asyncTask.get();
            if (commits == null){
                Toast.makeText(this, "Нет коммитов", Toast.LENGTH_LONG).show();
                onBackPressed();
            }
            else {
            CommitsAdapter adapter = new CommitsAdapter();
            adapter.setCommits(commits);
            LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            RecyclerView recyclerView = findViewById(R.id.commit_recycler_view);
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(adapter);
        } }catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public class AsyncTask extends android.os.AsyncTask<Void, Void, List<GHCommit>> {

        @Override
        public List<GHCommit> doInBackground(Void... voids) {
            try {
                GitHub github = new GitHubBuilder().withOAuthToken(token).build();
                List<GHRepository> ghRepositories = github.getMyself().listRepositories().toList();
                GHRepository repository = ghRepositories.get(repositoryPosition);

                Calendar cal = Calendar.getInstance();
                cal.set(2000, 0, 0);
                Date since = cal.getTime();
                cal.set(2040, 0, 0);
                Date until = cal.getTime();

                GHCommitQueryBuilder queryBuilder = repository.queryCommits().since(since).until(until);
                PagedIterable<GHCommit> commitsPagedIterable = queryBuilder.list();
                return commitsPagedIterable.toList();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<GHCommit> commits){
            super.onPostExecute(commits);
        }
}}