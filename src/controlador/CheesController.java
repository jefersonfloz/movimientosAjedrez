package controlador;

import modelo.Board;
import modelo.Converter;
import modelo.Game;
import modelo.PGN;
import vista.Vista;

import javax.swing.*;
import java.awt.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class CheesController implements ActionListener {
    Timer timer;
    private PGN pgn;
    private Game game;
    private boolean isSelected, isMove, notFlip, isPlay, isFigurine, isTop5;
    private int indexOfGame, indexOfMove;
    private Vista vista;

    public CheesController(Vista vista) {
        this.vista = vista;
        notFlip = true;
        indexOfMove = -1;
        timer = new Timer(1000,this);
        timer.start();
        this.vista.addController(this);

    }

    public void openPgn(){
        JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        j.setAcceptAllFileFilterUsed(false);
        j.setDialogTitle("Select a .pgn file");
        FileNameExtensionFilter restrict = new FileNameExtensionFilter("Only .pgn files", "pgn");
        j.addChoosableFileFilter(restrict);
        int r = j.showOpenDialog(null);
        if (r == JFileChooser.APPROVE_OPTION) {
            pgn = new PGN(j.getSelectedFile().getAbsolutePath());

            //add figurine checkbox and to5 checkbox to info panel
            vista.addFigurineCheckBox();
            isSelected = true;
            indexOfGame = 0;

            //set labels in info panel
            String nameOfFile = j.getSelectedFile().getName();
            vista.pgnLabel.setText(nameOfFile.substring(0, nameOfFile.length()-4));

            setGameButton();
        }


    }

    public void endMove(){
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
                vista.moveLabel.setText("Move " + ((indexOfMove + 2) / 2) + " of " + size);
                vista.colorLabel.setText("White");
            }
            else if(indexOfMove+1 != 0) {
                vista.moveLabel.setText("Move " + ((indexOfMove + 1) / 2) + " of " + size);
                vista.colorLabel.setText("Black");
            }

            vista.statsArea.setText(pgn.getGstat().toString(indexOfGame, indexOfMove, isTop5));

            vista.btnPlay.setText("▶");

            highlightMove();
            vista.repaint();
        }
    }

    public void previusMove(){
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
                vista.moveLabel.setText("Move " + ((indexOfMove + 2) / 2) + " of " + size);
                vista.colorLabel.setText("White");
            }
            else if(indexOfMove+1 != 0) {
                vista.moveLabel.setText("Move " + ((indexOfMove + 1) / 2) + " of " + size);
                vista.colorLabel.setText("Black");
            }
            else {
                vista.moveLabel.setText("Move 0 of " + size);
                vista.colorLabel.setText("");
            }

            vista.statsArea.setText(pgn.getGstat().toString(indexOfGame, indexOfMove, isTop5));

            vista.btnPlay.setText("▶");

            highlightMove();
            vista.repaint();
        }
    }


    public void nextMove(){
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
                vista.moveLabel.setText("Move " + ((indexOfMove + 2) / 2) + " of " + size);
                vista.colorLabel.setText("White");
            }
            else if(indexOfMove+1 != 0) {
                vista.moveLabel.setText("Move " + ((indexOfMove + 1) / 2) + " of " + size);
                vista.colorLabel.setText("Black");
            }

            vista.statsArea.setText(pgn.getGstat().toString(indexOfGame, indexOfMove, isTop5));

            vista.btnPlay.setText("▶");

            highlightMove();
            vista.repaint();
        }
    }

    public void searchMoveGame(){
        if(isSelected && vista.searchMove.getText() != null){
            int index = (Integer.parseInt(vista.searchMove.getText())-1)*2;
            if(index >= 0 && index < game.board.getMoves().size()) {
                if(vista.blackRadio.isSelected())
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
                    vista.moveLabel.setText("Move " + ((indexOfMove + 2) / 2) + " of " + size);
                    vista.colorLabel.setText("White");
                }
                else if(indexOfMove+1 != 0) {
                    vista.moveLabel.setText("Move " + ((indexOfMove + 1) / 2) + " of " + size);
                    vista.colorLabel.setText("Black");
                }
            }
            else if(index == -2){
                indexOfMove = -1;
                isMove = false;
                game.board.resetBoard();
                int size = game.board.getMoves().size();
                size = (size/2)+(size%2);
                vista.moveLabel.setText("Move "+(indexOfMove + 1)+" of "+size);
                vista.colorLabel.setText("");
            }
            isPlay = false;
            vista.btnPlay.setText("▶");
            vista.statsArea.setText(pgn.getGstat().toString(indexOfGame, indexOfMove, isTop5));
            highlightMove();
            vista.repaint();
        }
    }


    public void reset(){
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
            vista.moveLabel.setText("Move "+(indexOfMove + 1)+" of "+size);
            vista.colorLabel.setText("");

            vista.statsArea.setText(pgn.getGstat().toString(indexOfGame, indexOfMove, isTop5));

            vista.btnPlay.setText("▶");

            highlightMove();
            vista.repaint();
        }

    }



    public void play(){
        if(isSelected && indexOfMove < game.board.getMoves().size()-1){
            indexOfMove++;
            isMove = true;
            game.board.doMove(game.board.getMoves().get(indexOfMove));
            int size = game.board.getMoves().size();
            size = (size/2)+(size%2);
            if(indexOfMove+1 != 0 && (indexOfMove+1) % 2 == 1) {
                vista.moveLabel.setText("Move " + ((indexOfMove + 2) / 2) + " of " + size);
                vista.colorLabel.setText("White");
            }
            else if(indexOfMove+1 != 0) {
                vista.moveLabel.setText("Move " + ((indexOfMove + 1) / 2) + " of " + size);
                vista.colorLabel.setText("Black");
            }
            vista.statsArea.setText(pgn.getGstat().toString(indexOfGame, indexOfMove, isTop5));
            highlightMove();
        }
    }



    private void highlightMove(){
        Highlighter highlighter = vista.movesArea.getHighlighter();
        Highlighter.HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(new Color(241, 111, 129));
        highlighter.removeAllHighlights();
        if(indexOfMove >= 0) {
            int indexOfMoveText;
            if((indexOfMove+1) % 2 == 1) {
                indexOfMoveText = vista.movesArea.getText().indexOf(String.valueOf((indexOfMove + 2) / 2));
            }
            else {
                indexOfMoveText = vista.movesArea.getText().indexOf(String.valueOf((indexOfMove + 1) / 2));
            }
            if(vista.colorLabel.getText().equals("Black"))
                indexOfMoveText += game.board.getMoves().get(indexOfMove-1).getStringMove().length();
            String strMove;
            if(isFigurine)
                strMove = toFigurine(game.board.getMoves().get(indexOfMove).getStringMove());
            else
                strMove = game.board.getMoves().get(indexOfMove).getStringMove();
            int p0 = vista.movesArea.getText().indexOf(strMove, indexOfMoveText);
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

    private void setGameButton(){
        isMove = false;
        isPlay = false;
        indexOfMove = -1;
        game = pgn.getGames().get(indexOfGame);
        game.board = new Board(Converter.convertMoves(game.getStringMovesArray()));
        game.board.resetBoard();

        vista.tagsArea.setText(game.toString());
        if(isFigurine)
            vista.movesArea.setText(figurineText(game.getStrMovesText()));
        else
            vista.movesArea.setText(game.getStrMovesText());
        vista. statsArea.setText(pgn.getGstat().toString(indexOfGame, indexOfMove, isTop5));

        vista.gameLabel.setText("Game "+(indexOfGame+1)+" of "+pgn.getGames().size());
        int size = game.board.getMoves().size();
        size = (size/2)+(size%2);
        vista.moveLabel.setText("Move "+(indexOfMove + 1)+" of "+size);
        vista.colorLabel.setText("");

        vista.btnPlay.setText("▶");

        vista.repaint();
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.btnPgn) {
            openPgn();
        } else if (e.getSource() == vista.btnReset) {
            reset();
        } else if (e.getSource() == vista.btnEnd) {
            endMove();
        } else if (e.getSource() == vista.btnPreMove) {
            previusMove();
        } else if (e.getSource() == vista.btnNxtMove) {
            nextMove();
        } else if (e.getSource() == vista.btnSearchMove) {
            searchMoveGame();
        }else if (e.getSource() == vista.btnPlay) {
            if(isSelected && indexOfMove < game.board.getMoves().size()-1) {
                isPlay = !isPlay;
                if (isPlay) {
                    vista.btnPlay.setText("⏸\uFE0E");
                    play();
                    if(indexOfMove == game.board.getMoves().size()-1){
                        isPlay = false;
                        vista.btnPlay.setText("▶");
                    }
                    vista.repaint();
                }
                else {
                    vista.btnPlay.setText("▶");
                }
            }

        }
    }

    public int getIndexOfMove() {
        return indexOfMove;
    }

    public int getIndexOfGame() {
        return indexOfGame;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public boolean isMove() {
        return isMove;
    }

    public boolean isNotFlip() {
        return notFlip;
    }

    public boolean isFigurine() {
        return isFigurine;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public boolean isTop5() {
        return isTop5;
    }

    public CheesController() {
    }

    public Game getGame() {
        return game;
    }
}

