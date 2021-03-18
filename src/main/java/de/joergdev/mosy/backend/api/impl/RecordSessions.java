package de.joergdev.mosy.backend.api.impl;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import de.joergdev.mosy.api.APIConstants;
import de.joergdev.mosy.api.response.EmptyResponse;
import de.joergdev.mosy.api.response.record.session.CreateResponse;
import de.joergdev.mosy.api.response.record.session.LoadSessionsResponse;
import de.joergdev.mosy.backend.api.APIUtils;
import de.joergdev.mosy.backend.bl.record.session.Create;
import de.joergdev.mosy.backend.bl.record.session.Delete;
import de.joergdev.mosy.backend.bl.record.session.LoadSessions;

@Path(APIConstants.API_URL_BASE + "record-sessions")
public class RecordSessions
{
  @GET
  public Response loadSessions(@HeaderParam(HttpHeaders.AUTHORIZATION) String token)
  {
    return APIUtils.executeBL(null, new LoadSessionsResponse(), new LoadSessions(), token);
  }

  @Path("create")
  @POST
  public Response create(@HeaderParam(HttpHeaders.AUTHORIZATION) String token)
  {
    return APIUtils.executeBL(null, new CreateResponse(), new Create(), token, Status.CREATED);
  }

  @Path("{id}/delete")
  @DELETE
  public Response delete(@HeaderParam(HttpHeaders.AUTHORIZATION) String token, @PathParam("id") Integer id)
  {
    return APIUtils.executeBL(id, new EmptyResponse(), new Delete(), token);
  }
}