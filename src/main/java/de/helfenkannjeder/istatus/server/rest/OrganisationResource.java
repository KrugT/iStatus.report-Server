package de.helfenkannjeder.istatus.server.rest;

import static de.helfenkannjeder.istatus.server.rest.Constants.ADD_LINK;
import static de.helfenkannjeder.istatus.server.rest.Constants.FIRST_LINK;
import static de.helfenkannjeder.istatus.server.rest.Constants.LAST_LINK;
import static de.helfenkannjeder.istatus.server.rest.Constants.LIST_LINK;
import static de.helfenkannjeder.istatus.server.rest.Constants.REMOVE_LINK;
import static de.helfenkannjeder.istatus.server.rest.Constants.SELF_LINK;
import static de.helfenkannjeder.istatus.server.rest.Constants.UPDATE_LINK;
import static java.util.logging.Level.FINEST;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_XML;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.google.common.base.Strings;

import de.helfenkannjeder.istatus.server.business.OrganisationService;
import de.helfenkannjeder.istatus.server.business.OrganisationService.FetchType;
import de.helfenkannjeder.istatus.server.business.OrganisationService.OrderByType;
import de.helfenkannjeder.istatus.server.domain.Organisation;

@Path("/organisations")
@Produces({ APPLICATION_JSON})
@Consumes({ APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
@Stateless
public class OrganisationResource {
	
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	public static final String ORGANISATION_ID_PATH_PARAM = "id";
	public static final String ORGANISATION_NAME_QUERY_PARAM = "name";
	public static final String ORGANISATION_STREET_QUERY_PARAM = "street";
	public static final String ORGANISATION_ZIP_QUERY_PARAM = "zip";
	public static final String ORGANISATION_CITY_QUERY_PARAM = "city";
	
	@Context
	private UriInfo uriInfo;
	
	@Inject
	private UriHelper uriHelper;

	@Inject
	private OrganisationService os;
	
	private static final String REST_METHOD_FIND_ORGANISATION_BY_ID = "findOrganisationById";
	/**
	 * Get a Organisation with a GET to URI /organisations/{id}
	 * @param id ID of the organisation
	 * @return Organisation with specified id, if there exists one
	 */
	@GET
	@Path("{" + ORGANISATION_ID_PATH_PARAM + ":[1-9][0-9]*}")
	public Response findOrganisationById(@PathParam(ORGANISATION_ID_PATH_PARAM) Long id) {
		final Organisation organisation = os.findOrganisationById(id, FetchType.ORGANISATION_ONLY);

		if (organisation == null) {
			final String msg = "no organisation found with id " + id;
			throw new NotFoundException(msg);
		}

		setStructuralLinks(organisation, uriInfo);

		return Response.ok(organisation).links(getTransitionalLinks(organisation, uriInfo))
				.build();
	}
	
	/**
	 * With a GET to uri /organisatins all organisations will be returned. 
	 * Or With organisations?name=...  those with a specific name
	 * @param name Name of the organisation
	 * @param street Name of the street the organisation is based at
	 * @param zip Zip-code of the city the organisaiton is based at
	 * @param city City the organisaiton is based at
	 * @return Collection of found organisations
	 * @throws Exception 
	 */
	@GET
	public Response findOrganisations(
			@QueryParam(ORGANISATION_NAME_QUERY_PARAM) String name,
			@QueryParam(ORGANISATION_STREET_QUERY_PARAM) String street,
			@QueryParam(ORGANISATION_ZIP_QUERY_PARAM) 
			@Pattern(regexp = "\\d{5}", message = "{organisation.zip}")
			String zip,
			@QueryParam(ORGANISATION_CITY_QUERY_PARAM) String city) {

		List<? extends Organisation> organisations = null;

		// no query-parameter
		if (Strings.isNullOrEmpty(name)
				&& Strings.isNullOrEmpty(street)
				&& Strings.isNullOrEmpty(zip)
				&& Strings.isNullOrEmpty(city)) {
			organisations = os.findAllOrganisations(FetchType.ORGANISATION_ONLY, OrderByType.UNORDERED);
		}
		else {
			organisations = os.findOrganisationByCriteria(name, street, zip, city);
		}

		if (organisations == null) {
			final String msg = "No matching organisation found";
			throw new NotFoundException(msg);
		}

		Object entity = null;
		Link[] links = null;
		if (organisations != null) {
			organisations.forEach(o -> setStructuralLinks(o, uriInfo));
			entity = new GenericEntity<List<? extends Organisation>>(organisations){};
			links = getTransitionalLinksOrganisations(organisations, uriInfo);
		}

		return Response.ok(entity)
				.links(links)
				.build();
	}
	
	/**
	 * Create a new organisation with POST to uri /organisations
	 * @param organisation new organisation
	 * @return response-object with URI of the created organisaiton
	 */
	@POST
	public Response create(@Valid Organisation organisation) {
		
		organisation = os.createOrganisation(organisation);
		if (LOGGER.isLoggable(FINEST)) {
			LOGGER.finest(organisation.toString());
		}
		
		return Response.created(getUriOrganisation(organisation, uriInfo))
				       .build();
	}
	
	/**
	 * Update a organisaiton with PUT to uri /organisations
	 * @param organisation organisation to update
	 * @return Updated organisaiton
	 */
	@PUT
	public Response updateOrganisation(@Valid Organisation organisation) {
		// Vorhandenen Benutzer ermitteln
		final Organisation origOrganisation = os.findOrganisationById(organisation.getId(), FetchType.ORGANISATION_ONLY);
		if (LOGGER.isLoggable(FINEST)) {
			LOGGER.finest("Organisation before update = " + origOrganisation);
			LOGGER.finest("Values from PUT-Request = " + organisation);
		}
	
		// update values of existing Organisation
		origOrganisation.setValues(organisation);
		if (LOGGER.isLoggable(FINEST)) {
			LOGGER.finest("Organisation after update = " + origOrganisation);
		}
		
		// execute update
		organisation = os.updateOrganisation(origOrganisation);
		setStructuralLinks(organisation, uriInfo);
		
		return Response.ok(organisation)
				       .links(getTransitionalLinks(organisation, uriInfo))
				       .build();
	}
	
	private static final String REST_METHOD_DELETE_ORGANISATION = "deleteOrganisation";
	/**
	 * Delete a organisaiton with a DELETE to URI /organisaitons{id}
	 * @param id Id of the organisation which should be deleted
	 */
	@Path("{" + ORGANISATION_ID_PATH_PARAM + ":[1-9][0-9]*}")
	@DELETE
	public void deleteOrganisation(@PathParam(ORGANISATION_ID_PATH_PARAM) long id) {
		os.deleteOrganisationById(id);
	}
	
	public Link[] getTransitionalLinks(Organisation organisation, UriInfo uriInfo) {
		final Link self = Link.fromUri(getUriOrganisation(organisation, uriInfo))
								.rel(SELF_LINK)
								.build();

		final Link list = Link
				.fromUri(uriHelper.getUri(OrganisationResource.class, uriInfo))
				.rel(LIST_LINK)
				.build();

		final Link add = Link
				.fromUri(uriHelper.getUri(OrganisationResource.class, uriInfo))
				.rel(ADD_LINK)
				.build();

		final Link update = Link
				.fromUri(uriHelper.getUri(OrganisationResource.class, uriInfo))
				.rel(UPDATE_LINK)
				.build();

		final Link remove = Link
				.fromUri(
						uriHelper.getUri(OrganisationResource.class, REST_METHOD_DELETE_ORGANISATION,
								organisation.getId(), uriInfo)).rel(REMOVE_LINK)
				.build();

		return new Link[] { self, list, add, update, remove };
	}

	private Link[] getTransitionalLinksOrganisations(List<? extends Organisation> organisations,
			UriInfo uriInfo) {
		if (organisations == null || organisations.isEmpty()) {
			return null;
		}

		final Link first = Link.fromUri(getUriOrganisation(organisations.get(0), uriInfo))
								.rel(FIRST_LINK)
								.build();
		final int lastPos = organisations.size() - 1;
		final Link last = Link.fromUri(getUriOrganisation(organisations.get(lastPos), uriInfo))
							.rel(LAST_LINK)
							.build();

		return new Link[] { first, last };
	}
	
	public URI getUriOrganisation(Organisation organisation, UriInfo uriInfo) {
		return uriHelper.getUri(OrganisationResource.class, REST_METHOD_FIND_ORGANISATION_BY_ID,
				organisation.getId(), uriInfo);
	}
	
	public void setStructuralLinks(Organisation organisation, UriInfo uriInfo) {
		if (organisation == null || uriInfo == null) {
			throw new InvalidParameterException(
					"Invalid parameter at setStructuralLinks: organisation or uriInfo");
		}

//		if (organisation.getTourStationen() != null) {
//			for (TourStation station : organisation.getTourStationen()) {
//				setStructuralLinksTourStation(station, uriInfo);
//			}
//		}
	}
}
