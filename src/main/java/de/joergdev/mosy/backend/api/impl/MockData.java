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
import de.joergdev.mosy.api.response.mockdata.LoadResponse;
import de.joergdev.mosy.api.response.mockdata.SaveResponse;
import de.joergdev.mosy.backend.api.APIUtils;
import de.joergdev.mosy.backend.bl.mockdata.Delete;
import de.joergdev.mosy.backend.bl.mockdata.Load;
import de.joergdev.mosy.backend.bl.mockdata.Save;

@Path(APIConstants.API_URL_BASE + "mockdata")
public class MockData
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
                       de.joergdev.mosy.api.model.MockData mockdata)
  {
    return APIUtils.executeBL(mockdata, new SaveResponse(), new Save(), token);
  }

  @Path("{id}/delete")
  @DELETE
  public Response delete(@HeaderParam(HttpHeaders.AUTHORIZATION) String token, @PathParam("id") Integer id)
  {
    return APIUtils.executeBL(id, new EmptyResponse(), new Delete(), token);
  }
}