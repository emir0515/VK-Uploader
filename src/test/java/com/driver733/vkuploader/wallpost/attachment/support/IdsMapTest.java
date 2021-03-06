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
package com.driver733.vkuploader.wallpost.attachment.support;

import com.driver733.vkuploader.wallpost.attachment.AttachmentAddAudio;
import com.jcabi.aspects.Immutable;
import com.vk.api.sdk.client.AbstractQueryBuilder;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.httpclient.TransportClientCached;
import com.vk.api.sdk.httpclient.TransportClientHttp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * Test for {@link IdsMap}.
 *
 * @since 0.1
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle AnonInnerLengthCheck (500 lines)
 */
@Immutable
@SuppressWarnings("PMD.NonStaticInitializer")
public final class IdsMapTest {

    /**
     * Constant.
     */
    private static final String TOKEN1 = "TOKEN1";

    /**
     * Constant.
     */
    private static final String TOKEN2 = "TOKEN2";

    /**
     * Constant.
     */
    private static final String CACHE = "cache";

    /**
     * Object for testing purposes.
     */
    private final IdsMap queries;

    public IdsMapTest() {
        this.queries = new IdsMap(
            new ArrayList<AbstractQueryBuilder>(2) {
                {
                    addAll(
                        new AttachmentAddAudio(
                        new VkApiClient(
                            new TransportClientHttp()
                        ),
                        new UserActor(
                            0,
                            IdsMapTest.TOKEN1
                        ),
                        0,
                        1,
                        1
                        ).upload()
                    );
                    addAll(
                        new AttachmentAddAudio(
                            new VkApiClient(
                            new TransportClientCached(
                                IdsMapTest.CACHE
                            )
                            ),
                            new UserActor(
                                0,
                                IdsMapTest.TOKEN2
                            ),
                            0,
                            2,
                            1
                        ).upload()
                    );
                }
            }
        );
    }

    @Test
    public void idsMap() throws Exception {
        final Map<Integer, String> expected = new HashMap<>();
        expected.put(0, "1");
        expected.put(1, "2");
        Assertions.assertThat(
            this.queries.value()
        ).isEqualTo(expected);
    }

}
