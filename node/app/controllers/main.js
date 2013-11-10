var request = require('request')
var cheerio = require('cheerio')

exports.index = function (req, res) {
	res.render('index');
}

exports.fetch = function(req, res) {
	console.log(req.params);
	var url = 'http://www.911tabs.com/tabs/o/'+req.params.artist+'/guitar_pro_tabs/'+req.params.title+'_guitar_pro_tab.htm' 
	console.log(url);
	request(url, function(err, response, body) {
		if (err)
			console.log('error encountered')
		else {
			$ = cheerio.load(body);
			var counter = 0;
			$('.song-line').each(function() {
				var provider = $(this).children('.main-info').text().replace(/\s+/g, ' ');
    			if (provider.indexOf('Guitariff')>0 && counter==0) {
    				counter++;
    				var href = $(this).attr('href');
    				console.log(href);

    				var guitariff_link = 'http://www.911tabs.com'+href;
    				console.log(guitariff_link);

    				request(guitariff_link, function(err, response, guitariff_body) {
    					if (err) 
    						console.log('error encountered');
    					else {
    						$ = cheerio.load(guitariff_body);
    						console.log(guitariff_body);
    					}
    				})

    			}
			});
		}

	})
	res.send({
		 artist	: req.params.artist,
		 title 	: req.params.title
	})
}