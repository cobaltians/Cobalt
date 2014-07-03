#!/bin/bash

###########
#FUNCTIONS#
###########
#
#DISPLAY cobalt.sh ARGUMENTS USAGE
#
function displayHelp {

  echo
  echo "***cobalt.sh usage***"
  echo
  echo "     -a [PATH OF WORKSPACE] In order to create a new Cobalt project"
  echo "     -p In order to add plugins to a Cobalt project"
  echo
  echo "*********************"
  echo
}
#
#RETURN A RELATIVE PATH FROM TWO ABSOLUTE PATHS
#
# both $1 and $2 are absolute paths beginning with /
# returns relative path to $2/$target from $1/$source
function abs_path {

source=$1
target=$2

common_part=$source # for now
result="" # for now

while [[ "${target#$common_part}" == "${target}" ]]; do
    # no match, means that candidate common part is not correct
    # go up one level (reduce common part)
    common_part="$(dirname $common_part)"
    # and record that we went back, with correct / handling
    if [[ -z $result ]]; then
      result=".."
    else
      result="../$result"
    fi
  done

  if [[ $common_part == "/" ]]; then
    # special case for root (no common path)
    result="$result/"
  fi

forward_part="${target#$common_part}"

# and now stick all parts together
if [[ -n $result ]] && [[ -n $forward_part ]]; then
  result="$result$forward_part"
elif [[ -n $forward_part ]]; then
    # extra slash removal
    result="${forward_part:1}"
  fi

  echo $result

}
##################
function plugin {
  echo " now going to add some plugins"
#TO DO

}

#
#USED TO CREATE A NEW COBALT PROJECT
#
#ARGUMENT IS THE PROJECT PATH
function cobaltproject {
  PROJECT_NAME=""
  PROJECT_PATH=${1}/
  PACKAGE_NAME=""
### LET'S TEST IF ANDROID IS INSTALLED
ANDROID_BOOL=$(command -v android)

if [ -z $ANDROID_BOOL ]; then

  echo "Please install Android as a command line";
  exit 1;

fi
  echo

  while [ -z $PROJECT_NAME ]
  do
    echo "Enter Project name and press [ENTER] :"
    read PROJECT_NAME


    if [[ -n $PROJECT_NAME  ]]; then
      echo "The project name is $PROJECT_NAME"
    fi

  done
##TODO check the project name ( regex)


echo "Enter the main package name and press [ENTER] (leave it blank for com.android.app) :"
read PACKAGE_NAME
##TODO check the package name ( regex)
if [[ -z $PACKAGE_NAME ]]; then
  PACKAGE_NAME="com.android.app"
fi
echo "The package name is $PACKAGE_NAME"
echo
echo "Creating project..."

COBALT_PATH="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

ABS_PATH=$(abs_path ${PROJECT_PATH}/${PROJECT_NAME} $COBALT_PATH )

echo "abs path ${ABS_PATH}"
echo "Cobalt path ${COBALT_PATH}"
echo "project path ${PROJECT_PATH}"

PACKAGE_AS_A_PATH=$(echo $PACKAGE_NAME | sed 's/\./\//g')

android create project --name ${PROJECT_NAME} --target 1 --path ${1}/${PROJECT_NAME} --activity MainActivity --package $PACKAGE_NAME

android update project -p ${1}/${PROJECT_NAME} -l $ABS_PATH --target 1

mkdir ${PROJECT_PATH}${PROJECT_NAME}/assets
mkdir ${PROJECT_PATH}${PROJECT_NAME}/assets/www
mkdir ${PROJECT_PATH}${PROJECT_NAME}/assets/www/js
mkdir ${PROJECT_PATH}${PROJECT_NAME}/assets/www/css
touch ${PROJECT_PATH}${PROJECT_NAME}/assets/www/cobalt.conf
touch ${PROJECT_PATH}${PROJECT_NAME}/src/${PACKAGE_AS_A_PATH}/MainFragment.java
touch ${PROJECT_PATH}${PROJECT_NAME}/assets/www/home.html
###COPYING FILES

cp ${COBALT_PATH}/../../../distribution/web/Android/cobalt.js ${PROJECT_PATH}${PROJECT_NAME}/assets/www/js/
cp ${COBALT_PATH}/../../../distribution/web/Android/cobalt.min.js ${PROJECT_PATH}${PROJECT_NAME}/assets/www/js/


####WRITTING IN RIGHT FILES
#WRITTING cobalt.conf

echo "{

  \"default\":{
    \"androidController\":\"${PACKAGE_NAME}.MainActivity\"
  }
}
">> ${PROJECT_PATH}/${PROJECT_NAME}/assets/www/cobalt.conf

