package mapVis;

import bacon.move.BombMove;
import bacon.move.Move;
import bacon.move.OverrideMove;
import bacon.move.RegularMove;
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
        game.processMessage(hexString);
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
            move = Move.createNewMove(0, myMap, currentPlayer, loca.x, loca.y, bonusRequest);
        } catch (Exception e) {
            move = new BombMove(0, myMap, currentPlayer, loca.x, loca.y, bonusRequest);
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
                    move = new BombMove(0, myMap, currentPlayer, loca.x, loca.y, bonusRequest);
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
                    Move rm = Move.createNewMove(0, myMap, game.getCurrentState().getPlayerFromNumber(cp), x, y, 0);
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

    public static String hexString3 = "0200000034380a360a3120320a33332033330a30202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d20300a2d20302030202d202d202d202d202d20302030203020302030203020302030203020302030203020302030203020302030202d202d202d202d202d20302030202d0a2d20302030202d202d202d202d202d20302036203820302030203020302030206320302030203020302030203520372030202d202d202d202d202d20302030202d0a2d202d202d2062203020302062202d20302038203620302030203020302030206320302030203020302030203720352030202d2062203020302062202d202d202d0a2d202d202d2030203620352030202d20302030203020302030203020302030203020302030203020302030203020302030202d2030203720382030202d202d202d0a2d202d202d2030203520362030202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d2030203820372030202d202d202d0a2d202d202d2062203020302062202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d2062203020302062202d202d202d0a2d202d202d202d202d202d202d2030203020302030203020302030203020302030203020302030203020302030203020302030202d202d202d202d202d202d202d0a2d2030203020302030202d202d2030203020302030203020302030203020302030203020302030203020302030203020302030202d202d2030203020302030202d0a2d2030203020302030202d202d2030203020302030203020302030203020302030203020302030203020302030203020302030202d202d2030203020302030202d0a2d2030203020302030202d202d2030203020302030203020302030203020302030203020302030203020302030203020302030202d202d2030203020302030202d0a2d2030203020302030202d202d2030203020302030203020302030203020302030203020302030203020302030203020302030202d202d2030203020302030202d0a2d2030203020302030202d202d2030203020302030203020302030203020302030203020302030203020302030203020302030202d202d2030203020302030202d0a2d2030203020302030202d202d2030203020302030203020302030203020302030203020302030203020302030203020302030202d202d2030203020302030202d0a2d2030203020302030202d202d2030203020302030203020302030203020302030203020302030203020302030203020302030202d202d2030203020302030202d0a2d2030203020302030202d202d2030203020302030203020302030203020302069203020302030203020302030203020302030202d202d2030203020302030202d0a2d2030203020302030202d202d203020302030203020302030203020302069202d206920302030203020302030203020302030202d202d2030203020302030202d0a2d2030203020302030202d202d2030203020302030203020302030203020302069203020302030203020302030203020302030202d202d2030203020302030202d0a2d2030203020302030202d202d2030203020302030203020302030203020302030203020302030203020302030203020302030202d202d2030203020302030202d0a2d2030203020302030202d202d2030203020302030203020302030203020302030203020302030203020302030203020302030202d202d2030203020302030202d0a2d2030203020302030202d202d2030203020302030203020302030203020302030203020302030203020302030203020302030202d202d2030203020302030202d0a2d2030203020302030202d202d2030203020302030203020302030203020302030203020302030203020302030203020302030202d202d2030203020302030202d0a2d2030203020302030202d202d2030203020302030203020302030203020302030203020302030203020302030203020302030202d202d2030203020302030202d0a2d2030203020302030202d202d2030203020302030203020302030203020302030203020302030203020302030203020302030202d202d2030203020302030202d0a2d2030203020302030202d202d2030203020302030203020302030203020302030203020302030203020302030203020302030202d202d2030203020302030202d0a2d202d202d202d202d202d202d2030203020302030203020302030203020302030203020302030203020302030203020302030202d202d202d202d202d202d202d0a2d202d202d2062203020302062202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d2062203020302062202d202d202d0a2d202d202d2030203320342030202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d2030203220312030202d202d202d0a2d202d202d2030203420332030202d20302030203020302030203020302030203020302030203020302030203020302030202d2030203120322030202d202d202d0a2d202d202d2062203020302062202d20302034203220302030203020302030206320302030203020302030203320312030202d2062203020302062202d202d202d0a2d20302030202d202d202d202d202d20302032203420302030203020302030206320302030203020302030203120332030202d202d202d202d202d20302030202d0a2d20302030202d202d202d202d202d20302030203020302030203020302030203020302030203020302030203020302030202d202d202d202d202d20302030202d0a30202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d202d20300a3020302030203c2d3e203020333220340a3020302036203c2d3e203332203020320a302033322036203c2d3e20333220333220320a33322033322034203c2d3e203332203020300a312032342034203c2d3e203820323820360a322032342034203c2d3e203820323920360a332032342034203c2d3e203820333020360a342032342034203c2d3e203820333120360a32382032342034203c2d3e20323420323820320a32392032342034203c2d3e20323420323920320a33302032342034203c2d3e20323420333020320a33312032342034203c2d3e20323420333120320a3120382030203c2d3e2038203120360a3220382030203c2d3e2038203220360a3320382030203c2d3e2038203320360a3420382030203c2d3e2038203420360a323820382030203c2d3e203234203120320a323920382030203c2d3e203234203220320a333020382030203c2d3e203234203320320a333120382030203c2d3e203234203420320a3620362034203c2d3e20313520313620320a362032362030203c2d3e20313620313520340a32362032362030203c2d3e20313720313620360a323620362034203c2d3e2031362031372030";
    public static String hexString2 = "0200000034330a340a3120310a31352031350a2d202d202d202d202d20302030203220322032202d202d202d202d202d0a2d202d202d202d202d20302032203120322033202d202d202d202d202d0a2d202d202d202d202d20302033203020302030202d202d202d202d202d0a2d202d202d202d202d20302032203020692030202d202d202d202d202d0a2d202d202d202d202d20302032203020302030202d202d202d202d202d0a30203020302030203020302032203020302030203020302030203020300a30206320302030203020302031203220332030206920302030203020300a30203020302030203020302033203120322030203020302030203020300a30203020302062203020302032203320312030203020302030203020300a30203020302030203020302030203020302030203020302062203020300a2d202d202d202d202d20302030207820302030202d202d202d202d202d0a2d202d202d202d202d20302078207820782030202d202d202d202d202d0a2d202d202d202d202d20302030207820632030202d202d202d202d202d0a2d202d202d202d202d20302030203020302030202d202d202d202d202d0a2d202d202d202d202d20302030203020302030202d202d202d202d202d0a3620302030203c2d3e2039203020320a372031342034203c2d3e203720302030";
    public static String hexString = "0200000034380a340a3220330a34322034320a30203020302030203020302030203020302030202d20302030203020302030203020302030206220622062206220302030203020302030203020302030202d20302030203020302030203020302030203020300a30203020302030203020302030203020302030202d20302030203020302030203020302030203020302030203020302030203020302030203020302030202d20302030203020302030203020302030203020300a30203020302030203020302030203020302030202d20302030203020302030203020302030203020302030203020302030203020302030203020302030202d20302030203020302030203020302030203020300a30203020302030203020302030203020302030202d20302030203020302030203020302030203020302030203020302030203020302030203020302030202d20302030203020302030203020302030203020300a30203020302030203420352030203020302030202d20302030203020302030203020302030203020302030203020302030203020302030203020302030202d20302030203020302031203820302030203020300a30203020302030203520342030203020302030202d20302030203020302030203020302030203020302030203020302030203020302030203020302030202d20302030203020302038203120302030203020300a30203020302030203020302030203020302030202d20302030203020302030203020302030203020302030203020302030203020302030203020302030202d20302030203020302030203020302030203020300a30203020302030203020302030203020302030202d20302030203020302030203020302030203020302030203020302030203020302030203020302030202d20302030203020302030203020302030203020300a30203020302030203020302030203020302030202d20302030203020302030203020302030203220342033203120302030203020302030203020302030202d20302030203020302030203020302030203020300a30203020302030203020302030203020302063202d20302030203020302030203020302030203120332034203220302030203020302030203020302030202d20632030203020302030203020302030203020300a2d202d202d202d202d202d202d202d202d202d202d20302030203020302030203020302030203320312032203420302030203020302030203020302030202d202d202d202d202d202d202d202d202d202d202d0a30203020302030203020302030203020302030203020692030203020302030203020302030203420322031203320302030203020302030203020302069203020302030203020302030203020302030203020300a30203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020300a30203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020300a30203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020300a30203020302030203020302030203020302030203020302030203020302062203020302030203020302030203020302030203020622030203020302030203020302030203020302030203020302030203020300a30203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020300a30203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020300a30203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020300a62203020302030203020302030203020362038203720352030203020302030203020302030203020302030203020302030203020302030203020302030203020302036203820372035203020302030203020620a622030203020302030203020302030203520372038203620302030203020302030203020302030202d202d203020302030203020302030203020302030203020302035203720382036203020302030203020620a622030203020302030203020302030203720352036203820302030203020302030203020302030202d202d203020302030203020302030203020302030203020302037203520362038203020302030203020620a62203020302030203020302030203020382036203520372030203020302030203020302030203020302030203020302030203020302030203020302030203020302038203620352037203020302030203020620a30203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020300a30203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020300a30203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020300a30203020302030203020302030203020302030203020302030203020302062203020302030203020302030203020302030203020622030203020302030203020302030203020302030203020302030203020300a30203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020300a30203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020300a30203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020302030203020300a30203020302030203020302030203020302030203020692030203020302030203020302030203220342033203120302030203020302030203020302069203020302030203020302030203020302030203020300a2d202d202d202d202d202d202d202d202d202d202d20302030203020302030203020302030203120332034203220302030203020302030203020302030202d202d202d202d202d202d202d202d202d202d202d0a30203020302030203020302030203020302063202d20302030203020302030203020302030203320312032203420302030203020302030203020302030202d20632030203020302030203020302030203020300a30203020302030203020302030203020302030202d20302030203020302030203020302030203420322031203320302030203020302030203020302030202d20302030203020302030203020302030203020300a30203020302030203020302030203020302030202d20302030203020302030203020302030203020302030203020302030203020302030203020302030202d20302030203020302030203020302030203020300a30203020302030203020302030203020302030202d20302030203020302030203020302030203020302030203020302030203020302030203020302030202d20302030203020302030203020302030203020300a30203020302030203220372030203020302030202d20302030203020302030203020302030203020302030203020302030203020302030203020302030202d20302030203020302033203620302030203020300a30203020302030203720322030203020302030202d20302030203020302030203020302030203020302030203020302030203020302030203020302030202d20302030203020302036203320302030203020300a30203020302030203020302030203020302030202d20302030203020302030203020302030203020302030203020302030203020302030203020302030202d20302030203020302030203020302030203020300a30203020302030203020302030203020302030202d20302030203020302030203020302030203020302030203020302030203020302030203020302030202d20302030203020302030203020302030203020300a30203020302030203020302030203020302030202d20302030203020302030203020302030203020302030203020302030203020302030203020302030202d20302030203020302030203020302030203020300a30203020302030203020302030203020302030202d20302030203020302030203020302030206220622062206220302030203020302030203020302030202d20302030203020302030203020302030203020300a3920302032203c2d3e203332203020360a3920312032203c2d3e203332203120360a3920322032203c2d3e203332203220360a3920332032203c2d3e203332203320360a3920342032203c2d3e203332203420360a3920352032203c2d3e203332203520360a3920362032203c2d3e203332203620360a3920372032203c2d3e203332203720360a3920382032203c2d3e203332203820360a3920392032203c2d3e203332203920360a3920312031203c2d3e203332203020350a3920322031203c2d3e203332203120350a3920332031203c2d3e203332203220350a3920342031203c2d3e203332203320350a3920352031203c2d3e203332203420350a3920362031203c2d3e203332203520350a3920372031203c2d3e203332203620350a3920382031203c2d3e203332203720350a3920392031203c2d3e203332203820350a3920302033203c2d3e203332203120370a3920312033203c2d3e203332203220370a3920322033203c2d3e203332203320370a3920332033203c2d3e203332203420370a3920342033203c2d3e203332203520370a3920352033203c2d3e203332203620370a3920362033203c2d3e203332203720370a3920372033203c2d3e203332203820370a3920382033203c2d3e203332203920370a392033322032203c2d3e20333220333220360a392033332032203c2d3e20333220333320360a392033342032203c2d3e20333220333420360a392033352032203c2d3e20333220333520360a392033362032203c2d3e20333220333620360a392033372032203c2d3e20333220333720360a392033382032203c2d3e20333220333820360a392033392032203c2d3e20333220333920360a392034302032203c2d3e20333220343020360a392034312032203c2d3e20333220343120360a392033332031203c2d3e20333220333220350a392033342031203c2d3e20333220333320350a392033352031203c2d3e20333220333420350a392033362031203c2d3e20333220333520350a392033372031203c2d3e20333220333620350a392033382031203c2d3e20333220333720350a392033392031203c2d3e20333220333820350a392034302031203c2d3e20333220333920350a392034312031203c2d3e20333220343020350a392033322033203c2d3e20333220333320370a392033332033203c2d3e20333220333420370a392033342033203c2d3e20333220333520370a392033352033203c2d3e20333220333620370a392033362033203c2d3e20333220333720370a392033372033203c2d3e20333220333820370a392033382033203c2d3e20333220333920370a392033392033203c2d3e20333220343020370a392034302033203c2d3e20333220343120370a3020392034203c2d3e203020333220300a3120392034203c2d3e203120333220300a3220392034203c2d3e203220333220300a3320392034203c2d3e203320333220300a3420392034203c2d3e203420333220300a3520392034203c2d3e203520333220300a3620392034203c2d3e203620333220300a3720392034203c2d3e203720333220300a3820392034203c2d3e203820333220300a3920392034203c2d3e203920333220300a3120392035203c2d3e203020333220310a3220392035203c2d3e203120333220310a3320392035203c2d3e203220333220310a3420392035203c2d3e203320333220310a3520392035203c2d3e203420333220310a3620392035203c2d3e203520333220310a3720392035203c2d3e203620333220310a3820392035203c2d3e203720333220310a3920392035203c2d3e203820333220310a3020392033203c2d3e203120333220370a3120392033203c2d3e203220333220370a3220392033203c2d3e203320333220370a3320392033203c2d3e203420333220370a3420392033203c2d3e203520333220370a3520392033203c2d3e203620333220370a3620392033203c2d3e203720333220370a3720392033203c2d3e203820333220370a3820392033203c2d3e203920333220370a333220392034203c2d3e20333220333220300a333320392034203c2d3e20333320333220300a333420392034203c2d3e20333420333220300a333520392034203c2d3e20333520333220300a333620392034203c2d3e20333620333220300a333720392034203c2d3e20333720333220300a333820392034203c2d3e20333820333220300a333920392034203c2d3e20333920333220300a343020392034203c2d3e20343020333220300a343120392034203c2d3e20343120333220300a333320392035203c2d3e20333220333220310a333420392035203c2d3e20333320333220310a333520392035203c2d3e20333420333220310a333620392035203c2d3e20333520333220310a333720392035203c2d3e20333620333220310a333820392035203c2d3e20333720333220310a333920392035203c2d3e20333820333220310a343020392035203c2d3e20333920333220310a343120392035203c2d3e20343020333220310a333220392033203c2d3e20333320333220370a333320392033203c2d3e20333420333220370a333420392033203c2d3e20333520333220370a333520392033203c2d3e20333620333220370a333620392033203c2d3e20333720333220370a333720392033203c2d3e20333820333220370a333820392033203c2d3e20333920333220370a333920392033203c2d3e20343020333220370a343020392033203c2d3e20343120333220370a392033322031203c2d3e203332203920350a3920392033203c2d3e20333220333220370a3020302030203c2d3e20323020313920340a3020302036203c2d3e20313920323020320a3020302037203c2d3e20313920313920330a343120302031203c2d3e20323220313920350a343120302030203c2d3e20323120313920340a343120302032203c2d3e20323220323020360a34312034312033203c2d3e20323220323220370a34312034312032203c2d3e20323220323120360a34312034312034203c2d3e20323120323220300a302034312035203c2d3e20313920323220310a302034312034203c2d3e2032302032322030200a302034312036203c2d3e2031392032312032";
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
