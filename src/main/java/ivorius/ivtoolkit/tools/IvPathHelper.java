/*
 * Copyright 2014 Lukas Tenbrink
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ivorius.ivtoolkit.tools;

import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Created by lukas on 14.10.14.
 */
public class IvPathHelper
{
    public static Path resourceToPath(URL resource) throws IOException, URISyntaxException
    {
        Objects.requireNonNull(resource, "Resource URL cannot be null");
        URI uri = resource.toURI();

        String scheme = uri.getScheme();
        if (scheme.equals("file"))
        {
            return Paths.get(uri);
        }

        if (!scheme.equals("jar"))
        {
            throw new IllegalArgumentException("Cannot convert to Path: " + uri);
        }

        String s = uri.toString();
        int separator = s.indexOf("!/");
        String entryName = s.substring(separator + 2);
        URI fileURI = URI.create(s.substring(0, separator));

        try
        {
            FileSystem fs = FileSystems.getFileSystem(fileURI);
            if (fs.isOpen())
                return fs.getPath(entryName);
        }
        catch (FileSystemNotFoundException ignored) {}

        FileSystem fs = FileSystems.newFileSystem(fileURI, Collections.<String, Object>emptyMap());
        return fs.getPath(entryName);
    }

    public static Path pathFromResourceLocation(ResourceLocation resourceLocation) throws URISyntaxException, IOException
    {
        URL resource = IvPathHelper.class.getResource("/assets/" + resourceLocation.getResourceDomain() + "/" + resourceLocation.getResourcePath());
        return resource != null ? resourceToPath(resource.toURI().toURL()) : null;
    }

    public static List<Path> listFilesRecursively(Path dir, final DirectoryStream.Filter<Path> filter, final boolean recursive) throws IOException
    {
        final List<Path> files = new ArrayList<>();
        Files.walkFileTree(dir, new SimpleFileVisitor<Path>()
        {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
            {
                if (filter.accept(file))
                    files.add(file);

                return recursive ? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
            }
        });
        return files;
    }
}
