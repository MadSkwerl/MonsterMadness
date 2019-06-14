package labs.madskwerl.monstermadness;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LivingEntityBank
{
   
     private static Map<UUID, LivingEntityData> map = new HashMap<>();

     public static void addLivingEntityData(UUID uuid, LivingEntityData livingEntityData)
     {
         LivingEntityBank.map.put(uuid, livingEntityData);
     }

     public static void removeLivingEntityData(UUID uuid)
     {
         LivingEntityBank.map.remove(uuid);
     }

    public static LivingEntityData getLivingEntityData(String uuid)
    {
        return LivingEntityBank.map.get(UUID.fromString(uuid));
    }

    public static LivingEntityData getLivingEntityData(UUID uuid)
    {
        return LivingEntityBank.map.get(uuid);
    }



}
