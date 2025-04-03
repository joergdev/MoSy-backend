package de.joergdev.mosy.backend.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import org.junit.Test;

public class HttpRoutingTest
{
  @Test
  public void testGetCharsetFromHeadersMultivaluedMap()
  {
    MultivaluedMap<String, String> multiMap = new MultivaluedHashMap<String, String>();
    multiMap.put("charset", Arrays.asList("1"));

    assertEquals("1", HttpRouting.getCharsetFromHeaders(multiMap));

    multiMap = new MultivaluedHashMap<String, String>();
    multiMap.put("Content-Type", Arrays.asList("[text/xml;charset=2]"));

    assertEquals("2", HttpRouting.getCharsetFromHeaders(multiMap));

    assertNull(HttpRouting.getCharsetFromHeaders(new MultivaluedHashMap<String, String>()));
  }

  @Test
  public void testGetCharsetFromHeadersMapWithListValues()
  {
    Map<String, List<String>> map = new HashMap<>();
    map.put("CHARSET", Arrays.asList("1"));

    assertEquals("1", HttpRouting.getCharsetFromHeaders(map));

    map = new HashMap<>();
    map.put("Content-Type", Arrays.asList("[text/xml;charset=2]"));

    assertEquals("2", HttpRouting.getCharsetFromHeaders(map));
  }

  @Test
  public void testGetCharsetFromHeadersFlatMap()
  {
    Map<String, String> map = new HashMap<>();
    map.put("charset", "1");

    assertEquals("1", HttpRouting.getCharsetFromHeaders(map));

    map = new HashMap<>();
    map.put("Content-Type", "[text/xml;charset=2]");

    assertEquals("2", HttpRouting.getCharsetFromHeaders(map));
  }
}
