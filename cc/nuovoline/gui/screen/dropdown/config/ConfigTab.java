package cc.nuovoline.gui.screen.dropdown.config;

import cc.nuovoline.Nuovoline;
import cc.nuovoline.commands.impl.ConfigCommand;
import cc.nuovoline.gui.screen.dropdown.Tab;
import cc.nuovoline.modules.visual.HUD;
import net.minecraft.client.gui.Gui;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static cc.nuovoline.utils.fonts.impl.Fonts.SF.SF_20.SF_20;
import static cc.nuovoline.utils.notifications.NotificationType.ERROR;

public class ConfigTab extends Tab {

	private final List<Config> configs = new CopyOnWriteArrayList<>();
	private ConfigTextField newConfigName;
	private Config selectedConfig;

	public ConfigTab(float posX, float posY) {
		super(null, posX, posY);
		
		refreshConfigs();
	}

	public void drawScreen(int mouseX, int mouseY) {
		HUD hud = Nuovoline.getInstance().getModuleManager().getModule(HUD.class);


		int h = 0;
		for (Config config : configs) {
			h+=config.getYPerConfig();
		}
		h+=1;
		if(!opened) h = 0;
		Gui.drawRect(getPosX() - 1, getPosY(), getPosX() + 101, getPosY() + 15 + h, new Color(29, 29, 29, 255).getRGB());
		SF_20.drawString("Configs", getPosX() + 4, getPosY() + 4, 0xffffffff, true);

		if(opened) {
			configs.forEach(config -> config.drawScreen(mouseX, mouseY));
		}
	}

	public void refreshConfigs() {
		configs.clear();
		setSelectedConfig(null);

		Nuovoline.getInstance().getModuleManager().getConfigManager().getConfigs().forEach(config ->
				configs.add(new Config(config, this))
		);

		newConfigName = new ConfigTextField("Config name", this);

		configs.add(newConfigName);

		configs.add(new ConfigButton("Load", this, (configName) -> {
			if(configName.isEmpty()) {
				Nuovoline.getInstance().getNotificationManager().pop("Select a config!", ERROR);
			} else {
				ConfigCommand.loadConfig(Nuovoline.getInstance().getModuleManager().getConfigManager(), configName);
			}
		}));

		configs.add(new ConfigButton("Save", this, (ignored) -> {
			ConfigCommand.saveConfig(Nuovoline.getInstance().getModuleManager().getConfigManager(),getSelectedConfig() != null ? getSelectedConfig().getName() : newConfigName.getValue());
			refreshConfigs();
		}));

		configs.add(new ConfigButton("Delete", this, (configName) -> {
			if(configName.isEmpty()) {
				Nuovoline.getInstance().getNotificationManager().pop("Select a config!", ERROR);
			} else {
				ConfigCommand.deleteConfig(Nuovoline.getInstance().getModuleManager().getConfigManager(), configName);
				refreshConfigs();
			}
		}));
	}

	public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if(isHovered(mouseX, mouseY) && mouseButton == 1) {
			opened = !opened;
		}

		if(opened) {
			configs.forEach(config -> config.mouseClicked(mouseX, mouseY, mouseButton));
		}
	}

	public void keyTyped(char typedChar, int keyCode) {
		configs.forEach(config -> config.keyTyped(typedChar, keyCode));
	}

	public List<Config> getConfigs() {
		return configs;
	}

	public Config getSelectedConfig() {
		return selectedConfig;
	}

	public void setSelectedConfig(Config selectedConfig) {
		this.selectedConfig = selectedConfig;
	}
}
