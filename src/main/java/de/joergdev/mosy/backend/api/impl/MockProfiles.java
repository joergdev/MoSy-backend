package de.joergdev.mosy.backend.api.impl;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import de.joergdev.mosy.api.APIConstants;
import de.joergdev.mosy.api.model.MockProfile;
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
import de.joergdev.mosy.shared.Utils;

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

  @Path("{name}/mockdata")
  @GET
  public Response loadMockData(@HeaderParam(HttpHeaders.AUTHORIZATION) String token,
                               @PathParam("name") String idOrName)
  {
    MockProfile apiMockProfile = new MockProfile();

    if (Utils.isNumeric(idOrName))
    {
      apiMockProfile.setMockProfileID(Integer.valueOf(idOrName));
    }
    else
    {
      apiMockProfile.setName(idOrName);
    }

    return APIUtils.executeBL(apiMockProfile, new LoadMockDataResponse(), new LoadMockData(), token);
  }
}