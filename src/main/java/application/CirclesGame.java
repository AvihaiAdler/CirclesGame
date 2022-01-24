package application;

import org.tinylog.Logger;
import application.game.MainWindow;
import javafx.application.Application;
import javafx.stage.Stage;
	
public class CirclesGame extends Application {
	private MainWindow app;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.close();
		Logger.info("Starting circles game");
		app = new MainWindow("config.json", "./data/circles_game.csv");
		app.start();
	}
	
	@Override
	public void stop() {
//		app.close();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
