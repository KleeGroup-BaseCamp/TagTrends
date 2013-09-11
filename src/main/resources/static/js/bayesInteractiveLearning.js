function initializeLearningVariables(){
	learningTopic='No topic yet';
  	learningLength=null;
  	textsToLearn=null;
  	effectivelyLearnt=0;
}

  function showLearningTopicDialog(){
  	bootbox.prompt("What topic would you like to focus on? Write a keyword.", function(result) {                
	  if ( result === null) {
          // do nothing : the user has closed or cancelled
  		}else if(result === ""){
	  		alert("You have to write something !");  
      		showLearningTopicDialog();
  		} else { 
	  		$('div#launchLearningButton').hide();  
          	learningTopic = result;
          	showLearningLengthDialog();
   		}
  	});}
  
  function showLearningLengthDialog(){
	  bootbox.prompt("How many tweet texts would you like to categorize? Write a number.", function(result) { 
		  if ( result === null) {
	          // do nothing : the user has closed or cancelled
	  	}else if(result === ""){
		  	alert("You have to write something !"); 
		  	showLearningLengthDialog();
	  	} else { 
		  	learningLength = result;
          	makeLearningRequest('../myResourceLearning', {requestType: "POST", data: "length="+learningLength, topic: learningTopic, success: "getDataToLearn"});
	  	}                
	  });}
  
  function runLearningProcess(){
	  if (textsToLearn !== null){
	  	for (var i=0;i<textsToLearn.length;i++){
		  categorize(i);
		 
	  	}
	  	<!-- the resulting classification will be returned via POST Ajax request when asking for a stats graph (visualization page)-->
	  	
	  } else { alert("This database seems to be empty ...");}
  }
  
  function categorize(i){
	  bootbox.dialog({
		  message: textsToLearn[i].text,
		  title: "Topic : "+learningTopic,
		  buttons: {
		    success: {
		      label: "For",
		      className: "btn-success",
		      callback: function() {
		    	  _.extend(textsToLearn[i], {"opinion" : "for"});
		    	  effectivelyLearnt++;
		    	  if (i==0){
					  displayEndMessage(effectivelyLearnt);
				  }
		      }
		    },
		    danger: {
		      label: "Against",
		      className: "btn-danger",
		      callback: function() {
		    	  _.extend(textsToLearn[i], {"opinion" : "against"});
		    	  effectivelyLearnt++;
		    	  if (i==0){
					  displayMessage(effectivelyLearnt);
				  }
		      }
		    },
		    main: {
		      label: "Skip",
		      className: "btn-primary",
		      callback: function() {
		         // do nothing
		    	  if (i==0){
					  displayEndMessage(effectivelyLearnt);
				  }
		      }
		    }
		  }
		});
  }
  
	  function displayEndMessage(numberOfClassifiedTexts){
		  $('div.message').html("Your "+numberOfClassifiedTexts+" categorizations have been taken into account. You can now go to the 'Visualization' page and see the results in the 'Stats' popover.");
	  }
  
  
 