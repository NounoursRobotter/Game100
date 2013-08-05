package com.simonsgames.game100;

public class C100GameCore {

	public C100GameCore(int size, int startPoint, int gameMode ) // create the board of size x size with the starting point (startPoint%size,startPoint/size) in the game mode gameMode
	{
		
		
	}

	public void finalize()
	{
		
		
	}
	
	public int[] PossibleMoves() // Get the list of possible moves
	{
		int[] possible=new int[8];
		
		return possible;
	}
	
	public boolean PushMove(int position) // Get the list of possible moves
	{
		
		return true;
	}
	
	public int PopMove() // Cancel the last move
	{
		
		return 42;
	}
	
	public int[] GetState() // Get the current state of the game (the length of the table is the played moves) - for saving purposes
	{
		
		return new int[8];
	}
	
	public boolean SetState(int[] moves) // Set the current state of the game (the length of the table is the played moves) - for loading purposes
	{
		
		return true;
	}
	
	public int[] GetASolution() // if the number of free places is not too high, get a solution (gives size*size elements, 0 element if no solution found)
	{
		
		return new int[8];
	}
	
	public int[] TrySolutions() // randomly try to fill the grid (gives size*size elements, 0 element if no solution found)
    {
		
		return new int[8];
	}
	
	/* zones annulées */
	public boolean SetVoidPlaces(int[] places) // before the begenning of the game, cancel some places in the grid
    {
		
		return true;
	}
	
	public int[] GetVoidPlaces() // Get the list of canceled places - for loading purposes
    {
		
		return new int[8];
	}
	
	public int GetLastVoidedPlaces() // Get the place of the last canceled places
    {
		
		return 42;
	}
	
	/* par rapport à mon paragraphe précédent sur le multijoueur */
	public float[] GetScore_2p()
    {
		
		return new float[8];
	}
}
