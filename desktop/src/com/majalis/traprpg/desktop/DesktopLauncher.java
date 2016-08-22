package com.majalis.traprpg.desktop;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.majalis.traprpg.TrapRPG;
/*
 * Entry point of the package for desktop implementations - sets configuration elements and initializes the generic entry point.
 */
public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.addIcon("icon16.png", Files.FileType.Internal);
		config.addIcon("icon32.png", Files.FileType.Internal);
		config.addIcon("icon128.png", Files.FileType.Internal);
		config.title = "tRaPG";
		config.width = 1280;
		config.height = 720;
		//config.fullscreen = true;
		//config.vSyncEnabled = true;
		new LwjglApplication(new TrapRPG(), config);
	}
}
