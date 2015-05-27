package de.helfenkannjeder.istatus.server.domain.util;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import static javax.persistence.TemporalType.TIMESTAMP;
import static javax.xml.bind.annotation.XmlAccessType.FIELD;

@MappedSuperclass
@XmlAccessorType(FIELD)
public abstract class AbstractAuditable implements Serializable {

	private static final long serialVersionUID = -4049984827780868912L;

	@Temporal(TIMESTAMP)
	@Basic(optional = false)
	@XmlTransient
	private Date created;

	@Temporal(TIMESTAMP)
	@Basic(optional = false)
	@XmlTransient
	private Date updated;
	
	@PrePersist
	protected void prePersist() {
		created = new Date();
		updated = new Date();
	}
	
	@PreUpdate
	protected void preUpdate() {
		updated = new Date();
	}
	
	public Date getCreated() {
		return created == null ? null : (Date) created.clone();
	}

	public void setCreated(Date created) {
		this.created = created == null ? null : (Date) created.clone();
	}

	public Date getUpdated() {
		return updated == null ? null : (Date) updated.clone();
	}

	public void setUpdated(Date updated) {
		this.updated = updated == null ? null : (Date) updated.clone();
	}
	
	@Override
	public String toString() {
		return "AbstractAuditable [created=" + created + ", updated=" + updated + "]";
	}
}
