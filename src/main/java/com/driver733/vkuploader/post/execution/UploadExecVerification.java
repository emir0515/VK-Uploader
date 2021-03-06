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
package com.driver733.vkuploader.post.execution;

import com.driver733.vkuploader.wallpost.wallposts.WallPosts;
import com.jcabi.aspects.Immutable;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.queries.execute.ExecuteBatchQuery;
import java.io.IOException;
import java.util.List;

/**
 * Executes the queries and
 *  notifies {@link WallPosts} to cache the changes.
 *
 * @since 0.1
 */
@Immutable
public final class UploadExecVerification implements UploadExec {

    /**
     * {@link WallPosts} to execute and save to properties.
     */
    private final WallPosts posts;

    /**
     * Ctor.
     * @param posts The {@link WallPosts} to execute and save to properties.
     */
    public UploadExecVerification(final WallPosts posts) {
        this.posts = posts;
    }

    @Override
    public void execute() throws Exception {
        final List<ExecuteBatchQuery> queries;
        try {
            queries = this.posts.postsQueries();
        } catch (final IOException ex) {
            throw new IOException("Failed to obtain queries.", ex);
        }
        for (final ExecuteBatchQuery query : queries) {
            try {
                query.execute();
            } catch (final ApiException | ClientException ex) {
                throw new IOException("Failed to execute query.", ex);
            }
            try {
                this.posts.updateProperties();
            } catch (final IOException ex) {
                throw new IOException("Failed to update properties.", ex);
            }
        }
    }

}
