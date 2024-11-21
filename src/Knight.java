
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class Knight extends APiece{
    public Knight(Rectangle2D rectangle2D,Team team,int i, int j) {
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
        path.moveTo(rectangleCenterX - rectangleWidth/4, rectangleCenterY + rectangleHeight / 3);
        path.lineTo(rectangleCenterX - rectangleWidth/4, rectangleCenterY + rectangleHeight * 2 / 6);
        path.lineTo(rectangleCenterX, rectangleCenterY);
        path.lineTo(rectangleCenterX, rectangleCenterY - rectangleHeight / 8);
        path.lineTo(rectangleCenterX - rectangleWidth / 3, rectangleCenterY);
        path.lineTo(rectangleCenterX - rectangleWidth * 3 / 8, rectangleCenterY - rectangleHeight / 8);
        path.lineTo(rectangleCenterX - rectangleWidth / 16, rectangleCenterY - rectangleHeight / 4);
        path.lineTo(rectangleCenterX - rectangleWidth / 16, rectangleCenterY - rectangleHeight / 3);
        path.lineTo(rectangleCenterX, rectangleCenterY - rectangleHeight / 4);
        path.lineTo(rectangleCenterX + rectangleWidth / 12, rectangleCenterY - rectangleHeight * 7 / 24);
        path.lineTo(rectangleCenterX + rectangleWidth * 2 / 12, rectangleCenterY - rectangleHeight / 4);
        path.lineTo(rectangleCenterX + rectangleWidth / 4, rectangleCenterY - rectangleHeight / 8);
        path.lineTo(rectangleCenterX + rectangleWidth / 4, rectangleCenterY + rectangleHeight / 3);
        path.lineTo(rectangleCenterX - rectangleWidth / 4, rectangleCenterY + rectangleHeight / 3);
        path.closePath();
        this.path = path;
    }
    @Override
    public boolean isAbleToMoveHere (int i, int j, ArrayList<IPiece> initialisedPiecesList) {
        if (this.getI() - i == 2) {
            if (this.getJ() - j == -1 || this.getJ() - j == 1) {
                return true;
            }
        }
        if (this.getI() - i == -2) {
            if (this.getJ() - j == -1 || this.getJ() - j == 1) {
                return true;
            }
        }
        if (this.getI() - i == 1) {
            if (this.getJ() - j == -2 || this.getJ() - j == 2) {
                return true;
            }
        }
        if (this.getI() - i == -1) {
            if (this.getJ() - j == -2 || this.getJ() - j == 2) {
                return true;
            }
        }
        return false;
    }
}
