package p8.demo.p8sokoban;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;
import android.widget.Chronometer;
import android.os.SystemClock;



/**
 * Declaration de notre activity heritee de Activity
 */
public class p8_Sokoban extends Activity {
    private static Chronometer chronometer;
    public static Activity self;
    static private SokobanView mSokobanView;
    static SeekBar seekBar;
    private TextView NombreCouleur;
    static private TextView NombreCoups;
    static private TextView TexteScores;
    static private TextView TexteHigh_Score;

    static boolean z=false;
    int[][] Savecarte;

    /**
     * Initialisation de l'activity avec le constructeur parent
     * Charge le fichier main.xml comme vue de l'activite
     * Recuperation de la vue cree a partir de son id
     * Recuperation des widgets et autres avec les id
     * Gestion de l'interface du jeu avec les evenements de la seekbar et des affichages dynamiques des textes
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        self = this;
        setContentView(R.layout.main);

        chronometer = (Chronometer) findViewById(R.id.chronometer);
        mSokobanView = (SokobanView) findViewById(R.id.SokobanView);
        NombreCouleur = (TextView) findViewById(R.id.NombreCouleur);
        NombreCoups = (TextView) findViewById(R.id.TexteScore);
        TexteScores = (TextView) findViewById(R.id.TexteScores);
        TexteHigh_Score = (TextView) findViewById(R.id.TexteHigh_Score);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setProgress(4);
        mSokobanView.setVisibility(View.VISIBLE);

        TexteScores.setText("Score: " + String.valueOf(mSokobanView.Score));
        TexteHigh_Score.setText("Highscore: " + String.valueOf(mSokobanView.High_Score));
        NombreCouleur.setText("Nombre de couleurs = " + (seekBar.getProgress()+1));

        if ((seekBar.getProgress()) == 0) {
            mSokobanView.NbCoupsMax = 1;
        }
        if ((seekBar.getProgress()) == 1) {
            mSokobanView.NbCoupsMax = 6;
        }
        if ((seekBar.getProgress()) == 2) {
            mSokobanView.NbCoupsMax = 12;
        }
        if ((seekBar.getProgress()) == 3) {
            mSokobanView.NbCoupsMax = 27;
        }
        if ((seekBar.getProgress()) == 4) {
            mSokobanView.NbCoupsMax = 32;
        }
        if ((seekBar.getProgress()) == 5) {
            mSokobanView.NbCoupsMax = 38;
        }
        if ((seekBar.getProgress()) == 6) {
            mSokobanView.NbCoupsMax = 44;
        }
        if ((seekBar.getProgress()) == 7) {
            mSokobanView.NbCoupsMax = 50;
        }
        NombreCoups.setText(mSokobanView.NbCoups + "/" + mSokobanView.NbCoupsMax);

        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int nbColors;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mSokobanView.in==true) {
                    nbColors = progress;
                    NombreCouleur.setText("Nombre de couleurs = " + (progress + 1));
                    if ((seekBar.getProgress()) == 0) {
                        mSokobanView.NbCoupsMax = 1;
                    }
                    if ((seekBar.getProgress()) == 1) {
                        mSokobanView.NbCoupsMax = 6;
                    }
                    if ((seekBar.getProgress()) == 2) {
                        mSokobanView.NbCoupsMax = 12;
                    }
                    if ((seekBar.getProgress()) == 3) {
                        mSokobanView.NbCoupsMax = 27;
                    }
                    if ((seekBar.getProgress()) == 4) {
                        mSokobanView.NbCoupsMax = 32;
                    }
                    if ((seekBar.getProgress()) == 5) {
                        mSokobanView.NbCoupsMax = 38;
                    }
                    if ((seekBar.getProgress()) == 6) {
                        mSokobanView.NbCoupsMax = 44;
                    }
                    if ((seekBar.getProgress()) == 7) {
                        mSokobanView.NbCoupsMax = 50;
                    }
                    NombreCoups.setText(mSokobanView.NbCoups + "/" + mSokobanView.NbCoupsMax);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                switch (nbColors) {
                    case 0:
                        Toast.makeText(getApplicationContext(), "[Très facile]", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(getApplicationContext(), "[Très facile]", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(getApplicationContext(), "[Très facile]", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        Toast.makeText(getApplicationContext(), "[Facile]", Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        Toast.makeText(getApplicationContext(), "[Moyen]", Toast.LENGTH_SHORT).show();
                        break;
                    case 5:
                        Toast.makeText(getApplicationContext(), "[Difficile]", Toast.LENGTH_SHORT).show();
                        break;
                    case 6:
                        Toast.makeText(getApplicationContext(), "[Difficile +]", Toast.LENGTH_SHORT).show();
                        break;
                    case 7:
                        Toast.makeText(getApplicationContext(), "[Extreme]", Toast.LENGTH_SHORT).show();
                        break;
                    case 8:
                        Toast.makeText(getApplicationContext(), "Difficulty : [FLOOD]", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        });
        if(z==true){
            Intent mIntent = getIntent();
            finish();
            startActivity(mIntent);
        }
    }


    /**
     * Quand on stoppe l'activity, on sauvegarde la grille lorsqu'on arretes l'application
     */
    @Override
    public void onStop() {
        super.onStop();
        Savecarte = mSokobanView.carte;
    }


