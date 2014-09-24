/*
 * Copyright (c) Lukas Tenbrink, 2014.
 * View the license file at https://github.com/Ivorforce/IvToolkit/blob/master/LICENSE for the full license.
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
