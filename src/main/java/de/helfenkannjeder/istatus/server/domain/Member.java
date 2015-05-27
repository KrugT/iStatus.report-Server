package de.helfenkannjeder.istatus.server.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import de.helfenkannjeder.istatus.server.domain.util.AbstractVersionedAuditable;

@Entity
@Table(name = "member")
//@NamedQueries({
//		@NamedQuery(name = Challenge.FIND_ALL_CHALLENGES, query = "SELECT      c"
//				+ " FROM     Challenge c" + " ORDER BY c.name ASC"),
//		,
//})
public class Member extends AbstractVersionedAuditable {
	

	private static final long serialVersionUID = 6327428042039083621L;
//	
//	//Names of the NamendQueries
//	private static final String PREFIX = "Member.";
//	public static final String FIND_ALL_MEMBERS = PREFIX
//			+ "findMemberById";
//
//	//Parameters of NamedQueries
//	public static final String PARAM_USERNAME = "username";
	
	private static final String NAME_PATTERN = "[A-Z\u00C4\u00D6\u00DC][a-z\u00E4\u00F6\u00FC\u00DF]+";
	private static final String PREFIX_ADEL = "(o'|von|von der|von und zu|van)?";
	public static final String USERNAME_PATTERN = "[A-Za-z\u00C4\u00D6\u00DC\u00E4\u00F6\u00FC\u00DF][A-Za-z\u00C4\u00D6\u00DC\u00E4\u00F6\u00FC\u00DF0-9]+";
	public static final String SURNAME_PATTERN = PREFIX_ADEL + NAME_PATTERN + "(-" + NAME_PATTERN + ")?";
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
		  message = "{user.surname.length}")
	@Pattern(regexp = SURNAME_PATTERN, message = "{member.surname.pattern}")
	private String surname;

	@Size(max = GIVENNAME_LENGTH_MAX, message = "{member.givenName.length}")
	private String givenName;
	
	@Size(max = PASSWORD_LENGTH_MAX, message = "{member.password.length}")
	private String password;
	
	@Transient
	private String passwordWdh;
}
