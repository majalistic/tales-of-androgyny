package com.majalis.traprpg;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public enum ScreenEnum {
	 
    MAIN_MENU {
        public AbstractScreen getScreen(Game game, AbstractScreen parent, Object... params) {
            return new MainMenuScreen(game, parent);
        }
    },
    GAME {
        public AbstractScreen getScreen(Game game, AbstractScreen parent, Object... params) {
            return new GameScreen(game, parent, (Boolean)params[0]);
        }
    },
    GAME_OVER {
        public AbstractScreen getScreen(Game game, AbstractScreen parent,  Object... params) {
            return new GameOverScreen(game, parent);
        }
    },
    OPTIONS {
        public AbstractScreen getScreen(Game game, AbstractScreen parent,  Object... params) {
        	return new MainMenuScreen(game, parent);
        }
    },
    REPLAY {
        public AbstractScreen getScreen(Game game, AbstractScreen parent,  Object... params) {
        	return new MainMenuScreen(game, parent);
        }
    },
    EXIT {
        public AbstractScreen getScreen(Game game, AbstractScreen parent,  Object... params) {
        	Gdx.app.exit();
        	return new MainMenuScreen(game, parent);
        }
    };
	
    public abstract AbstractScreen getScreen(Game game, AbstractScreen parent,  Object... params);
}