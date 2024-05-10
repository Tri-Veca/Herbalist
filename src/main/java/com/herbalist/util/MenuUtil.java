package com.herbalist.util;

import com.herbalist.Herbalist;
import com.herbalist.init.custom.Kettle.KettleMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

 @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MenuUtil {
    private static final List<MenuType<?>> REGISTRY = new ArrayList<>();
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.CONTAINERS, Herbalist.MOD_ID);

    public static final RegistryObject<MenuType<KettleMenu>> KETTLE_MENU =
            registerMenuType(KettleMenu::new, "kettle_menu");

    private static <T extends AbstractContainerMenu> MenuType<T> register(String registryname, IContainerFactory<T> containerFactory) {
        MenuType<T> menuType = new MenuType<T>(containerFactory);
        menuType.setRegistryName(registryname);
        REGISTRY.add(menuType);
        return menuType;
        //registers MenuType
    }
    private static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>> registerMenuType(IContainerFactory<T> factory,
                                                                                                 String name) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    @SubscribeEvent
    public static void registerContainers(RegistryEvent.Register<MenuType<?>> event) {
        event.getRegistry().registerAll(REGISTRY.toArray(new MenuType[0]));

    }
}
