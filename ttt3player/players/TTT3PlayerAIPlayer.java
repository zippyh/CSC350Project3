package ttt3player.players;

import java.util.ArrayList;

import ttt3player.mvc.TTT3PlayerModel;

public class TTT3PlayerAIPlayer extends TTT3PlayerAbstractPlayer {
	TTT3PlayerModel model;
	char symbol;
	
	public TTT3PlayerAIPlayer(TTT3PlayerModel model, char symbol){
		this.model = model;
		this.symbol = symbol;
	}
	
	// Assume actions are numbered 1-16
	public char[][] result(char[][] state, int action){
		// Deep copy the state
		char[][] newstate = new char[4][4];
		for(int row=0; row<4; row++)
			for(int col=0; col<4; col++)
				newstate[row][col] = state[row][col];
		
		char turn = this.getTurn(state);
		
		action -= 1;
		int col = action % 4;
		int row = action / 4;
		newstate[row][col] = turn;
		
		return newstate;
	}
	
	public int[] actions(char[][] state){
		ArrayList<Integer> moves = new ArrayList<Integer>();
		for(int row=0; row<4; row++)
			for(int col=0; col<4; col++)
				if(state[row][col] == '-')
					moves.add(row*4+col+1);
		
		int[] results = new int[moves.size()];
		for(int i=0; i<results.length; i++)
			results[i] = moves.get(i);
		
		return results;
	}
	
	public boolean terminalTest(char[][] state){
		// Check for horizontal win
		for(int row=0; row<4; row++){
			for(int startcol=0; startcol<2; startcol++){
				if(state[row][startcol] != '-' && state[row][startcol] == state[row][startcol+1] && state[row][startcol] == state[row][startcol+2])
					return true;
			}
		}
		// Check for vertical win
		for(int col=0; col<4; col++){
			for(int startrow=0; startrow<2; startrow++){
				if(state[startrow][col] != '-' && state[startrow][col] == state[startrow+1][col] && state[startrow][col] == state[startrow+2][col])
					return true;
			}
		}
		// Check for diagonal \ win
		for(int startrow=0; startrow<2; startrow++){
			for(int startcol=0; startcol<2; startcol++){
				if(state[startrow][startcol] != '-' && state[startrow][startcol] == state[startrow+1][startcol+1] && state[startrow][startcol] == state[startrow+2][startcol+2])
					return true;
			}
		}
		for(int startrow=2; startrow<4; startrow++){
			for(int startcol=0; startcol<2; startcol++){
				if(state[startrow][startcol] != '-' && state[startrow][startcol] == state[startrow-1][startcol+1] && state[startrow][startcol] == state[startrow-2][startcol+2])
					return true;
			}
		}
		
		return isDraw(state);
	}
	
	public int utility(char[][] state){
		if(getWinner(state) == symbol)
			return 1000;
		else if(getWinner(state) != '-')
			return -1000;
		else if(isDraw(state))
			return 0;
		
		return 0; //should not happen
	}
	
	protected boolean isDraw(char[][] state){
		boolean allFilled = true;
		for(int row=0; row<4; row++)
			for(int col=0; col<4; col++)
				if(state[row][col] == '-')
					allFilled = false;
		return allFilled;
	}
	
	protected char getWinner(char[][] state){
		// Check for horizontal win
		for(int row=0; row<4; row++){
			for(int startcol=0; startcol<2; startcol++){
				if(state[row][startcol] != '-' && state[row][startcol] == state[row][startcol+1] && state[row][startcol] == state[row][startcol+2])
					return state[row][startcol];
			}
		}
		// Check for vertical win
		for(int col=0; col<4; col++){
			for(int startrow=0; startrow<2; startrow++){
				if(state[startrow][col] != '-' && state[startrow][col] == state[startrow+1][col] && state[startrow][col] == state[startrow+2][col])
					return state[startrow][col];
			}
		}
		// Check for diagonal \ win
		for(int startrow=0; startrow<2; startrow++){
			for(int startcol=0; startcol<2; startcol++){
				if(state[startrow][startcol] != '-' && state[startrow][startcol] == state[startrow+1][startcol+1] && state[startrow][startcol] == state[startrow+2][startcol+2])
					return state[startrow][startcol];
			}
		}
		for(int startrow=2; startrow<4; startrow++){
			for(int startcol=0; startcol<2; startcol++){
				if(state[startrow][startcol] != '-' && state[startrow][startcol] == state[startrow-1][startcol+1] && state[startrow][startcol] == state[startrow-2][startcol+2])
					return state[startrow][startcol];
			}
		}
		
		return '-'; // Should not happen
	}
	
