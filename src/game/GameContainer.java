package game;

import java.awt.GraphicsEnvironment;

public class GameContainer extends Thread {

	private Thread thread;
	GraphicsEnvironment env;
	private boolean running = false;
	private final double UPDATE_CAP = 1.0/300000.0;
	private int width, height;
	private float scale = 1f;
	private String title = "Effects";
	private Window window;
	private Input input;
	private Renderer renderer;
	Game game;
	
	
	public void start()
	{
	    env = GraphicsEnvironment.getLocalGraphicsEnvironment(); 
	    width = 1000;
	    height = 700;
		window	= new Window(this);
		input	= new Input(this);
		game 	= new Game(this);
		renderer = new Renderer(this);
		thread	= new Thread(this);
		thread.run();
	}
	
	public void run() {
		running = true;
		
		boolean render = false;
		double firstTime = 0;
		double lastTime = System.nanoTime() / 1000000000.0;
		double passedTime = 0;
		double unprocessedTime = 0;
		
		double frameTime = 0;
		int frames = 0;
		int fps = 0;
		
		while(running)
		{
			render = false;
			
			firstTime = System.nanoTime() / 1000000000.0;
			passedTime = firstTime - lastTime;
			lastTime = firstTime;
			
			unprocessedTime += passedTime;
			frameTime += passedTime;
			
			while(unprocessedTime >= UPDATE_CAP)
			{
				unprocessedTime -= UPDATE_CAP;
				render = true;
				
				if(frameTime >= 1.0)
				{
					frameTime = 0;
					fps = frames;
					frames = 0;
					System.out.println("FPS: " + fps);
				}
			}
			
			if(render)
			{
				game.update();
				renderer.clear();
				renderer.update();
				window.update();
				input.update();
				frames++;
			}
			else
			{
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	

	
	//Getter and Setter
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Window getWindow() {
		return window;
	}

	public Input getInput() {
		return input;
	}

	public Game getGame() {
		return game;
	}
}
