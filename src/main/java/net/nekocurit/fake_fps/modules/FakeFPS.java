package net.nekocurit.fake_fps.modules;

import net.nekocurit.fake_fps.utils.RandomUtils;
import net.nekocurit.fake_fps.wrapped.OpaiMinecraft;
import today.opai.api.enums.EnumModuleCategory;
import today.opai.api.events.EventRender2D;
import today.opai.api.features.ExtensionModule;
import today.opai.api.interfaces.EventHandler;
import today.opai.api.interfaces.modules.values.BooleanValue;
import today.opai.api.interfaces.modules.values.NumberValue;

import static net.nekocurit.fake_fps.FakeFPSExtension.openAPI;

public class FakeFPS extends ExtensionModule implements EventHandler {
    public FakeFPS() {
        super("Fake FPS", "Spoof client fps.", EnumModuleCategory.MISC);
        setEventHandler(this);
        addValues(minValue, maxValue, applyInF3Value);
    }


    private final NumberValue minValue = openAPI.getValueManager().createDouble("Min",300,0,3000,1);
    private final NumberValue maxValue = openAPI.getValueManager().createDouble("Max",320,0,3000,1);

    private final BooleanValue applyInF3Value = openAPI.getValueManager().createBoolean("ApplyInF3", false);

    public int spoofFps = 0;
    public int spoofTick = 0;

    public void updateSpoofFps() {
        spoofFps = RandomUtils.random(minValue.getValue().intValue(), maxValue.getValue().intValue());
        spoofTick = 0;
    }


    @Override
    public void onRender2D(EventRender2D event) {
        try {
            final OpaiMinecraft mc = OpaiMinecraft.getMinecraft();
            mc.setFps(spoofFps, applyInF3Value.getValue());
        } catch (Exception ignored) { }
    }

    @Override
    public void onTick() {
        spoofTick++;
        if (spoofTick > 20) updateSpoofFps();
    }
}
