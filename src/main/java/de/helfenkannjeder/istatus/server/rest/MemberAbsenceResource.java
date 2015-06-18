package de.helfenkannjeder.istatus.server.rest;

import static de.helfenkannjeder.istatus.server.rest.Constants.ADD_LINK;
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

import de.helfenkannjeder.istatus.server.business.MemberAbsenceService;
import de.helfenkannjeder.istatus.server.business.MemberAbsenceService.FetchType;
import de.helfenkannjeder.istatus.server.domain.MemberAbsence;

@Path("/memberAbsence")
@Produces({ APPLICATION_JSON})
@Consumes({ APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
@Stateless
public class MemberAbsenceResource {
	
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	public static final String MEMBER_ID_PATH_PARAM = "id";
	
	@Context
	private UriInfo uriInfo;
	
	@Inject
	private UriHelper uriHelper;

	@Inject
	private MemberAbsenceService ma;
	
	private static final String REST_METHOD_FIND_MEMBER_BY_ID = "findMemberById";
	/**
	 * Get a member absence with a GET to URI /memberAbsence/{id}
	 * @param id ID of the member absence
	 * @return Member absence with specified id, if there exists one
	 */
	@GET
	@Path("{" + MEMBER_ID_PATH_PARAM + ":[1-9][0-9]*}")
	public Response findMemberAbsenceById(@PathParam(MEMBER_ID_PATH_PARAM) Long id) {
		//FIXME This method should not be needed for production
		
		final MemberAbsence memberAbsence = ma.findMemberAbsenceById(id, FetchType.MEMBER_ABSENCE_ONLY);

		if (memberAbsence == null) {
			final String msg = "no memberAbsence found with id " + id;
			throw new NotFoundException(msg);
		}

		setStructuralLinks(memberAbsence, uriInfo);

		return Response.ok(memberAbsence).links(getTransitionalLinks(memberAbsence, uriInfo))
				.build();
	}
	
	/**
	 * Create a new member absence with POST to uri /memberAbsence
	 * @param memberAbsence new member absence
	 * @return response-object with URI of the created member absence
	 */
	@POST
	public Response create(@Valid MemberAbsence memberAbsence) {
		
		memberAbsence = ma.createMemberAbsence(memberAbsence);
		if (LOGGER.isLoggable(FINEST)) {
			LOGGER.finest(memberAbsence.toString());
		}
		
		return Response.created(getUriMemberAbsence(memberAbsence, uriInfo))
				       .build();
	}
	
	/**
	 * Update a member absence with PUT to uri /memberAbsence
	 * @param memberAbsence member absence to update
	 * @return Updated member absence
	 */
	@PUT
	public Response updateMemberAbsence(@Valid MemberAbsence memberAbsence) {
		final MemberAbsence origMemberAbsence = ma.findMemberAbsenceById(memberAbsence.getId(), FetchType.MEMBER_ABSENCE_ONLY);
	
		// update values of existing memberAbsence
		origMemberAbsence.setValues(memberAbsence);
		
		// execute update
		memberAbsence = ma.updateMemberAbsence(origMemberAbsence);
		setStructuralLinks(memberAbsence, uriInfo);
		
		return Response.ok(memberAbsence)
				       .links(getTransitionalLinks(memberAbsence, uriInfo))
				       .build();
	}
	
	public Link[] getTransitionalLinks(MemberAbsence memberAbsence, UriInfo uriInfo) {
		final Link self = Link.fromUri(getUriMemberAbsence(memberAbsence, uriInfo))
								.rel(SELF_LINK)
								.build();

		final Link add = Link
				.fromUri(uriHelper.getUri(MemberResource.class, uriInfo))
				.rel(ADD_LINK)
				.build();

		final Link update = Link
				.fromUri(uriHelper.getUri(MemberResource.class, uriInfo))
				.rel(UPDATE_LINK)
				.build();

		return new Link[] { self, add, update };
	}
	
	public URI getUriMemberAbsence(MemberAbsence memberAbsence, UriInfo uriInfo) {
		return uriHelper.getUri(MemberResource.class, REST_METHOD_FIND_MEMBER_BY_ID,
				memberAbsence.getId(), uriInfo);
	}
	
	public void setStructuralLinks(MemberAbsence memberAbsence, UriInfo uriInfo) {
		if (memberAbsence == null || uriInfo == null) {
			throw new InvalidParameterException(
					"Invalid parameter at setStructuralLinks: memberAbsence or uriInfo");
		}

		//TODO set uris in member absence
	}
}
