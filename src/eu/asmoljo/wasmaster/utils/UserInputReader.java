package eu.asmoljo.wasmaster.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserInputReader {

	static InputStreamReader istream = new InputStreamReader(System.in);
	static BufferedReader bufRead = new BufferedReader(istream);
	static int userInputInteger;
	static String userInputString;

	public static String getUserInputReadString() throws IOException {

		return userInputString = bufRead.readLine();

	}

	public static int getUserInputReadInteger() throws NumberFormatException, IOException {

		return userInputInteger = Integer.parseInt(bufRead.readLine());

	}

}