#WRITTING MainFragment.java

echo "
    package ${PACKAGE_NAME};
    import ${PACKAGE_NAME}.R;
    import org.json.JSONObject;
    import android.os.Bundle;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import fr.cobaltians.cobalt.fragments.CobaltFragment;

public class MainFragment extends CobaltFragment {

      @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);

		return view;
	}

	@Override
	protected boolean onUnhandledCallback(String callback, JSONObject data) {

		return false;
	}

  @Override
	protected boolean onUnhandledEvent(String event, JSONObject data,
			String callback) {

		return true;
	}

  @Override
	protected void onUnhandledMessage(JSONObject message) {

  }


}"> ${PROJECT_PATH}${PROJECT_NAME}/src/${PACKAGE_AS_A_PATH}/MainFragment.java

#WRITTING MainActivity.java
echo "
  package ${PACKAGE_NAME};
  import ${PACKAGE_NAME}.R;
  import fr.cobaltians.cobalt.Cobalt;
  import fr.cobaltians.cobalt.activities.CobaltActivity;
  import fr.cobaltians.cobalt.fragments.CobaltFragment;


public class MainActivity extends CobaltActivity {

	@Override
	protected int getLayoutToInflate() {
		return R.layout.main;
	}

	@Override
	protected CobaltFragment getFragment() {

		return Cobalt.getInstance(this).getFragmentForController(MainFragment.class, \"default\", \"home.html\");

  }

}"> ${PROJECT_PATH}${PROJECT_NAME}/src/${PACKAGE_AS_A_PATH}/MainActivity.java

#WRITTING home.html

echo "
  <!DOCTYPE html>
<html>
    <head>
        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />
        <meta name=\"viewport\" content=\"initial-scale=1.0, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0\"/>
        <title>Hello World</title>

        <script type=\"text/javascript\" src=\"js/cobalt.js\"></script>

    </head>
    <body>

        <p>HelloWorld<p>

        <script>
            Zepto(function($){
                cobalt.init({
                    debug : true,
                });
            });
        </script>
    </body>
</html>"> ${PROJECT_PATH}${PROJECT_NAME}/assets/www/home.html

#WRITTING main.xml

echo "
<?xml version=\"1.0\" encoding=\"utf-8\"?>
<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"
    android:orientation=\"vertical\"
    android:layout_width=\"fill_parent\"
    android:layout_height=\"fill_parent\"
    >

	<RelativeLayout
	    android:id=\"@+id/fragment_container\"
	    android:layout_width=\"match_parent\"
	    android:layout_height=\"match_parent\"/>

  </LinearLayout>" > ${PROJECT_PATH}${PROJECT_NAME}/res/layout/main.xml


}
#########################
###VARIABLE DEFINITION###
#########################

NO_ARG="";
while getopts "a:p:h" opt; do

  case $opt in
    a)
    NO_ARG="A"
    cobaltproject $OPTARG

  ;;
    p)
    NO_ARG="P"
    plugin
  ;;
    h)
    NO_ARG="H"
    displayHelp;
  ;;
    ?)
    displayHelp;
    exit 2;
    #echo "Invalid option: -$OPTARG" >&2
  ;;
esac
done

if [ -z $NO_ARG ]; then

  displayHelp;
  exit 2;

fi


#android create project --name hello --target 1 --path hello --activity DefaultActivity --package com.default.hello
