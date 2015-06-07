package de.helfenkannjeder.istatus.server.business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import de.helfenkannjeder.istatus.server.domain.Member;
import de.helfenkannjeder.istatus.server.domain.MemberState;
import de.helfenkannjeder.istatus.server.domain.MemberState.State;

public class StateService implements Serializable {

	private static final long serialVersionUID = 7630421539856219706L;

	@PersistenceContext
	private transient EntityManager em;
	
	@Inject
	private SquadService ss;
	
	public List<MemberState> getStatesBySquadId(Long squadId) {
		if (squadId == null) {
			return null;
		}
		
		List<MemberState> memberStates = new ArrayList<MemberState>();
		List<Member> squadMembers = ss.findSquadById(squadId, SquadService.FetchType.WITH_MEMBERS)
										.getMembers();
		List<Member> unavailableMembers = findUnavailableMembersBySquadId(squadId);
		
		for(Member member : squadMembers) {
			MemberState state = new MemberState();
			state.setMember(member);
			
			if(unavailableMembers.contains(member))
				state.setState(State.UNAVAILABLE);
			else
				state.setState(State.AVAILABLE);
			
			memberStates.add(state);
		}

		return memberStates;
	}
	
	private List<Member> findUnavailableMembersBySquadId(Long squadId) {
		final String PARAM_SQUAD_ID = "squadId";
		final String queryString = "SELECT DISTINCT m"
				+ " FROM MemberAbsence a"
				+ " JOIN a.member m"
				+ " JOIN m.squads s"
				+ " WHERE s.id = :" + PARAM_SQUAD_ID
				+ " AND a.begin < CURRENT_DATE"
				+ " AND (a.end IS NULL OR a.end < CURRENT_DATE)";
		
		TypedQuery<Member> query = em.createQuery(queryString, Member.class);
		query.setParameter(PARAM_SQUAD_ID, squadId);
		
		return query.getResultList();
	}
}
