package com.github.joergdev.mosy.backend.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Supplier;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.joergdev.mosy.shared.Utils;

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
    Map<String, Object> mapStack = xmlToMap(stack);
    Map<String, Object> mapNeedle = xmlToMap(needle);

    return mapContainsMap(mapStack, mapNeedle);
  }

  public static boolean jsonContainsJson(String stack, String needle)
  {
    Map<String, Object> mapStack = jsonToMap(stack);
    Map<String, Object> mapNeedle = jsonToMap(needle);

    return mapContainsMap(mapStack, mapNeedle);
  }

  private static Map<String, Object> xmlToMap(String xml)
  {
    try
    {
      Document doc = getDocumentFromInputString(xml);

      Map<String, Object> map = new HashMap<>();

      nodeToMap(doc.getDocumentElement(), map);

      return map;
    }
    catch (Exception ex)
    {
      throw new IllegalStateException(ex);
    }
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

  private static void nodeToMap(Node node, Map<String, Object> map)
  {
    String name = node.getNodeName();
    String content = node.getTextContent();

    List<Node> childNodes = getChildNodes(node);

    // dont transfer empty nodes
    if (!childNodes.isEmpty() || !Utils.isEmpty(content))
    {
      if (childNodes.isEmpty())
      {
        map.put(name, content);
      }
      else
      {
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> listForTag = (List<Map<String, Object>>) map.get(name);
        if (listForTag == null)
        {
          listForTag = new ArrayList<>();
          map.put(name, listForTag);
        }

        Map<String, Object> subMap = new HashMap<>();
        listForTag.add(subMap);

        childNodes.forEach(c -> nodeToMap(c, subMap));
      }
    }

  }

  private static List<Node> getChildNodes(Node rootNode)
  {
    List<Node> nodes = new ArrayList<>();

    org.w3c.dom.NodeList nl = rootNode.getChildNodes();

    for (int x = 0; x < nl.getLength(); x++)
    {
      Node n = nl.item(x);

      if (isRelevantNode(n))
      {
        nodes.add(n);
      }
    }

    return nodes;
  }

  private static boolean isRelevantNode(Node n)
  {
    if (Node.ELEMENT_NODE == n.getNodeType())
    {
      return true;
    }
    // TEXT_NODE -> false

    return false;
  }

  private static Document getDocumentFromInputString(String xml)
    throws Exception
  {
    Charset charset = getCharsetFromXml(xml);

    byte[] bytes = charset == null
        ? xml.getBytes()
        : xml.getBytes(charset);

    return getDocumentFromInputStream(() -> new ByteArrayInputStream(bytes));
  }

  private static Charset getCharsetFromXml(String xml)
  {
    try
    {
      XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance()
          .createXMLStreamReader(new StringReader(xml));

      String charsetStr = xmlStreamReader.getCharacterEncodingScheme();

      return Utils.isEmpty(charsetStr)
          ? null
          : Charset.forName(charsetStr);
    }
    catch (Exception ex)
    {
      throw new IllegalStateException(ex);
    }
  }

  private static Document getDocumentFromInputStream(Supplier<InputStream> isSupplier)
    throws Exception
  {
    try (InputStream is2 = isSupplier.get())
    {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(is2);

      // opt. but recommended
      doc.getDocumentElement().normalize();

      return doc;
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