package de.helfenkannjeder.istatus.server.business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import de.helfenkannjeder.istatus.server.domain.Member;
import de.helfenkannjeder.istatus.server.domain.MemberAbsence;
import de.helfenkannjeder.istatus.server.domain.MemberState;
import de.helfenkannjeder.istatus.server.domain.MemberState.State;

public class StateService implements Serializable {

	private static final long serialVersionUID = 7630421539856219706L;

	@PersistenceContext
	private transient EntityManager em;
	
	@Inject
	private SquadService ss;
	
	@Inject
	private MemberService ms;
	
	@Inject
	private MemberAbsenceService mas;
	
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
		final String PARAM_CURRENT_DATE = "currentDate";
		final String queryString = "SELECT DISTINCT m"
				+ " FROM MemberAbsence a"
				+ " JOIN a.member m"
				+ " JOIN m.squads s"
				+ " WHERE s.id = :" + PARAM_SQUAD_ID
				+ " AND a.begin < :" + PARAM_CURRENT_DATE
				+ " AND (a.end IS NULL OR a.end < CURRENT_DATE)";
		
		TypedQuery<Member> query = em.createQuery(queryString, Member.class);
		query.setParameter(PARAM_SQUAD_ID, squadId);
		query.setParameter(PARAM_CURRENT_DATE, new Date());
		
		return query.getResultList();
	}
	
	public void setAvailable(Long userId) throws Exception {
		if(userId == null)
			throw new Exception("userId required");
		
		Member member = ms.findMemberById(userId, MemberService.FetchType.MEMBER_ONLY);
		List<MemberAbsence> absences = mas.findAllOpenMemberAbsencesByMemberId(member.getId());
		
		for(MemberAbsence absence : absences) {
			absence.setEnd(new Date());
			mas.updateMemberAbsence(absence);
		}
	}
	
	public MemberAbsence setUnavailable(Long userId) {
		if(null == userId)
			return null;
		
		Member member = ms.findMemberById(userId, MemberService.FetchType.MEMBER_ONLY);
		MemberAbsence memberAbsence = new MemberAbsence();
		memberAbsence.setMember(member);
		memberAbsence.setBegin(new Date());
		
		return mas.createMemberAbsence(memberAbsence);
	}
}
