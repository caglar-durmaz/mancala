package com.caglardurmaz.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MancalaException extends Exception{
	private String message;
	private String errorCode;
}
