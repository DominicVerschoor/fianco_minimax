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

public class GameOver extends ScreenAdapter {
    Main game;
    private Stage stage;
    private Skin skin; // For button and label styling
    private BitmapFont font;
    private int winner;

    private Label titleLabel;
    private TextButton homeButton;

    public GameOver(Main game, int winner) {
        this.game = game;
        this.winner = winner;
        // Initialize the stage and skin
        stage = new Stage(new ScreenViewport());
        // SKINS FROM https://github.com/czyzby/gdx-skins
        skin = new Skin(Gdx.files.internal("lwjgl3\\src\\main\\resources\\Skins\\pixthulhu-ui.json"));
        mainMenuDisplay();
    }

    private void mainMenuDisplay() {
        float windowWidth = Gdx.graphics.getWidth();
        float windowHeight = Gdx.graphics.getHeight();
        float buttonHeight = 0.13f;
        float buttonWidth = 0.35f;

        // Create the title label
        if (this.winner == 1){
            titleLabel = new Label("White Wins!", skin);
            titleLabel.setPosition(windowWidth * 0.3f, windowHeight * 0.85f); 
            titleLabel.setSize(windowWidth * 0.8f, windowHeight * 0.1f);
        } else if (this.winner == -1) {
            titleLabel = new Label("Black Wins!", skin);
            titleLabel.setPosition(windowWidth * 0.32f, windowHeight * 0.85f); 
            titleLabel.setSize(windowWidth * 0.8f, windowHeight * 0.1f);
        } else{
            titleLabel = new Label("Draw!", skin);
            titleLabel.setPosition(windowWidth * 0.4f, windowHeight * 0.85f); 
            titleLabel.setSize(windowWidth * 0.8f, windowHeight * 0.1f);
        }
        
        titleLabel.setFontScale(2); // Set font scale for the title

        // Create buttons
        homeButton = new TextButton("Home", skin);

        // Add all elements to the stage (without setting position here)
        stage.addActor(titleLabel);
        stage.addActor(homeButton);

        homeButton.setPosition(windowWidth * 0.33f, windowHeight * 0.2f); 
        homeButton.setSize(windowWidth * buttonWidth, windowHeight * buttonHeight); 

        // Button click listeners
        homeButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                game.setScreen(new StartScreen(game)); // Switch to GameScreen
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
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw the stage and its UI elements
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
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
