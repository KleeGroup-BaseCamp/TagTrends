
function makeLearningRequest(url, options) {
    	options = options ||{};
    	options.RequestType = options.requestType; 
    	options.Success = options.success;
    	options.Topic = options.topic;
    	/*Realize an ajax request.*/
    	$.ajax({
            type: options.RequestType,//GET, POST ...
            url: url, //generates the url used to call the web service
            dataType: 'json',
            data: options.data,
            //Success callback
            success: function(response, text) {
            	serverSuccessIcon();
            	launchLearningSuccessAction(response, options.Success, options.Topic);
            },
            //Error callback
            error: function(request, status, error) {
            	serverErrorIcon();
            	launchLearningErrorAction(options.Success);
            }
		});
    }
     
    function launchLearningSuccessAction(response, success, topic){
    	textsToLearn = response;
    	requestSuccessIcon(); 
    	runLearningProcess();
    	
    }
    
    function launchLearningErrorAction(response, success){
    	requestErrorIcon();
    }