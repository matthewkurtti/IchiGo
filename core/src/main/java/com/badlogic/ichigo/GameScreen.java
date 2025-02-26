package com.badlogic.ichigo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {
    final Ichigo game;

    Texture backgroundTexture;
    Texture runnerTexture;
    Texture enemyTexture;
//    Texture strawberryTexture;
    Texture heartTexture;
    Sound hitSound;
    Sound biteSound;
    Music runningMusic;
    Sprite runnerSprite;
    int healthCounter;

    Vector2 touchPos;

    Array<Sprite> enemySprites;
    Array<Sprite> heartSprites;
    Array<Sprite> strawberrySprites;

    float enemyTimer;
    float strawberryTimer;

    Rectangle runnerRectangle;
    Rectangle enemyRectangle;
    Rectangle strawberryRectangle;

    public GameScreen(final Ichigo game){
        this.game = game;

        // load images for the background, runner and strawberry
        backgroundTexture = new Texture("running-track.jpg");
        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        runnerTexture = new Texture("main-char.png");
        enemyTexture = new Texture("runner.png");
        heartTexture = new Texture("strawberry.png");

        // load hit sound effect and background music
        hitSound = Gdx.audio.newSound(Gdx.files.internal("punch-2-37333.mp3"));
        biteSound = Gdx.audio.newSound(Gdx.files.internal("bite.mp3"));
        runningMusic = Gdx.audio.newMusic(Gdx.files.internal("deja-vu-middle.mp3"));
        runningMusic.setLooping(true);
        runningMusic.setVolume(2);

        runnerSprite = new Sprite(runnerTexture); // Initialize the sprite based on the texture
        runnerSprite.setSize(1, 1); // Define the size of the sprite
        runnerSprite.setPosition(0, 0.7f);

        touchPos = new Vector2();

        runnerRectangle = new Rectangle();
        enemyRectangle = new Rectangle();
        strawberryRectangle = new Rectangle();

        enemySprites = new Array<>();
        strawberrySprites = new Array<>();
        heartSprites = new Array<>();
        healthCounter = 5;
    }

    @Override
    public void show() {
        runningMusic.play();
    }

    @Override
    public void render(float v) {
        if(healthCounter == 0){
            game.setScreen(new GameOverScreen(game)); // game over :(
            dispose();
        }
        input();
        logic();
        draw();
    }

    private void input(){
        float speed = 7f;
        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            runnerSprite.translateY(speed * delta); // move the runner up
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            runnerSprite.translateY(-speed * delta); // move the runner down
        }else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            runnerSprite.translateX(-speed * delta); // move the runner left
        }else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            runnerSprite.translateX(speed * delta); // move the runner right
        }

        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY()); // Get where the touch happened on screen
            game.viewport.unproject(touchPos); // Convert the units to the world units of the viewport
            runnerSprite.setCenterY(touchPos.y); // Change the horizontally centered position of the bucket
        }
    }

    private void logic() {
        // Store the worldWidth and worldHeight as local variables for brevity
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        // Store the bucket size for brevity
        float runnerWidth = runnerSprite.getWidth();
        float runnerHeight = runnerSprite.getHeight();

        // Clamp y to values between the racetracks
        runnerSprite.setY(MathUtils.clamp(runnerSprite.getY(), 0.7f, worldHeight - runnerHeight - 3.5f));

        // clamp x to values on the screen
        runnerSprite.setX(MathUtils.clamp(runnerSprite.getX(), 0, worldWidth - runnerWidth));

        float delta = Gdx.graphics.getDeltaTime(); // retrieve the current delta

        // apply runner position and size to runner rectangle (hit box)
        runnerRectangle.set(runnerSprite.getX(), runnerSprite.getY(), runnerWidth, runnerHeight);

        // loop through each enemy
        // Loop through the sprites backwards to prevent out of bounds errors
        for (int i = enemySprites.size - 1; i >= 0; i--) {
            Sprite enemySprite = enemySprites.get(i); // Get the sprite from the list
            float enemyWidth = enemySprite.getWidth();
            float enemyHeight = enemySprite.getHeight();


            enemySprite.translateX(-4f * delta);

            // apply strawberry position and size to strawberry rectangle (hit box)
            enemyRectangle.set(enemySprite.getX(), enemySprite.getY(), enemyWidth, enemyHeight);

            // if the right of the enemy goes off screen, remove it
            if (enemySprite.getX() < -enemyWidth) enemySprites.removeIndex(i);
            else if (runnerRectangle.overlaps(enemyRectangle)) { // Check if the runner overlaps the enemy
                enemySprites.removeIndex(i); // Remove the enemy
                hitSound.play(); // make a hit sound if strawberry gets you
                if(healthCounter > 0){
                    healthCounter -= 1; // if player has health, subtract one heart
                }
            }
        }

        // loop through each strawberry
        // Loop through the sprites backwards to prevent out of bounds errors
        for (int i = strawberrySprites.size - 1; i >= 0; i--) {
            Sprite strawberrySprite = strawberrySprites.get(i); // Get the sprite from the list
            float strawberryWidth = strawberrySprite.getWidth();
            float strawberryHeight = strawberrySprite.getHeight();

            strawberrySprite.translateX(-4f * delta);

            // apply strawberry position and size to strawberry rectangle (hit box)
            strawberryRectangle.set(strawberrySprite.getX(), strawberrySprite.getY(), strawberryWidth, strawberryHeight);

            // if the right of the enemy goes off screen, remove it
            if (strawberrySprite.getX() < -strawberryWidth) strawberrySprites.removeIndex(i);
            else if (runnerRectangle.overlaps(strawberryRectangle)) { // Check if the runner overlaps the enemy
                strawberrySprites.removeIndex(i); // Remove the enemy
                biteSound.play(); // make a hit sound if strawberry gets you
                if(healthCounter < 5){
                    healthCounter += 1;
                }
            }
        }

        enemyTimer += delta; // Adds the current delta to the timer
        if (enemyTimer > 0.6f) { // Check if it has been more than .6 seconds
            enemyTimer = 0; // Reset the timer
            createEnemy(); // Create the enemy
        }

        strawberryTimer += delta; // Adds the current delta to the timer
        if (strawberryTimer > 6) { // Check if it has been more than .6 seconds
            strawberryTimer = 0; // Reset the timer
            createStrawberry(); // Create the enemy
        }

        createHearts();
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        game.batch.begin();

        // store the worldWidth and worldHeight as local variables for brevity
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        game.batch.draw(backgroundTexture, 0, 0, worldWidth, worldHeight); // draw the background
        runnerSprite.draw(game.batch); // draw the runner sprite

        // draw each enemy sprite
        for (Sprite enemySprite : enemySprites) {
            enemySprite.draw(game.batch);
        }

        // draw each strawberry sprite
        for (Sprite strawberrySprite : strawberrySprites) {
            strawberrySprite.draw(game.batch);
        }

        // draw each heart sprite
        for (Sprite heartSprite : heartSprites) {
            heartSprite.draw(game.batch);
        }

        game.batch.end();
    }

    private void createEnemy() {
        // create local variables for convenience
        float enemyWidth = 1;
        float enemyHeight = 1;
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        // use random num to determine strawberry location
        int ranNum = MathUtils.random(1, 5);
        float enemyLocation = 3.5f;

        switch (ranNum){ // each case corresponds to a lane on the track
            case 1:
                enemyLocation = 3.5f;
                break;
            case 2:
                enemyLocation = 4.6f;
                break;
            case 3:
                enemyLocation = 5.85f;
                break;
            case 4:
                enemyLocation = 7.1f;
                break;
            case 5:
                enemyLocation = 8.3f;
                break;
        }

        // create strawberry sprite
        Sprite enemySprite = new Sprite(enemyTexture);
        enemySprite.setSize(enemyWidth, enemyHeight);
        enemySprite.setX(worldWidth - enemyWidth);
        enemySprite.setY(worldHeight - enemyHeight - enemyLocation);
        enemySprites.add(enemySprite);


    }

    private void createStrawberry() {
        // create local variables for convenience
        float strawberryWidth = 1;
        float strawberryHeight = 1;
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();

        // use random num to determine strawberry location
        int ranNum = MathUtils.random(1, 5);
        float strawberryLocation = 3.5f;

        switch (ranNum){ // each case corresponds to a lane on the track
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
        Sprite strawberrySprite = new Sprite(heartTexture);
        strawberrySprite.setSize(strawberryWidth, strawberryHeight);
        strawberrySprite.setX(worldWidth - strawberryWidth);
        strawberrySprite.setY(worldHeight - strawberryHeight - strawberryLocation);
        strawberrySprites.add(strawberrySprite);
    }

    private void createHearts(){
        // reset hearts array
        heartSprites.clear();

        // create local variables for convenience
        float worldWidth = game.viewport.getWorldWidth();
        float worldHeight = game.viewport.getWorldHeight();
        float heartWidth = 1;
        float heartHeight = 1;

        for(int i = 0; i < healthCounter; i++){
            Sprite heartSprite = new Sprite(heartTexture);
            heartSprite.setSize(heartWidth, heartHeight);
            heartSprite.setX(i);
            heartSprite.setY(worldHeight - heartHeight);
            heartSprites.add(heartSprite);
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
        backgroundTexture.dispose();
        hitSound.dispose();
        runningMusic.dispose();
        enemyTexture.dispose();
        runnerTexture.dispose();
        heartTexture.dispose();
    }
}
