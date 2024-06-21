package de.joergdev.mosy.backend.bl._interface;

import de.joergdev.mosy.api.model.HttpMethod;
import de.joergdev.mosy.api.model.InterfaceType;
import de.joergdev.mosy.api.response.ResponseCode;
import de.joergdev.mosy.api.response._interface.LoadResponse;
import de.joergdev.mosy.backend.bl.core.AbstractBL;
import de.joergdev.mosy.backend.persistence.dao.InterfaceMethodDAO;
import de.joergdev.mosy.backend.persistence.model.Interface;
import de.joergdev.mosy.backend.persistence.model.InterfaceMethod;
import de.joergdev.mosy.shared.ObjectUtils;
import de.joergdev.mosy.shared.Utils;

/**
 * Load data for interface 
 * and root data for interface methods (id, name, mockSessionID, mockDisabled, record, routingOnNoMockData)
 */
public class Load extends AbstractBL<Integer, LoadResponse>
{
  private final de.joergdev.mosy.api.model.Interface apiInterface = new de.joergdev.mosy.api.model.Interface();

  @Override
  protected void validateInput()
  {
    leaveOn(request == null || !Utils.isPositive(request), ResponseCode.INVALID_INPUT_PARAMS.withAddtitionalInfo("name"));
  }

  @Override
  protected void execute()
  {
    Interface dbInterface = findDbEntity(Interface.class, request, "no interface with name: " + request);

    // Basedata
    ObjectUtils.copyValues(dbInterface, apiInterface, "type", "methods");

    if (dbInterface.getType() != null)
    {
      apiInterface.setType(InterfaceType.getById(dbInterface.getType().getInterfaceTypeId()));
    }
    else
    {
      apiInterface.setType(null);
    }

    // Methods
    transferMethods(dbInterface);

    // Record Y/N
    apiInterface.setRecord(dbInterface.getRecordConfig() == null ? null : Boolean.TRUE.equals(dbInterface.getRecordConfig().getEnabled()));
  }

  private void transferMethods(Interface dbInterface)
  {
    for (InterfaceMethod dbMethod : dbInterface.getMethods())
    {
      de.joergdev.mosy.api.model.InterfaceMethod apiMethod = new de.joergdev.mosy.api.model.InterfaceMethod();
      apiMethod.setMockInterfaceData(apiInterface);
      apiInterface.getMethods().add(apiMethod);

      ObjectUtils.copyValues(dbMethod, apiMethod, "mockInterface", "httpMethod");

      String httpMethod = dbMethod.getHttpMethod();
      if (!Utils.isEmpty(httpMethod))
      {
        apiMethod.setHttpMethod(HttpMethod.valueOf(httpMethod));
      }

      apiMethod.setRecord(getDao(InterfaceMethodDAO.class).isRecordEnabled(dbMethod.getInterfaceMethodId()));
    }
  }

  @Override
  protected void fillOutput()
  {
    response.setInterface(apiInterface);
  }
}
