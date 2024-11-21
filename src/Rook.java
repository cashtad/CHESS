import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class Rook extends APiece{

    public Rook(Rectangle2D rectangle2D,Team team,int i, int j) {
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
    public void setPath (Rectangle2D rectangle2D) {
        Path2D path = new Path2D.Double();
        double rectangleCenterX = rectangle2D.getCenterX();
        double rectangleWidth = rectangle2D.getWidth();
        double rectangleCenterY = rectangle2D.getCenterY();
        double rectangleHeight = rectangle2D.getHeight();
        path.moveTo(rectangleCenterX - rectangleWidth / 3, rectangleCenterY + rectangleHeight / 3);
        path.lineTo(rectangleCenterX - rectangleWidth / 3, rectangleCenterY + rectangleHeight * 2 / 12);
        path.lineTo(rectangleCenterX - rectangleWidth / 6,rectangleCenterY + rectangleHeight * 2 / 12);
        path.lineTo(rectangleCenterX - rectangleWidth / 8, rectangleCenterY - rectangleHeight / 6);
        path.lineTo(rectangleCenterX - rectangleWidth / 4, rectangleCenterY - rectangleHeight / 6);
        path.lineTo(rectangleCenterX - rectangleWidth / 4, rectangleCenterY - rectangleHeight / 3);
        path.lineTo(rectangleCenterX - rectangleWidth * 2 /12, rectangleCenterY - rectangleHeight / 3);
        path.lineTo(rectangleCenterX - rectangleWidth * 2 /12, rectangleCenterY - rectangleHeight * 7 / 24);
        path.lineTo(rectangleCenterX - rectangleWidth * 1 /12, rectangleCenterY - rectangleHeight * 7 / 24);
        path.lineTo(rectangleCenterX - rectangleWidth * 1 /12, rectangleCenterY - rectangleHeight / 3);
        path.lineTo(rectangleCenterX + rectangleWidth * 1 /12, rectangleCenterY - rectangleHeight / 3);
        path.lineTo(rectangleCenterX + rectangleWidth * 1 /12, rectangleCenterY - rectangleHeight * 7 / 24);
        path.lineTo(rectangleCenterX + rectangleWidth * 2 /12, rectangleCenterY - rectangleHeight * 7 / 24);
        path.lineTo(rectangleCenterX + rectangleWidth * 2 /12, rectangleCenterY - rectangleHeight / 3);
        path.lineTo(rectangleCenterX + rectangleWidth / 4, rectangleCenterY - rectangleHeight / 3);
        path.lineTo(rectangleCenterX + rectangleWidth / 4, rectangleCenterY - rectangleHeight / 6);
        path.lineTo(rectangleCenterX + rectangleWidth / 8, rectangleCenterY - rectangleHeight / 6);
        path.lineTo(rectangleCenterX + rectangleWidth / 6,rectangleCenterY + rectangleHeight * 2 / 12);
        path.lineTo(rectangleCenterX + rectangleWidth / 3, rectangleCenterY + rectangleHeight * 2 / 12);
        path.lineTo(rectangleCenterX + rectangleWidth / 3, rectangleCenterY + rectangleHeight / 3);
        path.lineTo(rectangleCenterX - rectangleWidth / 3, rectangleCenterY + rectangleHeight / 3);
        path.closePath();
        this.path = path;
    }
    @Override
    public boolean isAbleToMoveHere (int i, int j, ArrayList<IPiece> initialisedPiecesList) {
        if (this.getI() == i) {
            int finalI = this.getI();
            if (this.getJ() < j) {
                for (int k = this.getJ() + 1; k < j; k++) {
                    int finalJ = k;
                    if (initialisedPiecesList.stream().filter(piece -> piece.getI() == finalI && piece.getJ() == finalJ).toList().size() > 0) {
                        return false;
                    }
                }
                return true;
            }
            if (this.getJ() > j) {
                for (int k = this.getJ() - 1; k > j; k--) {
                    int finalJ = k;
                    if (initialisedPiecesList.stream().filter(piece -> piece.getI() == finalI && piece.getJ() == finalJ).toList().size() > 0) {
                        return false;
                    }
                }
                return true;
            }
        }
        if (this.getJ() == j) {
            int finalJ = this.getJ();
            if (this.getI() < i) {
                for (int m = this.getI() + 1; m < i; m++) {
                    int finalI = m;
                    if (initialisedPiecesList.stream().filter(piece -> piece.getI() == finalI && piece.getJ() == finalJ).toList().size() > 0) {
                        return false;
                    }
                }
                return true;
            }
            if (this.getI() > i) {
                for (int m = this.getI() - 1; m > i; m--) {
                    int finalI = m;
                    if (initialisedPiecesList.stream().filter(piece -> piece.getI() == finalI && piece.getJ() == finalJ).toList().size() > 0) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
