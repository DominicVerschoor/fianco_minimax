package io.fianco;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameOver implements Screen {

    private SpriteBatch batch;
    private BitmapFont font;
    private int winningPlayer;

    public GameOver(int winningPlayer) {
        this.winningPlayer = winningPlayer;
        batch = new SpriteBatch();
        font = new BitmapFont(); // Use a default font
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        font.draw(batch, "Player " + winningPlayer + " WINS!", Gdx.graphics.getWidth() / 2f,
                Gdx.graphics.getHeight() / 2f);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
