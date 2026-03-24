package c4.players;

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
    
}
