package com.example.bharadwaj.bakingrecipes;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bharadwaj.bakingrecipes.constants.Constants;
import com.example.bharadwaj.bakingrecipes.model.Recipe;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import butterknife.Unbinder;

public class StepDetailsFragment extends Fragment implements Player.EventListener {

    protected static final String LOG_TAG = StepDetailsFragment.class.getSimpleName();

    static final int fragmentIdentifier = 200;
    private static MediaSessionCompat mMediaSession;
    boolean isPlayWhenReady = true;
    @Nullable
    @BindView(R.id.steps_details)
    TextView stepDetailsView;
    @Nullable
    @BindView(R.id.previous_step_button)
    Button previousStepButton;
    @Nullable
    @BindView(R.id.next_step_button)
    Button nextStepButton;
    @BindView(R.id.step_player_view)
    PlayerView stepVideoPlayerView;
    @BindView(R.id.step_thumbnail_view)
    ImageView stepThumbnailView;
    @BindView(R.id.recipe_no_video_placeholder_view)
    TextView step_noVideo_noThumbnail_placeHolder;
    private Recipe mCurrentRecipe;
    private int mCurrentStepID;
    private SimpleExoPlayer mRecipeVideoPlayer;
    private PlaybackStateCompat.Builder mStateBuilder;
    private long mSavedPosition = 0;
    private Unbinder mUnbinder;

