package labs.madskwerl.monstermadness;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LivingEntityBank
{
   
     private Map<UUID, LivingEntityData> map = new HashMap<>();

     public void addLivingEntityData(UUID uuid, LivingEntityData livingEntityData)
     {
         this.map.put(uuid, livingEntityData);
     }

     public void removeLivingEntityData(UUID uuid)
     {
         this.map.remove(uuid);
     }

    public LivingEntityData getLivingEntityData(String uuid)
    {
        return this.map.get(UUID.fromString(uuid));
    }

    public LivingEntityData getLivingEntityData(UUID uuid)
    {
        return this.map.get(uuid);
    }



}
