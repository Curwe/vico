# Vico

## Export lib .aar files
1. Open gradle menu from the right hand side.
2. Expand Vico -> vico. Here you should see gradle projects "compose", "core" and a few more.
3. On both "compose" and "core". Expand ... -> Tasks -> build -> run assemble
4. Navigate to the build/output folder of both "compose" and "core", there should be 2 .aar files.
5. Rename *core-release.aar* to *vico-core.aar* and *compose-release.aar* to *vico-compose.aar*
6. Move *vico-core.aar* and *vico-compose.aar* to root libs folder in your project.

![](https://img.shields.io/maven-central/v/com.patrykandpatrick.vico/core)
![](https://img.shields.io/github/actions/workflow/status/patrykandpatrick/vico/build-debug-apk.yml?branch=master)
![](https://www.codefactor.io/repository/github/patrykandpatrick/vico/badge)
![](https://kotlin-version.aws.icerock.dev/kotlin-version?group=com.patrykandpatrick.vico&name=core)

![](cover.png)

Vico is a light and extensible chart library for Android. It’s compatible with both Jetpack Compose and the view system,
but its two main modules—`compose` and `views`—are independent.

There are three primary learning resources for Vico:

- [The wiki](https://patrykandpatrick.com/vico/wiki) includes
  [a setup guide](https://patrykandpatrick.com/vico/wiki/getting-started) and outlines Vico’s features and structure.
- [The sample app](https://github.com/patrykandpatrick/vico/tree/master/sample) demonstrates how to use Vico in Jetpack
  Compose and the view system. The charts showcased at the top of this document are implemented in the sample app.
- [The API reference](https://patrykandpatrick.com/vico/wiki/#api-reference) is a comprehensive manual for Vico’s API.
