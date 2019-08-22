package mapVis;

import bacon.move.*;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import bacon.*;
/**
 * MapVis is a tool to show a map and play on it?
 * currently most stuff is hardcodet
 */
public class MapVis extends Application {

    private bacon.Map myMap;
    private Label[][] labels;

    //things selected by toggles
    private Player currentPlayer;
    private MoveMode moveMode;
    private InteractionModes interactionMode;

    private Game game;

    //Layout
    private BorderPane border = new BorderPane();
    private VBox vBoxSettings = new VBox();
    private HBox hBoxInformation = new HBox();
    private GridPane grid = new GridPane();
    private Scene scene = new Scene(border, 800, 800);

    //cool buttons and stuff
    private ToggleGroup playerselection = new ToggleGroup();
    private RadioButton[] playerToggle;

    private ToggleGroup moveGroup = new ToggleGroup();
    private RadioButton[] moveToggle;

    private ToggleGroup modeGroup = new ToggleGroup();
    private RadioButton[] modeToggle;

    private ChoiceDialog<Integer> stoneSwichDialog;
    private ChoiceDialog<String> bombOrOverrideDialog;

    private Label overrideCount= new Label();
    private Label bombCount = new Label();

    private Label xPos = new Label("xPos");
    private  Label yPos = new Label("yPos");


