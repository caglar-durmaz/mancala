package com.caglardurmaz.service;

import com.caglardurmaz.MancalaConstants;
import com.caglardurmaz.domain.GameDomain;
import com.caglardurmaz.exceptions.MancalaException;
import com.caglardurmaz.model.*;
import com.caglardurmaz.rest.model.MoveRequest;
import com.caglardurmaz.util.GameUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

@Service @AllArgsConstructor public class GameService {

	public static int initialScore = 0;
	public static int initialMandalaStoneCount = 0;
	public static int initialPitStoneCount = 6;
	public static int initialPlayPitCount = 6;
	public static int playerTotalPitCount = 7;
	public static int oppositeIndexTotal = 6;

	public GameResponse createGame() {
		Game mancalaGame = new Game();
		mancalaGame.setGameId(UUID.randomUUID().toString());

		ArrayList<Player> players = new ArrayList();
		ArrayList<PlayerBoard> playerBoards = new ArrayList();
		for (int i = 0; i < 2; i++) {

			PlayerBoard playerBoard = new PlayerBoard();

			ArrayList<Pit> playerPits = new ArrayList();

			for (int j = 0; j < initialPlayPitCount; j++) {
				Pit playerPit = new Pit(initialPitStoneCount);
				playerPits.add(playerPit);
			}

			Pit[] pitList = playerPits.stream().toArray(n -> new Pit[n]);
			playerBoard.setPits(pitList);
			Pit mancala = new Pit(initialMandalaStoneCount);
			playerBoard.setMancala(mancala);
			Player player = new Player(initialScore, playerBoard);
			players.add(player);
		}

		mancalaGame.setPlayerTurn(GameUtils.generateStartingPlayer());
		mancalaGame.setPlayers(players.stream().toArray(n -> new Player[n]));
		mancalaGame.setGameStatus(GameStatus.NEW);
		mancalaGame.setWinnerPlayer(0);
		GameDomain.getInstance().setGameStorage(mancalaGame);

		return GameUtils.generateSuccessResponse(mancalaGame);
	}

	public GameResponse initiateGameWithId(String gameId) {
		Map<String, Game> gameStorage = GameDomain.getInstance().getGameStorage();

		if (!gameStorage.containsKey(gameId)) {
			return GameUtils.generateFailResponse(null, MancalaConstants.GAME_ID_NOT_FOUND, "Game Id Not Found");
		}

		Game mancalaGame = gameStorage.get(gameId);

		mancalaGame.setGameStatus(GameStatus.IN_PROGRESS);
		GameDomain.getInstance().setGameStorage(mancalaGame);
		return GameUtils.generateSuccessResponse(mancalaGame);
	}

	public GameResponse initiateGame() throws MancalaException {
		Game mancalaGame = GameDomain.getInstance().getGameStorage().values().stream().filter(game -> game.getGameStatus().equals(GameStatus.NEW)).findFirst()
				.orElseThrow(() -> new MancalaException("Game not found", MancalaConstants.GAME_NOT_FOUND));

		mancalaGame.setGameStatus(GameStatus.IN_PROGRESS);
		GameDomain.getInstance().setGameStorage(mancalaGame);
		return GameUtils.generateSuccessResponse(mancalaGame);
	}

