from tensorflow import keras
from tensorflow.keras import layers

''' ce script permet de creer un reseau de neurones avec la librairie keras,
de l entrainer puis de le tester
il permet auxxi d exporter le modele au format .h5'''

#creation de la variable inputs
inputs = keras.Input(shape=(784,), name='digits')
#creation de la premiere couche, avec pour entree les inputs
x = layers.Dense(64, activation='relu', name='dense_1')(inputs)
#deuxieme couche avec pour entree la premiere couche
x = layers.Dense(64, activation='relu', name='dense_2')(x)
#creation de la variable sortie apres une troisieme couche
outputs = layers.Dense(10, name='predictions')(x)

#mise en place du modele, qui prend en entree les inputs et sort les outputs
model = keras.Model(inputs=inputs, outputs=outputs)

#chargement de la base de donnees mnist
(x_train, y_train), (x_test, y_test) = keras.datasets.mnist.load_data()

# Preprocess the data (these are Numpy arrays)
#tableau de 60000 lignes (une pour chaque image)
#et 784 colonnes (chaque image a 784 pixels) pour entrainemant du reseau
x_train = x_train.reshape(60000, 784).astype('float32') / 255
#tableau de 10000 lignes (une pour chaque image)
#et 784 colonnes (chaque image a 784 pixels) pour le test
x_test = x_test.reshape(10000, 784).astype('float32') / 255

#labels des images x_train (il y en a 60 000)
y_train = y_train.astype('float32')
#labels des images x_test (il y en a 10 000)
y_test = y_test.astype('float32')


# Reserve 10,000 samples for validation
#tableau de 10000 images pour la validation (les dernieres 10000 imges de x_train
x_val = x_train[-10000:]
#labels de x_val
y_val = y_train[-10000:]
#tableau de 50000 images pour l'entrainement
x_train = x_train[:-10000]
#labels de x_train
y_train = y_train[:-10000]

##import numpy as np
##i = 105
##print(x_test[i])
##print(np.array(x_test[i]))
##print(np.array([x_test[i]]))
##exit(-1)



model.compile(optimizer=keras.optimizers.RMSprop(),  # Optimizer
              # Loss function to minimize
              loss=keras.losses.SparseCategoricalCrossentropy(from_logits=True),
              # List of metrics to monitor
              metrics=['sparse_categorical_accuracy'])


print('# Fit model on training data')
# la commande .fit pour entrainer le modele
history = model.fit(x_train, y_train,
                    batch_size=64,
                    epochs=3,
                    # We pass some validation for
                    # monitoring validation loss and metrics
                    # at the end of each epoch
                    validation_data=(x_val, y_val))

print('\nhistory dict:', history.history)


# Evaluate the model on the test data using `evaluate`
print('\n# Evaluate on test data')
results = model.evaluate(x_test, y_test, batch_size=128)
print('test loss, test acc:', results)

# Generate predictions (probabilities -- the output of the last layer)
# on new data using `predict`
##print('\n# Generate predictions for 3 samples')
##predictions = model.predict(x_test[:3])
##print('predictions shape:', predictions.shape)

import matplotlib.pyplot as plt
import numpy as np

#prediction de l image i
def prediction(i):
    '''
    fonction qui predit le label d'une image
    prend en entree le numero de l image a reconnaitre et les parametres du modele
    renvoie la liste [probabilite en %, nombre reconnu]
    '''
    probas = np.array(model.predict_on_batch(np.array([x_test[i]])))[0]
    nombre = [- np.Inf,None]
    for i in range (0,len(probas)):
        if nombre[0]<=probas[i]:
            nombre = [probas[i], i]
    return nombre[1]

#affichage de l image
def displayImage(i):
    '''
    fonction qui reconnait une image et l affiche avec en titre son label
    entree :
    i -- le numero de l image 
    '''
    plt.title('le nombre est un {0}'.format(prediction(i)))
    plt.imshow(x_test[i,:].reshape([28,28]), cmap='gray')
    plt.show()
        
displayImage(2762)

### creer un fichier pour sauver le modele ###
model.save('path_to_my_model2.h5')

### Export the model to a SavedModel au format tf
##model.save('saved_model', save_format='tf')
###pas bon
