package com.mononokehime.events.common;

/*-
 * #%L
 * Events Server Application
 * %%
 * Copyright (C) 2020 Mononokehime
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public final class LocaleDateFormatter {
    private LocaleDateFormatter() {
    }

    private static final DateTimeFormatter DATETIME_FORMATTER = ISODateTimeFormat.dateTime();
    public static DateTimeFormatter getDateTimeFormatter() {
        return DATETIME_FORMATTER;
    }
}
