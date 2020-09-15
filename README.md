# :memo: Compose Notes
[![Android CI](https://github.com/yashovardhan99/ComposeNotes/workflows/Android%20CI/badge.svg)](https://github.com/yashovardhan99/ComposeNotes/actions)

Compose Notes is a sample app built with :sparkles: [Jetpack Compose](https://developer.android.com/jetpack/compose).

To try out these sample apps, you need to use the latest Canary version of Android Studio 4.2.

This project showcases:-
- Creating simple UI with Compose
- State Management
- Text Input and focus management
- Integration with architecture components - ViewModel, Room, LiveData
- Basic animations and transitions
- Using MVVM pattern with Room database
- Unit tests and integration tests
- Dependency injection using Hilt

## Status: :construction: Work in progress
The master branch has basic features completed. Other features are still being worked on.

## Features
### :heavy_check_mark: Simple UI with compose
The project contains 2 simple 'screens'. The `NotesList` Composable is responsible for displaying the main notes list. The `NoteEditor` composable displays a simple `BaseTextField` with certain modifications. It even provides a special visual transformation for the 1st line of Text.

### :heavy_check_mark: State Management
State management is done using architecture components such as ViewModel and LiveData. Compose specific `State` is used at certain places. Flow is used for reactive streams of data.

### :heavy_check_mark: Text input and focus management
The `NoteEditor` composable has heavy focus on text input :pencil: and focus management.

### :heavy_check_mark: Integration with architecture components
The app is integrated with architecture components. ViewModel is used for the logic layer of the application. Room is used for data persistence.

### :heavy_check_mark: Basic animations
Basic animations :dancer: are included - Crossfade between screens, swipe to delete colour change and size change animation.

### :heavy_check_mark: Using MVVM pattern with Room
The app is following the recommended MVVM pattern along with using Room. A repository layer is used for linking room to the ViewModel.

### :construction: Unit tests and integration tests
Some basic unit and integration tests are already written. Others will be written soon.

### :new: Dependency injection using Hilt
Room database, Dao, Repository and ViewModel are all injected using Hilt. This will allow simpler testing.

----
This readme is a :construction: work in progress and will be updated :soon:.
## License
    Copyright 2020 Yashovardhan Dhanania

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
