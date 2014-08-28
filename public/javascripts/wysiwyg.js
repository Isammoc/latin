(function($){
	window.wysiwyg = function(tmpId) {
		var types = {
				"abl": "ablatif",
				"acc": "accusatif",
				"dat": "datif",
				"gen": "genitif",
				"gén": "genitif",
				"indic": "verbe",
				"inf": "infinitif",
				"inv": "invariant",
				"nom": "nominatif",
				"subj": "verbe"
				};

		rangy.init();
		var $display = $('#' + tmpId);
		$display.on('keyup', function(e) {
			console.log(e);
			e.preventDefault();
			if(e.which == 67 && e.altKey) {
				$display.trigger('mouseup');
			}
		});
		$display.on('mouseup', function(){
			var sel = rangy.getSelection()
			console.log(sel);
			if(sel.rangeCount) {
				var range = sel.getRangeAt(0);
				var $container = $(range.commonAncestorContainer);
				if( range.startOffset != range.endOffset && ($container.filter('.editable').length || $container.parents('.editable').length)) {
					if($container.filter("span").length) {
						var $r = $(range.commonAncestorContainer);
						$container.replaceWith($container.html());
					} else if ($container.parent().filter("span").html() == range.toHtml()) {
						$container.parent().replaceWith($container);
					} else {
						var dataType = prompt("Type ? ", "inv");
						if (dataType) {
							var el = document.createElement("span");
							var subType = dataType.split(" ")[0];
							var subClass = types[subType] ? types[subType] : "inconnu";

							el.classList.add(subClass);
							el.setAttribute("data-type", dataType);
							el.setAttribute("title", dataType);
							try {
								range.surroundContents(el);
								range.collapseAfter();
							} catch(ex) {
								console.log(ex);
							}
						}
					}
				}
			}
		});
		$.each($display.children("span"), function(i, s) {
			if($(s).data("type")) {
				var subType = $(s).data("type").split(" ")[0];
				var subClass = types[subType] ? types[subType] : "inconnu";
				$(s)
				  .attr("title", $(s).data("type"))
				  .addClass(subClass);
			}
		});
	}
})(jQuery);