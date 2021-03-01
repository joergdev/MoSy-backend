package de.joergdev.mosy.backend.api.impl;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import de.joergdev.mosy.api.APIConstants;
import de.joergdev.mosy.api.request.mockservices.CustomRequestRequest;
import de.joergdev.mosy.api.response.AbstractResponse;
import de.joergdev.mosy.api.response.ResponseMessage;
import de.joergdev.mosy.api.response.mockservices.CustomRequestResponse;
import de.joergdev.mosy.backend.api.APIUtils;
import de.joergdev.mosy.backend.api.intern.request.mockservices.CaptureCommonRequest;
import de.joergdev.mosy.backend.api.intern.request.mockservices.CaptureSoapRequest;
import de.joergdev.mosy.backend.api.intern.response.mockservices.CaptureCommonResponse;
import de.joergdev.mosy.backend.api.intern.response.mockservices.CaptureSoapResponse;
import de.joergdev.mosy.backend.bl.mockservices.CaptureCommon;
import de.joergdev.mosy.backend.bl.mockservices.CaptureSoap;

@Path(APIConstants.API_URL_BASE + "mock-services")
public class MockServices
{
  @Path("soap/{pth:.+}")
  @POST
  @Produces(value = MediaType.TEXT_XML)
  public Response captureSoap(@PathParam("pth") String path, @Context HttpHeaders headers,
                              @Context UriInfo uriInfo, String content)
  {
    CaptureSoapRequest blRequest = new CaptureSoapRequest();
    blRequest.setPath(path);
    blRequest.setContent(content);
    blRequest.setHttpHeaders(headers);
    blRequest.setAbsolutePath(uriInfo.getAbsolutePath().toString());

    CaptureSoapResponse blResponse = new CaptureSoapResponse();

    APIUtils.executeBL(blRequest, blResponse, new CaptureSoap());

    return getResponseByCaptureResponse(blResponse, () -> blResponse.getResponse(), true);
  }

  @Path("soap/{pth:.+}")
  @GET
  @Produces(value = MediaType.TEXT_HTML)
  public Response captureSoapWsdlRequest(@PathParam("pth") String path, @Context HttpHeaders headers,
                                         @Context UriInfo uriInfo)
  {
    Map<String, List<String>> qryParams = uriInfo.getQueryParameters();

    if (qryParams.keySet().stream().anyMatch(p -> "wsdl".equalsIgnoreCase(p)))
    {
      CaptureSoapRequest blRequest = new CaptureSoapRequest();
      blRequest.setPath(path);
      blRequest.setHttpHeaders(headers);
      blRequest.setWsdlRequest(true);
      blRequest.setAbsolutePath(uriInfo.getAbsolutePath().toString());

      CaptureSoapResponse blResponse = new CaptureSoapResponse();

      APIUtils.executeBL(blRequest, blResponse, new CaptureSoap());

      return getResponseByCaptureResponse(blResponse, () -> blResponse.getResponse(), false);
    }
    else
    {
      // 404
      return Response.status(Status.NOT_FOUND).build();
    }
  }

  @Path("rest/{pth:.+}")
  @POST
  public Response captureRestPost(@PathParam("pth") String path, @Context HttpHeaders headers, String content)
  {
    return captureRest(path, headers, content);
  }

  @Path("rest/{pth:.+}")
  @PUT
  public Response captureRestPut(@PathParam("pth") String path, @Context HttpHeaders headers, String content)
  {
    return captureRest(path, headers, content);
  }

  @Path("rest/{pth:.+}")
  @DELETE
  public Response captureRestDelete(@PathParam("pth") String path, @Context HttpHeaders headers,
                                    String content)
  {
    return captureRest(path, headers, content);
  }

  @Path("rest/{pth:.+}")
  @GET
  public Response captureRestGet(@PathParam("pth") String path, @Context HttpHeaders headers, String content)
  {
    return captureRest(path, headers, content);
  }

  private Response captureRest(String path, HttpHeaders headers, String content)
  {
    CaptureCommonRequest commonReq = new CaptureCommonRequest();
    commonReq.setHttpHeaders(headers);
    commonReq.setContent(content);

    int idxLastSlash = path.lastIndexOf("/");

    commonReq.setServicePathInterface(path.substring(0, idxLastSlash));
    commonReq.setServicePathMethod(path.substring(idxLastSlash + 1));

    CaptureCommonResponse commonResp = new CaptureCommonResponse();

    APIUtils.executeBL(commonReq, commonResp, new CaptureCommon());

    return getResponseByCaptureResponse(commonResp, () -> commonResp.getResponse(), false);
  }

  @Path("custom-request")
  @POST
  public Response customRequest(@Context HttpHeaders headers, CustomRequestRequest request)
  {
    CaptureCommonRequest commonReq = new CaptureCommonRequest();
    commonReq.setServicePathInterface(request.getInterfaceName());
    commonReq.setServicePathMethod(request.getInterfaceMethod());
    commonReq.setContent(request.getRequest());
    commonReq.setHttpHeaders(headers);

    CaptureCommonResponse commonResp = new CaptureCommonResponse();

    APIUtils.executeBL(commonReq, commonResp, new CaptureCommon());

    CustomRequestResponse customResponse = new CustomRequestResponse();
    customResponse.setResponse(commonResp.getResponse());
    customResponse.setRecord(Boolean.TRUE.equals(commonResp.getRecord()));
    customResponse.setRoute(Boolean.TRUE.equals(commonResp.getRoute()));
    customResponse.setInterfaceMethod(commonResp.getInterfaceMethod());
    customResponse.setStateOK(commonResp.isStateOK());
    customResponse.getMessages().addAll(commonResp.getMessages());

    return Response.status(Status.OK).entity(customResponse).build();
  }

  private Response getResponseByCaptureResponse(AbstractResponse blResponse, Supplier<String> getterResponse,
                                                boolean soap)
  {
    if (blResponse.isStateOK())
    {
      return Response.ok().entity(getterResponse.get()).build();
    }
    else
    {
      StringBuilder buiMsg = new StringBuilder();

      for (ResponseMessage responseMsg : blResponse.getMessages())
      {
        buiMsg.append(responseMsg.toString()).append("\n\n");
      }

      String errorMsg = buiMsg.toString();

      return soap
          ? getResponseForFailedSoapRequest(errorMsg)
          : Response.serverError().entity(errorMsg).build();
    }
  }

  private Response getResponseForFailedSoapRequest(String errorMsg)
  {
    // escape html tags
    errorMsg = errorMsg.replace("<", "#o").replace(">", "#e");

    StringBuilder buiSoap = new StringBuilder();

    buiSoap.append("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">");
    buiSoap.append("  <soap:Body>");
    buiSoap.append("    <soap:Fault>");
    buiSoap.append("      <faultcode>soap:Server</faultcode>");
    buiSoap.append("      <faultstring>").append(errorMsg).append("</faultstring>");
    buiSoap.append("    </soap:Fault>");
    buiSoap.append("  </soap:Body>");
    buiSoap.append("</soap:Envelope>");

    return Response.ok().entity(buiSoap.toString()).build();
  }
}