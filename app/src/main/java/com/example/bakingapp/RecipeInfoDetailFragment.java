package com.example.bakingapp;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bakingapp.databinding.FragmentRecipeInfoDetailBinding;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecipeInfoDetailFragment extends Fragment {
    private final String SELECTED_POSITION = "selected_position";
    private final String PLAY_WHEN_READY = "play_when_ready";
    private boolean playWhenReady = true;
    private long position = -1;
    private FragmentRecipeInfoDetailBinding binding;
    private Step currentStep;
    private SimpleExoPlayer mExoPlayer;
    private OnButtonClickListener buttonClickListener;
    private int list_size;
    private String activity;

    public RecipeInfoDetailFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            currentStep = bundle.getParcelable(AppUtils.EXTRAS_STEP);
            list_size = bundle.getInt("list_size");
            activity = bundle.getString("activity");
        }

        if (savedInstanceState != null) {
            position = savedInstanceState.getLong(SELECTED_POSITION, C.TIME_UNSET);
            playWhenReady = savedInstanceState.getBoolean(PLAY_WHEN_READY);
        }

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_info_detail, container, false);

        if (!activity.equals("RecipeInfoActivity")) {
            binding.btnNext.setVisibility(View.VISIBLE);
            binding.btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonClickListener.onButtonClicked(currentStep.getId() + 1);
                }
            });
            binding.btnPrev.setVisibility(View.VISIBLE);
            binding.btnPrev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonClickListener.onButtonClicked(currentStep.getId() - 1);
                }
            });
        }

        return binding.getRoot();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            buttonClickListener = (OnButtonClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    " must implement OnButtonClickListener");
        }
    }

    private void checkCurrentStep() {
        int id = currentStep.getId();
        if (id == 0) {
            binding.btnPrev.setEnabled(false);
        } else if (id == list_size - 1) {
            binding.btnNext.setEnabled(false);
        } else {
            binding.btnPrev.setEnabled(true);
            binding.btnNext.setEnabled(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        checkCurrentStep();

        binding.tvStepShortDescription.setText(currentStep.getShortDescription());
        binding.tvStepDescription.setText(currentStep.getDescription());

        if (!TextUtils.isEmpty(currentStep.getVideoURL()))
            initializerPlayer(Uri.parse(currentStep.getVideoURL()));
        else if (!currentStep.getThumbnailURL().isEmpty()) {
            binding.thumbnailView.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(currentStep.getThumbnailURL())
                    .into(binding.thumbnailView);
        }
    }

    private void initializerPlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            binding.playerView.setVisibility(View.VISIBLE);
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            binding.playerView.setPlayer(mExoPlayer);

            binding.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);

            String userAgent = Util.getUserAgent(getContext(), "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            if (position > 0)
                mExoPlayer.seekTo(position);
            mExoPlayer.setPlayWhenReady(playWhenReady);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mExoPlayer != null) {
            position = mExoPlayer.getCurrentPosition();
            playWhenReady = mExoPlayer.getPlayWhenReady();
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(SELECTED_POSITION, position);
        outState.putBoolean(PLAY_WHEN_READY, playWhenReady);
    }


    public interface OnButtonClickListener {
        void onButtonClicked(int stepId);
    }
}
