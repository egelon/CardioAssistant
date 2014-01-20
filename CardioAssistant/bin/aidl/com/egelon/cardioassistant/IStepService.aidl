package com.egelon.cardioassistant;

import com.egelon.cardioassistant.IStepServiceCallback;

interface IStepService {
		boolean isRunning();
		void setSensitivity(int sens);
		void registerCallback(IStepServiceCallback cb);
		void unregisterCallback(IStepServiceCallback cb);
}
