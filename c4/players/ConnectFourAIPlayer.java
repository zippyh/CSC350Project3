package c4.players;

import java.util.ArrayList;

import c4.mvc.ConnectFourModelInterface;

public class ConnectFourAIPlayer extends ConnectFourPlayer {
	ConnectFourModelInterface model;
    private int maxDepth = 10;
    private int playerNumber;

    public ConnectFourAIPlayer(ConnectFourModelInterface model){
        this.model = model;
        this.playerNumber = 0;
    }

    @Override
    public int getMove() {
        // Capture our player number (1 or 2) from the model at the start of the turn
        this.playerNumber = model.getTurn();
        
        // Return the action found by the Alpha-Beta Search algorithm
        return alphaBetaSearch(model.getGrid());
    }

    public int alphaBetaSearch(int[][] state) {
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

    public int utility(int[][] board) {
        int winner = getWinner(board);
        
        if (winner == this.playerNumber) {
            return 1000; // Win for us
        } else if (winner != -1 && winner != 0) {
            return -1000; // Win for opponent (Loss for us)
        } else {
            return 0; // Draw or non-terminal
        }
    }

    public int maxValue(int[][] state, int alpha, int beta, int depth) {
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

    public int minValue(int[][] state, int alpha, int beta, int depth) {
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

    public boolean isAutomated(){
        return true;
    }

    public boolean terminalTest(int[][] board){
        boolean win = false;
        // first we check for a positive diagonal win
		for(int col=3; col<7; col++){
			for(int row=0; row<=2; row++){
				if(board[col][row] != -1){
					win = (board[col][row] == board[col-1][row+1]) && (board[col][row] == board[col-2][row+2]) && (board[col][row] == board[col-3][row+3]);
				}
				if(win) return true; // if there is a win, return true
			}
		}
        // if not, check for a negative diagonal win
        for(int col=0; col<=3; col++){
			for(int row=0; row<=2; row++){
				if(board[col][row] != -1){
					win = (board[col][row] == board[col+1][row+1]) && (board[col][row] == board[col+2][row+2]) && (board[col][row] == board[col+3][row+3]);
				}
				if(win) return true; // if there is a win, return true
			}
		}
        // if not, check for a vertical win
        for(int col=0; col<7; col++){
			for(int row=0; row<=2; row++){
				if(board[col][row] != -1){
					win = (board[col][row] == board[col][row+1]) && (board[col][row] == board[col][row+2]) && (board[col][row] == board[col][row+3]);
				}
				if(win) return true; // if there is a win, return true
			}
		}
        // if not, we check for a horizontal win
        for(int row=0; row<6; row++){
			for(int col=0; col<=3; col++){
				if(board[col][row] != -1){
					win = (board[col][row] == board[col+1][row]) && (board[col][row] == board[col+2][row]) && (board[col][row] == board[col+3][row]);
				}
				if(win) return true; // if there is a win, return true
			}
		}
        // if not, we check for a draw 
        for(int i=0; i<7; i++){
			for(int j=0; j<6; j++){
				if(board[i][j] == -1)
					return false; // if at any point we see an open space, return false
			}
		}

        return true; // if there are no wins, and there is a draw, return true
    }

    public int getWinner(int[][] board) {
        // Check Horizontal
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col <= 3; col++) {
                if (board[col][row] != -1 && 
                    board[col][row] == board[col+1][row] && 
                    board[col][row] == board[col+2][row] && 
                    board[col][row] == board[col+3][row]) {
                    return board[col][row];
                }
            }
        }
        // Check Vertical
        for (int col = 0; col < 7; col++) {
            for (int row = 0; row <= 2; row++) {
                if (board[col][row] != -1 && 
                    board[col][row] == board[col][row+1] && 
                    board[col][row] == board[col][row+2] && 
                    board[col][row] == board[col][row+3]) {
                    return board[col][row];
                }
            }
        }
        // Check Diagonal (Positive Slope)
        for (int col = 0; col <= 3; col++) {
            for (int row = 3; row < 6; row++) {
                if (board[col][row] != -1 && 
                    board[col][row] == board[col+1][row-1] && 
                    board[col][row] == board[col+2][row-2] && 
                    board[col][row] == board[col+3][row-3]) {
                    return board[col][row];
                }
            }
        }
        // Check Diagonal (Negative Slope)
        for (int col = 0; col <= 3; col++) {
            for (int row = 0; row <= 2; row++) {
                if (board[col][row] != -1 && 
                    board[col][row] == board[col+1][row+1] && 
                    board[col][row] == board[col+2][row+2] && 
                    board[col][row] == board[col+3][row+3]) {
                    return board[col][row];
                }
            }
        }
        return -1; // No winner found
    }
    
    public int[] actions(int[][] board){
        ArrayList<Integer> moves = new ArrayList<Integer>();
		for(int col=0; col<7; col++){
			if(board[col][0] == -1){
                moves.add(col);
            }
        }

		int[] results = new int[moves.size()];
		for(int i=0; i<results.length; i++){
            results[i] = moves.get(i);
        }
		
		return results;
    }

    public int[][] result(int[][] board, int action){
        // deep copy the board
        int[][] newstate = new int[7][6];
        for(int col = 0; col < 7; col++){
            for(int row = 0; row < 6; row++){
                newstate[col][row] = board[col][row];
            }
        }

        // check how many pieces each player has played
        int player1Count = 0;
        int player2Count = 0;
        for (int col = 0; col < 7; col++) {
            for (int row = 0; row < 6; row++) {
                if (board[col][row] == 1) player1Count++;
                else if (board[col][row] == 2) player2Count++;
            }
        }

        // check whose turn it is based on how many pieces
        int turn;
        if(player1Count <= player2Count){
            turn = 1;
        }else{
            turn = 2;
        }

        // find the lowest empty row
        int targetRow = 5; // bottom row
        while (targetRow >= 0 && newstate[action][targetRow] != -1) {
            targetRow--;
        }

        // places the piece
        if (targetRow >= 0) {
            newstate[action][targetRow] = turn;
        }

        return newstate;
    }
}
