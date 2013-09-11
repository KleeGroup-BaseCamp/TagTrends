 function cloudRequest(button){
	 	serverWaitingIcon();
    	console.log('button', button);
    	var htag = button.id.substr(11);
    	serverWaitingIcon();
    	makeGraphicRequest('../myResourceCloud', {requestType: "POST", data: "hashtag="+htag, hashtag: htag, success: "cloud"});
    }
 
 function statsRequest(button){
	 serverWaitingIcon();
 	console.log('button', button);
 	var htag = button.id.substr(11);
 	serverWaitingIcon();
 	makeGraphicRequest('../myResourceStats', {requestType: "POST", data: "hashtag="+htag+"&learntData="+JSON.stringify(textsToLearn)+"&topic="+learningTopic, hashtag: htag, success: "stats", timeout: 8000});
 }
 
function makeGraphicRequest(url, options) {
    	options.RequestType = options.requestType; 
    	options.Success = options.success;
    	options.Hashtag = options.hashtag;
    	/*Realize an ajax request.*/
    	$.ajax({
            type: options.RequestType,//GET, POST ...
            url: url, //generates the url used to call the web service
            dataType: 'json',
            data: options.data,
            //Success callback
            success: function(response, text) {
            	serverSuccessIcon();
            	launchGraphicSuccessAction(response, options.Success, options.Hashtag);
            },
            //Error callback
            error: function(request, status, error) {
            	serverErrorIcon();
            	launchGraphicErrorAction(options.Success);
            }
		});
    }
     
    function launchGraphicSuccessAction(response, success, hashtag){
    	if (success == "graph") { 
    		$('div#graphContainer').empty();
    		drawGraph(response, 'div#graphContainer');
    	} else if (success == "cloud") { 
    		$('div#cloudContainer'+hashtag).empty();
    		drawCloud(response, 'div#cloudContainer'+hashtag); 
    		$('button#cloudButton'+hashtag).popover({
    			placement:'right',
    			html: true,
    			title: '#'+hashtag,
    			content: $('div#cloudContainer'+hashtag).html(),
    			trigger: 'manual',
    			delay: { show: 0, hide: 3000 }
    		}).popover('toggle').popover('toggle');
    	} else if (success == "stats") {
    		$('div#statsContainer'+hashtag).empty();
    		if (response.debate !== null){
    			renderStatsChart(response.debate, 'div#statsContainer'+hashtag);
    		}
    		$('button#statsButton'+hashtag).popover({
    			placement:'bottom',
    			html: true,
    			title: response.topic,
    			content: $('div#statsContainer'+hashtag).html(),
    			trigger: 'manual',
    			delay: { show: 0, hide: 3000 }
    		}).popover('toggle').popover('toggle');
    	}
    	requestSuccessIcon(); 
    }
    
    function launchGraphicErrorAction(response, success){
    	if (success == "graph") { 
    		var staticGraphData =  [{"info": [ {"time" :"1377265409000", "frequency":52, "semantics":2}  , {"time" :"1377265469000", "frequency":40, "semantics":3},{"time" :"1377265529000", "frequency":25, "semantics":-1}], "hashtag" : "FF"},
                                    { "info": [{"time" :"1377265409000", "frequency":46, "semantics":-7}  , {"time" :"1377265469000", "frequency":14, "semantics":8},{"time" :"1377265529000", "frequency":11, "semantics":2}], "hashtag" : "RT"}]   ;
    		drawGraph(staticGraphData, 'div#graphContainer');
    	} else if (success == "cloud"){ 
    		var staticCloudData = {"hashtag":"retraites", "cloud":[{ "tag" : "moreau" , "size" : 51} , { "tag" : "retraites" , "size" : 232} , { "tag" : "ose" , "size" : 11} , { "tag" : "imaginer" , "size" : 10} , { "tag" : "si" , "size" : 7} , 
            	                                                { "tag" : "assez" , "size" : 1} , { "tag" : "sarko" , "size" : 81}]}; 
    		$('div#cloudContainerretraites').empty();
    		drawCloud(staticCloudData.cloud, 'div#cloudContainerretraites');
    		$('div#graphretraites').popover({
    			html: true,
    			title: '#retraites',
    			content: $('div#cloudContainerretraites').html(),
    			trigger: 'manual'
    		}).popover('toggle');
    	} else if (success == "stats"){ 
    		var staticOpinionData = {"topic" : "interventionSyrie", "debate" : [{"opinion": "for", "rate": 20},{"opinion": "against", "rate": 5}] };
        		$('div#error').empty();
       		renderStatsChart(opinionData, "div#error");
       	}
    	requestErrorIcon();
    }