    /**
     * Quand on demarre l'activity, on lance le jeu normalement avec un nouveau niveau
     */
    @Override
    public void onStart() {
        super.onStart();
        mSokobanView.newlevel(seekBar.getProgress()+1);
    }


    /**
     * Quand on redemarre l'activity, on recupere la position de la seekBar
     * on stoppe et on redemarre le chronometre
     * on relance aussi un nouveau thread pour pouvoir relancer une partie
     */
    @Override
    public void onRestart() {
        super.onRestart();
        mSokobanView.Score = 0;
        mSokobanView.NbCoups = 0;

        if ((seekBar.getProgress()) == 0) {
            mSokobanView.NbCoupsMax = 1;
        }
        if ((seekBar.getProgress()) == 1) {
            mSokobanView.NbCoupsMax = 6;
        }
        if ((seekBar.getProgress()) == 2) {
            mSokobanView.NbCoupsMax = 12;
        }
        if ((seekBar.getProgress()) == 3) {
            mSokobanView.NbCoupsMax = 27;
        }
        if ((seekBar.getProgress()) == 4) {
            mSokobanView.NbCoupsMax = 32;
        }
        if ((seekBar.getProgress()) == 5) {
            mSokobanView.NbCoupsMax = 38;
        }
        if ((seekBar.getProgress()) == 6) {
            mSokobanView.NbCoupsMax = 44;
        }
        if ((seekBar.getProgress()) == 7) {
            mSokobanView.NbCoupsMax = 50;
        }

        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
        seekBar.setEnabled(true);
        mSokobanView.in = true;
        mSokobanView.cv_thread=new Thread(p8_Sokoban.mSokobanView);
        MajCoups();
    }


    /**
     * Methode qui permet de redemarrer l'activity
     * @param activity
     */
    static public void restartActivity(Activity activity){
        Intent mIntent = activity.getIntent();
        activity.finish();
        activity.startActivity(mIntent);
    }


    /**
     * Methode qui permet de lancer le chronometre
     */
    static public void LanceChrono(){
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
    }


    /**
     * Methode qui permet de recuperer la position de la seekBar
     * @return
     */
    static public int getProgress() {
        return seekBar.getProgress();
    }


    /**
     * Methode qui permet de mettre a jour le nombre de coups effectue par le joueur
     * en recuperant la position de la seekBar, on met le nombre de coups maximum a une valeur fixe
     * ensuite on affiche a l'ecran les valeurs en fonction de la progression du joueur
     */
    static public void MajCoups() {
        if ((seekBar.getProgress()) == 0) {
            mSokobanView.NbCoupsMax = 1;
        }
        if ((seekBar.getProgress()) == 1) {
            mSokobanView.NbCoupsMax = 6;
        }
        if ((seekBar.getProgress()) == 2) {
            mSokobanView.NbCoupsMax = 12;
        }
        if ((seekBar.getProgress()) == 3) {
            mSokobanView.NbCoupsMax = 27;
        }
        if ((seekBar.getProgress()) == 4) {
            mSokobanView.NbCoupsMax = 32;
        }
        if ((seekBar.getProgress()) == 5) {
            mSokobanView.NbCoupsMax = 38;
        }
        if ((seekBar.getProgress()) == 6) {
            mSokobanView.NbCoupsMax = 44;
        }
        if ((seekBar.getProgress()) == 7) {
            mSokobanView.NbCoupsMax = 50;
        }

        NombreCoups.setText(String.valueOf(mSokobanView.NbCoups) + "/" + String.valueOf(mSokobanView.NbCoupsMax));
        TexteScores.setText("Score: " + String.valueOf(mSokobanView.Score));
        TexteHigh_Score.setText("Highscore: " + String.valueOf(mSokobanView.High_Score));
    }
}