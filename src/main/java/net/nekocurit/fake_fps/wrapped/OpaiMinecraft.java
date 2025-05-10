package net.nekocurit.fake_fps.wrapped;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class OpaiMinecraft {

    public final Object wrapped;

    public OpaiMinecraft(Object wrapped) {
        this.wrapped = wrapped;
    }

    private static Field cacheF3Text;
    private static Field cacheFps;

    public void setFps(int fps, boolean applyInF3) throws Exception {
        if (cacheFps != null) {
            if (applyInF3) {
                cacheF3Text.set(wrapped, fps + " fps (0 chunk updates) T: inf fast vbo sf reg");
            }
            cacheFps.setInt(wrapped, fps);
            return;
        }

        AtomicReference<Integer> fpsDisplayF3 = new AtomicReference<>();

        Arrays.stream(wrapped.getClass().getFields())
                .filter(field -> field.getType() == String.class)
                .forEach(field -> {
                    try {
                        final String value = field.get(wrapped).toString();
                        // 有两种返回格式
                        // A: 114514 fps (0 chunk updates) T: inf fast vbo (未打开 F3)
                        // B: 114514/1919810 fps (0 chunk updates) T: inf fast vbo (打开 F3 后)
                        if (value.contains("fps")) {
                            cacheF3Text = field;
                            if (value.contains("/")) {
                                fpsDisplayF3.set(Integer.valueOf(value.substring(0, value.indexOf('/')).trim()));
                            } else {
                                fpsDisplayF3.set(Integer.valueOf(value.substring(0, value.indexOf(" fps")).trim()));
                            }

                            if (applyInF3) {
                                cacheF3Text.set(wrapped, fps + " fps (0 chunk updates) T: inf fast vbo sf reg");
                            }
                        }
                    } catch (Exception ignored) { }
                });

        Arrays.stream(wrapped.getClass().getDeclaredFields())
                .filter(field -> field.getType() == int.class)
                .forEach(field -> {
                    try {
                        field.setAccessible(true);
                        if (fpsDisplayF3.get().equals(field.getInt(wrapped))) {
                            field.setInt(wrapped, fps);
                            // 设置缓存 因为反射调用损耗性能
                            cacheFps = field;
                        }
                    } catch (Exception ignores) { }
                });
    }

    public static OpaiMinecraft getMinecraft() throws Exception {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        // 只要 Opai 不进行破坏性变动都能和混淆兼容
        final Class<?> clazz = Class.forName(stackTrace[stackTrace.length - 3].getClassName());

        // 寻找静态方法 Minecraft.getMinecraft();
        // 由于 Opai 进行了混淆
        // 我们只能从参数和返回值特性方面下手
        final Method methodGetMinecraft = Arrays.stream(clazz.getMethods())
                .filter(method -> method.getReturnType() == clazz)
                .filter(method -> Modifier.isStatic(method.getModifiers()))
                .filter(method -> method.getParameterCount() == 0)
                .findFirst()
                .get();


        return new OpaiMinecraft(methodGetMinecraft.invoke(null));
    }
}
