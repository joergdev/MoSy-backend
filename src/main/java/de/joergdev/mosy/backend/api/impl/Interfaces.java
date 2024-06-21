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
import de.joergdev.mosy.api.model.Interface;
import de.joergdev.mosy.api.model.InterfaceMethod;
import de.joergdev.mosy.api.response.EmptyResponse;
import de.joergdev.mosy.api.response._interface.LoadResponse;
import de.joergdev.mosy.api.response._interface.SaveResponse;
import de.joergdev.mosy.api.response._interface.method.LoadMockDataResponse;
import de.joergdev.mosy.api.response._interface.method.LoadRecordConfigsResponse;
import de.joergdev.mosy.api.response.record.LoadAllResponse;
import de.joergdev.mosy.backend.api.APIUtils;
import de.joergdev.mosy.backend.api.intern.request.record.LoadAllRequest;
import de.joergdev.mosy.backend.bl._interface.Delete;
import de.joergdev.mosy.backend.bl._interface.Load;
import de.joergdev.mosy.backend.bl._interface.Save;
import de.joergdev.mosy.backend.bl._interface.method.LoadMockData;
import de.joergdev.mosy.backend.bl._interface.method.LoadRecordConfigs;
import de.joergdev.mosy.backend.bl.record.LoadAll;

@Path(APIConstants.API_URL_BASE + "interfaces")
public class Interfaces
{
  @Path(value = "{id}")
  @GET
  public Response load(@HeaderParam(HttpHeaders.AUTHORIZATION) String token, @PathParam("id") Integer id)
  {
    return APIUtils.executeBL(id, new LoadResponse(), new Load(), token);
  }

  @Path(value = "save")
  @POST
  public Response save(@HeaderParam(HttpHeaders.AUTHORIZATION) String token, de.joergdev.mosy.api.model.Interface apiInterface)
  {
    return APIUtils.executeBL(apiInterface, new SaveResponse(), new Save(), token);
  }

  @Path(value = "{id}/delete")
  @DELETE
  public Response delete(@HeaderParam(HttpHeaders.AUTHORIZATION) String token, @PathParam("id") Integer id)
  {
    return APIUtils.executeBL(id, new EmptyResponse(), new Delete(), token);
  }

  @Path(value = "{i_id}/methods/{m_id}/mockdata")
  @GET
  public Response loadMethodMockData(@HeaderParam(HttpHeaders.AUTHORIZATION) String token, @PathParam("i_id") Integer interfaceId,
                                     @PathParam("m_id") Integer id)
  {
    return APIUtils.executeBL(createApiMethodObject(interfaceId, id), new LoadMockDataResponse(), new LoadMockData(), token);
  }

  @Path(value = "{i_id}/methods/{m_id}/recordconfigs")
  @GET
  public Response loadMethodRecordConfigs(@HeaderParam(HttpHeaders.AUTHORIZATION) String token, @PathParam("i_id") Integer interfaceId,
                                          @PathParam("m_id") Integer id)
  {
    return APIUtils.executeBL(createApiMethodObject(interfaceId, id), new LoadRecordConfigsResponse(), new LoadRecordConfigs(), token);
  }

  @Path("{i_id}/methods/{m_id}/records")
  @GET
  public Response loadRecordsForInterfaceMethod(@HeaderParam(HttpHeaders.AUTHORIZATION) String token, @PathParam("i_id") Integer interfaceId,
                                                @PathParam("m_id") Integer id)
  {
    InterfaceMethod apiMethod = createApiMethodObject(interfaceId, id);

    LoadAllRequest blRequest = new LoadAllRequest();
    blRequest.setInterfaceMethod(apiMethod);

    return APIUtils.executeBL(blRequest, new LoadAllResponse(), new LoadAll(), token);
  }

  private InterfaceMethod createApiMethodObject(Integer interfaceId, Integer id)
  {
    Interface apiInterface = new Interface();
    apiInterface.setInterfaceId(interfaceId);

    InterfaceMethod apiMethod = new InterfaceMethod();
    apiMethod.setInterfaceMethodId(id);
    apiMethod.setMockInterfaceData(apiInterface);
    return apiMethod;
  }
}
