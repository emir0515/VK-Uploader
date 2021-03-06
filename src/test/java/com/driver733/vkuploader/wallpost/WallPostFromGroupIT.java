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
package com.driver733.vkuploader.wallpost;

import com.driver733.vkuploader.wallpost.support.VkCredentials;
import com.jcabi.aspects.Immutable;
import com.vk.api.sdk.objects.wall.WallpostFull;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.jcip.annotations.NotThreadSafe;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

/**
 * IT for {@link WallPostFromGroup}.
 *
 * @checkstyle JavadocMethodCheck (500 lines)
 * @since 0.2
 */
@Immutable
@NotThreadSafe
public final class WallPostFromGroupIT {

    /**
     * VK user, group and auth token.
     */
    @Rule
    public final VkCredentials credentials =
        new VkCredentials();

    @After
    public void delay() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
    }

    @Test
    public void test() throws Exception {
        final int post = new WallPostFromGroup(
            new WallPostWithOwnerId(
                new WallPostWithMessage(
                    new WallPostBase(
                        this.credentials.client(),
                        this.credentials.actor()
                    ),
                    "Test message."
                ),
                -this.credentials.group()
            )
        ).construct()
            .execute()
            .getPostId();
        final List<WallpostFull> result = this.credentials.client()
            .wall().getById(
                this.credentials.actor(),
                String.format("%d_%d", -this.credentials.group(), post)
            ).execute();
        Assertions.assertThat(result.get(0).getOwnerId()).isEqualTo(-this.credentials.group());
        this.credentials.client()
            .wall()
            .delete(
                this.credentials.actor()
            ).ownerId(
                -this.credentials.group()
            ).postId(post)
            .execute();
    }

}
