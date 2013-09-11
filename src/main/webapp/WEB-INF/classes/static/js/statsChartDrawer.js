function renderStatsChart(data, container) {
	container = container || "body";

	var valueLabelWidth = 80; // space reserved for value labels (right)
	var barHeight = 20; // height of one bar
	var barLabelWidth = 0; // space reserved for bar labels
	var barLabelPadding = 5; // padding between bar and bar labels (left)
	var gridLabelHeight = 18; // space reserved for gridline labels
	var gridChartOffset = 3; // space between start of grid and first bar
	var maxBarWidth = 150; // width of the bar with the max value

	// accessor functions
	var barLabel = function(d) {
		return d['opinion'];
	};
	var barValue = function(d) {
		return parseFloat(d['rate']);
	};

	function color(opinion) {
		if (opinion == "for") {
			return "#00CC00"; // green
		} else if (opinion == "against") {
			return "#FF6600"; // red
		} else {
			return black;
		}
	}

	// scales
	var yScale = d3.scale.ordinal().domain(d3.range(0, data.length))
			.rangeBands([ 0, data.length * barHeight ]);
	var y = function(d, i) {
		return yScale(i);
	};
	var yText = function(d, i) {
		return y(d, i) + yScale.rangeBand() / 2;
	};
	var x = d3.scale.linear().domain([ 0, d3.max(data, barValue) ]).range(
			[ 0, maxBarWidth ]);

	// svg container element
	var chart = d3.select(container).append("svg").attr('width',
			maxBarWidth + barLabelWidth + valueLabelWidth).attr('height',
			gridLabelHeight + gridChartOffset + data.length * barHeight);

//	// bar labels
//	var labelsContainer = chart.append('g').attr(
//			'transform',
//			'translate(' + (barLabelWidth - barLabelPadding) + ','
//					+ (gridLabelHeight + gridChartOffset) + ')');
//	labelsContainer.selectAll('text').data(data).enter().append('text').attr(
//			'y', yText).attr('stroke', 'none').attr('fill', 'black').attr("dy",
//			".35em") // vertical-align: middle
//	.attr('text-anchor', 'end').text(barLabel);

	// bars
	var barsContainer = chart.append('g').attr(
			'transform',
			'translate(' + barLabelWidth + ','
					+ (gridLabelHeight + gridChartOffset) + ')');
	barsContainer.selectAll("rect").data(data).enter().append("rect").attr('y',
			y).attr('height', yScale.rangeBand()).attr('width', function(d) {
		return x(barValue(d));
	}).attr('stroke', 'white').attr('fill', function(d) {
		return color(barLabel(d));
	});

	// bar value labels
	barsContainer.selectAll("text").data(data).enter().append("text").attr("x",
			function(d) {
				return x(barValue(d));
			}).attr("y", yText).attr("dx", 3) // padding-left
	.attr("dy", ".35em") // vertical-align: middle
	.attr("text-anchor", "start") // text-align: right
	.attr("fill", "black").attr("stroke", "none").text(function(d) {
		return d3.round(barValue(d), 2);
	});

	// start line
	barsContainer.append("line").attr("y1", -gridChartOffset).attr("y2",
			yScale.rangeExtent()[1] + gridChartOffset).style("stroke", "#000");

}