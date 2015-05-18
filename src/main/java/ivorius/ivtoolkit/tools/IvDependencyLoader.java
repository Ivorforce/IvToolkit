/*
 * Copyright 2015 Lukas Tenbrink
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

import java.util.*;

/**
 * Created by lukas on 14.01.15.
 */
public abstract class IvDependencyLoader
{
    private String importStart;
    private String importEnd;
    private final Map<String, String> cachedResources = new HashMap<>();

    public IvDependencyLoader(String importStart, String importEnd)
    {
        this.importStart = importStart;
        this.importEnd = importEnd;
    }

    public String getImportStart()
    {
        return importStart;
    }

    public void setImportStart(String importStart)
    {
        this.importStart = importStart;
    }

    public String getImportEnd()
    {
        return importEnd;
    }

    public void setImportEnd(String importEnd)
    {
        this.importEnd = importEnd;
    }

    public String loadResource(String k) throws LoadException, RecursionException
    {
        return loadResource(k, new ArrayList<String>());
    }

    private String loadResource(String k, List<String> loading) throws LoadException, RecursionException
    {
        String res = cachedResources.get(k);
        if (res != null)
            return res;

        if (loading.contains(k))
            throw new RecursionException();
        loading.add(k);

        res = loadResourceRaw(k);
        int startIndex, endIndex;

        while ((startIndex = res.indexOf(importStart)) >= 0
                && (endIndex = res.indexOf(importEnd)) >= 0)
        {
            String dep = loadResource(res.substring(startIndex + importStart.length(), endIndex), loading);
            res = res.substring(0, startIndex) + dep + res.substring(endIndex + importEnd.length());
        }

        cachedResources.put(k, res);
        return res;
    }

    protected abstract String loadResourceRaw(String k) throws LoadException;

    public static class RecursionException extends Exception
    {
        public RecursionException()
        {
        }

        public RecursionException(String message)
        {
            super(message);
        }

        public RecursionException(String message, Throwable cause)
        {
            super(message, cause);
        }

        public RecursionException(Throwable cause)
        {
            super(cause);
        }

        public RecursionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
        {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

    public static class LoadException extends Exception
    {
        public LoadException()
        {
        }

        public LoadException(String message)
        {
            super(message);
        }

        public LoadException(String message, Throwable cause)
        {
            super(message, cause);
        }

        public LoadException(Throwable cause)
        {
            super(cause);
        }

        public LoadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
        {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
