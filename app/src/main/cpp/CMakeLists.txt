# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html
# Sets the minimum version of CMake required to build the native library.
cmake_minimum_required(VERSION 3.4.1)
project(CuraEngine)
list(APPEND CMAKE_MODULE_PATH ${CMAKE_CURRENT_SOURCE_DIR}/cmake)
#For reading image files.
find_package(Stb REQUIRED)
include_directories(${Stb_INCLUDE_DIRS})
option(USE_SYSTEM_LIBS "Use the system libraries if available" OFF)
if(USE_SYSTEM_LIBS)
    find_package(RapidJSON CONFIG REQUIRED)
    find_package(Polyclipping REQUIRED)
endif()
# convert build type to upper case letters
if(CMAKE_BUILD_TYPE)
    string(TOUPPER ${CMAKE_BUILD_TYPE} CMAKE_BUILD_TYPE_UPPER)
endif()
if(CMAKE_BUILD_TYPE_UPPER MATCHES "DEBUG")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${CMAKE_CXX_FLAGS_DEBUG_INIT}")
else()
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} ${CMAKE_CXX_FLAGS_RELEASE_INIT}")
endif()
set(CMAKE_CXX_STANDARD 11)
if(APPLE AND CMAKE_CXX_COMPILER_ID MATCHES "Clang")
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -stdlib=libc++")
endif()
if(NOT DEFINED LIB_SUFFIX)
    set(LIB_SUFFIX "")
endif()
set(CMAKE_INSTALL_RPATH "${CMAKE_INSTALL_PREFIX}/lib${LIB_SUFFIX}")
set(CURA_ENGINE_VERSION "master" CACHE STRING "Version name of Cura")
option(BUILD_TESTS OFF)
# Add a compiler flag to check the output for insane values if we are in debug mode.
if(CMAKE_BUILD_TYPE_UPPER MATCHES "DEBUG" OR CMAKE_BUILD_TYPE_UPPER MATCHES "RELWITHDEBINFO")
    message(STATUS "Building debug release of CuraEngine.")
    if (NOT MSVC)
        set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wextra -O0 -g -fno-omit-frame-pointer")
    endif()
    add_definitions(-DASSERT_INSANE_OUTPUT)
    add_definitions(-DUSE_CPU_TIME)
    add_definitions(-DDEBUG)
endif()
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall") # Add warnings
option (ENABLE_MORE_COMPILER_OPTIMIZATION_FLAGS
        "Enable more optimization flags" ON)
