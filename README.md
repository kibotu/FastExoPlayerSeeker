[![Donation](https://img.shields.io/badge/buy%20me%20a%20beer-brightgreen.svg)](https://www.paypal.me/janrabe/5) [![About Jan Rabe](https://img.shields.io/badge/about-me-green.svg)](https://about.me/janrabe)
# FastExoSeeker [![](https://jitpack.io/v/kibotu/FastExoSeeker.svg)](https://jitpack.io/#kibotu/FastExoSeeker) [![](https://jitpack.io/v/kibotu/FastExoSeeker/month.svg)](https://jitpack.io/#kibotu/FastExoSeeker) [![Javadoc](https://img.shields.io/badge/javadoc-SNAPSHOT-green.svg)](https://jitpack.io/com/github/kibotu/FastExoSeeker/master-SNAPSHOT/javadoc/index.html) [![Build Status](https://travis-ci.org/kibotu/FastExoSeeker.svg?branch=master)](https://travis-ci.org/kibotu/FastExoSeeker) [![API](https://img.shields.io/badge/API-16%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=15)  [![Gradle Version](https://img.shields.io/badge/gradle-5.4.1-green.svg)](https://docs.gradle.org/current/release-notes) [![kotlin](https://img.shields.io/badge/kotlin-1.3.31-green.svg)](https://kotlinlang.org/) [![Licence](https://img.shields.io/badge/licence-Apache%202-blue.svg)](https://raw.githubusercontent.com/kibotu/FastExoSeeker/master/LICENSE) [![androidx](https://img.shields.io/badge/androidx-brightgreen.svg)](https://developer.android.com/topic/libraries/support-library/refactor)

## Introduction

Adds fast seeking for exoplayer. (Note: it also depends on the amount of video encoding, mainly IDR Frames)

![demo](demo.gif)

## How to install

    repositories {
        maven {
            url "https://jitpack.io"
        }
    }

    dependencies {
        implementation 'com.github.kibotu:StreamingFastExoSeeker:-SNAPSHOT'
    }

## How to use

### Create Emitter

    val emitter = SeekPositionEmitter()
    emitter.seekFast(newSeekPosition)

### Create Observable

    simpleExoPlayer.seekWhenReady(emitter)
        .subscribe({
            logv("seekTo=${it.first} isSeeking=${it.second}")
        }, { loge("${it.message}") })
        .addTo(subscriptions) // don't forget to dispose when you're done!

## How to build

    graldew clean build

### CI

    gradlew clean assembleRelease test javadoc

#### Build Requirements

- JDK8
- Android Build Tools 28.0.3
- Android SDK 28

## Contributors

- [Jan Rabe](jan.rabe@kibotu.net)

### License

<pre>
Copyright 2019 Jan Rabe

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
</pre>
