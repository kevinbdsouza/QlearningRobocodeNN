# -*- coding: utf-8 -*-
import numpy as np
import matplotlib.pyplot as plt
from matplotlib.legend_handler import HandlerLine2D
from scipy.interpolate import spline
from pandas import Series
import pandas as pd

def find_nearest(array,value):
    idx = (np.abs(array-value)).argmin()
    return idx

# initialize variables and arrays 
maxRun = 30
maxEpoch = 50000;
count = 0
epochValues = []
trackErrors = []
trainError = []
valError = []
hidden = []
epochs = []

def movingaverage(interval, window_size):
    window = np.ones(int(window_size))/float(window_size)
    return np.convolve(interval, window, 'same')  

nRun = 2;
# read all files to determine min, max, min of epoch values 
#for nRun in range(0,maxRun): 
errorfile = open("/home/kevin/Learning systems/Part 3/errorFilesHidden/errorFileRMS"+ str(nRun+1) +".txt", "r") 
error =  errorfile.readlines()
error = error[0:100];
epochValues.append(len(error))
#trainError.append(error[maxEpoch - 1]) 
errorfile.close()

epochs = np.arange(0,100,1)
plt.xlabel('No. of Epochs')
plt.ylabel('RMS error')
plt.title('RMS error vs No. of Epochs')
lineMin, =  plt.plot(epochs, error, c='r', label="min", linewidth=2.0)
plt.show()

'''
for nRun in range(0,maxRun): 
	errorfile = open("/home/kevin/Learning systems/Part 3/errorFilesHidden/errorFileVal"+ str(nRun+1) +".txt", "r") 
	error =  errorfile.readlines()
	epochValues.append(len(error))
	valError.append(error[maxEpoch - 1]) 
	errorfile.close()	
moment = np.arange(0.1,0.9,0.1)
plt.xlabel('Momentum')
plt.ylabel('RMS error')
plt.title('RMS error vs Momentum')
lineTrain, =plt.plot(moment, trainError, c='r', label="Training Error", linewidth=2.0)
plt.show()
'''

'''
for nRun in range(0,maxRun): 
	errorfile = open("/home/kevin/Learning systems/Part 3/errorFiles/errorFileVal"+ str(nRun+1) +".txt", "r") 
	error =  errorfile.readlines()
	epochValues.append(len(error))
	valError.append(error[maxEpoch - 1]) 
	errorfile.close()

hidden = np.arange(1,maxRun + 1,1)
trainError=np.array(trainError,dtype=float)
valError=np.array(valError,dtype=float)

trainErrorMov = movingaverage(trainError, 2)
trainErrorMov[0] = trainError[0]
trainErrorMov[1] = trainError[1]
trainErrorMov[2] = trainError[2]
valErrorMov = movingaverage(valError, 2)
valErrorMov[0] = valError[0]
valErrorMov[1] = valError[1]
valErrorMov[2] = valError[2]

plt.xlabel('No. of Hidden Neurons')
plt.ylabel('RMS error')
plt.title('RMS error vs No. of Hidden Neurons')
lineTrain, =plt.plot(hidden, trainError, c='r', label="Training Error", linewidth=2.0)
lineVal, = plt.plot(hidden, valError, c='g', label="Validation Error", linewidth=2.0)
plt.legend(handler_map={lineTrain: HandlerLine2D(numpoints=4)})
plt.show()
'''

'''	
# calculate min, max, mean  	
epochMax = np.amax(epochValues)
index_max = np.argmax(epochValues)
epochMin = np.amin(epochValues)
index_min = np.argmin(epochValues)
epochMean = np.mean(epochValues)
index_mean = find_nearest(epochValues, epochMean)

print(epochMin)
print(epochMean)
print(epochMax) 

for k in range(0,maxRun):
	if (epochValues[k] > 1000):
		count = count + 1 
'''
'''
print(count)
# read these files again to plot  
runNumber = [index_min, index_mean, index_max]
for i in range(0,3):
	errorfile = open("/home/kevin/Learning systems/part1a/momentHigh/Bipolar/BipolarTxt1/errorFileRun"+ str(runNumber[i] + 1) +".txt", "r") 
	errorTemp = errorfile.readlines()
	trackErrors.append(errorTemp)
	totalEpochs = len(trackErrors[i])
	epochs.append(np.arange(0,totalEpochs,1))

# plot the 3 curves 
plt.xlabel('No. of Epochs')
plt.ylabel('Total error')
plt.title('Total error vs No. of Epochs')
lineMin, =  plt.plot(epochs[0], trackErrors[0], c='r', label="min", linewidth=2.0)
lineMean, = plt.plot(epochs[1], trackErrors[1], c='g', label="mean", linewidth=2.0)
lineMax, = plt.plot(epochs[2], trackErrors[2], c='b', label="max", linewidth=2.0)
plt.legend(handler_map={lineMin: HandlerLine2D(numpoints=4)})
plt.show()
'''


