# -*- coding: utf-8 -*-
import numpy as np
import matplotlib.pyplot as plt
from matplotlib.legend_handler import HandlerLine2D


def find_nearest(array,value):
    idx = (np.abs(array-value)).argmin()
    return idx

# initialize variables and arrays 
maxRun = 1000
count = 0
epochValues = []
trackErrors = []
epochs = []

# read all files to determine min, max, min of epoch values 
for nRun in range(0,maxRun): 
	errorfile = open("/home/kevin/Learning systems/part1a/momentHigh/Bipolar/BipolarTxt1/errorFileRun"+ str(nRun+1) +".txt", "r") 
	error =  errorfile.readlines()
	epochValues.append(len(error))
	errorfile.close()
	
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



