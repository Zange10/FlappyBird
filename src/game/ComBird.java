package game;

import ml.NeuralNetwork;

public class ComBird {
	GameContainer gc;
	int y, ySpeed, birdBounds, distance, score, fitness;
	boolean alive;
	Game game;
	Brain brain;
	
	ComBird(GameContainer gc, Game game, int birdBounds) {
		this.gc = gc;
		this.game = game;
		this.birdBounds = birdBounds;
		y = gc.getHeight()/2 - birdBounds/2;
		alive = true;
		score = 0;
		brain = new Brain();
	}
	
	public void update() {
		int nextBarrierIndex = getNextBarrierIndex(game.getBarriers(), game.birdX, game.birdSideLength, game.barriersWidth);
		Barrier nextBarrier = game.getBarriers()[nextBarrierIndex];
//		Barrier afterNextBarrier = game.getBarriers()[nextBarrierIndex+1];
		boolean birdUp = (brain.brain.feedforward(new double[]{
				y, 
				nextBarrier.getX() - (game.birdX + game.birdSideLength),
				nextBarrier.getSpaceY(),
				nextBarrier.getSpaceHeight(),
//				afterNextBarrier.getX() - (game.birdX + game.birdSideLength),
//				afterNextBarrier.getSpaceY(),
//				afterNextBarrier.getSpaceHeight(),
//				game.getSpeed()
				})[0] > 0.5);
		if(birdUp) {
			ySpeed = 15;
		} else {
			ySpeed -= 1 ;
		}
		y = y - ySpeed;	// positiv = nach unten
		if(y+birdBounds > gc.getHeight()) {
			alive = false;
			y = gc.getHeight()-birdBounds;
			ySpeed = 0;
		} else if(y < 0) {
			alive = false;
			y = 0;
			ySpeed = 0;
		}
		distance += game.getSpeed();
	}
	
	private int getNextBarrierIndex(Barrier[] barriers, int birdX, int birdWidth, int barriersWidth) {
		for(int i = 0; i < barriers.length; i++) {
			if(birdX < barriers[i].getX() + barriersWidth) {
				return i;
			}
		}
		return 0;
	}
	
	public boolean collides(Barrier[] barriers, int birdX, int birdBounds, int barriersWidth) {
		for(Barrier barrier : barriers) {
			if(birdX + birdBounds > barrier.getX() && birdX < barrier.getX() + barriersWidth) {
				if(y < barrier.getSpaceY() || y + birdBounds > barrier.getSpaceY() + barrier.getSpaceHeight()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void updateScore(Barrier[] barriers, int birdX, int barriersWidth, int speed) {
		for(Barrier barrier : barriers)	{
			if(birdX < barrier.getX() + barriersWidth) return;
			if(birdX < barrier.getX() + barriersWidth + speed) {
				score++;
			}
		}
	}
	
	public void calcFitness() {
		fitness = distance;
	}
	
	public ComBird clone() {
		ComBird baby = new ComBird(gc, game, birdBounds);
		baby.brain = this.brain.clone();
		return baby;
	}
	
	public void makeBaby(ComBird parent1, ComBird parent2) {
		brain.brain = NeuralNetwork.makeBaby(parent1.getBrain().brain.getDNA(), parent2.getBrain().brain.getDNA());
	}

	public int getY() {
		return y;
	}

	public int getySpeed() {
		return ySpeed;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getFitness() {
		return fitness;
	}

	public Brain getBrain() {
		return brain;
	}

	public int getScore() {
		return score;
	}
}
