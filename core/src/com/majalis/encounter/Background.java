package com.majalis.encounter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.majalis.asset.AnimatedActor;

public class Background extends Group {

	public static class BackgroundBuilder{
		
		private AnimatedActor animation;
		private TextureRegion backgroundTexture, foregroundTexture, dialogBoxTexture;
		private int x1, y1, width, height, x2, y2, width2, height2, x3, y3, width3, height3;
		private boolean tintBackground;
		
		public BackgroundBuilder(Texture background) {
			this(background, 1920, 1080);
		}
		
		public BackgroundBuilder(Texture background, int width, int height) {
			this(background, width, height, true);
		}
		
		public BackgroundBuilder(Texture background, boolean tintBackground) {
			this(background, 1920, 1080, tintBackground);
		}
		
		public BackgroundBuilder(Texture background, int width, int height, boolean tintBackground) {
			this.backgroundTexture = new TextureRegion(background);
			this.width = width;
			this.height = height;
			this.x1 = (1920 - this.width) / 2;
			this.y1 = 0;
			this.tintBackground = tintBackground;
		}
		

		public BackgroundBuilder setForeground(Texture foregroundTexture) {
			return setForeground(foregroundTexture, (1920 - (int) (foregroundTexture.getWidth() / (foregroundTexture.getHeight() / 1080f))) / 2, 0);
		}
		
		public BackgroundBuilder setForeground(Texture foregroundTexture, int x, int y) {
			this.foregroundTexture = new TextureRegion(foregroundTexture);
			this.width2 = (int) (foregroundTexture.getWidth() / (foregroundTexture.getHeight() / 1080f));
			this.height2 = 1080;
			x2 = x;
			y2 = y;
			return this;
		}
		
		public BackgroundBuilder setForeground(TextureRegion foregroundTexture) {
			this.foregroundTexture = foregroundTexture;
			return setForeground(foregroundTexture, (1920 - (int) (this.foregroundTexture.getRegionWidth() / (this.foregroundTexture.getRegionHeight() / 1080f))) / 2, 0);
		}
		
		public BackgroundBuilder setForeground(TextureRegion foregroundTexture, int x, int y) {
			this.foregroundTexture = foregroundTexture;
			this.width2 = (int) (this.foregroundTexture.getRegionWidth() / (this.foregroundTexture.getRegionHeight() / 1080f));
			this.height2 = 1080;
			x2 = x;
			y2 = y;
			return this;
		}
		
		public BackgroundBuilder setForeground(AnimatedActor foreground, int x, int y) {
			this.animation = foreground;
			if (!(x == y && x == 0)) animation.setSkeletonPosition(x, y);
			this.width2 = (int) (foreground.getWidth() / (foreground.getHeight() / 1080f));
			this.height2 = 1080;
			x2 = x;
			y2 = y;
			return this;
		}
		
		public BackgroundBuilder setDialogBox(Texture dialogBoxTexture) {
			return setDialogBox(dialogBoxTexture, 255, -75, 1410, 450);
		}
		
		public BackgroundBuilder setDialogBox(Texture dialogBoxTexture, int x, int y, int width, int height) {
			this.dialogBoxTexture = new TextureRegion(dialogBoxTexture);
			this.width3 = width;
			this.height3 = height;
			this.x3 = x;
			this.y3 = y;
			return this;
		}
		
		public Background build() {
			if (animation != null) {
				return new Background(getImage(backgroundTexture, x1, y1, width, height), backgroundTexture, animation, getImage(dialogBoxTexture, x3, y3, width3, height3), dialogBoxTexture, tintBackground);
			}
			return new Background(getImage(backgroundTexture, x1, y1, width, height), backgroundTexture, getImage(foregroundTexture, x2, y2, width2, height2), foregroundTexture, getImage(dialogBoxTexture, x3, y3, width3, height3), dialogBoxTexture, tintBackground);
		}
		
		private Image getImage(TextureRegion texture, int x, int y, int width, int height) {
			if (texture == null) return null;
			Image image = new Image(texture);
			image.setPosition(x, y);
			image.setSize(width, height);
			return image;
		}
	}
	
	private final TextureRegion backgroundTexture;
	private final TextureRegion foregroundTexture;
	private final AnimatedActor animation;
	private final TextureRegion dialogBoxTexture;
	private final Image background;
	private final Image foreground;
	private final Image dialogBox;
	private final boolean tintBackground;
	private boolean dialogBoxVisible;
	
	private Background(Image background, TextureRegion backgroundTexture, AnimatedActor animation, Image dialogBox, TextureRegion dialogBoxTexture, Boolean tintBackground) {
		this.background = background;
		this.backgroundTexture = backgroundTexture;
		this.foreground = null;
		this.foregroundTexture = null;
		this.animation = animation;
		this.dialogBox = dialogBox;
		this.dialogBoxTexture = dialogBoxTexture;
		if (background != null) {
			this.addActor(background);
		}
		if (animation != null) {
			this.addActor(animation);
		}
		if (dialogBox != null) {
			this.addActor(dialogBox);
		}
		this.tintBackground = tintBackground;
		dialogBoxVisible = true;
	}
		
	private Background(Image background, TextureRegion backgroundTexture, Image foreground, TextureRegion foregroundTexture, Image dialogBox, TextureRegion dialogBoxTexture, Boolean tintBackground) {
		this.background = background;
		this.backgroundTexture = backgroundTexture;
		this.foreground = foreground;
		this.animation = null;
		this.foregroundTexture = foregroundTexture;
		this.dialogBox = dialogBox;
		this.dialogBoxTexture = dialogBoxTexture;
		if (background != null) {
			this.addActor(background);
		}
		if (foreground != null) {
			this.addActor(foreground);
		}
		if (dialogBox != null) {
			this.addActor(dialogBox);
		}
		this.tintBackground = tintBackground;
		dialogBoxVisible = true;
	}
	
	protected Background clone() {
		Image newBackground = cloneImage(background, backgroundTexture);
		Image newForeground = cloneImage(foreground, foregroundTexture);
		Image newDialogBox = cloneImage(dialogBox, dialogBoxTexture);
		if (animation != null) {
			return new Background(newBackground, backgroundTexture, animation, newDialogBox, dialogBoxTexture, tintBackground);
		}
		return new Background(newBackground, backgroundTexture, newForeground, foregroundTexture, newDialogBox, dialogBoxTexture, tintBackground);
	}
	
	private Image cloneImage(Image toClone, TextureRegion texture) {
		if (toClone == null) return null;
		Image temp = new Image(texture);
		temp.setPosition(toClone.getX(), toClone.getY());
		temp.setSize(toClone.getWidth(), toClone.getHeight());
		return temp;
	}

	public void initAnimation() {
		if (animation != null)
			this.addActorAfter(background, animation);
	}

	public void toggleDialogBox(Actor display) {
		if (dialogBoxVisible) {
			dialogBox.addAction(Actions.hide());
			display.addAction(Actions.hide());
			dialogBoxVisible = false;
		}
		else {
			dialogBox.addAction(Actions.show());
			display.addAction(Actions.show());
			dialogBoxVisible = true;
		}
	}
	
	@Override
	public void setColor(Color color) {
		super.setColor(color);
		if (background != null && tintBackground) background.setColor(color);
	}

	public Image getDialogBox() {
		return dialogBox;
	}

	public Image getBackground() {
		return background;
	}
}
