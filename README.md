# reconnaissance-de-chiffres

Dossier Appli : 
Version de base de l'application de reconnaissance de chiffres manuscrits

Dossier appli_sudoku : 
Version qui donne la fonctionnalité de pouvoir dessiner plusieurs chiffres en même temps, dans le but de réaliser un sudoku interactif

Dossier reseau_Lcouches : 
Répertorie tous les fichiers python qui ont servi à générer les paramètres du réseau de neurones exploité par l'application pour la reconnaissance des chiffres

-L_Layers et L_layers_batch : scripts python de l'architecture de chaque réseau; L_layers étant codé pour entrainer un réseau sur l'ensemble des données d'entrainement; L_layers_batch étant codé pour entrainer un réseau sur des batchs des données

-rezo et rezo_saved : scripts pyhton servant à entrainer chaque réseau et renvoyer en sortie les paramètres finaux ainsi que les caractéristiques des chaque réseau dans les fichiers "model"

-model1, model3 et model_batch : fichiers python avec les caractéristiques de chaque réseau et les paramètres finaux. model1 et model3 sont les caractéristiques de 2 réseaux entrainés sur l'architecture L_layers; et model_batch regroupe les caractéristiques d'un réseau entrainé sur l'architecture L_layers_batch

-reseau_saved : script python permettant de tester les modèles sauvegardés

-les fichiers .png représentent l'évolution de la fonction de cout des modèles sauvegardés

Dossier reseau_keras : 
Répertorie tous les fichiers python qui ont permis de générer les paramètres du réseau de neurones avec keras

-train_evaluate : script pyhton qui permet de créer un réseau de neurones, de l'entrainer et de l'exporter (il permet de créer le fichier path_to_my_model2.h5 qui contient les paramètres du réseau en format .h5)

-evaluate : script pyhton qui permet d'évaluer un réseau de neurones

-converter : script pyhton qui permet de convertir un fichier .h5 en un fichier .tflite

-tf_lite_model_float.tflite et tf_lite_model_quant.tflite : fichiers qui permettent d'exploiter le réseau de neurones sur l'application

-labels.txt : liste des labels, chiffres de 0 à 9
