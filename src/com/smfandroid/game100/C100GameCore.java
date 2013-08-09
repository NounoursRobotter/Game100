package com.smfandroid.game100;

import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import android.graphics.Point;
import android.util.Log;


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
	private static final int MAX_REMAIN_BEFORE_FULL_SEARCH=50;
	private static final int NOT_PLAYED=-1;
	private static final int VOIDED_PLACE=-2;
	
	private enum MoveStatus { ALLOWED_OUTOFBOUND, ALLOWED_OCCUPIED, ALLOWED_SEERULES, ALLOWED_OK };
	
	private int board[][];
	private int nextNum;
	private int gameStyle; 
	private LinkedList<Point> playedMoves;
	private LinkedList<Point> voidedPlaces;
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
		if (isAllowedAsNextMove(startPoint,false)==MoveStatus.ALLOWED_OK)
		{
			board[startPoint.x][startPoint.y]=1;
			playedMoves.add(new Point(startPoint));
		}
		else
			throw new IllegalMoveException("First move out of bound");
	}

	public void finalize()
	{
		
	}
	
	public LinkedList<Point> possibleMoves() // Get the list of possible moves
	{
		List<Point> possibleMoves = new Vector<Point>();
		Point lastPoint=playedMoves.get(nextNum-2);
		
		if ((lastPoint.x-3>=0         )&&(board[lastPoint.x-3][lastPoint.y  ]==NOT_PLAYED)) possibleMoves.add(new Point(lastPoint.x-3,lastPoint.y  ));
		if ((lastPoint.y-3>=0         )&&(board[lastPoint.x  ][lastPoint.y-3]==NOT_PLAYED)) possibleMoves.add(new Point(lastPoint.x  ,lastPoint.y-3));
		if ((lastPoint.x+3<boardSize.x)&&(board[lastPoint.x+3][lastPoint.y  ]==NOT_PLAYED)) possibleMoves.add(new Point(lastPoint.x+3,lastPoint.y  ));
		if ((lastPoint.y+3<boardSize.y)&&(board[lastPoint.x  ][lastPoint.y+3]==NOT_PLAYED)) possibleMoves.add(new Point(lastPoint.x  ,lastPoint.y+3));
		
		if ((lastPoint.x-2>=0         )&&(lastPoint.y-2>=0         )&&(board[lastPoint.x-2][lastPoint.y-2]==NOT_PLAYED)) possibleMoves.add(new Point(lastPoint.x-2,lastPoint.y-2));
		if ((lastPoint.x-2>=0         )&&(lastPoint.y+2<boardSize.y)&&(board[lastPoint.x-2][lastPoint.y+2]==NOT_PLAYED)) possibleMoves.add(new Point(lastPoint.x-2,lastPoint.y+2));
		if ((lastPoint.x+2<boardSize.x)&&(lastPoint.y+2<boardSize.y)&&(board[lastPoint.x+2][lastPoint.y+2]==NOT_PLAYED)) possibleMoves.add(new Point(lastPoint.x+2,lastPoint.y+2));
		if ((lastPoint.x+2<boardSize.x)&&(lastPoint.y-2>=0         )&&(board[lastPoint.x+2][lastPoint.y-2]==NOT_PLAYED)) possibleMoves.add(new Point(lastPoint.x+2,lastPoint.y-2));
		
		return new LinkedList<Point>(possibleMoves);
	}

	
	public MoveStatus isAllowedAsNextMove(Point position, boolean checkIfInPossibleMoves) {		
		if (
				(position.x<0)||(position.y<0) ||
				(position.x>=boardSize.x)||(position.y>=boardSize.y)
			) return MoveStatus.ALLOWED_OUTOFBOUND;
		
		if (board[position.x][position.y]!=NOT_PLAYED) return MoveStatus.ALLOWED_OCCUPIED;
		
		// if is in possible moves list 
		if (checkIfInPossibleMoves)
		{
			List<Point> allowedMoves = possibleMoves();
			if (!allowedMoves.contains(position)) return MoveStatus.ALLOWED_SEERULES;
		}
		
		return MoveStatus.ALLOWED_OK;

	}
	
	public void pushMove(Point position) // Play a move
	{
		MoveStatus allowedStatus=isAllowedAsNextMove(position,true);
		
		if( allowedStatus == MoveStatus.ALLOWED_OUTOFBOUND) throw new IllegalMoveException("Out of bound");
		if( allowedStatus == MoveStatus.ALLOWED_OCCUPIED) throw new IllegalMoveException("Occuped place");
		if( allowedStatus == MoveStatus.ALLOWED_SEERULES) throw new IllegalMoveException("Illegal move");
		
		board[position.x][position.y]=nextNum;
		nextNum++;
		playedMoves.add(new Point(position));
	}
	
	public boolean isWon() // Did the player win?
	{
		if (nextNum-1==boardSize.x*boardSize.y-voidedPlaces.size()) return true; //boardSize.x*boardSize.y-unplayed
		return false;
	}
	
	public Point popMove() // Cancel the last move
	{
		
		if (nextNum<3)
		{
			throw new EmptyStackException();
		}
		Point lastPlayed=playedMoves.remove(nextNum-2);
		nextNum--;
		board[lastPlayed.x][lastPlayed.y]=NOT_PLAYED;
		return new Point(lastPlayed);
	}
	
	public LinkedList<Point> getCurrentState() // Get the current state of the game  - for saving purposes
	{
		return new LinkedList<Point>(playedMoves);
	}
	
	public void setCurrentState(LinkedList<Point> moves) throws IllegalMoveException // Set the current state of the game (the length of the table is the played moves) - for loading purposes
	{
		LinkedList<Point> nMove=new LinkedList<Point>(moves);
		if (nextNum!=2) throw new IllegalGameDefinition("Game's already running!");
		nMove.remove(0); // delete the first entry, used during the creation process
		
		try
		{
			while(!nMove.isEmpty()) pushMove(nMove.remove(0));
		}
		catch(IllegalMoveException e)
		{
			throw e;
			
		}
	}
	
	public LinkedList<Point> getSolution() // if the number of free places is not too high, get a solution (gives size*size elements, 0 element if no solution found)
	{
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	public LinkedList<Point> trySolutions() // randomly try to fill the grid (gives size*size elements, 0 element if no solution found)
    {
		// if too much empty, stop
		if (boardSize.x*boardSize.y-voidedPlaces.size()-nextNum+1<MAX_REMAIN_BEFORE_FULL_SEARCH) throw new IllegalMoveException("Too many empty places to test final solution");
		// save the current game
		int saveNextNum=nextNum;
		
		//solve
		boolean result=solve();
		if (result==false) 
			{
			throw new UnsupportedOperationException("No solutions found");
			}
		
		LinkedList<Point> foundSolution=new LinkedList<Point>(playedMoves);
		
		//restore state
		while (saveNextNum!=nextNum) popMove();
		
		return foundSolution;
	}
	
	private boolean solve()
	{
        if(isWon()) // stop condition
        {
            return true;
        }
        else
        {
            List<Point> nextElements = possibleMoves();
            for(Point p: nextElements)
            { // for every other move
                pushMove(p);
                if(solve())
                {
                	return true; // if the move end the game it is a good one
                }
                else
                {
                	popMove();   // else we just try the next
                }
            }
        }
        return false; // no move
	}
	
	/* Voided areas */
	public void addVoidPlace(Point places) // void a place in the grid
    {
		MoveStatus allowedStatus=isAllowedAsNextMove(places,false);
		
		if( allowedStatus==MoveStatus.ALLOWED_OUTOFBOUND) throw new IllegalMoveException("Out of bound");
		if( allowedStatus==MoveStatus.ALLOWED_OCCUPIED) throw new IllegalMoveException("Occuped place");
		
		board[places.x][places.y]=VOIDED_PLACE;
		voidedPlaces.add(new Point(places));
	}
	
	public void removeVoidPlace(Point places) // cancel a voided place in the grid
    {
		MoveStatus allowedStatus=isAllowedAsNextMove(places,false);
		
		if( allowedStatus==MoveStatus.ALLOWED_OUTOFBOUND) throw new IllegalMoveException("Out of bound");
		if( board[places.x][places.y]!=VOIDED_PLACE) throw new IllegalMoveException("Not a voided place");
		
		board[places.x][places.y]=NOT_PLAYED;
		if (voidedPlaces.remove(places)==false) throw new IllegalMoveException("Not a voided place...weird!!!");
		
	}
	
	public LinkedList<Point> getVoidPlaces() // Get the list of canceled places - for loading purposes
    {
		return new LinkedList<Point>(voidedPlaces);
	}
	
	
	/* Multiplayer get score */
	public float[] getScore2p()
    {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	/* Monoplayer get score */
	public float getScore()
    {
		return nextNum-1;
	}	
}
