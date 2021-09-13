package com.caglardurmaz.service;

import com.caglardurmaz.MancalaConstants;
import com.caglardurmaz.domain.GameDomain;
import com.caglardurmaz.model.*;
import com.caglardurmaz.rest.model.MoveRequest;
import com.caglardurmaz.util.GameUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class GameServiceTest {

	@InjectMocks
	GameService gameService;

	@Mock
	GameDomain gameDomain;

	@Test
	public void shouldInitiateGameWithIdFail(){
		String gameId = "gameId";

		GameResponse gameResponse = new GameResponse();
		gameResponse.setGame(null);
		gameResponse.setResponseCode(MancalaConstants.GAME_ID_NOT_FOUND);
		gameResponse.setResponseMessage("Game Id Not Found");
		gameResponse.setResponseStatus("ERROR");

		GameResponse response = gameService.initiateGameWithId(gameId);
		assertEquals(response, gameResponse);
	}

	@Test
	public void shouldReturnFailWithNoGameIdMove(){
		String gameId = "gameId";
		int pitIndex = 3;
		int playerNumber = 1;
		MoveRequest request = new MoveRequest();
		request.setGameId(gameId);
		request.setPitIndexToUse(pitIndex);
		request.setPlayerNumber(playerNumber);

		GameResponse gameResponse = new GameResponse();
		gameResponse.setGame(null);
		gameResponse.setResponseCode(MancalaConstants.GAME_ID_NOT_FOUND);
		gameResponse.setResponseMessage("Game Id Not Found");
		gameResponse.setResponseStatus("ERROR");

		GameResponse response = gameService.makeMove(request);
		assertEquals(response, gameResponse);
	}
}
