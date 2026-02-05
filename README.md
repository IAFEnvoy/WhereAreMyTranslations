# Where Are My Translations

This mod can export all untranslated keys into json files.

Image that you want to translate some untranslated keys in a mod. You need to manually type them, with a lot of time
waste and potential typo. Now with this mod, you can make translations more easily.

## What this mod can export

**All export files will be placed in `.minecraft/config/wamt/exports/<mod id>/<language>.json`.**

- Translate keys without any translations (Displayed like `item.xxx.yyy`): These keys can't always export all of them
  due to Minecraft language system limit. You may need to let them display at least once in game.
- Keys missing in specific language (Such as some texts are still in English when using Chinese): These keys will export
  immediately after resource reload.

