import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public interface IPiece {
    /**
     * Draws a piece on their current position and fills it with color of their team
     * @param g
     */
    void drawPiece(Graphics g);
    Path2D getPath();

    /**
     * Set path is used to scale a piece's size and position
     * @param rectangle2D cell of chess board
     */
    void setPath(Rectangle2D rectangle2D);
    int getI();

    void setI(int i);

    int getJ();

    void setJ(int j);
    boolean isAbleToMoveHere (int i, int j, ArrayList<IPiece> initialisedPiecesList);
    void makeMove(int i, int j);
    Team getTeam();
    int getInitI();
    int getInitJ();
    boolean isOnInitPos();
    int getLastI();
    int getLastJ();
}
