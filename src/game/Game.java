package game;

import java.awt.event.KeyEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

import ml.EvolutionWindow;
import ml.NetworkWindow;

public class Game {
	GameContainer gc;
	int[][] area;
	int barriersWidth, birdSideLength, birdX;
	double speed, startingSpeed;
	int[] barriersSpaceHeight, barriersSpaceY, barrierNextX;	// min and max
	PlayerBird playerBird;
	ComBird[] comBirds;
	boolean player;
	EvolutionWindow evoWindow, evoWindow2;
	NetworkWindow netWindow;
	Barrier[] barriers;
	boolean lastWasUp, evo2CanRun;
	private Random rand;
	int gen, fitnessSum;
	
	Game(GameContainer gc) {
		this.gc = gc;
		rand = new Random();
		area = new int[gc.getWidth()][gc.getHeight()];
		startingSpeed = 3;
		speed = startingSpeed;
		barriersWidth = 75;
		barriersSpaceHeight = new int[]{175, 250};
		barriersSpaceY = new int[]{50, gc.getHeight() - 300};
		barrierNextX = new int[]{300, 500};
		barriers = new Barrier[5];
		for(int i = 0; i < barriers.length; i++) {
			if(i == 0) {
				barriers[i] = new Barrier(gc, 
						700,
						rand.nextInt(barriersSpaceY[1] - barriersSpaceY[0]) + barriersSpaceY[0], 
						rand.nextInt(barriersSpaceHeight[1] - barriersSpaceHeight[0]) + barriersSpaceHeight[0]
				);
			} else {
				barriers[i] = new Barrier(gc, 
						rand.nextInt(barrierNextX[1] - barrierNextX[0]) + barrierNextX[0] + barriers[i-1].getX(), 
						rand.nextInt(barriersSpaceY[1] - barriersSpaceY[0]) + barriersSpaceY[0], 
						rand.nextInt(barriersSpaceHeight[1] - barriersSpaceHeight[0]) + barriersSpaceHeight[0]
				);
			}
		}
		birdX = 100;
		birdSideLength = 35;
		player = false;
		if(player) playerBird = new PlayerBird(gc, birdSideLength);
		else {
			comBirds = new ComBird[500];
			for(int i = 0; i < comBirds.length; i++) {
				comBirds[i] = new ComBird(gc, this, birdSideLength);
			}
			evoWindow = new EvolutionWindow(500, 500);
			evoWindow2 = new EvolutionWindow(500, 500);
			evo2CanRun = false;
			netWindow = new NetworkWindow(comBirds[0].getBrain().getNumInputs(), comBirds[0].getBrain().getNumHidden(), comBirds[0].getBrain().getNumOutputs());
			netWindow.updateData(comBirds[0].getBrain().getBrain().getWeightsArray(), comBirds[0].getBrain().getBrain().getBiasesArray());
			netWindow.update();
		}
	}

