import numpy as np
import matplotlib.pyplot as plt
from matplotlib.legend_handler import HandlerLine2D
from scipy.interpolate import spline
from pandas import Series
import pandas as pd
import math

rounds = 50000;
errorN1 = []
errorN2 = []
numRounds = np.arange(0,rounds,1)
noise1 = np.random.normal(0,0.005,9000)
noise2 = np.random.normal(0,0.001,9000)
noise3 = np.random.normal(0,0.005,6000)
noise4 = np.random.normal(0,0.001,11000)

error1 = 5*(1.0/np.power(numRounds,1./4)) + 0.5
errorN1[0:9000] = error1[0:9000] + noise1
errorN1[9000:18000] = error1[9000:18000] + noise2
errorN1[18000:50000] = error1[18000:50000]

error2 = 5*(1.0/np.power(numRounds,1./3)) + 0.5
errorN2[0:6000] = error2[0:6000] + noise3
errorN2[6000:17000] = error2[6000:17000] + noise4
errorN2[17000:50000] = error2[17000:50000]

plt.plot(numRounds, errorN1,c='b',linewidth=2.0,label="State 1")
plt.plot(numRounds, errorN2,c='r',linewidth=2.0,label="State 2")

plt.xlabel('No. of Rounds')
plt.ylabel('Error e(S)')
plt.title('e(S) for selected states')
plt.legend()
plt.show()
