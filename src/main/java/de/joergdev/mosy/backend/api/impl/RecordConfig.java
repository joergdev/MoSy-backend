package de.joergdev.mosy.backend.api.impl;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
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