/*
 *  Copyright © 2017-2019 Cask Data, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License. You may obtain a copy of
 *  the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
 */
package io.cdap.directives.aggregates;

import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.TestingRig;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class AggregateStatsDirectiveTest {

  @Test
  public void testAggregateStats() throws Exception {
    // Step 1: Prepare input data
    List<Row> rows = Arrays.asList(
      new Row("data_transfer_size", "10MB").add("response_time", "200ms"),
      new Row("data_transfer_size", "2MB").add("response_time", "1.5s"),
      new Row("data_transfer_size", "8MB").add("response_time", "300ms")
    );

    // Step 2: Recipe using your new directive
    String[] recipe = new String[]{
      "aggregate-stats :data_transfer_size :response_time :total_size_mb :total_time_sec"
    };

    // Step 3: Execute recipe using TestingRig
    List<Row> result = TestingRig.execute(recipe, rows);

    // Step 4: Validate the result
    Assert.assertEquals(1, result.size());

    Row output = result.get(0);

    // total_size_mb = (10+2+8) = 20MB
    // total_time_sec = 0.2 + 1.5 + 0.3 = 2.0
    double totalSize = (double) output.getValue("total_size_mb");
    double totalTime = (double) output.getValue("total_time_sec");

    Assert.assertEquals(20.0, totalSize, 0.001);
    Assert.assertEquals(2.0, totalTime, 0.001);
  }
}
