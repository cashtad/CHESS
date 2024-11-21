import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Class represent abstract chess piece, which has owns rectangle, path which is created by rectangle, i and j position
 * on board, and team (Black or White)
 */
public class APiece implements IPiece{
    /**
     * Path is a shape of figure, which size is controlled of current size of chess board cell.
     */
    protected Path2D path;
    /**
     * Rectangle is a cell of chess board.
     */
    protected Rectangle2D rectangle;
    /**
     * Team is needed to fill the piece with right color (Black or White)
     */
    protected Team team;
    /**
     * I position of rectangle in array of chess board cells
     */
    protected int i;
    /**
     * J position of rectangle in array of chess board cells
     */
    protected int j;
    protected int initI;
    protected int initJ;
    protected int lastI;
    protected int lastJ;
    protected boolean isOnInitPos = true;

    @Override
    public String toString() {
        return this.getClass().getSimpleName() +
                " team=" + team +
                ", i=" + i +
                ", j=" + j +
                ", lastI=" + lastI +
                ", lastJ=" + lastJ;
    }

    @Override
    public void drawPiece(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setStroke(new BasicStroke(3));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        if (team == Team.BLACK) {
            g2.setColor(Color.BLACK);
            g2.fill(path);
        } else {
            g2.setColor(Color.WHITE);
            g2.fill(path);
        }
        g2.setColor(Color.BLACK);
        g2.draw(path);
    }
    @Override
    public Path2D getPath() {
        return path;
    }
    @Override
    public int getI() {
        return i;
    }
    @Override
    public void setI(int i) {
        this.i = i;
    }
    @Override
    public int getJ() {
        return j;
    }
    @Override
    public void setJ(int j) {
        this.j = j;
    }

    @Override
    public boolean isAbleToMoveHere(int i, int j, ArrayList<IPiece> initialisedPiecesList) {
        return false;
    }

    @Override
    public Team getTeam() {
        return this.team;
    }

    @Override
    public int getInitI() {
        return initI;
    }

    @Override
    public int getInitJ() {
        return initJ;
    }

    @Override
    public boolean isOnInitPos() {
        return isOnInitPos;
    }
    @Override
    public void makeMove(int i, int j) {
        lastI = this.i;
        lastJ = this.j;
        setI(i);
        setJ(j);
        isOnInitPos = false;
    }

    @Override
    public int getLastI() {
        return lastI;
    }

    @Override
    public int getLastJ() {
        return lastJ;
    }

    @Override
    public void setPath(Rectangle2D rectangle2D) {
    }
}
