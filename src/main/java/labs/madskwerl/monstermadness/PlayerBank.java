package labs.madskwerl.monstermadness;

import java.util.ArrayList;
import java.util.List;

public class PlayerBank
{
    private List<PlayerData> playerList = new ArrayList<>();

    public void addPlayer(String uid)
    {
        playerList.add(new PlayerData(uid));
    }
    public PlayerData getPlayer(String uid)
    {
        for(PlayerData element: playerList)
        {
            if(element.getUID().equals(uid))
                return element;
        }
        return null;
    }

    public void removePlayer(String uid)
    {
        for(int i = 0; i < this.playerList.size(); i++)
        {
            if(this.playerList.get(i).getUID().equals(uid))
            {
                this.playerList.remove(this.playerList.get(i));
                return;
            }
        }
    }
}
