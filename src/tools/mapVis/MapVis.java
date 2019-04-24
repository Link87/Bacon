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


    @Override
    public void start(Stage primaryStage) throws Exception {
        //init some stuff
        game = Game.getGame();
        game.readMap(asciString);
        game.getCurrentState().setMe(game.getCurrentState().getPlayerFromNumber(1));
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
            playerToggle[i].setUserData(game.getCurrentState().getPlayerFromNumber(i + 1));
            vBoxSettings.getChildren().add(playerToggle[i]);
        }

        currentPlayer = game.getCurrentState().getPlayerFromNumber(1);
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
        stoneSwichDialog = new ChoiceDialog<>(currentPlayer.getPlayerNumber(), playerChoices);
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
        vBoxSettings.getChildren().addAll(new Button("Test"));

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
        //this works - trust me
        String[] colors = {"red","orange","yellow","lime","lightgreen","lightblue","pink","brown"};

        for (int i = 0; i < 8; i++) {
            Tile other = target.getTransition(Direction.values()[i]);
            if (other != null) {
                labels[other.x][other.y].setStyle("-fx-background-color: "+colors[i]);
            }
        }


    }

    public void unHover(Label label) {
        Coords loca = xyFromLabel(label);
        Tile target = myMap.getTileAt(loca.x, loca.y);
        for (int i = 0; i < 8; i++) {
            Tile other = target.getTransition(Direction.values()[i]);
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
            move = MoveFactory.createMove(game.getCurrentState(), currentPlayer, loca.x, loca.y,
                    BonusRequest.fromValue(bonusRequest, game.getCurrentState()));
        } catch (Exception e) {
            move = new BombMove(game.getCurrentState(), currentPlayer, loca.x, loca.y);
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
                    move = new BombMove(game.getCurrentState(), currentPlayer, loca.x, loca.y);
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
                    Move rm = MoveFactory.createMove(game.getCurrentState(), game.getCurrentState().getPlayerFromNumber(cp), x, y);
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
        } else if (tile.getOwner() != null) {
            out = tile.getOwner().number + "";
        }
        return out;
    }

    //this is bad but it gets the job done
    public Coords xyFromLabel(Label lab) {
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

    public static String asciString ="8\n" +
            "4\n" +
            "2 3\n" +
            "42 42\n" +
            "0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 b b b b 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 4 5 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 1 8 0 0 0 0\n" +
            "0 0 0 0 5 4 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 8 1 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 2 4 3 1 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 c - 0 0 0 0 0 0 0 0 1 3 4 2 0 0 0 0 0 0 0 0 - c 0 0 0 0 0 0 0 0 0\n" +
            "- - - - - - - - - - - 0 0 0 0 0 0 0 0 3 1 2 4 0 0 0 0 0 0 0 0 - - - - - - - - - - -\n" +
            "0 0 0 0 0 0 0 0 0 0 0 i 0 0 0 0 0 0 0 4 2 1 3 0 0 0 0 0 0 0 i 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 b 0 0 0 0 0 0 0 0 0 0 b 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "b 0 0 0 0 0 0 0 6 8 7 5 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 6 8 7 5 0 0 0 0 b\n" +
            "b 0 0 0 0 0 0 0 5 7 8 6 0 0 0 0 0 0 0 0 - - 0 0 0 0 0 0 0 0 0 0 0 5 7 8 6 0 0 0 0 b\n" +
            "b 0 0 0 0 0 0 0 7 5 6 8 0 0 0 0 0 0 0 0 - - 0 0 0 0 0 0 0 0 0 0 0 7 5 6 8 0 0 0 0 b\n" +
            "b 0 0 0 0 0 0 0 8 6 5 7 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 8 6 5 7 0 0 0 0 b\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 b 0 0 0 0 0 0 0 0 0 0 b 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 0 i 0 0 0 0 0 0 0 2 4 3 1 0 0 0 0 0 0 0 i 0 0 0 0 0 0 0 0 0 0 0\n" +
            "- - - - - - - - - - - 0 0 0 0 0 0 0 0 1 3 4 2 0 0 0 0 0 0 0 0 - - - - - - - - - - -\n" +
            "0 0 0 0 0 0 0 0 0 c - 0 0 0 0 0 0 0 0 3 1 2 4 0 0 0 0 0 0 0 0 - c 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 4 2 1 3 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 2 7 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 3 6 0 0 0 0\n" +
            "0 0 0 0 7 2 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 6 3 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0\n" +
            "0 0 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 b b b b 0 0 0 0 0 0 0 0 - 0 0 0 0 0 0 0 0 0 0\n" +
            "9 0 2 <-> 32 0 6\n" +
            "9 1 2 <-> 32 1 6\n" +
            "9 2 2 <-> 32 2 6\n" +
            "9 3 2 <-> 32 3 6\n" +
            "9 4 2 <-> 32 4 6\n" +
            "9 5 2 <-> 32 5 6\n" +
            "9 6 2 <-> 32 6 6\n" +
            "9 7 2 <-> 32 7 6\n" +
            "9 8 2 <-> 32 8 6\n" +
            "9 9 2 <-> 32 9 6\n" +
            "9 1 1 <-> 32 0 5\n" +
            "9 2 1 <-> 32 1 5\n" +
            "9 3 1 <-> 32 2 5\n" +
            "9 4 1 <-> 32 3 5\n" +
            "9 5 1 <-> 32 4 5\n" +
            "9 6 1 <-> 32 5 5\n" +
            "9 7 1 <-> 32 6 5\n" +
            "9 8 1 <-> 32 7 5\n" +
            "9 9 1 <-> 32 8 5\n" +
            "9 0 3 <-> 32 1 7\n" +
            "9 1 3 <-> 32 2 7\n" +
            "9 2 3 <-> 32 3 7\n" +
            "9 3 3 <-> 32 4 7\n" +
            "9 4 3 <-> 32 5 7\n" +
            "9 5 3 <-> 32 6 7\n" +
            "9 6 3 <-> 32 7 7\n" +
            "9 7 3 <-> 32 8 7\n" +
            "9 8 3 <-> 32 9 7\n" +
            "9 32 2 <-> 32 32 6\n" +
            "9 33 2 <-> 32 33 6\n" +
            "9 34 2 <-> 32 34 6\n" +
            "9 35 2 <-> 32 35 6\n" +
            "9 36 2 <-> 32 36 6\n" +
            "9 37 2 <-> 32 37 6\n" +
            "9 38 2 <-> 32 38 6\n" +
            "9 39 2 <-> 32 39 6\n" +
            "9 40 2 <-> 32 40 6\n" +
            "9 41 2 <-> 32 41 6\n" +
            "9 33 1 <-> 32 32 5\n" +
            "9 34 1 <-> 32 33 5\n" +
            "9 35 1 <-> 32 34 5\n" +
            "9 36 1 <-> 32 35 5\n" +
            "9 37 1 <-> 32 36 5\n" +
            "9 38 1 <-> 32 37 5\n" +
            "9 39 1 <-> 32 38 5\n" +
            "9 40 1 <-> 32 39 5\n" +
            "9 41 1 <-> 32 40 5\n" +
            "9 32 3 <-> 32 33 7\n" +
            "9 33 3 <-> 32 34 7\n" +
            "9 34 3 <-> 32 35 7\n" +
            "9 35 3 <-> 32 36 7\n" +
            "9 36 3 <-> 32 37 7\n" +
            "9 37 3 <-> 32 38 7\n" +
            "9 38 3 <-> 32 39 7\n" +
            "9 39 3 <-> 32 40 7\n" +
            "9 40 3 <-> 32 41 7\n" +
            "0 9 4 <-> 0 32 0\n" +
            "1 9 4 <-> 1 32 0\n" +
            "2 9 4 <-> 2 32 0\n" +
            "3 9 4 <-> 3 32 0\n" +
            "4 9 4 <-> 4 32 0\n" +
            "5 9 4 <-> 5 32 0\n" +
            "6 9 4 <-> 6 32 0\n" +
            "7 9 4 <-> 7 32 0\n" +
            "8 9 4 <-> 8 32 0\n" +
            "9 9 4 <-> 9 32 0\n" +
            "1 9 5 <-> 0 32 1\n" +
            "2 9 5 <-> 1 32 1\n" +
            "3 9 5 <-> 2 32 1\n" +
            "4 9 5 <-> 3 32 1\n" +
            "5 9 5 <-> 4 32 1\n" +
            "6 9 5 <-> 5 32 1\n" +
            "7 9 5 <-> 6 32 1\n" +
            "8 9 5 <-> 7 32 1\n" +
            "9 9 5 <-> 8 32 1\n" +
            "0 9 3 <-> 1 32 7\n" +
            "1 9 3 <-> 2 32 7\n" +
            "2 9 3 <-> 3 32 7\n" +
            "3 9 3 <-> 4 32 7\n" +
            "4 9 3 <-> 5 32 7\n" +
            "5 9 3 <-> 6 32 7\n" +
            "6 9 3 <-> 7 32 7\n" +
            "7 9 3 <-> 8 32 7\n" +
            "8 9 3 <-> 9 32 7\n" +
            "32 9 4 <-> 32 32 0\n" +
            "33 9 4 <-> 33 32 0\n" +
            "34 9 4 <-> 34 32 0\n" +
            "35 9 4 <-> 35 32 0\n" +
            "36 9 4 <-> 36 32 0\n" +
            "37 9 4 <-> 37 32 0\n" +
            "38 9 4 <-> 38 32 0\n" +
            "39 9 4 <-> 39 32 0\n" +
            "40 9 4 <-> 40 32 0\n" +
            "41 9 4 <-> 41 32 0\n" +
            "33 9 5 <-> 32 32 1\n" +
            "34 9 5 <-> 33 32 1\n" +
            "35 9 5 <-> 34 32 1\n" +
            "36 9 5 <-> 35 32 1\n" +
            "37 9 5 <-> 36 32 1\n" +
            "38 9 5 <-> 37 32 1\n" +
            "39 9 5 <-> 38 32 1\n" +
            "40 9 5 <-> 39 32 1\n" +
            "41 9 5 <-> 40 32 1\n" +
            "32 9 3 <-> 33 32 7\n" +
            "33 9 3 <-> 34 32 7\n" +
            "34 9 3 <-> 35 32 7\n" +
            "35 9 3 <-> 36 32 7\n" +
            "36 9 3 <-> 37 32 7\n" +
            "37 9 3 <-> 38 32 7\n" +
            "38 9 3 <-> 39 32 7\n" +
            "39 9 3 <-> 40 32 7\n" +
            "40 9 3 <-> 41 32 7\n" +
            "9 32 1 <-> 32 9 5\n" +
            "9 9 3 <-> 32 32 7\n" +
            "0 0 0 <-> 20 19 4\n" +
            "0 0 6 <-> 19 20 2\n" +
            "0 0 7 <-> 19 19 3\n" +
            "41 0 1 <-> 22 19 5\n" +
            "41 0 0 <-> 21 19 4\n" +
            "41 0 2 <-> 22 20 6\n" +
            "41 41 3 <-> 22 22 7\n" +
            "41 41 2 <-> 22 21 6\n" +
            "41 41 4 <-> 21 22 0\n" +
            "0 41 5 <-> 19 22 1\n" +
            "0 41 4 <-> 20 22 0 \n" +
            "0 41 6 <-> 19 21 2";
}
class Coords {
    int x;
    int y;

    public Coords(int x, int y) {
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
