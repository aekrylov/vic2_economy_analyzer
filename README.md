# Victoria 2 Savegame Economy Analyzer

Based on old [Nashetovich's code](http://oldforum.paradoxplaza.com/forum/showthread.php?715468) with minor changes, 
 so now it works with Java 8 and higher.

## Usage

1. Download [the latest release](https://github.com/aekrylov/vic2_economy_analyzer/releases) (standalone or installer)
2. Run the app (for standalone Windows the exe in root, for standalone Linux the binary in `./bin/`)

Double-click on a table row (or single click on a chart item) opens detailed info window.

Check out project wiki for more info.

## How to build

Use `gradle run` to run the app.

Use `gradle jpackage` to make a runtime image in `build/dist` folder (no JVM required)

## Troubleshooting

- **Can't load large late game file**

    `-Xmx` part in the .bat file is responsible for max memory usage.
    Change `-Xmx1024m` to bigger value, e.g. `-Xmx1500m` (should work for most systems).
    
    Please note that setting this value too big can cause errors on some systems.
 
## To do list:

- [x] Upload jar files
- [x] Test with different environments
- [x] Improve architecture and code quality
- [x] Implement continuous watching for game history
- [ ] Display or export game history 
