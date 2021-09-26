function sendPlaces() {
    let place = $('input:radio[name=place]:checked').val();
    let row = parseInt(place.split(",")[0]);
    let cell = parseInt(place.split(",")[1]);
    let session = parseInt($('#hiddenSession').attr("value"));
    let arr = {row: row, cell: cell, session: session};
    $.ajax({
        cache: false,
        type: 'POST',
        url: '/cinema/index',
        data: JSON.stringify(arr),
        dataType: 'json',
        contentType: 'application/json'
    }).done(function(data) {
            window.location.href='http://localhost:8080/cinema/payment.html';
    }).fail(function(err){
        alert(err);
    });
}
