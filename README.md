## Circles Game Application

This app, along with this [app](https://github.com/AvihaiAdler/LettersGame) developed as a tool for a research based on the following [article](https://www.researchgate.net/publication/23151714_Vrticka_P_Andersson_F_Grandjean_D_Sander_D_Vuilleumier_P_Individual_attachment_style_modulates_human_amygdala_and_striatum_activation_during_social_appraisal_PLoS_ONE_3_e2868). Specifically `Circles Game Application` is an implementation of the tool described in that article above.

### General description

The app consists of 4 alternating screens (excluding the welcome screen):

- a simple cross indicates `get-ready`.
- a divided screen with different number of circles of each side. The user must identify the side with the higher number of circles
- a black screen. The user make their 'choice' here with their keyboard keys. Left arrow for the left screen, right for the right
- a feedback screen, at which the user gets a random picture and a text based on their earlier choice i.e. if the user chose the correct side they'll get: a random image + `you won` text. If they chose the wrong side they'll get a random image + `you lost` text.

### Functionallity

As mentioned the user should make their choice during the 3rd screen.
At any point in time, pressing `ctrl + c` will terminate the app.
Each correct choice will increase the difficulty of the next game. Each wrong choice will lower it as described in the [article](https://www.researchgate.net/publication/23151714_Vrticka_P_Andersson_F_Grandjean_D_Sander_D_Vuilleumier_P_Individual_attachment_style_modulates_human_amygdala_and_striatum_activation_during_social_appraisal_PLoS_ONE_3_e2868).

The app saves some data from each game into a `.csv` file under `data/`. The data is saved in the following format:
`| Session | TimeStamp | Game# | Response | Difficulty | Circles_count_left | Circles_count_right | User Answer | Visual feedback | Image name |`

Where:

- `Session`: Represent the start/end of a session. Session in X number of games runs in a single app run
- `TimeStamp`: Represent the start of a game (in ISO-8601 format)
- `Game#` : Represent the number of the game
- `Response`: the time in millis since the user get the 3rd screen until they press a button (made a choice)
- `Difficulty`: Represent the difference between the number of circles on each side. the 'higher' the number - the easier it gets
- `Circles_count_left`/`Circles_count_right`: self explanatory
- `User Answer`: True/False, whether the user was right with their choice or not
- `Visual feedback`: positive/negative, the type of feedback the user got
- `Image name`: the name of the image presented to the user

In addition to the data, the app sends signals via a TCP socket at certain points. The signals are pretty much arbitrary numbers and suppose to interface with an [OpenVibe server](http://openvibe.inria.fr/).
The signals are sent upon:

- app start
- the user presented with the circle screen
- the user presented with the 3rd screen (i.e. when they suppose to make their choice)
- app shutdown

### Configuration

The app can be configured to some extent. All app related configuration values are located in `src/main/resources/config.json`.
Note that you can change the names of each column in the .csv file, however you can't change what data will be gathered. If you add a column to the list/remove one - it'll not affect the actual data you'll get.

The images can be changed too. To change them, make sure you place them in `src/main/resources/images`, and change the values in `config.json` under "images" to: `"image/<your_image_name>.<image_extention>": "<feedback_type>"` (`<feedback_type>` can be either "positive" or "negative", any other strings will break the app and will cause it to crash).

The `host` & `port` are used to established a connection to the `OpenVibe` server and can be changed to fit your preference.

### Build

To build the app, download the source code. Make sure you have [maven](https://maven.apache.org/download.cgi) installed. Open a command line in the root directory of the app and type `mvn compile assembly:single`, this will create a [fatJar](https://stackoverflow.com/questions/19150811/what-is-a-fat-jar) under `target/`.

### Setup

This section is meant for testing purposes as i know nothing about OpenVibe.

- OpenVibe requires python to work. Make sure you have it installed and configured as a PATH variable

To setup OpenVibe for testing you'll need to setup an `acquisition server` and a `designer`. The server
receives data from external applications through a TCP connection and sends it to the Designer. The Designer
process the data using various 'boxes'.

##### Acquisition Server

- open an acquisition server instance
- Driver: for testing purposes a Generic Oscillator should be fine
- Connection port: choose a port, the port number must match the port in the Designer
- Sample count per set block: set to 32
- Preferences: the port the server uses to communicate with the app is under TCP_Tagging_Port. Make sure it matches the port in the app's `config.json` file
- press Connect to initiate the server
- press Play to start send/receive data

##### Designer

- open a Designer instance
- drag & drop an `Acquisition Client` box
- make sure it listens to the port the Acquisition Server is set to, you can check the port by double clicking on the box
- drag & drop a `Signal Display` box
- connect the 2 boxes by drawing the lines between the Client's `Signal` output to the Signal's
  Display `Signal` input, and between the Client's `Stimulations` output to the Signal's Display `Stimulations`
  input