include_directories(${Polyclipping_INCLUDE_DIRS} ${CMAKE_BINARY_DIR} ${RAPIDJSON_INCLUDE_DIRS})
include_directories(${CMAKE_CURRENT_BINARY_DIR} libs libs/clipper)
add_library(clipper SHARED libs/clipper/clipper.cpp)
set(engine_SRCS # Except main.cpp.
        src/Application.cpp
        src/bridge.cpp
        src/ConicalOverhang.cpp
        src/ExtruderTrain.cpp
        src/FffGcodeWriter.cpp
        src/FffPolygonGenerator.cpp
        src/FffProcessor.cpp
        src/gcodeExport.cpp
        src/GCodePathConfig.cpp
        src/infill.cpp
        src/InsetOrderOptimizer.cpp
        src/layerPart.cpp
        src/LayerPlan.cpp
        src/LayerPlanBuffer.cpp
        src/MergeInfillLines.cpp
        src/mesh.cpp
        src/MeshGroup.cpp
        src/Mold.cpp
        src/multiVolumes.cpp
        src/pathOrderOptimizer.cpp
        src/Preheat.cpp
        src/PrimeTower.cpp
        src/raft.cpp
        src/Scene.cpp
        src/skin.cpp
        src/SkirtBrim.cpp
        src/SupportInfillPart.cpp
        src/Slice.cpp
        src/sliceDataStorage.cpp
        src/slicer.cpp
        src/support.cpp
        src/timeEstimate.cpp
        src/TopSurface.cpp
        src/TreeSupport.cpp
        src/WallsComputation.cpp
        src/wallOverlap.cpp
        src/Weaver.cpp
        src/Wireframe2gcode.cpp

        src/communication/ArcusCommunication.cpp
        src/communication/ArcusCommunicationPrivate.cpp
        src/communication/CommandLine.cpp
        src/communication/Listener.cpp

        src/infill/ImageBasedDensityProvider.cpp
        src/infill/NoZigZagConnectorProcessor.cpp
        src/infill/ZigzagConnectorProcessor.cpp
        src/infill/SierpinskiFill.cpp
        src/infill/SierpinskiFillProvider.cpp
        src/infill/SpaghettiInfill.cpp
        src/infill/SpaghettiInfillPathGenerator.cpp
        src/infill/SubDivCube.cpp
        src/infill/GyroidInfill.cpp

        src/pathPlanning/Comb.cpp
        src/pathPlanning/GCodePath.cpp
        src/pathPlanning/LinePolygonsCrossings.cpp
        src/pathPlanning/NozzleTempInsert.cpp
        src/pathPlanning/TimeMaterialEstimates.cpp

        src/progress/Progress.cpp
        src/progress/ProgressStageEstimator.cpp

        src/settings/AdaptiveLayerHeights.cpp
        src/settings/FlowTempGraph.cpp
        src/settings/PathConfigStorage.cpp
        src/settings/Settings.cpp

        src/utils/AABB.cpp
        src/utils/AABB3D.cpp
        src/utils/Date.cpp
        src/utils/gettime.cpp
        src/utils/getpath.cpp
        src/utils/LinearAlg2D.cpp
        src/utils/ListPolyIt.cpp
        src/utils/logoutput.cpp
        src/utils/MinimumSpanningTree.cpp
        src/utils/Point3.cpp
        src/utils/PolygonConnector.cpp
        src/utils/PolygonsPointIndex.cpp
        src/utils/PolygonProximityLinker.cpp
        src/utils/polygonUtils.cpp
        src/utils/polygon.cpp
        src/utils/ProximityPointLink.cpp
        src/utils/SVG.cpp
        src/utils/socket.cpp
        )

# List of tests. For each test there must be a file tests/${NAME}.cpp.
set(engine_TEST
        GCodeExportTest
        InfillTest
        LayerPlanTest
        MergeInfillLinesTest
        TimeEstimateCalculatorTest
        )
set(engine_TEST_INFILL
        )
set(engine_TEST_INTEGRATION
        SlicePhaseTest
        )
set(engine_TEST_SETTINGS
        SettingsTest
        )
set(engine_TEST_UTILS
        AABBTest
        AABB3DTest
        IntPointTest
        LinearAlg2DTest
        MinimumSpanningTreeTest
        PolygonConnectorTest
        PolygonTest
        PolygonUtilsTest
        SparseGridTest
        StringTest
        UnionFindTest
        )
# Helper classes for some tests.
set(engine_TEST_ARCUS_HELPERS
        tests/arcus/MockSocket.cpp
        )
set(engine_TEST_HELPERS
        tests/ReadTestPolygons.cpp
        )
# Compiling CuraEngine itself.
add_library(_CuraEngine SHARED ${engine_SRCS} ${engine_PB_SRCS}) #First compile all of CuraEngine as library, allowing this to be re-used for tests.

if (CuraEngine_Download_Stb)
    add_dependencies(_CuraEngine stb)
endif()
if(USE_SYSTEM_LIBS)
    target_link_libraries(_CuraEngine ${Polyclipping_LIBRARIES})
else()
    target_link_libraries(_CuraEngine clipper)
endif()
set_target_properties(_CuraEngine PROPERTIES COMPILE_DEFINITIONS "VERSION=\"${CURA_ENGINE_VERSION}\"")
#stdc++
target_link_libraries(_CuraEngine stdc++)
if (UNIX)
   # target_link_libraries(_CuraEngine pthread)
endif()
# Installing CuraEngine.
include(GNUInstallDirs)
# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
        native-lib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
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
        native-lib

        # Links the target library to the log library
        # included in the NDK.
        _CuraEngine)


target_link_libraries( # Specifies the target library.
        _CuraEngine

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})
target_link_libraries( # Specifies the target library.
        native-lib

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})