/*
 * Copyright 2016-2017 MessageML - Symphony LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.finos.symphony.messageml.messagemlutils.elements;

import org.finos.symphony.messageml.messagemlutils.MessageMLContext;
import org.finos.symphony.messageml.messagemlutils.MessageMLParser;
import org.finos.symphony.messageml.messagemlutils.bi.BiContext;
import org.finos.symphony.messageml.messagemlutils.bi.BiFields;
import org.finos.symphony.messageml.messagemlutils.exceptions.InvalidInputException;
import org.finos.symphony.messageml.messagemlutils.util.XmlPrintStream;
import org.w3c.dom.Node;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class representing a convenience element which has a number of visual elements and which can be opened and closed.
 * Translated to a div element.
 *
 * @author lukasz
 * @since 3/27/17
 */
public class Card extends Element {

  public static final String MESSAGEML_TAG = "card";
  public static final String PRESENTATIONML_ICON_ATTR = "data-icon-src";
  public static final String PRESENTATIONML_ACCENT_ATTR = "data-accent-color";
  public static final String PRESENTATIONML_CLASS = "card";
  private static final String PRESENTATIONML_TAG = "div";
  private static final String ATTR_ICON = "iconSrc";
  private static final String ATTR_ACCENT = "accent";

  public Card(Element parent, FormatEnum format) {
    super(parent, MESSAGEML_TAG, format);
  }

  @Override
  protected void buildAttribute(MessageMLParser parser, Node item) throws InvalidInputException {
    switch (item.getNodeName()) {
      case PRESENTATIONML_ICON_ATTR:
      case ATTR_ICON:
        setAttribute(ATTR_ICON, getStringAttribute(item));
        break;
      case PRESENTATIONML_ACCENT_ATTR:
      case ATTR_ACCENT:
        setAttribute(ATTR_ACCENT, getStringAttribute(item));
        break;
      default:
        super.buildAttribute(parser, item);
    }
  }

  @Override
  public void asPresentationML(XmlPrintStream out, MessageMLContext context) {
    Map<String, String> presentationAttrs = new LinkedHashMap<>();
    if (getAttribute(CLASS_ATTR) != null) {
      presentationAttrs.put(CLASS_ATTR, String.format("%s %s", PRESENTATIONML_CLASS, getAttribute(CLASS_ATTR)));
    } else {
      presentationAttrs.put(CLASS_ATTR, PRESENTATIONML_CLASS);
    }
    if (getAttribute(ATTR_ICON) != null) {
      presentationAttrs.put(PRESENTATIONML_ICON_ATTR, getAttribute(ATTR_ICON));
    }
    if (getAttribute(ATTR_ACCENT) != null) {
      presentationAttrs.put(PRESENTATIONML_ACCENT_ATTR, getAttribute(ATTR_ACCENT));
    }

    out.openElement(PRESENTATIONML_TAG, presentationAttrs);

    for (Element child : getChildren()) {
      child.asPresentationML(out, context);
    }

    out.closeElement();
  }

  @Override
  public String getPresentationMLTag() {
    return PRESENTATIONML_TAG;
  }

  @Override
  void updateBiContext(BiContext context) {
    super.updateBiContext(context);
    context.updateItemCount(BiFields.CARD.getValue());
  }
}
