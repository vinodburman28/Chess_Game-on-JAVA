package piece;

import main.GamePanel;
import main.Type;

import java.awt.print.PrinterIOException;

//for king
public class King extends Piece {
    public King(int color, int col, int row) {
        super(color, col, row);

        type= Type.KING;

        if(color== GamePanel.WHITE ){
            image=getImage("/piece/white king");
        }
        else {
            image=getImage("/piece/black king");
        }
    }
    public boolean canMove(int targetCol, int targetRow)
    {
        if(isWithinBoard(targetCol,targetRow))
        {
     if(Math.abs(targetCol-preCol)+Math.abs(targetRow-preRow)==1 ||
        Math.abs(targetCol-preCol)*Math.abs(targetRow-preRow)==1){

         if(isValidSquare(targetCol,targetRow)){
             return true;
         }
     }

     //castling
            if(moved==false){

                //right castling
                if(targetCol==preCol+2 && targetRow==preRow && pieceIsOnStraightLine(targetCol,targetRow)==false)
                {
                    for(Piece piece:GamePanel.simPieces)
                    {
                        if(piece.col==preCol+3 && piece.row==preRow && piece.moved==false){
                            GamePanel.castlingP=piece;
                            return true;
                        }
                    }
                }

                //left castling
                if(targetCol==preCol-2 && targetRow==preRow && pieceIsOnStraightLine(targetCol,targetRow)==false)
                {
                   Piece P[]=new Piece[2];
                   for(Piece piece:GamePanel.simPieces){
                       if(piece.col==preCol-3 && piece.row==targetRow){
                           P[0]=piece;
                       }
                       if(piece.col==preCol-4 && piece.row==targetRow){
                           P[1]=piece;
                       }
                       if(P[0]==null && P[1]!=null && P[1].moved==false){
                           GamePanel.castlingP=P[1];
                           return true;
                       }
                   }
                }
            }

        }
        return false;
    }
}
