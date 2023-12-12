package de.joergdev.mosy.backend.util;

import static org.junit.Assert.*;
import org.junit.Test;

public class MockServicesUtilTest
{
  @Test
  public void testStringContainsString()
  {
    // stack:     123456789 0ABCDEFGHIJKLMNOPQRSTUVWXYZ
    // needlePos:   34  78              LMN   R
    // needleNeg:   34  78      U       LMN   R        
    String stack = "123456789 0ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String needlePos = "3478LMNR";
    String needleNeg = "3478ULMNR";

    assertTrue(MockServicesUtil.stringContainsString(stack, needlePos));
    assertFalse(MockServicesUtil.stringContainsString(stack, needleNeg));
  }

  @Test
  public void testJsonContainsJson()
  {
    StringBuilder buiStack = new StringBuilder();
    buiStack.append(" { ");
    buiStack.append("    \"data\" : ");
    buiStack.append("      { ");
    buiStack.append("          \"field1\" : \"value1\", ");
    buiStack.append("          \"field2\" : \"value2\", ");
    buiStack.append("          \"sub\" : ");
    buiStack.append("            [{ ");
    buiStack.append("                  \"fieldSub\" : \"valueSub1\" ");
    buiStack.append("            }, ");
    buiStack.append("            { ");
    buiStack.append("                  \"fieldSub\" : \"valueSub2\" ");
    buiStack.append("            }] ");
    buiStack.append("      } ");
    buiStack.append(" } ");

    StringBuilder buiNeedlePos = new StringBuilder();
    buiNeedlePos.append(" { ");
    buiNeedlePos.append("    \"data\" : ");
    buiNeedlePos.append("      { ");
    buiNeedlePos.append("          \"field1\" : \"value1\", ");
    buiNeedlePos.append("          \"sub\" : ");
    buiNeedlePos.append("            [{ ");
    buiNeedlePos.append("                  \"fieldSub\" : \"valueSub1\" ");
    buiNeedlePos.append("            }] ");
    buiNeedlePos.append("      } ");
    buiNeedlePos.append(" } ");

    StringBuilder buiNeedleNeg = new StringBuilder();
    buiNeedleNeg.append(" { ");
    buiNeedleNeg.append("    \"data\" : ");
    buiNeedleNeg.append("      { ");
    buiNeedleNeg.append("          \"field1\" : \"value1\", ");
    buiNeedleNeg.append("          \"sub\" : ");
    buiNeedleNeg.append("            [{ ");
    buiNeedleNeg.append("                  \"fieldSub\" : \"valueSub__NEG__\" ");
    buiNeedleNeg.append("            }] ");
    buiNeedleNeg.append("      } ");
    buiNeedleNeg.append(" } ");

    assertTrue(MockServicesUtil.jsonContainsJson(buiStack.toString(), buiNeedlePos.toString()));
    assertFalse(MockServicesUtil.jsonContainsJson(buiStack.toString(), buiNeedleNeg.toString()));
  }

