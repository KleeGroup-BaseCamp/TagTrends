function displayGraphic(){
	$('div.content').hide();
	$('div#graphContainer').show();
	makeGraphicRequest('../myResourceGraph', {requestType: 'GET', success: 'graph'});
}

function displayLearning(){
	$('div.content').hide();
	initializeLearningVariables();
	//$('div.message').html('New learning starting');
	$('div#learningContainer').show();
}

function displayAbout(){
	$('div.content').hide();
	$('div#aboutContainer').show();
}