	public void update() {
		clearArea();
		// update Barriers -------------------------------------------------------
		for(Barrier barrier : barriers) {
			for(int i = 0; i < barrier.getSpaceY(); i++) {
				for(int j = 0; j < barriersWidth; j++) {
					if(barrier.getX() + j >= 0 && barrier.getX() + j < area.length) {
						area[barrier.getX() + j][i] = 0xffffffff;	// White	above Space of Barrier
					}
				}
			}
			for(int i = barrier.getSpaceY() + barrier.getSpaceHeight(); i < gc.getHeight(); i++) {
				for(int j = 0; j < barriersWidth; j++) {
					if(barrier.getX() + j >= 0 && barrier.getX() + j < area.length) {
						area[barrier.getX() + j][i] = 0xffffffff;	// White	below Space of Barrier
					}
				}
			}
		}
		
		for(int i = 0; i < barriers.length; i++) {
			barriers[i].setX((barriers[i].getX() - (int)speed));
			if(barriers[i].getX() + barriersWidth < 0) {
				removeBarrier();
				i--;
			}
		}
		
		
		if(player) {	// player ------------------------------------------------------
			if(!playerBird.isAlive()) resetGame();
			if(!lastWasUp) playerBird.update(gc.getInput().isKey(KeyEvent.VK_SPACE));
			else playerBird.update(false);
			if(playerBird.collides(barriers, birdX, birdSideLength, barriersWidth)) {
				playerBird.setAlive(false);
			}
			lastWasUp = gc.getInput().isKey(KeyEvent.VK_SPACE);
			
			if(playerBird.isAlive()) {
				for(int i = 0; i < birdSideLength; i++) {
					for(int j = 0; j < birdSideLength; j++) {
						area[birdX + i][playerBird.getY() + j] = 0xffffffff;	// White
					}
				}
			}		
			
		} else {	// comBirds -------------------------------------------------------------
			if(isEveryBirdDead()) resetGame();
			
			for(int i = 0; i < comBirds.length; i++) {
				if(comBirds[i].isAlive()) {
					comBirds[i].update();
					if(comBirds[i].collides(barriers, birdX, birdSideLength, barriersWidth)) {
						comBirds[i].setAlive(false);
						//return;
					}
					comBirds[i].updateScore(barriers, birdX, barriersWidth, (int)speed);
				}
			}
			boolean showAllBirds = false;
			
			for(ComBird bird : comBirds) {
				if(bird.isAlive()) {
					for(int i = 0; i < birdSideLength; i++) {
						for(int j = 0; j < birdSideLength; j++) {
							area[birdX + i][bird.getY() + j] = 0xffffffff;	// White
						}
					}
					if(!showAllBirds) break;
				}
			}
		}
		
		speed *= 1.0001;
//		System.out.println(speed);
		
	}

	private void removeBarrier() {
		for(int i = 0; i < barriers.length-1; i++) {
			barriers[i] = barriers[i+1];
		}
		barriers[barriers.length-1] = new Barrier(gc, 
				rand.nextInt(barrierNextX[1] - barrierNextX[0]) + barrierNextX[0] + barriers[3].getX(), 
				rand.nextInt(barriersSpaceY[1] - barriersSpaceY[0]) + barriersSpaceY[0],  
				rand.nextInt(barriersSpaceHeight[1] - barriersSpaceHeight[0]) + barriersSpaceHeight[0]
		);
	}
	
	private void clearArea() {
		for(int i = 0; i < area.length; i++) {
			for(int j = 0; j < area[i].length; j++) {
				area[i][j] = 0xff000000;	// Black
			}
		}
	}
	
	private boolean isEveryBirdDead() {
		for(ComBird bird : comBirds) {
			if(bird.isAlive()) return false;
		}
		return true;
	}

	private void resetGame() {
		if(!player) {
			calcFitnessPop();
			calcFitnessSum();
			updateEvolutionWindow();
			doNaturalSelection();
			mutateBabies();
		} else {
			playerBird = new PlayerBird(gc, birdSideLength);
		}
		resetBarriers();
		speed = startingSpeed;
	}

	private void resetBarriers() {
//		bird = new PlayerBird(gc, birdSideLength);
		for(int i = 0; i < barriers.length; i++) {
			if(i == 0) {
				barriers[i] = new Barrier(gc, 
						700,
						rand.nextInt(barriersSpaceY[1] - barriersSpaceY[0]) + barriersSpaceY[0], 
						rand.nextInt(barriersSpaceHeight[1] - barriersSpaceHeight[0]) + barriersSpaceHeight[0]
				);
			} else {
				barriers[i] = new Barrier(gc, 
						rand.nextInt(barrierNextX[1] - barrierNextX[0]) + barrierNextX[0] + barriers[i-1].getX(), 
						rand.nextInt(barriersSpaceY[1] - barriersSpaceY[0]) + barriersSpaceY[0], 
						rand.nextInt(barriersSpaceHeight[1] - barriersSpaceHeight[0]) + barriersSpaceHeight[0]
				);
			}
		}
	}
	
