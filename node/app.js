/* ------ Facebook Hackathon  -------
//
// Guitar Pro Viewer for Spotify
// NodeJS and Java based Spotify Application
// This is the main server
//
// Pratham Agrawal, Shivam Kejriwal, Tushar Bhushan
//
//-----------------------------------*/

//Each module is optional and configured independantly.
//Don't want it? Just comment out/delete the entire module out.

//Let's start with the basics first so that we have that out of the way
configuredModules 	= {}; //this will store which modules have been activated. Associative array of booleans.
var env 			= 'development' //set the env to development. Don't know why we might need this but ok.

//Basic requires that might be needed. (Might not exist in the future)
var filesystem	= require('fs'),
	http		= require('http'),
	express		= require('express'); //currently required as its an express app lol

//MONGOOSE
//-- MongoDB Driver for a database. Currently no other database is supported. Sorry
var mongoose = require('mongoose');	//require the mongoose module
mongoose.connect('mongodb://localhost/fbhack2013'); //the database to connect to. can be external to if you want to deploy to nodejitsu
configuredModules['mongoose'] = true; //Flag it as enabled
//-/MONGOOSE

//MODELS
//-- Require all the models under a given directory
//-- Should only work with mongoose so it is only done if mongoose is used
if (configuredModules['mongoose']) {
	var models_path = __dirname + '/app/models'
	filesystem.readdirSync(models_path).forEach(function (file) {
		require(models_path+'/'+file)
	})
	configuredModules['mongoose'] = true;
}
//-/MODELS

//REQUIRED - EXPRESS
//-- Express JS framework for node. This app revolves around it for now.
var app 	= express();
var cookieParser = express.cookieParser('fbhack2013')
var params = {
	app 		: app,
	cookieParser: cookieParser
};

require('./config/express')(params);
configuredModules['express'] = true;
//-/EXPRESS

//REQUIRED - ROUTER
//-- This is where all the routes are declared.
var routerParams = {
	app : app
};

require('./config/routes')(routerParams);
//-/ROUTER

//FINAL OPS
//Start the server listen on ports etc. etc.
var server = http.createServer(app); //Create the server
var port = process.env.PORT || 3000
server.listen(port)
console.log('listening on port '+port)

//EXPOSE APPLICATION //Got it, replaces the default exports and module.exports with app
exports = module.exports = app