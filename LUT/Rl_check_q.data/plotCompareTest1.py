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
winRateS = []
numRoundsS = []
winRateR = []
numRoundsR = []
winRateU = []
numRoundsU = []
winRateV = []
numRoundsV = []

def movingaverage(interval, window_size):
    window = np.ones(int(window_size))/float(window_size)
    return np.convolve(interval, window, 'same')


#E0 file 
winValueFileQ = open("/home/kevin/Robocode%20/robocode/robots/sample/Kevin/Try2/Rl_check_q.data/E0Train/E0Test/winsRate.txt", "r") 
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

#E0.2 file 
winValueFileT = open("/home/kevin/Robocode%20/robocode/robots/sample/Kevin/Try2/Rl_check_q.data/E0.2Train/E0Test/winsRate.txt", "r") 
winValueT =  winValueFileT.readlines()
winValueT = winValueT[0:4999]
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
winRateMovT[0] = winRateT[0]
winRateMovT[1] = winRateT[1]
winRateMovT[2] = winRateT[2]
winRateMovT = winRateMovT[0:45] 
numRoundsNewT = np.arange(0,len(winRateMovT)*100,100)

#E0.4
winValueFileT = open("/home/kevin/Robocode%20/robocode/robots/sample/Kevin/Try2/Rl_check_q.data/E0.4Train/E0Test/winsRate.txt", "r") 
winValueT =  winValueFileT.readlines()
winValueT = winValueT[0:4999]
roundsT = len(winValueT)
numRoundsT = np.arange(0,roundsT,100)

for i in range(0,(roundsT/100)+1):
        winValueTempT = winValueT[100*(i):100*(i+1)]
	#print(len(winValueTemp))
	winValueTempT = map(lambda s: s.strip(), winValueTempT)
	winValueTempT = [map(int, x) for x in winValueTempT]
	winNumT = np.count_nonzero(winValueTempT)
	winRateS.append((winNumT/100.0)*100) 

winRateMovS = movingaverage(winRateS, 6)
winRateMovS[0] = winRateS[0]
winRateMovS[1] = winRateS[1]
winRateMovS[2] = winRateS[2]
winRateMovS = winRateMovS[0:45] 
numRoundsNewS = np.arange(0,len(winRateMovS)*100,100)

#E0.6
winValueFileT = open("/home/kevin/Robocode%20/robocode/robots/sample/Kevin/Try2/Rl_check_q.data/E0.6Train/E0Test/winsRate.txt", "r") 
winValueT =  winValueFileT.readlines()
winValueT = winValueT[0:4999]
roundsT = len(winValueT)
numRoundsT = np.arange(0,roundsT,100)

for i in range(0,(roundsT/100)+1):
        winValueTempT = winValueT[100*(i):100*(i+1)]
	#print(len(winValueTemp))
	winValueTempT = map(lambda s: s.strip(), winValueTempT)
	winValueTempT = [map(int, x) for x in winValueTempT]
	winNumT = np.count_nonzero(winValueTempT)
	winRateR.append((winNumT/100.0)*100) 

winRateMovR = movingaverage(winRateR, 6)
winRateMovR[0] = winRateR[0]
winRateMovR[1] = winRateR[1]
winRateMovR[2] = winRateR[2]
winRateMovR = winRateMovR[0:45] 
numRoundsNewR = np.arange(0,len(winRateMovR)*100,100)

#E0.8
winValueFileT = open("/home/kevin/Robocode%20/robocode/robots/sample/Kevin/Try2/Rl_check_q.data/E0.8Train/E0Test/winsRate.txt", "r") 
winValueT =  winValueFileT.readlines()
winValueT = winValueT[0:4999]
roundsT = len(winValueT)
numRoundsT = np.arange(0,roundsT,100)

for i in range(0,(roundsT/100)+1):
        winValueTempT = winValueT[100*(i):100*(i+1)]
	#print(len(winValueTemp))
	winValueTempT = map(lambda s: s.strip(), winValueTempT)
	winValueTempT = [map(int, x) for x in winValueTempT]
	winNumT = np.count_nonzero(winValueTempT)
	winRateU.append((winNumT/100.0)*100) 

winRateMovU = movingaverage(winRateU, 6)
winRateMovU[0] = winRateU[0]
winRateMovU[1] = winRateU[1]
winRateMovU[2] = winRateU[2]
winRateMovU = winRateMovU[0:45] 
numRoundsNewU = np.arange(0,len(winRateMovU)*100,100)

#E1
winValueFileT = open("/home/kevin/Robocode%20/robocode/robots/sample/Kevin/Try2/Rl_check_q.data/E1Train/E0Test/winsRate.txt", "r") 
winValueT =  winValueFileT.readlines()
winValueT = winValueT[0:4999]
roundsT = len(winValueT)
numRoundsT = np.arange(0,roundsT,100)

for i in range(0,(roundsT/100)+1):
        winValueTempT = winValueT[100*(i):100*(i+1)]
	#print(len(winValueTemp))
	winValueTempT = map(lambda s: s.strip(), winValueTempT)
	winValueTempT = [map(int, x) for x in winValueTempT]
	winNumT = np.count_nonzero(winValueTempT)
	winRateV.append((winNumT/100.0)*100) 

winRateMovV = movingaverage(winRateV, 6)
winRateMovV[0] = winRateV[0]
winRateMovV[1] = winRateV[1]
winRateMovV[2] = winRateV[2]
winRateMovV = winRateMovV[0:45] 
numRoundsNewV = np.arange(0,len(winRateMovV)*100,100)

axes = plt.gca()
axes.set_ylim([0,100])
plt.plot(numRoundsNewQ, winRateMovQ,c='b',linewidth=2.0,label="E-0")
plt.plot(numRoundsNewT, winRateMovT,c='r',linewidth=2.0,label="E-0.2") 
plt.plot(numRoundsNewS, winRateMovS,c='g',linewidth=2.0,label="E-0.4") 
plt.plot(numRoundsNewR, winRateMovR,c='m',linewidth=2.0,label="E-0.6") 
plt.plot(numRoundsNewU, winRateMovU,c='c',linewidth=2.0,label="E-0.8") 
plt.plot(numRoundsNewV, winRateMovV,c='k',linewidth=2.0,label="E-1") 

#plt.plot(numRounds,winRate,c='r',linewidth=2.0,label="without smoothing")
plt.xlabel('No. of Rounds')
plt.ylabel('Win rate')
plt.title('Performance with E = 0')
plt.legend()
plt.show()


