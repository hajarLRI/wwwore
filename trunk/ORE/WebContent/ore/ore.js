var ore = {};
ore.connectURL = "http://localhost:8080/ORE/connect";
ore.onmessage = null;

ore.setMessageHandler = function(handler) {
	ore.onmessage = handler;
};

ore.messageHandler = function(text) {
	if((text === undefined) || (text === null) || (text === "") || (text === " ")) {
		return;
	}
	try {
		var obj = JSON.parse(text);
		if((obj !== undefined) && (obj !== null)) {
			ore.onmessage(obj);
		}
	} catch(e) {
		console.log(e.message);
	}
};

ore.connectHandler = function() {
	if(this.readyState === 4) {
		if(this.status !== 0) {
			ore.messageHandler(this.responseText);
			window.setTimeout("ore.connect()" , 200);
		} else {
			alert("Connection broke by server");
		}
	}
};

ore.connect = function() {
	var xhr = new XMLHttpRequest();
	xhr.open("GET", ore.connectURL, true);
	xhr.onreadystatechange = ore.connectHandler;
	xhr.send();
};

ore.init = function() {
	document.cookie = "sessionID=none";
	var xhr = new XMLHttpRequest();
	xhr.open("GET", ore.connectURL, false);
	xhr.send();
	ore.connect();
};