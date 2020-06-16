package com.example.myapplication;

import android.graphics.Bitmap;


/* redimensionne l image pour optimiser la reconnaissance
* le but est de faire rentrer le chiffre dans un carre de 16x17 pixels
* on recupere les 4pixels extremes haut, bas, droite, gauche
* un pixel extreme a droite est le pixel blanc le plus a droite
* un pixel extreme en haut est le pixel blanc le plus en haut
* etc*/
public class resizeImage {
    static int haut = 0;
    static int bas = 0;
    static int droite = 0;
    static int gauche = 0;
    private static Bitmap chiffre;
    private static Bitmap lebon;

    static int LARGE = 28;
    static int HAUTE = 49;

    //on recupere le bitmap sur lequel est dessine le chiffre
    public static Bitmap prendBitmap(){
        Bitmap bitmap2 = drawingview.getBitmap();
        Bitmap bitmap = Bitmap.createScaledBitmap(bitmap2, LARGE, HAUTE,false);

        return bitmap;
    }

    //les quatre fonctions suivantes recuperent les 4 pixels extremes du bitmap sur lequel est dessine le chiffre
    public static int bas(int hauteur,int largeur){
        for (int i = 0; i<hauteur-1; ++i){
            for (int j = 0; j<largeur-1; ++j){
                if (prendBitmap().getPixel(j, i) != prendBitmap().getPixel(0, 0)){
                    bas = i;
                }
            }
        }
        if (bas != HAUTE){
            bas++;
        }
        return bas;
    }

    public static int haut(int hauteur,int largeur){
        for (int i = 0; i<hauteur; ++i){
            for (int j = 0; j<largeur; ++j){
                if (prendBitmap().getPixel(largeur-1 - j, hauteur-1 - i) != prendBitmap().getPixel(0, 0)){
                    haut = hauteur-1 - i;
                }
            }
        }
        if (haut != 0){
            haut--;
        }
        return haut;
    }

    public static int droite(int hauteur,int largeur){
        for (int j = 0; j<largeur; ++j){
            for (int i = 0; i<hauteur; ++i){
                if (prendBitmap().getPixel(j, i) != prendBitmap().getPixel(0, 0)){
                    droite = j;
                }
            }
        }
        if (droite != LARGE){
            droite++;
        }
        return droite;
    }

    public static int gauche(int hauteur,int largeur){
        for (int j = 0; j<largeur; ++j){
            for (int i = 0; i<hauteur; ++i){
                if (prendBitmap().getPixel(largeur-1 - j, hauteur-1 - i) != prendBitmap().getPixel(0, 0)){
                    gauche = largeur-1 - j;
                }
            }
        }
        if (gauche != 0){
            gauche--;
        }
        return gauche;
    }

    /*cette fonction renvoie le bitmap final dans les bonnes dimensions
    * le chiffre rentre dans un carre de 16x17 pixels
    * ce carre est centre dans un bitmap de 28x28 pixels */
    public static Bitmap getBitmap2(){
        int hauteur = prendBitmap().getHeight();
        int largeur = prendBitmap().getWidth();

        haut = haut(hauteur,largeur);
        bas = bas(hauteur,largeur);
        droite = droite(hauteur,largeur);
        gauche = gauche(hauteur,largeur);

        if (droite-gauche < 9 & droite < LARGE - 5 & gauche > 5){
            gauche = gauche - 5;
            droite = droite + 5;
        }

        System.out.print("haut : ");
        System.out.println(haut);
        System.out.print("bas : ");
        System.out.println(bas);
        System.out.print("droite : ");
        System.out.println(droite);
        System.out.print("gauche : ");
        System.out.println(gauche);


        chiffre = Bitmap.createBitmap(prendBitmap(), gauche, haut,droite-gauche,bas-haut);
        chiffre = Bitmap.createScaledBitmap(chiffre, 16, 17,false);

        lebon = Bitmap.createScaledBitmap(prendBitmap(),28,28, false);
        lebon.eraseColor(-16777216);
        for (int i = 0; i<16; ++i) {
            for (int j = 0; j < 17; ++j) {
                lebon.setPixel(i+6, j+7, chiffre.getPixel(i, j));
            }
        }

        return lebon;
    }

}
