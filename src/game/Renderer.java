package game;

import java.awt.image.DataBufferInt;

public class Renderer {
	private GameContainer gc;
	private int pW, pH; 	//PixelsWidth, PixelsHeight
	private int[] p;		//Pixels
	//private int count;
	//private int add;
	
	
	public Renderer(GameContainer gc)
	{
		this.gc = gc;
		pW = gc.getWidth();
		pH = gc.getHeight();
		p = ((DataBufferInt)gc.getWindow().getImage().getRaster().getDataBuffer()).getData();
	}
	
	public void clear()
	{
		for(int i = 0; i < p.length; i++)
		{
			p[i] = 0xff000000;  //Black
		}
	}
	
	public void setPixel(int x, int y, int value)
	{
		if((x <= 0 ||  x >= pW || y <= 0 || y >= pH) || ((value >> 24) & 0xff) == 0)
		{
			return;
		}
		
		p[x + y * pW] = value;
	}

	public void update() {
		int[][] area = gc.getGame().getArea();
		for(int i = 0; i < area.length; i++) {
			for(int j = 0; j < area[i].length; j++) {
				setPixel(i,j,area[i][j]);
			}
		}
	}
	
	
	//Getters and Setters
	
	public void setpW(int pW) {
		this.pW = pW;
	}

	public void setpH(int pH) {
		this.pH = pH;
	}

	public void setp(int[] p) {
		this.p = p;
	}
}
