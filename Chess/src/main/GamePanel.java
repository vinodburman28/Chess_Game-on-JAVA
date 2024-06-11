package main;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import piece.Piece;
import piece.Pawn;
import piece.Rook;
import piece.Knight;
import piece.Bishop;
import piece.King;
import piece.Queen;

// game screen work
public class GamePanel extends JPanel implements Runnable{
    public static final int WIDTH = 800; // width of the game screen
    public static final int HEIGHT = 560; // height of the game screen
    final int FPS = 60; // to refresh the screen 60 times per second

    Thread gameThread; // to run the game on loop
    Board board = new Board();
    Mouse mouse=new Mouse();

    //pieces
    public static ArrayList<Piece> pieces =new ArrayList<>();
    public static ArrayList<Piece> simPieces =new ArrayList<>();
    ArrayList<Piece> promoPieces=new ArrayList<>();
    Piece activeP; //to handle the piece hold by player
    public static Piece castlingP; //for the special movement between king and elephant
    Piece checkingP; // to handle checkmate


    //booleans
    boolean canMove;
    boolean validSquare;
    boolean promotion;
    boolean gameover;
    boolean stalemate;
    //color
    public static final int WHITE=0;
    public static final int BLACK=1;
    int currentColor=WHITE;

    // constructor of the upper class
    public GamePanel(){
        setPreferredSize(new Dimension(WIDTH,HEIGHT));
        setBackground(Color.black);
        addMouseListener(mouse);
        addMouseMotionListener(mouse);

        setPieces();
        copyPieces(pieces,simPieces);
    }
    //instaninate the thread
    public void LaunchGame(){
        gameThread=new Thread(this);
        gameThread.start();
    }

    //adding pieces
    public void setPieces(){

        //white team
        pieces.add(new Pawn (WHITE,0,6));
        pieces.add(new Pawn (WHITE,1,6));
        pieces.add(new Pawn (WHITE,2,6));
        pieces.add(new Pawn (WHITE,3,6));
        pieces.add(new Pawn (WHITE,4,6));
        pieces.add(new Pawn (WHITE,5,6));
        pieces.add(new Pawn (WHITE,6,6));
        pieces.add(new Pawn (WHITE,7,6));
        pieces.add(new Rook (WHITE,0,7));
        pieces.add(new Rook (WHITE,7,7));
        pieces.add(new Knight (WHITE,1,7));
        pieces.add(new Knight (WHITE,6,7));
        pieces.add(new Bishop (WHITE,2,7));
        pieces.add(new Bishop (WHITE,5,7));
        pieces.add(new Queen (WHITE,3,7));
        pieces.add(new King (WHITE,4,7));

        //black team
        pieces.add(new Pawn (BLACK,0,1));
        pieces.add(new Pawn (BLACK,1,1));
        pieces.add(new Pawn (BLACK,2,1));
        pieces.add(new Pawn (BLACK,3,1));
        pieces.add(new Pawn (BLACK,4,1));
        pieces.add(new Pawn (BLACK,5,1));
        pieces.add(new Pawn (BLACK,6,1));
        pieces.add(new Pawn (BLACK,7,1));
        pieces.add(new Rook (BLACK,0,0));
        pieces.add(new Rook (BLACK,7,0));
        pieces.add(new Knight (BLACK,1,0));
        pieces.add(new Knight (BLACK,6,0));
        pieces.add(new Bishop (BLACK,2,0));
        pieces.add(new Bishop (BLACK,5,0));
        pieces.add(new Queen (BLACK,3,0));
        pieces.add(new King (BLACK,4,0));
    }

// for simpieces
    private void copyPieces(ArrayList<Piece>source,ArrayList<Piece>target) {
        target.clear();
        for (int i = 0; i < source.size(); i++) {
            target.add(source.get(i));
        }
    }


    @Override
    //inside this we create a gameloop

    public void run() {
       //Game Loop
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime=System.nanoTime();
        long currentTime;
        while(gameThread != null){
            currentTime=System.nanoTime();
            delta += (currentTime - lastTime)/drawInterval;
            lastTime=currentTime;
            if(delta>=1){
                update();   //calling the update method
                repaint(); //calling the paint component method
                delta--;
            }
        }

    }

