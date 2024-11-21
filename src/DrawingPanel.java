import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

/** Class DrawingPanel represents panel, which draws on itself chess board, chess pieces and
 * let user manipulate with them.
 * @author Leonid Malakhov
 * @version 1.8.0
 */
public class DrawingPanel extends JPanel {

    /**
     * This list contains every piece, that is active (should be drawn and is not "dead")
     */
    private final ArrayList<IPiece> initialisedPiecesList = new ArrayList<>(32);

    /**
     * This list contains every cell of chess board, A1-H8. And is used in Drag&Drop feature
     */
    private final ArrayList<Rectangle2D> cellsOfBoardList = new ArrayList<>(64);

    /**
     * This array contains every cell of chess board, A1-H8. And is used in coordinates of pieces
     */
    private final Rectangle2D[][] cellOfBoardArray = new Rectangle2D[8][8];

    /**
     * Boolean used in method paintComponent, to avoid infinite amount of pieces on board
     */
    private boolean arePiecesGenerated = false;

    /**
     * Timer for animation purposes
     */
    Timer timer;

    /**
     * Boolean representing mate
     */
    public boolean mate = false;

    /**
     * Boolean representing pate
     */
    public boolean pate = false;
    private long start = System.currentTimeMillis();

    /**
     * Which player can make a turn now
     */
    private Team turnSide = Team.WHITE;

    /**
     * Count of turns
     */
    private int turnCount = 1;

    /**
     * Boolean representing check
     */
    private boolean check = false;

    /**
     * This is the position which is used for drawing last turn cells
     */
    private int lastMoveISrc;

    /**
     * This is the position which is used for drawing last turn cells
     */
    private int lastMoveJSrc;
    /**
     * This is the position which is used for drawing last turn cells
     */
    private int lastMoveIRslt;
    /**
     * This is the position which is used for drawing last turn cells
     */
    private int lastMoveJRslt;
    /**
     * Piece that will be deleted during en passant move
     */
    IPiece enpassantPiece;
    /**
     * Turn of piece-candidate for en passant move
     */
    int enpassantTurn;
    /**
     * Is toggled AI (Black side)
     */
    public boolean isToggledAi = false;
    /**
     * Array for graph
     */
    private final ArrayList<Number> timeOfWhiteTurns = new ArrayList<Number>(50);
    /**
     * Array for graph
     */
    private final ArrayList<Number> timeOfBlackTurns = new ArrayList<Number>(50);
    /**
     * Array for graph
     */
    private final ArrayList<Integer> countOfWhiteTurns = new ArrayList<Integer>(50);
    /**
     * Array for graph
     */
    private final ArrayList<Integer> countOfBlackTurns = new ArrayList<Integer>(50);

    /**
     * Boolean representing status of animation (on/off)
     */
    boolean animation = false;

    /**
     * I-coordinate of drop-cell
     */
    int iResult;

    /**
     * J-coordinate of drop-cell
     */
    int jResult;

    /**
     * Captured piece, which user is going to drag&drop
     */
    IPiece capturedPiece;

