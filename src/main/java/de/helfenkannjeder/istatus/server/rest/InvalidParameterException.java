package de.helfenkannjeder.istatus.server.rest;

import javax.ejb.ApplicationException;

import de.helfenkannjeder.istatus.server.business.AbstractIstatusException;

@ApplicationException(rollback = true)
public class InvalidParameterException extends AbstractIstatusException {
	private static final long serialVersionUID = -6229228355693470386L;
	private static final String MESSAGE_KEY = "util.invalidParameters";

	public InvalidParameterException(String msg) {
		super("Invalid parameter");
	}

	@Override
	public String getMessageKey() {
		return MESSAGE_KEY;
	}

}