const CLIENT_ID = "1007447573383-kl38dafa0mmeo02ppebb9qag1cqujqa5.apps.googleusercontent.com";

function start() {
    gapi.load('auth2', function() {
        auth2 = gapi.auth2.init({
            client_id: CLIENT_ID,
            <!-- nodehill.com blog auto-converts non https-strings to https, thus the concatenation. -->
            scope: "https://www.googleapis.com/auth/calendar.events"
        });
    });
}

$('#signinButton').click(function() {
// signInCallback defined in step 6.
    console.log("Lina")
    auth2.grantOfflineAccess().then(signInCallback); //Här hämtar jag refreshtoken och kanske andra saker också
});

function signInCallback(authResult) {
    console.log('authResult', authResult);

    if (authResult['code']) {

        // Hide the sign-in button now that the user is authorized, for example:
        $('#signinButton').attr('style', 'display: none');

        // Send the code to the server
        $.ajax({
            type: 'POST',
            url: 'http://localhost:8080/storeauthcode',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            },
            contentType: 'application/octet-stream; charset=utf-8',
            success: function(result) {
                // Handle or verify the server response.
            },
            processData: false,
            data: authResult['code']
        });
    } else {
        // There was an error.
    }
}

$('#findDates').click(function(){
    console.log("Försöker hitta datum");
    $.ajax({
        type: 'GET',
        url: 'http://localhost:8080/lookForFreeTime',
        headers: {
            'X-Requested-With': 'XMLHttpRequest'
        },
        contentType: 'application/octet-stream; charset=utf-8',
        success: function(freeDates) {
            console.log(freeDates)
            freeDates.forEach(fd=> $('#showDates').append("<li>"+fd+"<button class ='chooseDate'id = '"+fd+"'>Choose this date</button></li>"))
            // Handle or verify the server response.
        },
        processData: false,
    });
})

$(document).on('click', '.chooseDate', function () {
    var id = $(this).attr('id')
    console.log(id)
})

$('#searchMovieButton').click(function(){
    console.log("clicked movie button söndag")
    var search = $('#movieSearch').val();
    
    $.ajax({
        type: "GET",
        url: "http://localhost:8080/search/byTitle",
        headers: {
            'X-Requested-With': 'XMLHttpRequest'
        },
        contentType: 'application/octet-stream; charset=utf-8',
        success: function(result) {
            console.log(result)
            document.getElementById("showMovieList").innerHTML = "";
            console.log('inne där jag tror')
            result.forEach(c => $('#showMovieList').append("<li>"+c.Title+"<button class='showTest' id = '"+c.imdbID+"'>"+c.imdbID+"</button></li>"))
        },
        processData: false,
        data: "title="+search
    })
});


$(document).on('click','.showTest',function(){

    console.log("clicked on single movie söndag")
    var id = $(this).attr('id')
    $.ajax({
        type: "GET",
        url: "http://localhost:8080/search/add",
        headers: {
            'X-Requested-With': 'XMLHttpRequest'
        },
        contentType: 'application/octet-stream; charset=utf-8',
        success: function(result) {
            console.log(result)
            document.getElementById("showOneMovie").innerHTML = "";

            $('#showOneMovie').append("här ska en film visas söndag");
            $('#showOneMovie').append("<ul></ul>");
            $('#showOneMovie').append("<li>"+result.Title+"<button class='selectMovie' id = '"+result.Title+"'>choose this movie</button></li>");
            $('#showOneMovie').append("<li>"+result.Year+"</li>");
            $('#showOneMovie').append("<li>"+result.Plot+"</li>");
            $('#showOneMovie').append("<li>"+result.Genre+"</li>");
            $('#showOneMovie').append("<li>"+result.Runtime+"</li>");
            $('#showOneMovie').append("<li>"+result.imdbRating+"</li>");
            $('#showOneMovie').append("<li>"+result.Language+"</li>");
            $('#showOneMovie').append("<li><img src='http://img.omdbapi.com/?i="+result.imdbID+"&apikey=6540b93c'></li>");

        },
        processData: false,
        data: "id="+id
    })
});

$(document).on('click','.selectMovie',function(){
    var title = $(this).attr('id')

    $.ajax({
        type: "GET",
        url: "http://localhost:8080/setMovieTitleToEvent",
        headers: {
            'X-Requested-With': 'XMLHttpRequest'
        },
        contentType: 'application/octet-stream; charset=utf-8',
        success: function(result) {
            console.log(result)
            document.getElementById("showOneMovie").innerHTML = "";
            document.getElementById("eventPreview").innerHTML = "";
            $('#eventPreview').append("<li>You have choosen the movie: "+title+"</li>")
            if(result.start==undefined){
                $('#eventPreview').append("<li>Now you have to choose a time for the event!</li>")
            }
            else $('#eventPreview').append("<li>and the time: "+result.start+"<button id = 'bookEvent'>Book!</button></li>")

        },
        processData: false,
        data: "movieTitle="+title
    })
    //ibackend ska det finnas ett event med atart och slut datetime
    // och en movie title
    // när man trycker på den här knappen ska man setMovieTitle,
    // get movie date om det finns,
    // detta ska skrivas ut, om datum inte finns ska det stå välj tid

})

$(document).on('click','.chooseDate',function() {
    var choosenDate = $(this).attr('id')

    $.ajax({
        type: "GET",
        url: "http://localhost:8080/setDateTimeToEvent",
        headers: {
            'X-Requested-With': 'XMLHttpRequest'
        },
        contentType: 'application/octet-stream; charset=utf-8',
        success: function (result) {
            console.log(result)
            document.getElementById("showOneMovie").innerHTML = "";
            document.getElementById("eventPreview").innerHTML = "";
            if (result) {
                $('#eventPreview').append("<li>You have choosen the movie: "+ result+"</li>")
                $('#eventPreview').append("<li>And the time: "+ choosenDate+"<button id = 'book'>Book!</button></li>")
            }
            else $('#eventPreview').append("<li>Somthing went wrong, choose a new date!!</li>")

        },
        processData: false,
        data: "eventStart=" +choosenDate
    })
})


$(document).on('click','#book',function() {
    var choosenDate = $(this).attr('id')
    $.ajax({
        type: "GET",
        url: "http://localhost:8080/bookEvent",
        headers: {
            'X-Requested-With': 'XMLHttpRequest'
        },
        contentType: 'application/octet-stream; charset=utf-8',
        success: function (result) {
            console.log(result)
            document.getElementById("showOneMovie").innerHTML = "";
            document.getElementById("eventPreview").innerHTML = "";
            if (result) {
                $('#eventPreview').append("<li>You have booked an event!</li>")

            }
            else $('#eventPreview').append("<li>Somthing went wrong!!</li>")

        },
        processData: false
    })
})
