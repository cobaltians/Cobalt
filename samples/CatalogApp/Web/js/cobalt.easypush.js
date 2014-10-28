(function(cobalt){
    var plugin={
        name:"EasyPush",
		
		//default callbacks
		onDeviceIdentifier : function( data ){
            cobalt.log('EasyPush : web received push identifier', data.identifier )
        },
        onReceivedPushNotification : function( data ){
			cobalt.log('EasyPush : web received notification', data.notification )
        },
        onOpenWithNotification : function( data ){
			cobalt.log('EasyPush : web received notification', data.notification )
        },
			
		//initing plugin
        init:function(options){			
			cobalt.log('initialising EasyPush plugin');

			//create shortcuts
            cobalt.EasyPush={
                config : this.config.bind(this)
            }
            if (options){
                cobalt.EasyPush.config(options);
            }
        },
		// called from init or if dev uses cobalt.EasyPush.config({...})
		config : function(options){
			
			if (options.EasyPushParameters){
	            cobalt.log('initialising EasyPush plugin with options', options);
                var params={
					EasyPushParameters : options.EasyPushParameters,
					AndroidParameters : options.AndroidParameters
				}
				if (typeof options.onDeviceIdentifier == "function"){
                    this.onDeviceIdentifier = options.onDeviceIdentifier
				}
				if (typeof options.onReceivedPushNotification == "function"){
                    this.onReceivedPushNotification = options.onReceivedPushNotification
				}
				if (typeof options.onOpenWithNotification == "function"){
                    this.onDeviceIdentifier = options.onOpenWithNotification
				}
								
				this.send('init', params)
				
			}else{
				cobalt.log('Error : no EasyPush options for EasyPush plugin. aborting.')
			}
			this.send('init')
            
			
		},
		send:function(event, data, callback){
		       cobalt.send({ type : "plugin", name : "EasyPush", event : event, data : data }, callback);
		},
        handleEvent:function(json){
            cobalt.log('received native EasyPush call', json)
			switch (json.data.event){
				case "onDeviceIdentifier":
					this.onDeviceIdentifier(json.data);
					break;
				case "onReceivedPushNotification":
					this.onReceivedPushNotification(json.data);
					break;
				case "onOpenWithNotification":
					this.onOpenWithNotification(json.data);
					break;
				default :
				cobalt.log('received something weird from EasyPush plugin')
					break;
			}
        }
    };
    cobalt.plugins.register(plugin);        
})(cobalt || {});