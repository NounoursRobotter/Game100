package com.smfandroid.game100;

import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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
	
	private Random rnd = new Random();
	
	private static final int MAX_SIZE=25;
	private static final int MAX_REMAIN_BEFORE_FULL_SEARCH=50;
	private static final int NOT_PLAYED=-1;
	private static final int VOIDED_PLACE=-2;
	private static final int SOLUTION_MAXCOMPUTE=1000;
	
	private enum MoveStatus { ALLOWED_OUTOFBOUND, ALLOWED_OCCUPIED, ALLOWED_SEERULES, ALLOWED_OK };
	
	private int board[][];

	private LinkedList<Point> playedMoves;
	private LinkedList<Point> voidedPlaces;
	private Point boardSize;
	
	public void createGame(Point boardSize)
	{
		createGame(boardSize, 1);
	}	
	
	public void createGame(Point size, int nbPlayer)
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

		voidedPlaces = new LinkedList<Point>();
		playedMoves = new LinkedList<Point>();
	}
	
	public Point getRandomEmptyPosition()
	{
		Point p = new Point();
		do {
			p.x = rnd.nextInt(boardSize.x);
			p.y = rnd.nextInt(boardSize.y);
		} while(isAllowedAsNextMove(p, false) != MoveStatus.ALLOWED_OK );
		return p;
	}
	
	public void autoInitGame(int nbVoids)
	{
		for(int i = 0; i < nbVoids; i++) {
			addVoidPlace(getRandomEmptyPosition());
		}		
	}
	
	public C100GameCore(Point size, int nbPlayer ) // create the board of size x size with the starting point (startPoint%size,startPoint/size) in the game mode gameMode
	{
		createGame(size, nbPlayer);
	}

	public void finalize()
	{
		
	}
	
	public LinkedList<Point> possibleMoves() // Get the list of possible moves
	{
		LinkedList<Point> possibleMoves = new LinkedList<Point>();
		
		if(getNbPlayedMoves() != 0) {
			Point lastPoint=playedMoves.getLast();
			
			if ((lastPoint.x-3>=0         )&&(board[lastPoint.x-3][lastPoint.y  ]==NOT_PLAYED)) possibleMoves.add(new Point(lastPoint.x-3,lastPoint.y  ));
			if ((lastPoint.y-3>=0         )&&(board[lastPoint.x  ][lastPoint.y-3]==NOT_PLAYED)) possibleMoves.add(new Point(lastPoint.x  ,lastPoint.y-3));
			if ((lastPoint.x+3<boardSize.x)&&(board[lastPoint.x+3][lastPoint.y  ]==NOT_PLAYED)) possibleMoves.add(new Point(lastPoint.x+3,lastPoint.y  ));
			if ((lastPoint.y+3<boardSize.y)&&(board[lastPoint.x  ][lastPoint.y+3]==NOT_PLAYED)) possibleMoves.add(new Point(lastPoint.x  ,lastPoint.y+3));
			
			if ((lastPoint.x-2>=0         )&&(lastPoint.y-2>=0         )&&(board[lastPoint.x-2][lastPoint.y-2]==NOT_PLAYED)) possibleMoves.add(new Point(lastPoint.x-2,lastPoint.y-2));
			if ((lastPoint.x-2>=0         )&&(lastPoint.y+2<boardSize.y)&&(board[lastPoint.x-2][lastPoint.y+2]==NOT_PLAYED)) possibleMoves.add(new Point(lastPoint.x-2,lastPoint.y+2));
			if ((lastPoint.x+2<boardSize.x)&&(lastPoint.y+2<boardSize.y)&&(board[lastPoint.x+2][lastPoint.y+2]==NOT_PLAYED)) possibleMoves.add(new Point(lastPoint.x+2,lastPoint.y+2));
			if ((lastPoint.x+2<boardSize.x)&&(lastPoint.y-2>=0         )&&(board[lastPoint.x+2][lastPoint.y-2]==NOT_PLAYED)) possibleMoves.add(new Point(lastPoint.x+2,lastPoint.y-2));
		} else {
			// If board is empty, any non void cell is available !
			for(int i = 0; i < boardSize.x; i++) {
				for(int j = 0; j< boardSize.y; j++) {
					Point proposition = new Point(i, j);
					if(isAllowedAsNextMove(proposition, false) == MoveStatus.ALLOWED_OK)
						possibleMoves.add(proposition);
				}
			}
		}
		return possibleMoves;
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
		MoveStatus allowedStatus;
		if(getNbPlayedMoves() == 0) // no number yet, everything is allowed
			allowedStatus=isAllowedAsNextMove(position,false);
		else
			allowedStatus=isAllowedAsNextMove(position,true);
		
		if( allowedStatus == MoveStatus.ALLOWED_OUTOFBOUND) throw new IllegalMoveException("Out of bound");
		if( allowedStatus == MoveStatus.ALLOWED_OCCUPIED) throw new IllegalMoveException("Occuped place");
		if( allowedStatus == MoveStatus.ALLOWED_SEERULES) throw new IllegalMoveException("Illegal move");
		
		playedMoves.add(new Point(position));
		board[position.x][position.y]=getNbPlayedMoves();
	}
	
	public boolean isWon() // Did the player win?
	{
		int actualNbElements = getNbPlayedMoves();
		int targetNbElements = boardSize.x * boardSize.y - voidedPlaces.size();
		return (targetNbElements == actualNbElements); 
	}
	
	public Point popMove() // Cancel the last move
	{
		
		if (getNbPlayedMoves() == 0)
		{
			throw new EmptyStackException();
		}
		
		Point lastPlayed = playedMoves.removeLast();
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
		
		// These two lines were commented becauses Simon uses this fonction like a monkey
		//if (getNbPlayedMoves() != 0) throw new IllegalGameDefinition("Game's already running!");
		//nMove.remove(0); // delete the first entry, used during the creation process
		
		// reset game
		while(!playedMoves.isEmpty()) popMove();
		
		//play game
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
		// save the current game
		int originalSize = getNbPlayedMoves();
		
		int[] interLevel=new int[1];
		interLevel[0]=0;
		
		
		boolean result=false;
		//solve
		interLevel[0]=SOLUTION_MAXCOMPUTE;
		
		List<Point> listMoves = possibleMoves();
		pushMove(listMoves.get(0));
		while ((result==false)&&(interLevel[0]>=SOLUTION_MAXCOMPUTE-1)&&(!listMoves.isEmpty()))
		{
			popMove();
			pushMove(listMoves.remove(0));
			interLevel[0]=0;
			result=solve(possibleMoves(),interLevel);
			Log.i("Game100Debug", "result "+Boolean.toString(result)+ " interLevel "+Integer.toString(interLevel[0])+ " fin list "+Boolean.toString(listMoves.isEmpty()));
		}
		if (result==false) 
			{
			throw new UnsupportedOperationException("No solutions found");
			}
		
//		for (Point elem: playedMoves)
//		{
//			Log.i("Game100Debug", "Final: X="+Integer.toString(elem.x)+" Y="+Integer.toString(elem.y)+ " Board at: "+ board[elem.x][elem.y]);
//		}
		LinkedList<Point> foundSolution=new LinkedList<Point>(playedMoves);
		
		//restore state
		while (originalSize != getNbPlayedMoves()) popMove();
		
		return foundSolution;
	}
	
	public LinkedList<Point> trySolutions() // randomly try to fill the grid (gives size*size elements, 0 element if no solution found)
    {
		// if too much empty, stop
		int remainingMoves = boardSize.x * boardSize.y - voidedPlaces.size() - getNbPlayedMoves(); 
		if (remainingMoves > MAX_REMAIN_BEFORE_FULL_SEARCH) 
			throw new IllegalMoveException("Too many empty places to test final solution");
		
		
		// save the current game
		int originalSize = getNbPlayedMoves();
		
		//solve
		boolean result=solve();
		
		// si on a atteint le niveau maximal 
		if (result==false) 
			{
			throw new UnsupportedOperationException("No solutions found");
			}
		
		LinkedList<Point> foundSolution=new LinkedList<Point>(playedMoves);
		
		//restore state
		while (originalSize != getNbPlayedMoves()) popMove();
		
		return foundSolution;
	}
	
	// Simon's version: used for solution finder: trySolutions()
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
	
	// Remy's version: used for solution finder: getSolution()
	//
	private boolean solve(List<Point> nextElements, int[] interLevel)
	{
		interLevel[0]++;
		if (interLevel[0]>SOLUTION_MAXCOMPUTE) return false;
		
		List<List<Point>> nextElemConnex = new LinkedList<List<Point>>();
		List<Point> SimonShouldNotLookAtThis= new LinkedList<Point>();
		int minVal;
		Point played;
		List<Point> elemMin = new LinkedList<Point>();
		
		for(Point p: nextElements)
		{
			pushMove(p);
			SimonShouldNotLookAtThis=possibleMoves();
			SimonShouldNotLookAtThis.add(p);
			nextElemConnex.add(SimonShouldNotLookAtThis);
			popMove();
		}
		//Log.i("Game100Debug", "nextElemConnex.size()="+Integer.toString(nextElemConnex.size()));
		
		// tant que la liste n'est pas vide
		while (!nextElemConnex.isEmpty())
		{
			minVal=100;
			// On prend le minimum de la liste,
			for (List<Point> elem: nextElemConnex)
			{
				if (minVal>elem.size())
				{
					minVal=elem.size();
					elemMin=elem;
				}
			}
			
			// On le vire de la liste
			nextElemConnex.remove(elemMin);
			
			played=elemMin.remove(elemMin.size()-1);
			// on le joue
			pushMove(played);
			
			// si sa connexité est nulle (=1 à cause du SimonShouldNotLookAtThis.add(p);)
			if (minVal==1)
			{
				boolean retVal=isWon();
				if (!retVal) popMove();
				//if (!retVal) Log.i("Game100Debug", "Return back");
		    	return retVal;
			}
			
			// sinon on reprend
			if (solve(elemMin,interLevel))
			{
				return true;
			}
			popMove();
			
		}
		return false;

	    }
	
	/* Voided areas */
	public void addVoidPlace(Point places) // void a place in the grid
    {
		MoveStatus allowedStatus=isAllowedAsNextMove(places,false);
		
		if( allowedStatus==MoveStatus.ALLOWED_OUTOFBOUND) 
			throw new IllegalMoveException("Out of bound");
		
		if( allowedStatus==MoveStatus.ALLOWED_OCCUPIED) 
			throw new IllegalMoveException("Occuped place");
		
		board[places.x][places.y]=VOIDED_PLACE;
		voidedPlaces.add(new Point(places));
	}
	
	public void removeVoidPlace(Point places) // cancel a voided place in the grid
    {
		MoveStatus allowedStatus=isAllowedAsNextMove(places,false);
		
		if( allowedStatus==MoveStatus.ALLOWED_OUTOFBOUND) 
			throw new IllegalMoveException("Out of bound");
		
		if( board[places.x][places.y]!=VOIDED_PLACE) 
			throw new IllegalMoveException("Not a voided place");
		
		board[places.x][places.y]=NOT_PLAYED;
		
		if (voidedPlaces.remove(places)==false) 
			throw new IllegalMoveException("Not a voided place...weird!!!");
		
	}
	
	public LinkedList<Point> getVoidPlaces() // Get the list of canceled places - for loading purposes
    {
		return voidedPlaces;
	}
	
	
	/* Multiplayer get score */
	public float[] getScore2p()
    {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
	/* Monoplayer get score */
	public float getScore()
    {
		return getNbPlayedMoves() - 1;
	}

	public int getNbPlayedMoves()
	{
		return playedMoves.size();
	}
}
