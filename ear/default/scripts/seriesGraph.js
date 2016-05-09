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

	if (graphConfig.width !== undefined) {
		width = graphConfig.width  - margin.left - margin.right;
	}

	if (graphConfig.height !== undefined) {
		height = graphConfig.height  - margin.left - margin.right;
	}

	var bodyRect = document.body.getBoundingClientRect(),
	offsetV   = host.top - bodyRect.top, 	// distance from the top of the window to the top of the svg
	offsetH = host.left - bodyRect.left;   // distance from the left of the window to the left of the svg

	addButtons();

	var buttonHeight = d3.select("#buttonBar").node().offsetHeight;

	var x = d3.scale.linear()
	.range([0, width]);

	var ranking_y = d3.scale.linear()
	.range([0, height-buttonHeight]);

	var color = d3.scale.category10();



	var yAxis = d3.svg.axis()
	.scale(ranking_y)
	.orient("left")
	.tickValues([1,5,10,15,20,25,30]);

	var line = d3.svg.line()
	.interpolate("linear")
	.x(function(d) { return x(d.round); })
	.y(function(d) { return ranking_y(d.ranking); });


	var svg = d3.select("#chart").append("svg")
	.attr("width", width + margin.left + margin.right)
	.attr("height", height + margin.top + margin.bottom + buttonHeight)
	.append("g")
	.attr("transform", "translate(" + margin.left + "," + margin.top + ")")
	
	d3.selectAll(".tooltip").remove();
	d3.selectAll(".blender").remove();
	
	var blender = d3.select("body").append("div")
	.attr("class","blender")
	.style("position","absolute")
	.style("opacity", 0)
	.style("z-index",100)
	.style("background", "#FFFFFF")
	.style("border", "#00ff00")
	.style("border-style", "solid")
	.style("border-width", "0px")
	.style("height", "50px")
	.style("width", "2px");  // hardcoded as 2px in graph.css
	
	var popup = d3.select("body").append("div")
	.attr("class","tooltip")
	.style("position","absolute")
	.style("z-index",50)
	.style("opacity", 0);


	




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

		if (pos == null) {
			if (graphConfig.startPos !== null) {
				pos = graphConfig.startPos
			} else {
				pos = getURLParameter('pos');

				if (!(/^\d$/.test(pos)))  {
					pos = 4;
				}
			}
		} else {
			ganew('send', 'event', graphConfig.client, 'click', pos, '1');		
		}

		var button = document.getElementById(pos);

		if (button !== null) {
			button.className = "btn btn-danger disabled";
		}

		if (compId == null) {
			if (graphConfig.compId !== null) {
				compId = graphConfig.compId
			} else {
				compId = getURLParameter('compId');

				if (!(/^\d$/.test(pos)))  {
					compId = 5095957750349824;
				}
			}
		}

		var url = "https://fantasyrugbyengine-hrd-prod.appspot.com/_ah/api/topten/v1/series/position/get?compId=" + compId + "&position=" + pos;
		if (window.location.href !== undefined && window.location.href.indexOf("127.0.0.1") !== -1) {
			url = "/_ah/api/topten/v1/series/position/get?compId=" + compId + "&position=" + pos;
		} else if (window.location.href !== undefined &&  window.location.href.indexOf("dev.rugby.net") !== -1) {
			url = "https://fantasyrugbyengine-hrd.appspot.com/_ah/api/topten/v1/series/position/get?compId=" + compId + "&position=" + pos;		
		}

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

			var numTicks = 5;
			if (graphConfig.ticks !== null) {
				numTicks = graphConfig.ticks;
			}


			// we only care about the names of the players in the most recent round
			var keys = d3.keys(playerLists[0].values); //map(function(d) { return d3.keys(d)});

			color.domain(keys);


			var players = color.domain().map(function(name) {
				return {
					name: name,
					values: playerLists.map(function(d) {
						var retval = {round: d.round};  
						if (name in d.values) {
							retval = {round: d.round, rating: +d.values[name][0].rating, ranking: +d.values[name][0].ranking, teamAbbr: d.values[name][0].teamAbbr, label: d.values[name][0].label, homeTeamAbbr: d.values[name][0].homeTeamAbbr, visitingTeamAbbr: d.values[name][0].visitingTeamAbbr, homeTeamScore: d.values[name][0].homeTeamScore, visitingTeamScore: d.values[name][0].visitingTeamScore, matchDate: d.values[name][0].matchDate, position: d.values[name][0].position, matchRating: d.values[name][0].matchRating, minutesPlayed: d.values[name][0].minutesPlayed, notes: d.values[name][0].notes };
						}
						return retval;      })
				};
			});

			x.domain(d3.extent(data.items, function(d) { return d.round; }));

			var xAxis = d3.svg.axis()
			.scale(x)
			.orient("bottom")
			.ticks(numTicks)
			.tickFormat(function(d,i) { 
				var val = xLabs[d];
				return val;
			});

			ranking_y.domain([
			                  d3.min(players, function(c) { return d3.min(c.values, function(v) { return v.ranking; }); }),
			                  d3.max(players, function(c) { return d3.max(c.values, function(v) { return v.ranking; }); })
			                  ]);

			svg.selectAll(".axis").remove();
			var translateAmount = height - buttonHeight;
			svg.append("g")
			.attr("class", "x axis")
			.attr("transform", "translate(0," + translateAmount + ")")
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

			svg.select(".pointyBit").remove();

			var pointyBit = svg.append("polygon")
			.attr("class","pointyBit")
			.attr("stroke","darkgrey")
			.attr("fill","white")
			.attr("stroke-width",2)
			.style("opacity",0);	
			
			point.selectAll("circle")
			.data(function(d,i) { return d.values;})
			.enter().append("circle")
			.attr("transform", function(d,i) { return "translate(" + x(d.round) + "," + ranking_y(d.ranking) + ")"; })
			.attr("r",5) 		
			.attr("class", "point-out")
			.on("mouseover",  function(d,i) { mouseover(x(d.round),ranking_y(d.ranking), this, pointyBit); } )
			.on("mouseout", function() { mouseout(this, pointyBit); });

			function mouseover(px, py, _this, pointyBit) {


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



				var html = "<div style=\"border-bottom-style:solid;border-bottom-width:1px;display:inline-block;width:100%;padding-bottom:10px;\"><div class=\"teamlogo-small " + _this.__data__.teamAbbr + "\" style=\"width:60px;height:50px;float:left;display:inline-block;padding-left:25px;\"></div><div style=\"display:inline-block;padding-top:15px;font-size:1.3em;\">" + _this.__data__.ranking + ". " + _this.parentNode.__data__.name + " (" + _this.__data__.rating + ")</div></div>"
				+ "<div style=\"border-bottom-style:solid;border-bottom-width:1px;\"><div class=\"teamlogo-small " + _this.__data__.homeTeamAbbr + "\" style=\"width:65px;height:65px;float:left;display:inline-block;padding-left:15px;padding-top:15px;\"></div><div style=\"display:inline-block;\"> <h3>" + _this.__data__.homeTeamScore + " - " + _this.__data__.visitingTeamScore + "</h3></div><div class=\"teamlogo-small " + _this.__data__.visitingTeamAbbr + "\" style=\"width:65px;height:65px;float:right;display:inline-block;padding-right:15px;padding-top:15px;\"></div>"
				+ "<div style=\"padding-bottom:20px;\"><br/><b>" + _this.__data__.label + "</b><br/>" + _this.__data__.matchDate + "</div></div>" 
				+ "<div style=\"border-bottom-style:solid;border-bottom-width:1px;min-height:80px;\"><div style=\"width:60px;height:60px;float:left;display:inline-block;padding-left:10px;padding-top:15px;text-align:left;\">Position:<br/><img class=\"" + _this.__data__.position + "\" style=\"width:100%;height:100%;\"></div><div style=\"display:inline-block;border-style: solid;border-width: 1px;padding:10px;margin:0px;background-color:#efefef;\"> <h4>Match Rating</h4><h4>" + _this.__data__.matchRating + "</h4></div><div style=\"width:60px;height:60px;float:right;display:inline-block;padding-right:15px;padding-top:15px;border-left:1px;\">Minutes:<h4>" + _this.__data__.minutesPlayed + "</h4></div></div>"
				+ "<div>" + buildNotes(_this.__data__.notes) + "</div>";
				popup.html(html);	


				popup.transition()		
				.duration(200)		
				.style("opacity", .9)
				.style("left", function() { return getPopupLeft(d3.event.pageX) + "px";})   // + "px")		
				.style("top",  function() { return getPopupTop(d3.event.pageY) + "px";})  // ( - 28) + "px");			
//
//				.style("left", function() { return getPopupLeft(px) + "px";})   //(d3.event.pageX) + "px")		
//				.style("top",  function() { return getPopupTop(py) + "px";})  // (d3.event.pageY - 28) + "px");			

				console.log("Move popup for click at SVG(" + px + "," + py + ") WIN(" + d3.event.pageX + "," + d3.event.pageY + ")to topW " + getPopupTop(d3.event.pageY) + " and leftW " + getPopupLeft(d3.event.pageX));
//				.style("left", function() { return getPopupLeft(d3.select("#chart")) + "px";})   //(d3.event.pageX) + "px")		
//				.style("top",  function() { return getPopupTop(d3.select("#chart")) + "px";})  // (d3.event.pageY - 28) + "px");			

				
				//pointyBit.attr("points", getPointyBitsCoords(d3.event.pageX, d3.event.pageY, px, py));

				pointyBit.transition()	
				.duration(200)	
				.attr("points", getPointyBitsCoords(d3.event.pageX, d3.event.pageY, px, py))
				.style("opacity", .9);	

				blender.transition()		
				.duration(200)		
				.style("opacity", 1)
				.style("left", function() { return getBlenderLeft(d3.event.pageX) + "px";})
				.style("top",  function() { return getBlenderTop(d3.event.pageY) + "px";});	
			}

			function mouseout(_this, pointyBit) { 
				d3.select(_this.parentNode)
				.selectAll(".point-on")
				.attr("class", "point-out")

				d3.select(_this)
				.attr("class","point-out")

				popup.transition()		
				.duration(500)		
				.style("opacity", 0);	

				pointyBit.transition()
				.duration(500)
				.style("opacity", 0);

				blender.transition()
				.duration(500)
				.style("opacity", 0);
			}

			
			const PU_HEIGHT = 350;  // in graph.css
			const PU_WIDTH = 275; // in graph.css
			const PU_MARGIN = 5;
			const PU_HORZ_OFFSET = 70;
			const PU_VERT_OFFSET = 28;

			function isHighY(py) { return (py < height/2) }
			function isMidY(py) { return (py < height/1.3) }
			function isLowY(py) { return (py >= height/1.3) }
			function isLeftX(px) { return  (px < width/2) }
			function isRightX(px) { return (px >= width/2) }
			
			// this is in window coords (not svg)
			function getPopupTop(py) {
				// grrrph
				// if we are above the midpoint of the range put it 28 px from the point of the event, if below 
				//py = d3.event.pageY;
				if (isHighY(py)) {
					return (py + PU_VERT_OFFSET);	
				} else if (isMidY(py)) {
					return (py - PU_HEIGHT/2 + PU_VERT_OFFSET);  // chart height of 350 hardcoded in graph.css
				} else {
					return (py - PU_HEIGHT + PU_VERT_OFFSET);
				}
			}

			// this is in window coords (not svg)
			function getPopupLeft(px) {
				//px = d3.event.pageX;
				if (isLeftX(px))
					return (px + PU_HORZ_OFFSET);	
				else {
					return (px - PU_WIDTH - PU_HORZ_OFFSET);
				}
			}

			const PB_OFFSET_VERT = 100; // distance down to attach from top edge of popup
			const PB_WIDTH = 50; //width at attachment point

			// pointyBits are in svg coords, unlike the popup/tooltip and blender. Comfruzing!
			function getPointyBitsCoords(evx, evy, px,py) {
				var top = getPopupTop(evy) -offsetV + PB_OFFSET_VERT - buttonHeight - margin.top;
				var bottom = top;
				var x_side = getPopupLeft(evx) - offsetH - margin.left + PU_MARGIN + 1; //convert back to svg coords

				if (isRightX(evx)) {
					x_side  += PU_WIDTH-2;
				}
				
				if (isMidY(evy)) {
					bottom += PB_WIDTH/2;
				} else {
					bottom += PB_WIDTH;
				}
				
				var nubX = px-offsetH,
				nubY = py-offsetV;
			
				var coords = x_side  + "," + top + " " + x_side + "," + bottom + " " + px + "," + py;
				console.log("pointyBits coords: " + coords);
				return coords;
			}

			function getBlenderTop(py) {
				if (isMidY(py)) {
					d3.select(".blender")
					.style("height",PB_WIDTH/2 + "px");
				} else {
					d3.select(".blender")
					.style("height",PB_WIDTH  + "px");
				}
				return getPopupTop(py) + PB_OFFSET_VERT;
			}

			function getBlenderLeft(px) {
				var x_side = getPopupLeft(px) + PU_MARGIN;

				if (isRightX(px)) {
					x_side  += PU_WIDTH-2;
				}
				return x_side;
			}
			
			
			function buildNotes(notes) {
				var retval = "<div style=\"float: left;display:inline-block;padding-left:0px;width: 45%;font-size:.85em;text-align:left;\"><ul>",
				count = 0;
				if (notes !== undefined) {
					notes.split("|").forEach( function(note) {
						if (note !== undefined && note !== "") {
							retval += "<li>" + note + "</li>";
							count++;
							if (count > 2) {
								retval += "</ul></div><div style=\"float: right;display:inline-block;padding-right:0px;width: 45%;font-size:.85em;;text-align:left;\"><ul>";
								count = 0;
							}
						}
					});
				}

				return retval + "</ul></div>";
			}
		});
	}

	function addButtons() {

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

//	d3.select(window).on('resize', resize); 

	function resize() {
		var chart = d3.select('#chart');
		var container = d3.select(chart.node().parentNode);
		// update width
		width = parseInt(container.style('width'), 10);
		width = width - margin.left - margin.right;

		//update height
		height = parseInt(container.style('height'), 10);
		height = height - margin.left - margin.right;

		// resize the chart
		x.range([0, width]);
		//d3.select(chart.node())
		d3.select("svg").attr('height', height)
		.attr('width', width);

		updateData();


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