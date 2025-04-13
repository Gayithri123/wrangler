/*
 * Copyright © 2017-2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under
 * the License.
 */

 package io.cdap.wrangler.api.parser;

 import com.google.gson.JsonElement;
 import com.google.gson.JsonPrimitive;
 
 /**
  * Token representing a time duration with units (e.g., ms, s, m, h).
  */
 public class TimeDuration implements Token {
   private final String raw;
   private final double value;
   private final String unit;
 
   public TimeDuration(String raw) {
     this.raw = raw.trim();
     String lower = raw.toLowerCase();
     int unitStart = findUnitStart(lower);
 
     if (unitStart == -1) {
       throw new IllegalArgumentException("Invalid time format: " + raw);
     }
 
     this.value = Double.parseDouble(lower.substring(0, unitStart));
     this.unit = lower.substring(unitStart);
   }
 
   private int findUnitStart(String s) {
     for (int i = 0; i < s.length(); i++) {
       if (Character.isLetter(s.charAt(i))) {
         return i;
       }
     }
     return -1;
   }
 
   public long getMilliseconds() {
     switch (unit) {
       case "ms":
         return (long) (value);
       case "s":
         return (long) (value * 1000);
       case "m":
         return (long) (value * 60 * 1000);
       case "h":
         return (long) (value * 60 * 60 * 1000);
       default:
         throw new IllegalArgumentException("Unknown time unit: " + unit);
     }
   }
 
   @Override
   public Object value() {
     return getMilliseconds();
   }
 
   @Override
   public TokenType type() {
     return TokenType.TIME_DURATION;
   }
 
   @Override
   public JsonElement toJson() {
     return new JsonPrimitive(getMilliseconds());
   }
 
   @Override
   public String toString() {
     return raw;
   }
}
