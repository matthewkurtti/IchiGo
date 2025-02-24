package com.badlogic.ichigo;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main implements ApplicationListener {
    Texture backgroundTexture;
    Texture runnerTexture;

    Sprite runnerSprite;
    SpriteBatch spriteBatch;
    FitViewport viewport;

    Vector2 touchPos;


    @Override
    public void create() {
        // Prepare your application here.
        backgroundTexture = new Texture("running-track.jpg");
        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        runnerTexture = new Texture("runner.png");

        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(16, 10);

        runnerSprite = new Sprite(runnerTexture); // Initialize the sprite based on the texture
        runnerSprite.setSize(1, 1); // Define the size of the sprite
        runnerSprite.setPosition(0, 0.7f);

        touchPos = new Vector2();
    }

    @Override
    public void resize(int width, int height) {
        // Resize your application here. The parameters represent the new window size.
        viewport.update(width, height, true); // true centers the camera
    }

    @Override
    public void render() {
        // Draw your application here.
        // organize code into three methods
        input();
        logic();
        draw();
    }

    private void input() {
        float speed = 6f;
        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            runnerSprite.translateY(speed * delta); // move the runner up
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            runnerSprite.translateY(-speed * delta); // move the bucket left
        }

        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY()); // Get where the touch happened on screen
            viewport.unproject(touchPos); // Convert the units to the world units of the viewport
            runnerSprite.setCenterY(touchPos.y); // Change the horizontally centered position of the bucket
        }

    }

    private void logic() {
        // Store the worldWidth and worldHeight as local variables for brevity
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        // Store the bucket size for brevity
        float runnerWidth = runnerSprite.getWidth();
        float runnerHeight = runnerSprite.getHeight();

        // Clamp x to values between 0 and worldWidth
        runnerSprite.setY(MathUtils.clamp(runnerSprite.getY(), 0.7f, worldHeight - runnerHeight - 3.5f)); // 3.5f added to keep runner on track
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        spriteBatch.setProjectionMatrix(viewport.getCamera().combined);
        spriteBatch.begin();

        // store the worldWidth and worldHeight as local variables for brevity
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        spriteBatch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight); // draw the background
        runnerSprite.draw(spriteBatch); // draw the runner sprite

        spriteBatch.end();
    }

    @Override
    public void pause() {
        // Invoked when your application is paused.
    }

    @Override
    public void resume() {
        // Invoked when your application is resumed after pause.
    }

    @Override
    public void dispose() {
        // Destroy application's resources here.
    }
}
