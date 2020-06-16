package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;

import org.tensorflow.lite.Interpreter; //pour que ca marche faut modifier le build.gradle

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/* cette classe calcule les resultats de la reconnaissance d image suite a l envoi d une image
* depuis la classe MainActivity
* elle cree un classifier qui est ensuite exploite par la classe Classifier
* qui renvoie le resultat correctement interprete*/
public class TensorFlowImageClassifier implements Classifier {

    //autorise le nombre de resultats
    private static final int MAX_RESULTS = 1;

    //seuil de probas selon lequel on accepte un resultat
    private static final float THRESHOLD = 0;

    private static final int IMAGE_MEAN = 0;
    private static final float IMAGE_STD = 128.0f;

    //l interpreter permet de faire les calculs avec les parametres du reseau de neurones
    //c est l objet le plus important de la classe
    private Interpreter interpreter;

    //taille de l image
    private int inputSize;

    //liste des labels, ici ce sont les chiffres de 0 a 9
    private List<String> labelList;
    private boolean quant;

    private TensorFlowImageClassifier() {

    }

    //creation du classifier qui repertorie tous les resultats
    static Classifier create(AssetManager assetManager,
                             String modelPath,
                             String labelPath,
                             int inputSize,
                             boolean quant) throws IOException {

        TensorFlowImageClassifier classifier = new TensorFlowImageClassifier();
        classifier.interpreter = new Interpreter(classifier.loadModelFile(assetManager, modelPath), new Interpreter.Options());
        classifier.labelList = classifier.loadLabelList(assetManager, labelPath);
        classifier.inputSize = inputSize;
        classifier.quant = quant;

        return classifier;
    }


    /* prend le bitmap a reconnaitre et renvoie la liste des resultats
    * cette fonction utilise toutes les suivantes */
    @Override
    public List<Recognition> recognizeImage(Bitmap bitmap) {
        ByteBuffer byteBuffer = convertBitmapToByteBuffer(bitmap);

        if(quant){
            byte[][] result = new byte[1][labelList.size()];

            //calcule l array de probalilites results
            interpreter.run(byteBuffer, result);

            System.out.println("result apres interpreter");

            for(int i = 0; i < result[0].length; i++) {
                System.out.print("result[");
                System.out.print(i);
                System.out.print("] : ");
                System.out.println(result[0][i]);
            }

            //renvoie la liste des resultats
            System.out.println(getSortedResultByte(result));

            return getSortedResultByte(result);
        } else {
            float [][] result = new float[1][labelList.size()];

            //calcule l array de probalilites results
            interpreter.run(byteBuffer, result);

            System.out.println("result apres interpreter");

            for(int i = 0; i < result.length; i++) {
                for (int j = 0; j < result[i].length; j++){
                    System.out.println(result[i][j]);
                }
            }

            System.out.println(getSortedResultFloat(result));

            //renvoie la liste des resultats
            return getSortedResultFloat(result);
        }

    }

    @Override
    public void close() {
        interpreter.close();
        interpreter = null;
    }

    //prend en entree le fichier des parametres et renvoie les parametres expoitables par l interpreter
    private MappedByteBuffer loadModelFile(AssetManager assetManager, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    //prend en entree le fichier des labels et renvoie la liste des labels
    private List<String> loadLabelList(AssetManager assetManager, String labelPath) throws IOException {
        List<String> labelList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open(labelPath)));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }

    //convertit le bitmap en byteBuffer pour qu il soit exploitable par l interpreter
    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer;

        if(quant) {
            byteBuffer = ByteBuffer.allocateDirect(inputSize * inputSize);
        } else {
            byteBuffer = ByteBuffer.allocateDirect(4 * inputSize * inputSize);
        }

        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[inputSize * inputSize];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int pixel = 0;

        for (int i = 0; i < inputSize; ++i) {
            for (int j = 0; j < inputSize; ++j) {
                final int val = intValues[pixel++];

                if(quant){
                    byteBuffer.put((byte) ((val >> 16) & 0xFF));
                    byteBuffer.put((byte) ((val >> 8) & 0xFF));
                    byteBuffer.put((byte) (val & 0xFF));
                } else {
                    byteBuffer.putFloat((((val >> 8) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                }

            }
        }
        return byteBuffer;
    }

    /* les deux fonctions suivantes en fonction de si le fichier est QUANT fonct la meme chose
    * elles prennent en entree l array de probas de l image qui doit etre reconnue
    * elles renvoient le label associe a l image*/
    @SuppressLint("DefaultLocale")
    private List<Recognition> getSortedResultByte(byte[][] labelProbArray) {

        PriorityQueue<Recognition> pq =
                new PriorityQueue<>(
                        MAX_RESULTS,
                        new Comparator<Recognition>() {
                            @Override
                            public int compare(Recognition lhs, Recognition rhs) {
                                return Float.compare(rhs.getConfidence(), lhs.getConfidence());
                            }
                        });

        for (int i = 0; i < labelList.size(); ++i) {
            float confidence = (labelProbArray[0][i] & 0xff) / 255.0f;
            if (confidence >= THRESHOLD) {
                pq.add(new Recognition("" + i,
                        labelList.size() > i ? labelList.get(i) : "unknown",
                        confidence, quant));
            }
        }

        final ArrayList<Recognition> recognitions = new ArrayList<>();
        int recognitionsSize = Math.min(pq.size(), MAX_RESULTS);
        for (int i = 0; i < recognitionsSize; ++i) {
            recognitions.add(pq.poll());
        }

        return recognitions;
    }

    @SuppressLint("DefaultLocale")
    private List<Recognition> getSortedResultFloat(float[][] labelProbArray) {

        PriorityQueue<Recognition> pq =
                new PriorityQueue<>(
                        MAX_RESULTS,
                        new Comparator<Recognition>() {
                            @Override
                            public int compare(Recognition lhs, Recognition rhs) {
                                return Float.compare(rhs.getConfidence(), lhs.getConfidence());
                            }
                        });

        for (int i = 0; i < labelList.size(); ++i) {
            float confidence = labelProbArray[0][i];
            if (confidence > THRESHOLD) {
                pq.add(new Recognition("" + i,
                        labelList.size() > i ? labelList.get(i) : "unknown",
                        confidence, quant));
            }
        }

        final ArrayList<Recognition> recognitions = new ArrayList<>();
        int recognitionsSize = Math.min(pq.size(), MAX_RESULTS);
        for (int i = 0; i < recognitionsSize; ++i) {
            recognitions.add(pq.poll());
        }

        return recognitions;
    }

}
