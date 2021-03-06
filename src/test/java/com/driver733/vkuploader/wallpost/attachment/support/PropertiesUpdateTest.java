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

import com.driver733.vkuploader.wallpost.PropsFile;
import com.driver733.vkuploader.wallpost.attachment.AttachmentAddAudio;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.jcabi.aspects.Immutable;
import com.vk.api.sdk.client.AbstractQueryBuilder;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.httpclient.TransportClientCached;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * Test for {@link PropertiesUpdate}.
 *
 *
 *
 * @since 0.1
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (20 lines)
 * @checkstyle LocalFinalVariableNameCheck (200 lines)
 */
@Immutable
@SuppressWarnings("PMD.NonStaticInitializer")
public final class PropertiesUpdateTest {

    /**
     * UploadedMediaId - AudioStatus.Uploaded .
     * AddedMediaId - AudioStatus.Added .
     */
    @Test
    public void test() throws Exception {
        final String fileName = "test.mp3";
        final File file = new File("src/test/resources/temp.properties");
        file.deleteOnExit();
        final PropsFile props = new PropsFile(file);
        final int ownerId = 111;
        final int uploadedMediaId = 222;
        final int addedMediaId = 333;
        props.with(
            fileName,
            String.format(
                "%s_%s_%s",
                AudioStatus.UPLOADED,
                ownerId,
                uploadedMediaId
            )
        );
        new PropertiesUpdate(
            props,
            new IdsMap(
                new ArrayList<AbstractQueryBuilder>(1) {
                    {
                        addAll(
                            new AttachmentAddAudio(
                                new VkApiClient(
                                    new TransportClientCached("")
                                ),
                                new UserActor(0, ""),
                                ownerId,
                                uploadedMediaId,
                                1
                            ).upload()
                        );
                    }
                }
            ).value(),
            new JsonParser().parse(
                new JsonReader(
                    new StringReader(
                        String.format(
                            "[%s]", addedMediaId
                        )
                    )
                )
            ).getAsJsonArray()
        ).run();
        final PropsFile result = new PropsFile(file);
        Assertions.assertThat(
            result.property(fileName)
        ).isEqualTo(
            String.format(
                "%s_%d", AudioStatus.ADDED, addedMediaId
            )
        );
    }

}
