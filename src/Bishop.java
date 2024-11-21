import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class Bishop extends APiece{
    public Bishop(Rectangle2D rectangle2D,Team team,int i, int j) {
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
        path.moveTo(rectangleCenterX - rectangleWidth/3, rectangleCenterY + rectangleHeight / 3);
        path.lineTo(rectangleCenterX - rectangleWidth/3, rectangleCenterY + rectangleHeight / 4);
        path.curveTo(rectangleCenterX - rectangleWidth/3,rectangleCenterY + rectangleHeight / 8,rectangleCenterX - rectangleWidth / 8,rectangleCenterY + rectangleHeight / 4,rectangleCenterX - rectangleWidth / 8, rectangleCenterY + rectangleHeight / 8);
        path.curveTo(rectangleCenterX - rectangleWidth / 3,rectangleCenterY,rectangleCenterX - rectangleWidth / 8,rectangleCenterY - rectangleHeight / 8,rectangleCenterX,rectangleCenterY - rectangleHeight/4);
        path.curveTo(rectangleCenterX - rectangleWidth / 12,rectangleCenterY - rectangleHeight *7 / 24,rectangleCenterX - rectangleWidth / 12,rectangleCenterY - rectangleHeight *7 / 24,rectangleCenterX,rectangleCenterY - rectangleHeight / 3);
        path.curveTo(rectangleCenterX + rectangleWidth / 12,rectangleCenterY - rectangleHeight *7 / 24,rectangleCenterX + rectangleWidth / 12,rectangleCenterY - rectangleHeight *7 / 24,rectangleCenterX,rectangleCenterY - rectangleHeight/4);
        path.curveTo(rectangleCenterX + rectangleWidth / 8,rectangleCenterY - rectangleHeight / 8,rectangleCenterX + rectangleWidth / 3,rectangleCenterY,rectangleCenterX + rectangleWidth / 8, rectangleCenterY + rectangleHeight / 8);
        path.curveTo(rectangleCenterX + rectangleWidth / 8,rectangleCenterY + rectangleHeight / 4,rectangleCenterX + rectangleWidth/3,rectangleCenterY + rectangleHeight / 8,rectangleCenterX + rectangleWidth/3, rectangleCenterY + rectangleHeight / 4);
        path.lineTo(rectangleCenterX + rectangleWidth/3, rectangleCenterY + rectangleHeight / 3);
        path.lineTo(rectangleCenterX - rectangleWidth/3, rectangleCenterY + rectangleHeight / 3);
        path.closePath();
        this.path = path;
    }
    @Override
    public boolean isAbleToMoveHere (int i, int j, ArrayList<IPiece> initialisedPiecesList) {
        if (Math.abs(this.getI() - i) == Math.abs(this.getJ() - j)) {
            if (this.getI() < i) {
                if (this.getJ() < j) {
                    int count = 1;
                    while (this.getI() + count < i) {
                        int finalI = this.getI() + count;
                        int finalJ = this.getJ() + count;
                        if (initialisedPiecesList.stream().filter(piece -> piece.getI() == finalI && piece.getJ() == finalJ).toList().size() > 0) {
                            return false;
                        }
                        count++;
                    }
                }
                else {
                    int count = 1;
                    while (this.getI() + count < i) {
                        int finalI = this.getI() + count;
                        int finalJ = this.getJ() - count;
                        if (initialisedPiecesList.stream().filter(piece -> piece.getI() == finalI && piece.getJ() == finalJ).toList().size() > 0) {
                            return false;
                        }
                        count++;
                    }
                }
            }
            else {
                if (this.getJ() < j) {
                    int count = 1;
                    while (this.getI() - count > i) {
                        int finalI = this.getI() - count;
                        int finalJ = this.getJ() + count;
                        if (initialisedPiecesList.stream().filter(piece -> piece.getI() == finalI && piece.getJ() == finalJ).toList().size() > 0) {
                            return false;
                        }
                        count++;
                    }
                }
                else {
                    int count = 1;
                    while (this.getI() - count > i) {
                        int finalI = this.getI() - count;
                        int finalJ = this.getJ() - count;
                        if (initialisedPiecesList.stream().filter(piece -> piece.getI() == finalI && piece.getJ() == finalJ).toList().size() > 0) {
                            return false;
                        }
                        count++;
                    }
                }
            }
            return true;
        }
        return false;
    }
}
