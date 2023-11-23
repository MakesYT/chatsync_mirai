package top.ncserver.chatsync.V2;

import org.smartboot.socket.transport.AioSession;

import java.util.HashMap;
import java.util.LinkedList;

public class ClientManager {

    public static HashMap<String, ClientInfo> aioSessionIdToClient = new HashMap<>();
    public static HashMap<Long, LinkedList<ClientInfo>> groupIdToClient = new HashMap<>();
    public static class ClientInfo{
        public  String Name;
        public  AioSession aioSession;
        public  String getName() {
            return Name;
        }

        public  long getGroupId() {
            return GroupId;
        }

        public  void setGroupId(long groupId) {
            GroupId = groupId;
        }

        public  void setName(String name) {
            Name = name;
        }

        public  long GroupId=0L;
    }
}

