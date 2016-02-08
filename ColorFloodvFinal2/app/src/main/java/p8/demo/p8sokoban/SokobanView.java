package p8.demo.p8sokoban;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import java.util.Random;


public class SokobanView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    // Declaration des images
    private Bitmap 		bleu;
    private Bitmap 		violet;
    private Bitmap 		rouge;
    private Bitmap      jaune;
    private Bitmap      rose;
    private Bitmap      marron;
    private Bitmap      noir;
    private Bitmap      vert;
    private Bitmap 		win;
    private Bitmap 		loose;
    private Bitmap 		btn_rose;
    private Bitmap 		btn_vert;
    private Bitmap 		btn_violet;
    private Bitmap 		btn_rouge;
    private Bitmap 		btn_bleu;
    private Bitmap 		btn_jaune;
    private Bitmap 		btn_noir;
    private Bitmap 		btn_marron;
    private Bitmap 		start;

    //Canvas pour le jeu et Score
    public Canvas c,d;
    private static SharedPreferences prefs;
    public static int Score=0;
    public static int High_Score=0;
    int test=0;
    private String saveScore="High_Score";
private String saveCarte = "carte";
    // Declaration des objets Ressources et Context permettant d'acceder aux ressources de notre application et de les charger
    private Resources 	mRes;
    private Context 	mContext;

    // tableau modelisant la carte du jeu
    public static int[][] carte;

    // ancres pour pouvoir centrer la carte du jeu
    int carteTopAnchor;                   // coordonnees en Y du point d'ancrage de notre carte
    int carteLeftAnchor;                  // coordonnees en X du point d'ancrage de notre carte
    int couleurOr;
    int NbCoupsMax=0;
    int NbCoups=0;
    int NbCouleurs;
    boolean GrilleComplete;

    // taille de la carte
    static final int    carteWidth    = 13;
    static final int    carteHeight   = 13;
    static final int    carteTileSize = 21;
    p8_Sokoban instance = new p8_Sokoban();

    // constante modelisant les differentes types de cases
    static final int    CST_bleu    = 0;
    static final int    CST_violet   = 1;
    static final int    CST_rouge      = 2;
    static final int    CST_vert      = 3;
    static final int    CST_jaune      = 4;
    static final int    CST_marron      = 5;
    static final int    CST_noir     = 6;
    static final int    CST_rose      = 7;

    //booleen pour arreter la génération aléatoire
    public boolean in  = true;
    private TextView textView;

    // thread utiliser pour pouvoir relancer le jeu
    public Thread  cv_thread;
    SurfaceHolder holder;
    Paint paint;
    private int stateToSave = 0;


    /**
     * The constructor called from the main JetBoy activity
     *
     * @param context
     * @param attrs
     */
    public SokobanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        prefs=context.getSharedPreferences("p8.demo.p8sokoban", context.MODE_PRIVATE);
        String spackage= "p8.demo.p8sokoban";

        saveScore="HighScore";
        High_Score = prefs.getInt(saveScore, 0);
        //saveCarte = "carte";
        //carte = prefs.getInt(saveCarte, 0);
       // permet d'ecouter les surfaceChanged, surfaceCreated, surfaceDestroyed
        holder = getHolder();
        holder.addCallback(this);

        // chargement des images
        mContext	= context;
        mRes 		= mContext.getResources();
        bleu 		= BitmapFactory.decodeResource(mRes, R.drawable.bleu);
        vert 		= BitmapFactory.decodeResource(mRes, R.drawable.vert);
        jaune		= BitmapFactory.decodeResource(mRes, R.drawable.jaune);
        rouge 		= BitmapFactory.decodeResource(mRes, R.drawable.rouge);
        noir 		= BitmapFactory.decodeResource(mRes, R.drawable.noir);
        violet 		= BitmapFactory.decodeResource(mRes, R.drawable.violet);
        rose 		= BitmapFactory.decodeResource(mRes, R.drawable.rose);
        marron 		= BitmapFactory.decodeResource(mRes, R.drawable.marron);
        btn_vert 		= BitmapFactory.decodeResource(mRes, R.drawable.btn_vert);
        btn_violet 		= BitmapFactory.decodeResource(mRes, R.drawable.btn_violet);
        btn_rose		= BitmapFactory.decodeResource(mRes, R.drawable.btn_rose);
        btn_rouge 		= BitmapFactory.decodeResource(mRes, R.drawable.btn_rouge);
        btn_bleu        = BitmapFactory.decodeResource(mRes, R.drawable.btn_bleu);
        btn_jaune 		= BitmapFactory.decodeResource(mRes, R.drawable.btn_jaune);
        btn_noir 		= BitmapFactory.decodeResource(mRes, R.drawable.btn_noir);
        btn_marron 		= BitmapFactory.decodeResource(mRes, R.drawable.btn_marron);


        win 		= BitmapFactory.decodeResource(mRes, R.drawable.win);
        start 		= BitmapFactory.decodeResource(mRes, R.drawable.start);
        loose 		= BitmapFactory.decodeResource(mRes, R.drawable.loose);

        // creation du thread
        cv_thread   = new Thread(this);
        // initialisation des parmametres du jeu
        initparameters();

        // prise de focus pour gestion des touches
        setFocusable(true);
    }


    /**
     * Initialisation du jeu
     */
    public void initparameters() {
        paint = new Paint();
        paint.setColor(0xff0000);

        paint.setDither(true);
        paint.setColor(0xFFFFFF00);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(3);
        paint.setTextAlign(Paint.Align.LEFT);
        carte           = new int[carteHeight][carteWidth];
        carteTopAnchor  = (getHeight()- carteHeight*carteTileSize)-200;
        carteLeftAnchor = (getWidth()- carteWidth*carteTileSize)/2;

        if ((cv_thread!=null) && (!cv_thread.isAlive())) {
            cv_thread.start();
            Log.e("-FCT-", "cv_thread.start()");
        }
    }


	/**
     * Verifie si la grille est bien remplie ou pas
     * en parcourant tout le tableau et retournant true si il est bien rempli
     * @return
     */
    private boolean CarteComplete()
    {
        boolean isRempli=true;
        for (int i = 0; i < carteHeight; i++) {
            for (int j = 0; j < carteWidth; j++) {
                switch (carte[i][j]){
                    case CST_violet :
                        break;
                    case CST_bleu :
                        break;
                    case CST_rouge :
                        break;
                    case CST_vert :
                        break;
                    case CST_jaune :
                        break;
                    case CST_marron :
                        break;
                    case CST_noir :
                        break;
                    case CST_rose :
                        break;
                    default:
                        isRempli=false;
                }
            }
        }
        if (isRempli==false) {
            return false;
        }
        return true;
    }


    /**
     * Génère la grille de jeu aléatoirement
     * en generant deux nombres entre 0 et 12 pour remplir le tableau (grille)
     * en generant un autre nombre aleatoire entre 0 et 7
     * ensuite on place les couleurs sur la grille selon la valeur de nbAleat tant que la grille n'est pas remplie
     * @param NombreCouleurs
     */
    public void newlevel(int NombreCouleurs) {
        int nbAleatHauteur;
        int nbAleatLargeur;
        Random rand = new Random();
        int nbAleat;

        do {
            nbAleatHauteur = rand.nextInt((carteHeight-1) - 0 + 1) ;
            nbAleatLargeur = rand.nextInt((carteWidth-1) - 0 + 1) ;
            nbAleat = rand.nextInt(NombreCouleurs+1);

            if (nbAleat == 0) {
                carte[nbAleatLargeur][nbAleatHauteur] = CST_bleu;
            }
            if (nbAleat == 1) {
                carte[nbAleatLargeur][nbAleatHauteur] = CST_violet;
            }
            if (nbAleat == 2) {
                carte[nbAleatLargeur][nbAleatHauteur] = CST_rouge;
            }
            if (nbAleat == 3) {
                carte[nbAleatLargeur][nbAleatHauteur] = CST_vert;
            }
            if (nbAleat == 4) {
                carte[nbAleatLargeur][nbAleatHauteur] = CST_jaune;
            }
            if (nbAleat == 5) {
                carte[nbAleatLargeur][nbAleatHauteur] = CST_marron;
            }
            if (nbAleat == 6) {
                carte[nbAleatLargeur][nbAleatHauteur] = CST_noir;
            }
            if (nbAleat == 7) {
                carte[nbAleatLargeur][nbAleatHauteur] = CST_rose;
            }

        }while(!CarteComplete());
    }
	
	
    /**
     * Affiche une image au milieu de l'écran lorsque le joueur a gagné
     * @param canvas
     */
    private void paintwin(Canvas canvas) {
        canvas.drawBitmap(win, (carteLeftAnchor + 3 * carteTileSize)-52,carteTopAnchor + 4 * carteTileSize, null);
    }


    /**
     * Affiche une image au milieu de l'écran lorsque le joueur a perdu
     * @param canvas
     */
    private void paintloose(Canvas canvas) {
        canvas.drawBitmap(loose, (carteLeftAnchor + 3 * carteTileSize)-52,carteTopAnchor + 4 * carteTileSize, null);
    }


    /**
     * Affiche la bande "Commencer la partie"
     * @param canvas
     */
    private void paintstart(Canvas canvas) {
        canvas.drawBitmap(start, -40, (getHeight()- carteHeight*carteTileSize)+90, null);
    }


    /**
     * Affiche les boutons de couleurs
     * @param canvas
     */
    private void paintbuttons(Canvas canvas) {
        canvas.drawBitmap(btn_violet, 0, 450, null);
        canvas.drawBitmap(btn_bleu, 40, 450, null);
        canvas.drawBitmap(btn_jaune, 80, 450, null);
        canvas.drawBitmap(btn_marron, 120, 450, null);
        canvas.drawBitmap(btn_noir, 160, 450, null);
        canvas.drawBitmap(btn_rose, 200, 450, null);
        canvas.drawBitmap(btn_rouge, 240, 450, null);
        canvas.drawBitmap(btn_vert, 280, 450, null);
    }


    /**
     * Affiche la grille de jeu avec tous les carrés de couleurs
     * @param canvas
     */
    private void paintcarte(Canvas canvas) {
        for (int i=0; i< carteHeight; i++) {
            for (int j=0; j< carteWidth; j++) {
                switch (carte[i][j]) {
                    case CST_bleu:
                        canvas.drawBitmap(bleu, carteLeftAnchor+ j*carteTileSize, carteTopAnchor+ i*carteTileSize, null);
                        break;
                    case CST_jaune:
                        canvas.drawBitmap(jaune,carteLeftAnchor+ j*carteTileSize, carteTopAnchor+ i*carteTileSize, null);
                        break;
                    case CST_marron:
                        canvas.drawBitmap(marron,carteLeftAnchor+ j*carteTileSize, carteTopAnchor+ i*carteTileSize, null);
                        break;
                    case CST_noir:
                        canvas.drawBitmap(noir, carteLeftAnchor+ j*carteTileSize, carteTopAnchor+ i*carteTileSize, null);
                        break;
                    case CST_rose:
                        canvas.drawBitmap(rose, carteLeftAnchor+ j*carteTileSize, carteTopAnchor+ i*carteTileSize, null);
                        break;
                    case CST_rouge:
                        canvas.drawBitmap(rouge, carteLeftAnchor+ j*carteTileSize, carteTopAnchor+ i*carteTileSize, null);
                        break;
                    case CST_vert:
                        canvas.drawBitmap(vert, carteLeftAnchor+ j*carteTileSize, carteTopAnchor+ i*carteTileSize, null);
                        break;
                    case CST_violet:
                        canvas.drawBitmap(violet, carteLeftAnchor+ j*carteTileSize, carteTopAnchor+ i*carteTileSize, null);
                        break;
                }
            }
        }
    }


    /**
     * Méthode qui permet de dessiner toute la surface du jeu en appelant les méthodes paint... vues précédemment
     * @param canvas
     */
    public void nDraw(Canvas canvas) {
        canvas.drawRGB(255, 255, 255);
        paintcarte(canvas);
        paintstart(canvas);
        paintbuttons(canvas);
    }


    /**
     * Callback sur le cycle de vie de la surfaceview
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i("-> FCT <-", "surfaceChanged " + width + " - " + height);
        initparameters();
    }


    /**
     * Callback sur le score a afficher
     */
    public void surfaceCreated(SurfaceHolder arg0) {
        Log.i("-> FCT <-", "surfaceCreated");
        Score=0;
    }


    /**
     * Callback sur le highscore et sa sauvegarde
     */
    public void surfaceDestroyed(SurfaceHolder arg0) {
        Log.i("-> FCT <-", "surfaceDestroyed");
        Score=0;
        prefs.edit().putInt(saveScore, High_Score).commit();
        cv_thread=null;
    }


    /**
     * Méthode principale qui contient le coeur du jeu
     * C'est une méthode récursive qui permet de rechercher les cases de la même couleur
     * que la première case et de les changer avec la nouvelle couleur qu'on a sélectionnée
     * La recherche se fait récursivement en vérifiant les quatre cases qui se trouvent autour de la case courante
     * @param x
     * @param y
     * @param AncienneCouleur
     * @param NouvelleCouleur
     */
    public void floodFill(int x, int y, int AncienneCouleur, int NouvelleCouleur)
    {
        if(d==null) {
            d = holder.lockCanvas(null);
        }
        if ((x < 0) || (x >= carteWidth)) return;
        if ((y < 0) || (y >= carteHeight)) return;
        if (carte[y][x] == AncienneCouleur) {
            carte[y][x] = NouvelleCouleur;
            nDraw(d);
            floodFill(x + 1, y, AncienneCouleur, NouvelleCouleur);
            nDraw(d);
            floodFill(x, y + 1, AncienneCouleur, NouvelleCouleur);
            nDraw(d);
            floodFill(x - 1, y, AncienneCouleur, NouvelleCouleur);
            nDraw(d);
            floodFill(x, y - 1, AncienneCouleur, NouvelleCouleur);
        }
    }


    /**
     * run (run du thread cree)
     * on endort le thread, on modifie le compteur d'animation, on prend la main pour dessiner et on dessine puis on libere le canvas
	 * Cette méthode s'occupe donc de la génération continue de la grille de jeu jusqu'à que le joueur sélectionne le nombre de couleurs qu'il veut.
     */
    public void run() {
        if(in==true) {
            c = null;
        }

        while (in) {
            try {

                NbCouleurs=instance.getProgress();

                if(NbCouleurs==1) {
                    NbCoupsMax = 1;
                }
                if(NbCouleurs==1){ //2 COULEURS
                    NbCoupsMax=4;
                }
                if(NbCouleurs==2){ //3 COULEURS
                    NbCoupsMax=8;
                }
                if(NbCouleurs==3){ //4 COULEURS
                    NbCoupsMax=15;
                }
                if(NbCouleurs==4){ //5 COULEURS
                    NbCoupsMax=25;
                }
                if(NbCouleurs==5){ //6 COULEURS
                    NbCoupsMax=32;
                }
                if(NbCouleurs==6){ //7 COULEURS
                    NbCoupsMax=38;
                }
                if(NbCouleurs==7){ //8 COULEURS
                    NbCoupsMax=44;
                }

                try {

                    c = holder.lockCanvas(null);

                    newlevel(NbCouleurs);
                    nDraw(c);
                } finally {

                    if (c != null) {
                        holder.unlockCanvasAndPost(c);
                    }
                }
            } catch(Exception e) {
                Log.e("-> RUN <-", "PB DANS RUN");
            }
        }
        if(in==false){
            try {
                c = holder.lockCanvas(null);
                nDraw(c);
            } finally {

                if (c != null) {
                    holder.unlockCanvasAndPost(c);
                }
            }
        }
    }


    /**
     * Méthode permettant de récuperer les évenements tactiles
     * Appui sur le bouton "Commencer la partie", lancement du jeu et du chrono
     * Appui sur les boutons de couleurs, la gestion de la couleur des cases, l'affichage du score, du nombre de coups
     * Tant que la grille n'est pas completée avec la meme couleur, on continue
     * @param event
     * @return
     */
    public boolean onTouchEvent (MotionEvent event) {
        Log.i("-> FCT <-", "onTouchEvent: "+ event.getY());

        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getY() > 125 && event.getY() < 255) {
                if (event.getX() > 55 && event.getX() < 280) {
                    if (test == 1) {
                        Score = 0;
                        prefs.edit().putInt(saveScore, High_Score).commit();
                        instance.restartActivity(p8_Sokoban.self);
                    }
                }
            }

            if (event.getY() > 345 && event.getY() < 370) {
                NbCoups = 0;
                in = false;
                instance.seekBar.setEnabled(false);
                instance.LanceChrono();
            }

            if(in==false){
                if (event.getY() > 450 && event.getY() < 490) {
                    couleurOr = carte[0][0];
                    d = null;
                    if (event.getX() > 0 && event.getX() < 40) {
                        if (CST_violet <= NbCouleurs) {
                            GrilleComplete = true;
                            floodFill(0, 0, couleurOr, CST_violet);
                            Score += 5;
                            if (Score > High_Score) {
                                High_Score = Score;
                            }
                            NbCoups++;
                            if (d != null) {
                                holder.unlockCanvasAndPost(d);
                            }
                            if (NbCoups >= NbCoupsMax) {
                                Canvas e;
                                e = null;
                                e = holder.lockCanvas(null);
                                paintloose(e);
                                holder.unlockCanvasAndPost(e);
                                test = 1;
                            }
                            for (int i = 0; i < carteHeight; i++) {
                                for (int j = 0; j < carteWidth; j++) {
                                    if (carte[i][j] != CST_violet) {
                                        GrilleComplete = false;
                                    }
                                }
                            }
                        } else {
                            GrilleComplete = false;
                        }
                    }
                    if (event.getX() > 40 && event.getX() < 80) {
                        if (CST_bleu <= NbCouleurs) {
                            GrilleComplete = true;
                            floodFill(0, 0, couleurOr, CST_bleu);
                            Score += 5;
                            if (Score > High_Score) {
                                High_Score = Score;
                            }
                            NbCoups++;
                            if (d != null) {
                                holder.unlockCanvasAndPost(d);
                            }
                            if (NbCoups >= NbCoupsMax) {
                                Canvas e;
                                e = null;
                                e = holder.lockCanvas(null);
                                paintloose(e);
                                holder.unlockCanvasAndPost(e);
                                test = 1;
                            }
                            for (int i = 0; i < carteHeight; i++) {
                                for (int j = 0; j < carteWidth; j++) {
                                    if (carte[i][j] != CST_bleu) {
                                        GrilleComplete = false;
                                    }
                                }
                            }
                        } else {
                            GrilleComplete = false;
                        }
                    }
                    if (event.getX() > 80 && event.getX() < 120) {
                        if (CST_jaune <= NbCouleurs) {
                            GrilleComplete = true;
                            floodFill(0, 0, couleurOr, CST_jaune);
                            Score += 5;
                            if (Score > High_Score) {
                                High_Score = Score;
                            }
                            NbCoups++;
                            if (d != null) {
                                holder.unlockCanvasAndPost(d);
                            }
                            if (NbCoups >= NbCoupsMax) {
                                Canvas e;
                                e = null;
                                e = holder.lockCanvas(null);
                                paintloose(e);
                                holder.unlockCanvasAndPost(e);
                                test = 1;
                            }
                            for (int i = 0; i < carteHeight; i++) {
                                for (int j = 0; j < carteWidth; j++) {
                                    if (carte[i][j] != CST_jaune) {
                                        GrilleComplete = false;
                                    }
                                }
                            }
                        } else {
                            GrilleComplete = false;
                        }
                    }
                    if (event.getX() > 120 && event.getX() < 160) {
                        if (CST_marron <= NbCouleurs) {
                            GrilleComplete = true;
                            floodFill(0, 0, couleurOr, CST_marron);
                            Score += 5;
                            if (Score > High_Score) {
                                High_Score = Score;
                            }
                            NbCoups++;

                            if (d != null) {
                                holder.unlockCanvasAndPost(d);
                            }
                            if (NbCoups >= NbCoupsMax) {
                                Canvas e;
                                e = null;
                                e = holder.lockCanvas(null);
                                paintloose(e);
                                holder.unlockCanvasAndPost(e);
                                test = 1;
                            }
                            for (int i = 0; i < carteHeight; i++) {
                                for (int j = 0; j < carteWidth; j++) {
                                    if (carte[i][j] != CST_marron) {
                                        GrilleComplete = false;
                                    }
                                }
                            }
                        } else {
                            GrilleComplete = false;
                        }
                    }
                    if (event.getX() > 160 && event.getX() < 200) {
                        if (CST_noir <= NbCouleurs) {
                            GrilleComplete = true;
                            floodFill(0, 0, couleurOr, CST_noir);
                            Score += 5;
                            if (Score > High_Score) {
                                High_Score = Score;
                            }
                            NbCoups++;
                            if (d != null) {
                                holder.unlockCanvasAndPost(d);
                            }
                            if (NbCoups >= NbCoupsMax) {
                                Canvas e;
                                e = null;
                                e = holder.lockCanvas(null);
                                paintloose(e);
                                holder.unlockCanvasAndPost(e);
                                test = 1;
                            }
                            for (int i = 0; i < carteHeight; i++) {
                                for (int j = 0; j < carteWidth; j++) {
                                    if (carte[i][j] != CST_noir) {
                                        GrilleComplete = false;
                                    }
                                }
                            }
                        } else {
                            GrilleComplete = false;
                        }
                    }
                    if (event.getX() > 200 && event.getX() < 240) {
                        if (CST_rose <= NbCouleurs) {
                            GrilleComplete = true;
                            floodFill(0, 0, couleurOr, CST_rose);
                            Score += 5;
                            if (Score > High_Score) {
                                High_Score = Score;
                            }
                            NbCoups++;
                            if (d != null) {
                                holder.unlockCanvasAndPost(d);
                            }
                            if (NbCoups >= NbCoupsMax) {
                                Canvas e;
                                e = null;
                                e = holder.lockCanvas(null);
                                paintloose(e);
                                holder.unlockCanvasAndPost(e);
                                test = 1;
                            }
                            for (int i = 0; i < carteHeight; i++) {
                                for (int j = 0; j < carteWidth; j++) {
                                    if (carte[i][j] != CST_rose) {
                                        GrilleComplete = false;
                                    }
                                }
                            }
                        } else {
                            GrilleComplete = false;
                        }
                    }
                    if (event.getX() > 240 && event.getX() < 280) {
                        if (CST_rouge <= NbCouleurs) {
                            GrilleComplete = true;
                            floodFill(0, 0, couleurOr, CST_rouge);
                            Score += 5;
                            if (Score > High_Score) {
                                High_Score = Score;
                            }
                            NbCoups++;
                            if (d != null) {
                                holder.unlockCanvasAndPost(d);
                            }
                            if (NbCoups >= NbCoupsMax) {
                                Canvas e;
                                e = null;
                                e = holder.lockCanvas(null);
                                paintloose(e);
                                holder.unlockCanvasAndPost(e);
                                test = 1;
                            }
                            for (int i = 0; i < carteHeight; i++) {
                                for (int j = 0; j < carteWidth; j++) {
                                    if (carte[i][j] != CST_rouge) {
                                        GrilleComplete = false;
                                    }
                                }
                            }
                        } else {
                            GrilleComplete = false;
                        }
                    }
                    if (event.getX() > 280 && event.getX() < 320) {
                        if (CST_vert <= NbCouleurs) {
                            GrilleComplete = true;
                            floodFill(0, 0, couleurOr, CST_vert);
                            Score += 5;
                            if (Score > High_Score) {
                                High_Score = Score;
                            }
                            NbCoups++;
                            if (d != null) {
                                holder.unlockCanvasAndPost(d);
                            }
                            if (NbCoups >= NbCoupsMax) {
                                Canvas e;
                                e = null;
                                e = holder.lockCanvas(null);
                                paintloose(e);
                                holder.unlockCanvasAndPost(e);
                                test = 1;
                            }
                            for (int i = 0; i < carteHeight; i++) {
                                for (int j = 0; j < carteWidth; j++) {
                                    if (carte[i][j] != CST_vert) {
                                        GrilleComplete = false;
                                    }
                                }
                            }
                        } else {
                            GrilleComplete = false;
                        }
                    }


                    instance.MajCoups();


                    if (GrilleComplete == true) {
                        Canvas e;
                        e = null;
                        e = holder.lockCanvas(null);
                        paintwin(e);
                        holder.unlockCanvasAndPost(e);
                        test = 1;
                    }
                }
            }
        }
        return super.onTouchEvent(event);
    }

}
