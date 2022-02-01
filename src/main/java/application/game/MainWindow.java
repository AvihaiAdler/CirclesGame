package application.game;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import application.util.ConfigValues;
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
  private ConfigValues configValues;
  private StimulusSender stimSender;
  private final DataOutputHandler dataHandler;
  private final ScreenGenerator screenGenerator;

  public MainWindow(String configFileName, String dataFileName) throws Exception {
    super();
    this.dataHandler = new DataOutputHandler(dataFileName);

    // reading configuration values
    try {
      configValues = (new ConfigureManager(configFileName)).getProperties();
      difficultyLvl = configValues.getDifficultyLvl();
      dataHandler.writeLine(getColumnsNames(), DataType.Title);
      stimSender = new StimulusSender(configValues.getHost(), configValues.getPort());
      stimSender.open();
    } catch (IOException io) {
      Logger.error(io);
    } catch (Exception e) {
      Logger.error(e);
      throw e;
    }

    var screenDim = javafx.stage.Screen.getPrimary().getBounds();
    width = screenDim.getWidth();
    height = screenDim.getHeight();
    screenGenerator = new ScreenGenerator(width, height, Color.BLACK);

    this.addEventFilter(KeyEvent.KEY_PRESSED, this::keyEventHandler);
  }

  private void keyEventHandler(KeyEvent e) {
    if (e.isControlDown() && e.getCode() == KeyCode.C)
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

    signal(100L, 0L);
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
        signal(5000L, 0L);
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
        signal(7000L, 0L);
        createTimer(1.5 * 1000);
        break;
      case Image:
        saveResults(getData(), true);
        
        currentScreen = screenGenerator.createCrossScreen(40, 8);
        gamesCounter++;

        createTimer(3.5 * 1000);
        break;
    }

    if (gamesCounter < configValues.getNumOfGames()) {
      Logger.info("Switching to " + currentScreen.getType() + " screen");
      this.setScene(currentScreen);
      this.show();
    } else {
      terminate();
    }
  }
  
  /*
   * must be called upon destruction
   */
  @Override
  public void close() {
    try {
      dataHandler.close();
      stimSender.close();
    } catch (IOException e) {
      Logger.error(e);
    }
  }
  
  public void terminate() {
    saveResults("", true);
    signal(200L, 0L);
    Logger.info("Terminating program");
    Platform.exit();
  }

  private String getData() {
    Logger.info("Getting data for [" + currentScreen.getType() + "] screen");
    return switch (currentScreen.getType()) {
      case Cross -> {
        var session = "-";
        if (gamesCounter == 0)
          session = "start";
        else if (gamesCounter == configValues.getNumOfGames() - 1)
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

  /*
   * saving the result for a series of 3 games
   */
  private void saveResults(String data, boolean endLine) {
    try {
      if (endLine)
        dataHandler.writeLine(data, DataType.Data);
      else
        dataHandler.write(data, DataType.Data);
    } catch (IOException e) {
      Logger.error(e);
    }
  }
  
  /*
   * send a signal to a server. 
   * 100 - app start
   * 200 - app shutdown
   * 5000 - stim for when the user chooses a screen
   * 7000 - stim for when the user gets the picture
   */
  public void signal(long signal, long timeStamp) {
    try {
      stimSender.send(signal, timeStamp);
    } catch (IOException e) {
      Logger.error(e);
    }
  }

  public ImageWrapper retrieveImageAttr() {
    var rand = new Random();

    var images = configValues.getImages();

    // get all image names
    String[] names = images.keySet().stream().toArray(String[]::new);

    // choose a random image
    String name = names[rand.nextInt(names.length)];
    return new ImageWrapper(name, FeedbackType.valueOf((String) images.get(name)));
  }

  public String getColumnsNames() {
    return Stream.of(configValues.getColumns())
            .map(String::valueOf)
            .collect(Collectors.joining(","));
  }
}
