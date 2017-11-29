# -*- coding: utf-8 -*-
import numpy as np
import matplotlib.pyplot as plt
from matplotlib.legend_handler import HandlerLine2D
from scipy.interpolate import spline
from pandas import Series
import pandas as pd

winRateL = []
numRoundsL = []
winRateN = []
numRoundsN = []


def movingaverage(interval, window_size):
    window = np.ones(int(window_size))/float(window_size)
    return np.convolve(interval, window, 'same')


#LUT
winValueFileL = open("/home/kevin/Robocode%20/robocode/robots/sample/Kevin/Try2/NN/Rl_lut.data/nnLUT/winsRateLUT.txt", "r") 
winValueL =  winValueFileL.readlines()
winValueL = winValueL[0:9999]
roundsL = len(winValueL)
numRoundsL = np.arange(0,roundsL,100)

for i in range(0,(roundsL/100)+1):
        winValueTempL = winValueL[100*(i):100*(i+1)]
	#print(len(winValueTemp))
	winValueTempL = map(lambda s: s.strip(), winValueTempL)
	winValueTempL = [map(int, x) for x in winValueTempL]
	winNumL = np.count_nonzero(winValueTempL)
	winRateL.append((winNumL/100.0)*100) 

winRateMovL = movingaverage(winRateL, 4)
winRateMovL = winRateMovL[0:95] 
numRoundsNewL = np.arange(0,len(winRateMovL)*100,100)

#NN
winValueFileN = open("/home/kevin/Robocode%20/robocode/robots/sample/Kevin/Try2/NN/Rl_lut.data/nnLUT/winsRate1.txt", "r") 
winValueN =  winValueFileN.readlines()
winValueN = winValueN[0:9999]
roundsN = len(winValueN)
numRoundsN = np.arange(0,roundsN,100)

for i in range(0,(roundsN/100)+1):
        winValueTempN = winValueN[100*(i):100*(i+1)]
	#print(len(winValueTemp))
	winValueTempN = map(lambda s: s.strip(), winValueTempN)
	winValueTempN = [map(int, x) for x in winValueTempN]
	winNumN = np.count_nonzero(winValueTempN)
	winRateN.append((winNumN/100.0)*100) 

winRateMovN = movingaverage(winRateN, 4)
winRateMovN = winRateMovN[0:95] 
numRoundsNewN = np.arange(0,len(winRateMovN)*100,100)

plt.plot(numRoundsNewL, winRateMovL,c='b',linewidth=2.0,label="LUT")
plt.plot(numRoundsNewN, winRateMovN,c='r',linewidth=2.0,label="NN") 


#plt.plot(numRounds,winRate,c='r',linewidth=2.0,label="without smoothing")
plt.xlabel('No. of Rounds')
plt.ylabel('Win rate')
plt.title('Learning Progress')
plt.legend()
plt.show()


