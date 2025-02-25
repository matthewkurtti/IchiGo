package com.badlogic.ichigo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameOverScreen implements Screen {
    final Ichigo game;

    Music gameOverMusic;

    Texture backgroundTexture;

    public GameOverScreen(final Ichigo game) {
        this.game = game;

        gameOverMusic = Gdx.audio.newMusic(Gdx.files.internal("retired.mp3"));
        gameOverMusic.setVolume(1);

        backgroundTexture = new Texture("game-over.png");
    }

    @Override
    public void show() {
        gameOverMusic.play();
    }

    @Override
    public void render(float v) {
        ScreenUtils.clear(Color.BLACK);

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.begin();
        // draw background
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();
        game.batch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight); // draw the background

        //draw text. Remember that x and y are in meters
        game.font.getData().setScale(0.04f);
        game.font.draw(game.batch, "Game Over :( !!! ", (worldWidth / 2) - 2.1f, (worldHeight / 2) - 1.5f);
        game.font.draw(game.batch, "Tap to try again!", (worldWidth / 2) - 2.1f, 2);
        game.batch.end();

        if (Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
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
        gameOverMusic.dispose();
    }
}
