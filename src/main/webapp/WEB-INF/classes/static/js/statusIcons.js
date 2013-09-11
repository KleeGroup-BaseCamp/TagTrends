function requestWaitingIcon(){
    	$('div#requestStatusIcon').empty().append(
    			'<button type="button" class="btn btn-default"> \
					<span class="glyphicon glyphicon-refresh"></span> \
				</button> '
    			);
    	enablePopover('div#requestStatusIcon', "Request : waiting");
    }
    
    function requestSuccessIcon(){
    	$('div#requestStatusIcon').empty().append(
    			'<button type="button" class="btn btn-default"> \
    				<span class="glyphicon glyphicon-ok"></span> \
    			</button>'
    			);
    	enablePopover('div#requestStatusIcon', "Request : success");
    }
    
    function requestErrorIcon(){
    	$('div#requestStatusIcon').empty().append(
    			'<button type="button" class="btn btn-default"> \
					<span class="glyphicon glyphicon-remove-circle"></span> \
				</button> '
    			);
    	enablePopover('div#requestStatusIcon', "Request : error");
    }
    
    function serverWaitingIcon(){
    	$('div#serverStatusIcon').empty().append(
    			'<button type="button" class="btn btn-default"> \
					<span class="glyphicon glyphicon-resize-full"></span> \
				</button> '
    			);
    	enablePopover('div#serverStatusIcon', "Server : waiting");
    }
    
    function serverSuccessIcon(){
    	$('div#serverStatusIcon').empty().append(
    			'<button type="button" class="btn btn-default"> \
    				<span class="glyphicon glyphicon-signal"></span> \
    			</button>'
    			);
    	enablePopover('div#serverStatusIcon', "Server : success");
    }
    
    function serverErrorIcon(){
    	$('div#serverStatusIcon').empty().append(
    			'<button type="button" class="btn btn-default"> \
					<span class="glyphicon glyphicon-warning-sign"></span> \
				</button> '
    			);
    	enablePopover('div#serverStatusIcon', "Server : error");
    }
    
    function enablePopover(iconContainer, contentText){
    	$(iconContainer).popover({
			placement:'bottom',
			html: true,
			content: contentText,
			delay: { show: 0, hide: 1000 }
		});
    	/* on 'click': appear and disappear */
    	$(iconContainer).on('click', function(){
    		$(iconContainer).popover('toggle')
    		.popover('toggle');});
    }