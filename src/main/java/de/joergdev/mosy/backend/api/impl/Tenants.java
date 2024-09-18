package de.joergdev.mosy.backend.api.impl;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;
import de.joergdev.mosy.api.APIConstants;
import de.joergdev.mosy.api.model.HttpMethod;
import de.joergdev.mosy.api.model.UrlArgument;
import de.joergdev.mosy.api.request.tenant.SaveRequest;
import de.joergdev.mosy.api.response.EmptyResponse;
import de.joergdev.mosy.api.response.tenant.LoadAllResponse;
import de.joergdev.mosy.api.response.tenant.SaveResponse;
import de.joergdev.mosy.backend.api.APIUtils;
import de.joergdev.mosy.backend.bl.tenant.Delete;
import de.joergdev.mosy.backend.bl.tenant.LoadAll;
import de.joergdev.mosy.backend.bl.tenant.Save;
import de.joergdev.mosy.backend.util.HttpRouting;

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

  // Routing

  @Path("{id}/{pth:.+}")
  @POST
  public Response routePostRequest(@PathParam("pth") String path, @Context HttpHeaders headers, @Context UriInfo uriInfo, @PathParam("id") Integer id,
                                   String content)
  {
    return routeRequest(headers, uriInfo, id, content, HttpMethod.POST);
  }

  @Path("{id}/{pth:.+}")
  @PUT
  public Response routePutRequest(@PathParam("pth") String path, @Context HttpHeaders headers, @Context UriInfo uriInfo, @PathParam("id") Integer id,
                                  String content)
  {
    return routeRequest(headers, uriInfo, id, content, HttpMethod.PUT);
  }

  @Path("{id}/{pth:.+}")
  @DELETE
  public Response routeDeleteRequest(@PathParam("pth") String path, @Context HttpHeaders headers, @Context UriInfo uriInfo, @PathParam("id") Integer id,
                                     String content)
  {
    return routeRequest(headers, uriInfo, id, content, HttpMethod.DELETE);
  }

  @Path("{id}/{pth:.+}")
  @GET
  public Response routeGetRequest(@PathParam("pth") String path, @Context HttpHeaders headers, @Context UriInfo uriInfo, @PathParam("id") Integer id,
                                  String content)
  {
    return routeRequest(headers, uriInfo, id, content, HttpMethod.GET);
  }

  private Response routeRequest(HttpHeaders headers, UriInfo uriInfo, Integer id, String content, HttpMethod httpMethod)
  {
    String endpointCalled = uriInfo.getAbsolutePath().toString() //
                            + UrlArgument.getUrlPartForUrlArguments(uriInfo.getQueryParameters());
    String routingURL = getRoutingUrl(endpointCalled, id);

    MultivaluedMap<String, String> headersMap = headers.getRequestHeaders();
    headersMap.add(APIConstants.HTTP_HEADER_TENANT_ID, String.valueOf(id));

    boolean isSOAP = endpointCalled.contains("/mock-services/soap/");

    Response responseRouted = HttpRouting.doRouting(routingURL, endpointCalled, content, httpMethod, headersMap, isSOAP, true);

    ResponseBuilder responseBui = Response.status(responseRouted.getStatus());
    responseBui.entity(responseRouted.getEntity());
    responseBui.allow(responseRouted.getAllowedMethods());
    responseBui.location(responseRouted.getLocation());
    responseBui.type(responseRouted.getMediaType());

    return responseBui.build();
  }

  /**
   * Remove the path "tenants/{id}/" from the endpoint.
   * 
   * Example:
   * http://server-xy/mosy/api/v_x_0/tenants/123/interfaces
   * => transform to
   * http://server-xy/mosy/api/v_x_0/interfaces
   */
  private String getRoutingUrl(String endpointCalled, Integer id)
  {
    String routingURL = endpointCalled.replace("/tenants/" + id, "");

    if (routingURL.endsWith("/"))
    {
      routingURL = routingURL.substring(0, routingURL.length() - 1);
    }

    if (routingURL.endsWith(APIConstants.API_URL_BASE + "tenants"))
    {
      throw new IllegalArgumentException("invalid url called");
    }

    return routingURL;
  }
}
