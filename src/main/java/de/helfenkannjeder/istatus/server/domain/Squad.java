package de.helfenkannjeder.istatus.server.domain;

import java.net.URI;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
		@NamedQuery(name = Squad.FIND_SQUADS_BY_ORGANISATION, 
			query = "SELECT DISTINCT s"
					+ " FROM Squad s"
					+ " JOIN s.organisation o"
					+ " WHERE o.id =:" + Squad.PARAM_ORGANISATION_ID
					+ " ORDER BY s.name ASC")		
})
@NamedEntityGraphs({
	@NamedEntityGraph(name = Squad.GRAPH_MEMBERS,
					attributeNodes = {@NamedAttributeNode("members")})
})
public class Squad extends AbstractVersionedAuditable {
	
	private static final long serialVersionUID = -37695106972002052L;
	
	//Names of the NamendQueries
	private static final String PREFIX = "Squad.";
	public static final String FIND_SQUADS_BY_ORGANISATION = PREFIX + "findSquadsByOrganisation";
	
	public static final String GRAPH_MEMBERS = PREFIX + "members";
	
	//Parameters of NamedQueries
	public static final String PARAM_ORGANISATION_ID = "organisationId";
	
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
	
	@ManyToMany
	@JoinTable(name = "squad_member", joinColumns = @JoinColumn(name = "squad_fk"), inverseJoinColumns = @JoinColumn(name = "member_fk"))
	@XmlTransient
	private List<Member> members;
	
	@Transient
	private URI membersUri;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Organisation getOrganisation() {
		return organisation;
	}

	public void setOrganisation(Organisation organisation) {
		this.organisation = organisation;
	}

	public URI getOrganisationUri() {
		return organisationUri;
	}

	public void setOrganisationUri(URI organisationUri) {
		this.organisationUri = organisationUri;
	}

	public List<Member> getMembers() {
		return members;
	}

	public void setMembers(List<Member> members) {
		this.members = members;
	}

	public URI getMembersUri() {
		return membersUri;
	}

	public void setMembersUri(URI membersUri) {
		this.membersUri = membersUri;
	}

	@Override
	public String toString() {
		return "Squad [id=" + id + ", name=" + name + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Squad other = (Squad) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
