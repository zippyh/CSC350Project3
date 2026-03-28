/*
ConnectFourAIPlayer.java
A class that simulates an AI agent who can play connect four. 
Code by Max Cheezic, Nicholas Demetrio, Hayden Ward
*/

package c4.players;

import c4.mvc.ConnectFourModelInterface;
import java.util.ArrayList;

public class ConnectFourAIPlayer extends ConnectFourPlayer {
	ConnectFourModelInterface model;
    private int maxDepth;
    private int playerNumber;

    public ConnectFourAIPlayer(ConnectFourModelInterface model){
        this.model = model;
        this.playerNumber = 0;
        this.maxDepth = 42;
    }

    public ConnectFourAIPlayer(ConnectFourModelInterface model, int maxDepth){
        this.model = model;
        this.playerNumber = 0;
        this.maxDepth = maxDepth;
    }

    @Override
    public int getMove() {
        this.playerNumber = model.getTurn();
        return alphaBetaSearch(model.getGrid(), this.playerNumber); // play the best possible move 
    }

    public int alphaBetaSearch(int[][] state, int turn) {
        int bestMove = -1;
        int v = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        int opponent;
        if (turn == 1) {
            opponent = 2;
        } else {
            opponent = 1;
        }

        for (int a : actions(state)) {
            // Simulate the move
            int[][] nextState = result(state, a, turn);
            // If this move wins the game right now, take it
            if (getWinner(nextState) == turn) {
                return a; 
            }
            // Otherwise, continue the search
            int val = minValue(nextState, alpha, beta, 1, opponent);
            if (val > v) {
                v = val;
                bestMove = a;
            }
            alpha = Math.max(alpha, v);
        }
        
        return bestMove;
    }

