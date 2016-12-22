package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Charlie on 12/20/16.
 */
public class Tile {
    private Sprite sprite;
    private Coord current;
    private Coord goal;
    private int value;
    private boolean deleteThisRound;

    public Tile(Texture texture){
        sprite = new Sprite(texture);
        deleteThisRound = false;
    }

    public void setCurrent(float x, float y){
        current = new Coord(x,y);
    }

    public Coord getCurrent(){
        return current;
    }

    public void setSprite(Texture texture){
        sprite = new Sprite(texture);
    }

    public Sprite getSprite(){
        return sprite;
    }

    public void setGoal(Coord next){
        goal = next;
    }

    public float getGoalX(){
        return goal.getX();
    }

    public float getGoalY(){
        return goal.getY();
    }

    public int getValue(){
        return value;
    }
    public void setValue(int v){
        value = v;
    }

    public void delete(){
        deleteThisRound = true;
    }

    public void deleted(){
        deleteThisRound = false;
    }

    public boolean shouldDelete(){
        return deleteThisRound;
    }


}
