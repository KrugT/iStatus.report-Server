package de.helfenkannjeder.istatus.server.domain.util;

import javax.persistence.Basic;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import javax.ws.rs.FormParam;

@MappedSuperclass
public abstract class AbstractVersionedAuditable extends AbstractAuditable implements Cloneable {

	private static final long serialVersionUID = -362545685142168115L;
	
	@Version
	@Basic(optional = false)
	@FormParam("version")
	private int version = 0;

	public int getVersion() {
		return version;
	}
	
	public void setValues(AbstractVersionedAuditable newValues) {
		version = newValues.version;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		final AbstractVersionedAuditable neuesObjekt = (AbstractVersionedAuditable) super.clone();
		neuesObjekt.version = getVersion();
		return neuesObjekt;
	}

	@Override
	public String toString() {
		return "AbstractVersionedAuditable [version=" + version + ", " + super.toString() + "]";
	}
}

