module.exports = function(params) {

	var app = params['app'];

	//__IMPORT ALL THE CONTROLLERS
	var main = require('../app/controllers/main');

	//__get the tab
	app.get('/search/:artist/:title', main.fetch);

 	//__FINALLY IF THERE IS NO KNOWN URL INCL. '/' THEN GO TO HOME
 	// app.get('/*', main.index);

}

