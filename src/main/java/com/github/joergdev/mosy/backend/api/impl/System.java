package com.github.joergdev.mosy.backend.api.impl;

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
import com.github.joergdev.mosy.api.response.system.LoadBaseDataResponse;
import com.github.joergdev.mosy.api.response.system.LoginResponse;
import com.github.joergdev.mosy.backend.api.APIUtils;
import com.github.joergdev.mosy.backend.bl.system.Boot;
import com.github.joergdev.mosy.backend.bl.system.LoadBaseData;
import com.github.joergdev.mosy.backend.bl.system.Login;
import com.github.joergdev.mosy.backend.bl.system.Logout;

@Path("mosy/api/v_1_0/system")
public class System
{
  @Path(value = "login")
  @POST
  public Response login(Integer hash)
  {
    return APIUtils.executeBL(hash, new LoginResponse(), new Login());
  }

  @Path(value = "logout")
  @POST
  public Response logout(@HeaderParam(HttpHeaders.AUTHORIZATION) String token)
  {
    return APIUtils.executeBL(null, new EmptyResponse(), new Logout(), token);
  }

  @Path(value = "load-basedata")
  @GET
  public Response loadBaseData(@HeaderParam(HttpHeaders.AUTHORIZATION) String token)
  {
    return APIUtils.executeBL(null, new LoadBaseDataResponse(), new LoadBaseData(), token);
  }

  @Path(value = "boot")
  @POST
  public Response boot(@HeaderParam(HttpHeaders.AUTHORIZATION) String token)
  {
    return APIUtils.executeBL(null, new EmptyResponse(), new Boot(), token);
  }

  @Path(value = "state")
  @GET
  public Response getState()
  {
    return Response.ok().build();
  }

  @Path(value = "state-subsystem/{subsystem}")
  @GET
  public Response getStateSubsystem(@PathParam("subsystem") String subsystem)
  {
    if (APIConstants.SUBSYSTEM_PERSISTENCE.equals(subsystem))
    {
      return Response.ok().build();
    }

    return Response.status(Status.BAD_REQUEST).entity("No subsystem " + subsystem).build();
  }
}