	public GameResponse makeMove(MoveRequest request) {
		if (!GameDomain.getInstance().getGameStorage().containsKey(request.getGameId())) {
			return GameUtils.generateFailResponse(null, MancalaConstants.GAME_ID_NOT_FOUND, "Game Id Not Found");
		}

		Game mancalaGame = GameDomain.getInstance().getGameStorage().get(request.getGameId());
		if (mancalaGame.getGameStatus().equals(GameStatus.FINISHED)) {
			return GameUtils.generateFailResponse(mancalaGame, MancalaConstants.GAME_FINISHED, "Game is finished");
		}

		int playerNumber = request.getPlayerNumber();
		int otherPlayerNumber = request.getPlayerNumber() == 1 ? 2 : 1;
		Player player = mancalaGame.getPlayers()[playerNumber - 1];
		Player otherPlayer = mancalaGame.getPlayers()[otherPlayerNumber - 1];

		Pit[] playerPits = player.getPlayerBoard().getPits();
		Pit playerMancala = player.getPlayerBoard().getMancala();
		Pit[] otherPlayerPits = otherPlayer.getPlayerBoard().getPits();
		Pit otherPlayerMancala = otherPlayer.getPlayerBoard().getMancala();

		int pitIndex = request.getPitIndexToUse();
		//check if pit has any stones in it
		if (playerPits[pitIndex].getStoneCount() == 0) {
			return GameUtils.generateFailResponse(mancalaGame, MancalaConstants.NO_STONE_IN_SELECTED_PIT, "No stone in this pit to sow");
		}

		int stonesToSow = playerPits[pitIndex].getStoneCount();
		playerPits[pitIndex].setStoneCount(0);
		int currentPitIndex = 1 + pitIndex;
		while (0 < stonesToSow) {
			int stoneCount = 0;
			//if this passes to other players pits
			if (currentPitIndex >= 6) {
				if (currentPitIndex % playerTotalPitCount == 6) {
					//other player mancala so not to put in opponents mancala pit
					if (currentPitIndex % 2 != 0) {
						currentPitIndex++;
						continue;
					} else {
						stoneCount = playerMancala.getStoneCount();
						playerMancala.setStoneCount(stoneCount + 1);
					}
				} else {
					int playerOrOpponentPits = (int) (currentPitIndex / playerTotalPitCount);
					if (playerOrOpponentPits % 2 != 0) {
						int otherPlayerPitIndex = currentPitIndex % playerTotalPitCount;
						stoneCount = otherPlayerPits[otherPlayerPitIndex].getStoneCount();
						if (stonesToSow == 0 && stoneCount == 0) {
							captureStones(playerPits, otherPlayerPits, currentPitIndex, playerMancala);
						} else {
							otherPlayerPits[otherPlayerPitIndex].setStoneCount(stoneCount + 1);
						}
					} else {
						int playerPitIndex = currentPitIndex % playerTotalPitCount;
						stoneCount = playerPits[playerPitIndex].getStoneCount();
						playerPits[playerPitIndex].setStoneCount(stoneCount + 1);
					}
				}
			} else {
				stoneCount = playerPits[currentPitIndex].getStoneCount();
				if (stonesToSow == 0 && stoneCount == 0) {
					captureStones(playerPits, otherPlayerPits, currentPitIndex, playerMancala);
				} else {
					playerPits[currentPitIndex].setStoneCount(stoneCount + 1);
				}
			}
			stonesToSow--;
			currentPitIndex++;
		}

		int playerTurn = otherPlayerNumber;
		//player mancala case
		if (currentPitIndex % playerTotalPitCount == initialPitStoneCount && currentPitIndex % 2 != 0) {
			playerTurn = playerNumber;
		}

		player.setScore(playerMancala.getStoneCount());
		otherPlayer.setScore(otherPlayerMancala.getStoneCount());

		mancalaGame.setPlayerTurn(playerTurn);

		GameDomain.getInstance().setGameStorage(mancalaGame);
		if (checkFinishStatus(mancalaGame)) {
			mancalaGame.setGameStatus(GameStatus.FINISHED);
			mancalaGame.setWinnerPlayer(getWinner(mancalaGame));
		}
		return GameUtils.generateSuccessResponse(mancalaGame);
	}

	private boolean checkFinishStatus(Game mancalaGame) {

		Pit[] combinedPits = Stream.of(mancalaGame.getPlayers()[0].getPlayerBoard().getPits(), mancalaGame.getPlayers()[1].getPlayerBoard().getPits()).flatMap(Stream::of)
				.toArray(Pit[]::new);

		boolean finishedStatus = true;
		for (Pit pit : combinedPits) {
			if (pit.getStoneCount() != 0) {
				finishedStatus = false;
				break;
			}
		}
		return finishedStatus;

	}

	private int getWinner(Game mancalaGame) {
		Player[] players = mancalaGame.getPlayers();
		if (players[0].getScore() > players[1].getScore()) {
			return 1;
		} else if (players[1].getScore() > players[0].getScore()) {
			return 2;
		} else {
			return 0;
		}
	}

	private void captureStones(Pit[] playerPits, Pit[] opponentPits, int lastIndex, Pit playerMancala) {
		int opponentIndex = oppositeIndexTotal - lastIndex;
		int opponentStones = opponentPits[opponentIndex].getStoneCount();
		int playerStones = playerPits[lastIndex].getStoneCount();
		int stoneCount = playerMancala.getStoneCount() + opponentStones + playerStones;
		playerMancala.setStoneCount(stoneCount);
		opponentPits[opponentIndex].setStoneCount(0);
		playerPits[lastIndex].setStoneCount(0);
	}

}
