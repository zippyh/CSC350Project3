package tictactoe.players;

import java.util.ArrayList;

import tictactoe.mvc.TicTacToeModel;

public class TicTacToeAIPlayer extends TicTacToePlayer {
	TicTacToeModel model;
	char symbol;
	int playerNumber;
	int maxDepth = 9;
	
	public TicTacToeAIPlayer(TicTacToeModel model, char symbol){
		this.model = model;
		this.symbol = symbol;
		this.playerNumber = 0;
	}
	
	// Assume actions are numbered 1-9
	public char[][] result(char[][] state, int action){
		// Deep copy the state
		char[][] newstate = new char[3][3];
		for(int row=0; row<3; row++)
			for(int col=0; col<3; col++)
				newstate[row][col] = state[row][col];
		
		char turn = this.getTurn(state);
		
		action -= 1;
		int col = action % 3;
		int row = action / 3;
		newstate[row][col] = turn;
		
		return newstate;
	}
	
	public int[] actions(char[][] state){
		ArrayList<Integer> moves = new ArrayList<Integer>();
		for(int row=0; row<3; row++)
			for(int col=0; col<3; col++)
				if(state[row][col] == '-')
					moves.add(row*3+col+1);
		
		int[] results = new int[moves.size()];
		for(int i=0; i<results.length; i++)
			results[i] = moves.get(i);
		
		return results;
	}
	
	public boolean terminalTest(char[][] state){
		for(int row=0; row<3; row++){
			if(state[row][0] != '-' && state[row][0] == state[row][1] && state[row][0] == state[row][2])
				return true;
		}
		for(int col=0; col<3; col++){
			if(state[0][col] != '-' && state[0][col] == state[1][col] && state[0][col] == state[2][col])
				return true;
		}
		if(state[0][0] != '-' && state[0][0] == state[1][1] && state[0][0] == state[2][2])
				return true;
		if(state[2][0] != '-' && state[2][0] == state[1][1] && state[2][0] == state[0][2])
				return true;
		
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
		for(int row=0; row<3; row++)
			for(int col=0; col<3; col++)
				if(state[row][col] == '-')
					allFilled = false;
		return allFilled;
	}
	
	protected char getWinner(char[][] state){
		for(int row=0; row<3; row++){
			if(state[row][0] != '-' && state[row][0] == state[row][1] && state[row][0] == state[row][2])
				return state[row][0];
		}
		for(int col=0; col<3; col++){
			if(state[0][col] != '-' && state[0][col] == state[1][col] && state[0][col] == state[2][col])
				return state[0][col];
		}
		if(state[0][0] != '-' && state[0][0] == state[1][1] && state[0][0] == state[2][2])
				return state[0][0];
		if(state[2][0] != '-' && state[2][0] == state[1][1] && state[2][0] == state[0][2])
				return state[2][0];
		
		return '-'; // Should not happen
	}
	
	protected char getTurn(char[][] state){
		int empties = 0;
		for(int row=0; row<3; row++)
			for(int col=0; col<3; col++)
				if(state[row][col] == '-')
					empties++;
		
		if(empties%2 == 1)
			return 'X';
		else
			return 'O';
	}
	

	@Override
    public int getMove() {
        // Capture our player number (1 or 2) from the model at the start of the turn
        this.playerNumber = model.getTurn();
        
        // Return the action found by the Alpha-Beta Search algorithm
        return alphaBetaSearch(model.getGrid());
    }

    public int alphaBetaSearch(char[][] state) {
        int bestMove = -1;
        int v = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        // The search root: iterate through all valid columns
        for (int a : actions(state)) {
            // Start the recursive tree search with minValue (opponent's turn)
            int val = minValue(result(state, a), alpha, beta, 1);
            
            // If this path yields a better value, update the best move
            if (val > v) {
                v = val;
                bestMove = a;
            }
            // Update alpha for pruning in the root
            alpha = Math.max(alpha, v);
        }
        
        return bestMove;
    }
	
    public int maxValue(char[][] state, int alpha, int beta, int depth) {
        // Terminal test or depth cutoff check 
        if (terminalTest(state) || depth >= maxDepth) {
            return utility(state);
        }
        
        int v = Integer.MIN_VALUE;
        for (int a : actions(state)) {
            v = Math.max(v, minValue(result(state, a), alpha, beta, depth + 1));
            if (v >= beta) return v; // Beta pruning 
            alpha = Math.max(alpha, v);
        }
        return v;
    }

    public int minValue(char[][] state, int alpha, int beta, int depth) {
        // Terminal test or depth cutoff check 
        if (terminalTest(state) || depth >= maxDepth) {
            return utility(state);
        }
        
        int v = Integer.MAX_VALUE;
        for (int a : actions(state)) {
            v = Math.min(v, maxValue(result(state, a), alpha, beta, depth + 1));
            if (v <= alpha) return v; // Alpha pruning 
            beta = Math.min(beta, v);
        }
        return v;
    }
}
