package application.game;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.tinylog.Logger;
import application.gui.CirclesPanel;
import application.gui.CirclesPanelContainer;
import application.gui.ImagePanel;
import application.gui.Panel;
import application.util.ConfigureManager;
import application.util.DataOutputHandler;
import application.util.DataType;
import application.util.FeedbackType;
import application.util.ScreenGenerator;
import application.util.ScreenType;
import application.util.StimulusSender;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Controller {
	private int totalGames;
	private final double width;
	private final double height;
	private int difficultyLvl;	
	private int gamesCounter;
	private long displayedMilliTime;
	private long interactedMilliTime;
	private Timeline timer;
	private Panel currentScreen;
	private Panel lastCirclesScreen;
	private StackPane panel;
	// main GUI component
	private Stage stage;	
	// represent the user's answer (right/wrong)
	private boolean answer;
	// represent all configuration values read from config.json
	private Map<String, Object> configValues;
	private StimulusSender sender;
	private final DataOutputHandler dataHandler;
	private final ScreenGenerator screenGenerator;
	
	public Controller(Stage stage, String configFileName, String dataFileName) throws FileNotFoundException {
		this.stage = stage;
		this.dataHandler = new DataOutputHandler(dataFileName);

		try {
			// reading configuration values
			configValues = (new ConfigureManager(configFileName)).getProperties();					
			totalGames = (int)configValues.get("number_of_games");
			difficultyLvl = (int) configValues.get("starting_difficulty_level");
			sender = new StimulusSender((String)configValues.get("host"), (int)configValues.get("port"));	
			sender.open();			
		} catch (FileNotFoundException fof) {
			Logger.error(fof);
			throw fof;
		} catch (IOException io) {
			Logger.error(io);
		}
		
		var screenDim = Screen.getPrimary().getBounds();
		width = screenDim.getWidth() / 2;
		height = screenDim.getHeight();
		screenGenerator = new ScreenGenerator(width, height);
		
		stage.addEventFilter(KeyEvent.KEY_PRESSED, this::keyEventHandler);
	}
	
	private void keyEventHandler(KeyEvent e) {
		if (lastCirclesScreen != null && currentScreen.getType() == ScreenType.Blank) {
			if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.RIGHT) {
				// get the time passed between screen displayed and user interaction
				interactedMilliTime = System.currentTimeMillis();
				var containter = (CirclesPanelContainer) lastCirclesScreen;
				var hasMoreCircles = ((CirclesPanel) containter.getInner().getChildren().get(0))
						.greaterThan((CirclesPanel) containter.getInner().getChildren().get(1));
				answer = e.getCode().toString().equalsIgnoreCase(hasMoreCircles.getSide().toString()) ? true : false;
				showNext();
			}
		}
	}
	
	private void createTimer(double millis) {
		Logger.info("creating a new timer with " + Double.toString(millis) + "ms delay");
		if (timer != null)
			timer.stop();
		timer = new Timeline(new KeyFrame(Duration.millis(millis), new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				showNext();
			}
		}));
		timer.setCycleCount(Timeline.INDEFINITE);
		timer.play();
	}
	
	public void show() {
		Logger.info("Constructing main screen");
		gamesCounter = 0;
		answer = false;
		
		panel = new StackPane();
		currentScreen = screenGenerator.createCrossScreen(Color.rgb(220, 220, 220), 40, 8);
		panel.getChildren().add((Pane)currentScreen);
		
		stage.setScene(new Scene(panel, width, height, Color.BLACK));
		stage.setMaximized(true);
		stage.setResizable(false);
		stage.centerOnScreen();
		stage.show();
		writeCriteria();
		
		createTimer(3.5 * 1000);
	}
	
	private void showNext() {
		switch (currentScreen.getType()) {
		case Cross:
			saveResults(getData(), false);
			
			currentScreen = screenGenerator.createCirclesScreen(10, 15, Color.rgb(220, 220, 220), difficultyLvl);
			createTimer(0.5 * 1000);
			break;
		case Circles:
			lastCirclesScreen = currentScreen;
			currentScreen = screenGenerator.createBlankPanel();
			displayedMilliTime = System.currentTimeMillis();
			interactedMilliTime = 0;
			createTimer(1.4 * 1000);
			break;
		case Blank:
			saveResults(getData(), false);
			
			// change difficulty
			if (answer && difficultyLvl > 0)
				difficultyLvl--;
			else if (!answer && difficultyLvl < 5)
				difficultyLvl++;
			
			currentScreen = screenGenerator.createImagesScreen(retrieveImageAttr(), answer ? "You won!" : "You lost!");
			answer = false;
			createTimer(1.5 * 1000);
			break;
		case Image:
			saveResults(getData(), true);

			currentScreen = screenGenerator.createCrossScreen(Color.rgb(220, 220, 220), 40, 8);
			gamesCounter++;
			
			
			createTimer(3.5 * 1000);
			break;
		}
		
		if(gamesCounter < totalGames) {
			Logger.info("Switching to " + currentScreen.getType().toString() + " screen");
			panel.getChildren().clear();
			panel.getChildren().add((Pane)currentScreen);			
		} else {
			terminate();
		}
	}
	
	private String getData() {
		Logger.info("Getting data for [" + currentScreen.getType() + "] screen");
		return switch (currentScreen.getType()) {
		case Cross -> {
			var session = "-";
			if (gamesCounter == 0)
				session = "start";
			else if (gamesCounter == totalGames - 1)
				session = "end";
			yield session + "," + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + ",";
		}
		case Circles -> {
			yield "";
		}
		case Blank -> {
			var containter = (CirclesPanelContainer) lastCirclesScreen;
			var circlesOnTheLeft = ((CirclesPanel) containter.getInner().getChildren().get(0)).getSpheresCount();
			var circlesOnTheRight = ((CirclesPanel) containter.getInner().getChildren().get(1)).getSpheresCount();
			yield (gamesCounter + 1) + ","
					+ (interactedMilliTime == 0 ? "No response"
							: (interactedMilliTime - displayedMilliTime))
					+ "," + difficultyLvl + "," + circlesOnTheLeft + ","
					+ circlesOnTheRight + "," + answer + ",";
		}
		case Image -> {
			var imgPanel = (ImagePanel) currentScreen;
			var imageName = imgPanel.getImageName().split("/")[1].split(".png")[0];
			yield imgPanel.getFeedbackType().toString() + "," + imageName;
		}
		};
	}
	
	public void terminate() {
		Logger.info("Terminating program");
		Platform.exit();
	}
	
	/*
	 * must be called upon destruction
	 */
	public void close() {
		try {
			dataHandler.close();
			sender.close();
		} catch (IOException e) {
			Logger.error(e);
		}
	}
	
	/*
	 * saving the result for a series of 3 games
	 */
	private void saveResults(String data, boolean endLine) {
		try {
			if(endLine)
				dataHandler.writeLine(data, DataType.Data);
			else
				dataHandler.write(data, DataType.Data);
		} catch (IOException e) {
			Logger.error(e);
		}
	}
	
	public ImageWrapper retrieveImageAttr() {
		var rand = new Random();	
		
		@SuppressWarnings("unchecked")	//casting Object into Map<String, Object>
		Map<String, Object> images = ((Map<String, Object>) configValues.get("images"));
		
		// get all image names
		String[] names = images
				.keySet()
				.stream()
				.toArray(String[]::new);
		
		// choose a random image
		String name = names[rand.nextInt(names.length)];
		return new ImageWrapper(name, FeedbackType.valueOf((String) images.get(name)));
	}
	
	/*
	 * Writes criteria columns into the corresponding .csv file
	 */
	public void writeCriteria() {	
		var title = Stream.of(configValues.get("columns"))
				.map(String::valueOf)
				.map(str -> str.replaceAll("[\\[\\]]", ""))
				.collect(Collectors.joining(","));
		

		try {
			dataHandler.writeLine(title, DataType.Title);
		} catch (IOException e) {
			Logger.error(e);
		}
	}
}
