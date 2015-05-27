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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import de.helfenkannjeder.istatus.server.domain.Organisation;
import de.helfenkannjeder.istatus.server.domain.Organisation_;

public class OrganisationService implements Serializable {


	private static final long serialVersionUID = 1383251675104597468L;

	public enum FetchType {
		ORGANISATION_ONLY, WITH_SQUADS
	}

	public enum OrderByType {
		UNORDERED, NAME
	}

	@PersistenceContext
	private transient EntityManager em;
	
	@NotNull(message = "{organisation.notFound.id}")
	public Organisation findOrganisationById(Long id, FetchType fetch) {
		if (id == null) {
			return null;
		}

		Organisation organisation = null;
		EntityGraph<?> entityGraph;
		Map<String, Object> props;
		switch (fetch) {
		case ORGANISATION_ONLY:
			organisation = em.find(Organisation.class, id);
			break;
		case WITH_SQUADS:
			entityGraph = em.getEntityGraph(Organisation.GRAPH_SQUADS);
			props = Collections.singletonMap(LOADGRAPH, entityGraph);
			organisation = em.find(Organisation.class, id, props);			
			break;
		default:
			break;
		}

		return organisation;
	}
	
	@NotNull(message = "{organisation.notFound.id}")
	public List<Organisation> findOrganisationByName(String name, FetchType fetch) {
		if (name == null) {
			return null;
		}
		
		TypedQuery<Organisation> query = null;
		EntityGraph<?> entityGraph;

		query = em.createNamedQuery(Organisation.FIND_ORGANISATIONS_BY_NAME_ORDER_BY_NAME, Organisation.class)
				.setParameter(Organisation.PARAM_Name,
				"%" + name.toUpperCase() + "%");
		
		switch (fetch) {
		case WITH_SQUADS:
			entityGraph = em.getEntityGraph(Organisation.GRAPH_SQUADS);
			query.setHint(LOADGRAPH, entityGraph);
			break;
		case ORGANISATION_ONLY:
		default:
			break;
		}

		return query.getResultList();
	}

	public List<Organisation> findAllOrganisations(FetchType fetch, OrderByType order) {
		
		EntityGraph<?> entityGraph;
		TypedQuery<Organisation> query = null;
		
		query = OrderByType.NAME.equals(order) 
				? em.createNamedQuery(Organisation.FIND_ALL_ORGANISATIONS_ORDER_BY_NAME, Organisation.class)
				: em.createNamedQuery(Organisation.FIND_ALL_ORGANISATIONS, Organisation.class);
				
		switch (fetch) {
		case WITH_SQUADS:
			entityGraph = em.getEntityGraph(Organisation.GRAPH_SQUADS);
			query.setHint(LOADGRAPH, entityGraph);
			break;

		case ORGANISATION_ONLY:
		default:
			break;
		}

		return query.getResultList();
	}
	
	@NotNull(message = "{organisation.notFound}")
	public List<Organisation> findOrganisationByCriteria(String name, String street, String zip, String city) {

		final CriteriaBuilder builder = em.getCriteriaBuilder();
		final CriteriaQuery<Organisation> criteriaQuery = builder
				.createQuery(Organisation.class);
		final Root<Organisation> c = criteriaQuery.from(Organisation.class);

		Predicate pred = null;

		if (name != null) {
			final Path<String> namePath = c.get(Organisation_.name);
			pred = builder.equal(builder.upper(namePath),
					builder.upper(builder.literal(name)));
		}
		if (street != null) {
			final Path<String> streetPath = c.get(Organisation_.street);
			final Predicate tmpPred = builder.equal(builder.upper(streetPath),
					builder.upper(builder.literal(street)));
			pred = pred == null ? tmpPred : builder.and(pred, tmpPred);
		}
		if (zip != null) {
			final Path<String> zipPath = c.get(Organisation_.zip);
			final Predicate tmpPred = builder.equal(builder.upper(zipPath),
					builder.upper(builder.literal(zip)));
			pred = pred == null ? tmpPred : builder.and(pred, tmpPred);
		}
		if (city != null) {
			final Path<String> cityPath = c.get(Organisation_.city);
			final Predicate tmpPred = builder.equal(builder.upper(cityPath),
					builder.upper(builder.literal(city)));
			pred = pred == null ? tmpPred : builder.and(pred, tmpPred);
		}

		criteriaQuery.where(pred).distinct(true);

		final List<Organisation> organisations = em.createQuery(criteriaQuery).getResultList();

		return organisations;
	}

	public <T extends Organisation> T createOrganisation(T organisation) {
		if (organisation == null) {
			return organisation;
		}

		em.persist(organisation);

		return organisation;
	}

	public <T extends Organisation> T updateOrganisation(T organisation) {
		if (organisation == null) {
			return null;
		}

		em.detach(organisation);

		// Was the object deleted competitive?
		Organisation tmp = findOrganisationById(organisation.getId(), FetchType.ORGANISATION_ONLY);
		if (tmp == null) {
			throw new ConcurrentDeletedException(organisation.getId());
		}
		em.detach(tmp);

		organisation = em.merge(organisation);
		return organisation;
	}

	public void deleteOrganisation(Organisation organisation) {
		if (organisation == null) {
			return;
		}

		deleteOrganisationById(organisation.getId());
	}
	
	public void deleteOrganisationById(Long id) {
		final Organisation organisation = findOrganisationById(id, FetchType.ORGANISATION_ONLY);
		if (organisation == null) {
			// Organisation does not exist or is already deleted
			return;
		}

		em.remove(organisation);
	}
}
