package modelo;

public class Method {

    public static Square getSquare(int[] position) {
        String name = (char) ('A' + position[1]) + String.valueOf(position[0] + 1);
        return Square.valueOf(name);
    }


    public static int[] getPosition(Square square) {
        return Square.position.get(square);
    }


    public static int[] getPosition(String s) {
        return getPosition(Square.valueOf(s.toUpperCase()));
    }

    public static Piece getPiece(int[] position) {
        return Square.piece.get(getSquare(position));
    }

    public static Piece getPiece(int i, char c) {
        boolean isWhite = (i % 2 == 0);
        String pieceStr = String.valueOf(c).toUpperCase();
        return switch (pieceStr) {
            case "P" -> isWhite ? Piece.WHITE_PAWN : Piece.BLACK_PAWN;
            case "R" -> isWhite ? Piece.WHITE_ROOK : Piece.BLACK_ROOK;
            case "N" -> isWhite ? Piece.WHITE_KNIGHT : Piece.BLACK_KNIGHT;
            case "B" -> isWhite ? Piece.WHITE_BISHOP : Piece.BLACK_BISHOP;
            case "Q" -> isWhite ? Piece.WHITE_QUEEN : Piece.BLACK_QUEEN;
            case "K" -> isWhite ? Piece.WHITE_KING : Piece.BLACK_KING;
            default -> null;
        };
    }


    public static char getNotationPiece(Piece piece) {
        return Piece.notation.get(piece);
    }


    public static char getNotationPiece(int[] position) {
        return getNotationPiece(getPiece(position));
    }


    public static void resetSquares() {
        for (Square s : Square.values()) {
            String squareName = s.toString();
            char file = squareName.charAt(0);
            char rank = squareName.charAt(1);

            Piece piece = switch (rank) {
                case '1' -> switch (file) {
                    case 'A', 'H' -> Piece.WHITE_ROOK;
                    case 'B', 'G' -> Piece.WHITE_KNIGHT;
                    case 'C', 'F' -> Piece.WHITE_BISHOP;
                    case 'D' -> Piece.WHITE_QUEEN;
                    case 'E' -> Piece.WHITE_KING;
                    default -> Piece.NONE;
                };
                case '8' -> switch (file) {
                    case 'A', 'H' -> Piece.BLACK_ROOK;
                    case 'B', 'G' -> Piece.BLACK_KNIGHT;
                    case 'C', 'F' -> Piece.BLACK_BISHOP;
                    case 'D' -> Piece.BLACK_QUEEN;
                    case 'E' -> Piece.BLACK_KING;
                    default -> Piece.NONE;
                };
                case '2' -> Piece.WHITE_PAWN;
                case '7' -> Piece.BLACK_PAWN;
                default -> Piece.NONE;
            };
            Square.piece.put(s, piece);
        }
    }
}

