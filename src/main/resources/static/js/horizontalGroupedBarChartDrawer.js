/*Function to draw a bar chart graph.*/
    function drawGraph(dataToDraw, container){
    	container = container || "body";
        /*Data consume by d3 js.*/
    	var data =  dataToDraw;
        
        /*Initialize the vizualization parameters.*/
    	var margin = {top: 20, right: 20, bottom: 30, left: 40},
        width = 960 - margin.left - margin.right,
        height = 500 - margin.top - margin.bottom;

	    var x0 = d3.scale.ordinal()
	        .rangeRoundBands([0, width], .1);
	
	    var x1 = d3.scale.ordinal();
	
	    var y = d3.scale.linear()
	        .range([height, 0]);
	
	    var color = d3.scale.ordinal()
	        .range(["#C44C51", "#FFB6B8", "#FFEFB6", "#A2B5BF", "#5F8CA3", "#FF5B2B", "#B1221C", "#34393E", "#8CC6D7", "#FFDA8C"]);
	
	    var xAxis = d3.svg.axis()
	        .scale(x0)
	        .orient("bottom");
	
	    var yAxis = d3.svg.axis()
	        .scale(y)
	        .orient("left")
	        .tickFormat(d3.format(".2s"));
	    
		/*Draw the graph container.*/
	    var svg = d3.select(container).append("svg")
	        		.attr("width", width + margin.left + margin.right)
	        		.attr("height", height + margin.top + margin.bottom)
	      			.append("g")
	        		.attr("transform", "translate(" + margin.left + "," + margin.top + ")");
	    
		/*Add data to the graph.*/
	    var time_session = d3.keys(data[0]).filter(function(key) { return key !== "hashtag" && key !== "_id"; });
	
	
	      data.forEach(function(d) {
	        d.info = time_session.map(function(name) { return {time: name, frequency: +d[name]}; });
	      });
	
	      x0.domain(data.map(function(d) { return d.hashtag; }));
	      x1.domain(time_session).rangeRoundBands([0, x0.rangeBand()]);
	      y.domain([0, d3.max(data, function(d) { return d3.max(d.info, function(d) { return d.frequency; }); })]);
	
	      svg.append("g")
	          .attr("class", "x axis")
	          .attr("transform", "translate(0," + height + ")")
	          .call(xAxis);
	
	      svg.append("g")
	          .attr("class", "y axis")
	          .call(yAxis)
	        .append("text")
	          .attr("transform", "rotate(-90)")
	          .attr("y", 6)
	          .attr("dy", ".71em")
	          .style("text-anchor", "end")
	          .text(x1.rangeBand());//"Frequency");
	
	      var state = svg.selectAll(".state")
	          .data(data)
	        .enter().append("g")
	          .attr("class", "g")
	          .attr("transform", function(d) { return "translate(" + x0(d.hashtag) + ",0)"; });
	
	      state.selectAll("rect")
	          .data(function(d) { return d.info; })
	        .enter().append("rect")
	          .attr("width", x1.rangeBand()) // width of 1 bar
	          .attr("x", function(d) { return x1(d.time); })
	          .attr("y", function(d) { return y(d.frequency); })
	          .attr("height", function(d) { return height - y(d.frequency); })
	          .style("fill", function(d) { return color(d.time); });
	
	      var legend = svg.selectAll(".legend")
	          .data(time_session.slice().reverse())
	        .enter().append("g")
	          .attr("class", "legend")
	          .attr("transform", function(d, i) { return "translate(0," + i * 20 + ")"; });
	
	      legend.append("rect")
	          .attr("x", width - 18)
	          .attr("width", 18)
	          .attr("height", 18)
	          .style("fill", color);
	
	      legend.append("text")
	          .attr("x", width - 24)
	          .attr("y", 9)
	          .attr("dy", ".35em")
	          .style("text-anchor", "end")
	          .text(function(d) { return d; });
	    	
	    }