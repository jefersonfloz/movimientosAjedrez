package modelo;
import java.util.ArrayList;
import java.util.Arrays;

public class Converter extends Method{
    // convierte la lista en movimientos
    public static ArrayList<Move> convertMoves(String[] movesArray) {
        ArrayList<Move> moves = new ArrayList<>();
        resetSquares();

        for (int i = 0; i < movesArray.length; i++) {
            String s = movesArray[i];
            int[] position, initial;
            String piece, x, y;

            switch (s.length()) {
                case 2:
                    position = getPosition(s);
                    initial = detectPawn(i, position, '0');
                    moves.add(new Move(getSquare(initial), getSquare(position), getPiece(initial), s));
                    break;

                case 3:
                    if (s.equals("O-O")) {
                        moves.add(new Move(i % 2 == 0, true, false, false, s));
                    } else {
                        piece = String.valueOf(s.charAt(0));
                        position = getPosition(s.charAt(1) + String.valueOf(s.charAt(2)));
                        initial = detectPiece(i, piece, position, false);
                        moves.add(new Move(getSquare(initial), getSquare(position), getPiece(initial), s));
                    }
                    break;

                case 4:
                    position = getPosition(s.charAt(2) + String.valueOf(s.charAt(3)));
                    if (s.contains("=")) {
                        initial = detectPawn(i, position, '0');
                        moves.add(new Move(getSquare(initial), getSquare(position), getPiece(i, s.charAt(3)), getPiece(initial), Piece.NONE, false, s));
                    } else if (s.contains("x")) {
                        initial = (Character.isUpperCase(s.charAt(0))) ?
                                detectPiece(i, String.valueOf(s.charAt(0)), position, true) :
                                detectPawn(i, position, Character.toUpperCase(s.charAt(0)));
                        moves.add(new Move(getSquare(initial), getSquare(position), getPiece(initial), getPiece(position), Piece.NONE, false, s));
                    } else {
                        piece = String.valueOf(s.charAt(0));
                        x = String.valueOf(s.charAt(1));
                        initial = detectPieceGivenInformation(i, piece,
                                Character.isLowerCase(s.charAt(1)) ? null : x,
                                Character.isLowerCase(s.charAt(1)) ? x : null,
                                position);
                        moves.add(new Move(getSquare(initial), getSquare(position), getPiece(initial), s));
                    }
                    break;

                case 5:
                    if (s.equals("O-O-O")) {
                        moves.add(new Move(i % 2 == 0, false, true, false, s));
                    } else {
                        position = getPosition(s.charAt(3) + String.valueOf(s.charAt(4)));
                        if (s.charAt(0) == 'Q' && Character.isLowerCase(s.charAt(1)) && Character.isLowerCase(s.charAt(3))
                                && Character.isDigit(s.charAt(2)) && Character.isDigit(s.charAt(4))) {
                            initial = getPosition(s.charAt(1) + String.valueOf(s.charAt(2)));
                            moves.add(new Move(getSquare(initial), getSquare(position), getPiece(initial), s));
                        } else {
                            String pieceInfo = String.valueOf(s.charAt(0));
                            String coordinate = Character.isLowerCase(s.charAt(1)) ? String.valueOf(s.charAt(1)) : String.valueOf(s.charAt(1));
                            initial = detectPieceGivenInformation(i, pieceInfo,
                                    Character.isLowerCase(s.charAt(1)) ? null : coordinate,
                                    Character.isLowerCase(s.charAt(1)) ? coordinate : null,
                                    position);
                            moves.add(new Move(getSquare(initial), getSquare(position), getPiece(initial), getPiece(position), Piece.NONE, false, s));
                        }
                    }
                    break;

                case 6:
                    position = getPosition(s.charAt(4) + String.valueOf(s.charAt(5)));
                    if (s.contains("=")) {
                        initial = detectPawn(i, position, Character.toUpperCase(s.charAt(0)));
                        moves.add(new Move(getSquare(initial), getSquare(position), getPiece(i, s.charAt(5)), getPiece(position), getPiece(initial), false, s));
                    } else if (s.contains("x")) {
                        initial = getPosition(s.charAt(1) + String.valueOf(s.charAt(2)));
                        moves.add(new Move(getSquare(initial), getSquare(position), getPiece(initial), getPiece(position), Piece.NONE, false, s));
                    }
                    break;
            }
        }
        return moves;
    }


    public static int[] detectPawn(int i, int[] position, char s1) {
        int direction = (i % 2 == 0) ? -1 : 1; // Determina la dirección del movimiento
        int newRow = position[0] + direction; // Nueva fila basada en la dirección
        int doubleStepRow = position[0] + (2 * direction); // Fila para el doble paso

        if (s1 == '0') {
            Piece pawn = (i % 2 == 0) ? Piece.WHITE_PAWN : Piece.BLACK_PAWN; // Selecciona el peón correspondiente
            return (getPiece(new int[]{newRow, position[1]}) == pawn) ?
                    new int[]{newRow, position[1]} :
                    new int[]{doubleStepRow, position[1]};
        } else {
            return getPosition(s1 + String.valueOf(position[0] + (i % 2 == 0 ? 0 : 2)));
        }
    }