    //update method will handle all the updates in the game
    private void update() {

        if(promotion) {
            promoting();

        }else if(!gameover && !stalemate) {


            //check if a mouse button is pressed
            if (mouse.pressed) {
                if (activeP == null) {
                    //if the active piece is null , check if you can pick up a piece
                    for (Piece piece : simPieces) {
                        //if the mouse is on  player's piece, pick it up as the current piece
                        if (piece.color == currentColor &&
                                piece.row == mouse.y / Board.square_size &&
                                piece.col == mouse.x / Board.square_size) {
                            activeP = piece;
                        }
                    }
                }
                //if player already holding a piece
                else {
                    //if the player is holding a piece
                    //and thinking about their move
                    thinking();
                }
            }
            //center the piece when mouse button released
            if (mouse.pressed == false) {
                if (activeP != null) {

                    if (validSquare) {

                        //move confirmerd
                        //update the piece list in case a piece has been captured and remove it from the list
                        copyPieces(simPieces, pieces);
                        activeP.updatePosition(); //it update the position

                        if (castlingP != null) //update castling piece position
                        {
                            castlingP.updatePosition();
                        }

                        //check if opponent king is check aur not
                        if (isKingInCheck() && isCheckmate()) {
                            // the game is over
                            gameover = true;

                        }
                        else if(isStalemate() && isKingInCheck()==false){

                            stalemate=true;
                        }
                        else { // the game is still going on

                            //for changing a new piece in exchange of pawn piece
                            if (canPromote()) {
                                promotion = true;
                            } else {
                                changePlayer(); //change player's turn
                            }

                        }
                    } else {
                        //the move is not valid so reset everything
                        copyPieces(pieces, simPieces);
                        activeP.resetPosition();
                        activeP = null;  //when the piece is released it make current piece to null

                    }
                }

            }


        }

    }



  private void thinking(){
        canMove=false;
        validSquare=false;

        //reset the list in every loop
        //this is basically for restoring the removed pieces during the thinking
      copyPieces(pieces,simPieces);

      //reset the castling piece's position
      if(castlingP!=null)
      {
          castlingP.col=castlingP.preCol;
          castlingP.x=castlingP.getX(castlingP.col);
          castlingP=null;
      }

        //if a player  is being held a piece, update its position
      activeP.x=mouse.x-Board.half_square;
      activeP.y=mouse.y-Board.half_square;
      activeP.col=activeP.getCol(activeP.x);
      activeP.row=activeP.getRow(activeP.y);

      //check if the piece is hovering over a reachable square
      if(activeP.canMove(activeP.col,activeP.row)){
         canMove=true;

         // if hitting a piece then remove it from the list
          if(activeP.hittingP!=null){
              simPieces.remove(activeP.hittingP.getIndex());
          }
          checkCastling();

          if(isIllegal(activeP)==false && opponentCanCaptureKing()==false)
          {
              validSquare=true;
          }
      }
  }

  //method to check king's capture
    public boolean isIllegal(Piece king){

        if(king.type==Type.KING){
            for(Piece piece:simPieces) {
                if(piece != king && piece.color!=king.color && piece.canMove(king.col, king.row))
                {
                   return true;
                }
            }
        }


        return false;

    }

    //TO DETECT MOVE WHEN KING IS CHECK
    private boolean opponentCanCaptureKing()
    {
         Piece king=getKing(false);
         for(Piece piece : simPieces)
         {
             if(piece.color !=king.color && piece.canMove(king.col, king.row)){
                 return true;
             }
         }

         return false;
    }

    //method to check king is check or not
    private boolean isKingInCheck()
    {
        Piece king = getKing(true);
        if(activeP.canMove(king.col, king.row)){
            checkingP=activeP;
           return true;
    }
     else{
         checkingP=null;
        }
        return false;
    }

    private Piece getKing(boolean opponent)
    {
        Piece king = null;
        for(Piece piece : simPieces) {
            if (opponent) {
                if (piece.type == Type.KING && piece.color != currentColor) {
                    king = piece;
                }
            }
            else {
                if(piece.type==Type.KING && piece.color==currentColor)
                {
                    king=piece;
                }
            }
        }
        return king;
    }

   // three method to check for final checkmate of king

