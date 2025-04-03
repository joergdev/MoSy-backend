package de.joergdev.mosy.backend.api.impl;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import de.joergdev.mosy.api.APIConstants;
import de.joergdev.mosy.api.model.BaseData;
import de.joergdev.mosy.api.response.EmptyResponse;
import de.joergdev.mosy.api.response.system.LoadBaseDataResponse;
import de.joergdev.mosy.api.response.system.LoginResponse;
import de.joergdev.mosy.backend.api.APIUtils;
import de.joergdev.mosy.backend.bl.system.Boot;
import de.joergdev.mosy.backend.bl.system.ImportData;
import de.joergdev.mosy.backend.bl.system.LoadBaseData;
import de.joergdev.mosy.backend.bl.system.Login;
import de.joergdev.mosy.backend.bl.system.Logout;

@Path(APIConstants.API_URL_BASE + "system")
public class System
{
  @Path(value = "login")
  @POST
  public Response login(de.joergdev.mosy.api.request.system.LoginRequest loginRequest)
  {
    return APIUtils.executeBL(loginRequest, new LoginResponse(), new Login());
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
  
  @Path(value = "import-data")
  @POST
  public Response importData(@HeaderParam(HttpHeaders.AUTHORIZATION) String token, BaseData apiBaseData)
  {
    return APIUtils.executeBL(apiBaseData, new EmptyResponse(), new ImportData(), token);
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

  @Path(value = "version")
  @GET
  public Response getVersion()
  {
    return Response.ok().entity("5.0.0").build();
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
