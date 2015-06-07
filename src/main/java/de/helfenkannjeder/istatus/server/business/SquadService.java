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

import de.helfenkannjeder.istatus.server.domain.Organisation;
import de.helfenkannjeder.istatus.server.domain.Squad;

public class SquadService implements Serializable {

	private static final long serialVersionUID = 7630421539856219706L;

	public enum FetchType {
		SQUAD_ONLY, WITH_MEMBERS
	}

	public enum OrderByType {
		UNORDERED, NAME
	}

	@PersistenceContext
	private transient EntityManager em;
	
	@NotNull(message = "{squad.notFound.id}")
	public Squad findSquadById(Long id, FetchType fetch) {
		if (id == null) {
			return null;
		}

		Squad squad = null;
		EntityGraph<?> entityGraph;
		Map<String, Object> props;
		switch (fetch) {
		case SQUAD_ONLY:
			squad = em.find(Squad.class, id);
			break;
		case WITH_MEMBERS:
			entityGraph = em.getEntityGraph(Squad.GRAPH_MEMBERS);
			props = Collections.singletonMap(LOADGRAPH, entityGraph);
			squad = em.find(Squad.class, id, props);		
			break;
		default:
			break;
		}

		return squad;
	}
	
	@NotNull(message = "{squad.notFound.id}")
	public List<Squad> findSquadByOrganisationId(Long organisationId, FetchType fetch) {
		if (organisationId == null) {
			return null;
		}
		
		TypedQuery<Squad> query = null;
		EntityGraph<?> entityGraph;

		query = em.createNamedQuery(Squad.FIND_SQUADS_BY_ORGANISATION, Squad.class)
				.setParameter(Squad.PARAM_ORGANISATION_ID, organisationId);
		
		switch (fetch) {
		case WITH_MEMBERS:
			entityGraph = em.getEntityGraph(Squad.GRAPH_MEMBERS);
			query.setHint(LOADGRAPH, entityGraph);
			break;
		case SQUAD_ONLY:
		default:
			break;
		}

		return query.getResultList();
	}
	
	public <T extends Squad> T createSquad(T squad) {
		if (squad == null) {
			return squad;
		}

		em.persist(squad);

		return squad;
	}

	public <T extends Squad> T updateSquad(T squad) {
		if (squad == null) {
			return null;
		}

		em.detach(squad);

		// Was the object deleted competitive?
		Squad tmp = findSquadById(squad.getId(), FetchType.SQUAD_ONLY);
		if (tmp == null) {
			throw new ConcurrentDeletedException(squad.getId());
		}
		em.detach(tmp);

		squad = em.merge(squad);
		return squad;
	}

	public void deleteSquad(Squad squad) {
		if (squad == null) {
			return;
		}

		deleteSquadById(squad.getId());
	}
	
	public void deleteSquadById(Long id) {
		final Squad squad = findSquadById(id, FetchType.SQUAD_ONLY);
		if (squad == null) {
			// Squad does not exist or is already deleted
			return;
		}

		em.remove(squad);
	}
}