    @Override
    public void start(Stage primaryStage) throws Exception {
        //init some stuff
        game = Game.getGame();
        game.readMap(asciiString);
        game.getCurrentState().setMe(1);
        myMap = game.getCurrentState().getMap();

        //making the map out of labels in a grid
        labels = new Label[myMap.width][myMap.height];
        for (int y = 0; y < myMap.width; y++) {
            for (int x = 0; x < myMap.height; x++) {
                Tile current = myMap.getTileAt(x, y);

                labels[x][y] = new Label(iconOf(current));
                labels[x][y].setStyle("-fx-background-color: white");
                labels[x][y].setMinWidth(16);
                labels[x][y].setMinHeight(16);
                labels[x][y].setAlignment(Pos.CENTER);

                //setting event listeners
                labels[x][y].setOnMouseEntered(e -> {
                    Label target = (Label) e.getSource();
                    hover(target);
                });
                labels[x][y].setOnMouseExited(e -> {
                    Label target = (Label) e.getSource();
                    unHover(target);
                });
                labels[x][y].setOnMouseClicked(e -> {
                    Label target = (Label) e.getSource();
                    click(target);
                });

                grid.setConstraints(labels[x][y], x, y);
                grid.getChildren().add(labels[x][y]);
            }
        }

        //set up Playerselction toggels
        vBoxSettings.getChildren().add(new Label("Playerselection"));
        int numberOfPlayers = game.getTotalPlayerCount();
        playerToggle = new RadioButton[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++) {
            playerToggle[i] = new RadioButton((i + 1) + "");
            playerToggle[i].setToggleGroup(playerselection);
            playerToggle[i].setUserData(game.getCurrentState().getPlayerFromId(i + 1));
            vBoxSettings.getChildren().add(playerToggle[i]);
        }

        currentPlayer = game.getCurrentState().getPlayerFromId(1);
        playerToggle[0].setSelected(true);

        playerselection.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observableValue, Toggle toggle, Toggle t1) {
                playerChange((Player) t1.getUserData());
            }
        });

        //set up move selction
        vBoxSettings.getChildren().add(new Label("Moveselection"));
        moveToggle = new RadioButton[3];
        moveToggle[0] = new RadioButton("Regular");
        moveToggle[0].setToggleGroup(moveGroup);
        moveToggle[0].setUserData(MoveMode.REGULAR);
        moveToggle[1] = new RadioButton("Override");
        moveToggle[1].setToggleGroup(moveGroup);
        moveToggle[1].setUserData(MoveMode.OVERRIDE);
        moveToggle[2] = new RadioButton("Bomb");
        moveToggle[2].setToggleGroup(moveGroup);
        moveToggle[2].setUserData(MoveMode.BOMB);
        moveMode = MoveMode.REGULAR;
        moveToggle[0].setSelected(true);
        vBoxSettings.getChildren().addAll(moveToggle[0], moveToggle[1], moveToggle[2]);

        moveGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observableValue, Toggle toggle, Toggle t1) {
                moveMode = (MoveMode) t1.getUserData();
            }
        });

        //set up modeGroup selection
        vBoxSettings.getChildren().add(new Label("Modeselection"));
        modeToggle = new RadioButton[3];
        modeToggle[0] = new RadioButton("GodMode");
        modeToggle[0].setToggleGroup(modeGroup);
        modeToggle[0].setUserData(InteractionModes.GODMODE);
        modeToggle[1] = new RadioButton("Normal");
        modeToggle[1].setToggleGroup(modeGroup);
        modeToggle[1].setUserData(InteractionModes.NORMAL);
        modeToggle[2] = new RadioButton("Rotating");
        modeToggle[2].setToggleGroup(modeGroup);
        modeToggle[2].setUserData(InteractionModes.ROTATING);
        interactionMode = InteractionModes.GODMODE;
        modeToggle[0].setSelected(true);
        vBoxSettings.getChildren().addAll(modeToggle[0], modeToggle[1], modeToggle[2]);

        modeGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observableValue, Toggle toggle, Toggle t1) {
                interactionMode = (InteractionModes) t1.getUserData();
            }
        });

        //set up swich stones with player allert
        List<Integer> playerChoices = new ArrayList<>();
        for (int i = 0; i < numberOfPlayers; i++) {
            playerChoices.add(i + 1);
        }
        stoneSwichDialog = new ChoiceDialog<>(currentPlayer.id, playerChoices);
        stoneSwichDialog.setTitle("Chose");
        stoneSwichDialog.setContentText("Chose a Player to switch stones with");

        //set up get Bomb or Override Stone Dialog
        List<String> playerGetChoices = new ArrayList<>();
        playerGetChoices.add("Override Stone");
        playerGetChoices.add("Bomb");
        bombOrOverrideDialog = new ChoiceDialog<>("Override Stone", playerGetChoices);
        bombOrOverrideDialog.setTitle("Chose");
        bombOrOverrideDialog.setContentText("Chose what you want to get");

        //set up Player information
        overrideCount.setText(currentPlayer.getOverrideStoneCount()+"");
        bombCount.setText(currentPlayer.getBombCount()+"");
        hBoxInformation.getChildren().addAll(new Label("Override Stones:"),overrideCount,new Label("Bomb Count:"),bombCount);

        //dont forget to add new stuff to the layout
        vBoxSettings.getChildren().addAll(xPos,yPos);

        border.setCenter(grid);
        border.setRight(vBoxSettings);
        border.setTop(hBoxInformation);

        primaryStage.setTitle("MapVis");
        primaryStage.setScene(scene);
        primaryStage.show();
        
    }

    public void hover(Label label) {
        Coords loca = xyFromLabel(label);
        Tile target = myMap.getTileAt(loca.x, loca.y);
        xPos.setText(loca.x+"");
        yPos.setText(loca.y+"");
        //this works - trust me
        String[] colors = {"red","orange","yellow","lime","lightgreen","lightblue","pink","brown"};

        for (int i = 0; i < 8; i++) {
            Tile other = target.getTransition(i);
            if (other != null) {
                labels[other.x][other.y].setStyle("-fx-background-color: "+colors[i]);
            }
        }


    }

    public void unHover(Label label) {
        Coords loca = xyFromLabel(label);
        Tile target = myMap.getTileAt(loca.x, loca.y);
        for (int i = 0; i < 8; i++) {
            Tile other = target.getTransition(i);
            if (other != null) {
                labels[other.x][other.y].setStyle("-fx-background-color: white");
            }
        }

    }

    public void click(Label label) {
        Coords loca = xyFromLabel(label);
        Tile target = myMap.getTileAt(loca.x, loca.y);
        int bonusRequest = 0;
        if (target.getProperty() == Tile.Property.CHOICE) {
            Optional<Integer> result = stoneSwichDialog.showAndWait();
            if (result.isPresent()) {
                bonusRequest = result.get();
            }
        } else if (target.getProperty() == Tile.Property.BONUS) {
            Optional<String> result = bombOrOverrideDialog.showAndWait();
            if (result.isPresent()) {
                if (result.get().equals("Bomb")) {
                    bonusRequest = 20;
                }
            }
        }
        Move move;
        try {
            move = MoveFactory.createMove(game.getCurrentState(), currentPlayer.id, loca.x, loca.y,
                    BonusRequest.fromValue(bonusRequest, game.getCurrentState()));
        } catch (Exception e) {
            move = new BombMove(game.getCurrentState(), currentPlayer.id, loca.x, loca.y);
        }
        if (interactionMode == InteractionModes.GODMODE) {
            switch (moveMode) {
                case REGULAR:
                    if (move instanceof RegularMove && move.isLegal()) move.doMove();
                    break;
                case OVERRIDE:
                    currentPlayer.receiveOverrideStone(1);
                    if (move instanceof OverrideMove && move.isLegal()) move.doMove();
                    else currentPlayer.receiveOverrideStone(-1);
                    break;
                case BOMB:
                    unHover(label);
                    currentPlayer.receiveBomb(1);
                    move = new BombMove(game.getCurrentState(), currentPlayer.id, loca.x, loca.y);
                    move.doMove();
                    break;
            }
        } else {
            switch (moveMode) {
                case REGULAR:
                    if (move instanceof RegularMove && move.isLegal()) move.doMove();
                    break;
                case OVERRIDE:
                    if (move instanceof OverrideMove && move.isLegal()) move.doMove();
                    break;
                case BOMB:
                    if (move instanceof BombMove && move.isLegal()) move.doMove();
                    break;
            }
        }
        if (interactionMode == InteractionModes.ROTATING) {
            Toggle selected = playerselection.getSelectedToggle();
            for (int i = 0; i < playerToggle.length; i++) {
                if (playerToggle[i] == selected) {
                    if (i < playerToggle.length - 1) {
                        playerToggle[i + 1].setSelected(true);
                    } else {
                        playerToggle[0].setSelected(true);
                    }
                    break;
                }
            }
        }
        redraw();
    }

    public void redraw() {
        for (int y = 0; y < myMap.width; y++) {
            for (int x = 0; x < myMap.height; x++) {
                Tile current = myMap.getTileAt(x, y);
                labels[x][y].setText(iconOf(current));
            }
        }
        overrideCount.setText(currentPlayer.getOverrideStoneCount()+"");
        bombCount.setText(currentPlayer.getBombCount()+"");
    }

    public void playerChange(Player newPlayer) {
        currentPlayer = newPlayer;
        overrideCount.setText(currentPlayer.getOverrideStoneCount()+"");
        bombCount.setText(currentPlayer.getBombCount()+"");
    }

    /**
     * colors and return all legal override moves currently
     *
     * @param cp
     * @return
     */
    public ArrayList<Move> getLegalMoves(int cp) {
        ArrayList<Move> moves = new ArrayList<>();
        for (int x = 0; x < myMap.width; x++) {
            for (int y = 0; y < myMap.height; y++) {
                try {
                    Move rm = MoveFactory.createMove(game.getCurrentState(), cp, x, y);
                    if (rm instanceof OverrideMove && rm.isLegal()) {
                        labels[x][y].setStyle("-fx-background-color: green");
                        moves.add(rm);
                    }
                } catch (Exception e) {

                }
            }
        }
        return moves;
    }

    public String iconOf(Tile tile) {
        String out = "0";
        if (tile.getProperty() == Tile.Property.HOLE) {
            out = "-";
        } else if (tile.getProperty() == Tile.Property.EXPANSION) {
            out = "x";
        } else if (tile.getProperty() == Tile.Property.CHOICE) {
            out = "c";
        } else if (tile.getProperty() == Tile.Property.BONUS) {
            out = "b";
        } else if (tile.getProperty() == Tile.Property.INVERSION) {
            out = "i";
        } else if (tile.getOwnerId() != Player.NULL_PLAYER_ID) {
            out = tile.getOwnerId() + "";
        }
        return out;
    }

    //this is bad but it gets the job done
    private Coords xyFromLabel(Label lab) {
        for (int y = 0; y < myMap.height; y++) {
            for (int x = 0; x < myMap.width; x++) {
                if (lab.equals(labels[x][y])) {
                    return new Coords(x, y);
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static String asciiString ="8\n" +
            "4\n" +
            "1 4\n" +
            "20 20\n" +
            "- - - - - - 8 4 0 0 0 0 3 7 - - - - - -\n" +
            "- - - - - 0 4 8 0 0 0 0 7 3 0 - - - - -\n" +
            "- - - - 0 0 0 0 0 0 0 0 0 0 0 0 - - - -\n" +
            "- - - 0 0 i 0 0 0 0 0 0 0 0 0 0 0 - - -\n" +
            "- - 0 0 0 0 0 0 0 0 x 0 0 0 0 0 0 0 - -\n" +
            "- 0 0 0 0 0 0 0 0 x 0 0 0 0 0 0 0 0 0 -\n" +
            "1 5 0 0 0 x 0 0 0 0 0 0 0 0 x 0 0 0 2 6\n" +
            "5 1 0 0 0 - 0 0 0 0 0 0 0 0 - 0 0 0 6 2\n" +
            "0 0 0 0 - - - 0 0 0 0 0 0 - - - 0 0 0 0\n" +
            "0 0 0 - - - - - 0 c b 0 - - - - - 0 0 0\n" +
            "0 0 0 - - - - - 0 b c 0 - - - - - 0 0 0\n" +
            "0 0 0 0 - - - 0 0 0 0 0 0 - - - 0 0 0 0\n" +
            "6 2 0 0 0 - 0 0 0 0 0 0 0 0 - 0 0 0 5 1\n" +
            "2 6 0 0 0 x 0 0 0 0 0 0 0 0 x 0 0 0 1 5\n" +
            "- 0 0 0 0 0 0 0 0 x 0 0 0 0 0 0 0 0 0 -\n" +
            "- - 0 0 0 0 0 0 0 0 x 0 0 0 0 0 0 0 - -\n" +
            "- - - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - - -\n" +
            "- - - - 0 0 0 0 0 0 0 0 0 0 0 0 - - - -\n" +
            "- - - - - 0 7 3 0 0 0 0 4 8 0 - - - - -\n" +
            "- - - - - - 3 7 0 0 0 0 8 4 - - - - - -\n" +
            "1 5 7 <-> 18 14 3\n" +
            "2 4 7 <-> 17 15 3\n" +
            "3 3 7 <-> 16 16 3\n" +
            "4 2 7 <-> 15 17 3\n" +
            "5 1 7 <-> 14 18 3\n" +
            "1 14 5 <-> 14 1 1\n" +
            "2 15 5 <-> 15 2 1\n" +
            "3 16 5 <-> 16 3 1\n" +
            "4 17 5 <-> 17 4 1\n" +
            "5 18 5 <-> 18 5 1";
}
class Coords {
    int x;
    int y;

    Coords(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

enum InteractionModes{
    GODMODE,
    NORMAL,
    ROTATING
}

enum MoveMode{
    REGULAR,
    OVERRIDE,
    BOMB
}
