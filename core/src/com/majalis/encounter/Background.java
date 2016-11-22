package com.majalis.encounter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Background extends Group{

	public static class BackgroundBuilder{
		
		Texture backgroundTexture, foregroundTexture, dialogBoxTexture;
		int x, y, width, height, x2, y2, width2, height2, x3, y3, width3, height3;
		
		public BackgroundBuilder(Texture background){
			this(background, 1280, 720);
		}
		
		public BackgroundBuilder(Texture background, int width, int height){
			this.backgroundTexture = background;
			this.width = width;
			this.height = height;
			this.x = (1280 - this.width) / 2;
			this.y = 0;
		}
		
		public BackgroundBuilder setForeground(Texture foregroundTexture){
			this.foregroundTexture = foregroundTexture;
			this.width2 = (int) (foregroundTexture.getWidth() / (foregroundTexture.getHeight() / 720f));
			this.height2 = 720;
			this.x2 = (1280 - width2) / 2;
			this.y2 = 0;
			return this;
		}
		
		public BackgroundBuilder setDialogBox(Texture dialogBoxTexture){
			this.dialogBoxTexture = dialogBoxTexture;
			this.width3 = 940;
			this.height3 = 300;
			this.x3 = 170;
			this.y3 = -50;
			return this;
		}
		
		public Background build(){
			return new Background(getImage(backgroundTexture, x, y, width, height), backgroundTexture, getImage(foregroundTexture, x2, y2, width2, height2), foregroundTexture, getImage(dialogBoxTexture, x3, y3, width3, height3), dialogBoxTexture);
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
	private final Texture dialogBoxTexture;
	private final Image background;
	private final Image foreground;
	private final Image dialogBox;
	
	private Background(Image background, Texture backgroundTexture, Image foreground, Texture foregroundTexture, Image dialogBox, Texture dialogBoxTexture){
		this.background = background;
		this.backgroundTexture = backgroundTexture;
		this.foreground = foreground;
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
		return new Background(newBackground, backgroundTexture, newForeground, foregroundTexture, newDialogBox, dialogBoxTexture);
	}
	
	private Image cloneImage(Image toClone, Texture texture){
		if (toClone == null) return null;
		Image temp = new Image(texture);
		temp.setPosition(toClone.getX(), toClone.getY());
		temp.setSize(toClone.getWidth(), toClone.getHeight());
		return temp;
	}
}
