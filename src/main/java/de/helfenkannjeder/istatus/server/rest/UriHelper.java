package de.helfenkannjeder.istatus.server.rest;

import static javax.ejb.TransactionAttributeType.SUPPORTS;

import java.net.URI;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ws.rs.core.UriInfo;

@Stateless
@TransactionAttribute(SUPPORTS)
public class UriHelper {
	
	public URI getUri(Class<?> clazz, UriInfo uriInfo) {
		return uriInfo.getBaseUriBuilder()
				      .path(clazz)
				      .build();
	}

	public URI getUri(Class<?> clazz, String methodName, Long id, UriInfo uriInfo) {		
		return uriInfo.getBaseUriBuilder()
	              .path(clazz)
	              .path(clazz, methodName)
	              .build(id);
	}
}
