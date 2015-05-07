import os

libName = "cobalt"
distrib_path = os.path.normpath(os.path.abspath(os.path.join(os.path.dirname(__file__))))
web_sources_path = os.path.abspath(os.path.join(os.pardir, os.pardir, 'sources','Web'))
common_file_path=os.path.abspath(os.path.join(web_sources_path, 'common', "%s.js" % libName ))


if not os.path.isfile(common_file_path):
    print "error : no common file %s " % common_file_path
    exit()


for str_os in ['iOs', 'Android']:
    print "\n---building %s file" % str_os
    #print os.getcwd()
    
    concatened_file_folder_path = os.path.join(distrib_path, str_os )
    concatened_file_path = os.path.join(distrib_path, str_os, "%s.js" % libName )
    
    adapter_file_path=os.path.abspath(os.path.join(web_sources_path, 'adapters', str_os, "adapter.js"))
    
    if os.path.isfile(adapter_file_path):
        print "got adapter file."        
        print "concating files %s and %s now!" % (common_file_path, adapter_file_path)
        
        filenames = [common_file_path, adapter_file_path]
        with open(concatened_file_path, 'w+') as outfile:
            for fname in filenames:
                with open(fname) as infile:
                    outfile.write(infile.read())
        
        print "creating minified version"
        os.chdir(concatened_file_folder_path)
        os.system('uglifyjs {libName}.js > {libName}.min.js'.format(libName=libName))
        
    else:
        print "error : no adapter file for %s" % str_os

print "\n---copying files to samples apps"
os.chdir(os.pardir)
os.system('./updateSamples.sh')
    
    
    
    

        
    
