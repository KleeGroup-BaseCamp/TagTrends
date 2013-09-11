/*Function to draw the tag cloud of a hashtag.*/
function drawCloud( dataToDraw, container ){
	 container = container || "body";
	 	 
	var fill = d3.scale.category20();
	max_size = 30;
	min_size = 10;
	
	var sortedData =  _.sortBy(dataToDraw, function(tag){ return tag.size; });
	var data = _.last(sortedData, 10);
	var min = _.min(data, function(data){ return data.size; }).size;
	var max = _.max(data, function(data){ return data.size; }).size;
	for (var i=0;i<data.length;i++){
		data[i].size = Math.round((max_size - min_size)/(max - min)*data[i].size + min_size);
		}
	
	d3.layout.cloud().size([250, 300])
      .words(data)
      .padding(5)
      .rotate(function() { 
    	  return ~~(Math.random() * 2) * 90; 
    	  })
      .font("Impact")
      .fontSize(function(d) { 
    	  return d.size; 
    	  })
      .on("end", draw)
      .start();

  function draw(words) {
    d3.select(container).append("svg")
        .attr("width", 250)
        .attr("height", 300)
      	.append("g")
      	.attr("class","cloudDrawing")
      	.attr("width", 250)
        .attr("height", 300)
        .attr("transform", "translate(110,150)")
      	.selectAll("text")
        .data(words)
      	.enter().append("text")
        .style("font-size", function(d) { return d.size + "px"; })
        .style("font-family", "Impact")
        .style("fill", function(d, i) { return fill(i); })
        .attr("text-anchor", "middle")
        .attr("transform", function(d) {
          return "translate(" + [d.x, d.y] + ")rotate(" + d.rotate + ")";
        })
        .text(function(d) { return d.tag; });
  }
  
}