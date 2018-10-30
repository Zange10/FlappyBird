package game;

public class Barrier {
	GameContainer gc;
	int x, spaceY, spaceHeight;
	
	Barrier(GameContainer gc, int x, int spaceY, int spaceHeight) {
		this.x = x;
		this.spaceY = spaceY;
		this.spaceHeight = spaceHeight;
	}

	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}

	public int getSpaceY() {
		return spaceY;
	}

	public int getSpaceHeight() {
		return spaceHeight;
	}
}