    public static int[] detectPiece(int i, String piece, int[] position, boolean capture){
        switch (piece.toUpperCase()) {
            case "R":
                for (int j = 0; j < 8; j++) {
                    int[] initial1 = new int[]{j, position[1]};
                    int[] initial2 = new int[]{position[0], j};
                    if(i % 2 == 0) {
                        if (getPiece(initial1) == Piece.WHITE_ROOK) {
                            if (isAllowedMove(initial1, position, capture, "vertically")) {
                                if (isAllowedCheck(i, getSquare(initial1), getSquare(position), getPiece(initial1)))
                                    return initial1;
                            }
                        }
                        if (getPiece(initial2) == Piece.WHITE_ROOK) {
                            if (isAllowedMove(initial2, position, capture, "horizontally")) {
                                if (isAllowedCheck(i, getSquare(initial2), getSquare(position), getPiece(initial2)))
                                    return initial2;
                            }
                        }
                    } else {
                        if (getPiece(initial1) == Piece.BLACK_ROOK) {
                            if (isAllowedMove(initial1, position, capture, "vertically")) {
                                if (isAllowedCheck(i, getSquare(initial1), getSquare(position), getPiece(initial1)))
                                    return initial1;
                            }
                        }
                        if (getPiece(initial2) == Piece.BLACK_ROOK) {
                            if (isAllowedMove(initial2, position, capture, "horizontally")) {
                                if (isAllowedCheck(i, getSquare(initial2), getSquare(position), getPiece(initial2)))
                                    return initial2;
                            }
                        }
                    }
                }
                break;
            case "B":
                for (int j = -7; j < 8; j++) {
                    if (position[0] + j >= 0 && position[0] + j <= 7 && position[1] + j >= 0 && position[1] + j <= 7) {
                        int[] initial1 = new int[]{position[0] + j, position[1] + j};
                        if(i % 2 == 0) {
                            if (getPiece(initial1) == Piece.WHITE_BISHOP) {
                                if (isAllowedMove(initial1, position, capture, "diagonally")) {
                                    if (isAllowedCheck(i, getSquare(initial1), getSquare(position), getPiece(initial1)))
                                        return initial1;
                                }
                            }
                        } else {
                            if (getPiece(initial1) == Piece.BLACK_BISHOP) {
                                if (isAllowedMove(initial1, position, capture, "diagonally")) {
                                    if (isAllowedCheck(i, getSquare(initial1), getSquare(position), getPiece(initial1)))
                                        return initial1;
                                }
                            }
                        }
                    }
                    if (position[0] + j >= 0 && position[0] + j <= 7 && position[1] - j >= 0 && position[1] - j <= 7) {
                        int[] initial2 = new int[]{position[0] + j, position[1] - j};
                        if(i % 2 == 0) {
                            if (getPiece(initial2) == Piece.WHITE_BISHOP) {
                                if (isAllowedMove(initial2, position, capture, "diagonally")) {
                                    if (isAllowedCheck(i, getSquare(initial2), getSquare(position), getPiece(initial2)))
                                        return initial2;
                                }
                            }
                        } else {
                            if (getPiece(initial2) == Piece.BLACK_BISHOP) {
                                if (isAllowedMove(initial2, position, capture, "diagonally")) {
                                    if (isAllowedCheck(i, getSquare(initial2), getSquare(position), getPiece(initial2)))
                                        return initial2;
                                }
                            }
                        }
                    }
                }
                break;
            case "N":
                if (position[0] - 1 >= 0 && position[1] - 2 >= 0) {
                    int[] initial = new int[]{position[0] - 1, position[1] - 2};
                    if(i % 2 == 0) {
                        if (getPiece(initial) == Piece.WHITE_KNIGHT) {
                            if (isAllowedCheck(i, getSquare(initial), getSquare(position), getPiece(initial)))
                                return initial;
                        }
                    } else {
                        if (getPiece(initial) == Piece.BLACK_KNIGHT) {
                            if (isAllowedCheck(i, getSquare(initial), getSquare(position), getPiece(initial)))
                                return initial;
                        }
                    }
                }
                if (position[0] - 1 >= 0 && position[1] + 2 <= 7) {
                    int[] initial = new int[]{position[0] - 1, position[1] + 2};
                    if(i % 2 == 0) {
                        if (getPiece(initial) == Piece.WHITE_KNIGHT) {
                            if (isAllowedCheck(i, getSquare(initial), getSquare(position), getPiece(initial)))
                                return initial;
                        }
                    } else {
                        if (getPiece(initial) == Piece.BLACK_KNIGHT) {
                            if (isAllowedCheck(i, getSquare(initial), getSquare(position), getPiece(initial)))
                                return initial;
                        }
                    }
                }
                if (position[0] - 2 >= 0 && position[1] - 1 >= 0) {
                    int[] initial = new int[]{position[0] - 2, position[1] - 1};
                    if(i % 2 == 0) {
                        if (getPiece(initial) == Piece.WHITE_KNIGHT) {
                            if (isAllowedCheck(i, getSquare(initial), getSquare(position), getPiece(initial)))
                                return initial;
                        }
                    } else {
                        if (getPiece(initial) == Piece.BLACK_KNIGHT) {
                            if (isAllowedCheck(i, getSquare(initial), getSquare(position), getPiece(initial)))
                                return initial;
                        }
                    }
                }
                if (position[0] - 2 >= 0 && position[1] + 1 <= 7) {
                    int[] initial = new int[]{position[0] - 2, position[1] + 1};
                    if(i % 2 == 0) {
                        if (getPiece(initial) == Piece.WHITE_KNIGHT) {
                            if (isAllowedCheck(i, getSquare(initial), getSquare(position), getPiece(initial)))
                                return initial;
                        }
                    } else {
                        if (getPiece(initial) == Piece.BLACK_KNIGHT) {
                            if (isAllowedCheck(i, getSquare(initial), getSquare(position), getPiece(initial)))
                                return initial;
                        }
                    }
                }
                if (position[0] + 1 <= 7 && position[1] - 2 >= 0) {
                    int[] initial = new int[]{position[0] + 1, position[1] - 2};
                    if(i % 2 == 0) {
                        if (getPiece(initial) == Piece.WHITE_KNIGHT) {
                            if (isAllowedCheck(i, getSquare(initial), getSquare(position), getPiece(initial)))
                                return initial;
                        }
                    } else {
                        if (getPiece(initial) == Piece.BLACK_KNIGHT) {
                            if (isAllowedCheck(i, getSquare(initial), getSquare(position), getPiece(initial)))
                                return initial;
                        }
                    }
                }
                if (position[0] + 1 <= 7 && position[1] + 2 <= 7) {
                    int[] initial = new int[]{position[0] + 1, position[1] + 2};
                    if(i % 2 == 0) {
                        if (getPiece(initial) == Piece.WHITE_KNIGHT) {
                            if (isAllowedCheck(i, getSquare(initial), getSquare(position), getPiece(initial)))
                                return initial;
                        }
                    } else {
                        if (getPiece(initial) == Piece.BLACK_KNIGHT) {
                            if (isAllowedCheck(i, getSquare(initial), getSquare(position), getPiece(initial)))
                                return initial;
                        }
                    }
                }
                if (position[0] + 2 <= 7 && position[1] - 1 >= 0) {
                    int[] initial = new int[]{position[0] + 2, position[1] - 1};
                    if(i % 2 == 0) {
                        if (getPiece(initial) == Piece.WHITE_KNIGHT) {
                            if (isAllowedCheck(i, getSquare(initial), getSquare(position), getPiece(initial)))
                                return initial;
                        }
                    } else {
                        if (getPiece(initial) == Piece.BLACK_KNIGHT) {
                            if (isAllowedCheck(i, getSquare(initial), getSquare(position), getPiece(initial)))
                                return initial;
                        }
                    }
                }
                if (position[0] + 2 <= 7 && position[1] + 1 <= 7) {
                    int[] initial = new int[]{position[0] + 2, position[1] + 1};
                    if(i % 2 == 0) {
                        if (getPiece(initial) == Piece.WHITE_KNIGHT) {
                            if (isAllowedCheck(i, getSquare(initial), getSquare(position), getPiece(initial)))
                                return initial;
                        }
                    } else {
                        if (getPiece(initial) == Piece.BLACK_KNIGHT) {
                            if (isAllowedCheck(i, getSquare(initial), getSquare(position), getPiece(initial)))
                                return initial;
                        }
                    }
                }
                break;
            case "Q":
                for (int j = 0; j < 8; j++) {
                    int[] initial1 = new int[]{j, position[1]};
                    int[] initial2 = new int[]{position[0], j};
                    if(i % 2 == 0) {
                        if (getPiece(initial1) == Piece.WHITE_QUEEN) {
                            if (isAllowedMove(initial1, position, capture, "vertically")) {
                                if (isAllowedCheck(i, getSquare(initial1), getSquare(position), getPiece(initial1)))
                                    return initial1;
                            }
                        }
                        if (getPiece(initial2) == Piece.WHITE_QUEEN) {
                            if (isAllowedMove(initial2, position, capture, "horizontally")) {
                                if (isAllowedCheck(i, getSquare(initial2), getSquare(position), getPiece(initial2)))
                                    return initial2;
                            }
                        }
                    } else {
                        if (getPiece(initial1) == Piece.BLACK_QUEEN) {
                            if (isAllowedMove(initial1, position, capture, "vertically")) {
                                if (isAllowedCheck(i, getSquare(initial1), getSquare(position), getPiece(initial1)))
                                    return initial1;
                            }
                        }
                        if (getPiece(initial2) == Piece.BLACK_QUEEN) {
                            if (isAllowedMove(initial2, position, capture, "horizontally")) {
                                if (isAllowedCheck(i, getSquare(initial2), getSquare(position), getPiece(initial2)))
                                    return initial2;
                            }
                        }
                    }
                }
                for (int j = -7; j < 8; j++) {
                    if (position[0] + j >= 0 && position[0] + j <= 7 && position[1] + j >= 0 && position[1] + j <= 7) {
                        int[] initial1 = new int[]{position[0] + j, position[1] + j};
                        if(i % 2 == 0) {
                            if (getPiece(initial1) == Piece.WHITE_QUEEN) {
                                if (isAllowedMove(initial1, position, capture, "diagonally")) {
                                    if (isAllowedCheck(i, getSquare(initial1), getSquare(position), getPiece(initial1)))
                                        return initial1;
                                }
                            }
                        } else {
                            if (getPiece(initial1) == Piece.BLACK_QUEEN) {
                                if (isAllowedMove(initial1, position, capture, "diagonally")) {
                                    if (isAllowedCheck(i, getSquare(initial1), getSquare(position), getPiece(initial1)))
                                        return initial1;
                                }
                            }
                        }
                    }
                    if (position[0] + j >= 0 && position[0] + j <= 7 && position[1] - j >= 0 && position[1] - j <= 7) {
                        int[] initial2 = new int[]{position[0] + j, position[1] - j};
                        if(i % 2 == 0) {
                            if (getPiece(initial2) == Piece.WHITE_QUEEN) {
                                if (isAllowedMove(initial2, position, capture, "diagonally")) {
                                    if (isAllowedCheck(i, getSquare(initial2), getSquare(position), getPiece(initial2)))
                                        return initial2;
                                }
                            }
                        } else {
                            if (getPiece(initial2) == Piece.BLACK_QUEEN) {
                                if (isAllowedMove(initial2, position, capture, "diagonally")) {
                                    if (isAllowedCheck(i, getSquare(initial2), getSquare(position), getPiece(initial2)))
                                        return initial2;
                                }
                            }
                        }
                    }
                }
                break;
            default:
                for (int j = -1; j < 2; j++) {
                    for (int k = -1; k < 2; k++) {
                        if(position[0] + j >= 0 && position[0] + j <= 7 &&
                                position[1] + k >= 0 && position[1] + k <= 7) {
                            int[] initial = new int[]{position[0] + j, position[1] + k};
                            if(i % 2 == 0) {
                                if (getPiece(initial) == Piece.WHITE_KING)
                                    return initial;
                            } else {
                                if (getPiece(initial) == Piece.BLACK_KING)
                                    return initial;
                            }
                        }
                    }
                }
                break;
        }
        return new int[]{};
    }

