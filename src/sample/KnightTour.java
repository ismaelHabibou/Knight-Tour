package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;


public class KnightTour extends Application {
    /**
     * Data field: chess board
     */
    private ChessBoard chessBoard = new ChessBoard();

    /**
     * Data field: initial row and column index of the knight
     */
    private static int rowIndex = -1;
    private static int columnIndex = -1;

    /**
     * Data field: the state of the cells
     */
    private static boolean[][] states = new boolean[ChessBoard.NUMBER_OF_ROWS][ChessBoard.NUMBER_OF_COLUMNS];

    /**
     * Data field: Different directions in which the knight can move (The knight can only move in L pattern)
     */
    private static int[][] directions = {{2, -1}, {1, -2}, {-1, -2}, {-2, -1}, {2, 1}, {1, 2}, {-1, 2}, {-2, 1}};

    /** Data field: different points visited by the knight*/
    ArrayList<Point2D> points = new ArrayList<>();

    /** Data field: animation*/
    Timeline animation;

    /** Data field: knight*/
    private ImageView imageView = new ImageView("sample/knight.jpg");

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Create a button
        Button btSolve = new Button("Solve");

        // Create a label
        Label message = new Label("Click on the board to place the knight!");
        message.setFont(Font.font("sans-serif", FontWeight.NORMAL, FontPosture.REGULAR,16));

        // Create a Border pane
        BorderPane borderPane = new BorderPane();

        // Add the board and the button to the border pane
        borderPane.setCenter(chessBoard);
        borderPane.setBottom(btSolve);
        BorderPane.setAlignment(btSolve, Pos.CENTER);
        BorderPane.setMargin(btSolve, new Insets(10, 0, 10, 0));
        borderPane.setStyle("-fx-background-color: white;");
        borderPane.setTop(message);
        BorderPane.setAlignment(message, Pos.CENTER);

        // Create a scene
        Scene scene = new Scene(borderPane);
        primaryStage.setTitle("KnightTour");
        primaryStage.setScene(scene); // places the scene on the stage
        primaryStage.show(); // show the scene


        // Resize the chessboard when the scene sizes change.
        scene.widthProperty().addListener(observable -> chessBoard.setPrefSize(scene.getWidth(), scene.getHeight() ));
        scene.heightProperty().addListener(observable -> chessBoard.setPrefSize(scene.getWidth(), scene.getHeight() ));

        // Handle animation
        EventHandler<ActionEvent> handleAnimation = actionEvent -> draw();

        animation = new Timeline(new KeyFrame(Duration.seconds(2),handleAnimation));
        animation.setCycleCount(Timeline.INDEFINITE);

