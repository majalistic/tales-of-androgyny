package com.majalis.character;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class DisplayWidget extends Group {
	protected void setDiffLabel(Label label, int value) { setDiffLabel(label, value, false); }
	protected void setDiffLabel(final Label label, int value, boolean reverse) {
		label.clearActions();
		if (value == 0) {
			label.setText("");
		}
		else if (value > 0) {
			label.setColor(reverse ? Color.RED : Color.GREEN);
			label.setText("+" + value);
			label.addAction(sequence(alpha(1), fadeOut(6)));
			label.setFontScale(1.5f);
			label.addAction(sequence(delay(1), new Action(){
				@Override
				public boolean act(float delta) {
					label.setFontScale(1);
					return false;
				} }));
		}
		else {
			label.setColor(reverse ? Color.GREEN : Color.RED);
			label.setText(String.valueOf(value));
			label.addAction(sequence(alpha(1), fadeOut(6)));
			label.setFontScale(1.5f);
			label.addAction(sequence(delay(1), new Action(){
				@Override
				public boolean act(float delta) {
					label.setFontScale(1);
					return false;
				} }));
		}
	}
}
