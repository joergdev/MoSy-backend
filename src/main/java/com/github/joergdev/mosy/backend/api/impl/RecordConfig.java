package com.github.joergdev.mosy.backend.api.impl;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import com.github.joergdev.mosy.api.APIConstants;
import com.github.joergdev.mosy.api.response.EmptyResponse;
import com.github.joergdev.mosy.api.response.recordconfig.LoadResponse;
import com.github.joergdev.mosy.api.response.recordconfig.SaveResponse;
import com.github.joergdev.mosy.backend.api.APIUtils;
import com.github.joergdev.mosy.backend.bl.recordconfig.Delete;
import com.github.joergdev.mosy.backend.bl.recordconfig.Load;
import com.github.joergdev.mosy.backend.bl.recordconfig.Save;

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
                       com.github.joergdev.mosy.api.model.RecordConfig recordConfig)
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