package piece;

import main.Board;
import main.GamePanel;
import main.Type;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Piece{
     public Type type;
     public BufferedImage image;
     public int x,y;
     public int col, row, preCol, preRow;
     public int color;
     public Piece hittingP;
     public boolean moved, twoStepped;

     public Piece(int color, int col, int row) {
         this.color=color;
         this.col=col;
         this.row=row;
         x=getX(col);
         y=getY(row);
         preCol=col;
         preRow=row;

     }
     // to load the image
     public BufferedImage getImage(String imagePath){
         BufferedImage  image=null;
          try{
                image= ImageIO.read(getClass().getResourceAsStream(imagePath+".png"));
          }catch(IOException e) {
              e.printStackTrace();
          }
          return image;
     }

     public int getX(int col){
         return col * Board.square_size;

     }

     public int getY(int row){

         return row* Board.square_size;
 }
    public int getCol(int row){

        return (x+Board.half_square)/Board.square_size;
    }
    public int getRow(int row){

        return (y+Board.half_square)/Board.square_size;
    }

    //method to get the index value of piece
    public int getIndex(){
         for(int index=0;index<GamePanel.simPieces.size();index++){
             if(GamePanel.simPieces.get(index)==this) {
                 return index;
             }
        }
         return 0;
    }


    public void updatePosition(){

         //to check an en peasant
        if(type==Type.PAWN)
        {
            if(Math.abs(row-preRow)==2){
                twoStepped=true;
            }
        }

         x=getX(col);
         y=getY(row);
         preCol= getCol(x);
         preRow=getRow(y);
         moved=true;
    }
     //reset the position of active piece
    public void resetPosition(){
         col=preCol;
         row=preRow;
         x=getX(col);
         y=getY(row);

    }


    // for piece moves restriction
    public boolean canMove(int targetCol , int  targetRow){
         return false;
    }

    // to check is the piece is within board or not
    public boolean isWithinBoard(int targetCol, int targetRow) {
        if (targetCol >= 0 && targetCol <= 7 && targetRow >= 0 && targetRow <= 7) {
            return true;
        }
        return false;
    }

    //method for elephant move
    public boolean isSameSquare(int targetCol, int targetRow){
         if(targetCol==preCol && targetRow==preRow){
             return true;
         }
         return false;
    }
    //method to check is there any piece in the way of horse
        public boolean pieceIsOnStraightLine(int targetCol, int targetRow){

         //when the elephant is moving left
            for(int c=preCol-1;c>targetCol;c--){
                for(Piece piece:GamePanel.simPieces){
                    if(piece.col==c && piece.row==targetRow) {
                        hittingP=piece;
                        return true;
                    }
                }

            }

           //when the elephant is moving right
            for(int c=preCol+1;c<targetCol;c++){
                for(Piece piece:GamePanel.simPieces){
                    if(piece.col==c && piece.row==targetRow) {
                        hittingP=piece;
                        return true;
                    }
                }

            }

            //when the elephant is moving up
            for(int r=preRow-1;r>targetRow;r--){
                for(Piece piece:GamePanel.simPieces){
                    if(piece.col==targetCol  && piece.row==r) {
                        hittingP=piece;
                        return true;
                    }
                }

            }

            //when the elephant is moving down
            for(int r=preRow+1;r<targetRow;r++){
                for(Piece piece:GamePanel.simPieces){
                    if(piece.col==targetCol  && piece.row==r) {
                        hittingP=piece;
                        return true;
                    }
                }

            }
return false;
     }

     //method to check camel direction obstacles
    public boolean pieceIsOnDiagonalLIne(int targetCol, int targetRow){

         if(targetRow<preRow){
             //Up Left
             for(int c=preCol-1; c>targetCol;c--){
                 int diff=Math.abs(c-preCol);
                 for(Piece piece:GamePanel.simPieces){
                     if(piece.col==c && piece.row==preRow-diff){
                         hittingP=piece;
                         return true;
                     }
                 }
             }

             //Up Right
             for(int c=preCol+1; c<targetCol;c++){
                 int diff=Math.abs(c-preCol);
                 for(Piece piece:GamePanel.simPieces){
                     if(piece.col==c && piece.row==preRow-diff){
                         hittingP=piece;
                         return true;
                     }
                 }
             }
         }

         if(targetRow>preRow){
             //Down Left
             for(int c=preCol-1; c>targetCol;c--){
                 int diff=Math.abs(c-preCol);
                 for(Piece piece:GamePanel.simPieces){
                     if(piece.col==c && piece.row==preRow+diff){
                         hittingP=piece;
                         return true;
                     }
                 }
             }

             //Down Right
             for(int c=preCol+1; c<targetCol;c++){
                 int diff=Math.abs(c-preCol);
                 for(Piece piece: GamePanel.simPieces){
                     if(piece.col==c && piece.row==preRow+diff){
                         hittingP=piece;
                         return true;
                     }
                 }
             }

         }

     return false;
     }

    //method to check if a piece is hitting some another piece
    public Piece getHittingP(int targetCol, int targetRow){
         for(Piece piece: GamePanel.simPieces){
             if(piece.col==targetCol && piece.row==targetRow && piece!=this){
                 return piece;
             }
         }
     return null;
    }

    //method to check is the square is empty or get place by some piece
    public boolean isValidSquare(int targetCol, int targetRow){
         hittingP=getHittingP(targetCol,targetRow);

         if(hittingP==null){ //it means square is empty
              return true;
         }
         else{
             // it means square is occupied
             if(hittingP.color!=this.color){
                 //if the color is different means it is oppenent
                 return true;
             }
             else{
                 //it means it is of same team piece
                 hittingP=null;
             }
         }
         return false;
    }
       //draw all the pieces on the board
       public void draw(Graphics2D g2){
         g2.drawImage(image,x,y,Board.square_size,Board.square_size,null);

       }
}
