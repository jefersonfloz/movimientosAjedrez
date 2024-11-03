package vista;

import controlador.CheesController;
import modelo.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class Vista extends JFrame {
    private final GridBagLayout layout ;
    private final GridBagConstraints constraints ;
    public JPanel infoPanel;
    public JButton btnPgn, btnReset, btnEnd, btnPreMove, btnNxtMove, btnSearchMove, btnFlip, btnPlay;
    public JTextArea tagsArea, statsArea, movesArea;
    public JScrollPane scrollTags, scrollStats, scrollMoves;
    public JTextField searchMove;
    public JLabel searchMoveLabel,pgnLabel, gameLabel, moveLabel, colorLabel;
    public JRadioButton whiteRadio, blackRadio;
    public ButtonGroup bg;
    private Game game;
    private boolean isSelected, isMove, notFlip, isPlay;



    public Vista(){
        super("PGN Reader");

        layout = new GridBagLayout();
        setLayout(layout);
        constraints = new GridBagConstraints();

        //create tagsArea
        tagsArea = new JTextArea(8, 15);
        scrollTags = setTextArea(tagsArea);

        //create statsArea
        statsArea = new JTextArea (8 , 10);
        scrollStats = setTextArea(statsArea);

        //create movesArea
        movesArea = new JTextArea (7 , 10);
        scrollMoves = setTextArea(movesArea);

        //create info panel
        infoPanel = new JPanel();
        infoPanel.setPreferredSize(new Dimension(100, 140));
        infoPanel.setLayout(layout);
        pgnLabel = new JLabel("");
        gameLabel = new JLabel("");
        moveLabel = new JLabel("");
        colorLabel = new JLabel("");
        constraints.insets = new Insets(0, 0, 6, 0);
        addComponentToInfoPanel(colorLabel, 3);

        //crear botones
        btnPgn = new JButton ("Open PGN");
        //

        btnReset = new JButton ("↺");
        //

        btnEnd = new JButton ("Fin");
        //


        btnPreMove = new JButton ("◁");
        //


        btnNxtMove = new JButton ("▷");
        //

        searchMove = new JTextField(5);
        searchMoveLabel = new JLabel("No. de Movimiento:");
        whiteRadio = new JRadioButton("Blancas");
        blackRadio = new JRadioButton("Negras");
        bg = new ButtonGroup();
        bg.add(whiteRadio); bg.add(blackRadio);
        btnSearchMove = new JButton("Search");
        //

        btnFlip = new JButton ("Voltear Tablero");
        btnFlip.addActionListener(e -> {
            notFlip = !notFlip;
            repaint();
        });

        btnPlay = new JButton ("▶");
        btnPlay.setBackground(Color.YELLOW);


        //add chessboard
        constraints.insets = new Insets(0, 0, 0, 0);
        addComponent(new ChessBoard(), 0, 0, 8,8);

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;

        //add textAreas with scroll
        constraints.insets = new Insets(5, 5, 0, 3);
        addComponent(scrollTags , 0 , 8 , 5 , 1);
        constraints.insets = new Insets(5, 2, 0, 5);
        addComponent(scrollStats , 0 , 13 , 4 , 1);
        constraints.insets = new Insets(5, 5, 0, 5);
        addComponent(scrollMoves , 1 , 8 , 10 , 1);
        addComponent(infoPanel, 1 , 15 , 2 , 1);

        //add pgn button
        constraints.insets = new Insets(10, 5, 0, 5);
        addComponent(btnPgn, 2 , 8 , 9 , 1);

        //add move buttons
        constraints.insets = new Insets(5, 5, 0, 2);
        addComponent(btnPreMove, 3 , 8 , 2 , 1);
        //add play button
        constraints.insets = new Insets(5, 3, 0, 0);
        addComponent(btnPlay, 3 , 11 , 4 , 1);

        constraints.insets = new Insets(5, 3, 0, 5);
        addComponent(btnNxtMove, 3 , 15 , 2 , 1);


        constraints.insets = new Insets(5, 5, 0, 2);
        addComponent(btnReset, 4 ,8  , 2 , 1);

        //add flip button
        constraints.insets = new Insets(5, 5, 0, 2);
        addComponent(btnFlip, 4, 13 , 2 , 1);

        constraints.insets = new Insets(5, 3, 0, 5);
        addComponent(btnEnd, 4 , 15 , 2 , 1);

        constraints.insets = new Insets(5, 5, 0, 2);
        addComponent(searchMoveLabel, 5 , 13 , 1 , 1);

        constraints.insets = new Insets(5, 3, 0, 5);
        addComponent(searchMove, 5 , 14 , 1 , 1);

        constraints.insets = new Insets(5, 5, 0, 2);
        addComponent(whiteRadio, 5 , 15 , 1 , 1);

        constraints.insets = new Insets(5, 3, 0, 5);
        addComponent(blackRadio, 5 , 16, 1 , 1);

        constraints.insets = new Insets(5, 3, 0, 5);
        addComponent(btnSearchMove, 6 , 8 ,10  , 1);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        setLocationRelativeTo(null);

    }


    public class ChessBoard extends JPanel{
        final int UNIT_SIZE = 60;
        final int SCREEN_SIZE = 8*UNIT_SIZE;

        public ChessBoard() {
            setPreferredSize(new Dimension(SCREEN_SIZE, SCREEN_SIZE));
            setBackground(new Color(255, 255, 255));
            setFocusable(true);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            draw(g);
        }

        public void draw(Graphics g) {
            // paint the board
            g.setColor(new Color(118, 150, 86));
            for(int i = 0; i <= SCREEN_SIZE; i += UNIT_SIZE) {
                for(int j = 0; j <= SCREEN_SIZE; j += UNIT_SIZE) {
                    if (((i / UNIT_SIZE) % 2 == 0 && (j / UNIT_SIZE) % 2 != 0)
                            || ((i / UNIT_SIZE) % 2 != 0 && (j / UNIT_SIZE) % 2 == 0))
                        g.fillRect(i, j, UNIT_SIZE, UNIT_SIZE);
                }
            }

            //paint the move (show initial and final square of move)
            if(isMove){
                Move move=null;
                /*if(indexOfMove >= 0)
                    move = game.board.getMoves().get(indexOfMove);
                else
                    move = game.board.getMoves().get(0);*/
                g.setColor(new Color(231, 231, 0));
                if(move.getTypeOfConstructor() != 3) {
                    int[] s1 = Method.getPosition(move.getFrom());
                    int[] s2 = Method.getPosition(move.getTo());
                    if(notFlip) {
                        g.fillRect(s1[1] * UNIT_SIZE, (7 - s1[0]) * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                        g.fillRect(s2[1] * UNIT_SIZE, (7 - s2[0]) * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                    } else {
                        g.fillRect((7-s1[1]) * UNIT_SIZE, (s1[0]) * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                        g.fillRect((7-s2[1]) * UNIT_SIZE, (s2[0]) * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                    }
                } else {
                    if(move.isWhite()){
                        if(move.isKingSideCastle()){
                            if(notFlip) {
                                g.fillRect((6) * UNIT_SIZE, (7) * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                                g.fillRect((4) * UNIT_SIZE, (7) * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                            } else {
                                g.fillRect( UNIT_SIZE, 0, UNIT_SIZE, UNIT_SIZE);
                                g.fillRect((3) * UNIT_SIZE, 0, UNIT_SIZE, UNIT_SIZE);
                            }
                        }
                        else if(move.isQueenSideCastle()){
                            if(notFlip) {
                                g.fillRect((4) * UNIT_SIZE, (7) * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                                g.fillRect((2) * UNIT_SIZE, (7) * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                            } else {
                                g.fillRect((3) * UNIT_SIZE, 0, UNIT_SIZE, UNIT_SIZE);
                                g.fillRect((5) * UNIT_SIZE, 0, UNIT_SIZE, UNIT_SIZE);
                            }
                        }
                    } else{
                        if(move.isKingSideCastle()){
                            if(notFlip) {
                                g.fillRect((6) * UNIT_SIZE, 0, UNIT_SIZE, UNIT_SIZE);
                                g.fillRect((4) * UNIT_SIZE, 0, UNIT_SIZE, UNIT_SIZE);
                            } else {
                                g.fillRect( UNIT_SIZE, (7) * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                                g.fillRect((3) * UNIT_SIZE, (7) * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                            }
                        }
                        else if(move.isQueenSideCastle()){
                            if(notFlip) {
                                g.fillRect((4) * UNIT_SIZE, 0, UNIT_SIZE, UNIT_SIZE);
                                g.fillRect((2) * UNIT_SIZE, 0, UNIT_SIZE, UNIT_SIZE);
                            } else {
                                g.fillRect((3) * UNIT_SIZE, (7) * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                                g.fillRect((5) * UNIT_SIZE, (7) * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                            }
                        }
                    }
                }
            }

            // draw coordinates of squares
            g.setFont(new Font("Calibri", Font.BOLD, 15));
            for(int i = 8; i > 0; i--){
                if(i % 2 == 1)
                    g.setColor(new Color(238, 238, 210));
                else
                    g.setColor(new Color(118, 150, 86));
                g.drawString(String.valueOf(i),2,13+UNIT_SIZE*(8-i));
                g.drawString(String.valueOf((char)(i+96)),(i)*UNIT_SIZE-10,8*UNIT_SIZE-5);
            }

            //draw image of pieces
            BufferedImage image = null;
            try {
                for(int i = 0; i < 8; i++) {
                    for (int j = 0; j < 8; j++) {
                        Piece piece = Method.getPiece(new int[]{i, j});
                        if(piece != Piece.NONE) {
                            switch (piece) {
                                case WHITE_PAWN -> image = ImageIO.read(new File("src/image/wp.png"));
                                case BLACK_PAWN -> image = ImageIO.read(new File("src/image/bp.png"));
                                case WHITE_ROOK -> image = ImageIO.read(new File("src/image/wr.png"));
                                case BLACK_ROOK -> image = ImageIO.read(new File("src/image/br.png"));
                                case WHITE_KNIGHT -> image = ImageIO.read(new File("src/image/wn.png"));
                                case BLACK_KNIGHT -> image = ImageIO.read(new File("src/image/bn.png"));
                                case WHITE_BISHOP -> image = ImageIO.read(new File("src/image/wb.png"));
                                case BLACK_BISHOP -> image = ImageIO.read(new File("src/image/bb.png"));
                                case WHITE_QUEEN -> image = ImageIO.read(new File("src/image/wq.png"));
                                case BLACK_QUEEN -> image = ImageIO.read(new File("src/image/bq.png"));
                                case WHITE_KING -> image = ImageIO.read(new File("src/image/wk.png"));
                                case BLACK_KING -> image = ImageIO.read(new File("src/image/bk.png"));
                            }
                            if(notFlip)
                                g.drawImage(image, j * UNIT_SIZE, (7-i) * UNIT_SIZE, null);
                            else
                                g.drawImage(image, (7-j) * UNIT_SIZE, i * UNIT_SIZE, null);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addComponent(Component component, int row, int column, int width, int height) {
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.gridwidth = width;
        constraints.gridheight = height;
        layout.setConstraints(component, constraints);
        add(component);
    }

    private void addComponentToInfoPanel(Component component, int row){
        constraints.gridx = 0;
        constraints.gridy = row;
        layout.setConstraints(component, constraints);
        infoPanel.add(component);
    }

    private JScrollPane setTextArea(JTextArea textArea){
        textArea.setFont(new Font("Sans-Serif", Font.BOLD, 15));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        return new JScrollPane(textArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    }

    public void addFigurineCheckBox(){
        if(!isSelected) {
            constraints.fill = GridBagConstraints.NONE;
            constraints.weightx = 0;
            constraints.insets = new Insets(0, 0, 0, 0);
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
        }
    }


    public void addController(CheesController controller) {
        btnPgn.addActionListener(controller);
        btnReset.addActionListener(controller);
        btnEnd.addActionListener(controller);
        btnPreMove.addActionListener(controller);
        btnNxtMove.addActionListener(controller);
        btnSearchMove.addActionListener(controller);
        btnFlip.addActionListener(controller);
        btnPlay.addActionListener(controller);
    }


}
