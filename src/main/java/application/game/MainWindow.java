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
import application.dao.ConfigureManager;
import application.dao.DataOutputHandler;
import application.dao.DataType;
import application.dao.FeedbackType;
import application.dao.StimulusSender;
import application.gui.CirclesPanel;
import application.gui.Screen;
import application.gui.ImagePanel;
import application.util.ImageWrapper;
import application.util.ScreenGenerator;
import application.util.ScreenType;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class MainWindow extends Stage {
	private int totalGames;
	private final double width;
	private final double height;
	private int difficultyLvl;	
	private int gamesCounter;
	private long displayedMilliTime;
	private long interactedMilliTime;
	private Timeline timer;
	private Screen currentScreen;
	private Screen lastCirclesScreen;	
	private boolean userAnswer;
	private Map<String, Object> configValues;
	private StimulusSender stimSender;
	private final DataOutputHandler dataHandler;
	private final ScreenGenerator screenGenerator;
	
	public MainWindow(String configFileName, String dataFileName) throws FileNotFoundException {
		super();
		this.dataHandler = new DataOutputHandler(dataFileName);
		
		try {
			// reading configuration values
			configValues = (new ConfigureManager(configFileName)).getProperties();					
			totalGames = (int)configValues.get("number_of_games");
			difficultyLvl = (int) configValues.get("starting_difficulty_level");
			dataHandler.writeLine(getColumnsNames(), DataType.Title);
			stimSender = new StimulusSender((String)configValues.get("host"), (int)configValues.get("port"));	
			stimSender.open();	
		} catch (FileNotFoundException fof) {
			Logger.error(fof);
			throw fof;
		} catch (IOException io) {
			Logger.error(io);
		}
		
		var screenDim = javafx.stage.Screen.getPrimary().getBounds();
		width = screenDim.getWidth();
		height = screenDim.getHeight();
		screenGenerator = new ScreenGenerator(width, height, Color.BLACK);
		
		this.addEventFilter(KeyEvent.KEY_PRESSED, this::keyEventHandler);
	}
	
	private void keyEventHandler(KeyEvent e) {
		if(e.isControlDown() && e.getCode() == KeyCode.C)
			terminate();
		
		if (lastCirclesScreen != null && currentScreen.getType() == ScreenType.Blank) {
			if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.RIGHT) {
				// get the time passed between screen displayed and user interaction
				interactedMilliTime = System.currentTimeMillis();
				var containter = (Pane) lastCirclesScreen.getRoot();
				var hasMoreCircles = ((CirclesPanel) containter.getChildren().get(0))
						.greaterThan((CirclesPanel) containter.getChildren().get(2));
				userAnswer = e.getCode().toString().equalsIgnoreCase(hasMoreCircles.getSide().toString()) ? true : false;
				showNext();
			}
		}
	}
	
	private void createTimer(double millis) {
		Logger.info("Creating a new timer with " + Double.toString(millis) + "ms delay");
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
	
	/*
	 * Initiate a window and starts the app. This method has to be called externally
	 */
	public void start() {
		Logger.info("Constructing main window");
		gamesCounter = 0;
		userAnswer = false;
		
		currentScreen = screenGenerator.createCrossScreen(40, 8);

		this.initStyle(StageStyle.UNDECORATED);
		this.setTitle("Circles Game");
		this.setMaximized(true);
		this.setResizable(false);
		this.centerOnScreen();
		this.setScene(currentScreen);
		
		createTimer(3.5 * 1000);
		this.show();
	}
	
	private void showNext() {
		switch (currentScreen.getType()) {
		case Cross:
			saveResults(getData(), false);
			
			currentScreen = screenGenerator.createCirclesScreen(10, 15, difficultyLvl);
			createTimer(0.5 * 1000);
			break;
		case Circles:
			lastCirclesScreen = currentScreen;
			currentScreen = screenGenerator.createBlankPanel();
			interactedMilliTime = 0;
			displayedMilliTime = System.currentTimeMillis();
			createTimer(1.4 * 1000);
			break;
		case Blank:
			saveResults(getData(), false);
			
			// change difficulty
			if (userAnswer && difficultyLvl > 0)
				difficultyLvl--;
			else if (!userAnswer && difficultyLvl < 5)
				difficultyLvl++;
			
			currentScreen = screenGenerator.createImagesScreen(retrieveImageAttr(), userAnswer ? "You won!" : "You lost!");
			userAnswer = false;
			createTimer(1.5 * 1000);
			break;
		case Image:
			saveResults(getData(), true);

			currentScreen = screenGenerator.createCrossScreen(40, 8);
			gamesCounter++;
			
			createTimer(3.5 * 1000);
			break;
		}
		
		if(gamesCounter < totalGames) {
			Logger.info("Switching to " + currentScreen.getType() + " screen");
			this.setScene(currentScreen);		
			this.show();
		} else {
			terminate();
		}
	}
	
	private String getData() {
		Logger.info("Getting data for [" + currentScreen.getType() + "] screen");
		return 
			switch (currentScreen.getType()) {
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
					var containter = (Pane) lastCirclesScreen.getRoot();
					var circlesOnTheLeft = ((CirclesPanel) containter.getChildren().get(0)).getSpheresCount();
					var circlesOnTheRight = ((CirclesPanel) containter.getChildren().get(2)).getSpheresCount();
					yield (gamesCounter + 1) + ","
							+ (interactedMilliTime == 0 ? "no response" : (interactedMilliTime - displayedMilliTime)) + ","
							+ difficultyLvl + "," + circlesOnTheLeft + "," + circlesOnTheRight + "," + userAnswer + ",";
				}
				case Image -> {
					var imgPanel = (ImagePanel) currentScreen.getRoot();
					var imageName = imgPanel.getImageName().split("/")[1].split(".png")[0];
					yield imgPanel.getFeedbackType() + "," + imageName;
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
			stimSender.close();
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
	public String getColumnsNames() {	
		return Stream.of(configValues.get("columns"))
				.map(String::valueOf)
				.map(str -> str.replaceAll("[\\[\\]]", ""))
				.collect(Collectors.joining(","));
	}
}
