/*
 * Copyright 2019 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.aeron.archive;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;

import static io.aeron.protocol.DataHeaderFlyweight.HEADER_LENGTH;

/**
 * Provides API to compute CRC for a data frame in a buffer.
 */
final class Crc32Helper
{
    private Crc32Helper()
    {
    }

    /**
     * Compute CRC over the frame's payload.
     *
     * @param state       container for the CRC state.
     * @param buffer      containing the frame.
     * @param frameOffset at which frame begins, including any headers.
     * @param frameLength of the frame in bytes, including any frame headers that is aligned up to
     *                    {@link io.aeron.logbuffer.FrameDescriptor#FRAME_ALIGNMENT}.
     * @return computed CRC checksum
     * @throws NullPointerException     if {@code null == state} or {@code null == buffer}
     * @throws IllegalArgumentException if {@code frameOffset} and/or {@code frameLength} are out of range
     */
    public static int crc32(final CRC32 state, final ByteBuffer buffer, final int frameOffset, final int frameLength)
    {
        final int position = buffer.position();
        final int limit = buffer.limit();
        buffer.limit(frameOffset + frameLength); // end of the frame plus alignment
        buffer.position(frameOffset + HEADER_LENGTH); // skip the frame header
        state.reset();
        state.update(buffer);
        final int checksum = (int)state.getValue();
        buffer.limit(limit).position(position); // restore original values
        return checksum;
    }
}
