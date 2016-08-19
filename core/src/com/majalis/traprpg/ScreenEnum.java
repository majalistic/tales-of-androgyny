package com.majalis.traprpg;

import com.badlogic.gdx.Gdx;

public enum ScreenEnum {
	 
    MAIN_MENU {
        public AbstractScreen getScreen(TrapRPG game, Object... params) {
            return new MainMenuScreen(game);
        }
    },
    GAME {
        public AbstractScreen getScreen(TrapRPG game, Object... params) {
            return new GameScreen(game, (Boolean)params[0]);
        }
    },
    GAME_OVER {
        public AbstractScreen getScreen(TrapRPG game, Object... params) {
            return new GameOverScreen(game);
        }
    },
    OPTIONS {
        public AbstractScreen getScreen(TrapRPG game, Object... params) {
        	return new MainMenuScreen(game);
        }
    },
    REPLAY {
        public AbstractScreen getScreen(TrapRPG game, Object... params) {
        	return new MainMenuScreen(game);
        }
    },
    EXIT {
        public AbstractScreen getScreen(TrapRPG game, Object... params) {
        	Gdx.app.exit();
        	return new MainMenuScreen(game);
        }
    };
	
    public abstract AbstractScreen getScreen(TrapRPG game, Object... params);
}