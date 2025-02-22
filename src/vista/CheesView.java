package vista;

import modelo.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class CheesView extends JFrame implements ActionListener {
    private final GridBagLayout layout ;
    private final GridBagConstraints constraints ;
    JPanel infoPanel;
    JButton btnPgn, btnReset, btnEnd, btnPreMove, btnNxtMove, btnSearchMove, btnFlip, btnPlay;
    JTextArea tagsArea, statsArea, movesArea;
    JScrollPane scrollTags, scrollStats, scrollMoves;
    JTextField searchMove;
    JLabel searchMoveLabel,pgnLabel, gameLabel, moveLabel, colorLabel;
    JRadioButton whiteRadio, blackRadio;
    ButtonGroup bg;
    Timer timer;
    private PGN pgn;
    private Game game;
    private boolean isSelected, isMove, notFlip, isPlay, isFigurine, isTop5;
    private int indexOfGame, indexOfMove;


    public CheesView(){
        super("PGN Reader");

        layout = new GridBagLayout();
        setLayout(layout);
        constraints = new GridBagConstraints();

        notFlip = true;
        indexOfMove = -1;
        timer = new Timer(1000, this);
        timer.start();

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
        btnPgn = new JButton ("Abrir PGN");
        btnPgn.addActionListener(e -> {
            JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            j.setAcceptAllFileFilterUsed(false);
            j.setDialogTitle("Select a .pgn file");
            FileNameExtensionFilter restrict = new FileNameExtensionFilter("Only .pgn files", "pgn");
            j.addChoosableFileFilter(restrict);
            int r = j.showOpenDialog(null);
            if (r == JFileChooser.APPROVE_OPTION) {
                pgn = new PGN(j.getSelectedFile().getAbsolutePath());

                //add figurine checkbox and to5 checkbox to info panel
                addFigurineCheckBox();
                isSelected = true;
                indexOfGame = 0;

                //set labels in info panel
                String nameOfFile = j.getSelectedFile().getName();
                pgnLabel.setText(nameOfFile.substring(0, nameOfFile.length()-4));

                setGameButton();
            }
        });


        btnReset = new JButton ("↺");
        btnReset.addActionListener(e -> {
            if(isSelected){
                //set variables
                isMove = false;
                isPlay = false;
                indexOfMove = -1;

                //reset board
                game.board.resetBoard();

                //set labels of info panel
                int size = game.board.getMoves().size();
                size = (size/2)+(size%2);
                moveLabel.setText("Move "+(indexOfMove + 1)+" of "+size);
                colorLabel.setText("");

                statsArea.setText(pgn.getGstat().toString(indexOfGame, indexOfMove, isTop5));

                btnPlay.setText("▶");

                highlightMove();
                repaint();
            }
        });

        btnEnd = new JButton ("Fin");
        btnEnd.addActionListener(e -> {
            if(isSelected && indexOfMove < game.board.getMoves().size()-1){
                //set variables
                isMove = true;
                isPlay = false;
                indexOfMove = game.board.getMoves().size()-1;

                //perform every move to reach the end of game
                for(int i = 0; i <= indexOfMove; i++)
                    game.board.doMove(game.board.getMoves().get(i));

                //set labels of info panel
                int size = game.board.getMoves().size();
                size = (size/2)+(size%2);
                if(indexOfMove+1 != 0 && (indexOfMove+1) % 2 == 1) {
                    moveLabel.setText("Move " + ((indexOfMove + 2) / 2) + " of " + size);
                    colorLabel.setText("White");
                }
                else if(indexOfMove+1 != 0) {
                    moveLabel.setText("Move " + ((indexOfMove + 1) / 2) + " of " + size);
                    colorLabel.setText("Black");
                }

                statsArea.setText(pgn.getGstat().toString(indexOfGame, indexOfMove, isTop5));

                btnPlay.setText("▶");

                highlightMove();
                repaint();
            }
        });


        btnPreMove = new JButton ("◁");
        btnPreMove.addActionListener(e -> {
            if(isSelected && indexOfMove >= 0){
                //set variables
                isMove = true;
                isPlay = false;
                indexOfMove--;

                //undo last move
                game.board.undoMove();

                //set labels of info panel
                int size = game.board.getMoves().size();
                size = (size/2)+(size%2);
                if(indexOfMove+1 != 0 && (indexOfMove+1) % 2 == 1) {
                    moveLabel.setText("Move " + ((indexOfMove + 2) / 2) + " of " + size);
                    colorLabel.setText("White");
                }
                else if(indexOfMove+1 != 0) {
                    moveLabel.setText("Move " + ((indexOfMove + 1) / 2) + " of " + size);
                    colorLabel.setText("Black");
                }
                else {
                    moveLabel.setText("Move 0 of " + size);
                    colorLabel.setText("");
                }

                statsArea.setText(pgn.getGstat().toString(indexOfGame, indexOfMove, isTop5));

                btnPlay.setText("▶");

                highlightMove();
                repaint();
            }
        });

        btnNxtMove = new JButton ("▷");
        btnNxtMove.addActionListener(e -> {
            if(isSelected && indexOfMove < game.board.getMoves().size()-1){
                //set variables
                isMove = true;
                isPlay = false;
                indexOfMove++;

                //perform next move
                game.board.doMove(game.board.getMoves().get(indexOfMove));

                //set labels of info panel
                int size = game.board.getMoves().size();
                size = (size/2)+(size%2);
                if(indexOfMove+1 != 0 && (indexOfMove+1) % 2 == 1) {
                    moveLabel.setText("Move " + ((indexOfMove + 2) / 2) + " of " + size);
                    colorLabel.setText("White");
                }
                else if(indexOfMove+1 != 0) {
                    moveLabel.setText("Move " + ((indexOfMove + 1) / 2) + " of " + size);
                    colorLabel.setText("Black");
                }

                statsArea.setText(pgn.getGstat().toString(indexOfGame, indexOfMove, isTop5));

                btnPlay.setText("▶");

                highlightMove();
                repaint();
            }
        });

        searchMove = new JTextField(5);
        searchMoveLabel = new JLabel("No. de Movimiento:");
        whiteRadio = new JRadioButton("Blancas");
        blackRadio = new JRadioButton("Negras");
        bg = new ButtonGroup();
        bg.add(whiteRadio); bg.add(blackRadio);
        btnSearchMove = new JButton("Search");
        btnSearchMove.addActionListener(e -> {
            if(isSelected && searchMove.getText() != null){
                int index = (Integer.parseInt(searchMove.getText())-1)*2;
                if(index >= 0 && index < game.board.getMoves().size()) {
                    if(blackRadio.isSelected())
                        index++;
                    if(index > indexOfMove) {
                        for (int i = indexOfMove + 1; i <= index; i++)
                            game.board.doMove(game.board.getMoves().get(i));
                        indexOfMove = index;
                    }
                    else if(index < indexOfMove){
                        while (index != indexOfMove){
                            indexOfMove--;
                            game.board.undoMove();
                        }
                    }
                    isMove = true;
                    int size = game.board.getMoves().size();
                    size = (size/2)+(size%2);
                    if(indexOfMove+1 != 0 && (indexOfMove+1) % 2 == 1) {
                        moveLabel.setText("Move " + ((indexOfMove + 2) / 2) + " of " + size);
                        colorLabel.setText("White");
                    }
                    else if(indexOfMove+1 != 0) {
                        moveLabel.setText("Move " + ((indexOfMove + 1) / 2) + " of " + size);
                        colorLabel.setText("Black");
                    }
                }
                else if(index == -2){
                    indexOfMove = -1;
                    isMove = false;
                    game.board.resetBoard();
                    int size = game.board.getMoves().size();
                    size = (size/2)+(size%2);
                    moveLabel.setText("Move "+(indexOfMove + 1)+" of "+size);
                    colorLabel.setText("");
                }
                isPlay = false;
                btnPlay.setText("▶");
                statsArea.setText(pgn.getGstat().toString(indexOfGame, indexOfMove, isTop5));
                highlightMove();
                repaint();
            }
        });

        btnFlip = new JButton ("Voltear Tablero");
        btnFlip.addActionListener(e -> {
            notFlip = !notFlip;
            repaint();
        });

        btnPlay = new JButton ("▶");
        btnPlay.setBackground(Color.YELLOW);
        btnPlay.addActionListener(e -> {
            if(isSelected && indexOfMove < game.board.getMoves().size()-1) {
                isPlay = !isPlay;
                if (isPlay)
                    btnPlay.setText("⏸\uFE0E");
                else
                    btnPlay.setText("▶");
            }
        });

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
                Move move;
                if(indexOfMove >= 0)
                    move = game.board.getMoves().get(indexOfMove);
                else
                    move = game.board.getMoves().get(0);
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

    private void addFigurineCheckBox(){
        if(!isSelected) {
            constraints.fill = GridBagConstraints.NONE;
            constraints.weightx = 0;
            constraints.insets = new Insets(0, 0, 0, 0);
            constraints.gridwidth = 1;
            constraints.gridheight = 1;
        }
    }

    private void setGameButton(){
        isMove = false;
        isPlay = false;
        indexOfMove = -1;
        game = pgn.getGames().get(indexOfGame);
        game.board = new Board(Converter.convertMoves(game.getStringMovesArray()));
        game.board.resetBoard();

        tagsArea.setText(game.toString());
        if(isFigurine)
            movesArea.setText(figurineText(game.getStrMovesText()));
        else
            movesArea.setText(game.getStrMovesText());
        statsArea.setText(pgn.getGstat().toString(indexOfGame, indexOfMove, isTop5));

        gameLabel.setText("Game "+(indexOfGame+1)+" of "+pgn.getGames().size());
        int size = game.board.getMoves().size();
        size = (size/2)+(size%2);
        moveLabel.setText("Move "+(indexOfMove + 1)+" of "+size);
        colorLabel.setText("");

        btnPlay.setText("▶");

        repaint();
    }


    private void play(){
        if(isSelected && indexOfMove < game.board.getMoves().size()-1){
            indexOfMove++;
            isMove = true;
            game.board.doMove(game.board.getMoves().get(indexOfMove));
            int size = game.board.getMoves().size();
            size = (size/2)+(size%2);
            if(indexOfMove+1 != 0 && (indexOfMove+1) % 2 == 1) {
                moveLabel.setText("Move " + ((indexOfMove + 2) / 2) + " of " + size);
                colorLabel.setText("White");
            }
            else if(indexOfMove+1 != 0) {
                moveLabel.setText("Move " + ((indexOfMove + 1) / 2) + " of " + size);
                colorLabel.setText("Black");
            }
            statsArea.setText(pgn.getGstat().toString(indexOfGame, indexOfMove, isTop5));
            highlightMove();
        }
    }

    private void highlightMove(){
        Highlighter highlighter = movesArea.getHighlighter();
        Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(new Color(241, 111, 129));
        highlighter.removeAllHighlights();
        if(indexOfMove >= 0) {
            int indexOfMoveText;
            if((indexOfMove+1) % 2 == 1) {
                indexOfMoveText = movesArea.getText().indexOf(String.valueOf((indexOfMove + 2) / 2));
            }
            else {
                indexOfMoveText = movesArea.getText().indexOf(String.valueOf((indexOfMove + 1) / 2));
            }
            if(colorLabel.getText().equals("Black"))
                indexOfMoveText += game.board.getMoves().get(indexOfMove-1).getStringMove().length();
            String strMove;
            if(isFigurine)
                strMove = toFigurine(game.board.getMoves().get(indexOfMove).getStringMove());
            else
                strMove = game.board.getMoves().get(indexOfMove).getStringMove();
            int p0 = movesArea.getText().indexOf(strMove, indexOfMoveText);
            int p1 = p0 + strMove.length();
            try {
                highlighter.addHighlight(p0, p1, painter);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }


    private String toFigurine(String s){
        if(indexOfMove % 2 == 0){
            for(int i = 0; i < s.length(); i++) {
                switch (s.charAt(i)) {
                    case 'R' -> s = s.replaceFirst("R", "♖");
                    case 'N' -> s = s.replaceFirst("N", "♘");
                    case 'B' -> s = s.replaceFirst("B", "♗");
                    case 'Q' -> s = s.replaceFirst("Q", "♕");
                    case 'K' -> s = s.replaceFirst("K", "♔");
                }
            }
        } else {
            for(int i = 0; i < s.length(); i++) {
                switch (s.charAt(i)) {
                    case 'R' -> s = s.replaceFirst("R", "♜");
                    case 'N' -> s = s.replaceFirst("N", "♞");
                    case 'B' -> s = s.replaceFirst("B", "♝");
                    case 'Q' -> s = s.replaceFirst("Q", "♛");
                    case 'K' -> s = s.replaceFirst("K", "♚");
                }
            }
        }
        return s;
    }

    private String figurineText(String s){
        for(int i = 0; i < s.length(); i++) {
            if(i > 1)
                i--;
            if (s.charAt(i) == '.') {
                i++;
                while(s.charAt(i) != '.') {
                    if(s.charAt(i) == ' ')
                        i++;
                    while(s.charAt(i) != ' ') {
                        switch (s.charAt(i)) {
                            case 'R' -> s = s.replaceFirst("R", "♖");
                            case 'N' -> s = s.replaceFirst("N", "♘");
                            case 'B' -> s = s.replaceFirst("B", "♗");
                            case 'Q' -> s = s.replaceFirst("Q", "♕");
                            case 'K' -> s = s.replaceFirst("K", "♔");
                        }
                        i++;
                    }
                    while(s.charAt(i) != '.') {
                        switch (s.charAt(i)) {
                            case 'R' -> s = s.replaceFirst("R", "♜");
                            case 'N' -> s = s.replaceFirst("N", "♞");
                            case 'B' -> s = s.replaceFirst("B", "♝");
                            case 'Q' -> s = s.replaceFirst("Q", "♛");
                            case 'K' -> s = s.replaceFirst("K", "♚");
                        }
                        i++;
                        if(i >= s.length())
                            break;
                    }
                    if(i >= s.length())
                        break;
                }
            }
        }
        return s;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(isPlay) {
            play();
            if(indexOfMove == game.board.getMoves().size()-1){
                isPlay = false;
                btnPlay.setText("▶");
            }
            repaint();
        }
    }


}
