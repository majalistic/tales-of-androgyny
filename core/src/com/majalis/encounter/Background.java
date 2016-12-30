package com.majalis.encounter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.majalis.character.EnemyCharacter;

public class Background extends Group{

	public static class BackgroundBuilder{
		
		EnemyCharacter enemy;
		Texture backgroundTexture, foregroundTexture, dialogBoxTexture;
		int x1, y1, width, height, x2, y2, width2, height2, x3, y3, width3, height3;
		
		public BackgroundBuilder(Texture background){
			this(background, 1920, 1080);
		}
		
		public BackgroundBuilder(Texture background, int width, int height){
			this.backgroundTexture = background;
			this.width = width;
			this.height = height;
			this.x1 = (1920 - this.width) / 2;
			this.y1 = 0;
		}
		
		public BackgroundBuilder setForeground(Texture foregroundTexture, int x, int y){
			this.foregroundTexture = foregroundTexture;
			this.width2 = (int) (foregroundTexture.getWidth() / (foregroundTexture.getHeight() / 1080f));
			this.height2 = 1080;
			x2 = x;
			y2 = y;
			return this;
		}
		
		public BackgroundBuilder setForeground(EnemyCharacter foregroundTexture, int x, int y){
			this.enemy = foregroundTexture;
			this.width2 = (int) (foregroundTexture.getWidth() / (foregroundTexture.getHeight() / 1080f));
			this.height2 = 1080;
			x2 = x;
			y2 = y;
			return this;
		}
			
		public BackgroundBuilder setForeground(Texture foregroundTexture){
			return setForeground(foregroundTexture, (1920 - (int) (foregroundTexture.getWidth() / (foregroundTexture.getHeight() / 1080f))) / 2, 0);
		}
		
		public BackgroundBuilder setDialogBox(Texture dialogBoxTexture){
			this.dialogBoxTexture = dialogBoxTexture;
			this.width3 = 1410;
			this.height3 = 450;
			this.x3 = 255;
			this.y3 = -75;
			return this;
		}
		
		public Background build(){
			if (enemy != null){
				return new Background(getImage(backgroundTexture, x1, y1, width, height), backgroundTexture, enemy, getImage(dialogBoxTexture, x3, y3, width3, height3), dialogBoxTexture);
			}
			return new Background(getImage(backgroundTexture, x1, y1, width, height), backgroundTexture, getImage(foregroundTexture, x2, y2, width2, height2), foregroundTexture, getImage(dialogBoxTexture, x3, y3, width3, height3), dialogBoxTexture);
		}
		
		private Image getImage(Texture texture, int x, int y, int width, int height){
			if (texture == null) return null;
			Image image = new Image(texture);
			image.setPosition(x, y);
			image.setSize(width, height);
			return image;
		}
	}
	
	private final Texture backgroundTexture;
	private final Texture foregroundTexture;
	private final EnemyCharacter enemy;
	private final Texture dialogBoxTexture;
	private final Image background;
	private final Image foreground;
	private final Image dialogBox;
	
	private Background(Image background, Texture backgroundTexture, EnemyCharacter enemy, Image dialogBox, Texture dialogBoxTexture){
		this.background = background;
		this.backgroundTexture = backgroundTexture;
		this.foreground = null;
		this.foregroundTexture = null;
		this.enemy = enemy;
		this.dialogBox = dialogBox;
		this.dialogBoxTexture = dialogBoxTexture;
		if (background != null){
			this.addActor(background);
		}
		if (enemy != null){
			this.addActor(enemy);
		}
		if (dialogBox != null){
			this.addActor(dialogBox);
		}
	}
		
	private Background(Image background, Texture backgroundTexture, Image foreground, Texture foregroundTexture, Image dialogBox, Texture dialogBoxTexture){
		this.background = background;
		this.backgroundTexture = backgroundTexture;
		this.foreground = foreground;
		this.enemy = null;
		this.foregroundTexture = foregroundTexture;
		this.dialogBox = dialogBox;
		this.dialogBoxTexture = dialogBoxTexture;
		if (background != null){
			this.addActor(background);
		}
		if (foreground != null){
			this.addActor(foreground);
		}
		if (dialogBox != null){
			this.addActor(dialogBox);
		}
	}
	
	protected Background clone(){
		Image newBackground = cloneImage(background, backgroundTexture);
		Image newForeground = cloneImage(foreground, foregroundTexture);
		Image newDialogBox = cloneImage(dialogBox, dialogBoxTexture);
		if (enemy != null){
			return new Background(newBackground, backgroundTexture, enemy, newDialogBox, dialogBoxTexture);
		}
		return new Background(newBackground, backgroundTexture, newForeground, foregroundTexture, newDialogBox, dialogBoxTexture);
	}
	
	private Image cloneImage(Image toClone, Texture texture){
		if (toClone == null) return null;
		Image temp = new Image(texture);
		temp.setPosition(toClone.getX(), toClone.getY());
		temp.setSize(toClone.getWidth(), toClone.getHeight());
		return temp;
	}

	public void initEnemy() {
		if (enemy != null)
			this.addActorAfter(background, enemy);
	}

}
