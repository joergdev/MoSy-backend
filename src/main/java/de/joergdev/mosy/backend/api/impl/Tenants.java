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
import de.joergdev.mosy.api.request.tenant.SaveRequest;
import de.joergdev.mosy.api.response.EmptyResponse;
import de.joergdev.mosy.api.response.tenant.LoadAllResponse;
import de.joergdev.mosy.api.response.tenant.SaveResponse;
import de.joergdev.mosy.backend.api.APIUtils;
import de.joergdev.mosy.backend.bl.tenant.Delete;
import de.joergdev.mosy.backend.bl.tenant.LoadAll;
import de.joergdev.mosy.backend.bl.tenant.Save;

@Path(APIConstants.API_URL_BASE + "tenants")
public class Tenants
{
  @GET
  public Response loadAll()
  {
    return APIUtils.executeBL(null, new LoadAllResponse(), new LoadAll());
  }

  @Path("save")
  @POST
  public Response save(@HeaderParam(HttpHeaders.AUTHORIZATION) String token, SaveRequest request)
  {
    return APIUtils.executeBL(request, new SaveResponse(), new Save(), token);
  }

  @Path("{id}/delete")
  @DELETE
  public Response delete(@HeaderParam(HttpHeaders.AUTHORIZATION) String token, @PathParam("id") Integer id)
  {
    return APIUtils.executeBL(id, new EmptyResponse(), new Delete(), token);
  }
}
