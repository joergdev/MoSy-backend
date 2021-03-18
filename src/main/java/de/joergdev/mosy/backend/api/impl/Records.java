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
import de.joergdev.mosy.api.response.record.LoadAllResponse;
import de.joergdev.mosy.api.response.record.LoadResponse;
import de.joergdev.mosy.api.response.record.SaveResponse;
import de.joergdev.mosy.backend.api.APIUtils;
import de.joergdev.mosy.backend.api.intern.request.record.LoadAllRequest;
import de.joergdev.mosy.backend.bl.record.Delete;
import de.joergdev.mosy.backend.bl.record.Load;
import de.joergdev.mosy.backend.bl.record.LoadAll;
import de.joergdev.mosy.backend.bl.record.Save;

@Path(APIConstants.API_URL_BASE + "records")
public class Records
{
  @GET
  public Response loadAll(@HeaderParam(HttpHeaders.AUTHORIZATION) String token,
                          @QueryParam("load_count") Integer loadCount,
                          @QueryParam("last_loaded_id") Integer lastLoadedId,
                          @QueryParam("record_session_id") Integer recordSessionID)
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
  public Response save(@HeaderParam(HttpHeaders.AUTHORIZATION) String token,
                       de.joergdev.mosy.api.model.Record record)
  {
    return APIUtils.executeBL(record, new SaveResponse(), new Save(), token);
  }

  @Path("{id}/delete")
  @DELETE
  public Response delete(@HeaderParam(HttpHeaders.AUTHORIZATION) String token, @PathParam("id") Integer id)
  {
    return APIUtils.executeBL(id, new EmptyResponse(), new Delete(), token);
  }
}