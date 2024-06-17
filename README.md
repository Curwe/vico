# Vico

## Export lib .aar files
1. Open gradle menu from the right hand side.
2. Expand Vico -> vico. Here you should see gradle projects "compose", "core" and a few more.
3. On both "compose" and "core". Expand ... -> Tasks -> build -> run assemble AND releaseSourceJar
4. Navigate to the build/output folder of both "compose" and "core", there should be 2 .aar files.
5. Rename *core-release.aar* to *vico-core.aar* and *compose-release.aar* to *vico-compose.aar*
6. Move *vico-core.aar* and *vico-compose.aar* to root libs folder in your project.
7. Navigate to the build/libs folder of both "compose" and "core", you could see a (core or compose)-...-sources.jar file take the latest
8. Rename *core-...-.sources.jar* to *vico-core-sources.jar* and *compose-...-.sources.jar* to *vico-compose-source.jar*
9. Move *vico-core-sources.jar* and *vico-compose-source.jar* to root libs folder in your project.

![](https://img.shields.io/maven-central/v/com.patrykandpatrick.vico/core)
![](https://img.shields.io/github/actions/workflow/status/patrykandpatrick/vico/build-debug-apk.yml?branch=master)
![](https://www.codefactor.io/repository/github/patrykandpatrick/vico/badge)
![](https://kotlin-version.aws.icerock.dev/kotlin-version?group=com.patrykandpatrick.vico&name=core)

![](cover.png)

Vico is a light and extensible chart library for Android. It’s compatible with both Jetpack Compose and the view system,
but its two main modules—`compose` and `views`—are independent.

Learn more in [the wiki](https://patrykandpatrick.com/vico/wiki).
