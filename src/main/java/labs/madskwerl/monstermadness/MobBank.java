package labs.madskwerl.monstermadness;

import java.util.ArrayList;
import java.util.List;

public class MobBank
{
    private List<MobData> mobList = new ArrayList<>();

    public void addMob(String uid)
    {
        mobList.add(new MobData(uid));
    }

    public MobData getMob(String uid)
    {
        for(MobData element: mobList)
        {
            if(element.getUID().equals(uid))
                return element;
        }
        return null;
    }

    public void removeMob(String uid)
    {
        for(int i = 0; i < this.mobList.size(); i++)
        {
            if(this.mobList.get(i).getUID().equals(uid))
            {
                this.mobList.remove(this.mobList.get(i));
                return;
            }
        }
    }
}
