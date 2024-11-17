# PineHookPlus

**PineHookPlus** is an advanced example project demonstrating creative and efficient ways to leverage the [Pine Hooking Framework](https://github.com/canyie/pine) for runtime modifications in Android apps. This project focuses on simplifying the integration and customization of hooks, offering a structured and flexible approach to app behavior modification.

---

## 🌟 **Features**

- Automatically loads libraries based on the user's system architecture:
  - **AArch64 (arm64)** and **armeabi-v7a** architectures supported.
  - A11 and UP.
- Libraries are stored in the root directory of the APK under `hook/` for streamlined access.
- Use `config.json` to define hooks in the `hook/` directory.
- Easily specify:
  - **Class Name**: Target class to hook.
  - **Method Name**: Method to override.
  - **Parameter Types**: Types of parameters accepted by the method.
  - **args**: Arguments to pass to the hooked method.
  - **Return Value**: The desired return value of the hooked method.
- No need to write or compile custom code for every method.
- Directly hook into and modify class constructors.
- Moves away from methods like `AppComponentFactory`, `android:name` and uses `ContentProvider`.

---

## 📦 **Project Structure**

```
PineHookPlus/
│
├── app/
│   ├── src/
│   │   ├── hook/            # Hook source directory
|   |   |   ├──hook/
|   |   |   |   ├── libarm.so            # ARM32 library
|   |   |   |   ├── libarm64.so          # ARM64 library
|   |   |   |   ├── config.json          # Hook configuration file
│   │   ├── main/            # Main application source code
│   │   └── ...              # Other modules and configurations
│   ├── build.gradle         # Project-specific Gradle file
│
├── build.gradle             # Root Gradle file
└── settings.gradle
```

---

## 📋 **Example Configurations**

### Hooking a Method

In `config.json`, define the target class and method to hook, along with the parameter types, arguments, and return value as needed:

```json
{
  // Target class to hook
     "com.pinehook.plus.MainActivity": {
        // Method name to hook
        "argMethod": {
            // parameter types of the method
            "paramTypes": [
                "boolean"
            ],
            // return result of the method after it's call
            "after": {
                "result": false
            }
        },
        "beforeOnlyMethod": {
            "paramTypes": [
                "java.lang.String"
            ],
            "before": {
                 // args to pass to the method before it's call
                "args": [
                    "modifiedBefore"
                ]
            }
        },
        "afterOnlyMethod": {
            "paramTypes": [
                "java.lang.String"
            ],
            "after": {
                 // return result of the method after it's call
                "result": "modifiedAfter"
            }
        }
    },
}
```

### Hooking a Constructor

```json
{
  // Constructor hook example
    "com.pinehook.plus.ConstructorClass": {
        // Do not change this value for constructor hook
        "constructor": {
             // parameter types of the constructor
            "paramTypes": [
                "boolean",
                "java.lang.String",
                "boolean",
                "long",
                "long",
                "java.lang.String",
                "java.lang.String",
                "java.lang.String"
            ],
             // args to pass to the constructor before it's call
            "before": {
                "args": [
                    true,
                    "PlayStore",
                    false,
                    0,
                    0,
                    "null",
                    "Yearly",
                    "null"
                ]
            }
        }
    }
}
```
Check [config.json](app/src/hook/hook/config.json) for more.

## Result
<img src="images/result.png" alt="result preview image" widt="200" height="400">

---

## Check out other projects

- [Fine](https://github.com/AbhiTheModder/Fine.git)
- [NewPineExample](https://github.com/AbhiTheModder/NewPineExample.git)

---

## 📜 **License**

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for details.

---

## 🙌 **Acknowledgments**

- [Canyie](https://github.com/canyie) for the incredible [Pine Framework](https://github.com/canyie/pine).
- [Hikki](https://github.com/kyzsuukii) for Initial Ideas, Support and Work.
- [Abhi](https://github.com/AbhiTheModder) for Constructor Hooks and other improvements.

---
