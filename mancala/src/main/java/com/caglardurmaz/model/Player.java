package com.caglardurmaz.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class Player {
	private int score;
	PlayerBoard playerBoard;
}
