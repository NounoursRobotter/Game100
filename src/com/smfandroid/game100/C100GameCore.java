package com.smfandroid.game100;

import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import android.graphics.Point;


public class C100GameCore
{
	public class IllegalMoveException extends RuntimeException {
		public IllegalMoveException(String string) { super(string); }
		private static final long serialVersionUID = 1L; 
	}
	
	public class IllegalGameDefinition extends RuntimeException {
		public IllegalGameDefinition(String string) { super(string); }
		private static final long serialVersionUID = 1L; 
	}
	
	
	private static final int MAX_SIZE=25;
	private static final int NOT_PLAYED=-1;
	private static final int VOIDED_PLACE=-2;
	
	public static final int ALLOWED_OK=0;
	public static final int ALLOWED_OUTOFBOUND=1;
	public static final int ALLOWED_OCCUPIED=2;
	public static final int ALLOWED_SEERULES=3;
	
	private int board[][];
	private int nextNum;
	private int gameStyle; 
	private List<Point> playedMoves;
	private List<Point> voidedPlaces;
	private Point boardSize;
	
	
	public C100GameCore(Point size, Point startPoint, int nbPlayer ) // create the board of size x size with the starting point (startPoint%size,startPoint/size) in the game mode gameMode
	{
		// matrix's initialization
		boardSize=size;
		if ((size.x<=4)||(size.x>=MAX_SIZE)) 
			throw new IllegalGameDefinition("Incorrect size X definition");
		else
			boardSize.y=size.y;
		
		if ((size.y<=4)||(size.y>=MAX_SIZE))
			throw new IllegalGameDefinition("Incorrect size Y definition");
		else
			boardSize.y=size.y;
	
		
		board=new int[boardSize.x][boardSize.y];
		
		for (int i=0;i<boardSize.x;i++)
		{
			for (int j=0;j<boardSize.y;j++)
			{
				board[i][j]=NOT_PLAYED;	
			}
		}
		
		
		//
		nextNum=2;
		gameStyle=nbPlayer;
		voidedPlaces = new LinkedList<Point>();
		
		playedMoves = new LinkedList<Point>();
		//if ((startPoint.x>=0)&&(startPoint.x<boardSize.x)&&(startPoint.y>=0)&&(startPoint.y<boardSize.y))
		if (isAllowedAsNextMove(startPoint,false)==ALLOWED_OK)
		{
			board[startPoint.x][startPoint.y]=1;
			playedMoves.add(startPoint);
		}
		else
			throw new IllegalMoveException("First move out of bound");
	}

	public void finalize()
	{
		
	}
	
	public Vector<Point> PossibleMoves() // Get the list of possible moves
	{
		Vector<Point> possibleMoves = new Vector<Point>();
		Point lastPoint=playedMoves.get(nextNum-2);
		
		if ((lastPoint.x-2>=0         )&&(board[lastPoint.x-2][lastPoint.y  ]==NOT_PLAYED)) possibleMoves.add(new Point(lastPoint.x-2,lastPoint.y  ));
		if ((lastPoint.y-2>=0         )&&(board[lastPoint.x  ][lastPoint.y-2]==NOT_PLAYED)) possibleMoves.add(new Point(lastPoint.x  ,lastPoint.y-2));
		if ((lastPoint.x+2<boardSize.x)&&(board[lastPoint.x+2][lastPoint.y  ]==NOT_PLAYED)) possibleMoves.add(new Point(lastPoint.x+2,lastPoint.y  ));
		if ((lastPoint.y+2<boardSize.y)&&(board[lastPoint.x  ][lastPoint.y+2]==NOT_PLAYED)) possibleMoves.add(new Point(lastPoint.x  ,lastPoint.y+2));
		
		if ((lastPoint.x-1>=0         )&&(lastPoint.y-1>=0         )&&(board[lastPoint.x-1][lastPoint.y-1]==NOT_PLAYED)) possibleMoves.add(new Point(lastPoint.x-1,lastPoint.y-1));
		if ((lastPoint.x-1>=0         )&&(lastPoint.y+1<boardSize.y)&&(board[lastPoint.x-1][lastPoint.y+1]==NOT_PLAYED)) possibleMoves.add(new Point(lastPoint.x-1,lastPoint.y+1));
		if ((lastPoint.x+1<boardSize.x)&&(lastPoint.y+1<boardSize.y)&&(board[lastPoint.x+1][lastPoint.y+1]==NOT_PLAYED)) possibleMoves.add(new Point(lastPoint.x+1,lastPoint.y+1));
		if ((lastPoint.x+1<boardSize.x)&&(lastPoint.y-1>=0         )&&(board[lastPoint.x+1][lastPoint.y-1]==NOT_PLAYED)) possibleMoves.add(new Point(lastPoint.x+1,lastPoint.y-1));
		
		return possibleMoves;
	}

