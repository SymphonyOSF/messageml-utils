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

package org.symphonyoss.symphony.messageml.elements;

import org.commonmark.node.Node;
import org.commonmark.node.StrongEmphasis;
import org.symphonyoss.symphony.messageml.bi.BiContext;
import org.symphonyoss.symphony.messageml.exceptions.InvalidInputException;

import java.util.regex.Pattern;

/**
 * Class representing a section heading of level 1-6.
 *
 * @author lukasz
 * @since 3/27/17
 */
public class Header extends Element {
  private static final Pattern PATTERN = Pattern.compile("^h([1-6])$");
  public static final String MESSAGEML_TAG = "h";
  private static final String MARKDOWN = "**";

  private final String tag;

  public Header(Element parent, String tag) {
    super(parent, tag);
    this.tag = tag;
  }

  @Override
  public Node asMarkdown() {
    return new StrongEmphasis(MARKDOWN);
  }

  public static boolean isHeaderElement(String tag) {
    return PATTERN.matcher(tag).matches();
  }

  @Override
  public void validate() throws InvalidInputException {
    assertPhrasingContent();
  }

  @Override
  public void updateBiContext(BiContext context) {
    context.updateItemInContext(getClass().getSimpleName(), tag);
  }
}
