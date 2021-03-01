package de.joergdev.mosy.backend.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.joergdev.mosy.shared.Utils;

public class MockServicesUtil
{
  public static boolean stringContainsString(String stack, String needle)
  {
    char[] needleChars = needle.toCharArray();

    int lastPosition = -1;

    for (char needleChar : needleChars)
    {
      lastPosition = stack.indexOf(needleChar, lastPosition + 1);

      if (lastPosition < 0)
      {
        return false;
      }
    }

    return true;
  }

  public static boolean xmlContainsXml(String stack, String needle)
  {
    Map<String, Object> mapStack = Utils.xmlToMap(stack);
    Map<String, Object> mapNeedle = Utils.xmlToMap(needle);

    return mapContainsMap(mapStack, mapNeedle);
  }

  public static boolean jsonContainsJson(String stack, String needle)
  {
    Map<String, Object> mapStack = jsonToMap(stack);
    Map<String, Object> mapNeedle = jsonToMap(needle);

    return mapContainsMap(mapStack, mapNeedle);
  }

  private static Map<String, Object> jsonToMap(String jason)
  {
    try
    {
      @SuppressWarnings("unchecked")
      Map<String, Object> result = new ObjectMapper().readValue(jason, HashMap.class);

      return result;
    }
    catch (Exception ex)
    {
      throw new IllegalStateException(ex);
    }
  }

  private static boolean mapContainsMap(Map<String, Object> stack, Map<String, Object> needle)
  {
    for (Map<String, Object> stackMap : getAllMapsInMap(stack))
    {
      boolean containsAll = true;

      for (Entry<String, Object> entry : needle.entrySet())
      {
        String needleKey = entry.getKey();
        Object needleValue = entry.getValue();

        if (!stackMap.containsKey(needleKey))
        {
          containsAll = false;
          break;
        }

        if (needleValue instanceof List)
        {
          if (!stackContainsNeedleList(stackMap, needleKey, needleValue))
          {
            containsAll = false;
            break;
          }
        }
        else if (needleValue instanceof Map)
        {
          if (!stackContainsNeedleMap(stackMap, needleKey, needleValue))
          {
            containsAll = false;
            break;
          }
        }
        // String
        else
        {
          if (!Objects.equals(needleValue, stackMap.get(needleKey)))
          {
            containsAll = false;
            break;
          }
        }
      }

      if (containsAll)
      {
        return true;
      }
    }

    return false;
  }

  private static List<Map<String, Object>> getAllMapsInMap(Map<String, Object> rootMap)
  {
    List<Map<String, Object>> allMaps = new ArrayList<>();

    allMaps.add(rootMap);
    allMaps.addAll(getAllMapsInCollection(rootMap.values()));

    return allMaps;
  }

  @SuppressWarnings("unchecked")
  private static List<Map<String, Object>> getAllMapsInCollection(Collection<Object> rootCol)
  {
    List<Map<String, Object>> allMaps = new ArrayList<>();

    for (Object obj : rootCol)
    {
      if (obj instanceof Map)
      {
        allMaps.addAll(getAllMapsInMap((Map<String, Object>) obj));
      }
      else if (obj instanceof Collection)
      {
        allMaps.addAll(getAllMapsInCollection((Collection<Object>) obj));
      }
    }

    return allMaps;
  }

  private static boolean stackContainsNeedleMap(Map<String, Object> stack, String needleKey,
                                                Object needleValue)
  {
    Object stackValue = stack.get(needleKey);
    if (stackValue instanceof Map == false)
    {
      return false;
    }

    @SuppressWarnings("unchecked")
    Map<String, Object> mapNeedle = (Map<String, Object>) needleValue;

    @SuppressWarnings("unchecked")
    Map<String, Object> mapStack = (Map<String, Object>) stackValue;

    if (!mapContainsMap(mapStack, mapNeedle))
    {
      return false;
    }

    return true;
  }

  private static boolean stackContainsNeedleList(Map<String, Object> stack, String needleKey,
                                                 Object needleValue)
  {
    Object stackValue = stack.get(needleKey);
    if (stackValue instanceof List == false)
    {
      return false;
    }

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> needleList = (List<Map<String, Object>>) needleValue;

    @SuppressWarnings("unchecked")
    List<Map<String, Object>> stackList = (List<Map<String, Object>>) stackValue;
    stackList = new ArrayList<>(stackList);

    for (Map<String, Object> mapOfNeedleList : needleList)
    {
      Map<String, Object> mapOfStackListMatching = null;

      for (Map<String, Object> mapOfStackList : stackList)
      {
        if (mapContainsMap(mapOfStackList, mapOfNeedleList))
        {
          mapOfStackListMatching = mapOfStackList;
          break;
        }
      }

      if (mapOfStackListMatching == null)
      {
        return false;
      }
      else
      {
        stackList.remove(mapOfStackListMatching);
      }
    }

    return true;
  }
}