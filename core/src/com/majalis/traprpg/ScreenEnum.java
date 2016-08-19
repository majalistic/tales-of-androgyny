package com.majalis.traprpg;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public enum ScreenEnum {
	 
    MAIN_MENU {
        public AbstractScreen getScreen(Game game, Object... params) {
            return new MainMenuScreen(game);
        }
    },
    GAME {
        public AbstractScreen getScreen(Game game, Object... params) {
            return new GameScreen(game, (Boolean)params[0]);
        }
    },
    GAME_OVER {
        public AbstractScreen getScreen(Game game, Object... params) {
            return new GameOverScreen(game);
        }
    },
    OPTIONS {
        public AbstractScreen getScreen(Game game, Object... params) {
        	return new MainMenuScreen(game);
        }
    },
    REPLAY {
        public AbstractScreen getScreen(Game game, Object... params) {
        	return new MainMenuScreen(game);
        }
    },
    EXIT {
        public AbstractScreen getScreen(Game game, Object... params) {
        	Gdx.app.exit();
        	return new MainMenuScreen(game);
        }
    };
	
    public abstract AbstractScreen getScreen(Game game, Object... params);
}