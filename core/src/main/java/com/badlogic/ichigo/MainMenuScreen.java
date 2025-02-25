package com.badlogic.ichigo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScreen implements Screen {

    final Ichigo game;

    Music backgroundMusic;

    Texture backgroundTexture;

    public MainMenuScreen(final Ichigo game) {
        this.game = game;

        // init music
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("deja-vu-beginning.mp3"));
        backgroundMusic.setVolume(1);

        // init texture
        backgroundTexture = new Texture("dramatic-runner.png");
    }

    @Override
    public void show() {
        backgroundMusic.play();
    }

    @Override
    public void render(float delta) {
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
        game.font.draw(game.batch, "Let's Ichi Gooooo!!! ", (worldWidth / 2) - 2.3f, worldHeight -1.5f);
        game.font.draw(game.batch, "Tap anywhere to begin!", worldWidth / 2 - 3, 1.5f);
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
        backgroundMusic.dispose();
    }
}
