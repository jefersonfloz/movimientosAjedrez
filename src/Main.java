import controlador.CheesController;
import vista.CheesView;
import vista.Vista;

public class Main {
    public static void main(String[] args) {
        Vista view = new Vista();
        CheesController controller = new CheesController(view);
        view.setVisible(true);
    }
}
