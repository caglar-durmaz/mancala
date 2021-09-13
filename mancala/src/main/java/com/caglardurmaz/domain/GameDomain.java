package com.caglardurmaz.domain;

import com.caglardurmaz.model.Game;

import java.util.HashMap;
import java.util.Map;

public class GameDomain {

	private static Map<String, Game> gameStorage;
	private static GameDomain instance;

	private GameDomain() {
		gameStorage = new HashMap<>();
	}

	public static synchronized GameDomain getInstance() {
		if (instance == null) {
			instance = new GameDomain();
		}
		return instance;
	}

	public Map<String, Game> getGameStorage() {
		return gameStorage;
	}

	public void setGameStorage(Game game) {
		gameStorage.put(game.getGameId(), game);
	}

}
