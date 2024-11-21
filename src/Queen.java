import org.w3c.dom.ls.LSOutput;

import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class Queen extends APiece{
    public Queen(Rectangle2D rectangle2D, Team team,int i, int j) {
        setPath(rectangle2D);
        this.rectangle = rectangle2D;
        this.team = team;
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
        path.lineTo(rectangleCenterX - rectangleWidth/6, rectangleCenterY - rectangleHeight / 24);
        path.lineTo(rectangleCenterX - rectangleWidth/12, rectangleCenterY - rectangleHeight * 2 / 24);
        path.lineTo(rectangleCenterX - rectangleWidth * 3 / 24, rectangleCenterY - rectangleHeight / 3);
        path.lineTo(rectangleCenterX - rectangleWidth * 2 / 24, rectangleCenterY - rectangleHeight * 5 / 18);
        path.lineTo(rectangleCenterX - rectangleWidth * 1 / 24, rectangleCenterY - rectangleHeight / 3);
        path.lineTo(rectangleCenterX, rectangleCenterY - rectangleHeight * 5 / 18);
        path.lineTo(rectangleCenterX + rectangleWidth * 1 / 24, rectangleCenterY - rectangleHeight / 3);
        path.lineTo(rectangleCenterX + rectangleWidth * 2 / 24, rectangleCenterY - rectangleHeight * 5 / 18);
        path.lineTo(rectangleCenterX + rectangleWidth * 3 / 24, rectangleCenterY - rectangleHeight / 3);
        path.lineTo(rectangleCenterX + rectangleWidth/12, rectangleCenterY - rectangleHeight * 2 / 24);
        path.lineTo(rectangleCenterX + rectangleWidth/6, rectangleCenterY - rectangleHeight / 24);
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
        //if queen doing move as ROOK
        if (this.getI() == i) {
            if (this.getJ() < j) {
                for (int k = this.getJ() + 1; k < j; k++) {
                    int finalI = this.getI();
                    int finalJ = k;
                    if (initialisedPiecesList.stream().filter(piece -> piece.getI() == finalI && piece.getJ() == finalJ).toList().size() > 0) {
                        return false;
                    }
                }
                return true;

            }
            if (this.getJ() > j) {
                for (int k = this.getJ() - 1; k > j; k--) {
                    int finalI = this.getI();
                    int finalJ = k;
                    if (initialisedPiecesList.stream().filter(piece -> piece.getI() == finalI && piece.getJ() == finalJ).toList().size() > 0) {
                        return false;
                    }
                }
                return true;

            }
        }
        if (this.getJ() == j) {
            if (this.getI() < i) {
                for (int m = this.getI() + 1; m < i; m++) {
                    int finalJ = this.getJ();
                    int finalI = m;
                    if (initialisedPiecesList.stream().filter(piece -> piece.getI() == finalI && piece.getJ() == finalJ).toList().size() > 0) {
                        return false;
                    }
                }
                return true;
            }
            if (this.getI() > i) {
                for (int m = this.getI() - 1; m > i; m--) {
                    int finalJ = this.getJ();
                    int finalI = m;
                    if (initialisedPiecesList.stream().filter(piece -> piece.getI() == finalI && piece.getJ() == finalJ).toList().size() > 0) {
                        return false;
                    }
                }
                return true;
            }
        }
        //if queen doing move as BISHOP
        if (Math.abs(this.getI() - i) == Math.abs(this.getJ() - j)) {
            if (this.getI() < i) {
                if (this.getJ() < j) {
                    int count = 1;
                    while (this.getI() + count < i) {
                        int finalI = this.getI() + count;
                        int finalJ = this.getJ() + count;
                        count++;
                        if (initialisedPiecesList.stream().filter(piece -> piece.getI() == finalI && piece.getJ() == finalJ).toList().size() > 0) {
                            return false;
                        }
                    }
                    return true;
                }
                if (this.getJ() > j) {
                    int count = 1;
                    while (this.getI() + count < i) {
                        int finalI = this.getI() + count;
                        int finalJ = this.getJ() - count;
                        count++;
                        if (initialisedPiecesList.stream().filter(piece -> piece.getI() == finalI && piece.getJ() == finalJ).toList().size() > 0) {
                            return false;
                        }
                    }
                    return true;

                }
            }
            if (this.getI() > i) {
                if (this.getJ() < j) {
                    int count = 1;
                    while (this.getI() - count > i) {
                        int finalI = this.getI() - count;
                        int finalJ = this.getJ() + count;
                        count++;
                        if (initialisedPiecesList.stream().filter(piece -> piece.getI() == finalI && piece.getJ() == finalJ).toList().size() > 0) {
                            return false;
                        }
                    }
                    return true;

                }
                if (this.getJ() > j) {
                    int count = 1;
                    while (this.getI() - count > i) {
                        int finalI = this.getI() - count;
                        int finalJ = this.getJ() - count;
                        count++;
                        if (initialisedPiecesList.stream().filter(piece -> piece.getI() == finalI && piece.getJ() == finalJ).toList().size() > 0) {
                            return false;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

}
