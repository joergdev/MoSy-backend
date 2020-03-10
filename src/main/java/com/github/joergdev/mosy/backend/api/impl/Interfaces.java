package com.github.joergdev.mosy.backend.api.impl;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import com.github.joergdev.mosy.api.model.Interface;
import com.github.joergdev.mosy.api.model.InterfaceMethod;
import com.github.joergdev.mosy.api.response.EmptyResponse;
import com.github.joergdev.mosy.api.response._interface.LoadResponse;
import com.github.joergdev.mosy.api.response._interface.SaveResponse;
import com.github.joergdev.mosy.api.response._interface.method.LoadMockDataResponse;
import com.github.joergdev.mosy.api.response._interface.method.LoadRecordConfigsResponse;
import com.github.joergdev.mosy.api.response.record.LoadAllResponse;
import com.github.joergdev.mosy.backend.api.APIUtils;
import com.github.joergdev.mosy.backend.api.intern.request.record.LoadAllRequest;
import com.github.joergdev.mosy.backend.bl._interface.Delete;
import com.github.joergdev.mosy.backend.bl._interface.Load;
import com.github.joergdev.mosy.backend.bl._interface.Save;
import com.github.joergdev.mosy.backend.bl._interface.method.LoadMockData;
import com.github.joergdev.mosy.backend.bl._interface.method.LoadRecordConfigs;
import com.github.joergdev.mosy.backend.bl.record.LoadAll;

@Path("mosy/api/v_1_0/interfaces")
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
  public Response save(@HeaderParam(HttpHeaders.AUTHORIZATION) String token,
                       com.github.joergdev.mosy.api.model.Interface apiInterface)
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
  public Response loadMethodMockData(@HeaderParam(HttpHeaders.AUTHORIZATION) String token,
                                     @PathParam("i_id") Integer interfaceId, @PathParam("m_id") Integer id)
  {
    return APIUtils.executeBL(createApiMethodObject(interfaceId, id), new LoadMockDataResponse(),
        new LoadMockData(), token);
  }

  @Path(value = "{i_id}/methods/{m_id}/recordconfigs")
  @GET
  public Response loadMethodRecordConfigs(@HeaderParam(HttpHeaders.AUTHORIZATION) String token,
                                          @PathParam("i_id") Integer interfaceId,
                                          @PathParam("m_id") Integer id)
  {
    return APIUtils.executeBL(createApiMethodObject(interfaceId, id), new LoadRecordConfigsResponse(),
        new LoadRecordConfigs(), token);
  }

  @Path("{i_id}/methods/{m_id}/records")
  @GET
  public Response loadRecordsForInterfaceMethod(@HeaderParam(HttpHeaders.AUTHORIZATION) String token,
                                                @PathParam("i_id") Integer interfaceId,
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
    apiMethod.setMockInterface(apiInterface);
    return apiMethod;
  }
}