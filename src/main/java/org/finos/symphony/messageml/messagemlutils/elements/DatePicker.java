package org.finos.symphony.messageml.messagemlutils.elements;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.finos.symphony.messageml.messagemlutils.MessageMLContext;
import org.finos.symphony.messageml.messagemlutils.MessageMLParser;
import org.finos.symphony.messageml.messagemlutils.bi.BiContext;
import org.finos.symphony.messageml.messagemlutils.bi.BiFields;
import org.finos.symphony.messageml.messagemlutils.bi.BiItem;
import org.finos.symphony.messageml.messagemlutils.exceptions.InvalidInputException;
import org.finos.symphony.messageml.messagemlutils.markdown.nodes.form.DatePickerNode;
import org.finos.symphony.messageml.messagemlutils.util.XMLAttribute;
import org.finos.symphony.messageml.messagemlutils.util.XmlPrintStream;
import org.finos.symphony.messageml.messagemlutils.util.pojo.DateInterval;
import org.w3c.dom.Node;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class representing a DatePicker inside a Form.
 *
 * @author enrico.molino (16/11/2020)
 */
public class DatePicker extends FormElement implements LabelableElement, TooltipableElement {

  public static final String MESSAGEML_TAG = "date-picker";
  public static final String PRESENTATIONML_INPUT_TYPE = "date";
  private static final String PRESENTATIONML_TAG = "input";
  private static final String VALUE_ATTR = "value";
  private static final String REQUIRED_ATTR = "required";
  private static final String PLACEHOLDER_ATTR = "placeholder";
  private static final String MIN_ATTR = "min";
  private static final String MAX_ATTR = "max";
  private static final String DISABLED_DATE_ATTR = "disabled-date";
  private static final String HIGHLIGHTED_DATE_ATTR = "highlighted-date";
  private static final String FORMAT_ATTR = "format";

  private static final int DATE_RANGE_MAX_LENGTH = 1024;
  private static final int DEFAULT_MAX_LENGTH = 64;

  // PresentationML specific attributes
  private static final String PRESENTATIONML_DISABLED_DATE_ATTR = "data-disabled-date";
  private static final String PRESENTATIONML_HIGHLIGHTED_DATE_ATTR = "data-highlighted-date";
  private static final String PRESENTATIONML_FORMAT_ATTR = "data-format";
  protected static final String DISABLED_ATTR = "disabled";
  protected static final String READONLY_ATTR = "readonly";

  private static final String DATE_FORMAT_ALLOWED = "^[0-9Mdy\\/. -:]+$";

  public DatePicker(Element parent, FormatEnum format) {
    super(parent, MESSAGEML_TAG, format);
  }

  @Override
  protected void buildAttribute(MessageMLParser parser, Node item) throws InvalidInputException {
    switch (item.getNodeName()) {
      case NAME_ATTR:
      case VALUE_ATTR:
      case REQUIRED_ATTR:
      case PLACEHOLDER_ATTR:
      case MIN_ATTR:
      case MAX_ATTR:
      case LABEL:
      case TITLE:
      case DISABLED_ATTR:
      case READONLY_ATTR:
        setAttribute(item.getNodeName(), getStringAttribute(item));
        break;
      case DISABLED_DATE_ATTR:
      case HIGHLIGHTED_DATE_ATTR:
      case FORMAT_ATTR:
      case FORMNOVALIDATE_ATTR:
        if (this.format != FormatEnum.MESSAGEML) {
          throwInvalidInputException(item);
        }
        setAttribute(item.getNodeName(), getStringAttribute(item));
        break;
      case TYPE_ATTR:
      case ID_ATTR:
      case PRESENTATIONML_DISABLED_DATE_ATTR:
      case PRESENTATIONML_HIGHLIGHTED_DATE_ATTR:
      case PRESENTATIONML_FORMAT_ATTR:
      case FORMNOVALIDATE_PML_ATTR:
        if (this.format != FormatEnum.PRESENTATIONML) {
          throwInvalidInputException(item);
        }
        fillAttributes(parser, item);
        setAttribute(item.getNodeName(), getStringAttribute(item));
        break;
      default:
        throwInvalidInputException(item);
    }
  }

