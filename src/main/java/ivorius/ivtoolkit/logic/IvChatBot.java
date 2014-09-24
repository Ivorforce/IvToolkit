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

package ivorius.ivtoolkit.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class IvChatBot
{
    public Random random;

    public List<IvChatLine> sendQueue = new ArrayList<>();

    public IvChatBot(Random rand)
    {
        this.random = rand;
    }

    public String update()
    {
        this.updateIdle();

        if (sendQueue.size() > 0)
        {
            IvChatLine currentLine = sendQueue.get(0);

            currentLine.delay--;

            if (currentLine.delay <= 0)
            {
                sendQueue.remove(0);
                return currentLine.lineString;
            }
        }

        return null;
    }

    public void addMessageToSendQueue(String message, int delay)
    {
        sendQueue.add(new IvChatLine(delay, message));
    }

    public void addMessageToSendQueue(String message, int minDelay, int maxDelay)
    {
        sendQueue.add(new IvChatLine(random.nextInt(maxDelay - minDelay + 1) + minDelay, message));
    }

    public void addMessageToSendQueue(String message)
    {
        this.addMessageToSendQueue(message, 10, 80);
    }

    public void addMessagesToSendQueue(String[] messages)
    {
        for (String s : messages)
        {
            addMessageToSendQueue(s);
        }
    }

    // Overridable

    public abstract void updateIdle();

    public abstract void receiveChatMessage(String message);
}
