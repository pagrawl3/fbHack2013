var request = require('request')
var cheerio = require('cheerio')

exports.index = function (req, res) {
	res.render('index');
}

exports.fetch = function(req, res) {
	console.log(req.params);
	
	var sys = require('sys')
	var exec = require('child_process').exec;
	function puts(error, stdout, stderr) { 
		console.log(stdout);
		res.send({
			 artist	: req.params.artist,
			 title 	: req.params.title
		})
	}
	exec('java -jar tabparser.jar -t "'+req.params.title+'" -a "'+req.params.artist+'" -d "tabs/'+req.params.artist.split(" ").join("_")+'-'+req.params.title.split(" ").join("_")+'" -j "tabs/'+req.params.artist.split(" ").join("_")+'-'+req.params.title.split(" ").join("_")+'.json"', puts);
}