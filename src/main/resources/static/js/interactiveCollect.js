function initializeCollectionVariables(){
	collectionName = "exampleData";
	collectLength = null;
}

function showCollectDialog(){
  	bootbox.prompt("What name would you like to give to your collection? Write a word.", function(result) {                
	  if ( result === null) {
		// do nothing : the user has closed or cancelled
          initializeCollectionVariables();
  		}else if(result === ""){
	  		alert("You have to write something !");  
	  		showCollectDialog();
  		} else { 
	  		$('div#launchCollectButton').hide();  
          	collectionName = result;
          	showCollectLengthDialog();
   		}
  	});}

function showCollectLengthDialog(){
	  bootbox.prompt("How long would you like to collect? Give a time between 5 and 120 (seconds).", function(result) { 
		  if ( result === null) {
	          // do nothing : the user has closed or cancelled
			  initializeCollectionVariables();
	  	}else if(result === ""){
		  	alert("You have to write something !"); 
		  	showCollectLengthDialog();
	  	} else { 
	  		if(result<121 && result>4){
	  			collectLength = result;
	  			launchCollectRequest(collectLength, collectionName);
	  		} else{
	  			alert("You have to write a number between 5 and 120 !"); 
	  			showCollectLengthDialog();
	  		}
	  	}                
	  });}



function displayAnalysisEndMessage(numberOfDistinctHashtags){
	bootbox.alert("The data you have collected includes "+numberOfDistinctHashtags+" different hashtags. You can now see the results.", displayGraphic());
	  }