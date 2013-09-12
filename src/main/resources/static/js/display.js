function displayGraphic(){
	$('div.content').hide();
	$('div#collectionTitle').html("<h2>Current collection : "+collectionName+"</h2>\
			<h6>I you wish to change it, choose 'New collect'.</h6>");
	$('div#graphicContainer').show();
	makeGraphicRequest('../myResourceGraph', {requestType: 'POST', data: "collection="+collectionName, success: 'graph'});
}

function displayLearning(){
	$('div.content').hide();
	initializeLearningVariables();
	showLearningTopicDialog();
}

function displayAbout(){
	$('div.content').hide();
	$('div#aboutContainer').show();
}

function displayCollect(){
	$('div.content').hide();
	initializeCollectionVariables();
	showCollectDialog();
}