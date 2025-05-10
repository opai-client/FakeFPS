package net.nekocurit.fake_fps;

import net.nekocurit.fake_fps.modules.FakeFPS;
import today.opai.api.Extension;
import today.opai.api.OpenAPI;
import today.opai.api.annotations.ExtensionInfo;

// Required @ExtensionInfo annotation
@ExtensionInfo(name = "FakeFPS",author = "NekoCurit",version = "1.0")
public class FakeFPSExtension extends Extension {
    public static OpenAPI openAPI;

    @Override
    public void initialize(OpenAPI openAPI) {
        FakeFPSExtension.openAPI = openAPI;

        openAPI.registerFeature(new FakeFPS());
    }
}
