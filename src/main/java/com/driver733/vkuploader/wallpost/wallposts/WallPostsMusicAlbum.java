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
package com.driver733.vkuploader.wallpost.wallposts;

import com.driver733.vkuploader.media.AudiosNonProcessed;
import com.driver733.vkuploader.media.MediaAudiosBasic;
import com.driver733.vkuploader.post.SuppressFBWarnings;
import com.driver733.vkuploader.post.UploadServersBasic;
import com.driver733.vkuploader.wallpost.PropsFile;
import com.driver733.vkuploader.wallpost.WallPost;
import com.driver733.vkuploader.wallpost.WallPostMusicAlbum;
import com.driver733.vkuploader.wallpost.attachment.support.AudioStatus;
import com.jcabi.aspects.Cacheable;
import com.jcabi.aspects.Immutable;
import com.jcabi.log.Logger;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.queries.execute.ExecuteBatchQuery;
import com.vk.api.sdk.queries.wall.WallPostQuery;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.cactoos.list.StickyList;

/**
 * Creates {@link WallPost}s
 *  with albums found in the specified directory.
 *  Each wall post has up 9 audios.
 *
 *
 *
 * @since 0.1
 *
 * @checkstyle ClassDataAbstractionCouplingCheck (2 lines)
 */
@Immutable
@SuppressFBWarnings(
    value = "NP_NULL_ON_SOME_PATH",
    justification = "If path exists then NP will not occur."
)
public final class WallPostsMusicAlbum implements WallPosts {

    /**
     * Maximum number of requests in each batch request.
     */
    private static final int BATCH_MAX_REQ = 25;

    /**
     * Number of files in each wall post.
     * 1 (Album image)
     */
    private static final int PHOTOS_IN_POST = 1;

    /**
     * The "cost" of a wall.post request.
     */
    private static final int WALL_POST_REQ = 1;

    /**
     * Maximum number of attachments in a wall post.
     */
    private static final int MAX_ATTACHMENTS = 10;

    /**
     * Audios in each batch request.
     */
    private static final int AUDIOS_IN_REQ =
        WallPostsMusicAlbum.BATCH_MAX_REQ
            - 3 * (WallPostsMusicAlbum.PHOTOS_IN_POST
            + WallPostsMusicAlbum.WALL_POST_REQ
        );

    /**
     * Audios in each Wall Postable.
     */
    private static final int AUDIOS_IN_POST =
        WallPostsMusicAlbum.MAX_ATTACHMENTS
            - WallPostsMusicAlbum.PHOTOS_IN_POST;

    /**
     * Group ID.
     */
    private final int group;

    /**
     * {@link VkApiClient} for all requests.
     */
    private final VkApiClient client;

    /**
     * UserActor on behalf of which all requests will be sent.
     */
    private final UserActor actor;

    /**
     * Album dir.
     */
    private final Path dir;

    /**
     * Upload servers that provide upload URLs for attachmentsFields.
     */
    private final UploadServersBasic servers;

    /**
     * Properties that contain the {@link AudioStatus}es of audios files.
     */
    private final PropsFile properties;

    /**
     * Ctor.
     * @param client The {@link VkApiClient} for all requests.
     * @param actor UserActor on behalf of which all requests will be sent.
     * @param dir Album dir.
     * @param servers Upload servers that provide upload URLs
     *  for attachmentsFields.
     * @param properties Properties that contain the
     *  {@link AudioStatus}es of audios files.
     * @param group Group ID.
     * @checkstyle ParameterNumberCheck (10 lines)
     */
    public WallPostsMusicAlbum(
        final VkApiClient client,
        final UserActor actor,
        final Path dir,
        final UploadServersBasic servers,
        final PropsFile properties,
        final int group
    ) {
        this.client = client;
        this.actor = actor;
        this.dir = dir;
        this.servers = servers;
        this.properties = properties;
        this.group = group;
    }

    // @checkstyle LocalFinalVariableNameCheck (20 lines)
    /**
     * Constructs queries for batch posting wall postsQueries
     * associated with the album.
     * @return ExecuteBatchQuery.
     * @throws Exception If no audios are found.
     */
    public List<ExecuteBatchQuery> postsQueries() throws Exception {
        final List<Path> audios = this.audios();
        final List<ExecuteBatchQuery> queries = new ArrayList<>(
            audios.size()
        );
        int iter = 0;
        Logger.debug(
            this,
            "Analyzing directory '%s'...",
            this.dir
        );
        while (iter < audios.size()) {
            final int to;
            if (audios.size() < iter + WallPostsMusicAlbum.AUDIOS_IN_REQ) {
                to = audios.size() - iter;
            } else {
                to = iter + WallPostsMusicAlbum.AUDIOS_IN_REQ;
            }
            queries.add(
                this.postsBatch(
                    audios.subList(
                        iter,
                        to
                    )
                )
            );
            iter += WallPostsMusicAlbum.AUDIOS_IN_REQ;
        }
        Collections.reverse(queries);
        if (queries.isEmpty()) {
            Logger.debug(
                this,
                "No audios files to upload. Skipping..."
            );
        }
        return queries;
    }

    @Override
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public void updateProperties() throws IOException {
        final List<Path> audios = this.audios();
        for (final Path audio : audios) {
            this.properties.with(
                audio.getFileName().toString(),
                new StringBuilder(
                    this.properties.property(
                        audio
                            .getFileName().toString()
                    )
                ).replace(
                    0,
                    1,
                    AudioStatus.POSTED
                        .toString()
                ).toString()
            );
        }
    }

    /**
     * Finds audios files that have not been posted yet.
     * @return An array of audios {@link File}s.
     * @throws IOException If a certain criteria of
     *  is not fulfilled.
     */
    @Cacheable(forever = true)
    private List<Path> audios() throws IOException {
        return new AudiosNonProcessed(
            new MediaAudiosBasic(
                this.dir
            ),
            this.properties
        ).files();
    }

    // @checkstyle LocalFinalVariableNameCheck (30 lines)
    /**
     * Constructs a query for batch posting wall postsQueries
     * associated with the album.
     * @param audios Audio files to include with the wall postsQueries.
     * @return ExecuteBatchQuery.
     * @throws Exception If the WallPost query cannot be obtained.
     */
    @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops",
        "PMD.OptimizableToArrayCall"
        })
    private ExecuteBatchQuery postsBatch(
        final List<Path> audios
    ) throws
        Exception {
        Logger.info(
            this,
            "Processing directory: '%s'...",
            this.dir
        );
        final List<WallPostQuery> posts = new ArrayList<>(
            audios.size()
        );
        int from = 0;
        while (from < audios.size()) {
            final int to;
            if (audios.size() < from + WallPostsMusicAlbum.AUDIOS_IN_POST) {
                to = audios.size();
            } else {
                to = from + WallPostsMusicAlbum.AUDIOS_IN_POST;
            }
            final WallPostQuery query;
            try {
                query = new WallPostMusicAlbum(
                    this.client,
                    this.actor,
                    new StickyList<>(
                        audios.subList(
                            from,
                            to
                        )
                    ),
                    this.servers,
                    this.properties,
                    this.group
                ).construct();
            } catch (final IOException ex) {
                throw new IOException(
                    "Failed to obtain a WallPost query",
                    ex
                );
            }
            posts.add(query);
            from += WallPostsMusicAlbum.AUDIOS_IN_POST;
        }
        Collections.reverse(posts);
        return new ExecuteBatchQuery(
            this.client,
            this.actor,
            posts.toArray(
                new WallPostQuery[0]
            )
        );
    }

}