    public static int[] detectPieceGivenInformation(int i, String piece, String x, String y, int[] position){
        if(x != null) {
            int x1 = Integer.parseInt(x)-1;
            switch (piece.toUpperCase()) {
                case "R":
                    return new int[]{x1, position[1]};
                case "N":
                    if(position[1]-1 >= 0) {
                        int[] initial = new int[]{x1, position[1] - 1};
                        if(i % 2 == 0) {
                            if (getPiece(initial) == Piece.WHITE_KNIGHT)
                                return initial;
                        } else {
                            if (getPiece(initial) == Piece.BLACK_KNIGHT)
                                return initial;
                        }
                    }
                    if(position[1]-2 >= 0) {
                        int[] initial = new int[]{x1, position[1] - 2};
                        if(i % 2 == 0) {
                            if (getPiece(initial) == Piece.WHITE_KNIGHT)
                                return initial;
                        } else {
                            if (getPiece(initial) == Piece.BLACK_KNIGHT)
                                return initial;
                        }
                    }
                    if(position[1]+1 <= 7) {
                        int[] initial = new int[]{x1, position[1] + 1};
                        if(i % 2 == 0) {
                            if (getPiece(initial) == Piece.WHITE_KNIGHT)
                                return initial;
                        } else {
                            if (getPiece(initial) == Piece.BLACK_KNIGHT)
                                return initial;
                        }
                    }
                    if(position[1]+2 <= 7) {
                        int[] initial = new int[]{x1, position[1] + 2};
                        if(i % 2 == 0) {
                            if (getPiece(initial) == Piece.WHITE_KNIGHT)
                                return initial;
                        } else {
                            if (getPiece(initial) == Piece.BLACK_KNIGHT)
                                return initial;
                        }
                    }
                    break;
                case "B":
                    for(int j = 0; j <= 7; j++){
                        int[] initial = new int[]{x1, j};
                        if(i % 2 == 0) {
                            if (getPiece(initial) == Piece.WHITE_BISHOP)
                                return initial;
                        } else {
                            if (getPiece(initial) == Piece.BLACK_BISHOP)
                                return initial;
                        }
                    }
                    break;
                case "Q":
                    for(int j = 0; j <= 7; j++){
                        int[] initial = new int[]{x1, j};
                        if(i % 2 == 0) {
                            if (getPiece(initial) == Piece.WHITE_QUEEN)
                                return initial;
                        } else {
                            if (getPiece(initial) == Piece.BLACK_QUEEN)
                                return initial;
                        }
                    }
                    break;
            }
        } else {
            int y1;
            switch (y.toUpperCase()){
                case ("A") -> y1 = 0;
                case ("B") -> y1 = 1;
                case ("C") -> y1 = 2;
                case ("D") -> y1 = 3;
                case ("E") -> y1 = 4;
                case ("F") -> y1 = 5;
                case ("G") -> y1 = 6;
                default -> y1 = 7;
            }
            switch (piece.toUpperCase()) {
                case "R":
                    if(y1 != position[1])
                        return new int[]{position[0], y1};
                    else{
                        for(int k = 0; k <= 7; k++){
                            int[] initial = new int[]{k, y1};
                            if(i % 2 == 0) {
                                if(getPiece(initial) == Piece.WHITE_ROOK)
                                    return new int[]{k, y1};
                            } else {
                                if(getPiece(initial) == Piece.BLACK_ROOK)
                                    return new int[]{k, y1};
                            }
                        }
                    }
                    break;
                case "N":
                    if(position[0]-1 >= 0) {
                        int[] initial = new int[]{position[0] - 1, y1};
                        if(i % 2 == 0) {
                            if (getPiece(initial) == Piece.WHITE_KNIGHT)
                                return initial;
                        } else {
                            if (getPiece(initial) == Piece.BLACK_KNIGHT)
                                return initial;
                        }
                    }
                    if(position[0]-2 >= 0) {
                        int[] initial = new int[]{position[0] - 2, y1};
                        if(i % 2 == 0) {
                            if (getPiece(initial) == Piece.WHITE_KNIGHT)
                                return initial;
                        } else {
                            if (getPiece(initial) == Piece.BLACK_KNIGHT)
                                return initial;
                        }
                    }
                    if(position[0]+1 <= 7) {
                        int[] initial = new int[]{position[0] + 1, y1};
                        if(i % 2 == 0) {
                            if (getPiece(initial) == Piece.WHITE_KNIGHT)
                                return initial;
                        } else {
                            if (getPiece(initial) == Piece.BLACK_KNIGHT)
                                return initial;
                        }
                    }
                    if(position[0]+2 <= 7) {
                        int[] initial = new int[]{position[0] + 2, y1};
                        if(i % 2 == 0) {
                            if (getPiece(initial) == Piece.WHITE_KNIGHT)
                                return initial;
                        } else {
                            if (getPiece(initial) == Piece.BLACK_KNIGHT)
                                return initial;
                        }
                    }
                    break;
                case "B":
                    for(int j = 0; j <= 7; j++){
                        int[] initial = new int[]{j, y1};
                        if(i % 2 == 0) {
                            if (getPiece(initial) == Piece.WHITE_BISHOP)
                                return initial;
                        } else {
                            if (getPiece(initial) == Piece.BLACK_BISHOP)
                                return initial;
                        }
                    }
                    break;
                case "Q":
                    for(int j = 0; j <= 7; j++){
                        int[] initial = new int[]{j, y1};
                        if(i % 2 == 0) {
                            if (getPiece(initial) == Piece.WHITE_QUEEN)
                                return initial;
                        } else {
                            if (getPiece(initial) == Piece.BLACK_QUEEN)
                                return initial;
                        }
                    }
                    break;
            }
        }
        return new int[]{};
    }

