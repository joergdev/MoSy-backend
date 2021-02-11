package com.github.joergdev.mosy.backend.api.impl;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import com.github.joergdev.mosy.api.APIConstants;
import com.github.joergdev.mosy.api.model.BaseData;
import com.github.joergdev.mosy.api.response.EmptyResponse;
import com.github.joergdev.mosy.backend.api.APIUtils;
import com.github.joergdev.mosy.backend.bl.globalconfig.Save;

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