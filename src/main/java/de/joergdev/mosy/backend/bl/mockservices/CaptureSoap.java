package de.joergdev.mosy.backend.bl.mockservices;

import de.joergdev.mosy.api.response.ResponseCode;
import de.joergdev.mosy.backend.api.intern.request.mockservices.CaptureCommonRequest;
import de.joergdev.mosy.backend.api.intern.request.mockservices.CaptureSoapRequest;
import de.joergdev.mosy.backend.api.intern.response.mockservices.CaptureCommonResponse;
import de.joergdev.mosy.backend.bl.core.AbstractBL;
import de.joergdev.mosy.shared.Utils;

public class CaptureSoap extends AbstractBL<CaptureSoapRequest, CaptureCommonResponse>
{
  @Override
  protected void beforeExecute()
  {
    checkToken = false;
  }

  @Override
  protected void validateInput()
  {
    leaveOn(request == null, ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("request"));

    leaveOn(de.joergdev.mosy.shared.Utils.isEmpty(request.getPath()),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("path"));

    leaveOn(!request.isWsdlRequest() && de.joergdev.mosy.shared.Utils.isEmpty(request.getContent()),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("content"));
  }

  @Override
  protected void execute()
  {
    CaptureCommonRequest commonReq = new CaptureCommonRequest();
    commonReq.setServicePathInterface(request.getPath());
    commonReq.setServicePathMethod(getServicePathMethod());
    commonReq.setHttpHeaders(request.getHttpHeaders());
    commonReq.setContent(request.getContent());
    commonReq.setAbsolutePath(request.getAbsolutePath());

    if (request.isWsdlRequest())
    {
      commonReq.setRouteOnly(true);
      commonReq.setRouteAddition("?wsdl");
    }

    invokeSubBL(new CaptureCommon(), commonReq, response);
  }

  private String getServicePathMethod()
  {
    if (Utils.isEmpty(request.getContent()))
    {
      return null;
    }

    String xmlContent = request.getContent();

    int idxSoapBody = xmlContent.toLowerCase().indexOf(":body>");
    leaveOn(idxSoapBody < 0, ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("no soap body"));

    //<soap:envelope> <soap:body> <ns2:blaRequest />
    int idxNextTag = xmlContent.indexOf("<", idxSoapBody + 1);
    leaveOn(idxNextTag < 0, ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("no tag after soap body"));

    int idxNextTagEnd = xmlContent.indexOf(">", idxNextTag + 1);
    leaveOn(idxNextTag < 0,
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("no tag end after soap body"));

    // ns2:blaRequest /
    String nextTag = xmlContent.substring(idxNextTag + 1, idxNextTagEnd).trim();
    leaveOn(Utils.isEmpty(nextTag),
        ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("first tag in soap body is empty"));

    // ns2:blaRequest
    int idxEndServicePath = Utils.min(nextTag.indexOf(" "), nextTag.indexOf("/"));
    if (idxEndServicePath > 0)
    {
      nextTag = nextTag.substring(0, idxEndServicePath);
    }

    int idxNamespace = nextTag.indexOf(":");
    if (idxNamespace > 0)
    {
      nextTag = nextTag.substring(idxNamespace + 1);
    }

    return nextTag;
  }

  @Override
  protected void fillOutput()
  {
    // nothing to do, response get filled by subcall
  }
}