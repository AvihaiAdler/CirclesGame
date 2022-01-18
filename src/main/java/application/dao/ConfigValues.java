package application.dao;

import java.util.Arrays;
import java.util.Map;

public class ConfigValues {
  private int numOfGames;
  private int difficultyLvl;
  private String host;
  private int port;
  private String[] columns;
  private Map<String, String> images;
  
  public ConfigValues() {
    
  }
  
  public ConfigValues(int numOfGames, int difficultyLvl, String host, int port, String[] columns,
          Map<String, String> images) {
    super();
    this.numOfGames = numOfGames;
    this.difficultyLvl = difficultyLvl;
    this.host = host;
    this.port = port;
    this.columns = columns;
    this.images = images;
  }
  
  public int getNumOfGames() {
    return numOfGames;
  }
  
  public void setNumOfGames(int numOfGames) {
    this.numOfGames = numOfGames;
  }
  
  public int getDifficultyLvl() {
    return difficultyLvl;
  }
  
  public void setDifficultyLvl(int difficultyLvl) {
    this.difficultyLvl = difficultyLvl;
  }
  
  public String getHost() {
    return host;
  }
  
  public void setHost(String host) {
    this.host = host;
  }
  
  public int getPort() {
    return port;
  }
  
  public void setPort(int port) {
    this.port = port;
  }
  
  public String[] getColumns() {
    return columns;
  }
  
  public void setColumns(String[] columns) {
    this.columns = columns;
  }
  public Map<String, String> getImages() {
    return images;
  }
  
  public void setImages(Map<String, String> images) {
    this.images = images;
  }
  
  @Override
  public String toString() {
    return "ConfigValues [number_of_games=" + numOfGames + ", starting_difficulty_level="
            + difficultyLvl + ", host=" + host + ", port=" + port + ", columns=" + Arrays.toString(columns)
            + ", images=" + images + "]";
  }
}
