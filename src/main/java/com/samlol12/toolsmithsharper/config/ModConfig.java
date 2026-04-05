package com.samlol12.toolsmithsharper.config;

import net.fabricmc.loader.api.FabricLoader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class ModConfig {
    public static int MAX_SHARPER_BASE_USES = 32;
    public static int MAX_COATING_BASE_USES = 10;
    public static double DAMAGE_MULTIPLIER = 0.25;
    public static double SPEED_BOOST = 2.0;
    public static int XP_COST = 1;
    public static double REPAIR_PERCENTAGE = 0.10;
    public static int MAX_WHETSTONE_USES = 3;
    public static int WHETSTONE_USE_TIME = 40;

    private static File getConfigFile() {
        return FabricLoader.getInstance().getConfigDir().resolve("toolsmithsharper.properties").toFile();
    }

    public static void loadConfig() {
        try {
            File file = getConfigFile();
            if (file.exists()) {
                Properties props = new Properties();
                props.load(new FileInputStream(file));
                MAX_SHARPER_BASE_USES = Integer.parseInt(props.getProperty("maxUses", "32"));
                MAX_COATING_BASE_USES = Integer.parseInt(props.getProperty("maxCoatingUses", "10"));
                MAX_WHETSTONE_USES = Integer.parseInt(props.getProperty("maxWheatstoneUses", "3"));
                WHETSTONE_USE_TIME = Integer.parseInt(props.getProperty("whetstoneUseTime", "40"));
                DAMAGE_MULTIPLIER = Double.parseDouble(props.getProperty("damageMultiplier", "0.25"));
                SPEED_BOOST = Double.parseDouble(props.getProperty("speedBoost", "2.0"));
                XP_COST = Integer.parseInt(props.getProperty("xpCost", "1"));
                REPAIR_PERCENTAGE = Double.parseDouble(props.getProperty("repairPercentage", "0.10"));
            } else {
                saveConfig();
            }
        } catch (Exception e) {
            System.out.println("Error loading Toolsmith Sharper config");
        }
    }

    public static void saveConfig() {
        try {
            Properties props = new Properties();
            props.setProperty("maxUses", String.valueOf(MAX_SHARPER_BASE_USES));
            props.setProperty("maxCoatingUses", String.valueOf(MAX_COATING_BASE_USES));
            props.setProperty("maxWheatstoneUses", String.valueOf(MAX_WHETSTONE_USES));
            props.setProperty("whetstoneUseTime", String.valueOf(WHETSTONE_USE_TIME));
            props.setProperty("damageMultiplier", String.valueOf(DAMAGE_MULTIPLIER));
            props.setProperty("speedBoost", String.valueOf(SPEED_BOOST));
            props.setProperty("xpCost", String.valueOf(XP_COST));
            props.setProperty("repairPercentage", String.valueOf(REPAIR_PERCENTAGE));
            props.store(new FileOutputStream(getConfigFile()), "Configuration of Toolsmith Sharper");
        } catch (Exception e) {
            System.out.println("Error saving Toolsmith Sharper config");
        }
    }
}