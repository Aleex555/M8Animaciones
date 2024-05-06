package com.alex.animation1;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class Animation1 implements ApplicationListener {

	// Constant rows and columns of the sprite sheet
	private static final int FRAME_COLS = 12, FRAME_ROWS = 4;

	// Objects used
	Animation<TextureRegion> walkAnimation; // Must declare frame type (TextureRegion)
	Texture walkSheet;
	SpriteBatch spriteBatch;

	TextureRegion[] downFrames;
	TextureRegion[] leftFrames;
	TextureRegion[] rightFrames;
	TextureRegion[] upFrames;

	Animation<TextureRegion> walkDownAnimation;
	Animation<TextureRegion> walkLeftAnimation;
	Animation<TextureRegion> walkRightAnimation;
	Animation<TextureRegion> walkUpAnimation;


	Texture background;

	TextureRegion bgRegion;
	int posx,posy;

	float SCR_WIDTH,SCR_HEIGHT;

	// A variable for tracking elapsed time for the animation
	float stateTime;

	Rectangle up, down, left, right;
	static final int DOWN = 0;
	static final int LEFT = 1;
	static final int RIGHT = 2;
	static final int UP = 3;

	 static private int lastdirection = UP;
	private OrthographicCamera camera;

	@Override
	public void create() {
		SCR_WIDTH = 480;
		SCR_HEIGHT = 800;
		background = new Texture(Gdx.files.internal("vector.jpg"));
		background.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
		bgRegion = new TextureRegion(background);
		posx = 0;
		posy = 0;
		camera = new OrthographicCamera();
		camera.setToOrtho(false, SCR_WIDTH, SCR_HEIGHT);

		up = new Rectangle(0, SCR_HEIGHT*2/3, SCR_WIDTH, SCR_HEIGHT/3);
		down = new Rectangle(0, 0, SCR_WIDTH, SCR_HEIGHT/3);
		left = new Rectangle(0, 0, SCR_WIDTH/3, SCR_HEIGHT);
		right = new Rectangle(SCR_WIDTH*2/3, 0, SCR_WIDTH/3, SCR_HEIGHT);


		// Load the sprite sheet as a Texture
		walkSheet = new Texture(Gdx.files.internal("hola.png"));

		// Use the split utility method to create a 2D array of TextureRegions. This is
		// possible becPer desplaçar el personatge per la pantalla, només caldrà modificar ON el pintem de la pantalla, just a les línies del codi de la funció render():ause this sprite sheet contains frames of equal size and they are
		// all aligned.
		TextureRegion[][] tmp = TextureRegion.split(walkSheet,
				walkSheet.getWidth() / FRAME_COLS,
				walkSheet.getHeight() / FRAME_ROWS);

		// Place the regions into a 1D array in the correct order, starting from the top
		// left, going across first. The Animation constructor requires a 1D array.
		TextureRegion[] walkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
		int index = 0;
		for (int i = 0; i < FRAME_ROWS; i++) {
			for (int j = 0; j < FRAME_COLS; j++) {
				walkFrames[index++] = tmp[i][j];
			}
		}

		downFrames = new TextureRegion[FRAME_COLS];
		leftFrames = new TextureRegion[FRAME_COLS];
		rightFrames = new TextureRegion[FRAME_COLS];
		upFrames = new TextureRegion[FRAME_COLS];

		for (int j = 0; j < FRAME_COLS; j++) {
			downFrames[j] = tmp[DOWN][j];
			leftFrames[j] = tmp[LEFT][j];
			rightFrames[j] = tmp[RIGHT][j];
			upFrames[j] = tmp[UP][j];
		}
		walkDownAnimation = new Animation<TextureRegion>(0.05f, downFrames);
		walkLeftAnimation = new Animation<TextureRegion>(0.05f, leftFrames);
		walkRightAnimation = new Animation<TextureRegion>(0.05f, rightFrames);
		walkUpAnimation = new Animation<TextureRegion>(0.05f, upFrames);

		// Initialize the Animation with the frame interval and array of frames
		walkAnimation = new Animation<TextureRegion>(0.05f, walkFrames);

		// Instantiate a SpriteBatch for drawing and reset the elapsed animation
		// time to 0
		spriteBatch = new SpriteBatch();
		stateTime = 0f;
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear screen
		stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time

		// Get current frame of animation for the current stateTime

		int direction = virtual_joystick_control();
		int bgMoveAmount = 25; // Esto puede ser menos que moveAmount para dar una sensación de profundidad
		TextureRegion currentFrame = walkUpAnimation.getKeyFrame(stateTime,false);
		switch (direction) {
			case UP:
				posy -= bgMoveAmount;
				currentFrame = walkUpAnimation.getKeyFrame(stateTime, true);
				break;
			case DOWN:
				posy += bgMoveAmount;
				currentFrame = walkDownAnimation.getKeyFrame(stateTime, true);
				break;
			case LEFT:
				posx -= bgMoveAmount;
				currentFrame = walkLeftAnimation.getKeyFrame(stateTime, true);
				break;
			case RIGHT:
				posx += bgMoveAmount;
				currentFrame = walkRightAnimation.getKeyFrame(stateTime, true);
				break;
			case 4:
				if (lastdirection == UP){
					currentFrame = walkUpAnimation.getKeyFrame(stateTime, false);
				}else if (lastdirection == DOWN){
					currentFrame = walkDownAnimation.getKeyFrame(stateTime, false);
				}else if (lastdirection == RIGHT){
					currentFrame = walkRightAnimation.getKeyFrame(stateTime, false);
				}else if (lastdirection == LEFT){
					currentFrame = walkLeftAnimation.getKeyFrame(stateTime, false);
				}
			default:

				break;
		}

		bgRegion.setRegion(posx, posy, 10000, 10000 );

		// pintar
		spriteBatch.begin();
		spriteBatch.draw(bgRegion, 0, 0);
		spriteBatch.draw(currentFrame, 1000, 450,currentFrame.getRegionWidth()*2,currentFrame.getRegionHeight()*2);
		spriteBatch.end();


	}

	protected int virtual_joystick_control() {
		// iterar per multitouch
		// cada "i" és un possible "touch" d'un dit a la pantalla
		for(int i=0;i<10;i++)
			if (Gdx.input.isTouched(i)) {
				Vector3 touchPos = new Vector3();
				touchPos.set(Gdx.input.getX(i), Gdx.input.getY(i), 0);
				// traducció de coordenades reals (depen del dispositiu) a 800x480
				camera.unproject(touchPos);
				if (up.contains(touchPos.x, touchPos.y)) {
					lastdirection = UP;
					return UP;
				} else if (down.contains(touchPos.x, touchPos.y)) {
					lastdirection = DOWN;
					return DOWN;
				} else if (left.contains(touchPos.x, touchPos.y)) {
					lastdirection = LEFT;
					return LEFT;
				} else if (right.contains(touchPos.x, touchPos.y)) {
					lastdirection = RIGHT;
					return RIGHT;
				}
			}
		return 4;
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() { // SpriteBatches and Textures must always be disposed
		spriteBatch.dispose();
		walkSheet.dispose();
	}
}
