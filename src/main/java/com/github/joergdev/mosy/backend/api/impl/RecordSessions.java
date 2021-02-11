package com.github.joergdev.mosy.backend.api.impl;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import com.github.joergdev.mosy.api.APIConstants;
import com.github.joergdev.mosy.api.response.EmptyResponse;
import com.github.joergdev.mosy.api.response.record.session.CreateResponse;
import com.github.joergdev.mosy.api.response.record.session.LoadSessionsResponse;
import com.github.joergdev.mosy.backend.api.APIUtils;
import com.github.joergdev.mosy.backend.bl.record.session.Create;
import com.github.joergdev.mosy.backend.bl.record.session.Delete;
import com.github.joergdev.mosy.backend.bl.record.session.LoadSessions;

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