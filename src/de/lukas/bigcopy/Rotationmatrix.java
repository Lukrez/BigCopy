package de.lukas.bigcopy;


public class Rotationmatrix {

	private int a1,a2,b1,b2;
	private Direction direction;
	
	public Rotationmatrix(Direction copyDirection, Direction pasteDirection){

		
		this.a1 = 1;
		this.a2 = 0;
		this.b1 = 0;
		this.b2 = 1;
		this.direction = copyDirection;
		while (this.direction != Direction.UNDEFINED && this.direction != pasteDirection){
			this.direction = this.direction.rotate();
			this.rotateMatrix();
		}
	}
	
	private void rotateMatrix(){
		int tempA1 = this.a1;
		int tempB1 = this.b1;
		
		this.a1 = -this.a2;
		this.a2 = tempA1;
		this.b1 = -this.b2;
		this.b2 = tempB1;
	}
	
	public int getA1(){
		return this.a1;
	}
	public int getA2(){
		return this.a2;
	}
	public int getB1(){
		return this.b1;
	}
	public int getB2(){
		return this.b2;
	}
	
	

	
}