  @Override
  public void validate() throws InvalidInputException {
    super.validate();

    assertAttributeNotBlank(NAME_ATTR);
    assertAttributeMaxLength(NAME_ATTR, DEFAULT_MAX_LENGTH);

    if (getAttribute(VALUE_ATTR) != null) {
      assertDateFormat(VALUE_ATTR, DateTimeFormatter.ISO_DATE);
    }
    if (getAttribute(REQUIRED_ATTR) != null) {
      assertAttributeValue(REQUIRED_ATTR, Arrays.asList("true", "false"));
    }
    if (getAttribute(MIN_ATTR) != null) {
      assertDateFormat(MIN_ATTR, DateTimeFormatter.ISO_DATE);
    }
    if (getAttribute(MAX_ATTR) != null) {
      assertDateFormat(MAX_ATTR, DateTimeFormatter.ISO_DATE);
    }
    if (getAttribute(DISABLED_ATTR) != null) {
      assertAttributeValue(DISABLED_ATTR, Arrays.asList("true", "false"));
    }
    if (getAttribute(READONLY_ATTR) != null) {
      assertAttributeValue(READONLY_ATTR, Arrays.asList("true", "false"));
    }
    assertJsonDatesRange(DISABLED_DATE_ATTR);
    assertJsonDatesRange(HIGHLIGHTED_DATE_ATTR);

    if (getAttribute(FORMAT_ATTR) != null) {
      assertAttributeMaxLength(FORMAT_ATTR, DEFAULT_MAX_LENGTH);
      String format = getAttribute(FORMAT_ATTR);
      if(!format.matches(DATE_FORMAT_ALLOWED)){
        throw new InvalidInputException("Attribute \"%s\" contains an unsupported date format, only 'M', 'd' and 'y' are supported with a space or '.','-','/',':' as separator", FORMAT_ATTR);
      }
      try {
        DateTimeFormatter.ofPattern(getAttribute(FORMAT_ATTR));
      } catch (IllegalArgumentException i) {
        throw new InvalidInputException("Attribute \"%s\" contains an invalid date format", FORMAT_ATTR);
      }
    }
    assertAttributeMaxLength(TITLE, DEFAULT_MAX_LENGTH);
    assertAttributeMaxLength(LABEL, DEFAULT_MAX_LENGTH);
  }

  @Override
  public void asPresentationML(XmlPrintStream out, MessageMLContext context) {
    Map<String, Object> presentationAttrs = buildDataPickerInputAttributes();
    if (isSplittable()) {
      // open div + adding splittable elements
      presentationAttrs.put(ID_ATTR, splittableAsPresentationML(out, context));
      // render element
      innerAsPresentationML(out, presentationAttrs);
      // close div
      out.closeElement();
    } else {
      innerAsPresentationML(out, presentationAttrs);
    }
  }

  private void innerAsPresentationML(XmlPrintStream out, Map<String, Object> presentationAttrs) {
    out.openElement(PRESENTATIONML_TAG, presentationAttrs);
    out.closeElement();
  }

  private Map<String, Object> buildDataPickerInputAttributes() {
    Map<String, Object> presentationAttrs = new LinkedHashMap<>();

    presentationAttrs.put(TYPE_ATTR, PRESENTATIONML_INPUT_TYPE);
    presentationAttrs.put(NAME_ATTR, getAttribute(NAME_ATTR));

    if (getAttribute(VALUE_ATTR) != null) {
      presentationAttrs.put(VALUE_ATTR, getAttribute(VALUE_ATTR));
    }
    if (getAttribute(PLACEHOLDER_ATTR) != null) {
      presentationAttrs.put(PLACEHOLDER_ATTR, getAttribute(PLACEHOLDER_ATTR));
    }
    if (getAttribute(MIN_ATTR) != null) {
      presentationAttrs.put(MIN_ATTR, getAttribute(MIN_ATTR));
    }
    if (getAttribute(MAX_ATTR) != null) {
      presentationAttrs.put(MAX_ATTR, getAttribute(MAX_ATTR));
    }
    if (getAttribute(REQUIRED_ATTR) != null) {
      presentationAttrs.put(REQUIRED_ATTR, getAttribute(REQUIRED_ATTR));
    }
    if (getAttribute(DISABLED_DATE_ATTR) != null) {
      presentationAttrs.put(PRESENTATIONML_DISABLED_DATE_ATTR, convertJsonDateToPresentationML(DISABLED_DATE_ATTR));
    }
    if (getAttribute(HIGHLIGHTED_DATE_ATTR) != null) {
      presentationAttrs.put(PRESENTATIONML_HIGHLIGHTED_DATE_ATTR, convertJsonDateToPresentationML(HIGHLIGHTED_DATE_ATTR));
    }
    if (getAttribute(FORMAT_ATTR) != null) {
      presentationAttrs.put(PRESENTATIONML_FORMAT_ATTR, getAttribute(FORMAT_ATTR));
    }
    if (getAttribute(DISABLED_ATTR) != null) {
      presentationAttrs.put(DISABLED_ATTR, getAttribute(DISABLED_ATTR));
    }
    if (getAttribute(READONLY_ATTR) != null) {
      presentationAttrs.put(READONLY_ATTR, getAttribute(READONLY_ATTR));
    }
    // PresentationML compatibility
    if (getAttribute(PRESENTATIONML_DISABLED_DATE_ATTR) != null) {
      presentationAttrs.put(PRESENTATIONML_DISABLED_DATE_ATTR, getAttribute(PRESENTATIONML_DISABLED_DATE_ATTR));
    }
    if (getAttribute(PRESENTATIONML_HIGHLIGHTED_DATE_ATTR) != null) {
      presentationAttrs.put(PRESENTATIONML_HIGHLIGHTED_DATE_ATTR, getAttribute(PRESENTATIONML_HIGHLIGHTED_DATE_ATTR));
    }
    if (getAttribute(PRESENTATIONML_FORMAT_ATTR) != null) {
      presentationAttrs.put(PRESENTATIONML_FORMAT_ATTR, getAttribute(PRESENTATIONML_FORMAT_ATTR));
    }
    if (getAttribute(FORMNOVALIDATE_ATTR) != null) {
      presentationAttrs.put(FORMNOVALIDATE_PML_ATTR, getAttribute(FORMNOVALIDATE_ATTR));
    }
    if(getAttribute(FORMNOVALIDATE_PML_ATTR) != null) {
      presentationAttrs.put(FORMNOVALIDATE_PML_ATTR, getAttribute(FORMNOVALIDATE_ATTR));
    }
    return presentationAttrs;
  }

