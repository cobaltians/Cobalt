### How to use ?

javascript files are split into a common file for all OS and a platform adapter for each OS.

Include the file corresponding to the platform you work on in your webpages.

There is no all-in-one-with-autodection version yet but we think about it :)


### how to update ?

to compile all cobalt.js and cobalt.min.js for each platforms in one time, run this line in a command line :

python compile.py

#### requirements for compilation

* You will need python (we have 2.7 here but 2.6 should be fine too)
* 'uglifyjs' should be installed first for minified versions to be built

you can find uglifyjs [here](https://github.com/mishoo/UglifyJS). it uses node.js I think.

