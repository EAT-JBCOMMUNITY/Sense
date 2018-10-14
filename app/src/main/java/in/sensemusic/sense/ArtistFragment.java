package in.sensemusic.sense;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.HashMap;
import java.util.Objects;

public class ArtistFragment extends Fragment {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    Cursor Artist_cursor;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_artist, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView = Objects.requireNonNull(getActivity()).findViewById(R.id.recyclerView_Artist);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        String[] projection = {
                MediaStore.Audio.Artists._ID,
                MediaStore.Audio.Artists.ARTIST,
                MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
        };

        ContentResolver content = getActivity().getContentResolver();
        Artist_cursor = content.query(
                MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Audio.Artists.ARTIST+"");

        recyclerView.setAdapter(new ArtistAdapter(getContext(),Artist_cursor));
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set Action Bar title
        ((MainActivity) Objects.requireNonNull(getActivity())).setActionBarTitle("Artists");
        // ((MainActivity) getActivity()).getSupportActionBar().setTitle("Artists");
    }

}

class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder>{

    private Cursor artistCursor;
    private LayoutInflater layoutInflater;
    private Context context;
    private HashMap<String,String> albumartData;

    ArtistAdapter(Context context, Cursor artistCursor) {
        this.artistCursor = artistCursor;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        albumartData = new HashMap<>();

        String[] projection = {
                MediaStore.Audio.Albums.ARTIST,
                MediaStore.Audio.Albums.ALBUM_ART
        };

        ContentResolver content = context.getContentResolver();
        try (Cursor albumArtCursor = content.query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null, //new String[]{songCursor.getString(songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))},
                null)) {

            while (Objects.requireNonNull(albumArtCursor).moveToNext()) {
                albumartData.put(albumArtCursor.getString(albumArtCursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)),
                        albumArtCursor.getString(albumArtCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART)));
                //   Log.d(TAG,albumartData.get(AlbumArtCursor.getString(AlbumArtCursor.getColumnIndex(MediaStore.Audio.Albums._ID)))+"Albumdata");
            }
        }
    }


    @NonNull
    @Override
    public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.artist_name,parent,false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
        if (!artistCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }

        holder.txt_artistName.setText(artistCursor.getString(artistCursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST)));
        holder.txt_no_of_songs.setText(String.format("%s Songs", artistCursor.getString(artistCursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS))));

        Glide
                .with(context)
                .load(albumartData.get(artistCursor.getString(artistCursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST))))
                .apply(new RequestOptions()
                        .placeholder(R.drawable.album_art)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                )
                .thumbnail(0.5f)
                .transition(new DrawableTransitionOptions()
                    .crossFade()
                )
                .into(holder.img_ArtistAlbumArt);
    }

    @Override
    public int getItemCount() {
        return artistCursor.getCount();
    }

    class ArtistViewHolder extends RecyclerView.ViewHolder{

        TextView txt_artistName,txt_no_of_songs;
        ImageView img_ArtistAlbumArt;
        ArtistViewHolder(View itemView) {
            super(itemView);
            txt_artistName = itemView.findViewById(R.id.artistName);
            txt_no_of_songs = itemView.findViewById(R.id.no_of_songs);
            img_ArtistAlbumArt = itemView.findViewById(R.id.imageView_Artist_AlbumArt);
        }
    }
}