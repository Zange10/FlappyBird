package game;

import ml.NeuralNetwork;

public class Brain {
	NeuralNetwork brain;
	int numInputs, numOutputs;
	double[] numHidden;
	
	Brain() {
		numInputs = 4;
		numHidden = new double[]{4};
		numOutputs = 1;
		brain = new NeuralNetwork(numInputs, numHidden, numOutputs);
	}
	
	public Brain clone() {
		Brain newBrain = new Brain();
		newBrain.brain = this.brain;
		return newBrain;
	}
	
	public void mutate(double mutationRate) {
		brain.mutate(mutationRate);
	}

	public NeuralNetwork getBrain() {
		return brain;
	}

	public int getNumInputs() {
		return numInputs;
	}

	public int getNumOutputs() {
		return numOutputs;
	}

	public double[] getNumHidden() {
		return numHidden;
	}
}
