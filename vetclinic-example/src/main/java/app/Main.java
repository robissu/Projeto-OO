package app;

import app.ui.AppointmentUI;
import app.ui.OwnerUI;
import app.ui.PetUI;
import app.ui.VetUI;
import framework.dao.DBConnection;
import framework.exceptions.FrameworkException;
import framework.ui.Action;
import framework.ui.Menu;

/**
 * Main – Ponto de entrada da aplicação Clínica Veterinária.
 *
 * <p>Sequência de inicialização:
 * <ol>
 *   <li>Abre a conexão JDBC com o SQLite</li>
 *   <li>Cria as tabelas (se ainda não existirem)</li>
 *   <li>Exibe o menu principal e aguarda interação do usuário</li>
 *   <li>Fecha a conexão ao sair</li>
 * </ol>
 */
public class Main {

    public static void main(String[] args) {
        try {
            // 1. Abre o banco de dados (cria o arquivo se não existir)
            DBConnection.getInstance().open("jdbc:sqlite:vetclinic.db");

            // 2. Cria as tabelas na primeira execução
            SchemaInitializer.initialize();

            // 3. Monta e executa o menu principal
            var mainMenu = new Menu("SISTEMA DE GESTÃO — CLÍNICA VETERINÁRIA", "Sair");
            mainMenu.addItem(new Action("Tutores",        () -> new OwnerUI().run()));
            mainMenu.addItem(new Action("Animais",        () -> new PetUI().run()));
            mainMenu.addItem(new Action("Veterinários",   () -> new VetUI().run()));
            mainMenu.addItem(new Action("Consultas",      () -> new AppointmentUI().run()));
            mainMenu.run();

        } catch (FrameworkException e) {
            System.err.println("\n[ERRO CRÍTICO] " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("\n[ERRO INESPERADO] " + e.getMessage());
            e.printStackTrace();
            System.exit(2);
        } finally {
            DBConnection.getInstance().close();
            System.out.println("\n  Até logo!\n");
        }
    }
}
