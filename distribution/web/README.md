### How to use ?

Javascript files are split into a common file for all OSes and an adapter file for each platform.
Include the adapter file corresponding to the platform you work on in your Web pages.
There is no all-in-one-with-autodection version yet but we think about it :)

### How to update ?

To build cobalt.js and cobalt.min.js files for each platform in one row, run the following command line:

    python compile.py

#### Building requirements

* You will need Python (we have 2.7 here but 2.6 should be fine too).
* 'uglifyjs' should be installed first for minified versions to be built.

You can find UglifyJS [here](https://github.com/mishoo/UglifyJS). It uses node.js I think.
