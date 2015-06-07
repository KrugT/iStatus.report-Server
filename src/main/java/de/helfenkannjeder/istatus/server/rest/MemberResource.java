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

import de.helfenkannjeder.istatus.server.business.MemberService;
import de.helfenkannjeder.istatus.server.business.MemberService.FetchType;
import de.helfenkannjeder.istatus.server.domain.Member;

@Path("/member")
@Produces({ APPLICATION_JSON})
@Consumes({ APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
@Stateless
public class MemberResource {
	
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	
	public static final String MEMBER_ID_PATH_PARAM = "id";
	
	@Context
	private UriInfo uriInfo;
	
	@Inject
	private UriHelper uriHelper;

	@Inject
	private MemberService ms;
	
	private static final String REST_METHOD_FIND_MEMBER_BY_ID = "findMemberById";
	/**
	 * Get a member with a GET to URI /members/{id}
	 * @param id ID of the member
	 * @return Member with specified id, if there exists one
	 */
	@GET
	@Path("{" + MEMBER_ID_PATH_PARAM + ":[1-9][0-9]*}")
	public Response findMemberById(@PathParam(MEMBER_ID_PATH_PARAM) Long id) {
		final Member member = ms.findMemberById(id, FetchType.MEMBER_ONLY);

		if (member == null) {
			final String msg = "no member found with id " + id;
			throw new NotFoundException(msg);
		}

		setStructuralLinks(member, uriInfo);

		return Response.ok(member).links(getTransitionalLinks(member, uriInfo))
				.build();
	}
	
	/**
	 * Create a new member with POST to uri /members
	 * @param member new member
	 * @return response-object with URI of the created member
	 */
	@POST
	public Response create(@Valid Member member) {
		
		member = ms.createMember(member);
		if (LOGGER.isLoggable(FINEST)) {
			LOGGER.finest(member.toString());
		}
		
		return Response.created(getUriMember(member, uriInfo))
				       .build();
	}
	
	/**
	 * Update a member with PUT to uri /members
	 * @param member member to update
	 * @return Updated member
	 */
	@PUT
	public Response updateMember(@Valid Member member) {
		final Member origMember = ms.findMemberById(member.getId(), FetchType.MEMBER_ONLY);
	
		// update values of existing member
		origMember.setValues(member);
		
		// execute update
		member = ms.updateMember(origMember);
		setStructuralLinks(member, uriInfo);
		
		return Response.ok(member)
				       .links(getTransitionalLinks(member, uriInfo))
				       .build();
	}
	
	private static final String REST_METHOD_DELETE_ORGANISATION = "deleteMember";
	/**
	 * Delete a member with a DELETE to URI /members/{id}
	 * @param id Id of the member which should be deleted
	 */
	@Path("{" + MEMBER_ID_PATH_PARAM + ":[1-9][0-9]*}")
	@DELETE
	public void deleteMember(@PathParam(MEMBER_ID_PATH_PARAM) long id) {
		ms.deleteMemberById(id);
	}
	
	public Link[] getTransitionalLinks(Member member, UriInfo uriInfo) {
		final Link self = Link.fromUri(getUriMember(member, uriInfo))
								.rel(SELF_LINK)
								.build();

		final Link list = Link
				.fromUri(uriHelper.getUri(MemberAbsenceResource.class, uriInfo))
				.rel(LIST_LINK)
				.build();

		final Link add = Link
				.fromUri(uriHelper.getUri(MemberAbsenceResource.class, uriInfo))
				.rel(ADD_LINK)
				.build();

		final Link update = Link
				.fromUri(uriHelper.getUri(MemberAbsenceResource.class, uriInfo))
				.rel(UPDATE_LINK)
				.build();

		final Link remove = Link
				.fromUri(
						uriHelper.getUri(MemberAbsenceResource.class, REST_METHOD_DELETE_ORGANISATION,
								member.getId(), uriInfo)).rel(REMOVE_LINK)
				.build();

		return new Link[] { self, list, add, update, remove };
	}

	private Link[] getTransitionalLinksMembers(List<? extends Member> members,
			UriInfo uriInfo) {
		if (members == null || members.isEmpty()) {
			return null;
		}

		final Link first = Link.fromUri(getUriMember(members.get(0), uriInfo))
								.rel(FIRST_LINK)
								.build();
		final int lastPos = members.size() - 1;
		final Link last = Link.fromUri(getUriMember(members.get(lastPos), uriInfo))
							.rel(LAST_LINK)
							.build();

		return new Link[] { first, last };
	}
	
	public URI getUriMember(Member member, UriInfo uriInfo) {
		return uriHelper.getUri(MemberAbsenceResource.class, REST_METHOD_FIND_MEMBER_BY_ID,
				member.getId(), uriInfo);
	}
	
	public void setStructuralLinks(Member member, UriInfo uriInfo) {
		if (member == null || uriInfo == null) {
			throw new InvalidParameterException(
					"Invalid parameter at setStructuralLinks: member or uriInfo");
		}

		//TODO set uris in member
	}
}
