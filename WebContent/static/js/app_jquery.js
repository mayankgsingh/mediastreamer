var appctx = "/mediastreamer"
function searchTitle() {
	//jQuery.get( "" [, data ] [, success ] [, dataType ] )
	var txtSearch = $("#txtSearch").val();
	
	var url = appctx + "/media/search/" + txtSearch;
	$.get(url, function(data) {
		var tblRes = $("#tblSearchRes > tbody");
		tblRes.empty();
		
		var jsonRes = data.data;
		
		if(data.data.length == 0) {
			var row = "<tr class=\"active\"><td colspan=\"5\">No records found.</td></tr>";
			tblRes.append(row);
		} else {
			$.each(jsonRes, function(key, value) {
			  var row = "<tr class=\"active\">";
			  //row = row + "<td><a href=\"#\" class=\"medialnk\">" + value.docid + "</a></td>";
			  row = row + "<td><a href=\"#\" class=\"medialnk\" id=\"" + value.docid + "\">" + value.title + "</a></td>";
			  row = row + "<td>" + value.album + "</td>";
			  row = row + "<td>" + value.artist + "</td>";
			  row = row + "<td>" + value.fileName + "</td>";
			  row = row + "</td></tr>";
			  
			  tblRes.append(row);
			}); 
		}
		
		$(".medialnk").click(function() {
			var fid = $(this).attr('id');
			var fileurl = appctx + "/media/play/" + fid;
			var audio = document.getElementById('playcontrol');

			  //var source = document.getElementById('audioSource');
			  audio.src = fileurl;
			  audio.load(); //call this to just preload the audio without playing
			  audio.play(); //call this to play the song right away 
			
		});
	});
}

function rebuildIndex() {
	var url = "/filelocator/media/scan";
	$.get(url, function(data) {
		var jsonRes = data.data;
		var txthtml = "<b>Scan Result !!!</b>";
		var indexedFiles = data.indexedFiles;
		var skippedFiles = data.skippedFiles;
		txthtml = txthtml + " <b>Indexed:</b> " + indexedFiles + " <b>Skipped</b>: " + skippedFiles;
		$("#alertcontent").html(txthtml);
		$('.alert').show();		
	});
}