package de.helfenkannjeder.istatus.server.business;

import static de.helfenkannjeder.istatus.server.business.Constants.LOADGRAPH;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.jboss.arquillian.protocol.servlet.arq514hack.descriptors.impl.web.Strings;

import de.helfenkannjeder.istatus.server.domain.Member;
import de.helfenkannjeder.istatus.server.domain.Organisation;

public class MemberService implements Serializable {


	private static final long serialVersionUID = 1383251675104597468L;

	public enum FetchType {
		MEMBER_ONLY, WITH_SQUADS
	}

	public enum OrderByType {
		UNORDERED, NAME
	}

	@PersistenceContext
	private transient EntityManager em;
	
	@NotNull(message = "{member.notFound.id}")
	public Member findMemberById(Long id, FetchType fetch) {
		if (id == null) {
			return null;
		}

		Member member = null;
		EntityGraph<?> entityGraph;
		Map<String, Object> props;
		switch (fetch) {
		case MEMBER_ONLY:
			member = em.find(Member.class, id);
			break;
		case WITH_SQUADS:
			entityGraph = em.getEntityGraph(Member.GRAPH_SQUADS);
			props = Collections.singletonMap(LOADGRAPH, entityGraph);
			member = em.find(Member.class, id, props);			
			break;
		default:
			break;
		}

		return member;
	}
	
	@NotNull(message = "{member.notFound.id}")
	public Member findMemberByUsername(String username, FetchType fetch) {
		if (Strings.isNullOrEmpty(username)) {
			return null;
		}
		
		Member member = null;
		TypedQuery<Member> query = null;
		EntityGraph<?> entityGraph;

		query = em.createNamedQuery(Member.FIND_MEMBER_BY_USERNAME, Member.class)
				.setParameter(Member.PARAM_USERNAME, username.toUpperCase());
		
		switch (fetch) {
		case WITH_SQUADS:
			entityGraph = em.getEntityGraph(Member.GRAPH_SQUADS);
			query.setHint(LOADGRAPH, entityGraph);
			break;
		case MEMBER_ONLY:
		default:
			break;
		}
		
		try {
			member = query.getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
		
		return member;
	}
	
	@NotNull(message = "{organisation.notFound.id}")
	public List<Member> findMembersByOrganisation(Long organisationId, FetchType fetch) {
		if (organisationId == null) {
			return null;
		}
		
		TypedQuery<Member> query = null;
		EntityGraph<?> entityGraph;

		query = em.createNamedQuery(Member.FIND_MEMBERS_BY_ORGANISATION, Member.class)
				.setParameter(Member.PARAM_ORGANISATION_ID, organisationId);
		
		switch (fetch) {
		case WITH_SQUADS:
			entityGraph = em.getEntityGraph(Organisation.GRAPH_SQUADS);
			query.setHint(LOADGRAPH, entityGraph);
			break;
		case MEMBER_ONLY:
		default:
			break;
		}

		return query.getResultList();
	}

	public <T extends Member> T createMember(T member) {
		if (member == null) {
			return member;
		}

		em.persist(member);

		return member;
	}

	public <T extends Member> T updateMember(T member) {
		if (member == null) {
			return null;
		}

		em.detach(member);

		// Was the object deleted competitive?
		Member tmp = findMemberById(member.getId(), FetchType.MEMBER_ONLY);
		if (tmp == null) {
			throw new ConcurrentDeletedException(member.getId());
		}
		em.detach(tmp);

		member = em.merge(member);
		return member;
	}

	public void deleteMember(Member member) {
		if (member == null) {
			return;
		}

		deleteMemberById(member.getId());
	}
	
	public void deleteMemberById(Long id) {
		final Member member = findMemberById(id, FetchType.MEMBER_ONLY);
		if (member == null) {
			// Member does not exist or is already deleted
			return;
		}

		em.remove(member);
	}
}
