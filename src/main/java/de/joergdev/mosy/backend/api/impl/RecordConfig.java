package de.joergdev.mosy.backend.api.impl;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import de.joergdev.mosy.api.APIConstants;
import de.joergdev.mosy.api.response.EmptyResponse;
import de.joergdev.mosy.api.response.recordconfig.LoadResponse;
import de.joergdev.mosy.api.response.recordconfig.SaveResponse;
import de.joergdev.mosy.backend.api.APIUtils;
import de.joergdev.mosy.backend.bl.recordconfig.Delete;
import de.joergdev.mosy.backend.bl.recordconfig.Load;
import de.joergdev.mosy.backend.bl.recordconfig.Save;

@Path(APIConstants.API_URL_BASE + "record-config")
public class RecordConfig
{
  @Path(value = "{id}")
  @GET
  public Response load(@HeaderParam(HttpHeaders.AUTHORIZATION) String token, @PathParam("id") Integer id)
  {
    return APIUtils.executeBL(id, new LoadResponse(), new Load(), token);
  }

  @Path("save")
  @POST
  public Response save(@HeaderParam(HttpHeaders.AUTHORIZATION) String token,
                       de.joergdev.mosy.api.model.RecordConfig recordConfig)
  {
    return APIUtils.executeBL(recordConfig, new SaveResponse(), new Save(), token);
  }

  @Path("{id}/delete")
  @DELETE
  public Response delete(@HeaderParam(HttpHeaders.AUTHORIZATION) String token, @PathParam("id") Integer id)
  {
    return APIUtils.executeBL(id, new EmptyResponse(), new Delete(), token);
  }
}