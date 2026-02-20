/*
 *  Copyright 2026 CNM Ingenuity, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package edu.cnm.deepdive.codebreaker.client.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Singleton class that provides information about the code points in the game pool.
 */
public class CodePointInfo {

  private static final String POOL_NAMES_KEY = "pool_names";
  private static final String POOL_CLASSES_KEY = "pool_classes";

  private static final Pattern PROPERTY_LIST_DELIMITER = Pattern.compile("\\s*,\\s*");

  private final Map<Integer, String> codePointNames;
  private final Map<Integer, String> codePointClasses;
  
  private CodePointInfo() {
    ResourceBundle resources = ResourceBundle.getBundle(Constants.BUNDLE_BASE_NAME);
    String pool = resources.getString(Constants.POOL_KEY);
    List<Integer> poolCodePoints = pool
        .codePoints()
        .boxed()
        .toList();
    List<String> poolNames = splitToList(resources.getString(POOL_NAMES_KEY));
    List<String> poolClasses = splitToList(resources.getString(POOL_CLASSES_KEY));
    codePointNames = new LinkedHashMap<>();
    codePointClasses = new LinkedHashMap<>();
    Iterator<Integer> codePointIter = poolCodePoints.iterator();
    Iterator<String> nameIter = poolNames.iterator();
    Iterator<String> classIter = poolClasses.iterator();
    while (codePointIter.hasNext() && nameIter.hasNext() && classIter.hasNext()) {
      Integer codePoint = codePointIter.next();
      codePointNames.put(codePoint, nameIter.next());
      codePointClasses.put(codePoint, classIter.next());
    }
  }
  
  public static CodePointInfo getInstance() {
    return Holder.INSTANCE;
  }

  public String getName(Integer codePoint) {
    return codePointNames.get(codePoint);
  }
  
  public String getStyleClass(Integer codePoint) {
    return codePointClasses.get(codePoint);
  }
  
  private List<String> splitToList(String joined) {
    return PROPERTY_LIST_DELIMITER
        .splitAsStream(joined)
        .filter(Predicate.not(String::isEmpty))
        .toList();
  }

  private static class Holder {
  
    private static final CodePointInfo INSTANCE = new CodePointInfo();
    
  }
  
}
