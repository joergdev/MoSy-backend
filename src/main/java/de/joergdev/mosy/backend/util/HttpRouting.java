package de.joergdev.mosy.backend.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import de.joergdev.mosy.api.APIConstants;
import de.joergdev.mosy.api.model.HttpMethod;
import de.joergdev.mosy.shared.Utils;

public class HttpRouting
{
  public static Response doRouting(String endpoint, String endpointCalled, String request, HttpMethod httpMethod, MultivaluedMap<String, String> headerMap,
                                   boolean isSOAP, boolean isInternalRouting)
  {
    ByteArrayOutputStream outputStreamRequest = null;
    OutputStream httpOutRequest = null;

    InputStream inputStreamResponse = null;
    InputStreamReader inputStreamReaderResponse = null;
    BufferedReader bufReaderResponse = null;

    try
    {
      boolean isWsdlRequest = isSOAP && endpoint.endsWith("?wsdl");

      HttpURLConnection httpConn = (HttpURLConnection) new URL(endpoint).openConnection();

      // Transfer header data
      transferHeaderData(headerMap, httpConn, isInternalRouting);

      // Set the appropriate HTTP parameters.
      if (isWsdlRequest)
      {
        httpConn.setRequestMethod(HttpMethod.GET.name());
        httpConn.setDoOutput(true);
      }
      else
      {
        byte[] bytesRequest = null;

        httpConn.setRequestMethod(httpMethod.name());
        httpConn.setDoOutput(true);

        if (!Utils.isEmpty(request))
        {
          httpConn.setDoInput(true);

          outputStreamRequest = new ByteArrayOutputStream();
          outputStreamRequest.write(request.getBytes(getCharset(getCharsetFromHeaders(headerMap))));
          bytesRequest = outputStreamRequest.toByteArray();

          // Write the content of the request to the outputstream of the HTTP Connection
          httpOutRequest = httpConn.getOutputStream();
          httpOutRequest.write(bytesRequest);
        }
      }

      // ready with sending the request => read the response
      try
      {
        inputStreamResponse = httpConn.getInputStream();
      }
      catch (IOException ioEx)
      {
        // for example FileNotFoundException => Http 4xx, try to read from error stream
        // or IOException Http 500 failed auth.
        inputStreamResponse = httpConn.getErrorStream();
      }

      StringBuilder buiResponse = new StringBuilder();

      if (inputStreamResponse != null)
      {
        inputStreamReaderResponse = new InputStreamReader(inputStreamResponse, getCharset(getCharsetFromHeaders(httpConn.getHeaderFields())));
        bufReaderResponse = new BufferedReader(inputStreamReaderResponse);

        // Write the message response to a StringBuilder
        String responseString = "";
        while ((responseString = bufReaderResponse.readLine()) != null)
        {
          buiResponse.append(responseString);
        }
      }

      return buildResponse(endpoint, endpointCalled, isWsdlRequest, httpConn, buiResponse);
    }
    catch (Exception ex)
    {
      throw new IllegalStateException(ex);
    }
    finally
    {
      Utils.safeClose(outputStreamRequest, httpOutRequest, inputStreamResponse, inputStreamReaderResponse, bufReaderResponse);
    }
  }

  private static Response buildResponse(String endpoint, String endpointCalled, boolean isWsdlRequest, HttpURLConnection httpConn, StringBuilder buiResponse)
    throws IOException
  {
    String response = buiResponse.toString();

    if (isWsdlRequest)
    {
      response = response.replace(endpoint.substring(0, endpoint.length() - "?wsdl".length()), //
          endpointCalled.replace("?wsdl", ""));
    }

    ResponseBuilder responseBui = Response.status(httpConn.getResponseCode());

    if (!Utils.isEmpty(response))
    {
      responseBui = responseBui.entity(response);
    }

    // transfer headers
    for (Entry<String, List<String>> headerEntry : Utils.nvlMap(httpConn.getHeaderFields()).entrySet())
    {
      Optional<String> headerValueOpt = Utils.nvlCollection(headerEntry.getValue()).stream().filter(v -> v != null).findFirst();
      if (headerValueOpt.isPresent())
      {
        responseBui = responseBui.header(headerEntry.getKey(), headerValueOpt.get());
      }
    }

    return responseBui.build();
  }

  private static void transferHeaderData(MultivaluedMap<String, String> headerMap, HttpURLConnection httpConn, boolean isInternalRouting)
  {
    for (String headerKey : headerMap.keySet())
    {
      if (!isInternalRouting && (APIConstants.HTTP_HEADER_MOCK_PROFILE_NAME.equals(headerKey) //
                                 || APIConstants.HTTP_HEADER_RECORD_SESSION_ID.equals(headerKey) //
                                 || APIConstants.HTTP_HEADER_TENANT_ID.equals(headerKey)))
      {
        continue;
      }

      List<String> headerValues = headerMap.get(headerKey);

      if (Utils.isCollectionEmpty(headerValues))
      {
        httpConn.addRequestProperty(headerKey, null);
      }
      else
      {
        for (String headerValue : headerValues)
        {
          httpConn.addRequestProperty(headerKey, headerValue);
        }
      }
    }
  }

  static String getCharsetFromHeaders(Map<String, ? extends Object> headers)
  {
    for (String headerKey : headers.keySet())
    {
      if ("charset".equalsIgnoreCase(headerKey))
      {
        Object value = headers.get(headerKey);

        if (value instanceof String)
        {
          return (String) value;
        }
        else if (value instanceof Collection)
        {
          for (Object valueElement : (Collection<?>) value)
          {
            if (valueElement instanceof String)
            {
              return (String) valueElement;
            }
          }
        }
      }
      // Content-Type=[text/xml;charset=UTF-8]}
      else if ("Content-Type".equalsIgnoreCase(headerKey))
      {
        Object value = headers.get(headerKey);
        String valueStr = null;

        if (value instanceof String)
        {
          valueStr = (String) value;
        }
        else if (value instanceof Collection)
        {
          for (Object valueElement : (Collection<?>) value)
          {
            if (valueElement instanceof String)
            {
              valueStr = (String) valueElement;
            }
          }
        }

        if (!Utils.isEmpty(valueStr))
        {
          for (String valuePart : valueStr.split(";"))
          {
            valuePart = valuePart.replace("[", "").replace("]", "");
            valuePart = valuePart.trim();

            if (valuePart.startsWith("charset="))
            {
              return valuePart.split("=")[1];
            }
          }
        }
      }
    }

    return null;
  }

  private static Charset getCharset(String charset)
  {
    if (Utils.isEmpty(charset))
    {
      return StandardCharsets.UTF_8;
    }

    try
    {
      return Charset.forName(charset);
    }
    catch (Exception ex)
    {
      // Fallback
      return StandardCharsets.UTF_8;
    }
  }
}
