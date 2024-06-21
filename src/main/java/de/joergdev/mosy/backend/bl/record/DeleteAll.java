package de.joergdev.mosy.backend.bl.record;

import de.joergdev.mosy.api.response.EmptyResponse;
import de.joergdev.mosy.backend.bl.core.AbstractBL;
import de.joergdev.mosy.backend.persistence.dao.RecordDAO;

public class DeleteAll extends AbstractBL<Void, EmptyResponse>
{
  @Override
  protected void validateInput()
  {
    // no input
  }

  @Override
  protected void execute()
  {
    getDao(RecordDAO.class).deleteAll();
  }

  @Override
  protected void fillOutput()
  {
    // no output
  }
}
