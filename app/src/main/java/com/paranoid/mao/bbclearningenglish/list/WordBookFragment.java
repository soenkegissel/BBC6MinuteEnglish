package com.paranoid.mao.bbclearningenglish.list;

import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paranoid.mao.bbclearningenglish.R;
import com.paranoid.mao.bbclearningenglish.data.DatabaseContract;
import com.paranoid.mao.bbclearningenglish.sync.SyncUtility;

import java.io.IOException;

/**
 * Created by Paranoid on 17/9/10.
 */

public class WordBookFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        WordBookAdapter.OnItemClickListener {

    private static final int WORD_BOOK_LOADER_ID = 64236;

    private WordBookAdapter mAdapter;
    private ItemTouchHelper mSwipeToDeleteHelper;
    private MediaPlayer mMediaPlayer;
    private String mCurrentAudioHref = "";

    public WordBookFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ItemTouchHelper.SimpleCallback swipeToDelete = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                mAdapter.updateExpendedSet(viewHolder.getAdapterPosition());
                viewHolder.itemView.setAlpha(1.0f);
                long ID = (long) viewHolder.itemView.getTag();
                Uri uri = DatabaseContract.VocabularyEntry.CONTENT_URI
                        .buildUpon()
                        .appendEncodedPath(String.valueOf(ID))
                        .build();
                getContext().getContentResolver().delete(uri, null, null);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    // Get RecyclerView item from the ViewHolder
                    View itemView = viewHolder.itemView;
                    itemView.setAlpha(1.0f - Math.abs(dX) / itemView.getWidth());

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }
        };
        mSwipeToDeleteHelper = new ItemTouchHelper(swipeToDelete);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getActivity().setTitle(R.string.custom_word_book);

        View view = inflater.inflate(R.layout.fragent_word_book_list, container, false);

        mAdapter = new WordBookAdapter(getContext(), this);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        RecyclerView recyclerView = view.findViewById(R.id.rv_word_book_list);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(manager);
        mSwipeToDeleteHelper.attachToRecyclerView(recyclerView);

        getLoaderManager().initLoader(WORD_BOOK_LOADER_ID, null, this);

        return view;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getContext(),
                DatabaseContract.VocabularyEntry.CONTENT_URI,
                WordBookAdapter.PROJECTION,
                null,
                null,
                DatabaseContract.VocabularyEntry._ID + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void OnPronunciationClick(String audioHref) {
        if (mCurrentAudioHref.equals(audioHref) && mMediaPlayer != null) {
            mMediaPlayer.start();
        } else {
            prepareMedia(audioHref);
            mCurrentAudioHref = audioHref;
        }
    }

    @Override
    public void OnDetailClick(long id) {
        Uri uri = DatabaseContract.VocabularyEntry.CONTENT_URI
                .buildUpon()
                .appendEncodedPath(String.valueOf(id))
                .build();
        SyncUtility.wordBookInitialize(getContext(), uri);
    }

    private void prepareMedia(String audioHref) {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
        }
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(audioHref);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
        } catch (IOException e) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
