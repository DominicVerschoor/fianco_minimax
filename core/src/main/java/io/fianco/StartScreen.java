package io.fianco;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class StartScreen extends ScreenAdapter {

    private Main game;
    private Stage stage;
    private Skin skin; // For button and label styling
    private BitmapFont font;

    public StartScreen(Main game) {
        this.game = game;

        // Initialize the stage and skin
        stage = new Stage(new ScreenViewport());
        // SKINS FROM https://github.com/czyzby/gdx-skins
        skin = new Skin(Gdx.files.internal("lwjgl3\\src\\main\\resources\\Skins\\pixthulhu-ui.json"));
        mainMenuDisplay();
    }

    private void mainMenuDisplay() {
        // Create a table for layout management
        Table table = new Table();
        table.setFillParent(true); // Ensure the table takes up the entire screen
        table.center(); // Align elements to the top by default

        // Create the title label and add it to the table (centered at the top)
        Label titleLabel = new Label("Welcome to the Fianco Game!", skin);
        titleLabel.setFontScale(2); // Make the title larger
        table.add(titleLabel).padTop(100).center(); // Add padding and center it

        // Create buttons for the start and exit options
        TextButton startButton = new TextButton("Start Game", skin);
        TextButton player1HumanButton = new TextButton("Mortal", skin);
        TextButton player2HumanButton = new TextButton("Mortal", skin);
        TextButton player1BotButton = new TextButton("Machine", skin);
        TextButton player2BotButton = new TextButton("Machine", skin);

        // Add the buttons to the table (aligned at the bottom)
        table.row().padTop(150); // First row (top row with two buttons)
        table.add(player1HumanButton).padBottom(10).padRight(10).center();
        table.add(player2HumanButton).padBottom(10).padLeft(10).center();

        table.row(); // Second row (with two buttons below)
        table.add(player1BotButton).padBottom(10).padRight(10).center();
        table.add(player2BotButton).padBottom(10).padLeft(10).center();

        table.row(); // Third row (single centered button)
        table.add(startButton).colspan(2).padBottom(10).center();

        // Add the table to the stage
        stage.addActor(table);

        final boolean[] isHumanPlayers = { true, false };

        // Button click listeners
        startButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                game.setScreen(new GameScreen(game, isHumanPlayers[0], isHumanPlayers[1])); // Switch to GameScreen
                return true;
            }
            return false;
        });

        player1HumanButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                isHumanPlayers[0] = true; // Switch to GameScreen
                return true;
            }
            return false;
        });

        player2HumanButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                isHumanPlayers[1] = true; // Switch to GameScreen
                return true;
            }
            return false;
        });

        player1BotButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                isHumanPlayers[0] = false; // Switch to GameScreen
                return true;
            }
            return false;
        });

        player2BotButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                isHumanPlayers[1] = false; // Switch to GameScreen
                return true;
            }
            return false;
        });
    }

    @Override
    public void show() {
        // Set the input processor to the stage so it can handle button clicks
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw the stage and its UI elements
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void hide() {
        // When switching to the GameScreen, we need to dispose the stage
        Gdx.input.setInputProcessor(null); // Clear the input processor
        stage.dispose(); // Dispose of the stage
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        font.dispose();
    }
}
