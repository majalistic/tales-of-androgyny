package com.majalis.traprpg.desktop;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;

import org.lwjgl.Sys;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.files.FileHandle;
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
		config.title = "Tales of Androgyny";
		config.width = 1;
		config.height = 1;
		config.allowSoftwareMode = true;
		
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
	         @Override
	         public void uncaughtException (Thread thread, final Throwable ex) {
	        	ex.printStackTrace();
	        	FileHandle errorLog = Gdx.files.local("error.txt");
	        	try {
					ex.printStackTrace(new PrintStream(errorLog.file()));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
	            Sys.alert("Critical Failure", "Sorry, fatal error - please let Majalis know!  Error: " + ex.getLocalizedMessage());
	         }
	      });
		
		new LwjglApplication(new TrapRPG(), config);
	}
}
