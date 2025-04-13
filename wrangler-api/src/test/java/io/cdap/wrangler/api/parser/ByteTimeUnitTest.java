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
package io.cdap.wrangler.api.parser;

import org.junit.Assert;
import org.junit.Test;

public class ByteTimeUnitTest {
    @Test
    public void testByteSizeParsing() {
        Assert.assertEquals(1024, new ByteSize("1KB").getBytes());
        Assert.assertEquals(1572864, new ByteSize("1.5MB").getBytes());
        Assert.assertEquals(1073741824, new ByteSize("1GB").getBytes());
    }

    @Test
    public void testTimeDurationParsing() {
        Assert.assertEquals(5, new TimeDuration("5ms").getMilliseconds());
        Assert.assertEquals(2100, new TimeDuration("2.1s").getMilliseconds());
        Assert.assertEquals(60000, new TimeDuration("1m").getMilliseconds());
        Assert.assertEquals(7200000, new TimeDuration("2h").getMilliseconds());
    }
}
