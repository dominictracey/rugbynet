var g_confgData = null;

$.ajax({
      url: "/_ah/api/topten/v1/configuration",

      success: function( data ) {
    	g_configData = data;
        var content = "<div style=\"width: 100%;\"><ul class id=\"dashboard-menu\">";
        var topContent = "";
		var compMap = data["competitionMap"];
		var seriesMap = data["seriesMap"];
		var ratingWording = {"BY_COMP": "By Round", "BY_POSITION": "By Position",
			"BY_COUNTRY": "By Country", "BY_TEAM": "By Team", "BY_MATCH": "By Match"};
		var secondOrLater = false;
		var toggleId = 1;
		for(var compId in compMap) {
			var compName = compMap[compId];
			content += "<li class";
			if(secondOrLater == false) {
				content += "=\"active\"";
			}
			content += "><a href=\"javascript:;\" class=\"dropdown-toggle\" id=\"trn-toggle-"
				+ toggleId + "\">"
				+ "<i class=\"fa fa-globe\"></i><span>"
				+ compName + "</span><b class=\"caret\"></b></a><ul class=\"submenu\""
				+ " style=\"display: ";
			
			if(secondOrLater == false) {	
				content += "block";
				} else {
				content += "none";
			}
			content += ";\"><li class=\"null\"><span></span><a href=\"/s/#Fx:c=" + compId + "\">Home</a></li>";
			
			topContent += "<div class=\"panel panel-default\"><div class=\"panel-heading\"><h4 class=\"panel-title\">"
				+ "<a data-toggle=\"collapse\" data-parent=\"#accordion\" href=\"#trn-top-" + toggleId + "\"";
			topContent += "\" class";
			if(secondOrLater == true) {	
				topContent += "=collapsed";
			}
			topContent += "><span class=\"glyphicon glyphicon-globe\"></span>"
				+ compName + "</a></h4></div><div id=\"trn-top-" + toggleId + "\" class=\"panel-collapse collapse";
			if(secondOrLater == false) {	
				topContent += " in";
			}
			topContent += "\"";
			if(secondOrLater == true) {	
				topContent += " style =\"height: 0px;\"";
			}
			topContent += "><div class=\"panel-body\"><table class=\"table\"><tr><td><a href=\"/s/#Fx:c=" 
				+ compId + "\">Home</a></td></tr>";

			toggleId++;
				
			var thisSeriesMap = seriesMap[compId];
			for(var rating in thisSeriesMap) {
				var pageJump = "href=\"/s/#Tx:c=" + compId + "&s=" + thisSeriesMap[rating] + "\"";
				content += "<li class=\"null\"><span></span><a " + pageJump + ">"
  				    	+ ratingWording[rating] + "</a></li>";
				topContent += "<tr><td><a " + pageJump + ">"
  				    	+ ratingWording[rating] + "</a></td></tr>";
			}
			content += "</ul>";
			if(secondOrLater == false) {
  				content += "<div class=\"pointer\"><div class=\"arrow\"></div><div class=\"arrow_border\"></div></div>";
  				secondOrLater = true;
  			}
  			content += "</li>";
  			topContent += "</table></div></div></div>";
		}
		content += "</ul></div>";
		$("#sidebar-nav").html(content);
		$("#accordion").html(topContent);
		for(var i=1;i<toggleId;i++) {
			var toggleNode = "#trn-toggle-" + i;
    		$(toggleNode).on('click', function (e) {
    			doDashboardMenuDropdownToggleClick(this, e);
			});
    		//Don't need to explicitly register the top nav click hander, but
    		//the unique IDs do seem to be necessary.
    		//toggleNode = "#trn-top-" + i;
    		//$(toggleNode).on('click', function (e) {
    		//	alert("huh?");
			//});    		
    	}
      }
    });

