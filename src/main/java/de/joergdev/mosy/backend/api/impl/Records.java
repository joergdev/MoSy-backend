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
import de.joergdev.mosy.api.response.EmptyResponse;
import de.joergdev.mosy.api.response.record.LoadAllResponse;
import de.joergdev.mosy.api.response.record.LoadResponse;
import de.joergdev.mosy.api.response.record.SaveResponse;
import de.joergdev.mosy.backend.api.APIUtils;
import de.joergdev.mosy.backend.api.intern.request.record.LoadAllRequest;
import de.joergdev.mosy.backend.bl.record.Delete;
import de.joergdev.mosy.backend.bl.record.DeleteAll;
import de.joergdev.mosy.backend.bl.record.Load;
import de.joergdev.mosy.backend.bl.record.LoadAll;
import de.joergdev.mosy.backend.bl.record.Save;

@Path(APIConstants.API_URL_BASE + "records")
public class Records
{
  @GET
  public Response loadAll(@HeaderParam(HttpHeaders.AUTHORIZATION) String token, @QueryParam("load_count") Integer loadCount,
                          @QueryParam("last_loaded_id") Integer lastLoadedId, @QueryParam("record_session_id") Integer recordSessionID)
  {
    LoadAllRequest request = new LoadAllRequest();
    request.setLoadCount(loadCount);
    request.setLastLoadedId(lastLoadedId);
    request.setRecordSessionID(recordSessionID);

    return APIUtils.executeBL(request, new LoadAllResponse(), new LoadAll(), token);
  }

  @Path(value = "{id}")
  @GET
  public Response load(@HeaderParam(HttpHeaders.AUTHORIZATION) String token, @PathParam("id") Integer id)
  {
    return APIUtils.executeBL(id, new LoadResponse(), new Load(), token);
  }

  @Path("save")
  @POST
  public Response save(@HeaderParam(HttpHeaders.AUTHORIZATION) String token, de.joergdev.mosy.api.model.Record record)
  {
    return APIUtils.executeBL(record, new SaveResponse(), new Save(), token);
  }

  @Path("{id}/delete")
  @DELETE
  public Response delete(@HeaderParam(HttpHeaders.AUTHORIZATION) String token, @PathParam("id") Integer id)
  {
    return APIUtils.executeBL(id, new EmptyResponse(), new Delete(), token);
  }

  @Path("delete")
  @DELETE
  public Response delete(@HeaderParam(HttpHeaders.AUTHORIZATION) String token)
  {
    return APIUtils.executeBL(null, new EmptyResponse(), new DeleteAll(), token);
  }
}
