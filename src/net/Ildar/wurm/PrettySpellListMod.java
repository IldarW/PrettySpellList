package net.Ildar.wurm;

import com.wurmonline.client.launcherfx.WurmSettingsFX;
import com.wurmonline.client.launcherfx.controls.BooleanOptionControl;
import com.wurmonline.client.launcherfx.controls.RangeSpinOptionControl;
import com.wurmonline.client.options.BooleanOption;
import com.wurmonline.client.options.Options;
import com.wurmonline.client.options.RangeOption;
import com.wurmonline.client.renderer.gui.HeadsUpDisplay;
import com.wurmonline.client.renderer.gui.PrettySpellListView;
import com.wurmonline.client.renderer.gui.WurmComponent;
import com.wurmonline.shared.constants.PlayerAction;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.Configurable;
import org.gotti.wurmunlimited.modloader.interfaces.Initable;
import org.gotti.wurmunlimited.modloader.interfaces.PreInitable;
import org.gotti.wurmunlimited.modloader.interfaces.WurmClientMod;

import java.io.Console;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrettySpellListMod implements WurmClientMod, Initable, PreInitable, Configurable {
    private static Logger logger = Logger.getLogger(PrettySpellListMod.class.getSimpleName());
    private static BooleanOption showPrettySpellListOption;
    private static RangeOption listRowSizeOption;
    private static HeadsUpDisplay hud;
    private static Properties properties;

    @Override
    public void configure(Properties properties) {
        PrettySpellListMod.properties = properties;
    }

    @Override
    public void preInit() {
        final ClassPool classPool = HookManager.getInstance().getClassPool();
        final CtClass ctWurmConsole;
        try {
            ctWurmConsole = classPool.getCtClass("com.wurmonline.client.console.WurmConsole");
            ctWurmConsole.getMethod("handleDevInput", "(Ljava/lang/String;[Ljava/lang/String;)Z").insertBefore("if (net.Ildar.wurm.PrettySpellListMod.handleInput($1,$2)) return true;");
        } catch (NotFoundException | CannotCompileException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() {
        try {
            HookManager.getInstance().registerHook("com.wurmonline.client.renderer.gui.HeadsUpDisplay", "init", "(II)V", () -> (proxy, method, args) -> {
                method.invoke(proxy, args);
                hud = (HeadsUpDisplay) proxy;
                initOptions();
                return null;
            });
            HookManager.getInstance().registerHook("com.wurmonline.client.renderer.gui.HeadsUpDisplay", "clearAllPopups", "()V", () -> ((proxy, method, args) -> {
                clearPrettySpellLists();
                return method.invoke(proxy, args);
            }));
            HookManager.getInstance().registerHook("com.wurmonline.client.renderer.gui.HeadsUpDisplay", "popupReceived", "(BLjava/util/List;Ljava/lang/String;)V", () -> ((proxy, method, args) -> {
                Object result = method.invoke(proxy, args);
                if (!showPrettySpellListOption.value())
                    return result;
                List<PlayerAction> actions = (List<PlayerAction>) args[1];
                List<SpellAction> spellActions = new ArrayList<>();
                for (PlayerAction playerAction : actions) {
                    SpellAction spellAction = SpellAction.getByActionId(playerAction.getId());
                    if (spellAction != SpellAction.UnknownSpell)
                        spellActions.add(spellAction);
                }
                if (!spellActions.isEmpty()) {
                    Object wurmPopup = ReflectionUtil.getPrivateField(hud, ReflectionUtil.getField(HeadsUpDisplay.class, "lastPopup"));
                    int x = ReflectionUtil.getPrivateField(wurmPopup, ReflectionUtil.getField(wurmPopup.getClass(), "x"));
                    int y = ReflectionUtil.getPrivateField(wurmPopup, ReflectionUtil.getField(wurmPopup.getClass(), "y"));
                    long[] popupTargets = ReflectionUtil.getPrivateField(hud, ReflectionUtil.getField(HeadsUpDisplay.class, "popupTargets"));
                    int columns = Math.min(PrettySpellListMod.listRowSizeOption.value(), spellActions.size());
                    PrettySpellListView spellListView = new PrettySpellListView(spellActions, x, y, columns, spellAction -> {
                        if (spellAction != SpellAction.UnknownSpell)
                            hud.sendAction(new PlayerAction(spellAction.getActionId(), PlayerAction.ANYTHING), popupTargets);
                        clearPrettySpellLists();
                    });
                    Method showComponentMethod = ReflectionUtil.getMethod(HeadsUpDisplay.class, "showComponent");
                    showComponentMethod.setAccessible(true);
                    showComponentMethod.invoke(hud, spellListView);
                }
                return result;
            }));
            logger.info("initialized");
        } catch (Exception e) {
            if (PrettySpellListMod.logger != null) {
                PrettySpellListMod.logger.log(Level.SEVERE, "Error loading mod", e);
                PrettySpellListMod.logger.log(Level.SEVERE, e.getMessage());
            }
        }
    }

    public static void clearPrettySpellLists() {
        List hudComponents;
        try {
            hudComponents = ReflectionUtil.getPrivateField(hud, ReflectionUtil.getField(HeadsUpDisplay.class, "components"));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        for(Object wurmComponent : new ArrayList<>(hudComponents)) {
            if (wurmComponent instanceof PrettySpellListView)
                hudComponents.remove(wurmComponent);
        }
    }

    private void initOptions() {
        try {
            Field dynamicOptionsField = ReflectionUtil.getField(Options.class, "dynamicOptions");
            dynamicOptionsField.setAccessible(true);
            Constructor<?> booleanOptionConstructor = BooleanOption.class.getDeclaredConstructors()[0];
            booleanOptionConstructor.setAccessible(true);
            Constructor<?> rangeOptionConstructor = RangeOption.class.getDeclaredConstructors()[0];
            rangeOptionConstructor.setAccessible(true);
            showPrettySpellListOption = (BooleanOption) booleanOptionConstructor.newInstance(Option.ShowPrettySpellList.name(), dynamicOptionsField.get(null), true);
            showPrettySpellListOption.set(properties.getProperty(Option.ShowPrettySpellList.name(), "true").equals("true"));
            listRowSizeOption = (RangeOption) rangeOptionConstructor.newInstance(Option.ListRowSize.name(), dynamicOptionsField.get(null), 5, 1, 20);
            listRowSizeOption.set(Integer.parseInt(properties.getProperty(Option.ListRowSize.name(), "5")));
            Field settingsInstanceField = ReflectionUtil.getField(WurmSettingsFX.class, "instance");
            settingsInstanceField.setAccessible(true);
            WurmSettingsFX wurmSettingsFX = (WurmSettingsFX) settingsInstanceField.get(null);
            wurmSettingsFX.addSpacer(Option.section);
            wurmSettingsFX.addOption(Option.section, new BooleanOptionControl(showPrettySpellListOption, Option.ShowPrettySpellList.description, Option.ShowPrettySpellList.tooltip, true));
            wurmSettingsFX.addOption(Option.section, new RangeSpinOptionControl(listRowSizeOption, Option.ListRowSize.description, Option.ListRowSize.tooltip, true));
        } catch (Exception e) {
            hud.consoleOutput("Error on option initialization");
            e.printStackTrace();
        }
    }


    public static boolean handleInput(final String cmd, final String[] data) {
        switch (cmd) {
            case "printoptions":
                hud.consoleOutput("Show pretty spell list - " + showPrettySpellListOption.value() + ", List row size - " + listRowSizeOption.value());
                return true;
            default:
                return false;
        }
    }

}
