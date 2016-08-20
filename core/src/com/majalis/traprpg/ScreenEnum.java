package com.majalis.traprpg;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

public enum ScreenEnum {
	 
    MAIN_MENU {
        public AbstractScreen getScreen(Game game, AbstractScreen parent, Object... params) {
            return new MainMenuScreen(game, parent, params);
        }
    },
    GAME {
        public AbstractScreen getScreen(Game game, AbstractScreen parent, Object... params) {
            return new GameScreen(game, parent, (Boolean) params[0], params);
        }
    },
    GAME_OVER {
        public AbstractScreen getScreen(Game game, AbstractScreen parent,  Object... params) {
            return new GameOverScreen(game, parent, params);
        }
    },
    OPTIONS {
        public AbstractScreen getScreen(Game game, AbstractScreen parent,  Object... params) {
        	return new OptionScreen(game, parent, params);
        }
    },
    REPLAY {
        public AbstractScreen getScreen(Game game, AbstractScreen parent,  Object... params) {
        	return new ReplayScreen(game, parent, params);
        }
    },
    EXIT {
        public AbstractScreen getScreen(Game game, AbstractScreen parent,  Object... params) {
        	Gdx.app.exit();
        	return new ExitScreen(game, parent);
        }
    };
	
    public abstract AbstractScreen getScreen(Game game, AbstractScreen parent, Object... params);
}