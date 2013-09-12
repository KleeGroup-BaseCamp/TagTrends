/*Function to draw a bar chart graph.*/
function drawGraph(dataToDraw, container){
    container = container || "body";
    for (var i=0;i<dataToDraw.length;i++){ dataToDraw[i].info = _.sortBy(dataToDraw[i].info, function(o){return o.time;}); }
    
    /*Initialize the vizualization parameters.*/
    var margin = {top: 20, right: 20, bottom: 30, left: 40},
    width = 750 - margin.left - margin.right,
    height = 120 - margin.top - margin.bottom;
    rectWidth = 5;
    var hourFormat =  d3.time.format("%H:%M");
    var dateFormat =  d3.time.format("%c");
    var parameters = {width: width, height: height, rectWidth: rectWidth};
    
    /*Prepare identical scaling for all graphs.*/
    scalingData = dataToDraw[0];
    info = scalingData.info;
    var time_session = [];
    for(var k=0; k<info.length; k++){
    	time_session[k] = new Date(+info[k].time);
    }
    
    var x1 = d3.time.scale().domain(time_session).range([0,width]);
    var y = d3.scale.linear().range([height, 0]);

    var color = d3.scale.ordinal().domain([-1, 0, 1]).range(["#D73027", "#FEE08B", "#00FF00"]);
    
    var xAxis = d3.svg.axis()
				.scale(x1)
				.orient("bottom")
				.ticks(d3.time.minutes, 2)
				.tickFormat(hourFormat);
				
    var yAxis = d3.svg.axis()
				.scale(y)
				.orient("left")
				.ticks(5);
    
    
//    x1.domain(time_session); 
    y.domain([0, d3.max(info, function(d) { return d.frequency; })]);
   

    /* 1 graph for each hashtag.*/
    for (var k=0; k<dataToDraw.length; k++){
    		
    	/*Data consume by d3 js.*/
    	var data =  dataToDraw[k];
        
       	/*Prepare data for the graph.*/
    	var info = data.info;
	    _.extend(info, { hashtag: data['hashtag'], total: data['total']});
	    
		/*Create the graph environement.*/
	    $('div#hiddenCloudContainer').append('<div id="cloudContainer'+info.hashtag+'"></div>');
	    $('div#hiddenStatsContainer').append('<div id="statsContainer'+info.hashtag+'"></div>');
	  if(k%2 == 0){
	    $(container).append(
	    		'<div class="col-md-4 panel panel-primary"> \
	    			<div class="panel-heading"> \
	    				<div class="row"> \
	    					<div class="col-md-8"><span class="panel-title">#'+info.hashtag+' : '+info.total+'</span></div> \
	    					<div class="col-md-4"> \
	    						<button type="button" class="btn btn-default popoverButton" id="statsButton'+info.hashtag+'" onclick="statsRequest(this)"> \
	    							<span class="glyphicon glyphicon-stats"></span> \
	    						</button> \
	    						<button type="button" class="btn btn-default popoverButton" id="cloudButton'+info.hashtag+'" onclick="cloudRequest(this)"> \
	    							<span class="glyphicon glyphicon-cloud"></span> \
	    						</button> \
	    					</div> \
	    				</div> \
	    			</div> \
	    			<div class="panel-body oneGraph" id=graph'+info.hashtag+'></div> \
	    		</div>');  
	  } else {
		  $(container).append(
		    		'<div class="col-md-4 col-md-offset-3 panel panel-primary"> \
		    			<div class="panel-heading"> \
		    				<div class="row"> \
		    					<div class="col-md-8"><span class="panel-title">#'+info.hashtag+' : '+info.total+'</span></div> \
		    					<div class="col-md-4"> \
		    						<button type="button" class="btn btn-default popoverButton" id="statsButton'+info.hashtag+'" onclick="statsRequest(this)"> \
		    							<span class="glyphicon glyphicon-stats"></span> \
		    						</button> \
		    						<button type="button" class="btn btn-default popoverButton" id="cloudButton'+info.hashtag+'" onclick="cloudRequest(this)"> \
		    							<span class="glyphicon glyphicon-cloud"></span> \
		    						</button> \
		    					</div> \
		    				</div> \
		    			</div> \
		    			<div class="panel-body oneGraph" id=graph'+info.hashtag+'></div> \
		    		</div>');  
		  }
	   
		/* Draw the graph container */
		var svg = d3.select('div#graph'+info.hashtag)
	    			.append("svg")
	    			.attr("width", width + margin.left + margin.right)
	    			.attr("height", height + margin.top + margin.bottom)
	    			.append("g")
	    			.attr("transform", "translate(" + margin.left + "," + (margin.top + 20) + ")");
	      
	      /* draw the y-axis */
	      svg.append("g")
        	.attr("class", "y axis")
        	.call(yAxis);
	    	
	    	/* Build barContainers linked to pieces of info */
	    var barContainer = svg.selectAll("barContainer")
	       						.data(info)
	       						.enter()
	       						.append("g")
	       						.attr("class", "barContainer")
	       						.attr("transform", function(d,i){ return "translate(" + i*rectWidth + ",0)";})
	       			/* create interaction on bars */
	       						.on("mouseover", function(){   
	       							d3.select(this)
	       							.selectAll("text")
	       							.style("visibility","visible");
	       						})
	       						.on("mouseout", function(){ 
	       							d3.select(this)
	       							.selectAll("text")
	       							.style("visibility","hidden");
	       						});
	       						
	      
	      barContainer.each(
	    	  function(d,i){
	    		  /* draw dimensionned rect for the bar */
	    		    d3.select(this)
	    		      .append("rect")
	    	          .attr("class", "rect")
	    	          .attr("width", rectWidth) // width of 1 bar
	    	          .attr("x", function(d, i) { return i*rectWidth; })//x1(d.time);})
	    	          .attr("y", function(d) { return y(d.frequency); })
	    	          .attr("height", function(d) { return height - y(d.frequency); })
	    	          .style("fill", function(d) { 
	    	        	  s = d.semantics;
	    	        	  if (s<0) return color(-1);
	    	        	  else if (s>0) return color(1);
	    	        	  else return color(0);
	    	        	});
	    		    
	    		  /* create interactive bar labels */
	    		  d3.select(this)
	    		    .append("text")
	    	        .attr("x", function(d, i) { return - 4;})
	    	        .attr("y", function(d) { return y(d.frequency) - 10; })
	    	        .attr("dx", 3) // padding-left
	    	        .attr("dy", ".35em") // vertical-align: middle
	    	        .attr("text-anchor", "start") // text-align: right
	    	        .attr("fill", "black")
	    	        .style("visibility","hidden")
	    	        .text(function(d) { return "f. "+d.frequency; });
	    		  
	    		  d3.select(this)
	    		    .append("text")
	    	        .attr("x", function(d, i) { return -15;})
	    	        .attr("y", function(d) { return y(d.frequency) - 25; })
	    	        .attr("dx", 3) // padding-left
	    	        .attr("dy", ".35em") // vertical-align: middle
	    	        .attr("text-anchor", "start") // text-align: right
	    	        .attr("fill", "black")
	    	        .style("visibility","hidden")
	    	        .text(function(d) { return hourFormat(new Date(+d.time)); });
	    		  
	    		  d3.select(this)
	    		    .append("text")
	    	        .attr("x", function(d, i) { return -15;})
	    	        .attr("y", function(d) { return y(d.frequency) - 40; })
	    	        .attr("dx", 3) // padding-left
	    	        .attr("dy", ".35em") // vertical-align: middle
	    	        .attr("text-anchor", "start") // text-align: right
	    	        .attr("fill", "black")
	    	        .style("visibility","hidden")
	    	        .text(function(d) { return "sem. "+d.semantics; });
	    	  }
	      );

	 }

}



