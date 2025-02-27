/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.streamnative.pulsar.handlers.kop.format;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.streamnative.pulsar.handlers.kop.KafkaServiceConfiguration;
import org.apache.pulsar.broker.service.plugin.EntryFilterWithClassLoader;

/**
 * Factory of EntryFormatter.
 *
 * @see EntryFormatter
 */
public class EntryFormatterFactory {

    enum EntryFormat {
        PULSAR,
        KAFKA,
        MIXED_KAFKA
    }

    public static EntryFormatter create(final KafkaServiceConfiguration kafkaConfig,
                                        final ImmutableMap<String, EntryFilterWithClassLoader> entryfilterMap) {
        final String format = kafkaConfig.getEntryFormat();
        try {
            EntryFormat entryFormat = Enum.valueOf(EntryFormat.class, format.toUpperCase());

            ImmutableList<EntryFilterWithClassLoader> entryfilters =
                    entryfilterMap == null ? ImmutableList.of() : entryfilterMap.values().asList();

            switch (entryFormat) {
                case PULSAR:
                    return new PulsarEntryFormatter(entryfilters);
                case KAFKA:
                    return new KafkaV1EntryFormatter(entryfilters);
                case MIXED_KAFKA:
                    return new KafkaMixedEntryFormatter(entryfilters);
                default:
                    throw new Exception("No EntryFormatter for " + entryFormat);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Unsupported entry.format '" + format + "': " + e.getMessage());
        }
    }
}
