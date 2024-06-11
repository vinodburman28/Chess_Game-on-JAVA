package piece;

import main.GamePanel;
import main.Type;

//for queen
public class Queen extends Piece {
    public Queen(int color, int col, int row) {
        super(color, col, row);

        type= Type.QUEEN;

        if (color == GamePanel.WHITE) {
            image = getImage("/piece/white queen");
        } else {
            image = getImage("/piece/black queen");
        }

    }

    public boolean canMove(int targetCol, int targetRow) {
        if (isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false) {

            //vertical and horizontal movement
            if (targetCol == preCol || targetRow == preRow) {
                if (isValidSquare(targetCol, targetRow) && pieceIsOnStraightLine(targetCol, targetRow)==false) {
                    return true;
                }
            }

            //diagonal movement
            if (Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow)) {
                if (isValidSquare(targetCol, targetRow) && pieceIsOnDiagonalLIne(targetCol, targetRow)==false) {
                    return true;
                }
            }

        }

        return false;
    }
}
