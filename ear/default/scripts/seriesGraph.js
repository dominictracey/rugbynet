/*
 * (c) 2016 The Rugby Net, Inc.
 * 
 * This script may only be used by the licensee. Contact info@rugby.net for support.
 */



require(["d3"], function(d3) {
	
	var host = document.getElementById('chart').getBoundingClientRect();
	
	var margin = {top: 20, right: 140, bottom: 30, left: 50},
	width = host.right - host.left - margin.left - margin.right,
	height = host.bottom - host.top - margin.top - margin.bottom;
//	width = 960 - margin.left - margin.right,
//	height = 500 - margin.top - margin.bottom;
	
	var parseDate = d3.time.format("%Y%m%d").parse,
	bisectRound = d3.bisector(function(d) { return d.round; }).left;

	var x = d3.scale.linear()
	.range([0, width]);

	var ranking_y = d3.scale.linear()
	.range([0, height]);

	var color = d3.scale.category10();



	var yAxis = d3.svg.axis()
	.scale(ranking_y)
	.orient("left")
	.tickValues([1,5,10,15,20,25,30]);

	var line = d3.svg.line()
	.interpolate("linear")
	.x(function(d) { return x(d.round); })
	.y(function(d) { return ranking_y(d.ranking); });

	
	
	// nav buttons
	d3.select("#chart").append("div")
	.attr("id","buttonBar");
	
	var buttonBar = d3.select('#buttonBar');
	
	addButton(buttonBar, "Prop", 0)
	addButton(buttonBar, "Hooker", 1)
	addButton(buttonBar, "Lock", 2)
	addButton(buttonBar, "Flanker", 3)
	addButton(buttonBar, "Number 8", 4)
	addButton(buttonBar, "Scrumhalf", 5)
	addButton(buttonBar, "Flyhalf", 6)
	addButton(buttonBar, "Centre", 7)
	addButton(buttonBar, "Wing", 8)
	addButton(buttonBar, "Fullback", 9)
	
	var svg = d3.select("#chart").append("svg")
	.attr("width", width + margin.left + margin.right)
	.attr("height", height + margin.top + margin.bottom)
	.append("g")
	.attr("transform", "translate(" + margin.left + "," + margin.top + ")")

	// loader settings
	var opts = {
			lines: 9, // The number of lines to draw
			length: 9, // The length of each line
			width: 5, // The line thickness
			radius: 14, // The radius of the inner circle
			color: '#EE3124', // #rgb or #rrggbb or array of colors
			speed: 1.9, // Rounds per second
			trail: 40, // Afterglow percentage
			className: 'spinner', // The CSS class to assign to the spinner
	};
	var target = document.getElementById('chart');



	updateData(null,null);


	//** Update data section (also called from the onclick)
	function updateData(pos,compId) {
		var spinner = null;
		require(["spin"], function(Spinner) {
			spinner = new Spinner(opts).spin(target);
		});
		
		for (i=0;i<10;i++) {
			var button = document.getElementById(i);
			if (button !== null) {
				button.className = "btn btn-primary disabled";
			}
		}
		
		var button = document.getElementById(pos);

		if (button !== null) {
			button.className = "btn btn-danger disabled";
		}

		if (pos == null) {
			pos = getURLParameter('pos');

			if (!(/^\d$/.test(pos)))  {
				pos = 4;
			}
		} else {
			ganew('send', 'event', 'graph', 'click', pos, '1');		
		}



		if (compId == null) {
			compId = getURLParameter('compId');

			if (!(/^\d$/.test(pos)))  {
				compId = 5095957750349824;
			}
		}

		url = "https://fantasyrugbyengine-hrd-prod.appspot.com/_ah/api/topten/v1/series/position/get?compId=" + compId + "&position=" + pos;
		//url = "/_ah/api/topten/v1/series/position/get?compId=" + compId + "&position=" + pos;

		d3.json(url, function(error, data) {
			if (error) throw error;

			//stop spin.js loader
			require(["spin"], function(Spinner) {
				spinner.stop();
			});
			
			for (i=0;i<10;i++) {
				var button = document.getElementById(i);
				if (button !== null) {
					button.className = button.className.substring(0,button.className.indexOf("disabled")); //"btn btn-primary disabled";
				}
			}

			var xLabs = {};
			var playerLists = data.items.map(function(d) { return  {round: d.round, values: d.playerMatches }});
			var xAxisLabels = data.items.map(function(d) { xLabs[d.round] = d.label; return {key:d.round, value:d.label}});

			var xAxis = d3.svg.axis()
			.scale(x)
			.orient("bottom")
			.tickFormat(function(d,i) { 
				var val = xLabs[d];
				return val;
			});

			// we only care about the names of the players in the most recent round
			var keys = d3.keys(playerLists[0].values); //map(function(d) { return d3.keys(d)});

			color.domain(keys);


			var players = color.domain().map(function(name) {
				return {
					name: name,
					values: playerLists.map(function(d) {
						var retval = {round: d.round};  
						if (name in d.values) {
							retval = {round: d.round, rating: +d.values[name].rating, ranking: +d.values[name].ranking, teamAbbr: d.values[name].teamAbbr, label: d.values[name].label, homeTeamAbbr: d.values[name].homeTeamAbbr, visitingTeamAbbr: d.values[name].visitingTeamAbbr, homeTeamScore: d.values[name].homeTeamScore, visitingTeamScore: d.values[name].visitingTeamScore, matchDate: d.values[name].matchDate, position: d.values[name].position, matchRating: d.values[name].matchRating, minutesPlayed: d.values[name].minutesPlayed, notes: d.values[name].notes };
						}
						return retval;      })
				};
			});

			x.domain(d3.extent(data.items, function(d) { return d.round; }));

			ranking_y.domain([
			                  d3.min(players, function(c) { return d3.min(c.values, function(v) { return v.ranking; }); }),
			                  d3.max(players, function(c) { return d3.max(c.values, function(v) { return v.ranking; }); })
			                  ]);

			svg.selectAll(".axis").remove();
			
			svg.append("g")
			.attr("class", "x axis")
			.attr("transform", "translate(0," + height + ")")
			.call(xAxis);

			svg.append("g")
			.attr("class", "y axis")
			.call(yAxis)
			.append("text")
			.attr("transform", "rotate(-90)")
			.attr("y", -35)
			.attr("dy", ".71em")
			.style("text-anchor", "end")
			.text("Ranking");

			var popup = d3.select("body").append("div")
			.attr("class","tooltip")
			.style("opacity", 0);

			svg.selectAll(".rating").remove();

			var rating = svg.selectAll(".rating")
			.data(players)
			.enter().append("g")
			.attr("class", "rating");

			rating.append("path")
			.attr("class", "line")
			.attr("d", function(d) { return line(d.values); })
			.style("stroke", function(d) { return color(d.name); })
			.style("stroke-width", 3)
			.style("stroke-opacity",.1);

			rating.append("text")
			.datum(function(d) { return {name: d.name, value: d.values[0]}; })
			.attr("transform", function(d) { return "translate(" + x(d.value.round) + "," + ranking_y(d.value.ranking) + ")"; })
			.attr("x", 7)
			.attr("dy", ".35em")
			.text(function(d) { return d.name; });

			var point = rating.append("g")
			.attr("class", "line-point");

			point.selectAll("circle")
			.data(function(d,i) { return d.values;})
			.enter().append("circle")
			.attr("transform", function(d,i) { return "translate(" + x(d.round) + "," + ranking_y(d.ranking) + ")"; })
			.attr("r",5) 		
			.attr("class", "point-out")
			.on("mouseover",  function(d,i) { mouseover(x(d.round),ranking_y(d.ranking), this); } )
			.on("mouseout", mouseout);

			function mouseover(px, py, _this) {


				d3.select(_this.parentNode)
				.selectAll(".point-out")
				.attr("class", "point-on")

				d3.select(_this)
				//.attr("r",15)
				.attr("class", "point-in")

				d3.selectAll(".line")
				.style("stroke-width", 3)
				.style("stroke-opacity",.1);

				d3.select(_this.parentNode.parentNode)
				.select(".line")
				.style("stroke-width", 5)
				.style("stroke-opacity",.7);

				popup.transition()		
				.duration(200)		
				.style("opacity", .9);		

				var html = "<div style=\"border-bottom-style:solid;border-bottom-width:1px;display:inline-block;width:100%;padding-bottom:10px;\"><div class=\"teamlogo-small " + _this.__data__.teamAbbr + "\" style=\"width:60px;height:50px;float:left;display:inline-block;padding-left:25px;\"></div><div style=\"display:inline-block;padding-top:15px;font-size:1.3em;\">" + _this.__data__.ranking + ". " + _this.parentNode.__data__.name + " (" + _this.__data__.rating + ")</div></div>"
				+ "<div style=\"border-bottom-style:solid;border-bottom-width:1px;\"><div class=\"teamlogo-small " + _this.__data__.homeTeamAbbr + "\" style=\"width:65px;height:65px;float:left;display:inline-block;padding-left:15px;padding-top:15px;\"></div><div style=\"display:inline-block;\"> <h3>" + _this.__data__.homeTeamScore + " - " + _this.__data__.visitingTeamScore + "</h3></div><div class=\"teamlogo-small " + _this.__data__.visitingTeamAbbr + "\" style=\"width:65px;height:65px;float:right;display:inline-block;padding-right:15px;padding-top:15px;\"></div>"
				+ "<div style=\"padding-bottom:20px;\"><br/><b>" + _this.__data__.label + "</b><br/>" + _this.__data__.matchDate + "</div></div>" 
				+ "<div style=\"border-bottom-style:solid;border-bottom-width:1px;min-height:80px;\"><div style=\"width:60px;height:60px;float:left;display:inline-block;padding-left:10px;padding-top:15px;text-align:left;\">Position:<br/><img class=\"" + _this.__data__.position + "\" style=\"width:100%;height:100%;\"></div><div style=\"display:inline-block;border-style: solid;border-width: 1px;padding:10px;margin:0px;background-color:#efefef;\"> <h4>Match Rating</h4><h4>" + _this.__data__.matchRating + "</h4></div><div style=\"width:60px;height:60px;float:right;display:inline-block;padding-right:15px;padding-top:15px;border-left:1px;\">Minutes:<h4>" + _this.__data__.minutesPlayed + "</h4></div></div>"
				+ "<div>" + buildNotes(_this.__data__.notes) + "</div>";
				popup.html(html)	
				.style("left", function() { return getPopupLeft(popup);})   //(d3.event.pageX) + "px")		
				.style("top",  function() { return getPopupTop(popup);})  // (d3.event.pageY - 28) + "px");	

			}

			function mouseout() { 
				d3.select(this.parentNode)
				.selectAll(".point-on")
				.attr("class", "point-out")

				d3.select(this)
				.attr("class","point-out")

				popup.transition()		
				.duration(500)		
				.style("opacity", 0);	
			}

			function buildNotes(notes) {
				var retval = "<div style=\"float: left;display:inline-block;padding-left:0px;width: 45%;font-size:.85em;\"><ul>",
				count = 0;
				for (var key in notes) {
					retval += "<li>" + notes[key] + "</li>";
					count++;
					if (count > 2) {
						retval += "</ul></div><div style=\"float: right;display:inline-block;padding-right:0px;width: 45%;font-size:.85em;\"><ul>";
						count = 0;
					}
				}

				return retval + "</ul></div>";
			}
			
			function getPopupTop(popup) {
				// if we are above the midpoint of the range put it 28 px from the point of the event, if below 
				if (d3.event.pageY < height/3)
					return (d3.event.pageY - 28) + "px";	
				else if (d3.event.pageY < height/1.3)
					return (d3.event.pageY - 350/2 + 28) + "px";  // popup height of 350 hardcoded in graph.css
				else {
					return (d3.event.pageY - 350 + 28) + "px";
				}
			}

			function getPopupLeft(popup) {
				if (d3.event.pageX < width/2)
					return (d3.event.pageX + 7) + "px";	
				else {
					return (d3.event.pageX - 275 - 7) + "px";
				}
				return (d3.event.pageX) + "px";
			}
		});
	}


function getURLParameter(name) {
	return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search)||[,""])[1].replace(/\+/g, '%20'))||null;
}

function addButton(buttonBar, id, ordinal) {
	buttonBar.append("div")
	.attr("id",id)
	.attr("class","positionButton")
  .append("input")
	.attr("class", "btn btn-primary")
	.attr("value",id)
	.attr("id",ordinal)
	.attr("type","button")
	.on('click', function() {updateData(ordinal)});
}

});

(function(i, s, o, g, r, a, m) {
	i['GoogleAnalyticsObject'] = r;
	i[r] = i[r] || function() {
		(i[r].q = i[r].q || []).push(arguments)
	}, i[r].l = 1 * new Date();
	a = s.createElement(o), m = s.getElementsByTagName(o)[0];
	a.async = 1;
	a.src = g;
	m.parentNode.insertBefore(a, m)
})(window, document, 'script', '//www.google-analytics.com/analytics.js',
'ganew');

ganew('create', 'UA-2626751-1', 'auto');
ganew('send', 'pageview');