    public static boolean isAllowedCheck(int i, Square from, Square to, Piece piece){
        boolean allowed = true;

        Square.piece.put(from, Piece.NONE);
        Piece pieceInTo = Square.piece.get(to);
        Square.piece.put(to, piece);

        for(Square s: Square.values()){
            Piece p = Square.piece.get(s);
            int[] positionOfKing = new int[2];
            if(i % 2 == 0) {
                for(Square s1: Square.values()){
                    if(Square.piece.get(s1) == Piece.WHITE_KING) {
                        positionOfKing = Square.position.get(s1);
                        break;
                    }
                }
                if (p == Piece.BLACK_ROOK){
                    int[] positionOfRook = Square.position.get(s);
                    if(positionOfRook[0] == positionOfKing[0] && positionOfRook[1] != positionOfKing[1]){
                        for(int k = Math.min(positionOfRook[1], positionOfKing[1])+1;
                            k < Math.max(positionOfRook[1], positionOfKing[1]); k++){
                            if(getPiece(new int[]{positionOfRook[0], k}) != Piece.NONE) {
                                allowed = true;
                                break;
                            }
                            allowed = false;
                        }
                    }
                    else if(positionOfRook[0] != positionOfKing[0] && positionOfRook[1] == positionOfKing[1]){
                        for(int k = Math.min(positionOfRook[0], positionOfKing[0])+1;
                            k < Math.max(positionOfRook[0], positionOfKing[0]); k++){
                            if(getPiece(new int[]{k, positionOfRook[1]}) != Piece.NONE) {
                                allowed = true;
                                break;
                            }
                            allowed = false;
                        }
                    }
                    if(!allowed) {
                        Square.piece.put(from, piece);
                        Square.piece.put(to, pieceInTo);
                        return false;
                    }
                }
                else if (p == Piece.BLACK_BISHOP){
                    int[] positionOfBishop = Square.position.get(s);
                    if((positionOfBishop[0] + positionOfBishop[1]) % 2 == (positionOfKing[0] + positionOfKing[1]) % 2
                            && Math.abs(positionOfKing[0]-positionOfBishop[0]) == Math.abs(positionOfKing[1]-positionOfBishop[1])){
                        if(positionOfKing[0] > positionOfBishop[0] && positionOfKing[1] > positionOfBishop[1]){
                            for (int j = 1; j < Math.max(positionOfKing[0]-positionOfBishop[0], positionOfKing[1]-positionOfBishop[1]); j++) {
                                int[] pos = new int[]{positionOfBishop[0] + j, positionOfBishop[1] + j};
                                if(positionOfBishop[0] + j >=0 && positionOfBishop[0] + j <=7 &&
                                        positionOfBishop[1] + j >=0 && positionOfBishop[1] + j <= 7) {
                                    if (getPiece(pos) != Piece.NONE) {
                                        allowed = true;
                                        break;
                                    }
                                    allowed = false;
                                }
                            }
                        }
                        else if(positionOfKing[0] > positionOfBishop[0] && positionOfKing[1] < positionOfBishop[1]){
                            for (int j = 1; j < Math.max(positionOfKing[0]-positionOfBishop[0], positionOfBishop[1]-positionOfKing[1]); j++) {
                                int[] pos = new int[]{positionOfBishop[0] + j, positionOfBishop[1] - j};
                                if(positionOfBishop[0] + j >=0 && positionOfBishop[0] + j <=7 &&
                                        positionOfBishop[1] - j >=0 && positionOfBishop[1] - j <= 7) {
                                    if (getPiece(pos) != Piece.NONE) {
                                        allowed = true;
                                        break;
                                    }
                                    allowed = false;
                                }
                            }
                        }
                        else if(positionOfKing[0] < positionOfBishop[0] && positionOfKing[1] > positionOfBishop[1]){
                            for (int j = 1; j < Math.max(positionOfBishop[0]-positionOfKing[0], positionOfKing[1]-positionOfBishop[1]); j++) {
                                int[] pos = new int[]{positionOfBishop[0] - j, positionOfBishop[1] + j};
                                if(positionOfBishop[0] - j >=0 && positionOfBishop[0] - j <=7 &&
                                        positionOfBishop[1] + j >=0 && positionOfBishop[1] + j <= 7) {
                                    if (getPiece(pos) != Piece.NONE) {
                                        allowed = true;
                                        break;
                                    }
                                    allowed = false;
                                }
                            }
                        }
                        else if(positionOfKing[0] < positionOfBishop[0] && positionOfKing[1] < positionOfBishop[1]){
                            for (int j = 1; j < Math.max(positionOfBishop[0]-positionOfKing[0], positionOfBishop[1]-positionOfKing[1]); j++) {
                                int[] pos = new int[]{positionOfBishop[0] - j, positionOfBishop[1] - j};
                                if(positionOfBishop[0] - j >=0 && positionOfBishop[0] - j <=7 &&
                                        positionOfBishop[1] - j >=0 && positionOfBishop[1] - j <= 7) {
                                    if (getPiece(pos) != Piece.NONE) {
                                        allowed = true;
                                        break;
                                    }
                                    allowed = false;
                                }
                            }
                        }
                        if(!allowed) {
                            Square.piece.put(from, piece);
                            Square.piece.put(to, pieceInTo);
                            return false;
                        }
                    }
                }
                else if (p == Piece.BLACK_QUEEN) {
                    int[] positionOfQueen = Square.position.get(s);
                    if(positionOfQueen[0] == positionOfKing[0] && positionOfQueen[1] != positionOfKing[1]){
                        for(int k = Math.min(positionOfQueen[1], positionOfKing[1])+1;
                            k < Math.max(positionOfQueen[1], positionOfKing[1]); k++){
                            if(getPiece(new int[]{positionOfQueen[0], k}) != Piece.NONE) {
                                allowed = true;
                                break;
                            }
                            allowed = false;
                        }
                    }
                    else if(positionOfQueen[0] != positionOfKing[0] && positionOfQueen[1] == positionOfKing[1]){
                        for(int k = Math.min(positionOfQueen[0], positionOfKing[0])+1;
                            k < Math.max(positionOfQueen[0], positionOfKing[0]); k++){
                            if(getPiece(new int[]{k, positionOfQueen[1]}) != Piece.NONE) {
                                allowed = true;
                                break;
                            }
                            allowed = false;
                        }
                    }
                    if(!allowed) {
                        Square.piece.put(from, piece);
                        Square.piece.put(to, pieceInTo);
                        return false;
                    }
                    if((positionOfQueen[0] + positionOfQueen[1]) % 2 == (positionOfKing[0] + positionOfKing[1]) % 2
                            && Math.abs(positionOfKing[0]-positionOfQueen[0]) == Math.abs(positionOfKing[1]-positionOfQueen[1])){
                        if(positionOfKing[0] > positionOfQueen[0] && positionOfKing[1] > positionOfQueen[1]){
                            for (int j = 1; j < Math.max(positionOfKing[0]-positionOfQueen[0], positionOfKing[1]-positionOfQueen[1]); j++) {
                                int[] pos = new int[]{positionOfQueen[0] + j, positionOfQueen[1] + j};
                                if(positionOfQueen[0] + j >=0 && positionOfQueen[0] + j <=7 &&
                                        positionOfQueen[1] + j >=0 && positionOfQueen[1] + j <= 7) {
                                    if (getPiece(pos) != Piece.NONE) {
                                        allowed = true;
                                        break;
                                    }
                                    allowed = false;
                                }
                            }
                        }
                        else if(positionOfKing[0] > positionOfQueen[0] && positionOfKing[1] < positionOfQueen[1]){
                            for (int j = 1; j < Math.max(positionOfKing[0]-positionOfQueen[0], positionOfQueen[1]-positionOfKing[1]); j++) {
                                int[] pos = new int[]{positionOfQueen[0] + j, positionOfQueen[1] - j};
                                if(positionOfQueen[0] + j >=0 && positionOfQueen[0] + j <=7 &&
                                        positionOfQueen[1] - j >=0 && positionOfQueen[1] - j <= 7) {
                                    if (getPiece(pos) != Piece.NONE) {
                                        allowed = true;
                                        break;
                                    }
                                    allowed = false;
                                }
                            }
                        }
                        else if(positionOfKing[0] < positionOfQueen[0] && positionOfKing[1] > positionOfQueen[1]){
                            for (int j = 1; j < Math.max(positionOfQueen[0]-positionOfKing[0], positionOfKing[1]-positionOfQueen[1]); j++) {
                                int[] pos = new int[]{positionOfQueen[0] - j, positionOfQueen[1] + j};
                                if(positionOfQueen[0] - j >=0 && positionOfQueen[0] - j <=7 &&
                                        positionOfQueen[1] + j >=0 && positionOfQueen[1] + j <= 7) {
                                    if (getPiece(pos) != Piece.NONE) {
                                        allowed = true;
                                        break;
                                    }
                                    allowed = false;
                                }
                            }
                        }
                        else if (positionOfKing[0] < positionOfQueen[0] && positionOfKing[1] < positionOfQueen[1]){
                            for (int j = 1; j < Math.max(positionOfQueen[0]-positionOfKing[0], positionOfQueen[1]-positionOfKing[1]); j++) {
                                int[] pos = new int[]{positionOfQueen[0] - j, positionOfQueen[1] - j};
                                if(positionOfQueen[0] - j >=0 && positionOfQueen[0] - j <=7 &&
                                        positionOfQueen[1] - j >=0 && positionOfQueen[1] - j <= 7) {
                                    if (getPiece(pos) != Piece.NONE) {
                                        allowed = true;
                                        break;
                                    }
                                    allowed = false;
                                }
                            }
                        }
                        if(!allowed) {
                            Square.piece.put(from, piece);
                            Square.piece.put(to, pieceInTo);
                            return false;
                        }
                    }
                }
            }
            else {
                for (Square s1 : Square.values()) {
                    if (Square.piece.get(s1) == Piece.BLACK_KING) {
                        positionOfKing = Square.position.get(s1);
                        break;
                    }
                }
                if (p == Piece.WHITE_ROOK) {
                    int[] positionOfRook = Square.position.get(s);
                    if (positionOfRook[0] == positionOfKing[0] && positionOfRook[1] != positionOfKing[1]) {
                        for (int k = Math.min(positionOfRook[1], positionOfKing[1]) + 1;
                             k < Math.max(positionOfRook[1], positionOfKing[1]); k++) {
                            if (getPiece(new int[]{positionOfRook[0], k}) != Piece.NONE) {
                                allowed = true;
                                break;
                            }
                            allowed = false;
                        }
                    } else if (positionOfRook[0] != positionOfKing[0] && positionOfRook[1] == positionOfKing[1]) {
                        for (int k = Math.min(positionOfRook[0], positionOfKing[0]) + 1;
                             k < Math.max(positionOfRook[0], positionOfKing[0]); k++) {
                            if (getPiece(new int[]{k, positionOfRook[1]}) != Piece.NONE) {
                                allowed = true;
                                break;
                            }
                            allowed = false;
                        }
                    }
                    if (!allowed) {
                        Square.piece.put(from, piece);
                        Square.piece.put(to, pieceInTo);
                        return false;
                    }
                } else if (p == Piece.WHITE_BISHOP) {
                    int[] positionOfBishop = Square.position.get(s);
                    if ((positionOfBishop[0] + positionOfBishop[1]) % 2 == (positionOfKing[0] + positionOfKing[1]) % 2
                            && Math.abs(positionOfKing[0]-positionOfBishop[0]) == Math.abs(positionOfKing[1]-positionOfBishop[1])) {
                        if (positionOfKing[0] > positionOfBishop[0] && positionOfKing[1] > positionOfBishop[1]) {
                            for (int j = 1; j < Math.max(positionOfKing[0] - positionOfBishop[0], positionOfKing[1] - positionOfBishop[1]); j++) {
                                int[] pos = new int[]{positionOfBishop[0] + j, positionOfBishop[1] + j};
                                if (positionOfBishop[0] + j >= 0 && positionOfBishop[0] + j <= 7 &&
                                        positionOfBishop[1] + j >= 0 && positionOfBishop[1] + j <= 7) {
                                    if (getPiece(pos) != Piece.NONE) {
                                        allowed = true;
                                        break;
                                    }
                                    allowed = false;
                                }
                            }
                        } else if (positionOfKing[0] > positionOfBishop[0] && positionOfKing[1] < positionOfBishop[1]) {
                            for (int j = 1; j < Math.max(positionOfKing[0] - positionOfBishop[0], positionOfBishop[1] - positionOfKing[1]); j++) {
                                int[] pos = new int[]{positionOfBishop[0] + j, positionOfBishop[1] - j};
                                if (positionOfBishop[0] + j >= 0 && positionOfBishop[0] + j <= 7 &&
                                        positionOfBishop[1] - j >= 0 && positionOfBishop[1] - j <= 7) {
                                    if (getPiece(pos) != Piece.NONE) {
                                        allowed = true;
                                        break;
                                    }
                                    allowed = false;
                                }
                            }
                        } else if (positionOfKing[0] < positionOfBishop[0] && positionOfKing[1] > positionOfBishop[1]) {
                            for (int j = 1; j < Math.max(positionOfBishop[0] - positionOfKing[0], positionOfKing[1] - positionOfBishop[1]); j++) {
                                int[] pos = new int[]{positionOfBishop[0] - j, positionOfBishop[1] + j};
                                if (positionOfBishop[0] - j >= 0 && positionOfBishop[0] - j <= 7 &&
                                        positionOfBishop[1] + j >= 0 && positionOfBishop[1] + j <= 7) {
                                    if (getPiece(pos) != Piece.NONE) {
                                        allowed = true;
                                        break;
                                    }
                                    allowed = false;
                                }
                            }
                        } else if (positionOfKing[0] < positionOfBishop[0] && positionOfKing[1] < positionOfBishop[1]) {
                            for (int j = 1; j < Math.max(positionOfBishop[0] - positionOfKing[0], positionOfBishop[1] - positionOfKing[1]); j++) {
                                int[] pos = new int[]{positionOfBishop[0] - j, positionOfBishop[1] - j};
                                if (positionOfBishop[0] - j >= 0 && positionOfBishop[0] - j <= 7 &&
                                        positionOfBishop[1] - j >= 0 && positionOfBishop[1] - j <= 7) {
                                    if (getPiece(pos) != Piece.NONE) {
                                        allowed = true;
                                        break;
                                    }
                                    allowed = false;
                                }
                            }
                        }
                        if (!allowed) {
                            Square.piece.put(from, piece);
                            Square.piece.put(to, pieceInTo);
                            return false;
                        }
                    }
                } else if (p == Piece.WHITE_QUEEN) {
                    int[] positionOfQueen = Square.position.get(s);
                    if (positionOfQueen[0] == positionOfKing[0] && positionOfQueen[1] != positionOfKing[1]) {
                        for (int k = Math.min(positionOfQueen[1], positionOfKing[1]) + 1;
                             k < Math.max(positionOfQueen[1], positionOfKing[1]); k++) {
                            if (getPiece(new int[]{positionOfQueen[0], k}) != Piece.NONE) {
                                allowed = true;
                                break;
                            }
                            allowed = false;
                        }
                    } else if (positionOfQueen[0] != positionOfKing[0]&& positionOfQueen[1] == positionOfKing[1]) {
                        for (int k = Math.min(positionOfQueen[0], positionOfKing[0]) + 1;
                             k < Math.max(positionOfQueen[0], positionOfKing[0]); k++) {
                            if (getPiece(new int[]{k, positionOfQueen[1]}) != Piece.NONE) {
                                allowed = true;
                                break;
                            }
                            allowed = false;
                        }
                    }
                    if (!allowed) {
                        Square.piece.put(from, piece);
                        Square.piece.put(to, pieceInTo);
                        return false;
                    }
                    if ((positionOfQueen[0] + positionOfQueen[1]) % 2 == (positionOfKing[0] + positionOfKing[1]) % 2
                            && Math.abs(positionOfKing[0]-positionOfQueen[0]) == Math.abs(positionOfKing[1]-positionOfQueen[1])) {
                        if (positionOfKing[0] > positionOfQueen[0] && positionOfKing[1] > positionOfQueen[1]) {
                            for (int j = 1; j < Math.max(positionOfKing[0] - positionOfQueen[0], positionOfKing[1] - positionOfQueen[1]); j++) {
                                int[] pos = new int[]{positionOfQueen[0] + j, positionOfQueen[1] + j};
                                if (positionOfQueen[0] + j >= 0 && positionOfQueen[0] + j <= 7 &&
                                        positionOfQueen[1] + j >= 0 && positionOfQueen[1] + j <= 7) {
                                    if (getPiece(pos) != Piece.NONE) {
                                        allowed = true;
                                        break;
                                    }
                                    allowed = false;
                                }
                            }
                        } else if (positionOfKing[0] > positionOfQueen[0] && positionOfKing[1] < positionOfQueen[1]) {
                            for (int j = 1; j < Math.max(positionOfKing[0] - positionOfQueen[0], positionOfQueen[1] - positionOfKing[1]); j++) {
                                int[] pos = new int[]{positionOfQueen[0] + j, positionOfQueen[1] - j};
                                if (positionOfQueen[0] + j >= 0 && positionOfQueen[0] + j <= 7 &&
                                        positionOfQueen[1] - j >= 0 && positionOfQueen[1] - j <= 7) {
                                    if (getPiece(pos) != Piece.NONE) {
                                        allowed = true;
                                        break;
                                    }
                                    allowed = false;
                                }
                            }
                        } else if (positionOfKing[0] < positionOfQueen[0] && positionOfKing[1] > positionOfQueen[1]) {
                            for (int j = 1; j < Math.max(positionOfQueen[0] - positionOfKing[0], positionOfKing[1] - positionOfQueen[1]); j++) {
                                int[] pos = new int[]{positionOfQueen[0] - j, positionOfQueen[1] + j};
                                if (positionOfQueen[0] - j >= 0 && positionOfQueen[0] - j <= 7 &&
                                        positionOfQueen[1] + j >= 0 && positionOfQueen[1] + j <= 7) {
                                    if (getPiece(pos) != Piece.NONE) {
                                        allowed = true;
                                        break;
                                    }
                                    allowed = false;
                                }
                            }
                        } else if (positionOfKing[0] < positionOfQueen[0] && positionOfKing[1] < positionOfQueen[1]){
                            for (int j = 1; j < Math.max(positionOfQueen[0] - positionOfKing[0], positionOfQueen[1] - positionOfKing[1]); j++) {
                                int[] pos = new int[]{positionOfQueen[0] - j, positionOfQueen[1] - j};
                                if (positionOfQueen[0] - j >= 0 && positionOfQueen[0] - j <= 7 &&
                                        positionOfQueen[1] - j >= 0 && positionOfQueen[1] - j <= 7) {
                                    if (getPiece(pos) != Piece.NONE) {
                                        allowed = true;
                                        break;
                                    }
                                    allowed = false;
                                }
                            }
                        }
                        if (!allowed) {
                            Square.piece.put(from, piece);
                            Square.piece.put(to, pieceInTo);
                            return false;
                        }
                    }
                }
            }
        }

        Square.piece.put(from, piece);
        Square.piece.put(to, pieceInTo);
        return allowed;
    }


