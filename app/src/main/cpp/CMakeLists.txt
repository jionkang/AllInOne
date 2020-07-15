# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

aux_source_directory(${CMAKE_SOURCE_DIR}/std_handler/ STD_SOURCE)

add_library( # Sets the name of the library.
        native-lib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        native-lib.cpp
        ${STD_SOURCE})

include_directories(${CMAKE_SOURCE_DIR}/std_handler/)



find_library(
        log-lib
        log)



target_link_libraries( # Specifies the target library.
        native-lib

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})