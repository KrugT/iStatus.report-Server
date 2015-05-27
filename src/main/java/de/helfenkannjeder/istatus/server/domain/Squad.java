package de.helfenkannjeder.istatus.server.domain;

import java.net.URI;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.helfenkannjeder.istatus.server.domain.util.AbstractVersionedAuditable;

@XmlRootElement
@Entity
@Table(name = "squad")
@NamedQueries({
//		@NamedQuery(name = Squad.FIND_SQUADS_BY_ORGANISATION, 
//			query = "SELECT s"
//					+ " FROM Squad s"
//					+ " WHERE o.name LIKE UPPER(:"
//					+ Organisation.PARAM_Name + ")"
//					+ " ORDER BY o.name ASC")		
})
public class Squad extends AbstractVersionedAuditable {
	
	private static final long serialVersionUID = -37695106972002052L;
	
	//Names of the NamendQueries
	private static final String PREFIX = "Squad.";
	public static final String FIND_SQUADS_BY_ORGANISATION = PREFIX + "findSquadsByOrganisation";
	
	//Parameters of NamedQueries
	public static final String PARAM_NAME = "name";
	
	private static final int NAME_LENGTH_MIN = 2;
	private static final int NAME_LENGTH_MAX = 32;
	
	@Id
	@GeneratedValue
	@Column(name = "ID", nullable = false, updatable = false)
	private Long id = null;
	
	@Size(min = NAME_LENGTH_MIN, max = NAME_LENGTH_MAX, message = "{squad.name.length}")
	private String name;

	@ManyToOne
	@JoinColumn(name = "organisation_fk")
	@XmlTransient
	private Organisation organisation;
	
	@Transient
	private URI organisationUri;
}
