#!/bin/sh

# VARIABLES INITIALIZATION FOR BOOLEAN TYPE USAGE
TRUE=0
FALSE=1

################################################################################################################################
#                                                                                                                              #
# This script builds a "PROJECT" in "DEBUG" mode and "RELEASE" mode and makes an .apk package for both.                        #
# On "DEBUG" mode, it also adds the revision number of the "PROJECT" to the versionName field of its AndroidManifest.xml file. #
#                                                                                                                              #
# WARNINGS :	You need to complete the "VARIABLES INITIALIZATION" below.                                                     #
#				On "RELEASE" mode, you need to have your keystore in this folder.                                              #
#				Check the ANDROID_SDK_DIRECTORY constant below matches your Android SDK directory                              #
#                                                                                                                              #
# Created by Sebastien VITARD on 08/03/2011                                                                                    #
#                                                                                                                              #
################################################################################################################################

############# TO COMPLETE #############

# VARIABLES INITIALIZATION
PROJECT="HPHybridCatalog"
IN_REPOSITORY_PATH="/Hellocoton/Socle/Android/trunk/HPHybridCatalog"
TARGET="Google Inc.:Google APIs:17"                     # You can list available targets with "android list targets" command.
# Here are some targets :   1.5     : "android-3", 
# MUST BE REGISTERED ON MANAGER         #                           1.6     : "android-4", 
MANAGER_NAME="HPHybridCatalog"                   #                           2.1     : "android-7", 
VERSION_NAME="1.0"                      #                           2.2     : "android-8", 
PACKAGE="fr.haploid.hphybridcatalog"               #                           2.3.3   : "android-10"
#                           3.0     : "android-11", 
ENABLE_DEBUG_BUILD=$FALSE               #                           3.1     : "android-12", 
ENABLE_RELEASE_BUILD=$TRUE              #                           3.2     : "android-13", 
#                           4.0     : "android-14". 

ENABLE_MANAGER_BUILD=$FALSE

#######################################

echo $PROJECT
echo $IN_REPOSITORY_PATH
echo $TARGET
echo $MANAGER_NAME
echo $VERSION_NAME
echo $PACKAGE
echo $ENABLE_DEBUG_BUILD
echo $ENABLE_RELEASE_BUILD
echo $ENABLE_MANAGER_BUILD

exit 0
