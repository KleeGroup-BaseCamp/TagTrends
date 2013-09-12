 function launchCollectRequest(length, name){
	 	serverWaitingIcon();
    	makeCollectRequest('../myResourceCollect', {requestType: "POST", data: "length="+length+"&collection="+name, success: "collectData", timeout: length*2});
    }
 
 function launchAnalysisRequest(name){
	 	serverWaitingIcon();
 		makeCollectRequest('../myResourceAnalysis', {requestType: "POST", data: "collection="+name, success: "analyzeData", timeout: collectLength*2});
	}
 
function makeCollectRequest(url, options) {
    	options.RequestType = options.requestType; 
    	options.Success = options.success;
    	/*Realize an ajax request.*/
    	$.ajax({
            type: options.RequestType,//GET, POST ...
            url: url, //generates the url used to call the web service
            dataType: 'json',
            data: options.data,
            //Success callback
            success: function(response, text) {
            	serverSuccessIcon();
            	launchCollectSuccessAction(response, options.Success);
            },
            //Error callback
            error: function(request, status, error) {
            	serverErrorIcon();
            	launchCollectErrorAction(options.Success);
            }
		});
    }
     
    function launchCollectSuccessAction(response, success, hashtag){
    	if (success == "collectData") { 
    		bootbox.alert("End of the collect : "+response+" tweets stored ! Analysis can start.", function() {
    			launchAnalysisRequest(collectionName);});
    	} else if (success == "analyzeData") { 
    		displayAnalysisEndMessage(response);
    	} 
    	requestSuccessIcon(); 
    }
    
    function launchCollectErrorAction(response, success){
    	requestErrorIcon();
    }