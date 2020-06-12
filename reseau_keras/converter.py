import tensorflow as tf
from tensorflow import keras
from tensorflow.keras import layers

'''this script allows a .h5 file to be converted to a .tflite model,
using the function convert_to'''

# Recreate the exact same model purely from the file
new_model = keras.models.load_model('path_to_my_model2.h5')



def convert_to (what):
    if what == 'float' :
        # Convert the model.
        converter = tf.lite.TFLiteConverter.from_keras_model(new_model)
        tflite_model = converter.convert()

        #create the .tflite file
        open("tf_lite_model_float.tflite", "wb").write(tflite_model)
    elif what == 'quant' :
        #convert the model
        converter2 = tf.lite.TFLiteConverter.from_keras_model(new_model)
        converter2.optimizations = [tf.lite.Optimize.DEFAULT]
        tflite_quant_model = converter2.convert()

        #create the .tflite file
        open("tf_lite_model_quant.tflite", "wb").write(tflite_quant_model)



convert_to('quant')
convert_to('float')

