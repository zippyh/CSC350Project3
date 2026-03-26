package c4.players;

import java.util.ArrayList;

import c4.mvc.ConnectFourModelInterface;

public class ConnectFourAIPlayer extends ConnectFourPlayer {
	ConnectFourModelInterface model;

    public ConnectFourAIPlayer(ConnectFourModelInterface model){
        this.model = model;
    }

	@Override
	public int getMove() {
		boolean[] moves = model.getValidMoves();
		int m = 0;
		while(!moves[m]){
            m++;
        }
		return m;
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
        int[][] newstate = new int[7][6];
        for(int col = 0; col < 7; col++){
            for(int row = 0; row < 6; row++){
                newstate[col][row] = board[col][row];
            }
        }


        throw new UnsupportedOperationException("Unimplemented");
    }
}
