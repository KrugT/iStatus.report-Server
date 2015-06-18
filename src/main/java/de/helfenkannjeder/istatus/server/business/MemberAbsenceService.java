package de.helfenkannjeder.istatus.server.business;

import static de.helfenkannjeder.istatus.server.business.Constants.LOADGRAPH;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import de.helfenkannjeder.istatus.server.domain.MemberAbsence;

public class MemberAbsenceService implements Serializable {

	private static final long serialVersionUID = 3844492959694457565L;

	public enum FetchType {
		MEMBER_ABSENCE_ONLY, WITH_MEMBER
	}

	@PersistenceContext
	private transient EntityManager em;
	
	@NotNull(message = "{memberAbsence.notFound.id}")
	public MemberAbsence findMemberAbsenceById(Long id, FetchType fetch) {
		if (id == null) {
			return null;
		}

		MemberAbsence memberAbsence = null;
		EntityGraph<?> entityGraph;
		Map<String, Object> props;
		switch (fetch) {
		case MEMBER_ABSENCE_ONLY:
			memberAbsence = em.find(MemberAbsence.class, id);
			break;
		case WITH_MEMBER:
			entityGraph = em.getEntityGraph(MemberAbsence.GRAPH_MEMBER);
			props = Collections.singletonMap(LOADGRAPH, entityGraph);
			memberAbsence = em.find(MemberAbsence.class, id, props);			
			break;
		default:
			break;
		}

		return memberAbsence;
	}
	
	public List<MemberAbsence> findAllOpenMemberAbsencesByMemberId(Long memberId) {
		if (memberId == null) {
			return null;
		}
		
		TypedQuery<MemberAbsence> query = null;

		query = em.createNamedQuery(MemberAbsence.FIND_ALL_OPEN_ABSENCES_BY_MEMBER_ID, MemberAbsence.class)
				.setParameter(MemberAbsence.PARAM_MEMBER_ID, memberId);

		return query.getResultList();
	}

	public <T extends MemberAbsence> T createMemberAbsence(T memberAbsence) {
		if (memberAbsence == null) {
			return memberAbsence;
		}

		em.persist(memberAbsence);

		return memberAbsence;
	}

	public <T extends MemberAbsence> T updateMemberAbsence(T memberAbsence) {
		if (memberAbsence == null) {
			return null;
		}

		em.detach(memberAbsence);

		// Was the object deleted competitive?
		MemberAbsence tmp = findMemberAbsenceById(memberAbsence.getId(), FetchType.MEMBER_ABSENCE_ONLY);
		if (tmp == null) {
			throw new ConcurrentDeletedException(memberAbsence.getId());
		}
		em.detach(tmp);

		memberAbsence = em.merge(memberAbsence);
		return memberAbsence;
	}

	public void deleteMemberAbsence(MemberAbsence memberAbsence) {
		if (memberAbsence == null) {
			return;
		}

		deleteMemberAbsenceById(memberAbsence.getId());
	}
	
	public void deleteMemberAbsenceById(Long id) {
		final MemberAbsence memberAbsence = findMemberAbsenceById(id, FetchType.MEMBER_ABSENCE_ONLY);
		if (memberAbsence == null) {
			// MemberAbsence does not exist or is already deleted
			return;
		}

		em.remove(memberAbsence);
	}
}
