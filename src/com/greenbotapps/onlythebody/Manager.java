package com.greenbotapps.onlythebody;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;

public class Manager {
	private final String name = "ONLYTHEBODYAPP";
	private SharedPreferences settings;
	private String key;
	private int levelValue;

	public Manager(String keyName, Activity act) {
		settings = act.getSharedPreferences(name, 0);
		key = keyName;
		levelValue = settings.getInt(key, 0);
		if (levelValue < 0 || levelValue > 3) {
			levelValue = 0;
		}
	}

	public void clear(Activity act) {
		act.getSharedPreferences(name, 0).edit().clear().commit();
	}

	public int getLevel() {
		return levelValue;
	}

	public int getColor() {
		switch (levelValue) {
		case 0:
			return Color.parseColor("#ffffff");
		case 1:
			return Color.parseColor("#ff0000");
		case 2:
			return Color.parseColor("#ffff00");
		case 3:
			return Color.parseColor("#00ff00");
		default:
			return Color.parseColor("#ffffff");
		}
	}

	public void setLevel(int level) {
		SharedPreferences.Editor editor = settings.edit();
		switch (level) {
		case 0:
			editor.putInt(key, 0);
			break;
		case 1:
			editor.putInt(key, 1);
			break;
		case 2:
			editor.putInt(key, 2);
			break;
		case 3:
			editor.putInt(key, 3);
			break;
		default:
			editor.putInt(key, 0);
		}
		editor.commit();
	}
}
