package de.helfenkannjeder.istatus.server.business;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class ConcurrentDeletedException extends AbstractIstatusException {
	private static final long serialVersionUID = 1481704386111865570L;
	private static final String MESSAGE_KEY = "persistence.concurrentDelete";
	
	private final Object id;

	public ConcurrentDeletedException(Object id) {
		super("Object with ID " + id + " was deleted competitive");
		this.id = id;
	}
	
	public Object getId() {
		return id;
	}
	
	@Override
	public String getMessageKey() {
		return MESSAGE_KEY;
	}
}
