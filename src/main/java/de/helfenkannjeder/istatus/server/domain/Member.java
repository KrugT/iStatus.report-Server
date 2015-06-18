package de.helfenkannjeder.istatus.server.domain;

import java.net.URI;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;

import de.helfenkannjeder.istatus.server.domain.util.AbstractVersionedAuditable;

@Entity
@Table(name = "member")
@NamedQueries({
		@NamedQuery(name = Member.FIND_ALL_MEMBERS, 
				query = "SELECT m"
						+ " FROM Member m" 
						+ " ORDER BY m.username ASC"),
		@NamedQuery(name = Member.FIND_MEMBER_BY_USERNAME,
				query = "SELECT m"
						+ " FROM Member m"
						+ " WHERE m.username LIKE UPPER(:"
						+ Member.PARAM_USERNAME + ")"),
		@NamedQuery(name = Member.FIND_MEMBERS_BY_ORGANISATION,
		query = "SELECT m"
				+ " FROM Member m"
				+ " LEFT JOIN m.organisation o"
				+ " WHERE o.id = :" + Member.PARAM_ORGANISATION_ID)
})
@NamedEntityGraphs({
	@NamedEntityGraph(name = Member.GRAPH_SQUADS,
					attributeNodes = {@NamedAttributeNode("squads")})
})
public class Member extends AbstractVersionedAuditable {
	

	private static final long serialVersionUID = 6327428042039083621L;
	
	//Names of the NamendQueries
	private static final String PREFIX = "Member.";
	public static final String FIND_ALL_MEMBERS = PREFIX + "findAllMember";
	public static final String FIND_MEMBER_BY_USERNAME = PREFIX + "findMemberByUsername";
	public static final String FIND_MEMBERS_BY_ORGANISATION = PREFIX + "findMembersByOrganisation";
	
	public static final String GRAPH_SQUADS = PREFIX + "squads";

	//Parameters of NamedQueries
	public static final String PARAM_USERNAME = "username";
	public static final String PARAM_ORGANISATION_ID = "organisationId";
	
	private static final String NAME_PATTERN = "[A-Z\u00C4\u00D6\u00DC][a-z\u00E4\u00F6\u00FC\u00DF]+";
	private static final String PREFIX_ADEL = "(o'|von|von der|von und zu|van)?";
	public static final String USERNAME_PATTERN = "[A-Za-z\u00C4\u00D6\u00DC\u00E4\u00F6\u00FC\u00DF][A-Za-z\u00C4\u00D6\u00DC\u00E4\u00F6\u00FC\u00DF0-9]+";
	public static final String SURNAME_PATTERN = PREFIX_ADEL + NAME_PATTERN + "(-" + NAME_PATTERN + ")?";
	private static final int USERNAME_LENGTH_MIN = 6;
	private static final int USERNAME_LENGTH_MAX = 32;
	private static final int SURNAME_LENGTH_MIN = 2;
	private static final int SURNAME_LENGTH_MAX = 32;
	private static final int GIVENNAME_LENGTH_MAX = 32;
	private static final int PASSWORD_LENGTH_MAX = 88;
	
	@Id
	@GeneratedValue
	@Column(name = "ID", nullable = false, updatable = false)
	private Long id = null;
	
	@NotNull(message = "{member.surname.notNull}")
	@Size(min = SURNAME_LENGTH_MIN,
		  max = SURNAME_LENGTH_MAX,
		  message = "{member.surname.length}")
	@Pattern(regexp = SURNAME_PATTERN, message = "{member.surname.pattern}")
	private String surname;

	@Size(max = GIVENNAME_LENGTH_MAX, message = "{member.givenName.length}")
	private String givenName;
	
	@NotNull(message = "{member.username.notNull}")
	@Size(min = USERNAME_LENGTH_MIN,
		  max = USERNAME_LENGTH_MAX,
		  message = "{member.username.length}")
	@Pattern(regexp = USERNAME_PATTERN, message = "{member.username.patter}")
	private String username;
	
	@Size(max = PASSWORD_LENGTH_MAX, message = "{member.password.length}")
	private String password;
	
	@Transient
	private String passwordWdh;
	
	@ManyToOne
	@JoinColumn(name = "organisation_fk")
	@XmlTransient
	private Organisation organisation;
	
	@Transient
	private URI organisationUri;
	
	@ManyToMany(mappedBy = "members")
	@XmlTransient
	private List<Squad> squads;
	
	@Transient
	private URI squadsUri;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordWdh() {
		return passwordWdh;
	}

	public void setPasswordWdh(String passwordWdh) {
		this.passwordWdh = passwordWdh;
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
	
	@Override
	public void setValues(AbstractVersionedAuditable newValues) {
		if (!(newValues instanceof Member)) {
			return;
		}
		
		super.setValues(newValues);
		 
		final Member m = (Member) newValues;
		surname = m.surname;
		givenName = m.givenName;
		username = m.username;
		password = m.password;
	}

	@Override
	public String toString() {
		return "Member [id=" + id + ", surname=" + surname + ", givenName="
				+ givenName + ", username=" + username + ", password="
				+ password + ", passwordWdh=" + passwordWdh + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((givenName == null) ? 0 : givenName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result
				+ ((passwordWdh == null) ? 0 : passwordWdh.hashCode());
		result = prime * result + ((surname == null) ? 0 : surname.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
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
		Member other = (Member) obj;
		if (givenName == null) {
			if (other.givenName != null)
				return false;
		} else if (!givenName.equals(other.givenName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (passwordWdh == null) {
			if (other.passwordWdh != null)
				return false;
		} else if (!passwordWdh.equals(other.passwordWdh))
			return false;
		if (surname == null) {
			if (other.surname != null)
				return false;
		} else if (!surname.equals(other.surname))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
}
