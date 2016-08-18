package com.majalis.traprpg;
import com.badlogic.gdx.Game;

public class TrapRPG extends Game {
	public void create() {
        ScreenManager.getInstance().initialize(this).showScreen( ScreenEnum.MAIN_MENU );
	}
}