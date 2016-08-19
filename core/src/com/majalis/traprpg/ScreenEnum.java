package com.majalis.traprpg;

import com.badlogic.gdx.Gdx;

public enum ScreenEnum {
	 
    MAIN_MENU {
        public AbstractScreen getScreen(Object... params) {
            return new MainMenuScreen();
        }
    },
    GAME {
        public AbstractScreen getScreen(Object... params) {
            return new GameScreen((Boolean)params[0]);
        }
    },
    GAME_OVER {
        public AbstractScreen getScreen(Object... params) {
            return new GameOverScreen();
        }
    },
    OPTIONS {
        public AbstractScreen getScreen(Object... params) {
        	return new MainMenuScreen();
        }
    },
    REPLAY {
        public AbstractScreen getScreen(Object... params) {
        	return new MainMenuScreen();
        }
    },
    EXIT {
        public AbstractScreen getScreen(Object... params) {
        	Gdx.app.exit();
        	return new MainMenuScreen();
        }
    };
	
    public abstract AbstractScreen getScreen(Object... params);
}