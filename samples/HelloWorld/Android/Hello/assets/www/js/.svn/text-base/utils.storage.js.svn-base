
if ( ! window.utils) var utils={};
utils.storage={
	/*	STORAGE
			help in using html5 storage.

			1) call utils.storage.enable()  (return false if failed)
			2) use like this :

			utils.storage.setItem('town','Lannion');
			utils.storage.getItem('town');
				//returns 'Lannion'

			utils.storage.setItem('age',12);
			utils.storage.getItem('age');
				//returns '12' (string)
			utils.storage.getItem('age','int'); //there is also float, date, json
				//returns 12 (number)

			//experimental :
			utils.storage.setItem('user',{name:'toto',age:6},'json');
			utils.storage.getItem('user','json');
				//returns {name:'toto',age:6} (object)

	 */
	storage:false,
	enable:function(){
		var storage,
				fail,
				uid;
		try {
			uid = new Date;
			(storage = window.localStorage).setItem(uid, uid);
			fail = storage.getItem(uid) != uid;
			storage.removeItem(uid);
			fail && (storage = false);
		} catch(e) {}

		if (!storage){
			return false;
		}else{
			this.storage=storage;
			return true;
		}
	},
	clear:function(){
		if (this.storage){
			this.storage.clear();
		}
	},
	getItem:function(uid, type){
		if (this.storage){
			var val=this.storage.getItem(uid);
			if (val){
				switch(type){
					case undefined :return val;
					case "int":     return parseInt(val);
					case "float":   return parseFloat(val);
					case "date":    return new Date(val);
					case "json":
						try{
							return JSON.parse(val);
						}catch(e){
							return {};
						}

				}
			}
			return undefined;
		}
	},
	setItem:function(uid, value, type){
		if (this.storage){
			switch ( type ){
				case undefined :return this.storage.setItem(uid,""+value);
				case 'json' :   return this.storage.setItem(uid, JSON.stringify(value));
			}
		}
	},
	removeItem:function(uid){
		if (this.storage){
			return this.storage.removeItem(uid)
		}
	}
};