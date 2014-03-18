package caveControl;

import java.util.logging.Logger;

import net.minecraft.world.gen.ChunkProviderGenerate;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenCaves;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;


import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.*;
import cpw.mods.fml.common.event.*;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "cavecontrol", name = "Cave Control", version = "0.1")

public class CaveControl {
    public static Logger logger = new Zeno410Logger("CaveControl").logger();
    private Configuration config;
    private CaveControlSettings settings = new CaveControlSettings();
    private CaveControlSettings defaultSettings = new CaveControlSettings();
    private OverworldDataStorage storage;
    
    public int sizeControl() {return settings.sizeControl;}
    public int frequencyControl() {return settings.frequencyControl;}

    static final String sizeControlName = "Size Control";
    static final String frequencyControlName = "Frequency Control";

    static final String caveControlCategory = "Cave Generation Parameters";

    static class CaveControlSettings extends WorldSavedData implements PublicallyCloneable {
        int sizeControl = 40;
        int frequencyControl = 15;

        CaveControlSettings() {super("CaveControlSettings");}

        void set(Configuration config) {
            sizeControl = config.get(caveControlCategory, "Size Control", 40).getInt();
            frequencyControl = config.get(caveControlCategory, "Frequency Control", 15).getInt();
            logger.info("size "+sizeControl + " frequency "+ frequencyControl);
        }

        public void readFromNBT(NBTTagCompound tag) {
            sizeControl = tag.getInteger(sizeControlName);
            frequencyControl = tag.getInteger(frequencyControlName);
            logger.info("loading size "+sizeControl + " frequency "+ frequencyControl);
        }

        public void writeToNBT(NBTTagCompound tag) {
            tag.setInteger(sizeControlName, sizeControl);
            tag.setInteger(frequencyControlName, frequencyControl);
            logger.info("saving size "+sizeControl + " frequency "+ frequencyControl);
        }

        public CaveControlSettings clone() {
            CaveControlSettings clone = new CaveControlSettings();
            clone.sizeControl = this.sizeControl;
            clone.frequencyControl = this.frequencyControl;
            return clone;
        }
    }

    private Accessor<ChunkProviderServer,IChunkProvider> providerFromChunkServer =
            new Accessor<ChunkProviderServer,IChunkProvider>("field_73246_d");

        private Accessor<ChunkProviderGenerate,MapGenBase> mapGenCavesFromGenerator =
            new Accessor<ChunkProviderGenerate,MapGenBase>("field_73226_t");

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        // get user setting; default to MasterCaver's suggestions
        defaultSettings.set(config);

        config.save();
        Acceptor<CaveControlSettings> acceptor = new Acceptor<CaveControlSettings>() {
            public void accept(CaveControlSettings accepted) {
                CaveControl.this.settings = accepted;
            }
        };
        
        storage = new OverworldDataStorage(
                "CaveControlSettings",
                this.settings,
                new Default.Self(this.defaultSettings),
                acceptor);
    }

    @EventHandler
    public void load(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.TERRAIN_GEN_BUS.register(this);
    }


    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        World world = event.world;
        if (world.isRemote) return;
        int dimension = world.provider.dimensionId;
        if (dimension != 0) return; // only set Overworld
        // pull out the Chunk Generator
        // this whole business will crash if things aren't right. Probably the best behavior,
        // although a polite message might be appropriate
        storage.onWorldLoad(event);
        ChunkProviderServer currentServer = (ChunkProviderServer)(world.getChunkProvider());
        IChunkProvider currentProvider = this.providerFromChunkServer.get(currentServer);
        if (currentProvider instanceof ChunkProviderGenerate){
            ChunkProviderGenerate generator = (ChunkProviderGenerate)currentProvider;
            mapGenCavesFromGenerator.setField(
                    generator, new MapGenCaveControl(sizeControl(),frequencyControl()));
        } else {
            throw new RuntimeException("Cave Control found a "+currentProvider.getClass().getName()+
                " when expecting a ChunkProviderGenerate");
        }
    }

    @SubscribeEvent
    public void onWorldSave(WorldEvent.Save event){
        World world = event.world;
        int dimension = world.provider.dimensionId;
        if (dimension != 0) return;

        storage.onWorldSave(event,this.settings);
    }
}