/*
 * Copyright © 2017-2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.cdap.wrangler.codec;

import io.cdap.wrangler.api.Arguments;
import io.cdap.wrangler.api.Directive;
import io.cdap.wrangler.api.DirectiveExecutionException;
import io.cdap.wrangler.api.ExecutorContext;
import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.ColumnName;
import io.cdap.wrangler.api.parser.Text;
import io.cdap.wrangler.api.parser.TimeDuration;
import io.cdap.wrangler.api.parser.TokenType;
import io.cdap.wrangler.api.parser.UsageDefinition;

import java.util.Collections;
import java.util.List;

/**
 * Directive to compute total or average values for byte size and time duration columns.
 */
public class AggregateStats implements Directive {
    private String byteCol, timeCol, outByteCol, outTimeCol;
    private String byteUnit = "B"; // default
    private String timeUnit = "ms"; // default

    @Override
    public UsageDefinition define() {
        UsageDefinition.Builder builder = UsageDefinition.builder("aggregate-stats");
        builder.define("source_byte_col", TokenType.COLUMN_NAME);
        builder.define("source_time_col", TokenType.COLUMN_NAME);
        builder.define("target_byte_col", TokenType.COLUMN_NAME);
        builder.define("target_time_col", TokenType.COLUMN_NAME);
        return builder.build();
    }


    @Override
    public void initialize(Arguments args) {
        byteCol = ((ColumnName) args.value("source_byte_col")).value();
        timeCol = ((ColumnName) args.value("source_time_col")).value();
        outByteCol = ((ColumnName) args.value("target_byte_col")).value();
        outTimeCol = ((ColumnName) args.value("target_time_col")).value();

        if (args.contains("byte_unit")) {
            byteUnit = ((Text) args.value("byte_unit")).value().toUpperCase();
        }
        if (args.contains("time_unit")) {
        timeUnit = ((Text) args.value("time_unit")).value().toLowerCase();
      }
          
    }

    @Override
    public List<Row> execute(List<Row> rows, ExecutorContext ctx) throws DirectiveExecutionException {
        long totalBytes = 0;
        long totalMillis = 0;

        for (Row row : rows) {
            Object byteVal = row.getValue(byteCol);
            Object timeVal = row.getValue(timeCol);

            if (byteVal instanceof ByteSize) {
                totalBytes += ((ByteSize) byteVal).getBytes();
            } else if (byteVal instanceof String) {
                totalBytes += new ByteSize((String) byteVal).getBytes();
            }

            if (timeVal instanceof TimeDuration) {
                totalMillis += ((TimeDuration) timeVal).getMilliseconds();
            } else if (timeVal instanceof String) {
                totalMillis += new TimeDuration((String) timeVal).getMilliseconds();
            }
        }

        Row result = new Row();

        result.add(outByteCol, convertBytes(totalBytes, byteUnit));
        result.add(outTimeCol, convertTime(totalMillis, timeUnit));

        return Collections.singletonList(result);
    }

    private double convertBytes(long bytes, String unit) {
        switch (unit) {
            case "KB": return bytes / 1024.0;
            case "MB": return bytes / (1024.0 * 1024);
            case "GB": return bytes / (1024.0 * 1024 * 1024);
            default: return bytes;
        }
    }

    private double convertTime(long millis, String unit) {
        switch (unit) {
            case "s": return millis / 1000.0;
            case "m": return millis / (60 * 1000.0);
            case "h": return millis / (3600 * 1000.0);
            default: return millis;
        }
    }
    @Override
    public void destroy() {
    }

}
