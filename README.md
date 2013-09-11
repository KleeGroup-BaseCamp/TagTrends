TagTrends : Analyse du réseau Twitter
=========  
   
.

Cette application vous donne accès aux résultats d'analyses obtenus à partir de tweets récents, et vous permet de décrypter visuellement l'actualité.  Vous trouverez ici une description des possibilités offertes.

---------------
###Page de visualisation#

La page de visualisation affiche, pour chaque *#hashtag* rencontré, l'évolution au cours du temps de sa fréquence. Les graphes apparaissent par ordre décroissant de la fréquence moyenne de ce *#hashtag*. Deux informations supplémentaires sont accessibles à partir du bandeau surmontant chacun des graphes:

  - l'icône portant un nuage affiche le nuage de tags correspondant; vous y retrouverez donc, avec une taille proportionnelle à leur fréquence, les mots les plus souvent rencontrés dans les tweets portant ce *#hashtag*.
  - l'icône portant un diagramme donne accès à la proportion de tweets favorables et défavorables à un certain sujet. Afin de déterminer ce sujet, reportez-vous au paragraphe suivant.

--------------  
###Page d'apprentissage#

La page d'apprentissage permet d'entraîner le programme à classifier les tweets en deux catégories. Pour cela, vous devez d'abord classifier manuellement un certain nombre de textes de tweets dans ces deux catégories. En lançant un *New learning*, voici la procédure que vous aurez à suivre:

 - choisir un nom pour le sujet d'actualité que vous désirez étudier ("reforme", "interventionSyrie", ...).
 - définir le nombre de textes que vous désirez classifier manuellement. Plus élevé sera ce nombre, plus exacte sera la classification automatique résultante pour les tweets inconnus.
 - attribuer aux *n* tweets qui se présentent à vous l'une des étiquettes **For** (favorable / satisfait) ou **Against** (défavorable / insatisfait). Si aucune étiquette ne semble appropriée, choisissez **Skip** ; l'apprentissage continuera sans tenir compte de ce tweet.
   
Une fois l'apprentissage terminé, vous êtes invité à retourner sur la page de visualisation. Les icônes portant un diagramme permettent désormais de visualiser les statistiques liées au sujet que vous avez choisi. 

--------------
###Technologies utilisées#

Voici les technologies qui ont été choisies pour ce projet:

#####Recueil et analyse des données#
 - langage de programmation - Java
 - API de Twitter - Twitter4j 3.0.4
 - stockage de données - MongoDB 2.4.5
 - classification par apprentissage - Java-Naive-Bayes-Classifier

#####Communication serveur#
 - Grizzly

#####Interface et affichage des résultats#
 - AJAX
 - bibliothèque JQuery 2.0.0
 - framework Bootstrap 3.0.0
 - librairie graphique d3.js


-*TagTrends* -
  

    