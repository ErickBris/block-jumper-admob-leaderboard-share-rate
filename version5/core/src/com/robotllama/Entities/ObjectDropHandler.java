package com.robotllama.Entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.robotllama.Entities.Objects.Side;
import com.robotllama.Settings.GameSettings;

public class ObjectDropHandler {

	enum CurrentSide{
		LEFT,
		RIGHT
	}
	
	static final float objectsWidth = GameSettings.objectSize;
	static final float screenWidth = GameSettings.screenWidth;
	
	public List<Objects> leftList = new ArrayList<Objects>();
	public List<Objects> rightList = new ArrayList<Objects>();
	Random rand = new Random();
	Objects standByLeft, standByRight;
	boolean started = false;
	CurrentSide currentSide;
	
	int dropCount = 0;
	int spacerCount = 0;
	int currentSpacerCount = 0;
	
	float  maxCount = 8;
	float  minCount = 1;
	float  spacerMin = 2;
	float  spacerMax = 4;
	float currentDropMax;
	float  currentSpacerMax;
	
	float offSet;
	
	List<Objects> addLeft = new ArrayList<Objects>();
	List<Objects> addRight = new ArrayList<Objects>();
	List<Objects> removeLeft = new ArrayList<Objects>();
	List<Objects> removeRight = new ArrayList<Objects>();
	
	List<Objects> passiveObjectsLeft = new ArrayList<Objects>();
	List<Objects> activeObjectsLeft = new ArrayList<Objects>();
	List<Objects> passiveObjectsRight = new ArrayList<Objects>();
	List<Objects> activeObjectsRight = new ArrayList<Objects>();

	
	public ObjectDropHandler(){
		
		int objectsCount = (int)(screenWidth / objectsWidth) * 2;
		for(int i = 0; i < objectsCount; i++){
			passiveObjectsLeft.add(new Objects(Side.LEFT, 0));
			passiveObjectsRight.add(new Objects(Side.RIGHT, 0));
		}
	}
	
	public void reset(){
		started = false;
		passiveObjectsLeft.addAll(leftList);
		leftList .clear();
		passiveObjectsRight.addAll(rightList);
		rightList.clear();
		standByLeft = null;
		standByRight = null;
	}
	
	public void start(){
		currentDropMax = rand.nextFloat() * (maxCount - minCount) + minCount;
		currentSpacerMax = rand.nextFloat() * (spacerMax - spacerMin) + spacerMin;
		
		currentSide = CurrentSide.LEFT;
		leftList.add(drop(Side.LEFT, 0));

		started = true;
	}
	
	public boolean hasStarted(){
		return started;
	}
	
	private Objects drop(Side side, float offSet){
		//up to counter
		switch(side){
		case LEFT:
			Objects left = passiveObjectsLeft.get(0);
			passiveObjectsLeft.remove(0);
			left.setOffSet(offSet);
			left.reset();
			return left;
		case RIGHT:
			Objects right = passiveObjectsRight.get(0);
			passiveObjectsRight.remove(0);
			right.setOffSet(offSet);
			right.reset();
			return right;
		}
		return null;
	}
	
	public List<Objects> getLists(){
		List<Objects> merged = new ArrayList<Objects>();
		merged.addAll(leftList);
		merged.addAll(rightList);
		return merged;
	}
	
	public void update(SpriteBatch sb, Hero hero){
		addLeft.clear();
		addRight.clear();
		removeLeft.clear();
		removeRight.clear();
		
		//left side
		for(int i = 0; i < leftList.size(); i++){
			Objects obj = leftList.get(i);
			
			if(obj.getRect().overlaps(hero.getRect())){
				hero.dead();
			}
			
			if(!obj.update(sb)){
				removeLeft.add(obj);
			}else{
				if(obj.getChild()){
					if(dropCount < currentDropMax && currentSide == CurrentSide.LEFT){
						addLeft.add(drop(Side.LEFT, 0));
						dropCount++;
					}else{
						
						obj.setPoint();
						currentSpacerMax = rand.nextFloat() * (spacerMax - spacerMin) + spacerMin;
						offSet = obj.height * currentSpacerMax;

						currentDropMax = rand.nextFloat() * (maxCount - minCount) + minCount;
						dropCount = 0;

						currentSide = CurrentSide.RIGHT;
						addRight.add(drop(Side.RIGHT, offSet));
						currentSpacerMax = 0;
					}
				}
			}
		}

		//right side
		for(int i = 0; i < rightList.size(); i++){
			Objects obj = rightList.get(i);
			
			if(obj.getRect().overlaps(hero.getRect())){
				hero.dead();
			}
			
			if(!obj.update(sb)){
				removeRight.add(obj);
			}else{
				if(obj.getChild()){
					if(dropCount  < currentDropMax && currentSide == CurrentSide.RIGHT){
						addRight.add(drop(Side.RIGHT, 0));
						dropCount ++;
					}else{
						obj.setPoint();
						currentSpacerMax = rand.nextFloat() * (spacerMax - spacerMin) + spacerMin;
						offSet = obj.height * currentSpacerMax;

						currentDropMax = rand.nextFloat() * (maxCount - minCount) + minCount;
						dropCount = 0;
						
						currentSide = CurrentSide.LEFT;
						addLeft.add(drop(Side.LEFT, offSet));
						currentSpacerMax = 0;
					}
				}
			}
		}

		
		//left
		for(int i = 0; i < removeLeft.size(); i++){
			Objects objects = leftList.get(i);
			leftList.remove(i);
			passiveObjectsLeft.add(objects);
		}
		
		for(int i = 0; i < addLeft.size(); i++){
			leftList.add(addLeft.get(i));
		}
		

		//right
		for(int i = 0; i < removeRight.size(); i++){
			Objects objects = rightList.get(i);
			rightList.remove(i);
			passiveObjectsRight.add(objects);
		}
		
		for(int i = 0; i < addRight.size(); i++){
			rightList.add(addRight.get(i));
		}
	}
}
