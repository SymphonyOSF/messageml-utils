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

import org.commonmark.node.Node;
import org.finos.symphony.messageml.messagemlutils.MessageMLParser;
import org.finos.symphony.messageml.messagemlutils.bi.BiContext;
import org.finos.symphony.messageml.messagemlutils.bi.BiFields;
import org.finos.symphony.messageml.messagemlutils.exceptions.InvalidInputException;
import org.finos.symphony.messageml.messagemlutils.markdown.nodes.TableCellNode;

/**
 * Class representing a table cell container.
 *
 * @author lukasz
 * @since 3/27/17
 */
public class TableCell extends Element {
  public static final String MESSAGEML_TAG = "td";
  private static final String ATTR_ROWSPAN = "rowspan";
  private static final String ATTR_COLSPAN = "colspan";

  public TableCell(Element parent) {
    super(parent, MESSAGEML_TAG);
  }

  @Override
  void buildAttribute(MessageMLParser parser,
      org.w3c.dom.Node item) throws InvalidInputException {
    switch (item.getNodeName()) {
      case ATTR_ROWSPAN:
        setAttribute(ATTR_ROWSPAN, getLongAttribute(item).toString());
        break;
      case ATTR_COLSPAN:
        setAttribute(ATTR_COLSPAN, getLongAttribute(item).toString());
        break;
      default:
        super.buildAttribute(parser, item);
    }
  }

  @Override
  public Node asMarkdown() {
    return new TableCellNode();
  }

  @Override
  public String toString() {
    return "Cell";
  }

  @Override
  void updateBiContext(BiContext context) {
    super.updateBiContext(context);
    if (getAttribute(ATTR_ROWSPAN) != null) {
      context.updateItemCount(BiFields.TABLE_CELL_ROW_SPAN.getValue());
    }
    if (getAttribute(ATTR_COLSPAN) != null) {
      context.updateItemCount(BiFields.TABLE_CELL_COL_SPAN.getValue());
    }
  }
}
