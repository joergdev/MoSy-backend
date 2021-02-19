package com.github.joergdev.mosy.backend.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;
import com.github.joergdev.mosy.api.APIConstants;
import com.github.joergdev.mosy.shared.Utils;

public class SoapRouting
{
  public static String doRouting(String endpoint, String endpointCalled, String request,
                                 MultivaluedMap<String, String> headerMap)
  {
    ByteArrayOutputStream outputStreamRequest = null;
    OutputStream httpOutRequest = null;

    InputStream inputStreamResponse = null;
    InputStreamReader inputStreamReaderResponse = null;
    BufferedReader bufReaderResponse = null;

    try
    {
      boolean isWsdlRequest = endpoint.endsWith("?wsdl");

      HttpURLConnection httpConn = (HttpURLConnection) new URL(endpoint).openConnection();

      // Set the appropriate HTTP parameters.
      if (isWsdlRequest)
      {
        httpConn.setRequestMethod("GET");
        httpConn.setDoOutput(true);
      }
      else
      {
        outputStreamRequest = new ByteArrayOutputStream();
        outputStreamRequest.write(request.getBytes());
        byte[] bytesRequest = outputStreamRequest.toByteArray();

        // Transfer header data
        for (String headerKey : headerMap.keySet())
        {
          if (APIConstants.HTTP_HEADER_MOCK_PROFILE_NAME.equals(headerKey)
              || APIConstants.HTTP_HEADER_RECORD_SESSION_ID.equals(headerKey))
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

        httpConn.setRequestMethod("POST");
        httpConn.setDoOutput(true);
        httpConn.setDoInput(true);

        // Write the content of the request to the outputstream of the HTTP Connection
        httpOutRequest = httpConn.getOutputStream();
        httpOutRequest.write(bytesRequest);
      }

      // ready with sending the request => read the response
      inputStreamResponse = httpConn.getInputStream();
      inputStreamReaderResponse = new InputStreamReader(inputStreamResponse);
      bufReaderResponse = new BufferedReader(inputStreamReaderResponse);

      // Write the SOAP message response to a StringBuilder
      String responseString = "";
      StringBuilder buiResponse = new StringBuilder();
      while ((responseString = bufReaderResponse.readLine()) != null)
      {
        buiResponse.append(responseString);
      }

      String response = buiResponse.toString();

      if (isWsdlRequest)
      {
        response = response.replace(endpoint.substring(0, endpoint.length() - "?wsdl".length()),
            endpointCalled);
      }

      return response;
    }
    catch (Exception ex)
    {
      throw new IllegalStateException(ex);
    }
    finally
    {
      Utils.safeClose(outputStreamRequest, httpOutRequest, inputStreamResponse, inputStreamReaderResponse,
          bufReaderResponse);
    }
  }
}