    public DrawingPanel() {
        this.addMouseListener(new MouseListener() {
            /**
             * Square to drop a captured piece
             */
            Rectangle2D squareToDrop;

            /**
             * If squareToDrop contains a piece, the piece will be saved in this variable
             */
            IPiece pieceToBeChanged;

            /**
             * If any piece was captured - true, else - false.
             */
            boolean wasPieceCaptured = false;

            @Override
            public void mouseClicked(MouseEvent e) {
            }
            @Override
            public void mousePressed(MouseEvent e) {
                //searching for piece
                initialisedPiecesList.stream()
                        .filter(piece -> piece.getPath().contains(e.getPoint())).forEach(piece -> capturedPiece = piece);
                if (capturedPiece != null && capturedPiece.getTeam() == turnSide) {
                    if (animation) {
                        animation = false;
                        timer.stop();
                    }
                    wasPieceCaptured = true;
                } else {
                    wasPieceCaptured = false;
                }
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {

                // if any piece was captured, it will save the cell, where mouse where realised
                if (wasPieceCaptured) {
                    cellsOfBoardList.stream()
                            .filter(rectangle -> rectangle.getBounds2D().contains(e.getPoint()))
                            .forEach(rectangle -> squareToDrop = rectangle);
                }
                // if input is OK
                if (wasPieceCaptured && squareToDrop != null) {
                    // finding coordinates of square && searching piece with this coords
                    for (int i = 0; i < 8; i++) {
                        for (int j = 0; j < 8; j++) {
                            if (cellOfBoardArray[i][j].contains(e.getPoint())) {
                                iResult = i;
                                jResult = j;
                                pieceToBeChanged = pieceWithCords(i,j,initialisedPiecesList);
                            }
                        }
                    }

                    //moving
                    if (pieceToBeChanged == null && capturedPiece.isAbleToMoveHere(iResult, jResult, initialisedPiecesList)) {
                        if (!isCheckOnSameSideAfterTurn(capturedPiece,iResult,jResult,initialisedPiecesList)) {
                            check = false;
                            animatedMove(capturedPiece,iResult,jResult);

                            capturedPiece.makeMove(iResult, jResult);
                            //promotion and enpassant cases
                            if (capturedPiece.getClass().getSimpleName().equals("Pawn")) {
                                if (promotion(capturedPiece)) {
                                    if (isCheckOnOppositeSideAfterTurn(turnSide,initialisedPiecesList)) {
                                        check = true;
                                        System.out.println("Check.");
                                    }
                                }
                                if (Math.abs(capturedPiece.getI() - capturedPiece.getInitI()) == 2) {
                                    enpassantPiece = capturedPiece;
                                    enpassantTurn = turnCount;
                                }
                            }
                            if (isCheckOnOppositeSideAfterTurn(turnSide,initialisedPiecesList)) {
                                check = true;
                                System.out.println("Check.");
                            }
                            makeATurn();
                        }
                    }
                    else

                    // En passant
                    if (pieceToBeChanged == null && capturedPiece.getClass().getSimpleName().equals("Pawn") && enpassantPiece != null && turnCount - enpassantTurn  == 1 && enpassantPiece.getI() == capturedPiece.getI() &&
                            (enpassantPiece.getJ() == capturedPiece.getJ() - 1 || enpassantPiece.getJ() == capturedPiece.getJ() + 1)) {
                        if (capturedPiece.getTeam() == Team.WHITE) {
                            if ((iResult == capturedPiece.getI() - 1) && jResult == enpassantPiece.getJ()) {
                                initialisedPiecesList.remove(enpassantPiece);
                                if (!isCheckOnSameSideAfterTurn(capturedPiece,iResult,jResult,initialisedPiecesList)) {
                                    check = false;
                                    animatedMove(capturedPiece,iResult,jResult);
                                    capturedPiece.makeMove(iResult,jResult);
                                    if (isCheckOnOppositeSideAfterTurn(turnSide,initialisedPiecesList)) {
                                        check = true;
                                        System.out.println("Check.");
                                    }
                                    makeATurn();
                                    enpassantPiece = null;
                                    enpassantTurn = 0;
                                } else {
                                    initialisedPiecesList.add(enpassantPiece);
                                }
                            }
                        } else {
                            if ((iResult == capturedPiece.getI() + 1) && jResult == enpassantPiece.getJ()) {
                                initialisedPiecesList.remove(enpassantPiece);
                                if (!isCheckOnSameSideAfterTurn(capturedPiece,iResult,jResult,initialisedPiecesList)) {
                                    animatedMove(capturedPiece,iResult,jResult);
                                    capturedPiece.makeMove(iResult,jResult);
                                    check = false;
                                    if (isCheckOnOppositeSideAfterTurn(turnSide,initialisedPiecesList)) {
                                        check = true;
                                        System.out.println("Check.");
                                    }
                                    makeATurn();
                                    enpassantPiece = null;
                                    enpassantTurn = 0;
                                } else {
                                    initialisedPiecesList.add(enpassantPiece);
                                }
                            }
                        }
                    }
                    else

                    //rotation
                    if(pieceToBeChanged != null
                            && capturedPiece.getClass().getSimpleName().equals("King")
                            && pieceToBeChanged.getClass().getSimpleName().equals("Rook")
                            && capturedPiece.getTeam() == pieceToBeChanged.getTeam()
                            && capturedPiece.isOnInitPos() && pieceToBeChanged.isOnInitPos()
                            && capturedPiece.isAbleToMoveHere(iResult,jResult,initialisedPiecesList)
                            && !check) {
                        int jCapPPrev = capturedPiece.getJ();
                        int jpTBC = pieceToBeChanged.getJ();
                        if (pieceToBeChanged.getJ() == 0) {
                            pieceToBeChanged.setJ(3);
                            iResult = capturedPiece.getI();
                            jResult = 2;
                            if (!isCheckOnSameSideAfterTurn(capturedPiece,iResult,jResult,initialisedPiecesList)) {
                                animatedMove(capturedPiece, capturedPiece.getI(), 2);
                                capturedPiece.makeMove(capturedPiece.getI(),2);
                                pieceToBeChanged.makeMove(pieceToBeChanged.getI(), 3);
                                check = false;

                                if (isCheckOnOppositeSideAfterTurn(turnSide,initialisedPiecesList)) {
                                    check = true;
                                    System.out.println("Check.");
                                }
                                makeATurn();
                            } else {
                                capturedPiece.setJ(jCapPPrev);
                                pieceToBeChanged.setJ(jpTBC);
                            }
                        }
                        if (pieceToBeChanged.getJ() == 7) {
                            pieceToBeChanged.setJ(5);
                            iResult = capturedPiece.getI();
                            jResult = 6;
                            if (!isCheckOnSameSideAfterTurn(capturedPiece,iResult,jResult,initialisedPiecesList)) {
                                animatedMove(capturedPiece,capturedPiece.getI(),6);
                                capturedPiece.makeMove(capturedPiece.getI(),6);
                                pieceToBeChanged.makeMove(pieceToBeChanged.getI(), 5);
                                check = false;
                                if (isCheckOnOppositeSideAfterTurn(turnSide,initialisedPiecesList)) {
                                    check = true;
                                    System.out.println("Check.");
                                }
                                makeATurn();
                            } else {
                                capturedPiece.setJ(jCapPPrev);
                                pieceToBeChanged.setJ(jpTBC);
                            }
                        }
                    }
                    else

                    //hitting
                    if (pieceToBeChanged != null && capturedPiece.isAbleToMoveHere(iResult, jResult, initialisedPiecesList) && capturedPiece.getTeam() != pieceToBeChanged.getTeam()) {
                        if (!isCheckOnSameSideAfterTurn(capturedPiece,iResult,jResult,initialisedPiecesList)) {
                            initialisedPiecesList.remove(pieceToBeChanged);
                            animatedMove(capturedPiece,iResult,jResult);
                            capturedPiece.makeMove(iResult,jResult);
                            check = false;
                            // promotion case
                            if (capturedPiece.getClass().getSimpleName().equals("Pawn")) {
                                promotion(capturedPiece);
                            }
                            if (isCheckOnOppositeSideAfterTurn(turnSide, initialisedPiecesList)) {
                                check = true;
                                System.out.println("Check.");
                            }
                            makeATurn();
                        }
                    }
                    wasPieceCaptured = false;
                    squareToDrop = null;
                    pieceToBeChanged = null;
                    if (!animation){
                        capturedPiece = null;
                    }
                    repaint();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }
            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }
    /**
     * Promotes a pawn to queen
     * @param capturedPiece
     * @return
     */
    boolean promotion(IPiece capturedPiece) {
        if (capturedPiece.getI() == 0 || capturedPiece.getI() == 7) {
            initialisedPiecesList.remove(capturedPiece);
            Queen queenP = new Queen(cellOfBoardArray[capturedPiece.getI()][capturedPiece.getJ()],capturedPiece.getTeam(), capturedPiece.getI(), capturedPiece.getJ());
            initialisedPiecesList.add(queenP);
            return true;
        }
        return false;
    }

    boolean isCheckOnSameSideAfterTurn(IPiece capturedPiece, int iResult, int jResult, ArrayList<IPiece> initialisedPiecesList) {
        IPiece king = initialisedPiecesList.stream().filter(piece -> piece.getClass().getSimpleName().equals("King") && piece.getTeam() == capturedPiece.getTeam()).toList().get(0);
        IPiece pieceToBeChanged = pieceWithCords(iResult,jResult,initialisedPiecesList);
        if (pieceToBeChanged != null && pieceToBeChanged.getTeam() != capturedPiece.getTeam()) {
            initialisedPiecesList.remove(pieceToBeChanged);
        }
        int iPrev = capturedPiece.getI();
        int jPrev = capturedPiece.getJ();
        capturedPiece.setI(iResult);
        capturedPiece.setJ(jResult);
        for (int i = 0; i < initialisedPiecesList.size(); i++) {
            IPiece piece = initialisedPiecesList.get(i);
            if (piece.getTeam() != capturedPiece.getTeam()) {
                if (piece.isAbleToMoveHere(king.getI(), king.getJ(), initialisedPiecesList)) {
                    capturedPiece.setI(iPrev);
                    capturedPiece.setJ(jPrev);
                    if (pieceToBeChanged != null) {
                        initialisedPiecesList.add(pieceToBeChanged);
                    }
                    return true;
                }
            }
        }
        capturedPiece.setI(iPrev);
        capturedPiece.setJ(jPrev);
        if (pieceToBeChanged != null) {
            initialisedPiecesList.add(pieceToBeChanged);
        }
        return false;
    }

    /**
     * Checks if there is a check on opposite side after turn/
     * @param turnSide
     * @param initialisedPiecesList
     * @return
     */
    boolean isCheckOnOppositeSideAfterTurn(Team turnSide, ArrayList<IPiece> initialisedPiecesList) {
        IPiece kingEnemy = initialisedPiecesList.stream().filter(piece -> piece.getClass().getSimpleName().equals("King") && piece.getTeam() != turnSide).toList().get(0);
        for (int i = 0; i < initialisedPiecesList.size(); i++) {
            IPiece piece = initialisedPiecesList.get(i);
            if (piece.getTeam() == turnSide) {
                if (piece.isAbleToMoveHere(kingEnemy.getI(), kingEnemy.getJ(), initialisedPiecesList)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Method is used by paintComponent to draw a chess board. Chess board contains 64 Rectangle2D, 32 are white and
     * 32 are transparent (Black color is represented by lines). Background of board is white. Every chess cell is putted
     * in List and Array. Chess board is always in the middle of drawing panel.
     *
     * @param g graphics
     */
    public void drawBoard(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        int lines = 8;
        int columns = 8;
        setBackground(Color.WHITE);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(1));
        double border = Math.min(this.getHeight(), this.getWidth());
        double tabWidth = (double)(this.getWidth() - this.getHeight()) / 2;
        if (tabWidth < 0) {
            tabWidth = 0;
        }
        double tabHeight = (double)(this.getHeight() - this.getWidth()) / 2;
        if (tabHeight < 0) {
            tabHeight = 0;
        }
        Rectangle2D background = new Rectangle2D.Double(tabWidth, tabHeight, border, border);
        g2.setClip(background);
        double stepLines = border / 80;
        double yCoordinateOfLineStart = tabHeight;
        double xCoordinateOfLineEnd = tabWidth;
        while ( Math.min(yCoordinateOfLineStart,xCoordinateOfLineEnd) < border * 2) {
            yCoordinateOfLineStart += stepLines;
            xCoordinateOfLineEnd += stepLines;
            Line2D line = new Line2D.Double(tabWidth,yCoordinateOfLineStart,xCoordinateOfLineEnd, tabHeight);
            g2.draw(line);
        }
        double stepXCells = border / 8;
        double stepYCells = border / 8;
        cellsOfBoardList.clear();
        for (int i = 0; i < lines; i++) {
            for (int j = 0; j < columns; j++) {
                cellOfBoardArray[i][j] = new Rectangle2D.Double(j*stepXCells + tabWidth,i*stepYCells + tabHeight,stepXCells,stepYCells);
                cellsOfBoardList.add(cellOfBoardArray[i][j]);
                if (i % 2 == 0) {
                    if (j % 2 ==0){
                        g2.setColor(Color.WHITE);
                        g2.fill(cellOfBoardArray[i][j]);
                    }
                } else {
                    if (j % 2 ==1){
                        g2.setColor(Color.WHITE);
                        g2.fill(cellOfBoardArray[i][j]);
                    }
                }
                if (turnCount > 1 && !animation) {
                    if (i == lastMoveISrc && j == lastMoveJSrc) {
                        g2.setColor(new Color(Color.YELLOW.getRed(),Color.YELLOW.getGreen(),Color.YELLOW.getBlue(),170));
                        g2.fill(cellOfBoardArray[i][j]);
                    }
                    if (i == lastMoveIRslt && j == lastMoveJRslt) {
                        g2.setColor(new Color(Color.YELLOW.getRed(),Color.YELLOW.getGreen(),Color.YELLOW.getBlue(),170));
                        g2.fill(cellOfBoardArray[i][j]);
                    }
                }
                if (capturedPiece != null && capturedPiece.getTeam() == turnSide && !animation) {
                    IPiece pieceToBeChanged = pieceWithCords(i,j,initialisedPiecesList);
                    //moving
                    if (pieceToBeChanged == null && capturedPiece.isAbleToMoveHere(i, j, initialisedPiecesList)) {
                        if (!isCheckOnSameSideAfterTurn(capturedPiece,i,j,initialisedPiecesList)) {
                            Ellipse2D canhithere= new Ellipse2D.Double(cellOfBoardArray[i][j].getCenterX() - cellOfBoardArray[i][j].getWidth() / 6,
                                    cellOfBoardArray[i][j].getCenterY() - cellOfBoardArray[i][j].getHeight() / 6,
                                    cellOfBoardArray[i][j].getWidth() / 3,
                                    cellOfBoardArray[i][j].getHeight() / 3);
                            g2.setColor(new Color(Color.YELLOW.getRed(),Color.YELLOW.getGreen(),Color.YELLOW.getBlue(),170));
                            g2.fill(canhithere);
                        }
                    }
                    else

                        // En passant
                        if (pieceToBeChanged == null && capturedPiece.getClass().getSimpleName().equals("Pawn") && enpassantPiece != null && turnCount - enpassantTurn  == 1 && enpassantPiece.getI() == capturedPiece.getI() &&
                                (enpassantPiece.getJ() == capturedPiece.getJ() - 1 || enpassantPiece.getJ() == capturedPiece.getJ() + 1)) {
                            if (capturedPiece.getTeam() == Team.WHITE) {
                                if ((i == capturedPiece.getI() - 1) && j == enpassantPiece.getJ()) {
                                    if (!isCheckOnSameSideAfterTurn(capturedPiece,i,j,initialisedPiecesList)) {
                                        Ellipse2D canhithere= new Ellipse2D.Double(cellOfBoardArray[i][j].getCenterX() - cellOfBoardArray[i][j].getWidth() / 6,
                                                cellOfBoardArray[i][j].getCenterY() - cellOfBoardArray[i][j].getHeight() / 6,
                                                cellOfBoardArray[i][j].getWidth() / 3,
                                                cellOfBoardArray[i][j].getHeight() / 3);
                                        g2.setColor(new Color(Color.YELLOW.getRed(),Color.YELLOW.getGreen(),Color.YELLOW.getBlue(),170));
                                        g2.fill(canhithere);
                                    }
                                }
                            } else {
                                if ((i == capturedPiece.getI() + 1) && j == enpassantPiece.getJ()) {
                                    if (!isCheckOnSameSideAfterTurn(capturedPiece,i,j,initialisedPiecesList)) {
                                        Ellipse2D canhithere= new Ellipse2D.Double(cellOfBoardArray[i][j].getCenterX() - cellOfBoardArray[i][j].getWidth() / 6,
                                                cellOfBoardArray[i][j].getCenterY() - cellOfBoardArray[i][j].getHeight() / 6,
                                                cellOfBoardArray[i][j].getWidth() / 3,
                                                cellOfBoardArray[i][j].getHeight() / 3);
                                        g2.setColor(new Color(Color.YELLOW.getRed(),Color.YELLOW.getGreen(),Color.YELLOW.getBlue(),170));
                                        g2.fill(canhithere);
                                    }
                                }
                            }
                        }
                        else

                            //rotation
                            if(pieceToBeChanged != null
                                    && capturedPiece.getClass().getSimpleName().equals("King")
                                    && pieceToBeChanged.getClass().getSimpleName().equals("Rook")
                                    && capturedPiece.getTeam() == pieceToBeChanged.getTeam()
                                    && capturedPiece.isOnInitPos() && pieceToBeChanged.isOnInitPos()
                                    && capturedPiece.isAbleToMoveHere(i,j,initialisedPiecesList)
                                    && !check) {
                                int jCapPPrev = capturedPiece.getJ();
                                if (pieceToBeChanged.getJ() == 0) {
                                    int iResult = capturedPiece.getI();
                                    int jResult = 2;
                                    if (!isCheckOnSameSideAfterTurn(capturedPiece,iResult,jResult,initialisedPiecesList)) {
                                        Ellipse2D canhithere= new Ellipse2D.Double(cellOfBoardArray[i][j].getCenterX() - cellOfBoardArray[i][j].getWidth() / 2,
                                                cellOfBoardArray[i][j].getCenterY() - cellOfBoardArray[i][j].getHeight() / 2,
                                                cellOfBoardArray[i][j].getWidth(),
                                                cellOfBoardArray[i][j].getHeight());
                                        g2.setColor(Color.RED);
                                        g2.setStroke(new BasicStroke(5));
                                        g2.draw(canhithere);
                                        g2.setStroke(new BasicStroke(1));

                                    } else {
                                        capturedPiece.setJ(jCapPPrev);
                                    }
                                }
                                if (pieceToBeChanged.getJ() == 7) {
                                    int iResult = capturedPiece.getI();
                                    int jResult = 6;
                                    if (!isCheckOnSameSideAfterTurn(capturedPiece,iResult,jResult,initialisedPiecesList)) {
                                        Ellipse2D canhithere= new Ellipse2D.Double(cellOfBoardArray[i][j].getCenterX() - cellOfBoardArray[i][j].getWidth() / 2,
                                                cellOfBoardArray[i][j].getCenterY() - cellOfBoardArray[i][j].getHeight() / 2,
                                                cellOfBoardArray[i][j].getWidth(),
                                                cellOfBoardArray[i][j].getHeight());
                                        g2.setColor(Color.RED);
                                        g2.setStroke(new BasicStroke(5));
                                        g2.draw(canhithere);
                                        g2.setStroke(new BasicStroke(1));
                                    } else {
                                        capturedPiece.setJ(jCapPPrev);
                                    }
                                }
                            }
                            else

                                //hitting
                                if (pieceToBeChanged != null && capturedPiece.isAbleToMoveHere(i, j, initialisedPiecesList) && capturedPiece.getTeam() != pieceToBeChanged.getTeam()) {
                                    if (!isCheckOnSameSideAfterTurn(capturedPiece,i,j,initialisedPiecesList)) {
                                        Ellipse2D canhithere= new Ellipse2D.Double(cellOfBoardArray[i][j].getCenterX() - cellOfBoardArray[i][j].getWidth() / 2,
                                                cellOfBoardArray[i][j].getCenterY() - cellOfBoardArray[i][j].getHeight() / 2,
                                                cellOfBoardArray[i][j].getWidth(),
                                                cellOfBoardArray[i][j].getHeight());
                                        g2.setColor(Color.RED);
                                        g2.setStroke(new BasicStroke(5));
                                        g2.draw(canhithere);
                                        g2.setStroke(new BasicStroke(1));
                                    }
                                }
                }
            }
        }
    }

    /**
     * Checks if it is possible to make a move for that team. Used for checking for mate and pate
     * @param turnSide
     * @param initialisedPiecesList
     * @return
     */
    boolean areAvaibleMoves(Team turnSide, ArrayList<IPiece> initialisedPiecesList) {
        for (int p = 0; p < initialisedPiecesList.size(); p++) {
            IPiece piece = initialisedPiecesList.get(p);
            if (piece.getTeam() == turnSide) {
                for (int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        if (piece.isAbleToMoveHere(i, j, initialisedPiecesList)) {
                            if (pieceWithCords(i, j, initialisedPiecesList) == null) {
                                if (!isCheckOnSameSideAfterTurn(piece, i, j, initialisedPiecesList)) {
                                    return true;
                                }
                            }
                            if (pieceWithCords(i, j, initialisedPiecesList) != null
                                    && piece.getTeam() != pieceWithCords(i, j, initialisedPiecesList).getTeam()) {
                                if (!isCheckOnSameSideAfterTurn(piece, i, j, initialisedPiecesList)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }


    /**
     * Method generates every piece of chess on right position, setting their team, and adds them in list of pieces.
     * Is used only once on the start of application, and being controlled by arePiecesGenerated.
     */
    public void generatePieces() {
        initialisedPiecesList.clear();
        King kingW = new King(cellOfBoardArray[7][4], Team.WHITE, 7, 4);
        initialisedPiecesList.add(kingW);
        King kingB = new King(cellOfBoardArray[0][4], Team.BLACK, 0, 4);
        initialisedPiecesList.add(kingB);
        Pawn pawnA7 = new Pawn(cellOfBoardArray[1][0], Team.BLACK, 1, 0);
        initialisedPiecesList.add(pawnA7);
        Pawn pawnB7 = new Pawn(cellOfBoardArray[1][1], Team.BLACK, 1, 1);
        initialisedPiecesList.add(pawnB7);
        Pawn pawnC7 = new Pawn(cellOfBoardArray[1][2], Team.BLACK, 1, 2);
        initialisedPiecesList.add(pawnC7);
        Pawn pawnD7 = new Pawn(cellOfBoardArray[1][3], Team.BLACK, 1, 3);
        initialisedPiecesList.add(pawnD7);
        Pawn pawnE7 = new Pawn(cellOfBoardArray[1][4],Team.BLACK,1,4);
        initialisedPiecesList.add(pawnE7);
        Pawn pawnF7 = new Pawn(cellOfBoardArray[1][5],Team.BLACK,1,5);
        initialisedPiecesList.add(pawnF7);
        Pawn pawnG7 = new Pawn(cellOfBoardArray[1][6],Team.BLACK,1,6);
        initialisedPiecesList.add(pawnG7);
        Pawn pawnH7 = new Pawn(cellOfBoardArray[1][7],Team.BLACK,1,7);
        initialisedPiecesList.add(pawnH7);
        Pawn pawnA2 = new Pawn(cellOfBoardArray[6][0],Team.WHITE,6,0);
        initialisedPiecesList.add(pawnA2);
        Pawn pawnB2 = new Pawn(cellOfBoardArray[6][1], Team.WHITE, 6, 1);
        initialisedPiecesList.add(pawnB2);
        Pawn pawnC2 = new Pawn(cellOfBoardArray[6][2], Team.WHITE, 6, 2);
        initialisedPiecesList.add(pawnC2);
        Pawn pawnD2 = new Pawn(cellOfBoardArray[6][3], Team.WHITE, 6, 3);
        initialisedPiecesList.add(pawnD2);
        Pawn pawnE2 = new Pawn(cellOfBoardArray[6][4],Team.WHITE,6,4);
        initialisedPiecesList.add(pawnE2);
        Pawn pawnF2 = new Pawn(cellOfBoardArray[6][5],Team.WHITE,6,5);
        initialisedPiecesList.add(pawnF2);
        Pawn pawnG2 = new Pawn(cellOfBoardArray[6][6], Team.WHITE, 6, 6);
        initialisedPiecesList.add(pawnG2);
        Pawn pawnH2 = new Pawn(cellOfBoardArray[6][7],Team.WHITE,6,7);
        initialisedPiecesList.add(pawnH2);
        Rook rookA8 = new Rook(cellOfBoardArray[0][0],Team.BLACK,0,0);
        initialisedPiecesList.add(rookA8);
        Rook rookH8 = new Rook(cellOfBoardArray[0][7],Team.BLACK,0,7);
        initialisedPiecesList.add(rookH8);
        Rook rookA1 = new Rook(cellOfBoardArray[7][0],Team.WHITE,7,0);
        initialisedPiecesList.add(rookA1);
        Rook rookH1 = new Rook(cellOfBoardArray[7][7],Team.WHITE,7,7);
        initialisedPiecesList.add(rookH1);
        Bishop bishopC8 = new Bishop(cellOfBoardArray[0][2],Team.BLACK,0,2);
        initialisedPiecesList.add(bishopC8);
        Bishop bishopF8 = new Bishop(cellOfBoardArray[0][5],Team.BLACK,0,5);
        initialisedPiecesList.add(bishopF8);
        Bishop bishopC1 = new Bishop(cellOfBoardArray[7][2],Team.WHITE,7,2);
        initialisedPiecesList.add(bishopC1);
        Bishop bishopF1 = new Bishop(cellOfBoardArray[7][5],Team.WHITE,7,5);
        initialisedPiecesList.add(bishopF1);
        Knight knightB8 = new Knight(cellOfBoardArray[0][1],Team.BLACK,0,1);
        initialisedPiecesList.add(knightB8);
        Knight knightG8 = new Knight(cellOfBoardArray[0][6],Team.BLACK,0,6);
        initialisedPiecesList.add(knightG8);
        Knight knightB1 = new Knight(cellOfBoardArray[7][1],Team.WHITE,7,1);
        initialisedPiecesList.add(knightB1);
        Knight knightG1 = new Knight(cellOfBoardArray[7][6],Team.WHITE,7,6);
        initialisedPiecesList.add(knightG1);
        Queen queenD8 = new Queen(cellOfBoardArray[0][3],Team.BLACK,0,3);
        initialisedPiecesList.add(queenD8);
        Queen queenD1 = new Queen(cellOfBoardArray[7][3],Team.WHITE,7,3);
        initialisedPiecesList.add(queenD1);
        System.out.println("Turn: " + turnCount + "; side: " + turnSide);
    }
    public IPiece pieceWithCords(int i, int j, ArrayList<IPiece> initialisedPiecesList) {
        if (initialisedPiecesList.stream().filter(piece -> piece.getI() == i && piece.getJ() == j).toList().size() > 0) {
            return initialisedPiecesList.stream().filter(piece -> piece.getI() == i && piece.getJ() == j).toList().get(0);
        }
        return null;
    }

    /**
     * Resets everything
     */
    public void reset() {
        arePiecesGenerated = false;
        turnSide = Team.WHITE;
        turnCount = 1;
        check = false;
        timer.stop();
        capturedPiece = null;
        lastMoveJRslt = 0;
        lastMoveIRslt = 0;
        lastMoveISrc = 0;
        lastMoveJSrc = 0;
        enpassantPiece = null;
        enpassantTurn = 0;
        animation = false;
        mate = false;
        pate= false;
        countOfBlackTurns.clear();
        countOfWhiteTurns.clear();
        timeOfWhiteTurns.clear();
        timeOfBlackTurns.clear();

        repaint();
    }

    /**
     * Method used by Random CPU. Chooses random piece, then calculates every move that it can do, and randomly chooses one of them.
     */
    public void makeRandomMove() {
        if (turnSide == Team.BLACK) {
            IPiece piece = getRandomBlackPiece(initialisedPiecesList);
            ArrayList<Position> avaiblePos = new ArrayList<>();
            while (true) {
                // finfing all avaible moves for this piece
                for (int k = 0; k < 8; k++) {
                    for (int l = 0; l < 8; l++) {
                        //moving
                        if (piece.isAbleToMoveHere(k, l, initialisedPiecesList) && pieceWithCords(k, l, initialisedPiecesList) == null) {
                            if (!isCheckOnSameSideAfterTurn(piece, k, l, initialisedPiecesList)) {
                                avaiblePos.add(new Position(k, l));
                            }
                        } else if (piece.isAbleToMoveHere(k, l, initialisedPiecesList) && pieceWithCords(k, l, initialisedPiecesList) != null
                                && piece.getTeam() != pieceWithCords(k, l, initialisedPiecesList).getTeam()) {
                            if (!isCheckOnSameSideAfterTurn(piece, k, l, initialisedPiecesList)) {
                                avaiblePos.add(new Position(k, l));
                            }
                        } else
                            // En passant
                            if (pieceWithCords(k, l, initialisedPiecesList) == null && piece.getClass().getSimpleName().equals("Pawn") && enpassantPiece != null && turnCount - enpassantTurn == 1 && enpassantPiece.getI() == piece.getI() &&
                                    (enpassantPiece.getJ() == piece.getJ() - 1 || enpassantPiece.getJ() == piece.getJ() + 1)) {
                                if (piece.getTeam() == Team.WHITE) {
                                    if ((k == piece.getI() - 1) && l == enpassantPiece.getJ()) {
                                        initialisedPiecesList.remove(enpassantPiece);
                                        if (!isCheckOnSameSideAfterTurn(piece, k, l, initialisedPiecesList)) {
                                            avaiblePos.add(new Position(k, l));

                                        }
                                        initialisedPiecesList.add(enpassantPiece);
                                    }
                                } else {
                                    if ((k == piece.getI() + 1) && l == enpassantPiece.getJ()) {
                                        initialisedPiecesList.remove(enpassantPiece);
                                        if (!isCheckOnSameSideAfterTurn(piece, k, l, initialisedPiecesList)) {
                                            avaiblePos.add(new Position(k, l));

                                        }
                                        initialisedPiecesList.add(enpassantPiece);
                                    }
                                }
                            } else
                                //rotation
                                if (pieceWithCords(k, l, initialisedPiecesList) != null
                                        && piece.getClass().getSimpleName().equals("King")
                                        && pieceWithCords(k, l, initialisedPiecesList).getClass().getSimpleName().equals("Rook")
                                        && piece.getTeam() == pieceWithCords(k, l, initialisedPiecesList).getTeam()
                                        && piece.isOnInitPos() && pieceWithCords(k, l, initialisedPiecesList).isOnInitPos()
                                        && piece.isAbleToMoveHere(k, l, initialisedPiecesList)
                                        && !check) {
                                    int jCapPPrev = piece.getJ();
                                    int jpTBC = pieceWithCords(k, l, initialisedPiecesList).getJ();
                                    if (pieceWithCords(k, l, initialisedPiecesList).getJ() == 0) {
                                        piece.setJ(3);
                                        if (!isCheckOnSameSideAfterTurn(piece, piece.getI(), 2, initialisedPiecesList)) {
                                            avaiblePos.add(new Position(k, l));
                                        }
                                        piece.setJ(jCapPPrev);
                                        pieceWithCords(k, l, initialisedPiecesList).setJ(jpTBC);
                                    }
                                    if (pieceWithCords(k, l, initialisedPiecesList).getJ() == 7) {
                                        pieceWithCords(k, l, initialisedPiecesList).setJ(5);

                                        if (!isCheckOnSameSideAfterTurn(piece, piece.getI(), 6, initialisedPiecesList)) {
                                            avaiblePos.add(new Position(k, l));
                                        }
                                        piece.setJ(jCapPPrev);
                                        pieceWithCords(k, l, initialisedPiecesList).setJ(jpTBC);
                                    }
                                }
                    }
                }
                if (avaiblePos.size() == 0) {
                    piece = getRandomBlackPiece(initialisedPiecesList);
                    continue;
                }
                Random r = new Random();
                int i = r.nextInt(0, avaiblePos.size());
                int p = avaiblePos.get(i).getI();
                int o = avaiblePos.get(i).getJ();
                //moving
                if (piece.isAbleToMoveHere(p, o, initialisedPiecesList) && pieceWithCords(p, o, initialisedPiecesList) == null) {
                    check = false;
                    piece.makeMove(p, o);
                    lastMoveISrc = piece.getLastI();
                    lastMoveJSrc = piece.getLastJ();
                    lastMoveIRslt = piece.getI();
                    lastMoveJRslt = piece.getJ();
                    if (piece.getClass().getSimpleName().equals("Pawn")) {
                        if (promotion(piece)) {
                        }
                        if (Math.abs(piece.getI() - piece.getInitI()) == 2) {
                            enpassantPiece = piece;
                            enpassantTurn = turnCount;
                        }
                    }
                    if (isCheckOnOppositeSideAfterTurn(turnSide, initialisedPiecesList)) {
                        check = true;
                        System.out.println("Check.");
                    }
                    makeATurn();
                    return;
                }
                //en passant
                if (pieceWithCords(o, p, initialisedPiecesList) == null && piece.getClass().getSimpleName().equals("Pawn") && enpassantPiece != null && turnCount - enpassantTurn == 1 && enpassantPiece.getI() == piece.getI() &&
                        (enpassantPiece.getJ() == piece.getJ() - 1 || enpassantPiece.getJ() == piece.getJ() + 1)) {
                    if (piece.getTeam() == Team.WHITE) {
                        if ((o == piece.getI() - 1) && p == enpassantPiece.getJ()) {
                            check = false;
                            capturedPiece.makeMove(o, p);
                            if (isCheckOnOppositeSideAfterTurn(turnSide, initialisedPiecesList)) {
                                check = true;
                                System.out.println("Check.");
                            }
                            lastMoveISrc = piece.getLastI();
                            lastMoveJSrc = piece.getLastJ();
                            lastMoveIRslt = piece.getI();
                            lastMoveJRslt = piece.getJ();
                            makeATurn();
                            enpassantPiece = null;
                            enpassantTurn = 0;
                            return;
                        }
                    } else {
                        if ((o == piece.getI() + 1) && p == enpassantPiece.getJ()) {
                            check = false;
                            capturedPiece.makeMove(o, p);
                            if (isCheckOnOppositeSideAfterTurn(turnSide, initialisedPiecesList)) {
                                check = true;
                                System.out.println("Check.");
                            }
                            lastMoveISrc = piece.getLastI();
                            lastMoveJSrc = piece.getLastJ();
                            lastMoveIRslt = piece.getI();
                            lastMoveJRslt = piece.getJ();
                            makeATurn();
                            enpassantPiece = null;
                            enpassantTurn = 0;
                            return;
                        }
                    }
                } else
                //rotation
                if(pieceWithCords(o, p, initialisedPiecesList) != null
                        && piece.getClass().getSimpleName().equals("King")
                        && pieceWithCords(o, p, initialisedPiecesList).getClass().getSimpleName().equals("Rook")
                        && piece.getTeam() == pieceWithCords(o, p, initialisedPiecesList).getTeam()
                        && piece.isOnInitPos() && pieceWithCords(o, p, initialisedPiecesList).isOnInitPos()
                        && piece.isAbleToMoveHere(iResult,jResult,initialisedPiecesList)
                        && !check) {
                    if (pieceWithCords(o, p, initialisedPiecesList).getJ() == 0) {
                        pieceWithCords(o, p, initialisedPiecesList).setJ(3);
                        if (!isCheckOnSameSideAfterTurn(piece,piece.getI(),2,initialisedPiecesList)) {
                            piece.makeMove(piece.getI(),2);
                            pieceWithCords(o, p, initialisedPiecesList).makeMove(pieceWithCords(o, p, initialisedPiecesList).getI(), 3);
                            check = false;
                            if (isCheckOnOppositeSideAfterTurn(turnSide,initialisedPiecesList)) {
                                check = true;
                                System.out.println("Check.");
                            }
                            lastMoveISrc = piece.getLastI();
                            lastMoveJSrc = piece.getLastJ();
                            lastMoveIRslt = piece.getI();
                            lastMoveJRslt = piece.getJ();
                            makeATurn();
                            return;
                        }
                    }
                    if (pieceWithCords(o, p, initialisedPiecesList).getJ() == 7) {
                        pieceWithCords(o, p, initialisedPiecesList).setJ(5);
                        if (!isCheckOnSameSideAfterTurn(piece,piece.getI(),6,initialisedPiecesList)) {
                            piece.makeMove(piece.getI(),6);
                            pieceWithCords(o, p, initialisedPiecesList).makeMove(pieceWithCords(o, p, initialisedPiecesList).getI(), 5);
                            check = false;
                            if (isCheckOnOppositeSideAfterTurn(turnSide,initialisedPiecesList)) {
                                check = true;
                                System.out.println("Check.");
                            }
                            lastMoveISrc = piece.getLastI();
                            lastMoveJSrc = piece.getLastJ();
                            lastMoveIRslt = piece.getI();
                            lastMoveJRslt = piece.getJ();
                            makeATurn();
                            return;
                        }
                    }
                }
                if (piece.isAbleToMoveHere(p, o, initialisedPiecesList) && pieceWithCords(p, o, initialisedPiecesList) != null) {
                    check = false;
                    initialisedPiecesList.remove(pieceWithCords(p, o, initialisedPiecesList));
                    piece.makeMove(p, o);
                    lastMoveISrc = piece.getLastI();
                    lastMoveJSrc = piece.getLastJ();
                    lastMoveIRslt = piece.getI();
                    lastMoveJRslt = piece.getJ();
                    if (isCheckOnOppositeSideAfterTurn(turnSide, initialisedPiecesList)) {
                        check = true;
                        System.out.println("Check.");
                    }
                    makeATurn();
                    return;
                }
                piece = getRandomBlackPiece(initialisedPiecesList);
                System.out.println("--------------------------------------------------------------------------------");
            }
        }
    }

    /**
     * Method used for random CPU
     * @param initialisedPiecesList list of pieces
     * @return random black piece
     */
    public IPiece getRandomBlackPiece(ArrayList<IPiece> initialisedPiecesList) {
        Random r = new Random();
        int i = r.nextInt(0,initialisedPiecesList.stream().filter(piece1 -> piece1.getTeam() == Team.BLACK).toList().size());
        return initialisedPiecesList.stream().filter(piece1 -> piece1.getTeam() == Team.BLACK).toList().get(i);
    }

    /**
     * Animation of move (only for player)
     * @param capturedPiece piece to be animated
     * @param i position
     * @param j position
     */
    public void animatedMove(IPiece capturedPiece, int i, int j) {
        animation = true;
        final double[] srcX = {cellOfBoardArray[capturedPiece.getI()][capturedPiece.getJ()].getX()};
        final double[] srcY = {cellOfBoardArray[capturedPiece.getI()][capturedPiece.getJ()].getY()};
        double rsltX = cellOfBoardArray[i][j].getX();
        double rsltY = cellOfBoardArray[i][j].getY();
        int timesTick = 32;
        int tickX = (int) (rsltX - srcX[0]) / 32;
        int tickY = (int) (rsltY - srcY[0]) / 32;
        while (Math.sqrt(tickX * tickX + tickY * tickY) > 15) {
            timesTick++;
            tickX = (int) (rsltX - srcX[0]) / timesTick;
            tickY = (int) (rsltY - srcY[0]) / timesTick;
        }
        int finalTickX = tickX;
        int finalTickY = tickY;
        timer = new Timer(500 / (int)(timesTick * 1.2), e -> {
            srcX[0] += finalTickX;
            srcY[0] += finalTickY;
            capturedPiece.setPath(new Rectangle2D.Double(srcX[0], srcY[0],cellOfBoardArray[0][0].getWidth(),cellOfBoardArray[0][0].getHeight()));
            repaint();
            if ((Math.abs(cellOfBoardArray[i][j].getCenterX() - capturedPiece.getPath().getBounds().getCenterX()) <= 10) &&
                    (Math.abs(cellOfBoardArray[i][j].getCenterY() - capturedPiece.getPath().getBounds().getCenterY()) <= 10)){
                animation = false;
                timer.stop();
                resetCapturedPiece();
                repaint();
            }
        });
        timer.start();
    }

    // makeATurn method to change turnSide and turnCount and call random CPU

    /**
     * Method changes turnSide and turnCount. If random CPU is toggled on, does random move. Also checks for pat and mate.
     */
    public void makeATurn() {
        if (turnSide == Team.BLACK) {
            timeOfBlackTurns.add((System.currentTimeMillis() - start) / 1000);
            countOfBlackTurns.add(turnCount);
            turnSide = Team.WHITE;
        } else {
            timeOfWhiteTurns.add((System.currentTimeMillis() - start) / 1000);
            countOfWhiteTurns.add(turnCount);
            turnSide = Team.BLACK;
        }
        start = System.currentTimeMillis();
        if (!isToggledAi) {
            lastMoveISrc = capturedPiece.getLastI();
            lastMoveJSrc = capturedPiece.getLastJ();
            lastMoveIRslt = capturedPiece.getI();
            lastMoveJRslt = capturedPiece.getJ();
        }
        if (areAvaibleMoves(turnSide,initialisedPiecesList)) {
            turnCount++;
            System.out.println("Turn: " + turnCount + "; side: " + turnSide);
            if (isToggledAi && turnSide == Team.BLACK) {
                makeRandomMove();
            }
        }
        else {
            if (check) {
                System.out.println("Mate");
                mate = true;
            } else {
                System.out.println("Pate");
                pate = true;
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        drawBoard(g2);
        if (!arePiecesGenerated) {
            generatePieces();
            arePiecesGenerated = true;
        }
        if (!animation) {
            initialisedPiecesList.forEach(piece -> piece.setPath(cellOfBoardArray[piece.getI()][piece.getJ()]));
        }
        else {
            initialisedPiecesList.stream().filter(piece -> piece != capturedPiece).forEach(piece -> piece.setPath(cellOfBoardArray[piece.getI()][piece.getJ()]));
        }
        initialisedPiecesList.forEach(piece -> piece.drawPiece(g2));
        if (mate || pate) {
            Rectangle2D back = new Rectangle2D.Double(0,(float) this.getHeight() / 2 - 50,this.getWidth(),100);
            g2.setColor(Color.GRAY);
            g2.fill(back);
            g2.setColor(Color.BLACK);
            String text = "";
            if (mate) {
                if (turnSide == Team.WHITE) {
                    text = "Mate. Black's won.\n Press R to restart the game";
                } else {
                    text = "Mate. White's won.\n Press R to restart the game";
                }
            }
            if (pate) {
                text = "Pate.\n Press R to restart the game";
            }
            g2.setFont(new Font("dasda",Font.BOLD,20));
            FontMetrics metrics = g2.getFontMetrics(g2.getFont());
            int textHeight = metrics.getHeight();
            int textWidth = metrics.stringWidth(text);
            g2.drawString(text, getWidth() / 2 - textWidth / 2, getHeight() / 2 + textHeight / 2);
        }
    }
    public void resetCapturedPiece() {
        capturedPiece = null;
    }

    public ArrayList<Number> getTimeOfWhiteTurns() {
        return timeOfWhiteTurns;
    }

    public ArrayList<Number> getTimeOfBlackTurns() {
        return timeOfBlackTurns;
    }

    public ArrayList<Integer> getCountOfWhiteTurns() {
        return countOfWhiteTurns;
    }

    public ArrayList<Integer> getCountOfBlackTurns() {
        return countOfBlackTurns;
    }
}