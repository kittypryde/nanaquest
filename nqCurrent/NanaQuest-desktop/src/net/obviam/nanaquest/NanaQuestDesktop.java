package net.obviam.nanaquest;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class NanaQuestDesktop {

	public static void main(String[] args) {
		new LwjglApplication(new NanaQuest(), "NanaQuest", 960, 640, true);
	}

}
