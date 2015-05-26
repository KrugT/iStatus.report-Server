package de.helfenkannjeder.istatus.server.rest;

import static de.helfenkannjeder.istatus.server.rest.Constants.REST_PATH;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath(REST_PATH)
public class JaxRsActivator extends Application {
}
