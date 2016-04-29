// load our stylesheets & libraries
function loadjscssfile(filename, filetype){
    if (filetype=="js"){ //if filename is a external JavaScript file
        var fileref=document.createElement('script')
        fileref.setAttribute("type","text/javascript")
        fileref.setAttribute("src", filename)
    }
    else if (filetype=="css"){ //if filename is an external CSS file
        var fileref=document.createElement("link")
        fileref.setAttribute("rel", "stylesheet")
        fileref.setAttribute("type", "text/css")
        fileref.setAttribute("href", filename)
    }
    if (typeof fileref!="undefined")
        document.getElementsByTagName("head")[0].appendChild(fileref)
}

function TRN_load() {
	loadjscssfile("https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.2.0/css/bootstrap.css", "css");
	loadjscssfile("http://www.rugby.net/stylesheets/therugbynet.css", "css");
	loadjscssfile("http://www.rugby.net/stylesheets/home.css", "css");
	loadjscssfile("/stylesheets/graph.css", "css");
}

require.config({
    paths: {
        d3: "https://cdnjs.cloudflare.com/ajax/libs/d3/3.4.1/d3.min",
        spin: "https://cdnjs.cloudflare.com/ajax/libs/spin.js/2.0.1/spin.min",
		graph: "/scripts/seriesGraph"
    }
});

TRN_load();
require(["graph"], function(graph) {
    
});

