package modelo;

import java.util.LinkedHashMap;

public class Game{
    public Board board;
    private final LinkedHashMap<String, String> tags;
    private String[] stringMovesArray;
    private String strMovesText;

    Game(String event, String site, String date, String round, String whitePlayer, String blackPlayer,
         String result, String whiteTitle, String blackTitle, String whiteElo, String blackElo, String eco,
         String opening, String variation, String whiteFideId, String blackFideId, String eventDate,
         String termination, Board board, String strMovesText, String[] stringMovesArray){
        tags = new LinkedHashMap<>();
        this.setEvent(event);
        this.setSite(site);
        this.setDate(date);
        this.setRound(round);
        this.setWhitePlayer(whitePlayer);
        this.setBlackPlayer(blackPlayer);
        this.setResult(result);
        this.setWhiteTitle(whiteTitle);
        this.setBlackTitle(blackTitle);
        this.setWhiteElo(whiteElo);
        this.setBlackElo(blackElo);
        this.setEco(eco);
        this.setOpening(opening);
        this.setVariation(variation);
        this.setWhiteFideId(whiteFideId);
        this.setBlackFideId(blackFideId);
        this.setEventDate(eventDate);
        this.setTermination(termination);
        this.setStringMovesArray(stringMovesArray);
        this.board = board;
        this.setStrMovesText(strMovesText);
    }

    public void setEvent(String event) {
        tags.put("Event", event);
    }

    public void setSite(String site) {
        tags.put("Site", site);
    }

    public void setDate(String date) {
        tags.put("Date", date);
    }

    public void setRound(String round) {
        tags.put("Round", round);
    }

    public void setWhiteElo(String whiteElo) {
        tags.put("White Elo", whiteElo);
    }

    public void setBlackElo(String blackElo) {
        tags.put("Black Elo", blackElo);
    }

    public void setEco(String eco) {
        tags.put("ECO", eco);
    }

    public String getWhitePlayer() {
        return tags.get("White");
    }

    public void setWhitePlayer(String whitePlayer) {
        tags.put("White", whitePlayer);
    }

    public String getBlackPlayer() {
        return tags.get("Black");
    }

    public void setBlackPlayer(String blackPlayer) {
        tags.put("Black", blackPlayer);
    }

    public String getOpening() {
        return tags.get("Opening");
    }

    public void setOpening(String opening) {
        tags.put("Opening", opening);
    }

    public String getResult() {
        return tags.get("Result");
    }

    public void setResult(String result) {
        tags.put("Result", result);
    }

    public void setWhiteTitle(String whiteTitle) {
        tags.put("White Title", whiteTitle);
    }

    public void setBlackTitle(String blackTitle) {
        tags.put("Black Title", blackTitle);
    }

    public void setWhiteFideId(String whiteFideId) {
        tags.put("White Fide Id", whiteFideId);
    }

    public void setBlackFideId(String blackFideId) {
        tags.put("Black Fide Id", blackFideId);
    }

    public void setVariation(String variation) {
        tags.put("Variation", variation);
    }

    public void setEventDate(String eventDate) {
        tags.put("Event Date", eventDate);
    }

    public void setTermination(String termination) {
        tags.put("Termination", termination);
    }

    public String getStrMovesText() {
        return strMovesText;
    }

    public void setStrMovesText(String strMovesText) {
        this.strMovesText = strMovesText;
    }

    public String[] getStringMovesArray() {
        return stringMovesArray;
    }

    public void setStringMovesArray(String[] stringMovesArray) {
        this.stringMovesArray = stringMovesArray;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for(String tag: tags.keySet()){
            if(tags.get(tag) != null)
                result.append(tag).append(":   ").append(tags.get(tag)).append("\n");
        }
        if(result.toString().contains("\n")) {
            int lastNextLine = result.lastIndexOf("\n");
            return result.delete(lastNextLine, lastNextLine + 2).toString();
        } else {
            return result.toString();
        }
    }
}