    private boolean isCheckmate() {
        Piece king = getKing(true);

        if (kingCanMove(king)) {
            return false;
        }

        else {
            // But you still have a chance
            // check if you can block  the attack  with your piece

            // first check the position of attacking piece and king is in check
            int colDiff = Math.abs(checkingP.col = king.col);
            int rowDiff = Math.abs(checkingP.row = king.row);

            // checking the attacking path
            if (colDiff == 0) {
                // the checking piece is attacking vertically
                if (checkingP.row < king.row) {
                    // the checkingP is above the king
                    for (int row = checkingP.row; row < king.row; row++) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)) {
                                return false;
                            }
                        }
                    }

                }
                if (checkingP.row > king.row) {
                    // the checkingP is below the king
                    for (int row = checkingP.row; row > king.row; row--) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(checkingP.col, row)) {
                                return false;
                            }
                        }
                    }

                }
            } else if (rowDiff == 0) {
                // the checking piece is attacking horizontally
                if (checkingP.col < king.col) {
                    // the checking piece is to the left
                    for (int col = checkingP.col; col < king.row; col++) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(col, checkingP.row)) {
                                return false;
                            }
                        }
                    }

                }
                if (checkingP.col > king.col) {
                    // the checking piece is to the right
                    for (int col = checkingP.col; col > king.row; col--) {
                        for (Piece piece : simPieces) {
                            if (piece != king && piece.color != currentColor && piece.canMove(col, checkingP.row)) {
                                return false;
                            }
                        }
                    }

                }

            } else if (colDiff == rowDiff) {
                // the checking piece is attacking diagonally

                if (checkingP.row < king.row) {
                    //the checking piece is above the king;

                    if (checkingP.col < king.col) {
                        // the checking piece is in the upper left
                        for (int col = checkingP.col, row = checkingP.row; col < king.col; col++, row++) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }

                    }
                    if (checkingP.col > king.col) {
                        // the checking piece is in the upper right
                        for (int col = checkingP.col, row = checkingP.row; col > king.col; col--, row++) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }
                }

                if (checkingP.row > king.row) {
                    // the checking piece is below the king
                    if (checkingP.col < king.col) {
                        // the checking piece is in the lower left
                        for (int col = checkingP.col, row = checkingP.row; col < king.col; col++, row--) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }

                    }
                    if (checkingP.col > king.col) {
                        //the checking piece is in the lower right
                        for (int col = checkingP.col, row = checkingP.row; col>king.col; col--, row--) {
                            for (Piece piece : simPieces) {
                                if (piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }

    return  true;
}
    private boolean kingCanMove(Piece king)
    {
        // check if king can move to any square when it is check
        if(isValidMove(king,-1,-1)) {return true;}
        if(isValidMove(king,0,-1)) {return true;}
        if(isValidMove(king,1,-1)) {return true;}
        if(isValidMove(king,-1,0)) {return true;}
        if(isValidMove(king, 1,0)) {return true;}
        if(isValidMove(king,-1,1)) {return true;}
        if(isValidMove(king, 0,1)) {return true;}
        if(isValidMove(king, 1,1)) {return true;}

      return false;
    }

    private boolean isValidMove (Piece king, int colPlus, int rowPlus)
    {
       boolean isValidMove =false;

       //update king's position for a second to check if it is safe place or not
        king.col += colPlus;
        king.row += rowPlus;
        if(king.canMove(king.col, king.row))
        {
            if(king.hittingP != null){
                simPieces.remove(king.hittingP.getIndex());
            }
            if(isIllegal(king)==false){
                isValidMove=true;
            }
        }

        // reset the king's position and restore the removed piece
        king.resetPosition();
        copyPieces(pieces, simPieces);

        return isValidMove;
    }

    private boolean isStalemate()
    {
        int count=0;
        // count the no. of pieces
        for(Piece piece : simPieces)
        {
            if(piece.color != currentColor){
                count++;
            }
        }

        //if only one piece (the king) is left
        if(count==1)
        {
            if(kingCanMove(getKing(true))==false)
            {
                return true;
            }
        }
        return false;
    }

  //method to check castling
    private void checkCastling() {
        if (castlingP != null) {
            if (castlingP.col == 0) //means rook on the left side
            {
                castlingP.col += 3;
            } else if (castlingP.col == 7) { //means rook on the right
                castlingP.col -= 2;
            }
            castlingP.x = castlingP.getX(castlingP.col);
        }
    }

        // changing player move or turn
    public void changePlayer(){
        if(currentColor==WHITE){
            currentColor=BLACK;

            //reset black's two stepped status
            for(Piece piece : pieces){
                if(piece.color==BLACK){
                    piece.twoStepped=false;
                }
            }
        }else{
            currentColor=WHITE;

            //reset White's two stepped status
            for(Piece piece : pieces){
                if(piece.color==WHITE){
                    piece.twoStepped=false;
                }
            }
        }
        activeP=null;
    }
   // creating a method for exchange pawn with new player
   private boolean canPromote(){
        if(activeP.type==Type.PAWN){
              if(currentColor==WHITE && activeP.row==0 || currentColor==BLACK && activeP.row==7){
                  promoPieces.clear();
                  promoPieces.add(new Rook(currentColor,9,2));
                  promoPieces.add(new Knight(currentColor,9,3));
                  promoPieces.add(new Bishop(currentColor,9,4));
                  promoPieces.add(new Queen(currentColor,9,5));
                  return true;
              }

        }
        return false;
   }

   private void promoting(){

        //selection of promoting piece
        if(mouse.pressed){
            for(Piece piece : promoPieces){
                if(piece.col==mouse.x/Board.square_size && piece.row == mouse.y/Board.square_size){
                    switch (piece.type){
                        case ROOK : simPieces.add(new Rook(currentColor,activeP.col,activeP.row));
                        break;
                        case KNIGHT : simPieces.add(new Knight(currentColor,activeP.col,activeP.row));
                        break;
                        case BISHOP: simPieces.add(new Bishop(currentColor,activeP.col,activeP.row));
                        break;
                        case QUEEN:  simPieces.add(new Queen(currentColor,activeP.col,activeP.row));
                        break;
                        default: break;

                    }
                    simPieces.remove(activeP.getIndex()); // remove pawn after selecting its exchange piece
                    copyPieces(simPieces,pieces);
                    activeP=null;
                    promotion=false;
                    changePlayer();
                }
            }

        }

   }

    //this method draw objects on the panel
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        //here we change the Graphics g with Graphics2d so that
        //we can call draw method of Board class in paintcomponent

        Graphics2D g2 = (Graphics2D) g;

        //board
        board.draw(g2); // calling draw method by object of Board class

        //pieces
        for (Piece p : simPieces) {
            p.draw(g2);
        }

        //change the color of the square when we position piece on it
        if (activeP != null) {
            if (canMove) {

                // change the color of square when king makes illegal movement
                // where it can be captured by opponent's piece
                if (isIllegal(activeP) || opponentCanCaptureKing()) {
                    g2.setColor(Color.red);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f)); //set the opacity
                    g2.fillRect(activeP.col * Board.square_size, activeP.row * Board.square_size, Board.square_size, Board.square_size);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)); //reset the opacity
                } else {
                    //it change the color of square white
                    g2.setColor(Color.white);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f)); //set the opacity
                    g2.fillRect(activeP.col * Board.square_size, activeP.row * Board.square_size, Board.square_size, Board.square_size);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)); //reset the opacity
                }
            }
                // draw the active piece in the end so it won't be hidden
                // by the board or the coloured square
                activeP.draw(g2);
            }

        //status message
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Book Antiqua", 100,30));
        g2.setColor(Color.white);

        // drawing promoting pieces on the side screen
        if(promotion){
            g2.drawString("Promote to : ", 600,100);
            for(Piece piece : promoPieces)
            {
                g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row),
                        Board.square_size,Board.square_size,null);

            }
        }
        else {
            if(currentColor==WHITE){
                g2.drawString("White's turn",600,410);

                //alert message for king is in check
                if(checkingP !=null && checkingP.color==BLACK)
                {
                    g2.setColor(Color.red);
                    g2.drawString("The King",620,250);
                    g2.drawString("is in check!",608,280);
                }
            }else{
                g2.drawString("Black's turn",600,180);

                //alert message for king is in check
                if(checkingP !=null && checkingP.color==WHITE)
                {
                    g2.setColor(Color.red);
                    g2.drawString("The King",620,300);
                    g2.drawString("is in check!",608,335);
                }
            }

        }
        if(gameover) {
            String s = "";
            if (currentColor == WHITE) {
                s = "White Wins the Game !!!";
            } else {
                s = "Black Wins the Game !!!";
            }
            g2.setFont(new Font("Arial", Font.PLAIN, 30));
            g2.setColor(Color.green);
            g2.drawString(s, 500, 300);
        }
       if(stalemate){
           g2.setFont(new Font("Arial", Font.PLAIN, 30));
           g2.setColor(Color.LIGHT_GRAY);
           g2.drawString("Stalemate", 500, 300);
       }


    }

}
