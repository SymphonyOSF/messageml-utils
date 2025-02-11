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
import org.finos.symphony.messageml.messagemlutils.markdown.nodes.TableCellNode;

/**
 * Class representing a table header cell container.
 *
 * @author lukasz
 * @since 3/27/17
 */
public class TableHeaderCell extends Element {
  public static final String MESSAGEML_TAG = "th";

  public TableHeaderCell(Element parent) {
    super(parent, MESSAGEML_TAG);
  }

  @Override
  public Node asMarkdown() {
    return new TableCellNode();
  }

  @Override
  public String toString() {
    return "Cell";
  }
}
