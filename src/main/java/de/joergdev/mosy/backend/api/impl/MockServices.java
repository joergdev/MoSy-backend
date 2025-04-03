package de.joergdev.mosy.backend.api.impl;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriInfo;
import de.joergdev.mosy.api.APIConstants;
import de.joergdev.mosy.api.model.HttpMethod;
import de.joergdev.mosy.api.model.UrlArgument;
import de.joergdev.mosy.api.request.mockservices.CustomRequestRequest;
import de.joergdev.mosy.api.response.ResponseMessage;
import de.joergdev.mosy.api.response.mockservices.CustomRequestResponse;
import de.joergdev.mosy.backend.api.APIUtils;
import de.joergdev.mosy.backend.api.intern.request.mockservices.CaptureCommonRequest;
import de.joergdev.mosy.backend.api.intern.request.mockservices.CaptureSoapRequest;
import de.joergdev.mosy.backend.api.intern.response.mockservices.CaptureCommonResponse;
import de.joergdev.mosy.backend.bl.mockservices.CaptureCommon;
import de.joergdev.mosy.backend.bl.mockservices.CaptureRest;
import de.joergdev.mosy.backend.bl.mockservices.CaptureSoap;
import de.joergdev.mosy.shared.Utils;

@Path(APIConstants.API_URL_BASE + "mock-services")
public class MockServices
{
  @Path("soap/{pth:.+}")
  @POST
  @Produces(value = MediaType.TEXT_XML)
  public Response captureSoap(@PathParam("pth") String path, @Context HttpHeaders headers, @Context UriInfo uriInfo, String content)
  {
    CaptureSoapRequest blRequest = new CaptureSoapRequest();
    blRequest.setPath(path);
    blRequest.setContent(content);
    blRequest.setHttpHeaders(headers);
    blRequest.setAbsolutePath(uriInfo.getAbsolutePath().toString());

    CaptureCommonResponse blResponse = new CaptureCommonResponse();

    APIUtils.executeBL(blRequest, blResponse, new CaptureSoap());

    return getResponseByCaptureResponse(blResponse, null, true);
  }

  @Path("soap/{pth:.+}")
  @GET
  @Produces(value = MediaType.TEXT_HTML)
  public Response captureSoapWsdlRequest(@PathParam("pth") String path, @Context HttpHeaders headers, @Context UriInfo uriInfo)
  {
    Map<String, List<String>> qryParams = uriInfo.getQueryParameters();

    if (qryParams.keySet().stream().anyMatch(p -> "wsdl".equalsIgnoreCase(p)))
    {
      CaptureSoapRequest blRequest = new CaptureSoapRequest();
      blRequest.setPath(path);
      blRequest.setHttpHeaders(headers);
      blRequest.setWsdlRequest(true);
      blRequest.setAbsolutePath(uriInfo.getAbsolutePath().toString());

      CaptureCommonResponse blResponse = new CaptureCommonResponse();

      APIUtils.executeBL(blRequest, blResponse, new CaptureSoap());

      return getResponseByCaptureResponse(blResponse, null, false);
    }
    else
    {
      // 404
      return Response.status(Status.NOT_FOUND).build();
    }
  }

  @Path("rest/{pth:.+}")
  @POST
  public Response captureRestPost(@PathParam("pth") String path, @Context HttpHeaders headers, @Context UriInfo uriInfo, String content)
  {
    return captureRest(path, HttpMethod.POST, headers, uriInfo, content);
  }

  @Path("rest/{pth:.+}")
  @PUT
  public Response captureRestPut(@PathParam("pth") String path, @Context HttpHeaders headers, @Context UriInfo uriInfo, String content)
  {
    return captureRest(path, HttpMethod.PUT, headers, uriInfo, content);
  }

  @Path("rest/{pth:.+}")
  @DELETE
  public Response captureRestDelete(@PathParam("pth") String path, @Context HttpHeaders headers, @Context UriInfo uriInfo, String content)
  {
    return captureRest(path, HttpMethod.DELETE, headers, uriInfo, content);
  }

  @Path("rest/{pth:.+}")
  @GET
  public Response captureRestGet(@PathParam("pth") String path, @Context HttpHeaders headers, @Context UriInfo uriInfo, String content)
  {
    return captureRest(path, HttpMethod.GET, headers, uriInfo, content);
  }

  private Response captureRest(String path, HttpMethod httpMethod, HttpHeaders headers, UriInfo uriInfo, String content)
  {
    CaptureCommonRequest commonReq = new CaptureCommonRequest();
    commonReq.setHttpHeaders(headers);
    commonReq.setContent(content);
    commonReq.setServicePathInterface(path);
    commonReq.setHttpMethod(httpMethod);
    commonReq.getUrlArguments().addAll(UrlArgument.getUrlArgumentsFromMap(uriInfo.getQueryParameters()));

    CaptureCommonResponse commonResp = new CaptureCommonResponse();

    APIUtils.executeBL(commonReq, commonResp, new CaptureRest());

    return getResponseByCaptureResponse(commonResp, () -> commonResp.getResponseHttpCode(), false);
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

  private Response getResponseByCaptureResponse(CaptureCommonResponse blResponse, Supplier<Integer> getterHttpReturnCode, boolean soap)
  {
    ResponseBuilder responseBui = null;

    if (blResponse.isStateOK())
    {
      Integer httpReturnCode = getterHttpReturnCode == null ? null : getterHttpReturnCode.get();

      responseBui = Response.status(httpReturnCode == null ? Status.OK : Status.fromStatusCode(getterHttpReturnCode.get()));

      String entity = blResponse.getResponse();
      if (entity != null)
      {
        responseBui = responseBui.entity(entity);
      }
    }
    else
    {
      StringBuilder buiMsg = new StringBuilder();

      Iterator<ResponseMessage> itMsg = blResponse.getMessages().iterator();
      while (itMsg.hasNext())
      {
        buiMsg.append(itMsg.next().toString());

        if (itMsg.hasNext())
        {
          buiMsg.append("\n\n");
        }
      }

      String errorMsg = buiMsg.toString();

      if (soap)
      {
        responseBui = getResponseForFailedSoapRequest(errorMsg);
      }
      else
      {
        responseBui = Response.serverError().entity(errorMsg);
      }
    }

    // Transfer Response Headers
    MultivaluedMap<String, Object> headers = blResponse.getResponseHeaders();
    if (headers != null)
    {
      for (Entry<String, List<Object>> headerEntry : headers.entrySet())
      {
        String headerKey = headerEntry.getKey();
        List<Object> headerList = headerEntry.getValue();

        if (!Utils.isEmpty(headerKey) && headerList != null && !isHeaderKeyBlacklisted(headerKey))
        {
          for (Object headerVal : headerList)
          {
            if (headerVal != null)
            {
              responseBui.header(headerKey, headerVal);
            }
          }
        }
      }
    }

    return responseBui.build();
  }

  /**
   * <pre>
   * This header keys should not be transfered to mock reponse.
   * 
   * - Transfer-Encoding => if set connection is aborted after timeout without response to client
   * </pre>
   * 
   * @param headerKey
   * @return boolean
   */
  private boolean isHeaderKeyBlacklisted(String headerKey)
  {
    return Arrays.asList("Transfer-Encoding").contains(headerKey);
  }

  private ResponseBuilder getResponseForFailedSoapRequest(String errorMsg)
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

    return Response.ok().entity(buiSoap.toString());
  }
}
