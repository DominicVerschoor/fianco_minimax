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

    private Label titleLabel;
    private TextButton startButton, player1HumanButton, player2HumanButton, player1BotButton, player2BotButton;

    public StartScreen(Main game) {
        this.game = game;

        // Initialize the stage and skin
        stage = new Stage(new ScreenViewport());
        // SKINS FROM https://github.com/czyzby/gdx-skins
        skin = new Skin(Gdx.files.internal("lwjgl3\\src\\main\\resources\\Skins\\pixthulhu-ui.json"));
        mainMenuDisplay();
    }

    private void mainMenuDisplay() {
        // Create the title label
        titleLabel = new Label("Welcome to the Fianco!", skin);
        titleLabel.setFontScale(2); // Set font scale for the title

        // Create buttons
        startButton = new TextButton("Start", skin);
        player1HumanButton = new TextButton("Mortal", skin);
        player2HumanButton = new TextButton("Mortal", skin);
        player1BotButton = new TextButton("Machine", skin);
        player2BotButton = new TextButton("Machine", skin);

        // Add all elements to the stage (without setting position here)
        stage.addActor(titleLabel);
        stage.addActor(player1HumanButton);
        stage.addActor(player2HumanButton);
        stage.addActor(player1BotButton);
        stage.addActor(player2BotButton);
        stage.addActor(startButton);

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
                isHumanPlayers[0] = true; // Player 1 is human
                return true;
            }
            return false;
        });

        player2HumanButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                isHumanPlayers[1] = true; // Player 2 is human
                return true;
            }
            return false;
        });

        player1BotButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                isHumanPlayers[0] = false; // Player 1 is bot
                return true;
            }
            return false;
        });

        player2BotButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                isHumanPlayers[1] = false; // Player 2 is bot
                return true;
            }
            return false;
        });

        // Set the initial positions and sizes based on the current window size
        updateUIPositions(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void updateUIPositions(float windowWidth, float windowHeight) {
        float buttonHeight = 0.13f;
        float buttonWidth = 0.35f;

        // Set position and size for the title label
        titleLabel.setPosition(windowWidth * 0.12f, windowHeight * 0.85f); // Start at 10% from left, 15% from top
        titleLabel.setSize(windowWidth * 0.8f, windowHeight * 0.1f); // 80% width and 10% height of window size

        // Set positions and sizes for the buttons (relative to window size)
        player1HumanButton.setPosition(windowWidth * 0.1f, windowHeight * 0.5f); // 10% from left, 50% from bottom
        player1HumanButton.setSize(windowWidth * buttonWidth, windowHeight * buttonHeight); // Button width=35%, height=8%

        player2HumanButton.setPosition(windowWidth * 0.55f, windowHeight * 0.5f); // 55% from left
        player2HumanButton.setSize(windowWidth * buttonWidth, windowHeight * buttonHeight);

        player1BotButton.setPosition(windowWidth * 0.1f, windowHeight * 0.35f); // 10% from left, 40% from bottom
        player1BotButton.setSize(windowWidth * buttonWidth, windowHeight * buttonHeight);

        player2BotButton.setPosition(windowWidth * 0.55f, windowHeight * 0.35f);
        player2BotButton.setSize(windowWidth * buttonWidth, windowHeight * buttonHeight);

        startButton.setPosition(windowWidth * 0.33f, windowHeight * 0.2f); // 35% from left, 20% from bottom
        startButton.setSize(windowWidth * buttonWidth, windowHeight * buttonHeight); // Button width=30%, height=10%
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
    public void resize(int width, int height) {
        updateUIPositions(width, height);
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
