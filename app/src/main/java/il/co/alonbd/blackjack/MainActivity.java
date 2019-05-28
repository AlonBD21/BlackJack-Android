package il.co.alonbd.blackjack;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.graphics.drawable.AnimationUtilsCompat;
import android.support.v4.view.KeyEventDispatcher;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    FrameLayout root;
    AppCompatImageButton hit;
    AppCompatImageButton doubleup;
    AppCompatImageButton end;
    AppCompatImageButton pause;
    AppCompatImageButton cog;
    Point size;
    Button startGameBtn, hallBtn;
    SharedPreferences sp;
    Match match;
    public static MediaPlayer mp;
    public static final String MUSIC_BOOL = "music_bool";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_layout);
        final Handler handler = new Handler();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                Card.loadBitmaps(MainActivity.this);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        menuSetup();
                    }
                });
            }
        });
        t.start();
        sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        size = new Point();
        wm.getDefaultDisplay().getSize(size);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mp = MediaPlayer.create(this, R.raw.henymusic);
        mp.setLooping(true);
        if (sp.getBoolean(MUSIC_BOOL, true)) {
            mp.start();
        }


    }

    protected void menuSetup() {
        setContentView(R.layout.mainmenu);
        startGameBtn = findViewById(R.id.start);
        hallBtn = findViewById(R.id.hall_btn);
        cog = findViewById(R.id.cog);
        cog.setOnClickListener(new SettingListener());
        startGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });
        hallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HallOfFame.class);
                startActivity(intent);
            }
        });
        Animation animation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setDuration(7000);
        animation.setInterpolator(new LinearInterpolator());
        cog.startAnimation(animation);
    }

    protected void play() {
        setContentView(R.layout.game);
        root = findViewById(R.id.match_root_fl);
        hit = findViewById(R.id.hit);
        end = findViewById(R.id.end);
        doubleup = findViewById(R.id.doubleup);
        pause = findViewById(R.id.pause);
        pause.setOnClickListener(new PauseListener());
        hit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                match.hit();
            }
        });
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                match.end();
            }
        });
        doubleup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                match.doubleup();
            }
        });
        match = new Match(root);
        match.startMatch();
    }

    class PauseListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            View dialogView = getLayoutInflater().inflate(R.layout.pausedialog, null);
            Button cancel = dialogView.findViewById(R.id.cancel);
            Button back = dialogView.findViewById(R.id.back);
            SwitchCompat musicState = dialogView.findViewById(R.id.music_switch);
            musicState.setChecked(sp.getBoolean(MUSIC_BOOL, true));
            musicState.setOnCheckedChangeListener(new MusicSwitchListener());

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            final AlertDialog dialog = builder.setView(dialogView).setCancelable(true).create();
            dialog.show();

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    menuSetup();
                }
            });

        }
    }

    class SettingListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_settings, null);
            Button cancel = dialogView.findViewById(R.id.cancel);
            Button clearHOF = dialogView.findViewById(R.id.clear_hof);
            SwitchCompat musicState = dialogView.findViewById(R.id.music_switch);
            musicState.setChecked(sp.getBoolean(MUSIC_BOOL, true));
            musicState.setOnCheckedChangeListener(new MusicSwitchListener());
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            final AlertDialog dialog = builder.setView(dialogView).setCancelable(true).create();
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            clearHOF.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setCancelable(true).setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            HallOfFame.clearEntries(MainActivity.this);
                            dialog.dismiss();


                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setTitle("Are You Sure?").setMessage("Do you want to delete all of the records in the 'Hall of Fame?'").show();

                }
            });
            dialog.show();
        }
    }

    class MusicSwitchListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                mp.start();
            } else {
                mp.pause();
            }
            if (isChecked) {
                sp.edit().putBoolean(MUSIC_BOOL, true).apply();
            } else {
                sp.edit().putBoolean(MUSIC_BOOL, false).apply();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mp.stop();
        mp.release();
    }


}
