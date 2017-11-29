# -*- coding: utf-8 -*-
import numpy as np
import matplotlib.pyplot as plt
from matplotlib.legend_handler import HandlerLine2D
from scipy.interpolate import spline
from pandas import Series
import pandas as pd

winRate = []
numRounds = []

winValueFile = open("/home/kevin/Robocode%20/robocode/robots/sample/Kevin/Try2/Rl_check_q_terminal.data/E0/winsRate.txt", "r") 
winValue =  winValueFile.readlines()
winValue = winValue[0:4999]
rounds = len(winValue)
numRounds = np.arange(0,rounds,100)
print(len(numRounds))

for i in range(0,(rounds/100)+1):
        winValueTemp = winValue[100*(i):100*(i+1)]
	#print(len(winValueTemp))
	winValueTemp = map(lambda s: s.strip(), winValueTemp)
	winValueTemp = [map(int, x) for x in winValueTemp]
	winNum = np.count_nonzero(winValueTemp)
	winRate.append((winNum/100.0)*100) 

print(len(winRate))


def movingaverage(interval, window_size):
    window = np.ones(int(window_size))/float(window_size)
    return np.convolve(interval, window, 'same')

winRateMov = movingaverage(winRate, 6)
winRateMov[0] = winRate[0]
winRateMov[1] = winRate[1]
winRateMov[2] = winRate[2]
winRateMov = winRateMov[0:45] 
numRoundsNew = np.arange(0,len(winRateMov)*100,100)

axes = plt.gca()
axes.set_ylim([0,100])
plt.plot(numRoundsNew, winRateMov,c='b',linewidth=2.0,label="smoothing") 
plt.plot(numRounds,winRate,c='r',linewidth=2.0,label="without smoothing")
plt.xlabel('No. of Rounds')
plt.ylabel('Win rate')
plt.title('Win rate vs No. of Rounds')
plt.legend()
plt.show()


