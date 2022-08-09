package com.example.githubclient;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

class ReposViewHolder extends RecyclerView.ViewHolder {
     ImageView authorIcon;
     TextView authorName;
     TextView repositoryName;
     TextView description;
     TextView forks;
     TextView watches;
     public ReposViewHolder(@NonNull View itemView) {
          super(itemView);
          authorIcon = itemView.findViewById(R.id.author_icon);
          authorName = itemView.findViewById(R.id.author_name);
          repositoryName = itemView.findViewById(R.id.repository_name);
          description = itemView.findViewById(R.id.repository_description);
          forks = itemView.findViewById(R.id.forks);
          watches = itemView.findViewById(R.id.watches);
     }
}

     class ReposArrayAdapter extends RecyclerView.Adapter<ReposViewHolder> {

          List<GHRepository> repositories = new ArrayList<GHRepository>();
          View view;
          int pos;
          ViewGroup parentActivity;
          String token;
          HashMap<String, String> hashMap;

          public void setRepositories(List<GHRepository> repositories, String token) {
               this.repositories = repositories;
               this.token = token;
               notifyDataSetChanged();
          }

          @NonNull
          @Override
          public ReposViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               parentActivity = parent;
               view = LayoutInflater.from(parent.getContext()).inflate(R.layout.repository_item_view, parent, false);
               return new ReposViewHolder(view);
          }

          @Override
          public void onBindViewHolder(@NonNull ReposViewHolder holder, int position) {
               pos = holder.getAdapterPosition();
               ReposAsyncTask reposAsyncTask = new ReposAsyncTask();
               reposAsyncTask.execute();
               try {
                    hashMap = reposAsyncTask.get();
                    Picasso.with(parentActivity.getContext()).load(Uri.parse(hashMap.get("author_icon"))).into(holder.authorIcon);
                    holder.authorName.setText(hashMap.get("author_name"));
                    holder.repositoryName.setText(hashMap.get("repository_name"));
                    holder.description.setText(hashMap.get("description"));
                    holder.forks.setText(hashMap.get("forks"));
                    holder.watches.setText(hashMap.get("watches"));
               } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
               }
               holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                         int repositoryPosition = holder.getLayoutPosition();
                         Intent intent = new Intent(view.getContext(), CommitMessageActivity.class);
                         intent.putExtra("repository_position", repositoryPosition);
                         intent.putExtra("token", token);
                         view.getContext().startActivity(intent);
                    }
               });

          }


          @Override
          public int getItemCount() {
               return repositories.size();
          }

          private class ReposAsyncTask extends android.os.AsyncTask<Void, Void, HashMap<String, String>> {

               @Override
               public HashMap<String, String> doInBackground(Void... params) {
                    try {
                         HashMap<String, String> hashMap = new HashMap<>();
                         GHRepository repos = repositories.get(pos);
                         hashMap.put("author_icon", repos.getOwner().getAvatarUrl());
                         hashMap.put("author_name", repos.getOwner().getName());
                         hashMap.put("repository_name", repos.getName());
                         hashMap.put("description", repos.getDescription());
                         hashMap.put("forks", String.valueOf(repos.getForks()));
                         hashMap.put("watches", String.valueOf(repos.getWatchers()));
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

