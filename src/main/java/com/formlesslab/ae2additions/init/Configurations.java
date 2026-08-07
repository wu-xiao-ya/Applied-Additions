package com.formlesslab.ae2additions.init;

import com.formlesslab.ae2additions.Reference;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = Reference.MOD_ID, name = Reference.MOD_ID, category = "")
@Config.LangKey("config.ae2additions")
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public final class Configurations {

    @Config.Name("wireless")
    @Config.LangKey("config.ae2additions.wireless")
    public static final Wireless WIRELESS = new Wireless();

    @Config.Name("quantumComputer")
    @Config.LangKey("config.ae2additions.quantum_computer")
    public static final QuantumComputer QUANTUM_COMPUTER = new QuantumComputer();

    @Config.Name("assemblerMatrix")
    @Config.LangKey("config.ae2additions.assembler_matrix")
    public static final AssemblerMatrix ASSEMBLER_MATRIX = new AssemblerMatrix();

    @Config.Name("client")
    @Config.LangKey("config.ae2additions.client")
    public static final Client CLIENT = new Client();

    private Configurations() {
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (Reference.MOD_ID.equals(event.getModID())) {
            ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
        }
    }

    public static final class Wireless {
        @Config.Name("maxRange")
        @Config.LangKey("config.ae2additions.wireless.max_range")
        @Config.Comment("Maximum wireless connector range in blocks")
        @Config.RangeDouble(min = 10.0, max = 10000.0)
        public double maxRange = 1000.0;

        @Config.Name("powerMultiplier")
        @Config.LangKey("config.ae2additions.wireless.power_multiplier")
        @Config.Comment("Power multiplier for wireless connector idle drain")
        @Config.RangeDouble(min = 0.0, max = 100.0)
        public double powerMultiplier = 1.0;
    }

    public static final class QuantumComputer {
        @Config.Name("maxSize")
        @Config.LangKey("config.ae2additions.quantum_computer.max_size")
        @Config.Comment("Maximum outer dimensions of the Quantum Computer multiblock")
        @Config.RangeInt(min = 5, max = 16)
        public int maxSize = 8;

        @Config.Name("acceleratorThreads")
        @Config.LangKey("config.ae2additions.quantum_computer.accelerator_threads")
        @Config.Comment("Threads provided by each Quantum Computer Accelerator")
        @Config.RangeInt(min = 1, max = 16)
        public int acceleratorThreads = 8;

        @Config.Name("maxMultiThreaders")
        @Config.LangKey("config.ae2additions.quantum_computer.max_multi_threaders")
        @Config.Comment("Maximum Multi Threaders per Quantum Computer multiblock")
        @Config.RangeInt(min = 1, max = 2)
        public int maxMultiThreaders = 1;

        @Config.Name("maxDataEntanglers")
        @Config.LangKey("config.ae2additions.quantum_computer.max_data_entanglers")
        @Config.Comment("Maximum Data Entanglers per Quantum Computer multiblock")
        @Config.RangeInt(min = 1, max = 2)
        public int maxDataEntanglers = 1;

        @Config.Name("multiThreaderMultiplier")
        @Config.LangKey("config.ae2additions.quantum_computer.multi_threader_multiplier")
        @Config.Comment("Multiplication factor for Quantum Computer Multi Threaders")
        @Config.RangeInt(min = 2, max = 8)
        public int multiThreaderMultiplier = 4;

        @Config.Name("dataEntanglerMultiplier")
        @Config.LangKey("config.ae2additions.quantum_computer.data_entangler_multiplier")
        @Config.Comment("Multiplication factor for Quantum Computer Data Entanglers")
        @Config.RangeInt(min = 2, max = 8)
        public int dataEntanglerMultiplier = 4;

        @Config.Name("enableEffects")
        @Config.LangKey("config.ae2additions.quantum_computer.enable_effects")
        @Config.Comment("Enable visual effects for Quantum Computer machines")
        public boolean enableEffects = true;
    }

    public static final class AssemblerMatrix {
        @Config.Name("maxSize")
        @Config.LangKey("config.ae2additions.assembler_matrix.max_size")
        @Config.Comment("Maximum outer dimensions of the Assembler Matrix multiblock")
        @Config.RangeInt(min = 3, max = 16)
        public int maxSize = 8;
    }

    public static final class Client {
        @Config.Name("craftingJobSystemNotifications")
        @Config.LangKey("config.ae2additions.client.crafting_job_system_notifications")
        @Config.Comment("Send a system notification when an AE2 crafting job finishes while the game is unfocused")
        public boolean craftingJobSystemNotifications = true;
    }
}