	//genetic algorithm ------------------------------------------------------------------------------------
	//Fitness ---------------------------------------------------------------------------------------------
	private void calcFitnessPop() {
		for(int i = 0; i < comBirds.length; i++) {
			comBirds[i].calcFitness();
		}
	}
	
	private void calcFitnessSum() {
		fitnessSum = 0;
		for(int i = 0; i < comBirds.length; i++) {
			fitnessSum += comBirds[i].getFitness();
		}
	}
	
	// parsing Bird data
	private void updateEvolutionWindow() {
		double best = 0, median = 0, worst = comBirds[0].getFitness();
		int index = 0;
		for(index = 0; index < comBirds.length; index++) {
			double fitness = comBirds[index].getFitness();
			median += fitness;
			if(fitness > best) best = fitness;
			else if(fitness < worst) worst = fitness;
		}
		median /= index;
		evoWindow.updateData(best, median, worst);
		evoWindow.updateLines();
		evoWindow.update();
		// evoWindow2
		best = 0;
		median = 0;
		worst = comBirds[0].getScore();
		index = 0;
		for(index = 0; index < comBirds.length; index++) {
			double score = comBirds[index].getScore();
			median += score;
			if(score > best) best = score;
			else if(score < worst) worst = score;
		}
		median /= index;
		evoWindow2.updateData(best, median, worst);
		evoWindow2.updateLines();
		evoWindow2.update();

	}
	
	public void updateNetworkWindow(int bestIndex) {
		netWindow.updateData(comBirds[bestIndex].getBrain().getBrain().getWeightsArray(), comBirds[bestIndex].getBrain().getBrain().getBiasesArray());
		netWindow.update();
	}
	
	public void writeLines(String path, ArrayList<String> lines) {
		PrintWriter pw;
		try {
			pw = new PrintWriter(new FileWriter(path));
			for(int I = 0; I < lines.size(); I++){
				pw.println(lines.get(I));
			}
			pw.flush();
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Natural Selection -------------------------------------------------------------------------------------
	private void doNaturalSelection() {
		ComBird[] newBirds = new ComBird[comBirds.length];
		int bestBirdInd = getBestBird();
		calcFitnessSum();
		
		newBirds[0] = comBirds[bestBirdInd].clone();
		
		System.out.println("Distance: " + comBirds[bestBirdInd].getDistance());
		System.out.println("Score: " + comBirds[bestBirdInd].getScore());
		updateNetworkWindow(bestBirdInd);
		
		for(int i = 1; i < newBirds.length; i++){
			//select parent based on fitness
			ComBird parent1 = selectParent();
			ComBird parent2 = selectParent();
			
			//get baby from it
			newBirds[i] = new ComBird(gc, this, birdSideLength);
			newBirds[i].makeBaby(parent1, parent2);
		}
		
		comBirds = newBirds.clone();
		gen++;
		System.out.println("Generation: " + gen);
	}
	
	private ComBird selectParent() {
		double ran = rand.nextDouble() * fitnessSum;
		double runningSum = 0;
		
		for(int i = 0; i < comBirds.length; i++) {
			runningSum += comBirds[i].getFitness();
			if(runningSum > ran) {
				return comBirds[i];
			}
		}
		//Should never get to this point 
		return null;
	}
	
	//mutate Babies ------------------------------------------------------------------------------------
	private void mutateBabies() {
		for(int i = 1; i < comBirds.length; i++) {
			comBirds[i].getBrain().mutate(0.05);
		}
	}
	
	//Best Dot ----------------------------------------------------------------------------------------
	private int getBestBird() {
		double max = 0;
		int maxIndex = 0;
		for(int i = 0; i < comBirds.length; i++) {
			if(comBirds[i].getFitness() > max) {
				max = comBirds[i].getFitness();
				maxIndex = i;
			}
		}
		return maxIndex;
	}

	public int[][] getArea() {
		return area;
	}

	public Barrier[] getBarriers() {
		return barriers;
	}

	public double getSpeed() {
		return speed;
	}
}
