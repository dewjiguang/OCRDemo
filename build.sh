#!/usr/bin/env bash
#export JAVA_HOME=/home/scmtools/buildkit/java/jdk-1.7u60/
ROOT=`pwd`
BUILDPATH=$ROOT"/buildBundles";
DIST=$BUILDPATH"/dist"
DISTDIR=$DIST
APKTEMPDIR=$BUILDPATH"temp"
DEMODIR="/OCRDemo"
APPDIR="/app"


./gradlew clean
./gradlew build

cd buildBundles
rm -rf $DIST
mkdir $DISTDIR

mkdir $DISTDIR/libs
cp ../ocrsdk/build/intermediates/bundles/default/classes.jar $DISTDIR/libs/ocr-sdk.jar

mkdir $DISTDIR/ocr_ui

cp -r ../ocr_ui/src $DIST/ocr_ui
cp -r ../ocr_ui/libs $DIST/ocr_ui
cp ../ocr_ui/build.gradle $DIST/ocr_ui/
cp ../ocr_ui/proguard-rules.pro $DIST/ocr_ui/

mkdir $APKTEMPDIR

#java -jar APKShellWebClient1.0.2.jar  -url http://10.212.106.32:8085  -i ../app/build/outputs/apk/app-debug.apk  -o output.packed.apk -username ruanshimin -password asd123456
unzip ../app/build/outputs/apk/app-release-unsigned.apk -d $APKTEMPDIR
#rm output.packed.apk

sdkSoFile1="libocr-sdk.so"
mkdir $DIST/libs/arm64-v8a
mkdir $DIST/libs/armeabi-v7a
mkdir $DIST/libs/armeabi
mkdir $DIST/libs/x86
cp  $APKTEMPDIR/lib/arm64-v8a/$sdkSoFile1 $DIST/libs/arm64-v8a
cp  $APKTEMPDIR/lib/armeabi-v7a/$sdkSoFile1 $DIST/libs/armeabi-v7a
cp  $APKTEMPDIR/lib/armeabi/$sdkSoFile1 $DIST/libs/armeabi
cp  $APKTEMPDIR/lib/x86/$sdkSoFile1 $DIST/libs/x86

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

FILEWITHVERSION="aip-ocr-android-sdk-1.4.4"
OUTPUT=$ROOT"/output"
rm -rf $OUTPUT
mkdir $OUTPUT
mv dist $FILEWITHVERSION
zip -r $OUTPUT/$FILEWITHVERSION.zip $FILEWITHVERSION/*
rm -rf $FILEWITHVERSION
