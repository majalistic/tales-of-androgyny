package com.majalis.traprpg;
import com.badlogic.gdx.Game;

public class TrapRPG extends Game {
	public void create() {
        new MainMenuScreen(this).showScreen( ScreenEnum.MAIN_MENU );
	}
}