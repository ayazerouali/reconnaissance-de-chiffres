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
    private Button saveButton, clearButton;
    private drawingview drawingView;
    private TextView textViewResult;
    private ImageView imageViewResult;
    private ImageView cadre2;

    private Classifier classifier;

    private Executor executor = Executors.newSingleThreadExecutor();

    private static final int INPUT_SIZE = 28; //va falloir changer la taille
    private static final String MODEL_PATH = "tf_lite_model_quant.tflite"; //le chemin
    //private static final String MODEL_PATH = "mobilenet_quant_v1_224.tflite";
    private static final boolean QUANT = false;
    private static final String LABEL_PATH = "labels2.txt";
    //private static final String LABEL_PATH = "labels.txt";


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeUI();
        setListeners();

        initTensorFlowAndLoadModel();
    }

    private void setListeners() {
        saveButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);
        cadre2.setImageResource(R.drawable.cadre2);
    }

    private void initializeUI() {
        drawingView = findViewById(R.id.scratch_pad);
        saveButton = findViewById(R.id.save_button);
        clearButton = findViewById(R.id.clear_button);
        textViewResult = findViewById(R.id.textViewResult);
        imageViewResult = findViewById(R.id.imageViewResult);
        cadre2 = findViewById(R.id.cadre2);
    }


    @Override public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_button:

                //recuperer l image

                //drawingView.loadImage(BitmapFactory.decodeResource(getResources(), R.raw.chiffre5));

                //Bitmap bitmap = drawingview.getBitmap();

                Bitmap bitmap = resizeImage.getBitmap2();

                //bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);

                imageViewResult.setImageBitmap(bitmap);

                final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);

                //textViewResult.setTextColor(Color.RED);

                textViewResult.setText(results.toString());

                break;
            case R.id.clear_button:
                drawingView.clear();

                Bitmap bitmap_clear = drawingview.getBitmap();

                bitmap_clear = Bitmap.createScaledBitmap(bitmap_clear, INPUT_SIZE, INPUT_SIZE, false);

                imageViewResult.setImageBitmap(bitmap_clear);

                textViewResult.setText("recommence");
                break;
            default:
                break;
        }

    }

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

    private void makeButtonVisible() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                saveButton.setVisibility(View.VISIBLE);
            }
        });
    }
}