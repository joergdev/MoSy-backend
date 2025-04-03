package de.joergdev.mosy.backend.api.impl;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
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