///* regularly draw the legend */
//if(k%12==1){
//	  
//	  /*Add metadata to the graph*/
//  	svg.attr('class', 'legendSvg');
//  	
//  	var legend = svg.append("g")
//      .attr("class", "legend")
//      .attr("height", 100)
//      .attr("width", 100)
//      .attr("transform", "translate(-200,0)");
//      
//  legend.selectAll('rect')
//      .data([-1,0,1])
//      .enter()
//      .append("rect")
//      .attr("x", parameters.width - 18)
//      .attr("transform", function(d, i) { 
//      	return "translate(0," + i * 20 + ")"; 
//    	})
//      .attr("width", 10)
//      .attr("height", 10)
//      .style("fill", function(d) {
//          return color(d);
//      });
//
//  legend.selectAll('text')
//      .data([-1,0,1])
//      .enter()
//      .append("text")
//      .attr("x", parameters.width - 24)
//    	.attr("y", 9)
//    	.attr("transform", function(d, i) { 
//    		return "translate(0," + i * 20 + ")"; 
//    	})
//    	.attr("dy", ".35em")
//    	.style("text-anchor", "end")
//      .text(function(d) {
//          return d;
//      });
//
//  legend.append("text")
//  .attr("transform", "translate(400,-25)")
//  	.attr("y", 6)
//  	.attr("dy", ".71em")
//  	.style("text-anchor", "start")
//  	.style("font-size", "12px")
//  .text("Semantics");
//	  
//}

