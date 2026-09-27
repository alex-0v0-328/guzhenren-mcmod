# Guzhenren

[简体中文](README.md) | English

A single-player, hardcore, survival-oriented xianxia RPG mod.

> In development ahead of the 1.0.0 release. Gameplay is not final, so this file lists no content.

This is a translation of [README.md](README.md); where the two differ, the Chinese version prevails.

## Requirements

|                  |                                       |
|------------------|---------------------------------------|
| Minecraft        | `1.21.1`                              |
| NeoForge         | `21.1.x`                              |
| Java             | `21`                                  |
| mod id / package | `guzhenren` · `net.alex.guzhenren` |
| Required         | Epic Fight · GeckoLib                 |
| Optional         | JEI · Curios                          |

Exact versions live in `gradle.properties` and `build.gradle`.

## Build and run

`build.gradle` references the Epic Fight and GeckoLib jars in `run/mods/` by file name. That directory is not tracked, so put the matching jars there before the first build (CI downloads them from Modrinth).

```text
gradlew.bat build              # compile, jar
gradlew.bat runClient          # dev client
gradlew.bat runData            # regenerate data
```

Use `./gradlew` on other systems. `runData` writes `src/generated/resources`, which is a source set: regenerate and commit it after any provider change.

## Tests

The automated tests stay on the developer's machine and are not published with the repository; CI compiles, packages and checks that generated data is up to date.

## License

All rights reserved. `LICENSE.txt` is the MIT license inherited from the NeoForge MDK template and **does not cover the mod's code**.
