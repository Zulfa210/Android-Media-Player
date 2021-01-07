package com.example.mediaplayerapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    final int PICK_AUDIO_REQUEST = 123;
    private boolean musicLoaded = false;
    private boolean musicStopped = true;
    private TextView loadText;
    private TextView musicNameText;
    private TextView durationStartText;
    private TextView durationEndText;
    private TextView volumeControlText;
    private ImageView headphoneImage;
    private ImageButton playButton;
    private ImageButton pauseButton;
    private ImageButton stopButton;
    private ImageButton rewindButton;
    private ImageButton forwardButton;
    private ImageButton volumeUpImage;
    private ImageButton volumeDownImage;
    private MediaPlayer mp;
    private Uri uriAudio;
    private String audioFileName;
    private SeekBar seekBarPlaytime;
    private SeekBar seekBarVolume;
    SoundPool effectSoundPool;
    int effectSoundID;
    int errorSoundID;
    boolean effectSoundLoaded = false;
    boolean errorSoundLoaded = false;
    private int seekForwardTime = 5000; // 5000 milliseconds
    private int seekBackwardTime = 5000; // 5000 milliseconds
    private Handler mHandler = new Handler(Looper.myLooper());
    private double currentTime = 0.0;
    private final static int MAX_VOLUME = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadText = findViewById(R.id.textViewLoad);
        musicNameText = findViewById(R.id.textViewMusicName);
        durationStartText = findViewById(R.id.textViewDurationStart);
        durationEndText = findViewById(R.id.textViewDurationEnd);
        volumeControlText = findViewById(R.id.textViewVolumeControl);
        headphoneImage = findViewById(R.id.imageViewMusic);
        playButton = findViewById(R.id.playButton);
        pauseButton = findViewById(R.id.pauseButton);
        stopButton = findViewById(R.id.stopButton);
        rewindButton = findViewById(R.id.rewindButton);
        forwardButton = findViewById(R.id.forwardButton);
        volumeUpImage = findViewById(R.id.volumeUp);
        volumeDownImage = findViewById(R.id.volumeDown);
        seekBarPlaytime = findViewById(R.id.seekBarPlaytime);
        seekBarVolume = findViewById(R.id.seekBarVolume);

        // CREATE A NEW SOUND POOL OBJECT.
        effectSoundPool = new SoundPool.Builder().setMaxStreams(1).build();

        // ALLOW TO PLAYBACK AFTER AUDIO IS SUCCESSFULLY LOADED.
        effectSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool sp, int id, int status) {
                if (status == 0 && id == 1)
                    effectSoundLoaded = true;
                else if (status == 0 && id == 2)
                    errorSoundLoaded = true;
            }
        });

        // LOAD THE AUDIO FILES FOR EFFECT.
        effectSoundID = effectSoundPool.load(this, R.raw.effect, 1);
        errorSoundID = effectSoundPool.load(this, R.raw.error_sound, 1);

        // CONTROL THE SEEKBAR OF PLAYTIME
        seekBarPlaytime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(final SeekBar seekBar, int ProgressValue, boolean fromUser) {
                // MOVE THE TIME BASED ON USER'S PROGRESS VALUE.
                if (fromUser) {
                    mp.seekTo(ProgressValue);
                }

                // GET PROGRESS TIME.
                int minute = ProgressValue / 1000 / 60;
                int second = (ProgressValue / 1000) % 60;
                String minuteStr = "";
                String secondStr = "";
                if(minute < 10) {
                    minuteStr += "0" + minute;
                }
                else if (minute >= 60) {
                    minute -= 60;
                    if (minute < 10) {
                        minuteStr += "0" + minute;
                    }
                    else {
                        minuteStr += minute;
                    }
                }
                else {
                    minuteStr += minute;
                }
                if(second < 10) {
                    secondStr += "0" + second;
                }
                else if (second >= 60) {
                    second -= 60;
                    if (second < 10) {
                        secondStr += "0" + second;
                    }
                    else {
                        secondStr += second;
                    }
                }
                else {
                    secondStr += second;
                }

                // GET THE DIFFERENCE BETWEEN FINISH TIME AND CURRENT TIME.
                int remainMinute = ((mp.getDuration()) - ProgressValue) / 1000 / 60;
                int remainSecond = ((mp.getDuration()) - ProgressValue) / 1000 % 60;

                if (ProgressValue >= mp.getDuration()) {
                    remainMinute = 0;
                    remainSecond = 0;
                }


                String remainMinuteStr = "";
                String remainSecondStr = "";

                if (remainMinute < 10) {
                    remainMinuteStr += "0" + remainMinute;
                }
                else {
                    remainMinuteStr += remainMinute;
                }
                if (remainSecond < 10) {
                    remainSecondStr += "0" + remainSecond;
                }
                else {
                    remainSecondStr += remainSecond;
                }

                // UPDATE TIME TEXTS.
                durationStartText.setText(minuteStr + ":" + secondStr);
                durationEndText.setText(remainMinuteStr + ":" + remainSecondStr);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // CONTROL THE AUDIO VOLUME
        seekBarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int ProgressVolume, boolean fromUser) {
                float volume = (float) (1 - (Math.log(MAX_VOLUME - ProgressVolume) / Math.log(MAX_VOLUME)));
                mp.setVolume(volume, volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    // PLAY THE EFFECT SOUND (SOUND POOL).
    public void playEffectSound() {
        if (effectSoundLoaded)
            effectSoundPool.play(effectSoundID, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    // PLAY THE ERROR EFFECT SOUND (SOUND POOL).
    public void playErrorSound() {
        if (errorSoundLoaded)
            effectSoundPool.play(errorSoundID, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public void loadMusic(View v) {
        // DECLARE INTENT.
        Intent i = new Intent();

        // SET THE TYPE FOR ONLY AUDIO FILES.
        i.setType("audio/*");

        // SET THE ACTION AND GET THE CONTENT OF THE IMAGE.
        i.setAction(Intent.ACTION_GET_CONTENT);

        // START THE ACTIVITY
        startActivityForResult(Intent.createChooser(i, "Select Music"), PICK_AUDIO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_AUDIO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriAudio = data.getData();
            audioFileName = getFileName(uriAudio);
            mp = MediaPlayer.create(this, uriAudio);
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mp.pause();
                }
            });

            // SET THE AUDIO FILE NAME AND MAKE IMAGE LOADING STATUS TRUE.
            musicNameText.setText("\"" + audioFileName + "\"");
            musicLoaded = true;

            // GET THE MINUTE AND SECOND.
            int minute = mp.getDuration() / 1000 / 60;
            int second = (mp.getDuration() / 1000) % 60;
            String minuteStr = "";
            String secondStr = "";

            if (minute < 10) {
                minuteStr += "0" + minute;
            }
            else {
                minuteStr += minute;
            }
            if (second < 10) {
                secondStr += "0" + second;
            }
            else {
                secondStr += second;
            }

            // SET PROGRESS VAR VALUES FOR THE AUDIO.
            seekBarPlaytime.setProgress(0);
            seekBarPlaytime.setMax((int)mp.getDuration());

            // SET PROGRESS VAR VALUES FOR THE AUDIO VOLUME
            seekBarVolume.setProgress(50);
            seekBarVolume.setMax(100);

            // DISPLAY ALL FEATURES FOR AUDIO.
            loadText.setVisibility(View.INVISIBLE);
            musicNameText.setVisibility(View.VISIBLE);
            durationStartText.setVisibility(View.VISIBLE);
            durationEndText.setVisibility(View.VISIBLE);
            durationEndText.setText(minuteStr + ":" + secondStr);
            seekBarPlaytime.setVisibility(View.VISIBLE);
            playButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.VISIBLE);
            stopButton.setVisibility(View.VISIBLE);
            rewindButton.setVisibility(View.VISIBLE);
            forwardButton.setVisibility(View.VISIBLE);
            seekBarVolume.setVisibility(View.VISIBLE);
            volumeDownImage.setVisibility(View.VISIBLE);
            volumeUpImage.setVisibility(View.VISIBLE);
            volumeControlText.setVisibility(View.VISIBLE);

            if (mp.getCurrentPosition() >= mp.getDuration()) {
                mp.seekTo(0);
                seekBarPlaytime.setProgress(0);
            }
        }
    }

    // TO PLAY AUDIO
    public void playAudio(View view) {
        // PLAY ONLY IF AN AUDIO FILE IS LOADED AND IS NOT CURRENTLY PLAYING.
        if (musicLoaded && !mp.isPlaying() && mp.getCurrentPosition() < mp.getDuration()) {
            try {
                playEffectSound();
                headphoneImage.setClickable(false);
                mp.start();
                musicStopped = false;
                musicNameText.setText("Playing \"" + audioFileName + "\"");
                Toast.makeText(getApplicationContext(), "The audio is played.", Toast.LENGTH_SHORT).show();
                mHandler.postDelayed(updateAudioTime, 100);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        // IF THE AUDIO NOT LOADED OR THE AUDIO IS PLAYING
        else {
            playErrorSound();
        }
    }

    // TO PAUSE AUDIO
    public void pauseAudio(View view) {
        // PAUSE ONLY IF AN AUDIO FILE IS LOADED AND IS CURRENTLY PLAYING
        if (musicLoaded && mp.isPlaying()) {
            playEffectSound();
            mp.pause();
            headphoneImage.setClickable(true);
            musicNameText.setText("\"" + audioFileName + "\" is paused.");
            Toast.makeText(getApplicationContext(), "The audio is paused.", Toast.LENGTH_SHORT).show();
        }
        // IF THE AUDIO NOT LOADED OR THE AUDIO IS ALREADY PAUSED
        else {
            playErrorSound();
        }
    }

    // TO STOP AUDIO
    public void stopAudio(View view) {
        // STOP ONLY IF AN AUDIO FILE IS LOADED AND IS CURRENTLY PLAYING
        if ((musicLoaded && mp.isPlaying()) || (musicLoaded && musicStopped == false)) {
            playEffectSound();
            mp.pause();
            mp.seekTo(0);
            seekBarPlaytime.setProgress(0);
            headphoneImage.setClickable(true);
            musicNameText.setText("\"" + audioFileName + "\" is stopped.");
            Toast.makeText(getApplicationContext(), "The audio is stopped.", Toast.LENGTH_SHORT).show();
            musicStopped = true;
        }
        // IF THE AUDIO NOT LOADED OR THE AUDIO IS ALREADY PAUSED OR STOPPED
        else {
            playErrorSound();
        }
    }

    // TO FORWARD THE AUDIO 5 Seconds
    public void forwardAudio(View view) {
        if(musicLoaded) {
            currentTime = mp.getCurrentPosition();
            playEffectSound();
            if (currentTime + seekForwardTime <= (int) mp.getDuration())
                currentTime += seekForwardTime;
            else
                currentTime = (int) mp.getDuration();
            mp.seekTo((int) currentTime);
            Toast.makeText(getApplicationContext(), "Jumped forward 5 seconds.", Toast.LENGTH_SHORT).show();
        }
        // IF THE AUDIO NOT LOADED
        else {
            playErrorSound();
        }
    }

    // TO REWIND THE AUDIO 5 Seconds
    public void rewindAudio(View view) {
        if(musicLoaded) {
            currentTime = mp.getCurrentPosition();
            playEffectSound();
            if (currentTime - seekBackwardTime >= 0)
                currentTime -= seekBackwardTime;
            else
                currentTime = 0;
            mp.seekTo((int) currentTime);
            Toast.makeText(getApplicationContext(), "Jumped backward 5 seconds.", Toast.LENGTH_SHORT).show();
        }
        // IF THE AUDIO NOT LOADED
        else {
            playErrorSound();
        }
    }

    // UPDATE AUDIO TIME
    private Runnable updateAudioTime = new Runnable() {
        public void run() {

            // GET CURRENT TIME.
            long minute = mp.getCurrentPosition() / 1000 / 60;
            long second = (mp.getCurrentPosition() / 1000) % 60;

            // IF THE NUMBER IS 0-9, PUT MORE 0 i.e. 4:9 -> 04:09
            String minuteStr = "";
            String secondStr = "";
            if(minute < 10) {
                minuteStr += "0" + minute;
            }
            else if (minute >= 60) {
                minute -= 60;
                if (minute < 10) {
                    minuteStr += "0" + minute;
                }
                else {
                    minuteStr += minute;
                }
            }
            else {
                minuteStr += minute;
            }
            if(second < 10) {
                secondStr += "0" + second;
            }
            else if (second >= 60) {
                second -= 60;
                if (second < 10) {
                    secondStr += "0" + second;
                }
                else {
                    secondStr += second;
                }
            }
            else {
                secondStr += second;
            }

            // GET THE DIFFERENCE BETWEEN FINISH TIME AND CURRENT TIME.
            int remainMinute = ((mp.getDuration()) - mp.getCurrentPosition()) / 1000 / 60;
            int remainSecond = ((mp.getDuration()) - mp.getCurrentPosition()) / 1000 % 60;

            if (mp.getCurrentPosition() >= mp.getDuration()) {
                remainMinute = 0;
                remainSecond = 0;
            }


            String remainMinuteStr = "";
            String remainSecondStr = "";

            if (remainMinute < 10) {
                remainMinuteStr += "0" + remainMinute;
            }
            else {
                remainMinuteStr += remainMinute;
            }
            if (remainSecond < 10) {
                remainSecondStr += "0" + remainSecond;
            }
            else {
                remainSecondStr += remainSecond;
            }

            // IF THE AUDIO IS COMPLETELY PLAYED, AUDIO'S STARTING POINT IS MOVED TO 0.
            if (mp.getCurrentPosition() >= mp.getDuration()) {
                durationStartText.setText("00:00");
                mp.seekTo(0);
                mp.pause();
                seekBarPlaytime.setProgress(0);
                headphoneImage.setClickable(true);
                musicNameText.setText("\"" + audioFileName + "\" is completely played.");
                Toast.makeText(getApplicationContext(), "The audio is completed played.", Toast.LENGTH_SHORT).show();
                mHandler.post(this);
            }
            // IF THE AUDIO IS PLAYED, IT SHOWED THE CURRENT TIME AND PROGRESS IN THE SEEK BAR OF PLAYBACK
            else {
                durationStartText.setText(minuteStr + ":" + secondStr);
                durationEndText.setText(remainMinuteStr + ":" + remainSecondStr);
                seekBarPlaytime.setProgress(mp.getCurrentPosition());
                mHandler.postDelayed(this, 100);
            }
        }
    };

}