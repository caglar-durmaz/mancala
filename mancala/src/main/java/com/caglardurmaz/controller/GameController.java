package com.caglardurmaz.controller;

import com.caglardurmaz.exceptions.MancalaException;
import com.caglardurmaz.model.Game;
import com.caglardurmaz.model.GameResponse;
import com.caglardurmaz.rest.model.MoveRequest;
import com.caglardurmaz.service.GameService;
import com.caglardurmaz.util.GameUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.lang3.StringUtils;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/game")
public class GameController {

	private final GameService gameService;

	@PostMapping("/create")
	public ResponseEntity<GameResponse> create(){
		log.info("create a game request");
		return ResponseEntity.ok(gameService.createGame());
	}

	@PostMapping("/start")
	public ResponseEntity<GameResponse> start(@RequestParam(required = false) String gameId) throws MancalaException {
		log.info("start a game with or without id");
		if(StringUtils.isNotBlank(gameId)){
			return ResponseEntity.ok(gameService.initiateGameWithId(gameId));
		}
		return ResponseEntity.ok(gameService.initiateGame());
	}

	@PostMapping("/makeMove")
	public ResponseEntity<GameResponse> makeMove(@RequestBody MoveRequest moveRequest) throws MancalaException {
		log.info("makeMove request: {}", moveRequest);
		return ResponseEntity.ok(gameService.makeMove(moveRequest));
	}

	@ExceptionHandler({ MancalaException.class })
	public ResponseEntity<GameResponse> handleException(MancalaException mancalaException) {
		return ResponseEntity.ok(GameUtils.generateFailResponse(null, mancalaException.getErrorCode(), mancalaException.getMessage()));
	}

}
