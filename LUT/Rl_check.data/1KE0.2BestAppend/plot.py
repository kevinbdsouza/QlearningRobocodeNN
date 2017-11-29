# -*- coding: utf-8 -*-
import numpy as np
import matplotlib.pyplot as plt
from matplotlib.legend_handler import HandlerLine2D

winRate = []
numRounds = []

winValueFile = open("/home/kevin/Robocode%20/robocode/robots/sample/Kevin/Try2/Rl_check.data/winsRate.txt", "r") 
winValue =  winValueFile.readlines()
winValue = winValue[0:999]
rounds = len(winValue)
numRounds = np.arange(0,rounds-50,50)
print(len(numRounds))

for i in range(0,(rounds/50)):
        winValueTemp = winValue[50*(i):50*(i+1)]
	#print(len(winValueTemp))
	winValueTemp = map(lambda s: s.strip(), winValueTemp)
	winValueTemp = [map(int, x) for x in winValueTemp]
	winNum = np.count_nonzero(winValueTemp)
	winRate.append((winNum/50.0)*100) 

print(len(winRate))
plt.xlabel('No. of Rounds')
plt.ylabel('Win rate')
plt.title('Win rate vs No. of Rounds')
plt.plot(numRounds,winRate,c='r',linewidth=2.0)
plt.show()
