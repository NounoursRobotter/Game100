package com.simonsgames.game100;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import android.graphics.Point;


public class C100GameCore
{
	private static int MAX_SIZE=25;
	private static int NOT_PLAYED=-1;
	private static int NOT_VOIDED=-2;
	
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
		if ((size.x<=4)||(size.x>=MAX_SIZE)) boardSize.x=10;
		if ((size.y<=4)||(size.y>=MAX_SIZE)) boardSize.y=10;
	
		
		board=new int[boardSize.x][boardSize.y];
		
		for (int i=0;i<boardSize.x;i++)
		{
			for (int j=0;j<boardSize.y;j++)
			{
				board[i][j]=NOT_PLAYED;	
			}
		}
		
		playedMoves = new LinkedList<Point>();
		if ((startPoint.x>=0)&&(startPoint.x<boardSize.x)&&(startPoint.y>=0)&&(startPoint.y<boardSize.y))
		{
			board[startPoint.x][startPoint.y]=1;
			playedMoves.add(startPoint);
		}
		else
		{
			board[0][0]=1;
			playedMoves.add(new Point(0,0));
		}
		
		//
		nextNum=2;
		gameStyle=nbPlayer;
		voidedPlaces = new LinkedList<Point>();
		
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
	
	public boolean PushMove(Point position) // Play a move
	{
		if ((position.x<0)||(position.y<0)||(position.x>=boardSize.x)||(position.y>=boardSize.y)) return false;
		if (board[position.x][position.y]!=NOT_PLAYED) return false;
		
		board[position.x][position.y]=nextNum;
		nextNum++;
		playedMoves.add(position);
		
		return true;
	}
	
	public boolean isWon() // Did the player win?
	{
		if (nextNum==boardSize.x*boardSize.y) return true; //boardSize.x*boardSize.y-unplayed
		return false;
	}
	
	public Point PopMove() // Cancel the last move
	{
		if (nextNum<3)
		{
			return new Point(-1,-1);
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
	
	public boolean SetState(List<Point> moves) // Set the current state of the game (the length of the table is the played moves) - for loading purposes
	{
		return true;
	}
	
	public List<Point> GetASolution() // if the number of free places is not too high, get a solution (gives size*size elements, 0 element if no solution found)
	{
		return  new LinkedList<Point>();
	}
	
	public List<Point> TrySolutions() // randomly try to fill the grid (gives size*size elements, 0 element if no solution found)
    {
		return new LinkedList<Point>();
	}
	
	/* Voided areas */
	public boolean SetNewVoidPlace(Point places) // void a place in the grid
    {
		return true;
	}
	
	public boolean CancelVoidPlace(Point places) // cancel a voided place in the grid
    {
		return true;
	}
	
	public List<Point> GetVoidPlaces() // Get the list of canceled places - for loading purposes
    {
		return new LinkedList<Point>();
	}
	
	
	/* Multiplayer get score */
	public float[] GetScore_2p()
    {
		float score[]=new float[2];
		score[0]=0.5f;
		score[1]=0.0f;
		
		return score;
	}
}