  /**
   * The Json for PresentationML is different from MessageML It needs to be rewritten, by adding the
   * type attribute, based on the content
   */
  private XMLAttribute convertJsonDateToPresentationML(String attributeName) {
    try {
      DateInterval[] dateIntervals = MAPPER.readValue(getAttribute(attributeName), DateInterval[].class);
      String result = MAPPER.writeValueAsString(dateIntervals);
      return XMLAttribute.of(result, XMLAttribute.Format.JSON);
    } catch (JsonProcessingException e) {
      // this exception should never happens because this method is called after validation
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Validates date ranges attributes, they should contains a json of a similar format:
   * <p>
   * [ {from: ‘YYYY-MM-DD’, to: ‘YYYY-MM-DD’}, {day: ‘YYYY-MM-DD’ }, {day: ‘YYYY-MM-DD’ }, {day:
   * ‘YYYY-MM-DD’ }, {from: ‘YYYY-MM-DD’, to: ‘YYYY-MM-DD’}, {daysOfWeek: [0, 1]} ]
   */
  private void assertJsonDatesRange(String attributeName) throws InvalidInputException {
    String attributeValue = getAttribute(attributeName);
    if (attributeValue != null) {
      if (attributeValue.length() > DATE_RANGE_MAX_LENGTH) {
        throw new InvalidInputException(
            String.format("Attribute \"%s\" exceed maximum allowed length (%d)", attributeName,
                DATE_RANGE_MAX_LENGTH));
      }
      try {
        DateInterval[] dateIntervals = MAPPER.readValue(attributeValue, DateInterval[].class);
        for(DateInterval dateInterval:dateIntervals){
          dateInterval.assertIsValid();
        }
      } catch (JsonProcessingException e) {
        throw new InvalidInputException(
            String.format("Error parsing json in attribute \"%s\": %s", attributeName,
                e.getMessage()), e);
      }
    }
  }

  @Override
  public org.commonmark.node.Node asMarkdown() {
    return new DatePickerNode(getAttribute(LABEL), getAttribute(TITLE), getAttribute(PLACEHOLDER_ATTR));
  }

  @Override
  public void updateBiContext(BiContext context) {
    Map<String, Object> attributesMapBi = new HashMap<>();

    this.putOneIfPresent(attributesMapBi, BiFields.TITLE.getValue(), TITLE);
    this.putOneIfPresent(attributesMapBi, BiFields.PLACEHOLDER.getValue(), PLACEHOLDER_ATTR);
    this.putOneIfPresent(attributesMapBi, BiFields.LABEL.getValue(), LABEL);
    this.putOneIfPresent(attributesMapBi, BiFields.REQUIRED.getValue(), REQUIRED_ATTR);
    this.computeAndPutDefault(attributesMapBi);
    this.computeAndPutValidationProperties(attributesMapBi);

    context.addItem(new BiItem(BiFields.DATE_SELECTOR.getValue(), attributesMapBi));
  }

  private void computeAndPutDefault(Map<String, Object> attributesMapBi) {
    boolean hasDefaultValue = getAttribute(VALUE_ATTR) != null;
    if (hasDefaultValue) {
      attributesMapBi.put(BiFields.DEFAULT.getValue(), 1);
    }
  }

  private void computeAndPutValidationProperties(Map<String, Object> attributesMapBi) {
    boolean validationMin = getAttribute(MIN_ATTR) != null;
    boolean validationMax = getAttribute(MAX_ATTR) != null;
    boolean validationPattern = getAttribute(FORMAT_ATTR) != null;
    boolean validationOptions = getAttribute(DISABLED_DATE_ATTR) != null;
    boolean highlightedOptions = getAttribute(HIGHLIGHTED_DATE_ATTR) != null;
    boolean hasValidation = validationMin || validationMax || validationPattern || validationOptions;

    if (validationMin) {
      attributesMapBi.put(BiFields.VALIDATION_MIN.getValue(), 1);
    }

    if (validationMax) {
      attributesMapBi.put(BiFields.VALIDATION_MAX.getValue(), 1);
    }

    if (validationPattern) {
      attributesMapBi.put(BiFields.VALIDATION_PATTERN.getValue(), 1);
    }

    if (validationOptions) {
      attributesMapBi.put(BiFields.VALIDATION_OPTIONS.getValue(), 1);
    }

    if (highlightedOptions) {
      attributesMapBi.put(BiFields.HIGHLIGHTED_OPTIONS.getValue(), 1);
    }

    if (hasValidation) {
      attributesMapBi.put(BiFields.VALIDATION.getValue(), 1);
    }
  }
}
