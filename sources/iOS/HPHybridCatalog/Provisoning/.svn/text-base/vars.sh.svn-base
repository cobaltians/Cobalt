#!/bin/sh
# Test relocate svn
# INITIALIZES AND EXPORTS VARIABLES FOR BOOLEAN TYPE USAGE
TRUE=0
FALSE=1

####################################################################################################################################
#                                                                                                                                  #
# This script builds two configurations of a "PROJECT" : a "DEBUG" configuration and an "ADHOC" one,                               #
# and makes an archive with an .ipa package and a version text file for both.                                                      #
# In "DEBUG" configuration, it also adds the revision number of the "PROJECT" to the CFBundleVersion field of its Info.plist file. #
#                                                                                                                                  #
# WARNING  :   You need to complete the "VARIABLES INITIALIZATION" below                                                           #
#                                                                                                                                  #
# Created by Sebastien VITARD on 03/03/2011                                                                                        #
#                                                                                                                                  #
####################################################################################################################################

################# TO COMPLETE #################

################## COMMON #################

IS_A_WORKSPACE_BUILD=$FALSE
IN_REPOSITORY_PATH="Hellocoton/Socle/iOS/trunk/HPHybridCatalog" 

# MUST BE REGISTERED ON MANAGER
BUNDLE_VERSION="1.0"
MANAGER_NAME="Hybrid Catalog"

# CERTIFICATE MUST BE HOSTED BY PLUTON
SIGNER="Haploid"
BUNDLE_IDENTIFIER="fr.haploid.hybridCatalog"

ADHOC_CONFIGURATION_NAME="AdHoc"
APPSTORE_CONFIGURATION_NAME="Distribution"

ENABLE_DEBUG_BUILD=$FALSE
ENABLE_ADHOC_BUILD=$TRUE
ENABLE_MANAGER_BUILD=$TRUE

###########################################

if [ $IS_A_WORKSPACE_BUILD -eq $TRUE ]
then
    ########### WORKSPACE ###########
    XCODE_WORKSPACE="HPNativeBridge"
    SCHEME="HPHybridCatalog"
    APP="HPHybridCatalog"
    #################################
else
    ############ PROJECT ############
    PROJECT="RMC"
    TARGET="RMC"
    APP="RMC"
    #################################
fi

###############################################

echo    $IS_A_WORKSPACE_BUILD
echo    $IN_REPOSITORY_PATH
echo    $APP
echo    $BUNDLE_VERSION
echo    $MANAGER_NAME
echo    $SIGNER
echo    $BUNDLE_IDENTIFIER
echo    $ADHOC_CONFIGURATION_NAME
echo	$APPSTORE_CONFIGURATION_NAME
echo    $ENABLE_DEBUG_BUILD
echo    $ENABLE_ADHOC_BUILD
echo    $ENABLE_MANAGER_BUILD
echo    $XCODE_WORKSPACE
echo    $SCHEME
echo    $PROJECT
echo    $TARGET

exit 0
