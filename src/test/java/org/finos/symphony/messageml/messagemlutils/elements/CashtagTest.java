package org.finos.symphony.messageml.messagemlutils.elements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.finos.symphony.messageml.messagemlutils.bi.BiFields;
import org.finos.symphony.messageml.messagemlutils.bi.BiItem;
import org.finos.symphony.messageml.messagemlutils.exceptions.InvalidInputException;
import org.finos.symphony.messageml.messagemlutils.exceptions.ProcessingException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CashtagTest extends ElementTest {

  private static final String CASHTAG_ENTITY_SUBTYPE = "org.symphonyoss.fin.security.id.ticker";

  @Test
  public void testCashTag() throws Exception {
    String input = "<messageML>Hello <cash tag=\"world\"/>!</messageML>";

    String expectedPresentationML = "<div data-format=\"PresentationML\" data-version=\"2.0\">"
        + "Hello <span class=\"entity\" data-entity-id=\"keyword1\">$world</span>!"
        + "</div>";
    String expectedJson = "{\"keyword1\":{"
        + "\"type\":\"org.symphonyoss.fin.security\","
        + "\"version\":\"1.0\","
        + "\"id\":[{"
        + "\"type\":\"org.symphonyoss.fin.security.id.ticker\","
        + "\"value\":\"world\""
        + "}]}}";
    String expectedText = "world";
    String expectedMarkdown = "Hello $world!";

    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);

    Element messageML = context.getMessageML();
    assertEquals("Element attributes", Collections.emptyMap(), messageML.getChildren().get(1).getAttributes());
    verifyCashTag(messageML, expectedPresentationML, expectedJson, expectedText, expectedMarkdown);
  }

  @Test
  public void testCashTagWithSpaceAndOnlyNumbers() throws Exception {
    String input = "<messageML>Hello <cash tag=\"1234 789\"/>!</messageML>";

    String expectedPresentationML = "<div data-format=\"PresentationML\" data-version=\"2.0\">"
            + "Hello <span class=\"entity\" data-entity-id=\"keyword1\">$1234 789</span>!"
            + "</div>";
    String expectedJson = "{\"keyword1\":{"
            + "\"type\":\"org.symphonyoss.fin.security\","
            + "\"version\":\"1.0\","
            + "\"id\":[{"
            + "\"type\":\"org.symphonyoss.fin.security.id.ticker\","
            + "\"value\":\"1234 789\""
            + "}]}}";
    String expectedText = "1234 789";
    String expectedMarkdown = "Hello $1234 789!";

    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);

    Element messageML = context.getMessageML();
    assertEquals("Element attributes", Collections.emptyMap(), messageML.getChildren().get(1).getAttributes());
    verifyCashTag(messageML, expectedPresentationML, expectedJson, expectedText, expectedMarkdown);
  }

  @Test
  public void testCashTagNonAlnum() throws Exception {
    String input = "<messageML>Hello <cash tag=\"_hello.w-o-r-l-d_\"/>!</messageML>";

    String expectedPresentationML = "<div data-format=\"PresentationML\" data-version=\"2.0\">"
        + "Hello <span class=\"entity\" data-entity-id=\"keyword1\">$_hello.w-o-r-l-d_</span>!"
        + "</div>";
    String expectedJson = "{\"keyword1\":{"
        + "\"type\":\"org.symphonyoss.fin.security\","
        + "\"version\":\"1.0\","
        + "\"id\":[{"
        + "\"type\":\"org.symphonyoss.fin.security.id.ticker\","
        + "\"value\":\"_hello.w-o-r-l-d_\""
        + "}]}}";
    String expectedText = "_hello.w-o-r-l-d_";
    String expectedMarkdown = "Hello $_hello.w-o-r-l-d_!";

    // Verify by MessageML
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
    Element messageML = context.getMessageML();
    assertEquals("Element attributes", Collections.emptyMap(), messageML.getChildren().get(1).getAttributes());
    verifyCashTag(messageML, expectedPresentationML, expectedJson, expectedText, expectedMarkdown);

    // Verify by PresentationML
    context.parseMessageML(expectedPresentationML, expectedJson, MessageML.MESSAGEML_VERSION);
    messageML = context.getMessageML();
    verifyCashTag(messageML, expectedPresentationML, expectedJson, expectedText, expectedMarkdown);
  }

  @Test
  public void testCashTagMoreSpecialChars() throws Exception {
    String input = "<messageML>Hello <cash tag=\"_hello#w-o-r-l-d_\"/>!</messageML>";

    String expectedPresentationML = "<div data-format=\"PresentationML\" data-version=\"2.0\">"
        + "Hello <span class=\"entity\" data-entity-id=\"keyword1\">$_hello#w-o-r-l-d_</span>!"
        + "</div>";
    String expectedJson = "{\"keyword1\":{"
        + "\"type\":\"org.symphonyoss.fin.security\","
        + "\"version\":\"1.0\","
        + "\"id\":[{"
        + "\"type\":\"org.symphonyoss.fin.security.id.ticker\","
        + "\"value\":\"_hello#w-o-r-l-d_\""
        + "}]}}";
    String expectedText = "_hello#w-o-r-l-d_";
    String expectedMarkdown = "Hello $_hello#w-o-r-l-d_!";

    // Verify by MessageML
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
    Element messageML = context.getMessageML();
    assertEquals("Element attributes", Collections.emptyMap(), messageML.getChildren().get(1).getAttributes());
    verifyCashTag(messageML, expectedPresentationML, expectedJson, expectedText, expectedMarkdown);

    // Verify by PresentationML
    context.parseMessageML(expectedPresentationML, expectedJson, MessageML.MESSAGEML_VERSION);
    messageML = context.getMessageML();
    verifyCashTag(messageML, expectedPresentationML, expectedJson, expectedText, expectedMarkdown);
  }

  @Test
  public void testCashTagByPresentationMLDiv() throws Exception {

    String input = "<div data-format=\"PresentationML\" data-version=\"2.0\">"
        + "Hello <div class=\"entity\" data-entity-id=\"cash123\">world</div>!"
        + "</div>";

    String expectedPresentationML = "<div data-format=\"PresentationML\" data-version=\"2.0\">"
        + "Hello <div class=\"entity\" data-entity-id=\"cash123\">$world</div>!"
        + "</div>";
    String entityJson = "{\"cash123\":{"
        + "\"type\":\"org.symphonyoss.fin.security\","
        + "\"version\":\"1.0\","
        + "\"id\":[{"
        + "\"type\":\"org.symphonyoss.fin.security.id.ticker\","
        + "\"value\":\"world\""
        + "}]}}";
    String expectedText = "world";
    String expectedMarkdown = "Hello $world!";

    context.parseMessageML(input, entityJson, MessageML.MESSAGEML_VERSION);

    Element messageML = context.getMessageML();
    assertEquals("Element attributes", 1, messageML.getChildren().get(1).getAttributes().size());
    assertEquals("Element class attribute", "entity", messageML.getChildren().get(1).getAttribute("class"));
    verifyCashTag(messageML, expectedPresentationML, entityJson, expectedText, expectedMarkdown);
  }

  @Test
  public void testCashTagByPresentationMLSpan() throws Exception {

    String input = "<div data-format=\"PresentationML\" data-version=\"2.0\">"
        + "Hello <span class=\"entity\" data-entity-id=\"cash123\">world</span>!"
        + "</div>";

    String expectedPresentationML = "<div data-format=\"PresentationML\" data-version=\"2.0\">"
        + "Hello <span class=\"entity\" data-entity-id=\"cash123\">$world</span>!"
        + "</div>";
    String entityJson = "{\"cash123\":{"
        + "\"type\":\"org.symphonyoss.fin.security\","
        + "\"version\":\"1.0\","
        + "\"id\":[{"
        + "\"type\":\"org.symphonyoss.fin.security.id.ticker\","
        + "\"value\":\"world\""
        + "}]}}";
    String expectedText = "world";
    String expectedMarkdown = "Hello $world!";

    context.parseMessageML(input, entityJson, MessageML.MESSAGEML_VERSION);

    Element messageML = context.getMessageML();
    assertEquals("Element attributes", 1, messageML.getChildren().get(1).getAttributes().size());
    assertEquals("Element class attribute", "entity", messageML.getChildren().get(1).getAttribute("class"));
    verifyCashTag(messageML, expectedPresentationML, entityJson, expectedText, expectedMarkdown);
  }

  @Test
  public void testCashTagByPresentationMLMissingEntityId() throws Exception {
    String input = "<messageML>Hello <span class=\"entity\">world</span>!</messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("The attribute \"data-entity-id\" is required");
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testCashTagInvalidAttr() throws Exception {
    String invalidAttr = "<messageML>Hello <cash tag=\"world\" class=\"label\"/>!</messageML>";
    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Attribute \"class\" is not allowed in \"cash\"");
    context.parseMessageML(invalidAttr, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testPresentationMLShorthandCashTag() throws Exception {
    String invalidElement = "<div class=\"com.symphony.presentationml\"><cash tag=\"invalid\"/></div>";
    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Shorthand tag \"cash\" is not allowed in PresentationML");
    context.parseMessageML(invalidElement, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testCashtagBi() throws Exception {
    String input = "<messageML><cash tag=\"Hello\"/><cash tag=\"world\"/><cash tag=\"HelloWorld\"/></messageML>";
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);

    List<BiItem> expectedBiItems = getExpectedCashtagsBiItems();

    List<BiItem> biItems = context.getBiContext().getItems();
    assertEquals(biItems.size(), expectedBiItems.size());
    assertTrue(biItems.containsAll(expectedBiItems));
    assertTrue(expectedBiItems.containsAll(biItems));
  }

  @Test
  public void testBiContextMentionEntity() throws InvalidInputException, IOException,
      ProcessingException {

    String input = "<messageML>Hello <cash tag=\"world\"/>!</messageML>";
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
    List<BiItem> items = context.getBiContext().getItems();

    Map<String, Object> cashTagExpectedAttributes =
        Collections.singletonMap(BiFields.COUNT.getValue(), 1);
    Map<String, Object> entityExpectedAttributes =
        Collections.singletonMap(BiFields.ENTITY_TYPE.getValue(), CASHTAG_ENTITY_SUBTYPE);

    BiItem cashTagBiItemExpected = new BiItem(BiFields.CASHTAGS.getValue(), cashTagExpectedAttributes);
    BiItem entityBiItemExpected = new BiItem(BiFields.ENTITY.getValue(), entityExpectedAttributes);

    assertEquals(3, items.size());
    assertSameBiItem(cashTagBiItemExpected, items.get(0));
    assertSameBiItem(entityBiItemExpected, items.get(1));
    assertMessageLengthBiItem(items.get(2), input.length());
  }

  private List<BiItem> getExpectedCashtagsBiItems() {
    List<BiItem> biItems = new ArrayList<>();
    biItems.add(new BiItem(BiFields.CASHTAGS.getValue(), Collections.singletonMap(BiFields.COUNT.getValue(), 3)));
    biItems.add(new BiItem(BiFields.ENTITY.getValue(), Collections.singletonMap(BiFields.ENTITY_TYPE.getValue(), CASHTAG_ENTITY_SUBTYPE)));
    biItems.add(new BiItem(BiFields.ENTITY.getValue(), Collections.singletonMap(BiFields.ENTITY_TYPE.getValue(), CASHTAG_ENTITY_SUBTYPE)));
    biItems.add(new BiItem(BiFields.ENTITY.getValue(), Collections.singletonMap(BiFields.ENTITY_TYPE.getValue(), CASHTAG_ENTITY_SUBTYPE)));
    biItems.add(new BiItem(BiFields.MESSAGE_LENGTH.getValue(), Collections.singletonMap(BiFields.COUNT.getValue(), 85)));
    return biItems;
  }


  private void verifyCashTag(Element messageML, String expectedPresentationML, String expectedJson, String expectedText,
      String expectedMarkdown) throws Exception {
    assertEquals("Element children", 3, messageML.getChildren().size());

    Element cashtag = messageML.getChildren().get(1);

    assertEquals("Element class", CashTag.class, cashtag.getClass());
    assertEquals("Element tag name", "cash", cashtag.getMessageMLTag());
    assertEquals("Element text", expectedText, ((CashTag) cashtag).getTag());
    assertEquals("PresentationML", expectedPresentationML, context.getPresentationML());
    assertEquals("Markdown", expectedMarkdown, context.getMarkdown());
    assertEquals("EntityJSON", expectedJson, MAPPER.writeValueAsString(context.getEntityJson()));
    assertEquals("Legacy entities", 1, context.getEntities().size());

    JsonNode entity = context.getEntities().get("hashtags");
    assertNotNull("Entity node", entity);
    assertEquals("Entity count", 1, entity.size());

    assertEquals("Entity text", "$" + expectedText, entity.get(0).get("text").textValue());
    assertEquals("Entity id", "$" + expectedText, entity.get(0).get("id").textValue());
    assertEquals("Entity type", "KEYWORD", entity.get(0).get("type").textValue());
  }
}
