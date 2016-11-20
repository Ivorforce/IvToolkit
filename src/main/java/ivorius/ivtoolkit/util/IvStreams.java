/*
 * Copyright 2016 Lukas Tenbrink
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

package ivorius.ivtoolkit.util;

import java.io.ByteArrayOutputStream;
import java.util.Optional;
import java.util.function.*;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Created by lukas on 18.09.16.
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class IvStreams
{
    public static <T> boolean visit(Stream<T> source, Predicate<T> action)
    {
        for (T item : (Iterable<T>) source::iterator)
        {
            if (!action.test(item))
                return false;
        }

        return true;
    }

    public static <T> Stream<T> streamopt(Optional<T> opt) {
        if (opt.isPresent())
            return Stream.of(opt.get());
        else
            return Stream.empty();
    }

    public static <T> Stream<T> flatMapToObj(IntStream stream, IntFunction<Stream<? extends T>> function)
    {
        return stream.mapToObj(function).flatMap(Function.identity());
    }

    public static <T> Stream<T> flatMapToObj(DoubleStream stream, DoubleFunction<Stream<? extends T>> function)
    {
        return stream.mapToObj(function).flatMap(Function.identity());
    }

    public static <T> Stream<T> flatMapToObj(LongStream stream, LongFunction<Stream<? extends T>> function)
    {
        return stream.mapToObj(function).flatMap(Function.identity());
    }

    public static byte[] toByteArray(IntStream stream) {
        return stream.collect(ByteArrayOutputStream::new, (baos, i) -> baos.write((byte) i),
                (baos1, baos2) -> baos1.write(baos2.toByteArray(), 0, baos2.size()))
                .toByteArray();
    }
}
