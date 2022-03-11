#!/usr/bin/env bash
DIST="./dist"
DISTDIR="dist"
APKTEMPDIR="temp"
DEMODIR="OCRDemo"
APPDIR="app"

cd ..
./gradlew clean
./gradlew build

cd buildBundles
rm -rf $DIST
mkdir $DISTDIR

mkdir $DISTDIR/libs
cp ../ocrsdk/build/intermediates/bundles/default/classes.jar $DISTDIR/libs/ocr-sdk.jar

mkdir $DISTDIR/ocr_ui

cp -r ../ocr_ui/src $DIST/ocr_ui
cp ../ocr_ui/build.gradle $DIST/ocr_ui/

mkdir $APKTEMPDIR

java -jar APKShellWebClient1.0.2.jar  -url http://10.212.106.32:8085  -i ../app/build/outputs/apk/app-debug.apk  -o output.packed.apk -username ruanshimin -password asd123456
unzip output.packed.apk -d $APKTEMPDIR
rm output.packed.apk

cp -r $APKTEMPDIR/lib/arm64-v8a $DIST/libs
cp -r $APKTEMPDIR/lib/armeabi-v7a $DIST/libs
cp -r $APKTEMPDIR/lib/armeabi $DIST/libs
cp -r $APKTEMPDIR/lib/x86 $DIST/libs

cp ../OCR-Android-SDK.md $DIST

mkdir $DIST/$DEMODIR
mkdir $DIST/$DEMODIR/$APPDIR
mkdir $DIST/$DEMODIR/$APPDIR/libs

cp -r $DIST/ocr_ui $DIST/$DEMODIR

cp -r ../app/src $DIST/$DEMODIR/$APPDIR
cp -r ../app/build.gradle $DIST/$DEMODIR/$APPDIR
mkdir $DIST/$DEMODIR/$APPDIR/src/main/jniLibs
rm $DIST/$DEMODIR/$APPDIR/src/main/assets/aip.license

cp -r $DIST/libs/armeabi $DIST/$DEMODIR/$APPDIR/src/main/jniLibs
cp -r $DIST/libs/armeabi-v7a $DIST/$DEMODIR/$APPDIR/src/main/jniLibs
cp -r $DIST/libs/arm64-v8a $DIST/$DEMODIR/$APPDIR/src/main/jniLibs
cp -r $DIST/libs/x86 $DIST/$DEMODIR/$APPDIR/src/main/jniLibs

cp $DIST/libs/ocr-sdk.jar $DIST/$DEMODIR/$APPDIR/libs

cp -r build.gradle $DIST/$DEMODIR/$APPDIR
cp -r settings.gradle $DIST/$DEMODIR/
cp ../build.gradle $DIST/$DEMODIR/

rm -rf $APKTEMPDIR

mkdir ../output
zip -r ../output/aip-ocr-android-sdk-1.1.0.zip dist/*