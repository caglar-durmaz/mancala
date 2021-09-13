package com.caglardurmaz.rest.model;

import lombok.Data;

@Data
public class MoveRequest {
	private int playerNumber;
	private int pitIndexToUse;
	private String gameId;
}
