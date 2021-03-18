package de.joergdev.mosy.backend.api.impl;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import de.joergdev.mosy.api.APIConstants;
import de.joergdev.mosy.api.model.BaseData;
import de.joergdev.mosy.api.response.EmptyResponse;
import de.joergdev.mosy.backend.api.APIUtils;
import de.joergdev.mosy.backend.bl.globalconfig.Save;

@Path(APIConstants.API_URL_BASE + "globalconfig")
public class Globalconfig
{
  @Path(value = "save")
  @POST
  public Response save(@HeaderParam(HttpHeaders.AUTHORIZATION) String token, BaseData basedata)
  {
    return APIUtils.executeBL(basedata, new EmptyResponse(), new Save(), token);
  }
}