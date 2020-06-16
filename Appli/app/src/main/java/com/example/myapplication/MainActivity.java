package com.example.myapplication;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // decalration des objets
    private Button saveButton, clearButton;
    private drawingview drawingView;
    private TextView textViewResult;
    private ImageView imageViewResult;
    private ImageView cadre2;

    private Classifier classifier;

    private Executor executor = Executors.newSingleThreadExecutor();

    private static final int INPUT_SIZE = 28; //taille de l image

    //fichiers avec les parametres du reseau de neurones, ils sont dans les assets
    private static final String MODEL_PATH = "tf_lite_model_quant.tflite"; //le chemin
    private static final boolean QUANT = false;
    private static final String LABEL_PATH = "labels2.txt";



    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeUI();
        setListeners();

        initTensorFlowAndLoadModel();
    }

    private void setListeners() {
        //cree les boutons sauve_image et clear
        saveButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);

        //fait apparaitre le cadre des resultats
        cadre2.setImageResource(R.drawable.cadre2);
    }

    //initialise les objets en leur affectant une identite
    private void initializeUI() {
        drawingView = findViewById(R.id.scratch_pad);
        saveButton = findViewById(R.id.save_button);
        clearButton = findViewById(R.id.clear_button);
        textViewResult = findViewById(R.id.textViewResult);
        imageViewResult = findViewById(R.id.imageViewResult);
        cadre2 = findViewById(R.id.cadre2);
    }


    //au moment ou on clique sur le bouton
    @Override public void onClick(View view) {
        switch (view.getId()) {

            //le bouton save_image reconnait le chiffre dessine sur l ecran
            case R.id.save_button:

                //recupere l image mise dans les dimensions
                Bitmap bitmap = resizeImage.getBitmap2();

                //affiche l image dans le cadre des resultats
                imageViewResult.setImageBitmap(bitmap);

                //calcul des resultats
                final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);

                //affichage des resultats dans le cadre des resultats
                textViewResult.setText(results.toString());

                break;

            // le bouton clear efface la zone de dessin et la rend noire
            case R.id.clear_button:
                drawingView.clear();

                Bitmap bitmap_clear = drawingview.getBitmap();

                bitmap_clear = Bitmap.createScaledBitmap(bitmap_clear, INPUT_SIZE, INPUT_SIZE, false);

                imageViewResult.setImageBitmap(bitmap_clear);

                //on affiche qu on peut recommencer
                textViewResult.setText("recommence");
                break;

            default:
                break;
        }

    }


    //si on quitte l appli, le classifier se ferme
    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                classifier.close();
            }
        });
    }

    //cree un classifier
    private void initTensorFlowAndLoadModel() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = TensorFlowImageClassifier.create(
                            getAssets(),
                            MODEL_PATH,
                            LABEL_PATH,
                            INPUT_SIZE,
                            QUANT);
                    makeButtonVisible();
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });
    }


    //si tout marche bien les boutons apparaissent
    private void makeButtonVisible() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                saveButton.setVisibility(View.VISIBLE);
            }
        });
    }
}