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

/**
 * For elements which have a different representaion in MessageML and PresentationML ("convenience tags"), this enum is used
 * to determine in which format the element was provided.
 * @author lukasz
 * @since 4/20/17
 */
public enum FormatEnum {
  MESSAGEML,
  PRESENTATIONML
}
