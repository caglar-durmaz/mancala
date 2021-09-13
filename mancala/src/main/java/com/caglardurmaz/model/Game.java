package com.caglardurmaz.model;

import lombok.Data;

@Data
public class Game {
	private Player[] players;
	private String gameId;
	private GameStatus gameStatus;
	private int playerTurn;
	private int winnerPlayer;
}
