# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.
set (CMAKE_CXX_STANDARD 11)
add_library( # Sets the name of the library.
        txzmarvin

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        main/DeviceManager.h
        main/DeviceManager.cpp
        # fm_1388的代码
        fm_1388/fm_fm1388.h
        fm_1388/fm_fm1388.c
        fm_1388/libfm1388/errorcode.h
        fm_1388/libfm1388/libfm1388.c
        fm_1388/libfm1388/libfm1388.h
        fm_1388/libfmrec_1388/fm_wav.c
        fm_1388/libfmrec_1388/fm_wav.h
        fm_1388/libfmrec_1388/libfmrec.c
        fm_1388/libfmrec_1388/libfmrec.h
        # 云知声fvm的代码
        # fvm/capehostx.cpp
        # fvm/capehostx.h
        fvm/command.h
        fvm/cxdish.cpp
        fvm/CxFlash.cpp
        fvm/CxFlash.h
        fvm/getcxFlash.h
        fvm/host_depend.cpp
        # SO库
        native-lib.cpp)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        txzmarvin

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})