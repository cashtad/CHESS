import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class King extends APiece{
    public King(Rectangle2D rectangle2D, Team team,int i, int j) {
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
        path.lineTo(rectangleCenterX - rectangleWidth/4, rectangleCenterY + rectangleHeight / 4);
        path.lineTo(rectangleCenterX - rectangleWidth/6, rectangleCenterY + rectangleHeight / 4);
        path.lineTo(rectangleCenterX - rectangleWidth/12, rectangleCenterY);
        path.lineTo(rectangleCenterX - rectangleWidth/6, rectangleCenterY - rectangleHeight / 18);
        path.lineTo(rectangleCenterX - rectangleWidth/6, rectangleCenterY - rectangleHeight * 2 / 18);
        path.lineTo(rectangleCenterX - rectangleWidth/18, rectangleCenterY - rectangleHeight * 3 / 18);
        path.lineTo(rectangleCenterX - rectangleWidth/18, rectangleCenterY - rectangleHeight * 4 / 18);
        path.lineTo(rectangleCenterX - rectangleWidth/6, rectangleCenterY - rectangleHeight * 4 / 18);
        path.lineTo(rectangleCenterX - rectangleWidth/6, rectangleCenterY - rectangleHeight * 5 / 18);
        path.lineTo(rectangleCenterX - rectangleWidth/18, rectangleCenterY - rectangleHeight * 5 / 18);
        path.lineTo(rectangleCenterX - rectangleWidth/18, rectangleCenterY - rectangleHeight * 6 / 18);
        path.lineTo(rectangleCenterX + rectangleWidth/18, rectangleCenterY - rectangleHeight * 6 / 18);
        path.lineTo(rectangleCenterX + rectangleWidth/18, rectangleCenterY - rectangleHeight * 5 / 18);
        path.lineTo(rectangleCenterX + rectangleWidth/6, rectangleCenterY - rectangleHeight * 5 / 18);
        path.lineTo(rectangleCenterX + rectangleWidth/6, rectangleCenterY - rectangleHeight * 4 / 18);
        path.lineTo(rectangleCenterX + rectangleWidth/18, rectangleCenterY - rectangleHeight * 4 / 18);
        path.lineTo(rectangleCenterX + rectangleWidth/18, rectangleCenterY - rectangleHeight * 3 / 18);
        path.lineTo(rectangleCenterX + rectangleWidth/6, rectangleCenterY - rectangleHeight * 2 / 18);
        path.lineTo(rectangleCenterX + rectangleWidth/6, rectangleCenterY - rectangleHeight / 18);
        path.lineTo(rectangleCenterX + rectangleWidth/12, rectangleCenterY);
        path.lineTo(rectangleCenterX + rectangleWidth/6, rectangleCenterY + rectangleHeight / 4);
        path.lineTo(rectangleCenterX + rectangleWidth/4, rectangleCenterY + rectangleHeight / 4);
        path.lineTo(rectangleCenterX + rectangleWidth/4, rectangleCenterY + rectangleHeight / 3);
        path.lineTo(rectangleCenterX - rectangleWidth/4, rectangleCenterY + rectangleHeight / 3);
        path.closePath();
        this.path = path;
    }
    @Override
    public boolean isAbleToMoveHere (int i, int j, ArrayList<IPiece> initialisedPiecesList) {
        // rotation case
        if (isOnInitPos() && i == this.getI() && initialisedPiecesList.stream().filter(piece -> (piece.getClass().getSimpleName().equals("Rook") &&
                piece.getI() == i && piece.getJ() == j && piece.isOnInitPos())).toList().size() > 0) {
            if (this.getJ() < j) {
                for (int k = this.getJ() + 1; k < j; k++) {
                    int finalI = this.getI();
                    int finalJ = k;
                    if (initialisedPiecesList.stream().filter(piece -> piece.getI() == finalI && piece.getJ() == finalJ).toList().size() > 0) {
                        return false;
                    }
                }
            } if (this.getJ() > j) {
                for (int k = this.getJ() - 1; k > j; k--) {
                    int finalI = this.getI();
                    int finalJ = k;
                    if (initialisedPiecesList.stream().filter(piece -> piece.getI() == finalI && piece.getJ() == finalJ).toList().size() > 0) {
                        return false;
                    }
                }
            }
            return true;
        }
        else {
            if (Math.abs(this.getI() - i) <= 1 && (Math.abs(this.getJ() - j) <= 1)) {
                return true;
            }
        }
        return false;
    }
}
