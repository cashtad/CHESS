
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class Pawn extends APiece{


    public Pawn(Rectangle2D rectangle2D,Team team,int i, int j) {
        this.team = team;
        setPath(rectangle2D);
        this.rectangle = rectangle2D;
        this.i = i;
        this.j = j;
        initI = i;
        initJ = j;
        lastI = this.i;
        lastJ = this.j;
    }
    @Override
    public void setPath(Rectangle2D rectangle2D) {
        Path2D path = new Path2D.Double();
        double rectangleCenterX = rectangle2D.getCenterX();
        double rectangleWidth = rectangle2D.getWidth();
        double rectangleCenterY = rectangle2D.getCenterY();
        double rectangleHeight = rectangle2D.getHeight();
        path.moveTo(rectangleCenterX - rectangleWidth / 4, rectangleCenterY + rectangleHeight / 3);
        path.curveTo(rectangleCenterX - rectangleWidth * 3/ 8, rectangleCenterY + rectangleHeight / 4, rectangleCenterX - rectangleWidth * 3 / 10, rectangleCenterY, rectangleCenterX - rectangleWidth / 6, rectangleCenterY);
        path.curveTo(rectangleCenterX - rectangleWidth * 1 / 4, rectangleCenterY - rectangleHeight / 16, rectangleCenterX - rectangleWidth / 8, rectangleCenterY - rectangleHeight * 2/ 8, rectangleCenterX - rectangleWidth / 12, rectangleCenterY - rectangleHeight / 4);
        path.curveTo(rectangleCenterX - rectangleWidth / 16, rectangleCenterY - rectangleHeight / 3, rectangleCenterX - rectangleWidth / 16, rectangleCenterY - rectangleHeight / 3, rectangleCenterX, rectangleCenterY - rectangleHeight / 3);
        path.curveTo(rectangleCenterX + rectangleWidth / 16, rectangleCenterY - rectangleHeight / 3, rectangleCenterX + rectangleWidth / 16, rectangleCenterY - rectangleHeight / 3, rectangleCenterX + rectangleWidth / 12, rectangleCenterY - rectangleHeight / 4);
        path.curveTo(rectangleCenterX + rectangleWidth / 8, rectangleCenterY - rectangleHeight * 2/ 8, rectangleCenterX + rectangleWidth * 1 / 4, rectangleCenterY - rectangleHeight / 16, rectangleCenterX + rectangleWidth / 6, rectangleCenterY);
        path.curveTo(rectangleCenterX + rectangleWidth * 3 / 10, rectangleCenterY, rectangleCenterX + rectangleWidth * 3/ 8, rectangleCenterY + rectangleHeight / 4, rectangleCenterX + rectangleWidth / 4, rectangleCenterY + rectangleHeight / 3);
        path.closePath();
        this.path = path;
    }
    @Override
    public boolean isAbleToMoveHere (int i, int j, ArrayList<IPiece> initialisedPiecesList) {
        // if hitting
        if (initialisedPiecesList.stream().filter(piece -> piece.getI() == i && piece.getJ() == j).toList().size() > 0) {
            if (this.getTeam() == Team.BLACK) {
                if (this.getI() - i == -1) {
                    if (this.getJ() - j == -1 || this.getJ() - j == 1) {
                        return true;
                    }
                }
            }
            if (this.getTeam() == Team.WHITE) {
                if (this.getI() - i == 1) {
                    if (this.getJ() - j == -1 || this.getJ() - j == 1) {
                        return true;
                    }
                }
            }
            return false;
            //moving
        } else {
            // if on init pos is able to move on 1 or 2 cells
            if (isOnInitPos) {
                if (this.team == Team.BLACK) {
                    //move on 1 cell forward or 2 cells forward and no barrier on the way
                    if ((i - this.getI() == 1 && j == this.getJ())
                        || (i - this.getI() == 2 && j == this.getJ()
                        && initialisedPiecesList.stream().filter(piece -> piece.getI() == i - 1 && piece.getJ() == j).toList().size() == 0)) {
                            return true;
                    }
                } else {
                    if ((i - this.getI() == -1 && j == this.getJ())
                        || (i - this.getI() == -2 && j == this.getJ()
                        && initialisedPiecesList.stream().filter(piece -> piece.getI() == i + 1 && piece.getJ() == j).toList().size() == 0)) {
                        return true;
                    }
                }
                // else can move only on 1 cell
            } else {
                if (this.team == Team.BLACK) {
                    if (i - this.getI() == 1 && j == this.getJ()) {
                        return true;
                    }
                } else {
                    if (i - this.getI() == -1 && j == this.getJ()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
