package com.info121.mylimo.adapters;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.info121.mylimo.App;
import com.info121.mylimo.R;
import com.info121.mylimo.models.Song;
import com.info121.mylimo.utils.PrefDB;

import java.util.Date;
import java.util.List;

/**
 * Created by KZHTUN on 1/30/2018.
 */


public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    List<Song> mSongs;
    Context mContext;
    int lastIndex = -1;
    PrefDB prefDB = null;

    String MODE = "";

    MediaPlayer mediaPlayer;

    public SongAdapter(List<Song> songs, String mode) {
        this.mSongs = songs;
        this.MODE = mode;

        mediaPlayer  = new MediaPlayer();
    }

    @Override
    public int getItemCount() {
        return mSongs.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        final View v = layoutInflater.inflate(R.layout.card_song, parent, false);

        prefDB = new PrefDB(mContext);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        holder.songName.setText(mSongs.get(i).getSongName());
        holder.path.setText(mSongs.get(i).getData());

        holder.select.setChecked(lastIndex == i);
    }


    public void dismiss(){
        mediaPlayer.stop();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView songName, path;
        RadioButton select;

        public ViewHolder(View itemView) {
            super(itemView);

            songName = (TextView) itemView.findViewById(R.id.song_title);
            path = (TextView) itemView.findViewById(R.id.song_path);
            select = (RadioButton) itemView.findViewById(R.id.select);

            select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lastIndex = getAdapterPosition();
                    notifyDataSetChanged();

                    if(MODE.equalsIgnoreCase("PROMINENT")){
                        prefDB.putString(App.CONST_PROMINENT_TONE, mSongs.get(lastIndex).getData());
                    }

                    if(MODE.equalsIgnoreCase("NOTIFICATION")){
                        prefDB.putString(App.CONST_NOTIFICATION_TONE, mSongs.get(lastIndex).getData());
                    }

                    Date now = new Date();
                    long CHANNEL_ID = now.getTime();

                    prefDB.putString("OLD_CH_ID", prefDB.getString("NEW_CH_ID"));
                    prefDB.putString("NEW_CH_ID", CHANNEL_ID + "");

                    prefDB.putString("OLD_CH_ID_P", prefDB.getString("NEW_CH_ID_P"));
                    prefDB.putString("NEW_CH_ID_P", CHANNEL_ID + "_P");

                    Toast.makeText(mContext, "Set notification tone (" + mSongs.get(lastIndex) + ")" , Toast.LENGTH_SHORT).show();
                }
            });

            songName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Song song = mSongs.get(getAdapterPosition());
                    playAudio(song.getData());

                }
            });
        }
    }


    public void playAudio(String path){
        //set up MediaPlayer
        mediaPlayer.stop();
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(path );
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
