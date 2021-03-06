/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Mikhail Yakushin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.driver733.vkuploader.wallpost.attachment.upload;

import com.jcabi.aspects.Immutable;
import com.vk.api.sdk.client.AbstractQueryBuilder;
import com.vk.api.sdk.client.VkApiClient;
import java.util.Collection;
import org.cactoos.list.StickyList;

/**
 * A fake {@link com.vk.api.sdk.queries.audio.AudioAddQuery}.
 *
 *
 *
 * @since 0.1
 * @checkstyle ProtectedMethodInFinalClassCheck (500 lines)
 */
@Immutable
public final class QueryFakeAudioAdd
    extends AbstractQueryBuilder<QueryFakeAudioAdd, Integer> {

    /**
     * Fake audios.add query.
     * @param client A {@link VkApiClient} that is used for all VK API requests.
     */
    public QueryFakeAudioAdd(final VkApiClient client) {
        super(client, "fake.audio.add", Integer.class);
    }

    @Override
    protected QueryFakeAudioAdd getThis() {
        return this;
    }

    @Override
    protected Collection<String> essentialKeys() {
        return new StickyList<>();
    }

}
