package org.turbogiants.common.user;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class User extends NettyUser {
    private UserDef userDef;
    private final Lock lock;
    private Channel channelInstance;

    // this is not the best sln but it does what i want
    public static ArrayList<User> userPool = new ArrayList<>();

    public static boolean isUserIDExists(int id){
        boolean isExists = false;
        for(int i = 0; i < userPool.size(); i++){
            if (!userPool.get(i).ch.isActive()){
                userPool.remove(i);
                break;
            }
            if (userPool.get(i).getUserDef() == null)
                continue;
            if (userPool.get(i).getUserDef().getUID() == 0)
                continue;
            if (userPool.get(i).getUserDef().getUID() == id) {
                isExists = true;
                break;
            }
        }
        return isExists;
    }



    // inferior (need to make this multi thread esp if theres a lot of users)
    // we can live with this for now as this is only a prototype
    // then we could just make a limit of how many users per servers (distributed)
    // after the paper is done
    public static User getUserByID(int id){
        for(int i = 0; i < userPool.size(); i++){
            if(!userPool.get(i).ch.isActive()){
                userPool.remove(i);
                break;
            }
            if(userPool.get(i).getUserDef() == null)
                continue;
            if(userPool.get(i).getUserDef().getUID() == 0)
                continue;
            if (userPool.get(i).getUserDef().getUID() == id){
                return userPool.get(i);
            }
        }
        return null;
    }

    public User(Channel channel, byte[] serverIV, byte[] clientIV) {
        super(channel, serverIV, clientIV);
        lock = new ReentrantLock(true);
        userPool.add(this);
    }

    public Lock getLock() {
        return lock;
    }

    public Channel getChannelInstance() {
        return channelInstance;
    }

    public void setChannelInstance(Channel channelInstance) {
        this.channelInstance = channelInstance;
    }

    public void setUserDef(UserDef userDef){
        this.userDef = userDef;
    }

    public UserDef getUserDef(){
        return userDef;
    }
}
