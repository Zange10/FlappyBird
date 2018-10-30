package game;


public class PlayerBird {
	GameContainer gc;
	int y, ySpeed, birdBounds, score;
	boolean alive;
	
	PlayerBird(GameContainer gc, int birdBounds) {
		this.gc = gc;
		this.birdBounds = birdBounds;
		y = gc.getHeight()/2 - birdBounds/2;
		alive = true;
	}
	
	public void update(boolean birdUp) {
		if(birdUp) {
			ySpeed = 15;
		} else {
			ySpeed -= 1 ;
		}
		y = y - ySpeed;	// positiv = nach unten
		if(y+birdBounds > gc.getHeight()) {
			y = gc.getHeight()-birdBounds;
			ySpeed = 0;
		} else if(y < 0) {
			y = 0;
			ySpeed = 0;
		}
	}
	
	public boolean collides(Barrier[] barriers, int birdX, int birdWidth, int barriersWidth) {
		for(Barrier barrier : barriers) {
			if(birdX + birdWidth > barrier.getX() && birdX < barrier.getX() + barriersWidth) {
				if(y < barrier.getSpaceY() || y > barrier.getSpaceY() + barrier.getSpaceHeight()) {
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
				System.out.println("Score: " + score);
			}
		}
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
}