  @Test
  public void testXmlContainsXml()
  {
    StringBuilder buiStack = new StringBuilder();
    buiStack.append("<Data>");
    buiStack.append("   <Field1>Value1</Field1>");
    buiStack.append("   <Field2>Value2</Field2>");
    buiStack.append("   <Sub>");
    buiStack.append("     <SubField1>SubValue11</SubField1>");
    buiStack.append("     <SubField2>SubValue12</SubField2>");
    buiStack.append("     <SubSub>");
    buiStack.append("       <SubSubField1>SubValueSS1</SubSubField1>");
    buiStack.append("       <SubSubSub>");
    buiStack.append("         <SubSubSubField1>sss1</SubSubSubField1>");
    buiStack.append("         <SubSubSubField1>sss2</SubSubSubField1>");
    buiStack.append("       </SubSubSub>");
    buiStack.append("     </SubSub>");
    buiStack.append("   </Sub>");
    buiStack.append("   <Sub>");
    buiStack.append("     <SubField1>SubValue21</SubField1>");
    buiStack.append("     <SubField2>SubValue22</SubField2>");
    buiStack.append("   </Sub>");
    buiStack.append("</Data>");

    StringBuilder buiNeedle = new StringBuilder();
    buiNeedle.append("<Data>");
    buiNeedle.append("   <Field2>Value2</Field2>");
    buiNeedle.append("   <Sub>");
    buiNeedle.append("     <SubSub>");
    buiNeedle.append("       <SubSubSub>");
    buiNeedle.append("         <SubSubSubField1>sss2</SubSubSubField1>");
    buiNeedle.append("       </SubSubSub>");
    buiNeedle.append("     </SubSub>");
    buiNeedle.append("   </Sub>");
    buiNeedle.append("   <Sub>");
    buiNeedle.append("     <SubField1>SubValue21</SubField1>");
    buiNeedle.append("     <SubField2>SubValue22</SubField2>");
    buiNeedle.append("   </Sub>");
    buiNeedle.append("</Data>");

    StringBuilder buiNeedleNEG = new StringBuilder();
    buiNeedleNEG.append("<Data>");
    buiNeedleNEG.append("   <Field2>Value2</Field2>");
    buiNeedleNEG.append("   <Sub>");
    buiNeedleNEG.append("     <SubSub>");
    buiNeedleNEG.append("       <SubSubSub>");
    buiNeedleNEG.append("         <SubSubSubField1>____NEG____</SubSubSubField1>");
    buiNeedleNEG.append("       </SubSubSub>");
    buiNeedleNEG.append("     </SubSub>");
    buiNeedleNEG.append("   </Sub>");
    buiNeedleNEG.append("   <Sub>");
    buiNeedleNEG.append("     <SubField1>SubValue21</SubField1>");
    buiNeedleNEG.append("     <SubField2>SubValue22</SubField2>");
    buiNeedleNEG.append("   </Sub>");
    buiNeedleNEG.append("</Data>");

    assertTrue(MockServicesUtil.xmlContainsXml(buiStack.toString(), buiNeedle.toString()));
    assertFalse(MockServicesUtil.xmlContainsXml(buiStack.toString(), buiNeedleNEG.toString()));
  }

  @Test
  public void testXmlContainsXmlPartiell()
  {
    StringBuilder buiStack = new StringBuilder();
    buiStack.append("<Data>");
    buiStack.append("   <Field1>Value1</Field1>");
    buiStack.append("   <Field2>Value2</Field2>");
    buiStack.append("   <Sub>");
    buiStack.append("     <SubField1>SubValue11</SubField1>");
    buiStack.append("     <SubField2>SubValue12</SubField2>");
    buiStack.append("     <SubSub>");
    buiStack.append("       <SubSubField1>SubValueSS1</SubSubField1>");
    buiStack.append("       <SubSubSub>");
    buiStack.append("         <SubSubSubField1>sss1</SubSubSubField1>");
    buiStack.append("         <SubSubSubField1>sss2</SubSubSubField1>");
    buiStack.append("       </SubSubSub>");
    buiStack.append("     </SubSub>");
    buiStack.append("   </Sub>");
    buiStack.append("   <Sub>");
    buiStack.append("     <SubField1>SubValue21</SubField1>");
    buiStack.append("     <SubField2>SubValue22</SubField2>");
    buiStack.append("   </Sub>");
    buiStack.append("</Data>");

    StringBuilder buiNeedle = new StringBuilder();
    buiNeedle.append("       <SubSubSub>");
    buiNeedle.append("         <SubSubSubField1>sss2</SubSubSubField1>");
    buiNeedle.append("       </SubSubSub>");

    StringBuilder buiNeedle2 = new StringBuilder();
    buiNeedle2.append("         <SubSubSubField1>sss2</SubSubSubField1>");

    assertTrue(MockServicesUtil.xmlContainsXml(buiStack.toString(), buiNeedle.toString()));
    assertTrue(MockServicesUtil.xmlContainsXml(buiStack.toString(), buiNeedle2.toString()));
  }

  @Test
  public void testGetUrlWithReplacedDynVars()
  {
    assertEquals("test/%/a/%", MockServicesUtil.getUrlWithReplacedDynVars("test/{id}/a/{hello}"));
    assertEquals("test/%/a/%/ua", MockServicesUtil.getUrlWithReplacedDynVars("test/{id}/a/{hello}/ua"));
  }
}