package com.pinehook.plus;

import top.canyie.pine.Pine;
import top.canyie.pine.callback.MethodReplacement;
import top.canyie.pine.callback.MethodHook;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import android.util.Log;

public class Hook {
    private static final String TAG = "Hook";

    public static void doHook(Map<String, Map<String, Object>> config) {
        try {
            for (Map.Entry<String, Map<String, Object>> classEntry : config.entrySet()) {
                Class<?> clazz = Class.forName(classEntry.getKey());
                for (Map.Entry<String, Object> methodEntry : classEntry.getValue().entrySet()) {
                    String methodName = methodEntry.getKey();
                    Map<String, Object> methodDetails = (Map<String, Object>) methodEntry.getValue();

                    if (Objects.equals(methodName, "constructor")) {
                        Constructor<?> constructor = getConstructor(clazz, methodName, methodDetails);

                        Log.d(TAG, "Hooking constructor in class: " + clazz.getName());

                        Pine.hook(constructor, new MethodHook() {
                            @Override
                            public void beforeCall(Pine.CallFrame callFrame) throws Throwable {
                                Log.d(TAG, "Before constructor call: " + clazz.getName());
                                handleBeforeCall(callFrame, methodDetails);
                            }

                            @Override
                            public void afterCall(Pine.CallFrame callFrame) throws Throwable {
                                Log.d(TAG, "After constructor call: " + clazz.getName());
                                handleAfterCall(callFrame, methodDetails);
                            }
                        });

                    } else  if (!Objects.equals(methodName, "constructor")){
                        Method method = getMethod(clazz, methodName, methodDetails);

                        Log.d(TAG, "Hooking method: " + methodName + " in class: " + clazz.getName());

                        if (methodDetails.containsKey("before") || methodDetails.containsKey("after")) {
                            Pine.hook(method, new MethodHook() {
                                @Override
                                public void beforeCall(Pine.CallFrame callFrame) throws Throwable {
                                    Log.d(TAG, "Before call: " + methodName);
                                    handleBeforeCall(callFrame, methodDetails);
                                }

                                @Override
                                public void afterCall(Pine.CallFrame callFrame) throws Throwable {
                                    Log.d(TAG, "After call: " + methodName);
                                    handleAfterCall(callFrame, methodDetails);
                                }
                            });
                        } else {
                            Pine.hook(method, MethodReplacement.returnConstant(methodDetails.get("result")));
                        }
                    }
                }
            }
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            Log.e(TAG, "Error during hooking", e);
            throw new RuntimeException(e);
        }
    }

    private static Method getMethod(Class<?> clazz, String methodName, Map<String, Object> methodDetails) throws NoSuchMethodException {
        if (methodDetails.containsKey("paramTypes")) {
            List<String> paramTypeNames = (List<String>) methodDetails.get("paramTypes");
            Class<?>[] paramTypes = paramTypeNames.stream()
                    .map(Hook::getClassForName)
                    .toArray(Class<?>[]::new);
            return clazz.getDeclaredMethod(methodName, paramTypes);
        } else {
            return clazz.getDeclaredMethod(methodName);
        }
    }

    private static Constructor<?> getConstructor(Class<?> clazz, String methodName, Map<String, Object> methodDetails) throws NoSuchMethodException {
        if (methodDetails.containsKey("paramTypes")) {
            List<String> paramTypeNames = (List<String>) methodDetails.get("paramTypes");
            Class<?>[] paramTypes = paramTypeNames.stream()
                    .map(Hook::getClassForName)
                    .toArray(Class<?>[]::new);
            return clazz.getDeclaredConstructor(paramTypes);
        } else {
            return clazz.getDeclaredConstructor();
        }
    }

    private static Class<?> getClassForName(String className) {
        try {
            switch (className) {
                case "int":
                    return int.class;
                case "boolean":
                    return boolean.class;
                case "float":
                    return float.class;
                case "double":
                    return double.class;
                case "long":
                    return long.class;
                case "short":
                    return short.class;
                case "byte":
                    return byte.class;
                case "char":
                    return char.class;
                default:
                    return Class.forName(className);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void handleBeforeCall(Pine.CallFrame callFrame, Map<String, Object> methodDetails) {
        if (methodDetails.containsKey("before")) {
            Map<String, Object> beforeConfig = (Map<String, Object>) methodDetails.get("before");
            if (beforeConfig.containsKey("args")) {
                List<Object> args = (List<Object>) beforeConfig.get("args");
                for (int i = 0; i < Objects.requireNonNull(args).size(); i++) {
                    callFrame.args[i] = args.get(i);
                }
            }
            if (beforeConfig.containsKey("result")) {
                callFrame.setResultIfNoException(beforeConfig.get("result"));
            }
        }
    }

    private static void handleAfterCall(Pine.CallFrame callFrame, Map<String, Object> methodDetails) {
        if (methodDetails.containsKey("after")) {
            Map<String, Object> afterConfig = (Map<String, Object>) methodDetails.get("after");
            if (afterConfig.containsKey("result")) {
                callFrame.setResultIfNoException(afterConfig.get("result"));
            }
        }
    }
}
