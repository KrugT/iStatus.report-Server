package de.helfenkannjeder.istatus.server.rest;

import static de.helfenkannjeder.istatus.server.rest.Constants.ADD_LINK;
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
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.helfenkannjeder.istatus.server.business.SquadService;
import de.helfenkannjeder.istatus.server.business.SquadService.FetchType;
import de.helfenkannjeder.istatus.server.domain.Squad;

@Path("/squads")
@Produces({ APPLICATION_JSON})
@Consumes({ APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
@Stateless
public class SquadResource {
	
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	public static final String SQUAD_ID_PATH_PARAM = "id";
	
	@Context
	private UriInfo uriInfo;
	
	@Inject
	private UriHelper uriHelper;

	@Inject
	private SquadService ss;
	
	private static final String REST_METHOD_FIND_SQUAD_BY_ID = "findSquadById";
	/**
	 * Get a squad with a GET to URI /squads/{id}
	 * @param id ID of the squad
	 * @return Squad with specified id, if there exists one
	 */
	@GET
	@Path("{" + SQUAD_ID_PATH_PARAM + ":[1-9][0-9]*}")
	public Response findSquadById(@PathParam(SQUAD_ID_PATH_PARAM) Long id) {
		
		final Squad squad = ss.findSquadById(id, FetchType.WITH_MEMBERS);

		if (squad == null) {
			final String msg = "no squad found with id " + id;
			throw new NotFoundException(msg);
		}

		setStructuralLinks(squad, uriInfo);

		return Response.ok(squad).links(getTransitionalLinks(squad, uriInfo))
				.build();
	}
	
	/**
	 * Create a new squad with POST to uri /squads
	 * @param squad new squad
	 * @return response-object with URI of the created squad
	 */
	@POST
	public Response create(@Valid Squad squad) {
		
		squad = ss.createSquad(squad);
		if (LOGGER.isLoggable(FINEST)) {
			LOGGER.finest(squad.toString());
		}
		
		return Response.created(getUriSquad(squad, uriInfo))
				       .build();
	}
	
	/**
	 * Update a squad with PUT to uri /squads
	 * @param squad Squad to update
	 * @return Updated squad
	 */
	@PUT
	public Response update(@Valid Squad squad) {
		final Squad origSquad = ss.findSquadById(squad.getId(), FetchType.SQUAD_ONLY);
	
		// update values of existing memberAbsence
		origSquad.setValues(squad);
		
		// execute update
		squad = ss.updateSquad(origSquad);
		setStructuralLinks(squad, uriInfo);
		
		return Response.ok(squad)
				       .links(getTransitionalLinks(squad, uriInfo))
				       .build();
	}
	
	private static final String REST_METHOD_DELETE_SQUAD = "deleteSquad";
	/**
	 * Delete a squad with a DELETE to URI /squads/{id}
	 * @param id Id of the squad which should be deleted
	 */
	@Path("{" + SQUAD_ID_PATH_PARAM + ":[1-9][0-9]*}")
	@DELETE
	public void deleteSquad(@PathParam(SQUAD_ID_PATH_PARAM) long id) {
		ss.deleteSquadById(id);
	}
	
	public Link[] getTransitionalLinks(Squad squad, UriInfo uriInfo) {
		final Link self = Link.fromUri(getUriSquad(squad, uriInfo))
				.rel(SELF_LINK)
				.build();

		final Link list = Link
		.fromUri(uriHelper.getUri(SquadResource.class, uriInfo))
		.rel(LIST_LINK)
		.build();
		
		final Link add = Link
		.fromUri(uriHelper.getUri(SquadResource.class, uriInfo))
		.rel(ADD_LINK)
		.build();
		
		final Link update = Link
		.fromUri(uriHelper.getUri(SquadResource.class, uriInfo))
		.rel(UPDATE_LINK)
		.build();
		
		final Link remove = Link
		.fromUri(
				uriHelper.getUri(SquadResource.class, REST_METHOD_DELETE_SQUAD,
						squad.getId(), uriInfo)).rel(REMOVE_LINK)
		.build();
		
		return new Link[] { self, list, add, update, remove };
	}
	
	public URI getUriSquad(Squad squad, UriInfo uriInfo) {
		return uriHelper.getUri(SquadResource.class, REST_METHOD_FIND_SQUAD_BY_ID,
				squad.getId(), uriInfo);
	}
	
	public void setStructuralLinks(Squad squad, UriInfo uriInfo) {
		if (squad == null || uriInfo == null) {
			throw new InvalidParameterException(
					"Invalid parameter at setStructuralLinks: squad or uriInfo");
		}

		//TODO set uris in squad
	}
}