    public static boolean isAllowedMove(int[] initial, int[] position, boolean capture, String type){
        boolean allowed = true;
        if(type.equals("vertically")){
            for (int k = Math.min(initial[0], position[0]); k <= Math.max(initial[0], position[0]); k++) {
                if (k != initial[0]) {
                    if (getPiece(new int[]{k, position[1]}) != Piece.NONE) {
                        if(capture){
                            if(!Arrays.equals(new int[]{k, position[1]}, position)) {
                                allowed = false;
                                break;
                            }
                        } else {
                            allowed = false;
                            break;
                        }
                    }
                }
            }
        }
        else if(type.equals("horizontally")){
            for (int k = Math.min(initial[1], position[1]); k <= Math.max(initial[1], position[1]); k++) {
                if (k != initial[1]) {
                    if (getPiece(new int[]{position[0], k}) != Piece.NONE) {
                        if(capture){
                            if(!Arrays.equals(new int[]{position[0], k}, position)) {
                                allowed = false;
                                break;
                            }
                        } else {
                            allowed = false;
                            break;
                        }
                    }
                }
            }
        }
        else {
            if((initial[0] + initial[1]) % 2 == (position[0] + position[1]) % 2
                    && Math.abs(position[0]-initial[0]) == Math.abs(position[1]-initial[1])){
                if(position[0] > initial[0] && position[1] > initial[1]){
                    for (int j = 1; j < Math.max(position[0]-initial[0], position[1]-initial[1]); j++) {
                        int[] pos = new int[]{initial[0] + j, initial[1] + j};
                        if(initial[0] + j >=0 && initial[0] + j <=7 &&
                                initial[1] + j >=0 && initial[1] + j <= 7) {
                            if (getPiece(pos) != Piece.NONE) {
                                if(capture){
                                    if(!Arrays.equals(pos, position)) {
                                        allowed = false;
                                        break;
                                    }
                                } else {
                                    allowed = false;
                                    break;
                                }
                            }
                        }
                    }
                }
                else if(position[0] > initial[0] && position[1] < initial[1]){
                    for (int j = 1; j < Math.max(position[0]-initial[0], initial[1]-position[1]); j++) {
                        int[] pos = new int[]{initial[0] + j, initial[1] - j};
                        if(initial[0] + j >=0 && initial[0] + j <=7 &&
                                initial[1] - j >=0 && initial[1] - j <= 7) {
                            if (getPiece(pos) != Piece.NONE) {
                                if(capture){
                                    if(!Arrays.equals(pos, position)) {
                                        allowed = false;
                                        break;
                                    }
                                } else {
                                    allowed = false;
                                    break;
                                }
                            }
                        }
                    }
                }
                else if(position[0] < initial[0] && position[1] > initial[1]){
                    for (int j = 1; j < Math.max(initial[0]-position[0], position[1]-initial[1]); j++) {
                        int[] pos = new int[]{initial[0] - j, initial[1] + j};
                        if(initial[0] - j >=0 && initial[0] - j <=7 &&
                                initial[1] + j >=0 && initial[1] + j <= 7) {
                            if (getPiece(pos) != Piece.NONE) {
                                if(capture){
                                    if(!Arrays.equals(pos, position)) {
                                        allowed = false;
                                        break;
                                    }
                                } else {
                                    allowed = false;
                                    break;
                                }
                            }
                        }
                    }
                }
                else if(position[0] < initial[0] && position[1] < initial[1]){
                    for (int j = 1; j < Math.max(initial[0]-position[0], initial[1]-position[1]); j++) {
                        int[] pos = new int[]{initial[0] - j, initial[1] - j};
                        if(initial[0] - j >=0 && initial[0] - j <=7 &&
                                initial[1] - j >=0 && initial[1] - j <= 7) {
                            if (getPiece(pos) != Piece.NONE) {
                                if(capture){
                                    if(!Arrays.equals(pos, position)) {
                                        allowed = false;
                                        break;
                                    }
                                } else {
                                    allowed = false;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return allowed;
    }

}
