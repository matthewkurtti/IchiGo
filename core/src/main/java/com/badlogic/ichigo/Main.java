package com.badlogic.ichigo;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main implements ApplicationListener {
    Texture backgroundTexture;
    Texture runnerTexture;
    Texture strawberryTexture;
    Sound hitSound;
    Music runningMusic;
    Sprite runnerSprite;
    SpriteBatch spriteBatch;
    FitViewport viewport;

    Vector2 touchPos;

    Array<Sprite> strawberrySprites;

    float strawberryTimer;

    Rectangle runnerRectangle;
    Rectangle strawberryRectangle;

    @Override
    public void create() {
        // Prepare your application here.
        backgroundTexture = new Texture("running-track.jpg");
        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        runnerTexture = new Texture("runner.png");
        strawberryTexture = new Texture("strawberry.png");
        hitSound = Gdx.audio.newSound(Gdx.files.internal("punch-2-37333.mp3"));
        runningMusic = Gdx.audio.newMusic(Gdx.files.internal("running-in-the-90s.mp3"));
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(16, 10);

        runnerSprite = new Sprite(runnerTexture); // Initialize the sprite based on the texture
        runnerSprite.setSize(1, 1); // Define the size of the sprite
        runnerSprite.setPosition(0, 0.7f);

        touchPos = new Vector2();

        strawberrySprites = new Array<>();

        runnerRectangle = new Rectangle();
        strawberryRectangle = new Rectangle();

        runningMusic.setLooping(true);
        runningMusic.setVolume(.5f);
        runningMusic.play();

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

        float delta = Gdx.graphics.getDeltaTime(); // retrieve the current delta

        // apply runner position and size to runner rectangle (hit box)
        runnerRectangle.set(runnerSprite.getX(), runnerSprite.getY(), runnerWidth, runnerHeight);

        // loop through each strawberry
        // Loop through the sprites backwards to prevent out of bounds errors
        for (int i = strawberrySprites.size - 1; i >= 0; i--) {
            Sprite strawberrySprite = strawberrySprites.get(i); // Get the sprite from the list
            float strawberryWidth = strawberrySprite.getWidth();
            float strawberryHeight = strawberrySprite.getHeight();

            strawberrySprite.translateX(-2f * delta);

            // apply strawberry position and size to strawberry rectangle (hit box)
            strawberryRectangle.set(strawberrySprite.getX(), strawberrySprite.getY(), strawberryWidth, strawberryHeight);



            // if the top of the drop goes below the bottom of the view, remove it
            if (strawberrySprite.getX() < -strawberryWidth) strawberrySprites.removeIndex(i);
            else if (runnerRectangle.overlaps(strawberryRectangle)) { // Check if the bucket overlaps the drop
                strawberrySprites.removeIndex(i); // Remove the drop
                hitSound.play(); // make a hit sound if strawberry gets you
            }
        }

        strawberryTimer += delta; // Adds the current delta to the timer
        if (strawberryTimer > 1f) { // Check if it has been more than a second
            strawberryTimer = 0; // Reset the timer
            createStrawberry(); // Create the strawberry
        }

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

        // draw each strawberry sprite
        for (Sprite strawberrySprite : strawberrySprites) {
            strawberrySprite.draw(spriteBatch);
        }

        spriteBatch.end();
    }

    private void createStrawberry() {
        // create local variables for convenience
        float strawberryWidth = 1.5f;
        float strawberryHeight = 1;
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        // use random num to determine strawberry location
        int ranNum = MathUtils.random(1, 5);
        float strawberryLocation = 3.5f;

        switch (ranNum){
            case 1:
                strawberryLocation = 3.5f;
                break;
            case 2:
                strawberryLocation = 4.6f;
                break;
            case 3:
                strawberryLocation = 5.85f;
                break;
            case 4:
                strawberryLocation = 7.1f;
                break;
            case 5:
                strawberryLocation = 8.3f;
                break;
        }

        // create strawberry sprite
        Sprite strawberrySprite = new Sprite(strawberryTexture);
        strawberrySprite.setSize(strawberryWidth, strawberryHeight);
        strawberrySprite.setX(worldWidth - strawberryWidth);
        strawberrySprite.setY(worldHeight - strawberryHeight - strawberryLocation);
        strawberrySprites.add(strawberrySprite);

        // strawberry locations:
        // top track: 3.5f
        // track 2: 4.6f
        // track 3: 5.85f
        // track 4: 7.1f
        // track 5: 8.3f
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
