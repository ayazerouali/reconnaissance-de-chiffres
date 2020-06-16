package com.example.myapplication;

import android.graphics.Bitmap;

import java.util.List;

/* un classifier est une liste qui contient les resultats de la reconnaissance d images
* ici on decide d afficher seulement le chiffre reconnu : "c est le chiffre 7" par exemple */
public interface Classifier {

    class Recognition {
        /**
         * A unique identifier for what has been recognized. Specific to the class, not the instance of
         * the object.
         */
        private final String id;

        /**
         * Display name for the recognition.
         */
        private final String title;

        /**
         * Whether or not the model features quantized or float weights.
         */
        private final boolean quant;

        /**
         * A sortable score for how good the recognition is relative to others. Higher should be better.
         */
        private final Float confidence;

        public Recognition(
                final String id, final String title, final Float confidence, final boolean quant) {
            this.id = id;
            this.title = title;
            this.confidence = confidence;
            this.quant = quant;
        }


        public Float getConfidence() {
            return confidence;
        }

        @Override
        public String toString() {
            String resultString = "c'est le chiffre ";

            if (title != null) {
                resultString += title + " ";
            }

            return resultString.trim();
        }
    }


    List<Recognition> recognizeImage(Bitmap bitmap);

    void close();
}