	protected char getTurn(char[][] state){
		int empties = 0;
		for(int row=0; row<4; row++)
			for(int col=0; col<4; col++)
				if(state[row][col] == '-')
					empties++;
		
		if(empties%3 == 1)
			return 'X';
		else if(empties%3 == 0)
			return 'O';
		else
			return '+';
	}
	
	public int getMove(){
    	char[][] state = model.getGrid();
    	int bestMove = -1;
    	int bestValue = Integer.MIN_VALUE;

    	for(int action : actions(state)){
        	char[][] newState = result(state, action);
        	int[] values = threePlayerMiniMax(newState, 5);

        	int myIndex = getPlayerIndex(symbol);
        	if(values[myIndex] > bestValue){
            	bestValue = values[myIndex];
            	bestMove = action;
        	}
    	}

    	return bestMove;
	}

	private int[] threePlayerMiniMax(char[][] state, int depth){
    	if(terminalTest(state) || depth == 0){
        	return evaluate(state);
		}

    	char turn = getTurn(state);
    	int playerIndex = getPlayerIndex(turn);

    	int[] bestValue = null;

    	for(int action : actions(state)){
        	char[][] newState = result(state, action);
        	int[] childValue = threePlayerMiniMax(newState, depth - 1);

        	if(bestValue == null || childValue[playerIndex] > bestValue[playerIndex]){
            	bestValue = childValue;
        	}
    	}

    	return bestValue;
	}

	private int getPlayerIndex(char player){
    	if(player == 'X'){
			return 0;
		} 
    	if(player == 'O'){
			return 1;
		} 

    	return 2;
	}

	private int[] evaluate(char[][] state){
    	int[] scores = new int[3];

    	char winner = getWinner(state);

    	if(winner == 'X'){ 
			scores[0] = 1000;
		}else if(winner == 'O'){
			scores[1] = 1000;
		}else if(winner == '+'){ 
			scores[2] = 1000;
		}else if(isDraw(state)) {
			return scores;
		}else{
        	scores = heuristic(state);
    	}

    	return scores;
	}

	private int[] heuristic(char[][] state){
    	int[] scores = new int[3];

    	for(int row=0; row<4; row++){
        	for(int col=0; col<2; col++){
            	evaluateLine(state[row][col], state[row][col+1], state[row][col+2], scores);
        	}
    	}

    	for(int col=0; col<4; col++){
        	for(int row=0; row<2; row++){
            	evaluateLine(state[row][col], state[row+1][col], state[row+2][col], scores);
        	}
    	}

    	for(int row=0; row<2; row++){
        	for(int col=0; col<2; col++){
            	evaluateLine(state[row][col], state[row+1][col+1], state[row+2][col+2], scores);
        	}
    	}

    	for(int row=2; row<4; row++){
        	for(int col=0; col<2; col++){
            	evaluateLine(state[row][col], state[row-1][col+1], state[row-2][col+2], scores);
        	}
    	}

    	return scores;
	}

	private void evaluateLine(char a, char b, char c, int[] scores){
    	int[] count = new int[3];

    	char[] line = {a, b, c};

    	for(char cell : line){
        	if(cell == 'X'){
				count[0]++;
			}else if(cell == 'O'){
				count[1]++;
			}
        	else if(cell == '+'){
				count[2]++;
			} 
    	}

    	for(int i=0; i<3; i++){
        	if(count[i] > 0 && count[(i+1)%3] == 0 && count[(i+2)%3] == 0){
            	if(count[i] == 2){
					scores[i] += 50;
				}
            	else if(count[i] == 1){
					scores[i] += 10;
				}
        	}
    	}
	}
}
