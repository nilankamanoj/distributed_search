var urlList = {}

var addNode = function(i,host,port){
    const infoUrl = "http://"+host+":"+port
    urlList[i] = {url : infoUrl, running : true}
    $('#nodeList').append(`<div id="box` + i + `" class="col-md-4">
        <ul class="list-group">
            <li class="list-group-item active">Node- `+host +`:` + (port) +`</li>
            <li class="list-group-item"><input id="searchText`+i+`"/> <button onClick="search(`+i+`)">Search</button></li>
            <li class="list-group-item"><textarea wrap='off' id="results`+i+`" class="form-control" readonly></textarea></li>
        </ul>
        </div>`)

    $('#results'+i).html("Loading...");

    setInterval(function(){
        if(urlList[i].running){
        $.get(infoUrl+"/info", function( data ) {
            $('#results'+i).html(data);
        }).fail(function() {
            $("#box"+i)[0].children[0].children[0].style.backgroundColor = "red"
            alert(urlList[i].url + " is down!");
            urlList[i].running = false
        })
    }
    },2000)
}

var nodeCount = parseInt(window.location.href.split("?n=")[1])
console.log(nodeCount)
for (let i = 0; i < nodeCount; i++) {
      var port =  (10001+i)
      addNode(i,'localhost',port);
}


var search = function(index){
    $.post(urlList[index].url+"/search/"+$('#searchText'+index).val(), function(data){
        console.log("Response from Search:",data)
    })
    $('#searchText'+index).val("")
}
