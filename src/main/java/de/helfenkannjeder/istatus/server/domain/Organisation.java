package de.helfenkannjeder.istatus.server.domain;

import java.net.URI;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.helfenkannjeder.istatus.server.domain.util.AbstractVersionedAuditable;

@XmlRootElement
@Entity
@Table(name = "organistation")
@NamedQueries({
		@NamedQuery(name = Organisation.FIND_ALL_ORGANISATIONS, 
			query = "SELECT o"
					+ " FROM Organisation o"),
		@NamedQuery(name = Organisation.FIND_ALL_ORGANISATIONS_ORDER_BY_NAME, 
			query = "SELECT o"
					+ " FROM Organisation o" 
					+ " ORDER BY o.name ASC")
//		@NamedQuery(name = Organisation.FIND_ORGANISATIONS_BY_NAME_ORDER_BY_NAME, 
//			query = "SELECT o"
//					+ " FROM Organisation o"
//					+ " WHERE o.name LIKE UPPER(:"
//					+ Organisation.PARAM_Name + ")"
//					+ " ORDER BY o.name ASC")		
})
@NamedEntityGraphs({
	@NamedEntityGraph(name = Organisation.GRAPH_SQUADS,
					  attributeNodes = {@NamedAttributeNode("squads")})
})
public class Organisation extends AbstractVersionedAuditable implements Cloneable{
	
	private static final long serialVersionUID = -769238756514088685L;
	
	//Names of the NamendQueries
	private static final String PREFIX = "Organisation.";
	public static final String FIND_ALL_ORGANISATIONS = PREFIX
			+ "findAllOrganisations";
	public static final String FIND_ALL_ORGANISATIONS_ORDER_BY_NAME = PREFIX
			+ "findAllOrganisationsOrderByName";
	public static final String FIND_ORGANISATIONS_BY_NAME_ORDER_BY_NAME = PREFIX
			+ "findOrganisationsByName";
	
	public static final String GRAPH_SQUADS = PREFIX + "squads";

	//Parameters of NamedQueries
	public static final String PARAM_Name = "name";
	
	private static final int NAME_LENGTH_MIN = 2;
	private static final int NAME_LENGTH_MAX = 32;
	
	@Id
	@GeneratedValue
	@Column(name = "ID", nullable = false, updatable = false)
	private Long id = null;
	
	@NotNull(message = "{organisation.name.notNull}")
	@Size(min = NAME_LENGTH_MIN, max = NAME_LENGTH_MAX, message = "{organisation.name.length}")
	private String name;
	
	@NotNull(message = "{organisation.street.notNull}")
	private String street;
	
	@NotNull(message = "{organisation.zip.notNull}")
	@Digits(integer = 5, fraction = 0, message = "{organisation.zip.digits}")
	private String zip;
	
	@NotNull(message = "{organisation.city.notNull}")
	private String city;
	
	@OneToMany(mappedBy = "organisation")
	@OrderColumn(name = "name")
	@XmlTransient
	private List<Squad> squads;
	
	@Transient
	private URI squadsUri;
	
	@OneToMany(mappedBy = "organisation")
	@OrderColumn(name = "username")
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

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public List<Squad> getSquads() {
		return squads;
	}

	public void setSquads(List<Squad> squads) {
		this.squads = squads;
	}
	
	public URI getSquadsUri() {
		return squadsUri;
	}

	public void setSquadsUri(URI squadsUri) {
		this.squadsUri = squadsUri;
	}

	public URI getMembersUri() {
		return membersUri;
	}

	public void setMembersUri(URI membersUri) {
		this.membersUri = membersUri;
	}

	@Override
	public void setValues(AbstractVersionedAuditable newValues) {
		if (!(newValues instanceof Organisation)) {
			return;
		}
		
		super.setValues(newValues);
		 
		final Organisation o = (Organisation) newValues;
		name = o.name;
		street = o.street;
		zip = o.zip;
		city = o.city;
	}

	@Override
	public String toString() {
		return "Organisation [id=" + id + ", name=" + name + ", street="
				+ street + ", zip=" + zip + ", city=" + city + ", " + super.toString() + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime
				* result
				+ ((street == null) ? 0 : street.hashCode());
		result = prime
				* result
				+ ((zip == null) ? 0 : zip.hashCode());
		result = prime
				* result
				+ ((zip == null) ? 0 : zip.hashCode());
		result = prime
				* result
				+ ((city == null) ? 0 : city.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Organisation other = (Organisation) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (street == null) {
			if (other.street != null)
				return false;
		} else if (!street.equals(other.street))
			return false;
		if (zip == null) {
			if (other.zip != null)
				return false;
		} else if (!zip.equals(other.zip))
			return false;
		if (city == null) {
			if (other.city != null)
				return false;
		} else if (!city.equals(other.city))
			return false;
		return true;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		final Organisation newObject = (Organisation) super.clone();
		
		newObject.id = id;
		newObject.name = name;
		newObject.street = street;
		newObject.zip = zip;
		newObject.city = city;
		newObject.setCreated(getCreated());
		newObject.setUpdated(getUpdated());
		return newObject;
	}
}
