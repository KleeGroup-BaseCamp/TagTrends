/*Function to draw a bar chart graph.*/
    function drawGraph(dataToDraw, container){
    	container = container || "body";
        /*Data consume by d3 js.*/
    	var data =  dataToDraw;
        
        /*Initialize the vizualization parameters.*/
    	var margin = {top: 20, right: 20, bottom: 30, left: 40},
        width = 960 - margin.left - margin.right,
        height = 2000 - margin.top - margin.bottom;
    	labelLength = 100; //  .attr("transform", "translate(" + labelLength + ", 0)")
    	
    	// axis with numbers
	    var x0 = d3.scale.linear()
	        .range([0, width-labelLength-50]);
	   
	    // axis with labels
	    var y0 = d3.scale.ordinal()
	        .rangeRoundBands([0, height], .1);
	    var y1 = d3.scale.ordinal();
	    
	    // range of colors for the legend
	    var color = d3.scale.ordinal()
	       // .range(["#98abc5", "#8a89a6", "#7b6888", "#6b486b", "#a05d56", "#d0743c", "#ff8c00"]);
	   .range(["#C44C51", "#FFB6B8", "#FFEFB6", "#A2B5BF", "#5F8CA3", "#FF5B2B", "#B1221C", "#34393E", "#8CC6D7", "#FFDA8C"]);
	   
	    var xAxis = d3.svg.axis()
	        .scale(x0)
	        .orient("top")
	        .tickFormat(d3.format(".2s"));
	
	    var yAxis = d3.svg.axis()
	        .scale(y0)
	        .orient("left");
    	
	    /*Draw the graph container.*/
	    var svg = d3.select(container).append("svg")
	        		.attr("width", width + margin.left + margin.right + labelLength)
	        		.attr("height", height + margin.top + margin.bottom)
	      			.append("g")
	        		.attr("transform", "translate(" + margin.left + "," + margin.top + ")");
	    
	    /*Add data to the graph.*/
	    // pick all time_session labels
	    var time_session = d3.keys(data[0]).filter(function(key) { return key !== "hashtag" && key !== "_id"; });
	
	
	      data.forEach(function(d) {
	        d.info = time_session.map(function(name) { return {time: name, frequency: +d[name]}; });
	       // console.log(d.info); 
	      });
	
	      // horizontal scaling based on maximal value
	      x0.domain([0, d3.max(data, function(d) { return d3.max(d.info, function(d) { return d.frequency; }); })]);
	   // vertical scaling for the groups of bars
	      y0.domain(data.map(function(d) { return d.hashtag; }));
	      // vertical scaling for 1 group of bars
	      y1.domain(time_session).rangeRoundBands([0, y0.rangeBand()]);
	      
	
	      // draw the y-axis and his legend
	      svg.append("g")
          .attr("class", "y axis")
          .attr("transform", "translate(" + labelLength + ", 0)")
          .call(yAxis)
	      .selectAll("text")  
          .style("text-anchor", "end")
          .attr('transform', function(d, i) {
             // return "translate(-15," + y0(d.hashtag) + ") rotate (90)" ;
              //return "rotate (-45)" ;
            });
	      	      
	   // draw the x-axis and his legend
	      svg.append("g")
          .attr("class", "x axis")
          .attr("transform", "translate(" + labelLength + ", 0)")
          .call(xAxis)
        .append("text")
          .attr("x", 6)
          .attr("dx", ".71em")
          .style("text-anchor", "end")
           .attr("transform", "translate(" + ( width - labelLength + 20) + ", 0)")
          .text("Frequency");
	      
	      
	      var state = svg.selectAll(".state")
          .data(data)
        .enter().append("g")
          .attr("class", "g")
          .attr("transform", function(d) { return "translate(" + labelLength + "," + y0(d.hashtag) + ")"; });
	    
	      // settings for 1 bar belonging to a group of bars named "state"
	   var rect =  state.selectAll("rect")
          .data(function(d) { return d.info; })
        .enter().append("rect")
          .attr("height", y1.rangeBand()) // height of 1 bar
          .attr("y", function(d) { return y1(d.time); }) // position of the bar on the vertical axis 
          .attr("x", 0) // no blank between bar and vertical axis
          .attr("width", function(d) { return x0(d.frequency); }) // length of the bar
          .style("fill", function(d) { return color(d.time); });
	  
	      
	      // display frequency next to each bar
	  var endBarLabel = state.selectAll("endBarLabel")
	  .data(function(d) { return d.info; })
	     .enter()
	     .append("text")
  .attr("x", function(d) { return x0(d.frequency); })
  .attr("y", function(d) { return y1(d.time) + y1.rangeBand()/2; })
  .attr("dx", 3) // padding-left
  .attr("dy", ".35em") // vertical-align: middle
  .attr("text-anchor", "start") // text-align: right
  .attr("fill", "black")
  .attr("stroke", "none")
  .text(function(d) { return d.frequency; });
	  
	     
	      // global settings for legend
	      var legend = svg.selectAll(".legend")
          .data(time_session.slice().reverse())
        .enter().append("g")
          .attr("class", "legend")
          .attr("transform", function(d, i) { return "translate(40," + (i * 20 + 100) + ")"; });
	      
	      
	      // settings for legend rect
	      legend.append("rect")
          .attr("x", width - 18)
          .attr("width", 18)
          .attr("height", 18)
          .style("fill", color);
	      
	      // settings for legend labels
	      legend.append("text")
          .attr("x", width - 24)
          .attr("y", 9)
          .attr("dy", ".35em")
          .style("text-anchor", "end")
          .text(function(d) { return d; });
	      
    }