	public int isAllowedAsNextMove(Point position, boolean checkIfInPossibleMoves) {		
		if ((position.x<0)||(position.y<0)||(position.x>=boardSize.x)||(position.y>=boardSize.y)) return ALLOWED_OUTOFBOUND;
		if (board[position.x][position.y]!=NOT_PLAYED) return ALLOWED_OCCUPIED;
		
		// if is in possible moves list 
		if (checkIfInPossibleMoves)
		{
			Vector<Point> allowedMoves = PossibleMoves();
			if (!allowedMoves.contains(position)) return ALLOWED_SEERULES;
		}
		
		return ALLOWED_OK;

	}
	
	public void PushMove(Point position) // Play a move
	{
		int allowedStatus=isAllowedAsNextMove(position,true);
		
		if( allowedStatus==ALLOWED_OUTOFBOUND) throw new IllegalMoveException("Out of bound");
		if( allowedStatus==ALLOWED_OCCUPIED) throw new IllegalMoveException("Occuped place");
		if( allowedStatus==ALLOWED_SEERULES) throw new IllegalMoveException("Illegal move");
		
		board[position.x][position.y]=nextNum;
		nextNum++;
		playedMoves.add(position);
	}
	
	public boolean isWon() // Did the player win?
	{
		if (nextNum-1==boardSize.x*boardSize.y-voidedPlaces.size()) return true; //boardSize.x*boardSize.y-unplayed
		return false;
	}
	
	public Point PopMove() // Cancel the last move
	{
		
		if (nextNum<3)
		{
			throw new EmptyStackException();
		}
		Point lastPlayed=playedMoves.remove(nextNum-2);
		nextNum--;
		board[lastPlayed.x][lastPlayed.y]=NOT_PLAYED;
		return lastPlayed;
	}
	
	public List<Point> GetState() // Get the current state of the game  - for saving purposes
	{
		return playedMoves;
	}
	
	public void SetState(List<Point> moves) throws IllegalMoveException // Set the current state of the game (the length of the table is the played moves) - for loading purposes
	{
		if (nextNum!=2) throw new IllegalGameDefinition("Game's already running!");
		moves.remove(0); // delete the first entry, used during the creation process
		
		try
		{
			while(!moves.isEmpty()) PushMove(moves.remove(0));
		}
		catch(IllegalMoveException e)
		{
			throw e;
			
		}
	}
	
	public List<Point> GetASolution() // if the number of free places is not too high, get a solution (gives size*size elements, 0 element if no solution found)
	{
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	public List<Point> TrySolutions() // randomly try to fill the grid (gives size*size elements, 0 element if no solution found)
    {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	/* Voided areas */
	public boolean SetNewVoidPlace(Point places) // void a place in the grid
    {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	public boolean CancelVoidPlace(Point places) // cancel a voided place in the grid
    {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	public List<Point> GetVoidPlaces() // Get the list of canceled places - for loading purposes
    {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	
	/* Multiplayer get score */
	public float[] GetScore_2p()
    {
		throw new UnsupportedOperationException("Not implemented yet");
	}
}
