package net.server.ttt.system.utils.corpse;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.server.v1_15_R1.NetworkManager;
import net.minecraft.server.v1_15_R1.Packet;
import net.minecraft.server.v1_15_R1.PacketPlayInUseEntity;
import net.server.ttt.main.Main;
import net.server.ttt.system.utils.events.npc.CorpseAttackEvent;
import net.server.ttt.system.utils.events.npc.CorpseInteractEvent;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.lang.reflect.Field;
import java.util.List;

public class CorpsePacketReader implements Listener {

    Field actionField;
    static Field channelField;


    public CorpsePacketReader() {

        // register listener
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());

        try {
            actionField = PacketPlayInUseEntity.class.getDeclaredField("a");
            actionField.setAccessible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Field field : NetworkManager.class.getDeclaredFields()) {
            if (field.getType().isAssignableFrom(Channel.class)) {
                channelField = field;
                break;
            }
        }
    }

//    public void inject(Player player) {
//
//        try {
//            Channel channel = (Channel) channelField.get(((CraftPlayer) player).getHandle().playerConnection.networkManager);
//            if (channel != null) {
//                channel.pipeline().addAfter("decoder", "SGPacketDecoder", new MessageToMessageDecoder<Packet>() {
//                    @Override
//                    protected void decode(ChannelHandlerContext chc, Packet packet, List<Object> out) throws Exception {
//
//                        if (packet instanceof PacketPlayInUseEntity) {
//                            PacketPlayInUseEntity usePacket = (PacketPlayInUseEntity) packet;
//                            if (usePacket.a() == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT || usePacket.a() == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT_AT) {
//                                int entityId = actionField.getInt(usePacket);
//
//                                //entityId is the id of the entity that has been interacted with.
//
//                                // call NPCInteractEvent
//                                NPCInteractEvent NIEvent = new NPCInteractEvent(player, CorpseManager.getCorpseFromId(entityId), "INTERACT");
//                                Bukkit.getServer().getPluginManager().callEvent(NIEvent);
//
//                            }
//                            else if(usePacket.a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
//                                int entityId = actionField.getInt(usePacket);
//
//                                // call NPCInteractEvent
//                                //NPCAttackEvent NAEvent = new NPCAttackEvent(player, NPC_Manager.getNPCFromId(entityId), damage ,damageType);
//                                //Bukkit.getServer().getPluginManager().callEvent(NAEvent);
//
//                            }
//                        }
//                        out.add(packet);
//                    }
//                });
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static void inject(Player player){
        CraftPlayer cPlayer = (CraftPlayer) player;
        Channel channel = cPlayer.getHandle().playerConnection.networkManager.channel;

        channel.pipeline().addAfter("decoder", "PacketInjector", new MessageToMessageDecoder<Packet<?>>() {
            @Override
            protected void decode(ChannelHandlerContext arg0, Packet<?> packet,List<Object> arg2) throws Exception {
                arg2.add(packet);
                readPacket(packet, player);
            }
        });
    }
    public static void eject(Player player) {

        try {
            Channel channel = (Channel) channelField.get(((CraftPlayer) player).getHandle().playerConnection.networkManager);
            if (channel != null) {
                if (channel.pipeline().get("SGPacketDecoder") != null) {
                    channel.pipeline().remove("SGPacketDecoder");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void readPacket(Packet<?> packet, Player player){
        if(packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInUseEntity")){
            int id = (Integer)getValue(packet, "a");

            Corpse corpse = CorpseManager.getCorpseFromId(id);

            if(getValue(packet, "action").toString().equalsIgnoreCase("ATTACK")){
                // attack action
                CorpseAttackEvent NAEvent = new CorpseAttackEvent(player, corpse);
                Bukkit.getPluginManager().callEvent(NAEvent);
            }
            else if(getValue(packet, "action").toString().equalsIgnoreCase("INTERACT")){
                // interact action
                CorpseInteractEvent NIEvent = new CorpseInteractEvent(player, corpse);
                Bukkit.getPluginManager().callEvent(NIEvent);
            }
        }
    }
    public static void setValue(Object obj,String name,Object value){
        try{
            Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(obj, value);
        }catch(Exception e){}
    }
    public static Object getValue(Object obj,String name){
        try{
            Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(obj);
        }catch(Exception e){}
        return null;
    }

}
