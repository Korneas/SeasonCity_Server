package server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import serial.Validation;

public class Communication extends Observable implements Observer, Runnable {

	private static final int PORT = 5000;
	private boolean life = true;
	private ServerSocket sS;
	private ArrayList<Control> users;

	public Communication() {

		life = true;
		users = new ArrayList<Control>();

		try {
			sS = new ServerSocket(PORT);
			System.out.println("Servidor iniciado");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		while (life) {
			try {
				System.out.println("Esperando...");
				users.add(new Control(sS.accept(), this, users.size() + 1));
				System.out.println("Usuarios totales: " + users.size());
				Thread.sleep(100);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void sendAll(String cmd) {
		try {
			for (Control control : users) {
				control.send(new Validation(true, cmd));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendTurn(boolean cmd, int who) {
		try {
			users.get(who).send(new Validation(cmd, "yourTurn"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		if (!(arg instanceof String)) {
			setChanged();
			notifyObservers(arg);
			clearChanged();
		} else if (arg instanceof String) {
			String not = (String) arg;
			if (not.contains("finConexion")) {
				users.remove(o);
				System.out.println("Clientes restantes: " + users.size());
			}
		}
	}

}
