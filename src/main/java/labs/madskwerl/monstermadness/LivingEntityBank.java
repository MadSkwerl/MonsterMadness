package labs.madskwerl.monstermadness;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LivingEntityBank
{
     private static Map<UUID, LivingEntityData> map2Data = new HashMap<>();
     private static Map<LivingEntityData, UUID> map2UUID = new HashMap<>();

     public static void addLivingEntityData(UUID uuid, LivingEntityData livingEntityData)
     {
         map2Data.put(uuid, livingEntityData);
         map2UUID.put(livingEntityData, uuid);
     }

     public static void removeLivingEntityData(UUID uuid)
     {
         map2UUID.remove(map2Data.remove(uuid));
     }

    public static void removeLivingEntityData(LivingEntityData livingEntityData)
    {
        map2Data.remove(map2UUID.remove(livingEntityData));
    }

    public static LivingEntityData getLivingEntityData(String uuid)
    {
        return map2Data.get(UUID.fromString(uuid));
    }

    public static LivingEntityData getLivingEntityData(UUID uuid)
    {
        return map2Data.get(uuid);
    }

    public static UUID getUUID(LivingEntityData livingEntityData)
    {
        return map2UUID.get(livingEntityData);
    }

}
