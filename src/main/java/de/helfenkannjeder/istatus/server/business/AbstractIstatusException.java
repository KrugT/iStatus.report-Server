package de.helfenkannjeder.istatus.server.business;

public abstract class AbstractIstatusException extends RuntimeException {

	private static final long serialVersionUID = -8301036394322600316L;

	public AbstractIstatusException(String msg) {
		super(msg);
	}

	public AbstractIstatusException(String msg, Throwable t) {
		super(msg, t);
	}

	public abstract String getMessageKey();
}