    public int utility(int[][] board, int depth) {
        int winner = getWinner(board);
        
        // Subtract depth to reward finishing the game quickly
        if (winner == this.playerNumber) return 1000000 - depth;
        
        // Add depth to reward dragging the game out (staying alive) as long as possible
        if (winner != -1) return -1000000 + depth;
        
        if (isDraw(board)) return 0;

        int score = 0;
        int opponent;
        if (this.playerNumber == 1) {
            opponent = 2;
        } else {
            opponent = 1;
        }

        // Center columns have more potential so we make these more desirable 
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 7; c++) {
                if (board[c][r] == this.playerNumber) {
                    switch (c) {
                        case 3 -> score += 5;
                        case 2, 4 -> score += 3;
                        case 1, 5 -> score += 2;
                        default -> score += 1;
                    }
                }
            }
        }

        // Then check through the board 
        score += scanAllWindows(board, this.playerNumber, opponent);

        return score;
    }

    // A "window" is any straight line of four slots in a row 
    private int evaluateWindow(int[] window, int[] windowRows, int[] windowCols, int[][] state, int me, int opp) {
        int score = 0;
        int countMe = 0, countOpp = 0, countEmpty = 0;
        int emptyCol = -1;
        int emptyRow = -1;

        // Counts each player's pieces 
        for (int i = 0; i < 4; i++) {
            if (window[i] == me) countMe++;
            else if (window[i] == opp) countOpp++;
            else {
                countEmpty++;
                emptyCol = windowCols[i];
                emptyRow = windowRows[i];
            }
        }

        // Immediate win so return a gigantic number and be done with it 
        if (countMe == 4) return 1000000; 

        // Blocking or making 3-in-a-row
        if (countOpp == 3 && countEmpty == 1) {
            // Check if the opponent can actually play here next turn
            if (isReachable(state, emptyCol, emptyRow)) {
                return -500000; // The opponent can make a four in a row
            } else {
                return -100; // Not actually a big threat yet (we'd need to stack a bunch of pieces to actually play that, for instance), so ignore it. Still sucks for us but not as bad as an immediate threat
            }
        }
        
        if (countMe == 3 && countEmpty == 1) {
            if (isReachable(state, emptyCol, emptyRow)) {
                score += 5000; // Aggressive move
            } else {
                score += 100; // Rewards a future 3-in-a-row less than one we could make now
            }
        }

        // Lower priorities (block/make 2 in a row). Makes it play defensively but not overly so 
        else if (countOpp == 2 && countEmpty == 2) score -= 50;
        else if (countMe == 2 && countEmpty == 2) score += 20;

        return score;
    }

    // Helper to see if a slot is on the bottom or resting on a piece
    // This helps to ensure the AI isn't overly scared by a 3 in a row that can't turn into a win due to a slot not yet being fillable
    private boolean isReachable(int[][] state, int col, int row) {
        if (row == 5) return true; // Bottom row is always reachable
        return state[col][row + 1] != -1; // Reachable if there's a piece below it
    }

    private int scanAllWindows(int[][] state, int me, int opp) {
        int totalScore = 0;

        // Horizontal windows
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c <= 3; c++) {
                int[] window = {state[c][r], state[c+1][r], state[c+2][r], state[c+3][r]};
                int[] rows = {r, r, r, r};
                int[] cols = {c, c+1, c+2, c+3};
                totalScore += evaluateWindow(window, rows, cols, state, me, opp);
            }
        }

        // Vertical windows
        for (int c = 0; c < 7; c++) {
            for (int r = 0; r <= 2; r++) {
                int[] window = {state[c][r], state[c][r+1], state[c][r+2], state[c][r+3]};
                int[] rows = {r, r+1, r+2, r+3};
                int[] cols = {c, c, c, c};
                totalScore += evaluateWindow(window, rows, cols, state, me, opp);
            }
        }

        // Positive diagonal windows
        for (int c = 0; c <= 3; c++) {
            for (int r = 3; r < 6; r++) {
                int[] window = {state[c][r], state[c+1][r-1], state[c+2][r-2], state[c+3][r-3]};
                int[] rows = {r, r-1, r-2, r-3};
                int[] cols = {c, c+1, c+2, c+3};
                totalScore += evaluateWindow(window, rows, cols, state, me, opp);
            }
        }

        // Negative diagonal windows
        for (int c = 0; c <= 3; c++) {
            for (int r = 0; r <= 2; r++) {
                int[] window = {state[c][r], state[c+1][r+1], state[c+2][r+2], state[c+3][r+3]};
                int[] rows = {r, r+1, r+2, r+3};
                int[] cols = {c, c+1, c+2, c+3};
                totalScore += evaluateWindow(window, rows, cols, state, me, opp);
            }
        }

        return totalScore;
    }

    public int maxValue(int[][] state, int alpha, int beta, int depth, int turn) {
        if (terminalTest(state) || depth >= maxDepth) {
            // Pass depth here so the utility can reward faster wins
            return utility(state, depth); 
        }
        
        int v = Integer.MIN_VALUE;
        int nextTurn;
        if (turn == 1) {
            nextTurn = 2;
        } else {
            nextTurn = 1;
        }
        
        for (int a : actions(state)) {
            v = Math.max(v, minValue(result(state, a, turn), alpha, beta, depth + 1, nextTurn));
            if (v >= beta) return v; 
            alpha = Math.max(alpha, v);
        }
        return v;
    }

    public int minValue(int[][] state, int alpha, int beta, int depth, int turn) {
        if (terminalTest(state) || depth >= maxDepth) {
            // Pass depth here so the utility can penalize faster losses
            return utility(state, depth); 
        }
        
        int v = Integer.MAX_VALUE;
        int nextTurn;
        if (turn == 1) {
            nextTurn = 2;
        } else {
            nextTurn = 1;
        }
        
        for (int a : actions(state)) {
            v = Math.min(v, maxValue(result(state, a, turn), alpha, beta, depth + 1, nextTurn));
            if (v <= alpha) return v; 
            beta = Math.min(beta, v);
        }
        return v;
    }

    public boolean isAutomated(){
        return true;
    }

    public boolean terminalTest(int[][] board) {
        // If there's a winner, it's a terminal state
        if (getWinner(board) != -1) {
            return true;
        }
        
        // If there's no winner, check for a draw
        for (int col = 0; col < 7; col++) {
            if (board[col][0] == -1) { 
                return false; // Found an empty spot, not a draw yet
            }
        }
        
        return true; // Must be a draw, draw is a terminal state
    }

    // Different from terminal test. 
    // terminalTest returns if a specific state results in a win or draw
    // getWinner specifically returns the player number of the winner and -1 if there is no winner
    public int getWinner(int[][] board) {
        // Check horizontal
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
        // Check vertical
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
        // Check diagonal (positive slope)
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
        // Check diagonal (negative slope)
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

    // returns if there is a draw
    // could probably be using this in terminalTest but whatever it's fine
    public boolean isDraw(int[][] state) {
        for (int col = 0; col < 7; col++) {
            if (state[col][0] == -1) { 
                return false; 
            }
        }
        return getWinner(state) == -1; 
    }
    
    public int[] actions(int[][] board){
        ArrayList<Integer> moves = new ArrayList<>();
        // The statistically best columns to check first for pruning
        int[] searchOrder = {3, 2, 4, 1, 5, 0, 6};
        
        for (int col : searchOrder) {
            if (board[col][0] == -1) { // Check if column is not full
                moves.add(col);
            }
        }

        int[] results = new int[moves.size()];
        for (int i = 0; i < results.length; i++) {
            results[i] = moves.get(i);
        }
        return results;
    }

    public int[][] result(int[][] board, int action, int turn){
        // deep copy the board
        int[][] newstate = new int[7][6];
        for(int col = 0; col < 7; col++){
            for(int row = 0; row < 6; row++){
                newstate[col][row] = board[col][row];
            }
        }

        // find the lowest empty row
        int targetRow = 5; // bottom row
        while (targetRow >= 0 && newstate[action][targetRow] != -1) {
            targetRow--;
        }

        // places the piece using the turn passed from the search
        if (targetRow >= 0) {
            newstate[action][targetRow] = turn;
        }

        return newstate;
    }
}
