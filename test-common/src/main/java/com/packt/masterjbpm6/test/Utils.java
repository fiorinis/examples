package com.packt.masterjbpm6.test;

import java.io.IOException;

import org.junit.Test;

public class Utils {

	@Test
	public void waitUserInput() {
		Utils.waitUserInput("type something");
		Utils.waitUserInput("type something else");
	}

	public static void waitUserInput(String msg) {
		try {
			System.out.println(msg);
			System.out.print(">");
			System.in.read();
			while (System.in.available() > 0) {
				System.in.read();
			}
		} catch (IOException e) {
		}
	}
}
