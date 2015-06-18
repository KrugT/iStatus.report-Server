package de.helfenkannjeder.istatus.server.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.helfenkannjeder.istatus.server.domain.util.AbstractVersionedAuditable;

@XmlRootElement
@Entity
@Table(name = "member_absence")
@NamedQueries({
	@NamedQuery(name = MemberAbsence.FIND_ALL_OPEN_ABSENCES_BY_MEMBER_ID, 
		query = "SELECT a"
				+ " FROM MemberAbsence a"
				+ " JOIN a.member m"
				+ " WHERE m.id = :" + MemberAbsence.PARAM_MEMBER_ID
//				+ " AND a.begin < CURRENT_DATE"
				+ " AND a.end IS NULL")	
})
@NamedEntityGraphs({
	@NamedEntityGraph(name = MemberAbsence.GRAPH_MEMBER,
					  attributeNodes = {@NamedAttributeNode("member")})
})
public class MemberAbsence extends AbstractVersionedAuditable implements Cloneable{

	private static final long serialVersionUID = -548146012168536966L;
	
	//Names of the NamendQueries
	private static final String PREFIX = "MemberAbsence.";
	public static final String FIND_ALL_OPEN_ABSENCES_BY_MEMBER_ID = PREFIX
			+ "findAllOpenAbsencesByMemberID";
	
	//Parameters of NamedQueries
	public static final String PARAM_MEMBER_ID = "memberId";
	
	public static final String GRAPH_MEMBER = PREFIX + "member";
	
	@Id
	@GeneratedValue
	@Column(name = "ID", nullable = false, updatable = false)
	private Long id = null;
	
	@NotNull(message = "{memberAbsence.beginn.notNull}")
	private Date begin;
	
	private Date end;
	
	@ManyToOne
	@JoinColumn(name = "member_fk")
	@XmlTransient
	private Member member;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getBegin() {
		return begin;
	}

	public void setBegin(Date begin) {
		this.begin = begin;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	@Override
	public String toString() {
		return "MemberAbsence [id=" + id + ", beginn=" + begin + ", end="
				+ end + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((begin == null) ? 0 : begin.hashCode());
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		MemberAbsence other = (MemberAbsence) obj;
		if (begin == null) {
			if (other.begin != null)
				return false;
		} else if (!begin.equals(other.begin))
			return false;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public void setValues(AbstractVersionedAuditable newValues) {
		if (!(newValues instanceof MemberAbsence)) {
			return;
		}
		
		super.setValues(newValues);
		 
		final MemberAbsence memberAbsence = (MemberAbsence) newValues;
		begin = memberAbsence.begin;
		end = memberAbsence.end;
	}
}
