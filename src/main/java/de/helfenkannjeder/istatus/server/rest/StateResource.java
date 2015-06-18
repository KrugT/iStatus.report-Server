package de.helfenkannjeder.istatus.server.rest;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_XML;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.helfenkannjeder.istatus.server.business.StateService;

@Path("/state")
@Produces({ APPLICATION_JSON})
@Consumes({ APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
@Stateless
public class StateResource {
	
	public static final String USERNAME_PATH_PARAM = "userId";
	
	@Context
	private UriInfo uriInfo;

	@Inject
	private StateService ss;
	
	/**
	 * Set the state of the authenticated user to available with a GET to URI /setAvailable
	 * @return 
	 */
	@GET
	@Path("setAvailable/{" + USERNAME_PATH_PARAM + ":[1-9][0-9]*}")
	public Response setAvailable(@PathParam(USERNAME_PATH_PARAM) Long id) {
		if (id == null) {
			final String msg = "user id is required";
			throw new InvalidParameterException(msg);
		}
		
		try {
			ss.setAvailable(id);
		} catch (Exception e) {
			final String msg = "can not update state for user";
			throw new InvalidParameterException(msg);
		}		

		return Response.ok()
				.build();
	}
	
	/**
	 * Set the state of the authenticated user to unavailable with a GET to URI /setUnavailable
	 * @return 
	 */
	@GET
	@Path("setUnavailable/{" + USERNAME_PATH_PARAM + ":[1-9][0-9]*}")
	public Response setUnavailable(@PathParam(USERNAME_PATH_PARAM) Long id) {
		if (id == null) {
			final String msg = "user id is required";
			throw new InvalidParameterException(msg);
		}
		
		ss.setUnavailable(id);	

		return Response.ok()
				.build();
	}
}
