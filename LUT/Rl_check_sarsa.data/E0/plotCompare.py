# -*- coding: utf-8 -*-
import numpy as np
import matplotlib.pyplot as plt
from matplotlib.legend_handler import HandlerLine2D
from scipy.interpolate import spline
from pandas import Series
import pandas as pd

winRateQ = []
numRoundsQ = []
winRateS = []
numRoundsS = []

def movingaverage(interval, window_size):
    window = np.ones(int(window_size))/float(window_size)
    return np.convolve(interval, window, 'same')


#Q file 
winValueFileQ = open("/home/kevin/Robocode%20/robocode/robots/sample/Kevin/Try2/Rl_check_q.data/E0/winsRate.txt", "r") 
winValueQ =  winValueFileQ.readlines()
winValueQ = winValueQ[0:4999]
roundsQ = len(winValueQ)
numRoundsQ = np.arange(0,roundsQ,100)

for i in range(0,(roundsQ/100)+1):
        winValueTempQ = winValueQ[100*(i):100*(i+1)]
	#print(len(winValueTemp))
	winValueTempQ = map(lambda s: s.strip(), winValueTempQ)
	winValueTempQ = [map(int, x) for x in winValueTempQ]
	winNumQ = np.count_nonzero(winValueTempQ)
	winRateQ.append((winNumQ/100.0)*100) 

winRateMovQ = movingaverage(winRateQ, 6)
winRateMovQ[0] = winRateQ[0]
winRateMovQ[1] = winRateQ[1]
winRateMovQ[2] = winRateQ[2]
winRateMovQ = winRateMovQ[0:45] 
numRoundsNewQ = np.arange(0,len(winRateMovQ)*100,100)

#sarsa file 
winValueFileS = open("/home/kevin/Robocode%20/robocode/robots/sample/Kevin/Try2/Rl_check_sarsa.data/E0/winsRate.txt", "r") 
winValueS =  winValueFileS.readlines()
winValueS = winValueS[0:4999]
roundsS = len(winValueS)
numRoundsS = np.arange(0,roundsS,100)

for i in range(0,(roundsS/100)+1):
        winValueTempS = winValueS[100*(i):100*(i+1)]
	#print(len(winValueTemp))
	winValueTempS = map(lambda s: s.strip(), winValueTempS)
	winValueTempS = [map(int, x) for x in winValueTempS]
	winNumS = np.count_nonzero(winValueTempS)
	winRateS.append((winNumS/100.0)*100) 

winRateMovS = movingaverage(winRateS, 6)
winRateMovS[0] = winRateS[0]
winRateMovS[1] = winRateS[1]
winRateMovS[2] = winRateS[2]
winRateMovS = winRateMovS[0:45] 
numRoundsNewS = np.arange(0,len(winRateMovS)*100,100)

axes = plt.gca()
axes.set_ylim([0,100])
plt.plot(numRoundsNewQ, winRateMovQ,c='b',linewidth=2.0,label="Q")
plt.plot(numRoundsNewS, winRateMovS,c='r',linewidth=2.0,label="sarsa")  
#plt.plot(numRounds,winRate,c='r',linewidth=2.0,label="without smoothing")
plt.xlabel('No. of Rounds')
plt.ylabel('Win rate')
plt.title('Win rate vs No. of Rounds')
plt.legend()
plt.show()


