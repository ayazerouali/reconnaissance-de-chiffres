from tensorflow import keras
from tensorflow.keras import layers

''' ce script permet d evaluer un modele entraine, pour cela il faut rentrer
le nom du modele dans la ligne 9'''


# Recreate the exact same model purely from the file
new_model = keras.models.load_model('path_to_my_model2.h5')


#chargement de la base de donnees mnist
(x_train, y_train), (x_test, y_test) = keras.datasets.mnist.load_data()

# Preprocess the data (these are Numpy arrays)
#tableau de 10000 lignes (une pour chaque image)
#et 784 colonnes (chaque image a 784 pixels) pour le test
x_test = x_test.reshape(10000, 784).astype('float32') / 255


import numpy as np

# Chargement des predictions
new_predictions = new_model.predict(x_test)

'''la ligne suivante compare les predictions chargees avec les predictions
calculees directement, mais elles n apparaissent pas dans ce fihier'''
#np.testing.assert_allclose(predictions, new_predictions, rtol=1e-6, atol=1e-6)


import matplotlib.pyplot as plt

    
#prediction de l image i
def prediction(i):
    '''
    fonction qui predit le label d'une image
    prend en entree le numero de l image a reconnaitre et les parametres du modele
    renvoie la liste [probabilite en %, nombre reconnu]
    '''
    
    probas = np.array(new_model.predict_on_batch(np.array([x_test[i]])))[0]
    nombre = [- np.Inf,None]
    for i in range (0,len(probas)):
        if nombre[0]<=probas[i]:
            nombre = [probas[i], i]
    return nombre[1]

#affichage de m image
def displayImage(i):

    '''
    fonction qui reconnait une image et l affiche avec en titre son label
    entree :
    i -- le numero de l image 
    '''
    
    plt.title('le nombre est un {0}'.format(prediction(i)))
    plt.imshow(x_test[i,:].reshape([28,28]), cmap='gray')
    plt.show()
        


def disp_plusieursImages(nb, start):

    '''
    fonction qui reconnait plusieurs images et affiche l'image avec en titre son label
    entree :
    nb -- le nombre d image (c est mieux si c est un multiple de 3)
    start -- le numero d image sur lequel on commence
    parameters -- les parametres du modele
    
    '''
    
    #plt.figure(figsize=(7.195, 3.841), dpi=100)
    for i in range(nb):
        plt.subplot(3,nb/3,i+1)
        titre = plt.title('le nombre est {0}'.format(prediction(start+i))); titre.set_fontsize(6)
        plt.imshow(x_test[start+i,:].reshape([28,28]), cmap='gray')
        plt.axis('off')
    plt.show()

#displayImage(267)
disp_plusieursImages(18, 1934)

