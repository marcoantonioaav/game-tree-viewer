import agent.ExampleAI;
import app.StartDesktopApp;
import manager.ai.AIRegistry;

public class App {
    public static void main(String[] args) {
        AIRegistry.registerAI("Example", () -> {return new ExampleAI();}, (game) -> {return true;});
        StartDesktopApp.main(new String[0]);
    }
}