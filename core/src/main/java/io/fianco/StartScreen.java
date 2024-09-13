package io.fianco;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class StartScreen extends ScreenAdapter {

    private Main game;
    private SpriteBatch batch;
    private BitmapFont font;

    public StartScreen(Main game) {
        this.game = game;
        this.batch = new SpriteBatch();
        this.font = new BitmapFont(); // Use a default font
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        float textX = Gdx.graphics.getWidth() / 2f - 150; // Adjust the X position for centering
        float textY = Gdx.graphics.getHeight() / 2f + 20; // Adjust the Y position
        font.draw(batch, "Welcome to the Fianco Game! Press Enter to Start", Gdx.graphics.getWidth() / 2f,
                Gdx.graphics.getHeight() / 2f);
        batch.end();

        // Check if the Enter key is pressed to switch to the GameScreen
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.ENTER)) {
            game.setScreen(new GameScreen(game));
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
