package top.ncserver.chatsync.V2;

import org.smartboot.socket.transport.AioSession;

import java.util.HashMap;
import java.util.LinkedList;

public class ClientManager {
    public static LinkedList<AioSession> clients = new LinkedList<>();
    public static HashMap<String, String> clientName = new HashMap<>();
}

