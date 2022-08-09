package com.example.githubclient;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.PagedIterable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

class CommsViewHolder extends RecyclerView.ViewHolder {
    TextView commitHash;
    TextView commitMessage;
    TextView commitAuthor;
    TextView commitDate;
    public CommsViewHolder(@NonNull View itemView) {
        super(itemView);
        commitHash = itemView.findViewById(R.id.commit_hash);
        commitAuthor = itemView.findViewById(R.id.commit_author);
        commitMessage = itemView.findViewById(R.id.commit_message);
        commitDate = itemView.findViewById(R.id.commit_date);
    }
}

class CommitsAdapter extends RecyclerView.Adapter<CommsViewHolder> {

    List<GHCommit> commits = new ArrayList<GHCommit>();
    View view;
    int pos;
    ViewGroup parentActivity;
    HashMap<String, String> hashMap;

    public void setCommits(List<GHCommit> commits) {
        this.commits = commits;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        parentActivity = parent;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.commits_item_view, parent, false);
        return new CommsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommsViewHolder holder, int position) {
        pos = holder.getAdapterPosition();
        CommsAsyncTask commsAsyncTask = new CommsAsyncTask();
        commsAsyncTask.execute();
        try {
            hashMap = commsAsyncTask.get();
            holder.commitHash.setText(hashMap.get("commit_hash"));
            holder.commitAuthor.setText(hashMap.get("commit_author"));
            holder.commitMessage.setText(hashMap.get("commit_message"));
            holder.commitDate.setText(hashMap.get("commit_date"));
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

    }


    @Override
    public int getItemCount() { return commits.size(); }

    public class CommsAsyncTask extends android.os.AsyncTask<Void, Void, HashMap<String, String>> {

        @Override
        public HashMap<String, String> doInBackground(Void... params) {
            try {
                HashMap<String, String> hashMap = new HashMap<>();
                GHCommit commit = commits.get(pos);
                hashMap.put("commit_hash", commit.getSHA1());
                hashMap.put("commit_author", commit.getAuthor().getName());
                hashMap.put("commit_message", commit.getCommitShortInfo().getMessage());
                hashMap.put("commit_date", String.valueOf(commit.getCommitDate()));
                return hashMap;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(HashMap<String, String> hashMap){
            super.onPostExecute(hashMap);
        }
    }


}
