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

 package io.cdap.wrangler.codec;

 import io.cdap.wrangler.TestingRig;
 import io.cdap.wrangler.api.Row;
 import org.junit.Assert;
 import org.junit.Test;
 
 import java.util.Arrays;
 import java.util.List;
 
 /**
  * Unit test for the aggregate-stats directive.
  */
 public class AggregateStatsTest {
 
   @Test
   public void testAggregateStatsTotals() throws Exception {
     List<Row> input = Arrays.asList(
       new Row("data_size", "1MB").add("duration", "2s"),
       new Row("data_size", "512KB").add("duration", "1.5s"),
       new Row("data_size", "256KB").add("duration", "500ms")
     );
 
     String[] recipe = new String[] {
       "aggregate-stats :data_size :duration total_mb total_sec 'MB' 's'"
     };
 
     List<Row> results = TestingRig.execute(recipe, input);
 
     Assert.assertEquals(1, results.size());
 
     double totalMB = (1 * 1024 * 1024 + 512 * 1024 + 256 * 1024) / (1024.0 * 1024);
     double totalSec = (2 * 1000 + 1500 + 500) / 1000.0;
 
     Assert.assertEquals(totalMB, (double) results.get(0).getValue("total_mb"), 0.001);
     Assert.assertEquals(totalSec, (double) results.get(0).getValue("total_sec"), 0.001);
   }
 }
  