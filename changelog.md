#Cobalt changelog

Below is the Cobalt changelog.

## 0.2.7

* version check at startup : Cobalt now warn if you use a different version of Cobalt between web and native side.


### nativeBar

You can now define a top and bottom bar directly from the cobalt.conf. Then you can change title, show/hide buttons, use an android overflow item, and so on.

see wiki/nativeBars for detail.

### navigation improvement

* You can now pop to any previous controller. You just need to pass the controller name and html page on which you want to go back.
* A new "replace" type of navigation appears : it replace the current one by an other, removing the first from the history.
* push and replace types of navigation now have an "animated" parameter (default to true) to enable or disable default animation



## 0.2.6

* New plugin system
* Removed external library for pullToRefresh
* Samples and documentation have been updated.
* new look and feel for Catalog sample
* new shiny website

### some changes in cobalt.conf :

Since Cobalt uses the cobalt.conf file for controllers AND plugins configuration now, you have to put all your controller declarations in a new "controllers" object in the cobalt.conf root object. "iosController" and "androidController" have been renamed too.
  
This cobalt.conf ...  
  
    {
      myPage :{
	      iosController : "mySimpleController",
	  	  androidController : "com.cobaltians.HelloWorldActivity"
	  }
    }

should now be written as follow :

    {
		controllers : {
			myPage :{
				ios : "mySimpleController",
				android : "com.cobaltians.HelloWorldActivity"
			}	
		}
    }



## 0.2.5

* Cobalt is now Open Source
* GitHub repository created
* documentation created