        // start touring
        btSolve.setOnAction(actionEvent -> {
            if (rowIndex != -1 && columnIndex != -1){
                imageView.setFitHeight(chessBoard.getH() / ChessBoard.NUMBER_OF_ROWS);
                imageView.setFitWidth(chessBoard.getW() / ChessBoard.NUMBER_OF_COLUMNS);

                imageView.setX(columnIndex * (chessBoard.getW() / ChessBoard.NUMBER_OF_COLUMNS));
                imageView.setY(rowIndex * (chessBoard.getH() / ChessBoard.NUMBER_OF_ROWS));

                chessBoard.getChildren().add(imageView);

                // remove the message from the pane
                borderPane.setTop(null);
            }

            startTour();
            animation.play();
        });
    }

    /**
     * Start the knight tour
     */
    private void startTour() {
        move(rowIndex, columnIndex);
    }

    /** Draw*/
    private void draw(){
        if (points.size() > 1){
            Line line = new Line();
            line.setEndX(points.get(0).getX());
            line.setEndY(points.get(0).getY());
            line.setStartX(points.get(1).getX());
            line.setStartY(points.get(1).getY());

            chessBoard.getChildren().add(line);

            imageView.setY(points.get(1).getY() - 0.5 * (chessBoard.getH() / ChessBoard.NUMBER_OF_ROWS));
            imageView.setX(points.get(1).getX() - 0.5 * (chessBoard.getW() / ChessBoard.NUMBER_OF_COLUMNS));

        } else
            animation.stop();

        // Remove the last point from the points arrayList
        points.remove(0);
    }

    /**
     * Move the knight on the board
     */
    public void move(int rowIndex, int columnIndex) {
        // check that the row and column meet the boundary conditions and the cell is unoccupied
        if ((boundaryCondition(rowIndex,columnIndex) && !states[rowIndex][columnIndex])){

            // Change the value of the cell at (rowIndex, columnIndex) to true;
            states[rowIndex][columnIndex] = true;

            Point2D point1 = new Point2D((columnIndex + 0.5) * (chessBoard.getW() / ChessBoard.NUMBER_OF_COLUMNS),
                    (rowIndex + 0.5) * (chessBoard.getH() / ChessBoard.NUMBER_OF_ROWS));
            points.add(point1);

            // The knight can only move in L-shaped pattern
            for (int[] direction: directions)
                move(rowIndex + direction[0], columnIndex + direction[1]);
        }
    }

    /**
     * Check if the boundary conditions are met
     *
     * @return true if the row is smaller than 8 and greater or equal to zero and same goes for the column.
     */
    private  boolean boundaryCondition(int row, int column) {
        return (row < chessBoard.NUMBER_OF_ROWS && row >= 0) && (column < chessBoard.NUMBER_OF_COLUMNS && column >= 0);
    }

    /**
     * Chess board
     */
    static class ChessBoard extends Pane {
        /**
         * Data field: number of rows
         */
        final static int NUMBER_OF_ROWS = 8;

        /**
         * Data field: number of columns
         */
        final static int NUMBER_OF_COLUMNS = 8;

        /**
         * Create a board
         */
        public ChessBoard() {
            // draw the grid of the boar
            drawGrid();

            // Handle the even fired when the user places the knight on the grid.
            this.setOnMouseClicked(mouseEvent -> {
                rowIndex = (int) (mouseEvent.getY() / (h / NUMBER_OF_ROWS));
                columnIndex = (int) (mouseEvent.getX() / (w / NUMBER_OF_COLUMNS));

                handleEven(); // handle event
            });
        }

        /**
         * Data field: height
         */
        private double h = 560;

        /**
         * Data field: width
         */
        private double w = 560;

        /**
         * Data field: the image of the knight is placed on the board
         */
        private static boolean placed = false;

        /**
         * Create the grid of the chess board
         */
        private void drawGrid() {
            // Clear the pane before adding any line
            this.getChildren().clear();

            // Draw vertical lines
            for (int row = 0; row <= NUMBER_OF_ROWS; row++) {
                Line verticalLine = new javafx.scene.shape.Line(0, 0, 0, h);
                verticalLine.setStartX((w / NUMBER_OF_COLUMNS) * row);
                verticalLine.setEndX((w / NUMBER_OF_COLUMNS) * row);

                this.getChildren().add(verticalLine); // add the line
            }

            // Draw horizontal lines
            for (int col = 0; col <= NUMBER_OF_COLUMNS; col++) {
                Line horizontalLine = new Line(0, 0, w, 0);
                horizontalLine.setStartY((h / NUMBER_OF_ROWS) * col);
                horizontalLine.setEndY((h / NUMBER_OF_ROWS) * col);

                this.getChildren().add(horizontalLine); // add the line
            }

        }

        /**
         * Handle event fired by user's action
         */
        private void handleEven() {
            if (!placed) {
                ImageView imageView = new ImageView("sample/knight.jpg");
                imageView.setFitHeight(h / NUMBER_OF_ROWS);
                imageView.setFitWidth(w / NUMBER_OF_COLUMNS);
                imageView.setX(columnIndex * (w / NUMBER_OF_COLUMNS));
                imageView.setY(rowIndex * (h / NUMBER_OF_ROWS));
                this.getChildren().add(imageView);

                // set place to true;
                placed = true;

            }
        }

        /**
         * Set the preferred width of the pane
         */
        @Override
        public void setPrefSize(double prefWidth, double prefHeight) {
            super.setPrefSize(prefWidth, prefHeight);
            this.w = prefWidth;
            this.h = prefHeight;

            // draw the grid again
            drawGrid();
        }

        /**
         * Get the height
         */
        public double getH() {
            return h;
        }

        /**
         * Get the width
         */
        public double getW() {
            return w;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
