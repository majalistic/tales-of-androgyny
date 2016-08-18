package com.majalis.traprpg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ObjectMap;

public class TextureMap {
	
	private static ObjectMap <String, Texture> textureMap = new ObjectMap<String, Texture>(); 
	
	public static Texture getTexture(String textureName){
		if (!textureMap.containsKey(textureName)){
			textureMap.put(textureName, new Texture(Gdx.files.internal(textureName+".png")));
		}
		return textureMap.get(textureName);	
	}
	
	public static void dispose(){
		for (Texture texture: textureMap.values()){
			texture.dispose();
		}
	}
}
