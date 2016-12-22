package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;



public class MyGdxGame implements ApplicationListener, GestureDetector.GestureListener {
	SpriteBatch batch;//don't know what a batch does
	Texture[] textures;// list of textures (pics)
    Tile[][] board;// matrix of Tile objects
    int ti;//touched column
    int tj;//touched row
    int swi;
    int swj;
    Coord[][] spots;//matrix of coordinates corresponding to possible places a tile will be
    boolean moving;
    boolean switchback;

    private final static int GRID_WIDTH = 4;
    private final static int GRID_HEIGHT = 10;
    private final static int TILE_SPEED = 5;
	@Override
	public void create () {
        batch = new SpriteBatch();
        board = new Tile[GRID_WIDTH][GRID_HEIGHT];
        spots = new Coord[GRID_WIDTH][GRID_HEIGHT];
        textures= new Texture[4];
		textures[0] = new Texture("tonyface.jpg");
		textures[1] = new Texture("rosyface.jpg");
		textures[2]= new Texture("joanieface.jpg");
        textures[3] = new Texture("lucyface.jpg");
        moving = false;
        switchback = false;

        int val;
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[i].length; j++){
                val = (int)(Math.random()*textures.length);
                board[i][j] = new Tile(textures[val]);
                board[i][j].setValue(val);
                spots[i][j] = new Coord(150 + (i * board[i][j].getSprite().getWidth()), 500 + (j * board[i][j].getSprite().getHeight()));
                board[i][j].setGoal(spots[i][j]);
                board[i][j].getSprite().setPosition(board[i][j].getGoalX(),board[i][j].getGoalY());
            }
        }

        GestureDetector gd = new GestureDetector(this);
        Gdx.input.setInputProcessor(gd);

	}

    @Override
    public void resize(int width, int height) {

    }

    @Override
	public void render () {

        moving = moveABit();

        if(moving == false){
            if(switchback){
                switchBack();
            }
            checkForMatches();
        }

        Gdx.gl.glClearColor(135/255f, 206/255f, 235/255f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j< board[i].length; j++){
                board[i][j].getSprite().draw(batch);
            }
        }
		batch.end();
        removeMatches();
	}


    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
	public void dispose () {
		batch.dispose();
		for(int  i = 0; i < board.length; i++){
			for(int j = 0; j < board[i].length; j++ ){
                board[i][j].getSprite().getTexture().dispose();
            }
		}
	}


    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        y = Gdx.graphics.getHeight() - y;

        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[i].length; j++){
                if(x > board[i][j].getSprite().getX() && (x < (board[i][j].getSprite().getX()+board[i][j].getSprite().getWidth())) && (y > board[i][j].getSprite().getY()) && (y < (board[i][j].getSprite().getY()+board[i][j].getSprite().getHeight()))){
                    ti = i;
                    tj = j;
                }
            }
        }
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        if(moving==false){
            if(Math.abs(velocityX)>Math.abs(velocityY)){
                switchTiles(velocityX,false);
            }else{
                switchTiles(velocityY,true);
            }
        }


        return false;
    }

    @Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		return true;
	}

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }

    public void removeMatches(){
        Tile tempTile;
        int newVal;
        for(int i = 0; i < board.length; i++){
            for(int j = board[i].length-1; j >= 0; j--){
                if(board[i][j].shouldDelete()){
                    //board[i][j].getSprite().setPosition(board[i][j].getSprite().getX(),1500);
                    tempTile = board[i][j];
                    for(int k = j+1; k < board[i].length; k++){
                        board[i][k].setGoal(spots[i][k-1]);
                        board[i][k-1] = board[i][k];
                    }
                    newVal = (int) (Math.random()*(textures.length-1));
                    tempTile.setValue(newVal);
                    tempTile.setSprite(textures[newVal]);
                    tempTile.getSprite().setPosition(spots[i][board[i].length-1].getX(),1500);
                    tempTile.setGoal(spots[i][board[i].length-1]);
                    board[i][board[i].length-1] = tempTile;
                }
                board[i][j].deleted();
            }
        }
    }

    public boolean moveABit(){
        boolean moveMade = false;
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j< board[i].length; j++){
                //move x
                if(board[i][j].getSprite().getX()!=board[i][j].getGoalX()){
                    if((board[i][j].getGoalX()-board[i][j].getSprite().getX()) > 2){
                        board[i][j].getSprite().setX(board[i][j].getSprite().getX()+TILE_SPEED);
                        moveMade = true;
                    }else if((board[i][j].getGoalX()-board[i][j].getSprite().getX()) < -2){
                        board[i][j].getSprite().setX(board[i][j].getSprite().getX()-TILE_SPEED);
                        moveMade = true;
                    }
                }
                //move y
                if(board[i][j].getSprite().getY()!=board[i][j].getGoalY()){
                    if((board[i][j].getGoalY()-board[i][j].getSprite().getY()) > 2){
                        board[i][j].getSprite().setY(board[i][j].getSprite().getY() + TILE_SPEED);
                        moveMade = true;
                    }else if((board[i][j].getGoalY()-board[i][j].getSprite().getY()) < -2){
                        board[i][j].getSprite().setY(board[i][j].getSprite().getY() - TILE_SPEED);
                        moveMade = true;
                    }
                }
            }
        }
        return moveMade;
    }

    public void switchTiles(float direction, boolean vert){
        swi = ti;
        swj = tj;
        Tile tempTile;

        if(direction>0){
            if(vert){
                swj = tj-1;
            }else {
                swi = ti+1;
            }
        }else{
            if(vert){
                swj = tj+1;
            }else{
                swi = ti-1;
            }
        }

        if(swi > GRID_WIDTH-1 || swi < 0 || ti > GRID_WIDTH-1 || ti < 0 || swj > GRID_HEIGHT-1 || swj < 0 || tj > GRID_HEIGHT-1 || tj < 0 ){
            return;
        }


        board[ti][tj].setGoal(spots[swi][swj]);
        board[swi][swj].setGoal(spots[ti][tj]);

        tempTile = board[ti][tj];
        board[ti][tj] = board[swi][swj];
        board[swi][swj] = tempTile;

        if(!legalMove(ti,tj) && !legalMove(swi,swj)){
            switchback = true;
        }


    }

    public void switchBack(){
        Tile tempTile;
        board[ti][tj].setGoal(spots[swi][swj]);
        board[swi][swj].setGoal(spots[ti][tj]);

        tempTile = board[ti][tj];
        board[ti][tj] = board[swi][swj];
        board[swi][swj] = tempTile;

        switchback = false;
    }

    public boolean legalMove(int i, int j){
        if(checkDown(i,j,0) >= 3 || checkUp(i, j, 0) >=3 || checkLeft(i, j, 0) >= 3 || checkRight(i, j, 0) >=3 || checkMiddle(i,j)){
            return true;
        }
        return false;
    }

    public void checkForMatches(){
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j< board[i].length; j++){
                if(checkDown(i, j, 0) >= 3 || checkUp(i, j, 0) >=3 || checkLeft(i, j, 0) >= 3 || checkRight(i, j, 0) >=3 || checkMiddle(i,j)){
                    board[i][j].delete();
                }
            }
        }
    }

    public boolean checkMiddle(int i, int j){
        if(i > 0 && i < board.length-1){
            if(board[i][j].getValue() == board[i+1][j].getValue() && board[i][j].getValue() == board[i-1][j].getValue()){
                return true;
            }
        }
        if(j > 0 && j < GRID_HEIGHT-1){
            if(board[i][j].getValue() == board[i][j+1].getValue() && board[i][j].getValue() == board[i][j-1].getValue()){
                return true;
            }
        }
        return false;
    }


    public int checkDown(int i, int j, int number){
        number = number + 1;
        if (j > 0) {
            if(board[i][j-1].getValue() == board[i][j].getValue()){
                return checkDown(i,j-1,number);
            }
        }
        return number;
    }
    public int checkUp(int i, int j, int number){
        number = number + 1;

        if(j < board[i].length-1) {
            if(board[i][j+1].getValue() == board[i][j].getValue()){
                return checkUp(i, j + 1, number);
            }
        }
        return number;
    }
    public int checkLeft(int i, int j, int number){
        number = number + 1;

        if(i > 0){
            if(board[i-1][j].getValue() == board[i][j].getValue()){
                return checkLeft(i - 1, j, number);
            }
        }
        return number;
    }
    public int checkRight(int i, int j, int number){
        number = number + 1;

        if(i < board.length-1){
            if(board[i+1][j].getValue() == board[i][j].getValue()){
                return checkRight(i + 1, j, number);
            }
        }
        return number;
    }
}
