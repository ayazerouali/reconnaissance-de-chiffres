import model_batch as model
import rezo as r

import L_layers as L

from keras.datasets import mnist
import numpy as np
import matplotlib.pyplot as plt

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
# vecteur de 10000 elements qui sont les labels de X_train
Y_test = np_utils.to_categorical(y_test, K)

parameters = model.parameters

r.disp_plusieursImages(9,67, parameters)
print(r.accuracy(X_test.T, Y_test, parameters))
print((L.L_model_forward(X_test.T, parameters)[0]).T.shape)
