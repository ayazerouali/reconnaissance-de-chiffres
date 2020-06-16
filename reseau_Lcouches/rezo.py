from keras.datasets import mnist
import numpy as np

# on récupère les données
(X_train, y_train), (X_test, y_test) = mnist.load_data()


# on redimensionne les images :  28x28 image -> 784 dim. vector
X_train = X_train.reshape(60000, 784)
X_test = X_test.reshape(10000, 784)
X_train = X_train.astype('float32')
X_test = X_test.astype('float32')


# Normalization
X_train /= 255
X_test /= 255


from keras.utils import np_utils

K=10

# convert class vectors to binary class matrices
# vecteur de 60000 elements qui sont les labels de X_train
Y_train = np_utils.to_categorical(y_train, K)
# vecteur de 10000 elements qui sont les labels de X_test
Y_test = np_utils.to_categorical(y_test, K)

#print(Y_test)


import h5py
import matplotlib.pyplot as plt

import sys
sys.path.insert(0, "/Users/thomasb/Desktop/CS_2A/projet_2A/apprentissage")
import L_layers as L


#ici on va entrainer un modele sur 2000 images et le tester sur 10000 images
X = X_train[0:2000,:].T
Y = Y_train[0:2000,:].T
images = X_test.T
labels = Y_test
layers_dim = [784,20,10]

#print(L.L_layer_model(X,Y,layers_dim,0.1,3000, True)) # ca a l air de marcher

def prediction(i, parameters):
    '''
    fonction qui predit le label d'une image
    prend en entree le numero de l image a reconnaitre et les parametres du modele
    renvoie la liste [nombre reconnu, sa probabilite en %]
    '''

    probas = L.L_model_forward(np.array([X_test[i]]).T, parameters)[0]
    nombre = [- np.Inf,None]
    for i in range (0,len(probas)):
        if nombre[0]<=probas[i]:
            nombre = [probas[i], i]
    nombre[0] = np.floor(nombre[0]*100)
    return nombre

def disp_plusieursImages(nb, start, parameters):

    '''
    fonction qui reconnait plusieurs images et affiche l'image avec en titre son label
    entree :
    nb -- le nombre d image (c est mieux si c est un multiple de 3)
    start -- le numero d image sur lequel on commence
    parameters -- les parametres du modele
    
    '''

    for i in range(nb):
        plt.subplot(3,nb/3,i+1)
        nombre = prediction(start+i, parameters)
        titre = plt.title('le nombre est {0} a {1}%'.format(nombre[1],np.squeeze(nombre[0]))); titre.set_fontsize(4)
        plt.imshow(X_test[start+i,:].reshape([28,28]), cmap='gray')
        plt.axis('off')
    plt.show()

def accuracy(images, labels, parameters):
    '''inupts :
    images : les images  tester (X_test) en taille 784*(nb d exemple)
    labels : les labels associes (Y_test) en taille (nb d exemple)*10
    parameters : les parametres du modele'''
    pred = (L.L_model_forward(images, parameters)[0]).T

    return np.where(pred.argmax(axis=1) != labels.argmax(axis=1) , 0.,1.).mean()*100.0

import os

def model_save_py(X,Y,layers_dim,learning_rate,iterations, print_cost, X_test, Y_test):
    '''save parameters and data of the model
    inputs :
    X -- data to train the model, size (inputs, nb of examples)
    Y -- true label matrix, size (nb of classes, nb of examples)
    layers_dims -- list containing the input size and each layer size, of length (number of layers + 1).
    learning_rate -- learning rate of the gradient descent update rule
    num_iterations -- number of iterations of the optimization loop
    print_cost -- if True, it prints the cost every 100 steps
    X_test -- data to test the model, size (inputs, number of examples)
    Y_test -- labels of X_test, size (nb of examples, nb of classes)

    returns :
    python file with every information about the model, including
    accuracy -- accuracy of the model on the data X_test
    parameters -- dictionnary of the parameters
    
    the python file stores all the parameters necessary to use the model for predictions '''



    
    parameters = L.L_layer_model(X,Y,layers_dim,learning_rate,iterations, print_cost)
    accu = accuracy(X_test, Y_test, parameters)
    
    file = open("model3.py","w")

    filepath = os.path.join("/Users/thomasb/Desktop/CS_2A/projet_2A/apprentissage", "model3.py")
    
    file.write("'''reseau de neurones a "+str(len(layers_dim)-1)+" couches pour la reconnaissances d'images. '''\n")
    file.write("import numpy as np \n")
    file.write("layers_dim = " + str(layers_dim) + "\n")
    file.write("learning_rate = " + str(learning_rate) + "\n")
    file.write("iterations = " +str(iterations) + "\n")
    file.write("accuracy = " +str(accu) + "\n")
    file.write("parameters = {")
    for l in range(1, len(layers_dim)):
        file.write("'W"+str(l)+ "' : np.array([[")
        for i in range (len(parameters["W"+str(l)])):
            for j in range (len(parameters["W"+str(l)][i])):
                if j != (len(parameters["W"+str(l)][i])-1):
                    file.write(str(parameters["W"+str(l)][i][j])+", ")
                elif (i == (len(parameters["W"+str(l)])-1)) and (j == (len(parameters["W"+str(l)][i])-1)):
                    file.write(str(parameters["W"+str(l)][i][j])+"]]), \n")
                else :
                    file.write(str(parameters["W"+str(l)][i][j])+"], [")
                    
                if j%8 == 0 :
                    file.write("\n")
        
        file.write("'b"+str(l)+ "' : np.array([")
        for i in range (len(parameters["b"+str(l)])):
            if i != (len(parameters["b"+str(l)])-1):
                file.write("["+str(parameters["W"+str(l)][i][0])+"], ")
            else :
                file.write("["+str(parameters["W"+str(l)][i][0])+"]])")
            if l == (len(layers_dim)-1) and i == (len(parameters["b"+str(l)])-1):
                file.write("}")
            elif i == (len(parameters["b"+str(l)])-1) and l != (len(layers_dim)-1):
                file.write(",")
            else :
                file.write("\n")
        file.write("\n")
                
    file.close()

#model_save_py(X,Y,layers_dim,0.1,3000, True, images, labels)

import pandas as pd

def model_save_h5(X,Y,layers_dim,learning_rate,iterations, print_cost):
    parameters = L.L_layer_model(X,Y,layers_dim,learning_rate,iterations, print_cost)
    
    store = pd.HDFStore('data.h5')

    filepath = os.path.join("/Users/thomasb/Desktop/CS_2A/projet_2A/apprentissage", "data.h5")
    
    store['parameters'] = parameters
    store.close()

#model_save_h5(X,Y,layers_dim,0.1,3000, True)
#ca marche pas manque la dependencies tables du module pandas je sais pas c est quoi
