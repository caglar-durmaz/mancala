package com.caglardurmaz.model;

import lombok.Data;

@Data
public class GameResponse {
	String responseCode;
	String responseMessage;
	String responseStatus;
	Game game;
}
