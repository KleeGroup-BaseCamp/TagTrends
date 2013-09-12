function displayGraphic(){
	$('div.content').hide();
	$('div#collectionTitle').html("<h2>Current collection : "+collectionName+"</h2>\
			<h6>If you wish to change it, choose 'New collect'.</h6>");
	$('div#graphicContainer').show();
	makeGraphicRequest('../myResourceGraph', {requestType: 'POST', data: "collection="+collectionName, success: 'graph'});
}

function displayLearning(){
	$('div.content').hide();
	initializeLearningVariables();
	showLearningTopicDialog();
}

function displayCollect(){
	$('div.content').hide();
	initializeCollectionVariables();
	showCollectDialog();
}

function displayTheTagTrendsProject(){
	$('div.content').hide();
	$('div#theTagTrendsProjectContainer').show();
	$('div#theTagTrendsProjectContainer').html(theTagTrendsProjectText);
}

function displayFunctionnalities(){
	$('div.content').hide();
	$('div#functionnalitiesContainer').show();
	$('div#functionnalitiesContainer').html(functionnalitiesText);
}

function displayTechnologies(){
	$('div.content').hide();
	$('div#technologiesContainer').show();
	$('div#technologiesContainer').html(technologiesText);
}



var theTagTrendsProjectText = "<h1 class='title'>TagTrends : Analyse du r&eacuteseau Twitter</h1>\
<p>L'objectif de ce projet est de parvenir &agrave extraire des informations sur l'actualit&eacute &agrave partir de flux collect&eacutes sur internet. En partant du concept de <b>#tag</b>, on parvient &agrave isoler les th&egravemes d'int&eacuter&ecirct.</p>\
<p>Cette application exploite l\'API propos&eacutee par Twitter. Elle donne acc&egraves aux r&eacutesultats obtenus par l\'analyse de tweets r&eacutecents, et vous permet de d&eacutecrypter l\'actualit&eacute sur un mode visuel.</p>\
<p>Vous trouverez dans l'onglet <i>About</i> une description des <b>fonctionnalit&eacutes</b> offertes ainsi que la liste des <b>technologies</b> utilis&eacutees.</p>";

var technologiesText = "<h1 class='title'>Les technologies utilis&eacutees</h1>\
<h4>Recueil et analyse des donn&eacutees</h4>\
<ul><li>langage de programmation - Java</li><li>API de Twitter - Twitter4j 3.0.4</li><li>stockage de donn&eacutees - MongoDB 2.4.5</li><li>classification par apprentissage - <a href='https://github.com/ptnplanet/Java-Naive-Bayes-Classifier.git'>Java-Naive-Bayes-Classifier</a></li></ul>\
</br><h4>Communication serveur</h4>\
<ul><li>Grizzly 1.3.5</li><li>Jersey 1.17</li></ul>\
</br><h4>Interface et affichage des r&eacutesultats</h4>\
<ul><li>AJAX</li><li>API de Twitter - Twitter4j 3.0.4</li><li>biblioth&egraveque JQuery 2.0.0</li><li>framework Bootstrap 3.0.0</li><li>librairie graphique d3.js</li></ul>";

var functionnalitiesText ="<h1 class='title'>Les fonctionnalit&eacutes de cette application</h1>\
</br><h3>Page de visualisation</h3>\
La page de visualisation affiche, pour chaque <i>#hashtag</i> rencontr&eacute, l'&eacutevolution au cours du temps de sa fr&eacutequence. Les graphes apparaissent par ordre d&eacutecroissant de la fr&eacutequence moyenne de ce <i>#hashtag</i>. Deux informations suppl&eacutementaires sont accessibles &agrave partir du bandeau surmontant chacun des graphes:\
</br></br><ul>\
 <li>l'ic&ocircne portant un nuage affiche le nuage de tags correspondant; vous y retrouverez donc, avec une taille proportionnelle &agrave leur fr&eacutequence, les mots les plus souvent rencontr&eacutes dans les tweets portant ce <i>#hashtag</i>.</li>\
  <li>l'ic&ocircne portant un diagramme donne acc&egraves &agrave la proportion de tweets favorables et d&eacutefavorables &agrave un certain sujet. Afin de d&eacuteterminer ce sujet, reportez-vous au paragraphe suivant.</li>\
</ul>\
</br><h3>Page d'apprentissage</h3>\
La page d'apprentissage permet d'entra&icircner le programme &agrave classifier les tweets en deux cat&eacutegories. Pour cela, vous devez d'abord classifier manuellement un certain nombre de textes de tweets dans ces deux cat&eacutegories. En lan&ccedilant un <i>New learning</i>, voici la proc&eacutedure que vous aurez &agrave suivre:\
</br></br><ul>\
 <li>choisir un nom pour le sujet d'actualit&eacute que vous d&eacutesirez &eacutetudier ('reforme', 'interventionSyrie', ...).</li>\
  <li>d&eacutefinir le nombre de textes que vous d&eacutesirez classifier manuellement. Plus &eacutelev&eacute sera ce nombre, plus exacte sera la classification automatique r&eacutesultante pour les tweets inconnus.</li>\
  <li>attribuer aux <i>n</i> tweets qui se pr&eacutesentent &agrave vous l'une des &eacutetiquettes <b>For</b> (favorable / satisfait) ou <b>Against</b> (d&eacutefavorable / insatisfait). Si aucune &eacutetiquette ne semble appropri&eacutee, choisissez <b>Skip</b> ; l'apprentissage continuera sans tenir compte de ce tweet.</li>\
</ul>\
</br>Une fois l\'apprentissage termin&eacute, vous &ecirctes invit&eacute &agrave retourner sur la page de visualisation. Les ic&ocircnes portant un diagramme permettent d&eacutesormais de visualiser les statistiques li&eacutees au sujet que vous avez choisi.</br>\
</br><h3>Page de collecte</h3>\
Jusqu'ici, toutes les analyses sont faites sur des donn&eacutees datant de septembre 2013, stock&eacutees dans la <b>collection par d&eacutefaut</b>: <i>exampleData</i>. Afin de visualiser des donn&eacutees tout &agrave fait <b>actuelles</b>, vous pouvez effectuer votre propre collecte. Il suffit pour cela de choisir <i>New collect</i>. Une bo&icircte de dialogue vous permet alors:\
</br></br><ul><li>choisir un nom pour votre collection.</li><li>d&eacutefinir le nombre de secondes d'&eacutecoute de Twitter.</li></ul>\
</br><p>Une fois la collecte termin&eacutee, vous pouvez prendre connaissance du nombre de tweets re&ccedilus ainsi que du nombre de hashtags distincts rencontr&eacutes. Vous &ecirctes ensuite redirig&eacute vers la page de visualisation. Les graphes correspondent d&eacutesormais aux tweets que vous venez de collecter.</p>\
</br><p>Notez que vous pouvez <b>cumuler</b> les tweets de plusieurs collectes; il suffit pour cela d'entrer un nom de collection identique &agrave une collecte pr&eacutec&eacutedente. Cependant, les donn&eacutees ne seront conserv&eacutees que 24 heures.</p>";
