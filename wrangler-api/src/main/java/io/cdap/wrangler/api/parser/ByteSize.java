
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
  * Token representing a byte size with units (e.g., KB, MB, GB).
  */
 public class ByteSize implements Token {
   private final String raw;
   private final double value;
   private final String unit;
 
   public ByteSize(String raw) {
     this.raw = raw.trim();
     String upper = raw.toUpperCase();
     int unitStart = findUnitStart(upper);
 
     if (unitStart == -1) {
       throw new IllegalArgumentException("Invalid byte size format: " + raw);
     }
 
     this.value = Double.parseDouble(upper.substring(0, unitStart));
     this.unit = upper.substring(unitStart);
   }
 
   private int findUnitStart(String s) {
     for (int i = 0; i < s.length(); i++) {
       if (Character.isLetter(s.charAt(i))) {
         return i;
       }
     }
     return -1;
   }
 
   public long getBytes() {
     switch (unit) {
       case "KB":
         return (long) (value * 1024);
       case "MB":
         return (long) (value * 1024 * 1024);
       case "GB":
         return (long) (value * 1024 * 1024 * 1024);
       case "TB":
         return (long) (value * 1024L * 1024L * 1024L * 1024L);
       case "B":
         return (long) (value);
       default:
         throw new IllegalArgumentException("Unknown byte unit: " + unit);
     }
   }
 
   @Override
   public Object value() {
     return getBytes();
   }
 
   @Override
   public TokenType type() {
     return TokenType.BYTE_SIZE;
   }
 
   @Override
   public JsonElement toJson() {
     return new JsonPrimitive(getBytes());
   }
 
   @Override
   public String toString() {
     return raw;
   }
}

