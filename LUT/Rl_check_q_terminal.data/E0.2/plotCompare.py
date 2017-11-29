# -*- coding: utf-8 -*-
import numpy as np
import matplotlib.pyplot as plt
from matplotlib.legend_handler import HandlerLine2D
from scipy.interpolate import spline
from pandas import Series
import pandas as pd

winRateQ = []
numRoundsQ = []
winRateT = []
numRoundsT = []

def movingaverage(interval, window_size):
    window = np.ones(int(window_size))/float(window_size)
    return np.convolve(interval, window, 'same')


#Q file 
winValueFileQ = open("/home/kevin/Robocode%20/robocode/robots/sample/Kevin/Try2/Rl_check_q.data/E0.2/winsRate.txt", "r") 
winValueQ =  winValueFileQ.readlines()
winValueQ = winValueQ[0:9999]
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
winRateMovQ = winRateMovQ[0:95] 
numRoundsNewQ = np.arange(0,len(winRateMovQ)*100,100)

#sarsa file 
winValueFileT = open("/home/kevin/Robocode%20/robocode/robots/sample/Kevin/Try2/Rl_check_q_terminal.data/E0.2/winsRate.txt", "r") 
winValueT =  winValueFileT.readlines()
winValueT = winValueT[0:9999]
roundsT = len(winValueT)
numRoundsT = np.arange(0,roundsT,100)

for i in range(0,(roundsT/100)+1):
        winValueTempT = winValueT[100*(i):100*(i+1)]
	#print(len(winValueTemp))
	winValueTempT = map(lambda s: s.strip(), winValueTempT)
	winValueTempT = [map(int, x) for x in winValueTempT]
	winNumT = np.count_nonzero(winValueTempT)
	winRateT.append((winNumT/100.0)*100) 

winRateMovT = movingaverage(winRateT, 6)
winRateMovT = winRateMovT[0:95] 
numRoundsNewT = np.arange(0,len(winRateMovT)*100,100)


plt.plot(numRoundsNewQ, winRateMovQ,c='b',linewidth=2.0,label="Q-Intermediate")
plt.plot(numRoundsNewT, winRateMovT,c='r',linewidth=2.0,label="Q-Terminal")  
#plt.plot(numRounds,winRate,c='r',linewidth=2.0,label="without smoothing")
plt.xlabel('No. of Rounds')
plt.ylabel('Win rate')
plt.title('Win rate vs No. of Rounds')
plt.legend()
plt.show()


