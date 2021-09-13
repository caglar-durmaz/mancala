package com.caglardurmaz.util;

import com.caglardurmaz.MancalaConstants;
import com.caglardurmaz.model.Game;
import com.caglardurmaz.model.GameResponse;

import java.util.Random;

public interface GameUtils {

	static int generateStartingPlayer() {
		Random random = new Random();
		return random.nextInt(1) + 1;
	}

	static GameResponse generateSuccessResponse(Game game) {
		GameResponse response = new GameResponse();
		response.setGame(game);
		response.setResponseCode(MancalaConstants.SUCCESS);
		response.setResponseMessage(MancalaConstants.SUCCESS_MSG);
		response.setResponseStatus("SUCCESS");
		return response;
	}

	static GameResponse generateFailResponse(Game game, String errorCode, String errorMsg){
		GameResponse response = new GameResponse();
		response.setGame(game);
		response.setResponseCode(errorCode);
		response.setResponseMessage(errorMsg);
		response.setResponseStatus("ERROR");
		return response;
	}
}
