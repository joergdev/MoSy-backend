package de.joergdev.mosy.backend.api.impl;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import de.joergdev.mosy.api.APIConstants;
import de.joergdev.mosy.api.response.EmptyResponse;
import de.joergdev.mosy.api.response.mockprofile.LoadAllResponse;
import de.joergdev.mosy.api.response.mockprofile.LoadMockDataResponse;
import de.joergdev.mosy.api.response.mockprofile.LoadResponse;
import de.joergdev.mosy.api.response.mockprofile.SaveResponse;
import de.joergdev.mosy.backend.api.APIUtils;
import de.joergdev.mosy.backend.api.intern.request.mockprofile.LoadAllRequest;
import de.joergdev.mosy.backend.bl.mockprofile.Delete;
import de.joergdev.mosy.backend.bl.mockprofile.Load;
import de.joergdev.mosy.backend.bl.mockprofile.LoadAll;
import de.joergdev.mosy.backend.bl.mockprofile.LoadMockData;
import de.joergdev.mosy.backend.bl.mockprofile.Save;

@Path(APIConstants.API_URL_BASE + "mock-profiles")
public class MockProfiles
{
  @Path(value = "{id}")
  @GET
  public Response load(@HeaderParam(HttpHeaders.AUTHORIZATION) String token, @PathParam("id") Integer id)
  {
    return APIUtils.executeBL(id, new LoadResponse(), new Load(), token);
  }

  @GET
  public Response loadAll(@HeaderParam(HttpHeaders.AUTHORIZATION) String token,
                          @QueryParam("load_count") Integer loadCount,
                          @QueryParam("last_loaded_id") Integer lastLoadedId)
  {
    LoadAllRequest request = new LoadAllRequest();
    request.setLoadCount(loadCount);
    request.setLastLoadedId(lastLoadedId);

    return APIUtils.executeBL(request, new LoadAllResponse(), new LoadAll(), token);
  }

  @Path("save")
  @POST
  public Response save(@HeaderParam(HttpHeaders.AUTHORIZATION) String token,
                       de.joergdev.mosy.api.model.MockProfile mockProfile)
  {
    return APIUtils.executeBL(mockProfile, new SaveResponse(), new Save(), token);
  }

  @Path("{id}/delete")
  @DELETE
  public Response delete(@HeaderParam(HttpHeaders.AUTHORIZATION) String token, @PathParam("id") Integer id)
  {
    return APIUtils.executeBL(id, new EmptyResponse(), new Delete(), token);
  }

  @Path("{id}/mockdata")
  @GET
  public Response loadMockData(@HeaderParam(HttpHeaders.AUTHORIZATION) String token,
                               @PathParam("id") Integer id)
  {
    return APIUtils.executeBL(id, new LoadMockDataResponse(), new LoadMockData(), token);
  }
}