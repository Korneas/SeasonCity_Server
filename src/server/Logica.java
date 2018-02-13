package server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;

public class Logica implements Observer {

	private PApplet app;
	private Communication com;
	private final static int TOTAL_USERS = 2;
	private final static int TIMEKEEPER = 15;

	private int time;
	private int offsetTime;
	private int turn;
	private int subturn;
	private boolean init, change = false;

	private PFont fuente;
	private PImage[] elem;

	/*
	 * ID's para las pruebas
	 * 
	 * 1 Vergara 2 Jairo 3 Ana 3 Jaime 4 Alitza 5 Jhonatan 6 Julio 7 Jaime 8 Cuadros
	 * 9 Camilo
	 */

	public Logica(PApplet app) {
		super();
		this.app = app;

		com = new Communication();
		new Thread(com).start();
		com.addObserver(this);

		try {
			System.out.println(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}

	public void ejecutar() {
		if (init == true) {
			time = ((app.millis() - offsetTime));
			System.out.println("Time " + time / 10);

			if ((time/10) % (TIMEKEEPER * 100) == 0) {
				change = true;
			} else {
				change = false;
			}

			if (change && subturn <= TOTAL_USERS - 1) {
				com.sendTurn(false, subturn);
				subturn++;
				com.sendTurn(true, subturn);
			} else if (change && subturn > TOTAL_USERS - 1) {
				com.sendTurn(false, subturn);
				turn++;
				subturn = 0;
				com.sendTurn(true, subturn);
			}
		}

		app.text("Servidor", 200, 200);
		app.textAlign(PApplet.CENTER);
	}

	public void key() {
		if (app.keyPressed) {
			System.out.println("Simulation started!");
			if (app.key == PApplet.ENTER && !init) {
				com.sendAll("start");
				turn = 1;
				subturn = 0;
				init = true;
				offsetTime = app.millis();
				com.sendTurn(true, subturn);
			}
		}
	}

	@Override
	public void update(Observable o, Object arg) {
	}

}