    public StepDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "Entering onCreate");
        //setRetainInstance(true);

        retreiveFromSavedInstanceOrArguments(savedInstanceState);

        Log.v(LOG_TAG, "Leaving onCreate");
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeMediaSession();
        initializeAndSetupRecipeVideoPlayer();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(LOG_TAG, "Entering onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_step_details, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);

        setupViewsBasedOnConfiguration();


        // Initializing and setting the Media Session handled in onResume.


        Log.v(LOG_TAG, "Leaving onCreateView");

        return rootView;
        // Inflate the layout for this fragment
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);

        //retreiveFromSavedInstanceOrArguments(savedInstanceState);
    }

    private void retreiveFromSavedInstanceOrArguments(Bundle savedInstanceState) {
        //Retrieving Video player position saved
        if (savedInstanceState != null) {
            mSavedPosition = savedInstanceState.getLong(Constants.PLAYER_SAVED_POSITION);
            //Log.v(LOG_TAG, "Saved position : " + mSavedPosition);

        }

        //Retrieving Recipe and Step ID
        if (savedInstanceState != null) {
            Log.v(LOG_TAG, "From saved instance state");
            if (savedInstanceState.getInt(Constants.STEP_ID, -1) != -1) {
                mCurrentStepID = savedInstanceState.getInt(Constants.STEP_ID);
            }
            if (savedInstanceState.getParcelable(Constants.RECIPE) != null) {
                mCurrentRecipe = Parcels.unwrap(savedInstanceState.getParcelable(Constants.RECIPE));
            }
            isPlayWhenReady = savedInstanceState.getBoolean(Constants.PLAY_STATE);
            Log.v(LOG_TAG, " savedInstanceState object : " + mCurrentRecipe.getName());
        } else if (getArguments() != null) {
            Log.v(LOG_TAG, "From Arguments");
            mCurrentStepID = getArguments().getInt(Constants.STEP_ID);
            mCurrentRecipe = Parcels.unwrap(getArguments().getParcelable(Constants.RECIPE));
            Log.v(LOG_TAG, " savedInstanceState object : " + savedInstanceState);
        }

        //Log.v(LOG_TAG, "Current step id is : " + mCurrentStepID);
        //Log.v(LOG_TAG, "Current recipe is : " + mCurrentRecipe.getName());

    }

    /**
     * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
     * and media controller.
     */
    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(getContext(), LOG_TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new StepDetailsFragment.MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }

    private void initializeAndSetupRecipeVideoPlayer() {
        Log.v(LOG_TAG, "Entering initializeAndSetupRecipeVideoPlayer");
        // Create a default TrackSelector
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();

        // Create the player
        mRecipeVideoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);

        // Bind the player to the view.
        stepVideoPlayerView.setPlayer(mRecipeVideoPlayer);
        // Set the ExoPlayer.EventListener to this activity.
        mRecipeVideoPlayer.addListener(this);

        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getContext(),
                Util.getUserAgent(getContext(), Constants.APP_NAME), defaultBandwidthMeter);
        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(mCurrentRecipe.getSteps().get(mCurrentStepID).getVideoURL()).buildUpon().build());

        // Prepare the player with the source.
        mRecipeVideoPlayer.prepare(videoSource);
        mRecipeVideoPlayer.setPlayWhenReady(isPlayWhenReady);

        stepVideoPlayerView.getPlayer().seekTo(mSavedPosition);

        if (getResources().getString(R.string.configurationDetector).equals(Constants.IN_LANDSCAPE_MODE)) {
            stepVideoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        }

        Log.v(LOG_TAG, "Leaving initializeAndSetupRecipeVideoPlayer");
    }

    void showPlaceholder_hideVideo_hideThumbnail() {
        stepVideoPlayerView.setVisibility(View.GONE);
        stepThumbnailView.setVisibility(View.GONE);
        step_noVideo_noThumbnail_placeHolder.setVisibility(View.VISIBLE);
        step_noVideo_noThumbnail_placeHolder.setText(Constants.VIDEO_THUMBNAIL_NOT_AVAILABLE);
    }

    void showVideo_hideThumbnail_hidePlaceholder() {
        step_noVideo_noThumbnail_placeHolder.setVisibility(View.GONE);
        stepThumbnailView.setVisibility(View.GONE);
        stepVideoPlayerView.setVisibility(View.VISIBLE);
    }

    void showThumbnail_hideVideo_hidePlaceholder() {
        step_noVideo_noThumbnail_placeHolder.setVisibility(View.GONE);
        stepVideoPlayerView.setVisibility(View.GONE);
        stepThumbnailView.setVisibility(View.VISIBLE);
    }

    void showVideoViewAndHideOtherViews() {
        stepVideoPlayerView.setVisibility(View.VISIBLE);
        //Hiding all other views
        stepThumbnailView.setVisibility(View.GONE);
        step_noVideo_noThumbnail_placeHolder.setVisibility(View.GONE);
        stepDetailsView.setVisibility(View.GONE);
        previousStepButton.setVisibility(View.GONE);
        nextStepButton.setVisibility(View.GONE);
    }

    void setupViewsBasedOnConfiguration() {
        String thumbnailUrl = mCurrentRecipe.getSteps().get(mCurrentStepID).getThumbnailURL();
        if (thumbnailUrl.toLowerCase().endsWith(Constants.MP4)) {
            mCurrentRecipe.getSteps().get(mCurrentStepID).setVideoURL(mCurrentRecipe.getSteps().get(mCurrentStepID).getThumbnailURL());
        }
        switch (getResources().getString(R.string.configurationDetector)) {
            case Constants.IN_PORTRAIT_MODE:
                if (!mCurrentRecipe.getSteps().get(mCurrentStepID).getVideoURL().isEmpty()) {
                    showVideo_hideThumbnail_hidePlaceholder();
                } else if (!mCurrentRecipe.getSteps().get(mCurrentStepID).getThumbnailURL().isEmpty()) {
                    showThumbnail_hideVideo_hidePlaceholder();
                } else {
                    showPlaceholder_hideVideo_hideThumbnail();
                }
                setStepDetails();
                break;
            case Constants.IN_LANDSCAPE_MODE:
                if (!mCurrentRecipe.getSteps().get(mCurrentStepID).getVideoURL().isEmpty()) {
                    showVideoViewAndHideOtherViews();
                } else if (!mCurrentRecipe.getSteps().get(mCurrentStepID).getThumbnailURL().isEmpty()) {
                    showThumbnail_hideVideo_hidePlaceholder();
                    setStepDetails();
                } else {
                    showPlaceholder_hideVideo_hideThumbnail();
                    setStepDetails();
                }
                break;
            case Constants.IN_TABLET_MODE:
                if (!mCurrentRecipe.getSteps().get(mCurrentStepID).getVideoURL().isEmpty()) {
                    showVideo_hideThumbnail_hidePlaceholder();
                } else if (!mCurrentRecipe.getSteps().get(mCurrentStepID).getThumbnailURL().isEmpty()) {
                    showThumbnail_hideVideo_hidePlaceholder();
                } else {
                    showPlaceholder_hideVideo_hideThumbnail();
                }
                stepDetailsView.setText(mCurrentRecipe.getSteps().get(mCurrentStepID).getDescription());
                break;
            default:
                Log.v(LOG_TAG, "Configuration not recognized");
        }

        /*Log.v(LOG_TAG,"RootView is : " + rootView.toString());
        Log.v(LOG_TAG,"Visibility of details : " + String.valueOf(stepDetailsView.getVisibility()));
        Log.v(LOG_TAG,"Visibility of place holder : " + String.valueOf(step_noVideo_noThumbnail_placeHolder.getVisibility()));
        Log.v(LOG_TAG,"Visibility of video holder : " + String.valueOf(stepVideoPlayerView.getVisibility()));
        Log.v(LOG_TAG,"Visibility of previous button : " + String.valueOf(previousStepButton.getVisibility()));
        Log.v(LOG_TAG,"Visibility of next button : " + String.valueOf(nextStepButton.getVisibility()));
        */
    }

    private void setStepDetails() {
        Log.v(LOG_TAG, "Entering setStepDetails");
        if (mCurrentStepID >= 0) {
            stepDetailsView.setVisibility(View.VISIBLE);
            stepDetailsView.setText(mCurrentRecipe.getSteps().get(mCurrentStepID).getDescription());

            if (!mCurrentRecipe.getSteps().get(mCurrentStepID).getThumbnailURL().isEmpty()) {
                Picasso.with(getContext())
                        .load(mCurrentRecipe.getSteps().get(mCurrentStepID).getThumbnailURL())
                        .placeholder(R.mipmap.recipe_preview_view_holder)
                        .error(R.mipmap.recipe_preview_view_error)
                        .fit()
                        .into(stepThumbnailView);
            }

            if (mCurrentStepID == 0) {
                previousStepButton.setVisibility(View.GONE);
                nextStepButton.setVisibility(View.VISIBLE);
            } else if (mCurrentStepID == mCurrentRecipe.getSteps().size() - 1) {
                previousStepButton.setVisibility(View.VISIBLE);
                nextStepButton.setVisibility(View.GONE);
            } else {
                nextStepButton.setVisibility(View.VISIBLE);
                previousStepButton.setVisibility(View.VISIBLE);
            }
        } else {
            Log.v(LOG_TAG, "Step ID is negative. Something wrong");
        }
        Log.v(LOG_TAG, "Leaving setStepDetails");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.v(LOG_TAG, "Entering onSaveInstanceState");
        super.onSaveInstanceState(outState);
        Log.v(LOG_TAG, "Saving Recipe : " + mCurrentRecipe.getName() + " to out instance state");
        outState.putParcelable(Constants.RECIPE, Parcels.wrap(mCurrentRecipe));
        Log.v(LOG_TAG, "Saving Step ID : " + mCurrentStepID + " to out instance state");
        outState.putInt(Constants.STEP_ID, mCurrentStepID);
        Log.v(LOG_TAG, "Saving position of video player" + mSavedPosition + " to out instance state");
        outState.putLong(Constants.PLAYER_SAVED_POSITION, mSavedPosition);
        isPlayWhenReady = mRecipeVideoPlayer.getPlayWhenReady();
        outState.putBoolean(Constants.PLAY_STATE, isPlayWhenReady);

        Log.v(LOG_TAG, "Leaving onSaveInstanceState");
    }

    @Optional
    @OnClick(R.id.previous_step_button)
    void previousStepButtonClicked() {
        Log.v(LOG_TAG, "Navigating to previous Step");
        mCurrentStepID--;
        performWhenPreviousOrNextButtonIsClicked();
    }

    @Optional
    @OnClick(R.id.next_step_button)
    void nextStepButtonClicked() {
        Log.v(LOG_TAG, "Navigating to next Step");
        mCurrentStepID++;
        performWhenPreviousOrNextButtonIsClicked();
    }


    void performWhenPreviousOrNextButtonIsClicked() {
        resetRecipeVideoPlayer();
        setupViewsBasedOnConfiguration();
        initializeAndSetupRecipeVideoPlayer();
    }


    void resetRecipeVideoPlayer() {
        mRecipeVideoPlayer.stop();
        mRecipeVideoPlayer.release();
        mRecipeVideoPlayer = null;
    }

    @Override
    public void onPause() {
        Log.v(LOG_TAG, "Entering onPause method");
        super.onPause();
        mSavedPosition = Math.max(0, stepVideoPlayerView.getPlayer().getCurrentPosition());
        stepVideoPlayerView.getPlayer().release();

        Log.v(LOG_TAG, "Entering onPause method\n\n");
    }

    /**
     * Release the player when the activity is destroyed.
     */
    @Override
    public void onDestroy() {
        Log.v(LOG_TAG, "Entering onDestroy method");
        super.onDestroy();
        resetRecipeVideoPlayer();
        mMediaSession.setActive(false);
        mUnbinder.unbind();
        Log.v(LOG_TAG, "Leaving onDestroy method\n\n");
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
        Log.v(LOG_TAG, "onTimelineChanged method called");
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        Log.v(LOG_TAG, "onTracksChanged method called");

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
        Log.v(LOG_TAG, "onLoadingChanged method called");

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        Log.v(LOG_TAG, "onPlayerStateChanged method called");
        if ((playbackState == Player.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mRecipeVideoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == Player.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mRecipeVideoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {
        Log.v(LOG_TAG, "onRepeatModeChanged method called");

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
        Log.v(LOG_TAG, "onShuffleModeEnabledChanged method called");

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Log.v(LOG_TAG, "onPlayerError method called");

    }

    @Override
    public void onPositionDiscontinuity(int reason) {
        Log.v(LOG_TAG, "onPositionDiscontinuity method called");

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
        Log.v(LOG_TAG, "onPlaybackParametersChanged method called");

    }

    @Override
    public void onSeekProcessed() {
        Log.v(LOG_TAG, "onSeekProcessed method called");

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mRecipeVideoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mRecipeVideoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mRecipeVideoPlayer.seekTo(0);
        }
    }


}
