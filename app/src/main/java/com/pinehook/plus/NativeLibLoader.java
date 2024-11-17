package com.pinehook.plus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Objects;
import org.json.JSONObject;

public class NativeLibLoader {
    @SuppressLint("UnsafeDynamicallyLoadedCode")
    public static void loadNativeLib(Context context, String libName) {
        String arch = getArch();
        try {
            File targetFile = new File(context.getFilesDir(), libName);

            if (!targetFile.exists()) {
                String resourcePath = "hook/" + arch;
                InputStream inputStream = context.getClassLoader().getResourceAsStream(resourcePath);
                if (inputStream == null) {
                    throw new RuntimeException("Library file not found: " + resourcePath);
                }

                if (!Objects.requireNonNull(targetFile.getParentFile()).mkdirs() && !targetFile.getParentFile().exists()) {
                    throw new RuntimeException("Failed to create directories for library file");
                }

                try (InputStream input = inputStream; FileOutputStream output = new FileOutputStream(targetFile)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = input.read(buffer)) > 0) {
                        output.write(buffer, 0, length);
                    }
                    output.flush();
                }

                if (!targetFile.exists()) {
                    throw new RuntimeException("Failed to create library file");
                }

                if (!targetFile.setExecutable(true, true)) {
                    throw new RuntimeException("Failed to set executable permission on library file");
                }
            }

            System.load(targetFile.getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load native library: " + libName, e);
        }
    }

    public static Map<String, Map<String, Object>> loadConfig(Context context) {
        try {
            String configPath = "hook/config.json";
            InputStream inputStream = context.getClassLoader().getResourceAsStream(configPath);
            String jsonString = getString(inputStream, configPath);
            JSONObject jsonObject = new JSONObject(jsonString);
            return JsonParser.parseConfig(jsonObject);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config file", e);
        }
    }

    @NonNull
    private static String getString(InputStream inputStream, String configPath) throws IOException {
        if (inputStream == null) {
            throw new RuntimeException("Config file not found: " + configPath);
        }
        InputStreamReader reader = new InputStreamReader(inputStream);
        StringBuilder jsonBuilder = new StringBuilder();
        char[] buffer = new char[1024];
        int length;
        while ((length = reader.read(buffer)) > 0) {
            jsonBuilder.append(buffer, 0, length);
        }
        return jsonBuilder.toString();
    }

    static String getArch() {
        String[] supportedAbis;
        supportedAbis = Build.SUPPORTED_ABIS;

        for (String abi : supportedAbis) {
            if (abi.contains("arm64-v8a")) {
                return "libpine64";
            } else if (abi.contains("armeabi-v7a")) {
                return "libpine32";
            }
        }

        throw new RuntimeException("Unsupported architecture");
    }
}
