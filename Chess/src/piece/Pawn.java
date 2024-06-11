package piece;

import main.GamePanel;
import main.Type;

// for soldier
public class Pawn extends Piece{
    public Pawn(int color, int col, int row) {
        super(color, col, row);

        //assign a id
        type= Type.PAWN;

        if(color== GamePanel.WHITE ){
            image=getImage("/piece/white soldier");
        }
        else {
            image=getImage("/piece/black soldier");
        }
    }
    public boolean canMove(int targetCol, int targetRow){
        if(isWithinBoard(targetCol,targetRow) && isSameSquare(targetCol,targetRow)==false){

            //define its move based on its color
            int moveValue;
            if(color==GamePanel.WHITE)
            {
                moveValue=-1;
            }else{
                moveValue=1;
            }

            //check the hitting piece
            hittingP=getHittingP(targetCol,targetRow);
            if(targetCol==preCol && targetRow==preRow + moveValue && hittingP==null){
                return true;
            }

            //for two square movement
            if(targetCol==preCol && targetRow==preRow + moveValue*2  && hittingP==null && moved==false &&
            pieceIsOnStraightLine(targetCol,targetRow)==false)
            {
                return true;
            }

            //diagonal movement and capturing opponents piece
            if(Math.abs(targetCol-preCol)==1 && targetRow==preRow + moveValue && hittingP!=null &&
            hittingP.color != color){
                return true;
            }

            //en peasant move
            if(Math.abs(targetCol-preCol)==1 && targetRow==preRow + moveValue)
            {
                for(Piece piece : GamePanel.simPieces)
                {
                    if(piece.col==targetCol && piece.row==preRow && piece.twoStepped==true){
                        hittingP=piece;
                        return true;
                    }
                }
            }

        }
        return false;
    }
}
